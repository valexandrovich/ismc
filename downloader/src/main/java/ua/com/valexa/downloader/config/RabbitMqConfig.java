package ua.com.valexa.downloader.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${queue.cpms.step.update}")
    private String queueCpmsStepUpdate;

    @Value("${queue.downloader.step.request}")
    private String queueDownloaderStepRequest;

    @Bean
    public String getQueueCpmsStepUpdate() {
        return queueCpmsStepUpdate;
    }

    @Bean
    public String getQueueDownloaderStepRequest() {
        return queueDownloaderStepRequest;
    }

    @Bean
    public Queue downloaderStepRequestQueue() {
        return new Queue(getQueueDownloaderStepRequest(), true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

}
