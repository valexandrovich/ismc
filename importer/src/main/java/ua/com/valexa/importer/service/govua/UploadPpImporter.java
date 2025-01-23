package ua.com.valexa.importer.service.govua;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ua.com.valexa.afscommon.dto.cpms.StepRequestDto;
import ua.com.valexa.afscommon.dto.cpms.StepUpdateDto;
import ua.com.valexa.afscommon.enums.cpms.TaskStatus;
import ua.com.valexa.importer.service.Importable;

import java.util.Date;
import java.util.Map;

@Service("uploader_pp")
public class UploadPpImporter implements Importable {

    private static final Logger log = LoggerFactory.getLogger(UploadPpImporter.class);

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    private String getQueueCpmsStepUpdate;

    @Autowired
    @Qualifier("upload_pp_job")
    Job job;

    @Override
    public StepUpdateDto handleStepRequest(StepRequestDto stepRequestDto) {

        StepUpdateDto stepUpdateDto = new StepUpdateDto();
        stepUpdateDto.setStepId(stepRequestDto.getStepId());


        try {

            stepUpdateDto.setStatus(TaskStatus.IN_PROGRESS);
            sendStepUpdate(stepUpdateDto);


            JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
            jobParametersBuilder.addString("startedAt", new Date().toString());
            jobParametersBuilder.addString("stepId", String.valueOf(stepUpdateDto.getStepId()));
            jobParametersBuilder.addString("jobId", String.valueOf("1"));

            for (Map.Entry<String, String> entry : stepRequestDto.getParameters().entrySet()) {
                jobParametersBuilder.addString(entry.getKey(), entry.getValue());
            }
            JobParameters jp = jobParametersBuilder.toJobParameters();
            JobExecution jobExecution = jobLauncher.run(job, jp);

            if (jobExecution.getStatus().equals(BatchStatus.FAILED)) {
                stepUpdateDto.setStatus(TaskStatus.FAILED);
                sendStepUpdate(stepUpdateDto);
                return stepUpdateDto;
            }
        } catch (Exception e) {
            log.error("Importer failed : " + e.getMessage());
            stepUpdateDto.setStatus(TaskStatus.FAILED);
            stepUpdateDto.setComment(e.getMessage());
            sendStepUpdate(stepUpdateDto);
        }
        stepUpdateDto.setStatus(TaskStatus.FINISHED);
        return stepUpdateDto;


    }

    private void sendStepUpdate(StepUpdateDto stepUpdateDto) {
        log.debug("Step ID: {}; Sending StepUpdate: {}", stepUpdateDto.getStepId(), stepUpdateDto);
        rabbitTemplate.convertAndSend(getQueueCpmsStepUpdate, stepUpdateDto);
    }

}
