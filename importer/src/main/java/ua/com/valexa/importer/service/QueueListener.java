package ua.com.valexa.importer.service;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.Propagation;
import brave.propagation.TraceContextOrSamplingFlags;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ua.com.valexa.afscommon.dto.cpms.StepRequestDto;
import ua.com.valexa.afscommon.dto.cpms.StepUpdateDto;
import ua.com.valexa.afscommon.enums.cpms.TaskStatus;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class QueueListener {

    private static final Logger log = LoggerFactory.getLogger(QueueListener.class);

    ExecutorService executorService = Executors.newFixedThreadPool(3);

//    Importable importable;
    final ApplicationContext applicationContext;
    final String getQueueCpmsStepUpdate;
    final Tracing tracing;
    final ObjectMapper objectMapper;
    final RabbitTemplate rabbitTemplate;
    final String getQueueImporterStepRequest;

    public QueueListener(ApplicationContext applicationContext, String getQueueCpmsStepUpdate, Tracing tracing, ObjectMapper objectMapper, RabbitTemplate rabbitTemplate, String getQueueImporterStepRequest) {
//        this.importable = importable;
        this.applicationContext = applicationContext;
        this.getQueueCpmsStepUpdate = getQueueCpmsStepUpdate;
        this.tracing = tracing;
        this.objectMapper = objectMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.getQueueImporterStepRequest = getQueueImporterStepRequest;
    }

    @RabbitListener(queues = "#{getQueueImporterStepRequest}", errorHandler = "queueListenerErrorHandler")
    public void recieveStepRequestMessage(Message message) {
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        TraceContextOrSamplingFlags contextOrFlags = tracing.propagation()
                .extractor((Propagation.Getter<Map<String, Object>, String>) (request, key) -> {
                    Object value = request.get(key);
                    return value != null ? value.toString() : null;
                })
                .extract(headers);
        Span span = tracing.tracer().nextSpan(contextOrFlags).name("downloader").start();
        try (Tracer.SpanInScope ws = tracing.tracer().withSpanInScope(span)) {
            StepRequestDto stepRequestDto = objectMapper.readValue(message.getBody(), StepRequestDto.class);
            log.info("Step ID: {}; Recieved StepRequest : {}", stepRequestDto.getStepId(),  stepRequestDto.toString());
//            importable = applicationContext.getBean(stepRequestDto.getWorker(), Importable.class);
            Importable importable = applicationContext.getBean(stepRequestDto.getWorker(), Importable.class);
            CompletableFuture<StepUpdateDto> cfuture = CompletableFuture.supplyAsync(() -> importable.handleStepRequest(stepRequestDto), executorService);
            cfuture.thenAcceptAsync(taskResponseDto -> sendUpdate(taskResponseDto, span));
        } catch (Exception e) {
            span.error(e);
            log.error(e.getMessage());
            StepUpdateDto stepUpdateDto = new StepUpdateDto();
            stepUpdateDto.setStepId(stepUpdateDto.getStepId());
            stepUpdateDto.setStatus(TaskStatus.FAILED);
            stepUpdateDto.setComment(e.getMessage());
            sendUpdate(stepUpdateDto, span);
        }
    }

    private void sendUpdate(StepUpdateDto stepUpdateDto, Span span) {
        try {
            log.info("Step ID: {}; Sending StepUpdate : {}", stepUpdateDto.getStepId(), stepUpdateDto.toString());
            MessageProperties messageProperties = new MessageProperties();
            tracing.propagation().injector(MessageProperties::setHeader)
                    .inject(span.context(), messageProperties);
            byte[] messageBody = objectMapper.writeValueAsBytes(stepUpdateDto);
            Message message = new Message(messageBody, messageProperties);
            rabbitTemplate.convertAndSend(getQueueCpmsStepUpdate, message);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            span.finish();
        }
    }

}
