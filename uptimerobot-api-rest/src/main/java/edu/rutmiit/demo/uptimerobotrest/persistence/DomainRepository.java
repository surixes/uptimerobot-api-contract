package edu.rutmiit.demo.uptimerobotrest.persistence;

import edu.rutmiit.demo.uptimerobotapicontract.dto.*;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public class DomainRepository {
    private final JdbcTemplate jdbc;
    private final CheckRepository checks;

    public DomainRepository(JdbcTemplate jdbc, CheckRepository checks) {
        this.jdbc = jdbc;
        this.checks = checks;
    }

    public long nextRuleId() {
        return jdbc.queryForObject("SELECT nextval('alert_rule_ids')", Long.class);
    }

    public long nextIncidentId() {
        return jdbc.queryForObject("SELECT nextval('manual_incident_ids')", Long.class);
    }

    private CheckResponse check(long id) {
        CheckEntity e = checks.findById(id).orElseThrow();
        return CheckResponse.builder()
                .id(e.getId())
                .countryCode(e.getCountryCode())
                .name(e.getName())
                .url(e.getUrl())
                .method(e.getMethod())
                .intervalSec(e.getIntervalSec())
                .timeoutMs(e.getTimeoutMs())
                .enabled(e.getEnabled())
                .expectedStatusCode(e.getExpectedStatusCode())
                .expectedResponseContains(e.getExpectedResponseContains())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .lastResponseTimeMs(e.getLastResponseTimeMs())
                .build();
    }

    public List<AlertRuleResponse> allRules() {
        return jdbc.query("SELECT * FROM alert_rules ORDER BY id", this::mapRule);
    }

    public AlertRuleResponse findRule(Long id) {
        return jdbc.query("SELECT * FROM alert_rules WHERE id=?", this::mapRule, id).stream()
                .findFirst()
                .orElse(null);
    }

    public void deleteRule(Long id) {
        jdbc.update("DELETE FROM alert_rules WHERE id=?", id);
    }

    private AlertRuleResponse mapRule(ResultSet r, int row) throws SQLException {
        return AlertRuleResponse.builder()
                .id(r.getLong("id"))
                .check(check(r.getLong("check_id")))
                .alertName(r.getString("alert_name"))
                .ruleType(AlertRuleTypeEnum.valueOf(r.getString("rule_type")))
                .severity(IncidentSeverityEnum.valueOf(r.getString("severity")))
                .enabled(r.getObject("enabled", Boolean.class))
                .thresholdMs(r.getObject("threshold_ms", Integer.class))
                .expectedStatusCode(r.getObject("expected_status_code", Integer.class))
                .expectedResponseContains(r.getString("expected_response_contains"))
                .failureCount(r.getObject("failure_count", Integer.class))
                .message(r.getString("message"))
                .details(r.getString("details"))
                .createdAt(r.getObject("created_at", OffsetDateTime.class))
                .updatedAt(r.getObject("updated_at", OffsetDateTime.class))
                .build();
    }

    public void saveRule(AlertRuleResponse v) {
        jdbc.update(
                """
                INSERT INTO alert_rules (
                    id,
                    check_id,
                    alert_name,
                    rule_type,
                    severity,
                    enabled,
                    threshold_ms,
                    expected_status_code,
                    expected_response_contains,
                    failure_count,
                    message,
                    details,
                    created_at,
                    updated_at
                ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                ON CONFLICT (id) DO UPDATE SET
                    check_id = EXCLUDED.check_id,
                    alert_name = EXCLUDED.alert_name,
                    rule_type = EXCLUDED.rule_type,
                    severity = EXCLUDED.severity,
                    enabled = EXCLUDED.enabled,
                    threshold_ms = EXCLUDED.threshold_ms,
                    expected_status_code = EXCLUDED.expected_status_code,
                    expected_response_contains = EXCLUDED.expected_response_contains,
                    failure_count = EXCLUDED.failure_count,
                    message = EXCLUDED.message,
                    details = EXCLUDED.details,
                    created_at = EXCLUDED.created_at,
                    updated_at = EXCLUDED.updated_at
                """,
                v.getId(),
                v.getCheck().getId(),
                v.getAlertName(),
                v.getRuleType().name(),
                v.getSeverity().name(),
                v.getEnabled(),
                v.getThresholdMs(),
                v.getExpectedStatusCode(),
                v.getExpectedResponseContains(),
                v.getFailureCount(),
                v.getMessage(),
                v.getDetails(),
                v.getCreatedAt(),
                v.getUpdatedAt());
    }

    public List<IncidentResponse> allIncidents() {
        return jdbc.query("SELECT * FROM incidents ORDER BY id", this::mapIncident);
    }

    public IncidentResponse findIncident(Long id) {
        return jdbc.query("SELECT * FROM incidents WHERE id=?", this::mapIncident, id).stream()
                .findFirst()
                .orElse(null);
    }

    public void deleteIncident(Long id) {
        jdbc.update("DELETE FROM incidents WHERE id=?", id);
    }

    private IncidentResponse mapIncident(ResultSet r, int row) throws SQLException {
        return IncidentResponse.builder()
                .id(r.getLong("id"))
                .check(check(r.getLong("check_id")))
                .alertRule(findRule(r.getLong("alert_rule_id")))
                .status(IncidentStatusEnum.valueOf(r.getString("status")))
                .severity(IncidentSeverityEnum.valueOf(r.getString("severity")))
                .message(r.getString("message"))
                .details(r.getString("details"))
                .openedAt(r.getObject("opened_at", OffsetDateTime.class))
                .updatedAt(r.getObject("updated_at", OffsetDateTime.class))
                .acknowledgedAt(r.getObject("acknowledged_at", OffsetDateTime.class))
                .acknowledgedBy(r.getString("acknowledged_by"))
                .resolvedAt(r.getObject("resolved_at", OffsetDateTime.class))
                .closedAt(r.getObject("closed_at", OffsetDateTime.class))
                .build();
    }

    public void saveIncident(IncidentResponse v) {
        jdbc.update(
                """
                INSERT INTO incidents (
                    id,
                    check_id,
                    alert_rule_id,
                    status,
                    severity,
                    message,
                    details,
                    opened_at,
                    updated_at,
                    acknowledged_at,
                    acknowledged_by,
                    resolved_at,
                    closed_at
                ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)
                ON CONFLICT (id) DO UPDATE SET
                    check_id = EXCLUDED.check_id,
                    alert_rule_id = EXCLUDED.alert_rule_id,
                    status = EXCLUDED.status,
                    severity = EXCLUDED.severity,
                    message = EXCLUDED.message,
                    details = EXCLUDED.details,
                    opened_at = EXCLUDED.opened_at,
                    updated_at = EXCLUDED.updated_at,
                    acknowledged_at = EXCLUDED.acknowledged_at,
                    acknowledged_by = EXCLUDED.acknowledged_by,
                    resolved_at = EXCLUDED.resolved_at,
                    closed_at = EXCLUDED.closed_at
                """,
                v.getId(),
                v.getCheck().getId(),
                v.getAlertRule().getId(),
                v.getStatus().name(),
                v.getSeverity().name(),
                v.getMessage(),
                v.getDetails(),
                v.getOpenedAt(),
                v.getUpdatedAt(),
                v.getAcknowledgedAt(),
                v.getAcknowledgedBy(),
                v.getResolvedAt(),
                v.getClosedAt());
    }
}
