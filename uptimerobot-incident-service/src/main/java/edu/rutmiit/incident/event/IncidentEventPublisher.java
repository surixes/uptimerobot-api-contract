package edu.rutmiit.incident.event;

import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import edu.rutmiit.demo.events.IncidentEvent;
import edu.rutmiit.demo.events.EventEnvelope;
import edu.rutmiit.demo.events.RoutingKeys;

@Component
public class IncidentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(IncidentEventPublisher.class);
    private static final String SOURCE = "incident-service";

    private final RabbitTemplate rabbitTemplate;

    public IncidentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishOpened(Long incidentId, Long checkId, Long alertRuleId, String severity,
            String message, Instant openedAt) {
        send(RoutingKeys.INCIDENT_OPENED, new IncidentEvent.Opened(incidentId, checkId, alertRuleId,
                severity, message, openedAt));
    }

    public void publishResolved(Long incidentId, Instant resolvedAt) {
        send(RoutingKeys.INCIDENT_RESOLVED, new IncidentEvent.Resolved(incidentId, resolvedAt));
    }

    public void publishClosed(Long incidentId, Instant closedAt) {
        send(RoutingKeys.INCIDENT_CLOSED, new IncidentEvent.Closed(incidentId, closedAt));
    }

    private void send(String routingKey, IncidentEvent event) {
        try {
            EventEnvelope<IncidentEvent> envelope = EventEnvelope.wrap(event, SOURCE, routingKey);
            rabbitTemplate.convertAndSend(RoutingKeys.EXCHANGE, routingKey, envelope);
            log.info("Incident event sent: {} [eventId={}]", routingKey,
                    envelope.metadata().eventId());
        } catch (Exception e) {
            log.error("Failed to send incident event {}: {}", routingKey, e.getMessage(), e);
        }
    }
}
