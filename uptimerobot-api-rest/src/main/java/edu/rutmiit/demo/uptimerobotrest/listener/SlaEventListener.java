package edu.rutmiit.demo.uptimerobotrest.listener;

import edu.rutmiit.demo.events.EventMetadata;
import edu.rutmiit.demo.events.RoutingKeys;
import edu.rutmiit.demo.events.SlaEvent;
import edu.rutmiit.demo.uptimerobotrest.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@Component
public class SlaEventListener {

    private static final Logger log = LoggerFactory.getLogger(SlaEventListener.class);

    private final JsonMapper jsonMapper;

    public SlaEventListener(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.SLA_EVENTS_QUEUE)
    public void handleSlaEvent(Message message) {
        try {
            JsonNode root = jsonMapper.readTree(message.getBody());
            EventMetadata metadata = jsonMapper.treeToValue(root.get("metadata"), EventMetadata.class);
            JsonNode payloadNode = root.get("payload");

            switch (metadata.eventType()) {
                case RoutingKeys.SLA_CALCULATED -> {
                    SlaEvent.Calculated event =
                            jsonMapper.treeToValue(payloadNode, SlaEvent.Calculated.class);

                    log.info(
                            "sla event received: checkId={} checkName={} uptime={} status={} total={} success={} failure={} avgMs={} maxMs={} windowStart={} windowEnd={}",
                            event.checkId(),
                            event.checkName(),
                            event.uptimePercent(),
                            event.availabilityStatus(),
                            event.totalExecutions(),
                            event.successCount(),
                            event.failureCount(),
                            event.averageResponseTimeMs(),
                            event.maxResponseTimeMs(),
                            event.windowStartedAt(),
                            event.windowEndedAt()
                    );
                }
                default -> log.debug("sla event ignored: eventType={}", metadata.eventType());
            }
        } catch (Exception e) {
            log.error("sla event processing failed: error={}", e.getMessage(), e);
            throw new RuntimeException("Could not process SLA event", e);
        }
    }
}
