package edu.rutmiit.demo.grpcenrichment.config;

import edu.rutmiit.demo.events.RoutingKeys;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

/**
 * Конфигурация RabbitMQ для enrichment-клиента.
 *
 * Этот сервис одновременно и consumer (слушает book.created),
 * и publisher (публикует book.enriched). Поэтому конфигурация включает:
 * - Exchange (общий для всех сервисов)
 * - Очередь для приёма book.created
 * - DLQ для необработанных сообщений
 * - RabbitTemplate для публикации book.enriched
 *
 * Каждый consumer определяет свою очередь и привязку.
 * Enrichment-клиент слушает только book.created (не все события),
 * поэтому binding key = "book.created", а не "#".
 */
@Configuration
public class RabbitMQConfig {

    public static final String SLA_CHECK_EXECUTED_QUEUE = "q.sla.check-executed";
    public static final String SLA_CHECK_EXECUTED_DLQ = "q.sla.check-executed.dlq";

    @Bean
    public MessageConverter jsonMessageConverter(JsonMapper jsonMapper) {
        return new JacksonJsonMessageConverter(jsonMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(3);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    @Bean
    public TopicExchange eventsExchange() {
        return ExchangeBuilder
                .topicExchange(RoutingKeys.EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder
                .directExchange(RoutingKeys.EXCHANGE + ".dlx")
                .durable(true)
                .build();
    }

    @Bean
    public Queue slaCheckExecutedQueue() {
        return QueueBuilder
                .durable(SLA_CHECK_EXECUTED_QUEUE)
                .deadLetterExchange(RoutingKeys.EXCHANGE + ".dlx")
                .deadLetterRoutingKey(SLA_CHECK_EXECUTED_DLQ)
                .build();
    }

    @Bean
    public Queue slaCheckExecutedDlq() {
        return QueueBuilder.durable(SLA_CHECK_EXECUTED_DLQ).build();
    }

    @Bean
    public Binding slaCheckExecutedBinding(Queue slaCheckExecutedQueue, TopicExchange eventsExchange) {
        return BindingBuilder
                .bind(slaCheckExecutedQueue)
                .to(eventsExchange)
                .with(RoutingKeys.CHECK_EXECUTED);
    }

    @Bean
    public Binding slaCheckExecutedDlqBinding(Queue slaCheckExecutedDlq,
                                              DirectExchange deadLetterExchange) {
        return BindingBuilder
                .bind(slaCheckExecutedDlq)
                .to(deadLetterExchange)
                .with(SLA_CHECK_EXECUTED_DLQ);
    }
}
