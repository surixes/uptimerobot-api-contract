package edu.rutmiit.demo.uptimerobotrest.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import edu.rutmiit.demo.events.AlertRuleEvent;
import edu.rutmiit.demo.events.EventEnvelope;
import edu.rutmiit.demo.events.RoutingKeys;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRuleResponse;

@Component
public class AlertRuleEventPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(AlertRuleEventPublisher.class);

    private static final String SOURCE = "uptimerobot-api-rest";

    private final RabbitTemplate rabbitTemplate;

    public AlertRuleEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void alertRuleCreated(AlertRuleResponse alertRule) {

        var event = new AlertRuleEvent.Created(
                alertRule.getId(),
                alertRule.getCheck().getId(),
                alertRule.getAlertName(),
                alertRule.getRuleType().name(),
                alertRule.getSeverity().name(),
                alertRule.getEnabled()
        );

        send(RoutingKeys.ALERT_RULE_CREATED, event);
    }

    public void alertRuleUpdated(AlertRuleResponse alertRule) {

        var event = new AlertRuleEvent.Updated(
                alertRule.getId(),
                alertRule.getCheck().getId(),
                alertRule.getAlertName(),
                alertRule.getRuleType().name(),
                alertRule.getSeverity().name(),
                alertRule.getEnabled()
        );

        send(RoutingKeys.ALERT_RULE_UPDATED, event);
    }

    public void alertRuleDeleted(AlertRuleResponse alertRule) {

        var event = new AlertRuleEvent.Deleted(
                alertRule.getId(),
                alertRule.getCheck().getId()
        );

        send(RoutingKeys.ALERT_RULE_DELETED, event);
    }

    private void send(String routingKey, AlertRuleEvent event) {

        try {
            EventEnvelope<AlertRuleEvent> envelope =
                    EventEnvelope.wrap(event, SOURCE, routingKey);

            rabbitTemplate.convertAndSend(
                    RoutingKeys.EXCHANGE,
                    routingKey,
                    envelope
            );

            log.info(
                    "event published: routingKey={} eventId={} source={}",
                    routingKey,
                    envelope.metadata().eventId(),
                    SOURCE
            );

        } catch (Exception e) {

            log.error(
                    "event publish failed: routingKey={} error={}",
                    routingKey,
                    e.getMessage(),
                    e
            );
        }
    }
}
