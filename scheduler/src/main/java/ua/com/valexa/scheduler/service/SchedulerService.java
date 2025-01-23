package ua.com.valexa.scheduler.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.com.valexa.afscommon.scheduler.StoredJobRequestDto;
import ua.com.valexa.afscommon.scheduler.StoredStepRequestDto;
import ua.com.valexa.scheduler.model.StoredJob;
import ua.com.valexa.scheduler.model.StoredStep;
import ua.com.valexa.scheduler.repository.StoredJobRepository;
import brave.Span;
import brave.Tracing;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.springframework.amqp.core.MessageProperties;
@Service
public class SchedulerService {

    private static final Logger log = LoggerFactory.getLogger(SchedulerService.class);

    @Value("${queue.cpms.job-request}")
    private String queueCpmsJobRequest;

    final RabbitTemplate rabbitTemplate;
    final StoredJobRepository storedJobRepository;
    final Tracing tracing;
    final ObjectMapper objectMapper;

    public SchedulerService(RabbitTemplate rabbitTemplate, StoredJobRepository storedJobRepository, Tracing tracing, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.storedJobRepository = storedJobRepository;
        this.tracing = tracing;
        this.objectMapper = objectMapper;
    }

    public void initStoredJob(Long storedJobId, String initiatorName) {
        log.info("Starting Stored Job : {}", storedJobId);


        try {
            StoredJob storedJob = storedJobRepository.findById(storedJobId).orElseThrow(() -> new RuntimeException("Can't find Stored Job : " + storedJobId));
            StoredJobRequestDto storedJobRequestDto = buildStoredJobRequestDto(storedJob, initiatorName);
            Span span = tracing.tracer().nextSpan().name("scheduler_init_stored_job").start();
            MessageProperties messageProperties = new MessageProperties();
            tracing.propagation().injector(MessageProperties::setHeader)
                    .inject(span.context(), messageProperties);
            byte[] messageBody = objectMapper.writeValueAsBytes(storedJobRequestDto);
            Message message = new Message(messageBody, messageProperties);
            rabbitTemplate.convertAndSend(queueCpmsJobRequest, message);
        } catch (Exception ex){
            throw new RuntimeException(ex.getMessage());
        }

//        StoredJob storedJob = storedJobRepository.findById(storedJobId).orElseThrow(() -> new RuntimeException("Can't find Stored Job : " + storedJobId));
//        StoredJobRequestDto storedJobRequestDto = buildStoredJobRequestDto(storedJob);
//        rabbitTemplate.convertAndSend("afs.cpms.job.request", storedJobRequestDto);
    }

    private StoredJobRequestDto buildStoredJobRequestDto(StoredJob storedJob, String initiatorName) {
        StoredJobRequestDto storedJobRequestDto = new StoredJobRequestDto();
        storedJobRequestDto.setName(storedJob.getName());
        storedJobRequestDto.setShortName(storedJob.getShortName());
        storedJobRequestDto.setSource(storedJob.getSource());
        //TODO make injection of web username
        storedJobRequestDto.setInitiatorName(initiatorName);
        storedJobRequestDto.setSteps(
                storedJob.getSteps().stream()
                        .filter(StoredStep::getIsEnabled)
                        .sorted(Comparator.comparing(StoredStep::getStepOrder))
                        .map(this::convertToStepRequest)
                        .collect(Collectors.toList()));
        return storedJobRequestDto;
    }

    private StoredStepRequestDto convertToStepRequest(StoredStep storedStep) {
        StoredStepRequestDto storedStepRequestDto = new StoredStepRequestDto();
        storedStepRequestDto.setStepOrder(storedStep.getStepOrder());
        storedStepRequestDto.setService(storedStep.getService());
        storedStepRequestDto.setWorker(storedStep.getWorker());
        storedStepRequestDto.setIsSkipable(storedStep.getIsSkippable());
        storedStepRequestDto.setParameters(storedStep.getParameters());
        return storedStepRequestDto;
    }

}
