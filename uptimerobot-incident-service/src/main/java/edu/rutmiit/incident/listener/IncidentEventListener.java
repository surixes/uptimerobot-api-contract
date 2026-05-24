package edu.rutmiit.incident.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.rutmiit.demo.events.CheckEvent;
import edu.rutmiit.demo.events.EventMetadata;
import edu.rutmiit.demo.events.RoutingKeys;
import edu.rutmiit.incident.config.RabbitMQConfig;
import edu.rutmiit.incident.service.IncidentProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class IncidentEventListener {

    private static final Logger log = LoggerFactory.getLogger(IncidentEventListener.class);

    private final IncidentProcessor incidentProcessor;
    private final ObjectMapper jsonMapper;

    public IncidentEventListener(IncidentProcessor incidentProcessor, ObjectMapper jsonMapper) {
        this.incidentProcessor = incidentProcessor;
        this.jsonMapper = jsonMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.INCIDENT_PROCESSOR_QUEUE)
    public void handleEvent(Message message) {
        try {
            JsonNode root = jsonMapper.readTree(message.getBody());
            EventMetadata metadata =
                    jsonMapper.treeToValue(root.get("metadata"), EventMetadata.class);
            JsonNode payloadNode = root.get("payload");

            switch (metadata.eventType()) {
                case RoutingKeys.CHECK_EXECUTED -> {
                    CheckEvent.Executed event =
                            jsonMapper.treeToValue(payloadNode, CheckEvent.Executed.class);
                    incidentProcessor.handleExecuted(event);
                }
                default -> log.debug("Ignored event type: {}", metadata.eventType());
            }

        } catch (Exception e) {
            log.error("Failed to process event: {}", e.getMessage(), e);
            throw new RuntimeException("Could not process domain event", e);
        }
    }
}
