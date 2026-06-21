package edu.rutmiit.demo.uptimerobotrest.event;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.rutmiit.demo.dto.AlertRuleSnapshot;
import edu.rutmiit.demo.dto.CheckExecutionSnapshot;
import edu.rutmiit.demo.dto.CheckSnapshot;
import edu.rutmiit.demo.dto.IncidentSnapshot;
import edu.rutmiit.demo.events.CheckEvent;
import edu.rutmiit.demo.events.EventEnvelope;
import edu.rutmiit.demo.events.RoutingKeys;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRuleResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentResponse;

@Component
public class CheckEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(CheckEventPublisher.class);
    private static final String SOURCE = "uptimerobot-api-rest";

    private final RabbitTemplate rabbitTemplate;

    public CheckEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishCreated(CheckResponse check) {
        var event = new CheckEvent.Created(
            check.getId(),
            check.getName(),
            check.getUrl(),
            check.getMethod(),
            check.getTimeoutMs(),
            check.getExpectedStatusCode(),
            check.getExpectedResponseContains(),
            check.getEnabled()
        );
        send(RoutingKeys.CHECK_CREATED, event);
    }

    public void publishUpdate(CheckResponse check) {
        var event = new CheckEvent.Updated(
            check.getId(),
            check.getName(),
            check.getUrl(),
            check.getMethod(),
            check.getIntervalSec(),
            check.getTimeoutMs(),
            check.getExpectedStatusCode(),
            check.getExpectedResponseContains(),
            check.getEnabled()
        );
        send(RoutingKeys.CHECK_UPDATED, event);
    }

    public void publishDeleted(CheckResponse check, int deletedAlertsCount) {
        var event = new CheckEvent.Deleted(
            check.getId(),
            deletedAlertsCount
        );
        send(RoutingKeys.CHECK_DELETED, event);
    }

    public void publishExecuted(
            CheckResponse check,
            CheckExecutionSnapshot execution,
            List<AlertRuleResponse> alertRules,
            List<IncidentResponse> incidents
    ) {

        List<AlertRuleSnapshot> alertRuleSnapshots = alertRules.stream()
                .map(this::toAlertRuleSnapshot)
                .toList();

        List<IncidentSnapshot> incidentSnapshots = incidents.stream()
                .map(this::toIncidentSnapshot)
                .toList();
        var event = new CheckEvent.Executed(
                execution.UUID(),
                execution.executedAt(),
                toCheckSnapshot(check),
                execution,
                alertRuleSnapshots,
                incidentSnapshots
        );

        send(RoutingKeys.CHECK_EXECUTED, event);
    }
    
    private CheckSnapshot toCheckSnapshot(CheckResponse check) {
        return new CheckSnapshot(check.getId(), check.getName(), check.getUrl(), check.getMethod(),
                check.getIntervalSec(), check.getTimeoutMs(), check.getEnabled(),
                check.getExpectedStatusCode(), check.getExpectedResponseContains());
    }

    private AlertRuleSnapshot toAlertRuleSnapshot(AlertRuleResponse alertRule) {
        return new AlertRuleSnapshot(
            alertRule.getId(),
            alertRule.getCheck().getId(),
            alertRule.getAlertName(),
            alertRule.getRuleType().toString(),
            alertRule.getSeverity().toString(),
            alertRule.getEnabled(),
            alertRule.getThresholdMs(),
            alertRule.getExpectedStatusCode(),
            alertRule.getExpectedResponseContains(),
            alertRule.getFailureCount(),
            alertRule.getMessage(),
            alertRule.getDetails()
        );
    }

    private IncidentSnapshot toIncidentSnapshot(IncidentResponse incident) {
        return new IncidentSnapshot(
            incident.getId(),
            incident.getCheck().getId(),
            incident.getAlertRule().getId(),
            incident.getStatus().toString(),
            incident.getSeverity().toString(),
            incident.getMessage(),
            incident.getDetails()
        );
    }
    
    private void send(String routingKey, CheckEvent event) {
        try {
            EventEnvelope<CheckEvent> envelope = EventEnvelope.wrap(event, SOURCE, routingKey);
            rabbitTemplate.convertAndSend(RoutingKeys.EXCHANGE, routingKey, envelope);
            log.info("event published: routingKey={} eventId={} source={}", routingKey, envelope.metadata().eventId(), SOURCE);
        } catch (Exception e) {
            log.error("event publish failed: routingKey={} error={}", routingKey, e.getMessage(), e);
        }
    }
}
