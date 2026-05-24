package edu.rutmiit.incident.service;

import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import edu.rutmiit.demo.dto.AlertRuleSnapshot;
import edu.rutmiit.demo.dto.IncidentSnapshot;
import edu.rutmiit.demo.events.CheckEvent;
import edu.rutmiit.incident.event.IncidentEventPublisher;
import edu.rutmiit.incident.model.IncidentKey;
import edu.rutmiit.incident.model.IncidentState;

@Service
public class IncidentProcessor {

    private static final int FAILURE_THRESHOLD = 3;

    private static final int RESOLVE_SUCCESS_THRESHOLD = 1;
    private static final int CLOSE_SUCCESS_THRESHOLD = 3;

    private static final String STATUS_OPEN = "OPEN";
    private static final String STATUS_ACKNOWLEDGED = "ACKNOWLEDGED";
    private static final String STATUS_RESOLVED = "RESOLVED";

    private final IncidentEventPublisher incidentEventPublisher;
    private final Map<IncidentKey, IncidentState> states = new ConcurrentHashMap<>();

    public IncidentProcessor(IncidentEventPublisher incidentEventPublisher) {
        this.incidentEventPublisher = incidentEventPublisher;
    }

    public void handleExecuted(CheckEvent.Executed event) {
        Long checkId = event.check().checkId();
        boolean success = event.execution().success();
        Instant now = Instant.now();

        for (AlertRuleSnapshot rule : event.alertRules()) {
            if (!Boolean.TRUE.equals(rule.enabled())) {
                continue;
            }

            IncidentKey key = new IncidentKey(checkId, rule.alertRuleId());

            states.compute(key, (k, current) -> {
                if (current == null) {
                    current = restoreStateFromEvent(event, rule);
                }

                int threshold =
                        rule.failureCount() != null ? rule.failureCount() : FAILURE_THRESHOLD;

                if (success) {
                    IncidentState afterSuccess = current.onSuccess();

                    if (current.open()
                            && afterSuccess.consecutiveSuccesses() >= RESOLVE_SUCCESS_THRESHOLD) {

                        incidentEventPublisher.publishResolved(current.incidentId(), now);

                        return afterSuccess.markResolved();
                    }

                    if (current.resolved()
                            && afterSuccess.consecutiveSuccesses() >= CLOSE_SUCCESS_THRESHOLD) {

                        incidentEventPublisher.publishClosed(current.incidentId(), now);

                        return afterSuccess.closed();
                    }

                    return afterSuccess;
                }

                IncidentState failed = current.onFailure();

                if (current.open()) {
                    return failed;
                }

                if (current.resolved()) {
                    incidentEventPublisher.publishOpened(current.incidentId(), checkId,
                            rule.alertRuleId(), rule.severity(),
                            "Incident reopened. Last failure: " + event.execution().failureReason(),
                            now);

                    return failed.reopened();
                }

                if (failed.consecutiveFailures() >= threshold) {
                    long incidentId = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;

                    String message = "Check failed " + failed.consecutiveFailures() + " times";

                    incidentEventPublisher.publishOpened(incidentId, checkId, rule.alertRuleId(),
                            rule.severity(),
                            message + ". Last failure: " + event.execution().failureReason(), now);

                    return failed.opened(incidentId);
                }

                return failed;
            });
        }
    }

    private IncidentState restoreStateFromEvent(CheckEvent.Executed event, AlertRuleSnapshot rule) {
        if (event.incidents() == null || event.incidents().isEmpty()) {
            return IncidentState.empty();
        }

        return event.incidents().stream()
                .filter(incident -> Objects.equals(incident.checkId(), event.check().checkId()))
                .filter(incident -> Objects.equals(incident.alertRuleId(), rule.alertRuleId()))
                .filter(incident -> isActiveIncidentStatus(incident.status()))
                .max(Comparator.comparing(IncidentSnapshot::incidentId)).map(this::toIncidentState)
                .orElseGet(IncidentState::empty);
    }

    private boolean isActiveIncidentStatus(String status) {
        return STATUS_OPEN.equals(status) || STATUS_ACKNOWLEDGED.equals(status)
                || STATUS_RESOLVED.equals(status);
    }

    private IncidentState toIncidentState(IncidentSnapshot incident) {
        if (STATUS_RESOLVED.equals(incident.status())) {
            return new IncidentState(false, true, incident.incidentId(), 0, 0);
        }

        return new IncidentState(true, false, incident.incidentId(), 0, 0);
    }
}
