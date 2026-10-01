CREATE SEQUENCE alert_rule_ids START 1;
CREATE SEQUENCE manual_incident_ids START -1 INCREMENT -1 MINVALUE -9223372036854775808 MAXVALUE -1 RESTART -1;
CREATE TABLE alert_rules (
    UNIQUE (id, check_id),
    id BIGINT PRIMARY KEY,
    check_id BIGINT NOT NULL REFERENCES checks(id) ON DELETE CASCADE,
    alert_name TEXT NOT NULL,
    rule_type VARCHAR(80) NOT NULL,
    severity VARCHAR(80) NOT NULL,
    enabled BOOLEAN NOT NULL,
    threshold_ms INTEGER,
    expected_status_code INTEGER,
    expected_response_contains TEXT,
    failure_count INTEGER,
    message TEXT,
    details TEXT,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_alert_rules_check ON alert_rules(check_id);
CREATE TABLE incidents (
    id BIGINT PRIMARY KEY,
    check_id BIGINT NOT NULL REFERENCES checks(id) ON DELETE CASCADE,
    alert_rule_id BIGINT NOT NULL,
    FOREIGN KEY (alert_rule_id, check_id) REFERENCES alert_rules(id, check_id) ON UPDATE CASCADE ON DELETE CASCADE,
    status VARCHAR(80) NOT NULL,
    severity VARCHAR(80) NOT NULL,
    message TEXT,
    details TEXT,
    opened_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    acknowledged_at TIMESTAMPTZ,
    acknowledged_by TEXT,
    resolved_at TIMESTAMPTZ,
    closed_at TIMESTAMPTZ
);
CREATE INDEX idx_incidents_check ON incidents(check_id);
CREATE INDEX idx_incidents_rule ON incidents(alert_rule_id);
