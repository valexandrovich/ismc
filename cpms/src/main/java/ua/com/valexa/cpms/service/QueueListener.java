package ua.com.valexa.cpms.service;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.Propagation;
import brave.propagation.TraceContextOrSamplingFlags;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.valexa.afscommon.dto.cpms.StepUpdateDto;
import ua.com.valexa.afscommon.scheduler.StoredJobRequestDto;

import java.util.Map;

@Service
public class QueueListener {

    private static final Logger log = LoggerFactory.getLogger(QueueListener.class);

    final CpmsService cpmsService;

    @Autowired
    Tracing tracing;

    @Autowired
    private ObjectMapper objectMapper;

    public QueueListener(CpmsService cpmsService) {
        this.cpmsService = cpmsService;
    }

    @RabbitListener(queues = "#{getQueueCpmsStepUpdate}", errorHandler = "queueListenerErrorHandler")
    public void receiveStepUpdateMessage(Message message) {
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        TraceContextOrSamplingFlags contextOrFlags = tracing.propagation()
                .extractor((Propagation.Getter<Map<String, Object>, String>) (request, key) -> {
                    Object value = request.get(key);
                    return value != null ? value.toString() : null;
                })
                .extract(headers);
        Span span = tracing.tracer().nextSpan(contextOrFlags).name("cpms_job_request").start();
        try (Tracer.SpanInScope ws = tracing.tracer().withSpanInScope(span)) {
            StepUpdateDto stepUpdateDto = objectMapper.readValue(message.getBody(), StepUpdateDto.class);
            log.debug("Step ID: {}; Received StepUpdate : {}", stepUpdateDto.getStepId(), stepUpdateDto);
            cpmsService.handleStepUpdate(stepUpdateDto);
        } catch (Exception e) {
            log.error(e.getMessage());
            span.error(e);
        } finally {
            span.finish();
        }
    }

    @RabbitListener(queues = "#{getQueueCpmsJobRequest}", errorHandler = "queueListenerErrorHandler")
    public void recieveJobRequestMessage(Message message) {
        Map<String, Object> headers = message.getMessageProperties().getHeaders();
        StoredJobRequestDto storedJobRequestDto = null;
        TraceContextOrSamplingFlags contextOrFlags = tracing.propagation()
                .extractor((Propagation.Getter<Map<String, Object>, String>) (request, key) -> {
                    Object value = request.get(key);
                    return value != null ? value.toString() : null;
                })
                .extract(headers);
        Span span = tracing.tracer().nextSpan(contextOrFlags).name("cpms_job_request").start();
        try (Tracer.SpanInScope ws = tracing.tracer().withSpanInScope(span)) {
            storedJobRequestDto = objectMapper.readValue(message.getBody(), StoredJobRequestDto.class);
            log.info("Received StoredJobRequest : {}", storedJobRequestDto);
            cpmsService.handleStoredJobRequest(storedJobRequestDto);
        } catch (Exception e) {
            log.error(e.getMessage());
            span.error(e);
        } finally {
            span.finish();
        }

    }



}
