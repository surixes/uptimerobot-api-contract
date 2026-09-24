CREATE TABLE checks (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    url VARCHAR(1000) NOT NULL,
    method VARCHAR(25) NOT NULL,
    interval_sec INTEGER NOT NULL,
    timeout_ms INTEGER NOT NULL,
    enabled BOOLEAN NOT NULL,
    expected_status_code INTEGER,
    expected_response_contains VARCHAR(1000),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    last_response_time_ms INTEGER NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_checks_uuid UNIQUE (uuid),
    CONSTRAINT uk_checks_name UNIQUE (name)
);

-- These three records replace the checks that were previously seeded in InMemoryStorage.
INSERT INTO checks (
    id, uuid, name, url, method, interval_sec, timeout_ms, enabled,
    expected_status_code, expected_response_contains, created_at, updated_at,
    last_response_time_ms, version
) VALUES
    (1, '11111111-1111-1111-1111-111111111111', 'gateway-check',
     'https://catfact.ninja.test', 'GET', 10, 5000, TRUE, 200, NULL,
     CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '2 hours', 80, 0),
    (2, '22222222-2222-2222-2222-222222222222', 'catfact-api-check',
     'https://catfact.ninja/fact', 'GET', 15, 5000, TRUE, 200, 'fact',
     CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '1 hour', 180, 0),
    (3, '33333333-3333-3333-3333-333333333333', 'dog-api-check',
     'https://dog.ceo/api/breeds/image/random', 'GET', 20, 5000, TRUE, 200, 'success',
     CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '45 minutes', 220, 0);

SELECT setval(pg_get_serial_sequence('checks', 'id'), (SELECT MAX(id) FROM checks), true);
