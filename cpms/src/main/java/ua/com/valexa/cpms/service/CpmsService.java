package ua.com.valexa.cpms.service;

import brave.Span;
import brave.Tracing;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.valexa.afscommon.dto.cpms.StepRequestDto;
import ua.com.valexa.afscommon.dto.cpms.StepUpdateDto;
import ua.com.valexa.afscommon.enums.cpms.TaskStatus;
import ua.com.valexa.afscommon.scheduler.StoredJobRequestDto;
import ua.com.valexa.afscommon.scheduler.StoredStepRequestDto;
import ua.com.valexa.cpms.model.Job;
import ua.com.valexa.cpms.model.Step;
import ua.com.valexa.cpms.repository.JobRepository;
import ua.com.valexa.cpms.repository.StepRepository;

import java.time.LocalDateTime;

@Service
public class CpmsService {

    private static final Logger log = LoggerFactory.getLogger(CpmsService.class);


    @Autowired
    Tracing tracing;


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    String getQueueDownloaderStepRequest;

    @Autowired
    String getQueueImporterStepRequest;

    final JobRepository jobRepository;
    final StepRepository stepRepository;
    final RabbitTemplate rabbitTemplate;

    public CpmsService(JobRepository jobRepository, StepRepository stepRepository, RabbitTemplate rabbitTemplate) {
        this.jobRepository = jobRepository;
        this.stepRepository = stepRepository;
        this.rabbitTemplate = rabbitTemplate;
    }


    public void handleStoredJobRequest(StoredJobRequestDto storedJobRequestDto) {
            Job job = buildJobFromStoredJobRequest(storedJobRequestDto);
        handleNextStep(job);
    }



    private Job buildJobFromStoredJobRequest(StoredJobRequestDto storedJobRequestDto) {
        Job job = new Job();
        job.setShortName(storedJobRequestDto.getShortName());
        job.setName(storedJobRequestDto.getName());
        job.setSource(storedJobRequestDto.getSource());
        job.setInitiatorName(storedJobRequestDto.getInitiatorName());
        job.setStartedAt(LocalDateTime.now());
        job = jobRepository.save(job);
        for (StoredStepRequestDto storedStepRequestDto : storedJobRequestDto.getSteps()) {
            Step s = new Step();
            s.setStatus(TaskStatus.NEW);
            s.setStepOrder(storedStepRequestDto.getStepOrder());
            s.setService(storedStepRequestDto.getService());
            s.setWorker(storedStepRequestDto.getWorker());
            s.setIsSkippable(storedStepRequestDto.getIsSkipable());
            s.setParameters(storedStepRequestDto.getParameters());
            s.getParameters().put("jobId", job.getId().toString());
            s.setJob(job);
            s = stepRepository.save(s);
            job.getSteps().add(s);
        }
        return job;
    }


    private void handleNextStep(Job job) {
        Step nextStep = job.getSteps().stream().filter(step -> step.getStatus().equals(TaskStatus.NEW)).findFirst().orElse(null);
        if (nextStep == null) {
            commitJob(job );
        } else {
            nextStep.setStartedAt(LocalDateTime.now());
            nextStep = stepRepository.save(nextStep);
            StepRequestDto stepRequestDto = new StepRequestDto();
            stepRequestDto.setStepId(nextStep.getId());
            stepRequestDto.setWorker(nextStep.getWorker());
            stepRequestDto.setParameters(nextStep.getParameters());
            stepRequestDto.getParameters().putAll(job.getResults());
            sendStepRequest(nextStep.getService(), stepRequestDto);
        }
    }

    public void handleStepUpdate(StepUpdateDto stepUpdateDto) {
        Step step = stepRepository.findById(stepUpdateDto.getStepId()).orElseThrow(() -> new RuntimeException("Cant find Step with id: " + stepUpdateDto.getStepId()));

        if (
                step.getStatus().equals(TaskStatus.FINISHED) ||
                        step.getStatus().equals(TaskStatus.FAILED) ||
                        step.getStatus().equals(TaskStatus.SKIPPED)

        ) {
            log.error("Trying to change finished STEP!");
            log.error(step.toString());
            log.error(stepUpdateDto.toString());
            return;
        }

        if (stepUpdateDto.getStatus() != null) {
            step.setStatus(stepUpdateDto.getStatus());
        }

        if (stepUpdateDto.getProgress() != null) {
            step.setProgress(stepUpdateDto.getProgress());
        }

        if (stepUpdateDto.getComment() != null) {
            step.setComment(stepUpdateDto.getComment());
        }

        step.getJob().getResults().putAll(stepUpdateDto.getResults());
        jobRepository.save(step.getJob());

        if (stepUpdateDto.getStatus().equals(TaskStatus.FINISHED)) {
            step.setFinishedAt(LocalDateTime.now());
        } else if (stepUpdateDto.getStatus().equals(TaskStatus.FAILED)) {
            if (step.getIsSkippable()) {
                step.setFinishedAt(LocalDateTime.now());
                step.setStatus(TaskStatus.SKIPPED);
            }
        }
        step = stepRepository.save(step);

        if (
                step.getStatus().equals(TaskStatus.FINISHED) ||
                        step.getStatus().equals(TaskStatus.SKIPPED)
        ) {
            log.info("Step Id: {}; Handling next step", step.getId());
            handleNextStep(step.getJob());
        }

        if (step.getStatus().equals(TaskStatus.FAILED)) {
            commitJob(step.getJob());
        }
    }

    private void commitJob(Job job) {
        log.info("Commiting job: {}", job);
        job.setFinishedAt(LocalDateTime.now());
        jobRepository.save(job);
    }

    private void sendStepRequest(String service, StepRequestDto stepRequestDto) {
        try {
            Span span = tracing.tracer().nextSpan().name("cpms_sending_task_request").start();
            MessageProperties messageProperties = new MessageProperties();
            tracing.propagation().injector(MessageProperties::setHeader)
                    .inject(span.context(), messageProperties);
            byte[] messageBody = objectMapper.writeValueAsBytes(stepRequestDto);
            Message message = new Message(messageBody, messageProperties);

            switch (service) {
                case "downloader":
                    rabbitTemplate.convertAndSend(getQueueDownloaderStepRequest, message);
                    break;
                case "importer":
                    rabbitTemplate.convertAndSend(getQueueImporterStepRequest, message);
                    break;
                default:
                    log.error("No such queue for service {}", service);
                    break;
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }


}
