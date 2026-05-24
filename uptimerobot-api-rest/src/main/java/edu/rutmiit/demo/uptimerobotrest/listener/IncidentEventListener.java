package edu.rutmiit.demo.uptimerobotrest.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.rutmiit.demo.events.EventMetadata;
import edu.rutmiit.demo.events.IncidentEvent;
import edu.rutmiit.demo.events.RoutingKeys;
import edu.rutmiit.demo.uptimerobotrest.config.RabbitMQConfig;
import edu.rutmiit.demo.uptimerobotrest.service.IncidentService;

@Component
public class IncidentEventListener {

    private static final Logger log = LoggerFactory.getLogger(IncidentEventListener.class);

    private final ObjectMapper objectMapper;
    private final IncidentService incidentService;

    public IncidentEventListener(ObjectMapper objectMapper, IncidentService incidentService) {
        this.objectMapper = objectMapper;
        this.incidentService = incidentService;
    }

    @RabbitListener(queues = RabbitMQConfig.INCIDENT_EVENTS_QUEUE)
    public void handleIncidentEvent(Message message) {
        try {
            JsonNode root = objectMapper.readTree(message.getBody());
            EventMetadata metadata =
                    objectMapper.treeToValue(root.get("metadata"), EventMetadata.class);
            JsonNode payloadNode = root.get("payload");
            switch (metadata.eventType()) {
                case RoutingKeys.INCIDENT_OPENED -> {
                    IncidentEvent.Opened event =
                            objectMapper.treeToValue(payloadNode, IncidentEvent.Opened.class);
                    incidentService.markOpened(event);
                }
                case RoutingKeys.INCIDENT_RESOLVED -> {
                    IncidentEvent.Resolved event =
                            objectMapper.treeToValue(payloadNode, IncidentEvent.Resolved.class);
                    incidentService.markResolved(event.incidentId());
                }
                case RoutingKeys.INCIDENT_CLOSED -> {
                    IncidentEvent.Closed event =
                            objectMapper.treeToValue(payloadNode, IncidentEvent.Closed.class);
                    incidentService.markClosed(event.incidentId());
                }
                default -> log.debug("Ignored event type: {}", metadata.eventType());
            }
        } catch (Exception e) {
            log.error("Failed to process incident event: {}", e.getMessage(), e);
            throw new RuntimeException("Could not process incident event", e);
        }
    }
}
