package ua.com.valexa.cpms.exceptions;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class QueueListenerErrorHandler implements RabbitListenerErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(QueueListenerErrorHandler.class);

    @Override
    public Object handleError(Message amqpMessage, Channel channel, org.springframework.messaging.Message<?> message, ListenerExecutionFailedException exception) throws Exception {
        log.error("Error in queue listener; Message = " + new String(amqpMessage.getBody(), StandardCharsets.UTF_8).replaceAll("\n", " ").replaceAll("\r", " "));
        return null;
    }

    @Override
    public Object handleError(Message message, org.springframework.messaging.Message<?> message1, ListenerExecutionFailedException e) throws Exception {
        log.error("Error in queue listener; Message = " + new String(message.getBody(), StandardCharsets.UTF_8).replaceAll("\n", " ").replaceAll("\r", " "));
        return null;
    }
}
