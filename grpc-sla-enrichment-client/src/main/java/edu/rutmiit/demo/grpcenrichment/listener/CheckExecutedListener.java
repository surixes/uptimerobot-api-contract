package edu.rutmiit.demo.grpcenrichment.listener;

import edu.rutmiit.demo.events.CheckEvent;
import edu.rutmiit.demo.events.EventMetadata;
import edu.rutmiit.demo.grpcenrichment.config.RabbitMQConfig;
import edu.rutmiit.demo.grpcenrichment.model.ExecutionSample;
import edu.rutmiit.demo.grpcenrichment.service.SlaWindowAggregator;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Слушатель событий check.executed из RabbitMQ.
 *
 * Десериализация — ручная, потому что EventEnvelope<T>
 * является generic-типом, и Jackson не может определить конкретный подтип T.
 */
@Component
public class CheckExecutedListener {

        private static final Logger log = LoggerFactory.getLogger(CheckExecutedListener.class);

        private final JsonMapper jsonMapper;
        private final SlaWindowAggregator aggregator;

        public CheckExecutedListener(JsonMapper jsonMapper, SlaWindowAggregator aggregator) {
                this.jsonMapper = jsonMapper;
                this.aggregator = aggregator;
        }

        @RabbitListener(queues = RabbitMQConfig.SLA_CHECK_EXECUTED_QUEUE, messageConverter = "")
        public void handleCheckExecuted(Message message) {
                try {
                        JsonNode root = jsonMapper.readTree(message.getBody());
                        EventMetadata metadata = jsonMapper.treeToValue(root.get("metadata"),
                                        EventMetadata.class);
                        CheckEvent.Executed event = jsonMapper.treeToValue(root.get("payload"),
                                        CheckEvent.Executed.class);

                        Long checkId = event.check().checkId();
                        String checkName = event.check().name();
                        boolean success = event.execution().success();

                        ExecutionSample sample = new ExecutionSample(event.executionId(), checkId,
                                        checkName, success, responseTimeMs(event),
                                        event.execution().failureReason(),
                                        event.executedAt() != null ? event.executedAt().toInstant()
                                                        : Instant.now());

                        aggregator.add(sample);

                        log.info("sla sample received: checkId={} success={} responseTimeMs={} eventId={}",
                                        sample.checkId(), sample.success(), sample.responseTimeMs(),
                                        metadata.eventId());
                } catch (Exception e) {
                        log.error("sla sample processing failed: error={}", e.getMessage(), e);
                        throw new RuntimeException("Не удалось обработать check.executed для SLA",
                                        e);
                }
        }

        /**
         * Если в твоём CheckExecutionSnapshot поле называется иначе, поменяй только эту строку.
         */
        private int responseTimeMs(CheckEvent.Executed event) {
                Integer value = event.execution().responseTimeMs();
                return value != null ? value : 0;
        }
}
