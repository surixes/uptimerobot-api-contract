package edu.rutmiit.demo.uptimerobotrest.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import edu.rutmiit.demo.events.RoutingKeys;

@Configuration
public class RabbitMQConfig {

    public static final String INCIDENT_EVENTS_QUEUE = "q.incident.events";
    public static final String INCIDENT_EVENTS_DLQ = "q.incident.events.dlq";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }

    @Bean
    public TopicExchange eventsExchange() {
        return ExchangeBuilder.topicExchange(RoutingKeys.EXCHANGE).durable(true).build();
    }

    @Bean
    public Queue incidentEventsQueue() {
        return new Queue(INCIDENT_EVENTS_QUEUE, true);
    }

    @Bean
    public Binding incidentEventsBinding(Queue incidentEventsQueue, TopicExchange eventsExchange) {
        return BindingBuilder.bind(incidentEventsQueue).to(eventsExchange)
                .with(RoutingKeys.ALL_INCIDENT_EVENTS);
    }
}
