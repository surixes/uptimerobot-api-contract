package edu.rutmiit.demo.events;

import java.time.OffsetDateTime;
import java.util.List;
import edu.rutmiit.demo.dto.AlertRuleSnapshot;
import edu.rutmiit.demo.dto.CheckExecutionSnapshot;
import edu.rutmiit.demo.dto.CheckSnapshot;
import edu.rutmiit.demo.dto.IncidentSnapshot;

public sealed interface CheckEvent {

        record Created(
                Long checkId,
                String name,
                String url,
                String method,
                Integer timeoutMs,
                Integer expectedStatusCode,
                String expectedResponseContains,
                boolean enabled
        ) implements CheckEvent {}

        record Updated(
                Long checkId,
                String name,
                String url,
                String method,
                Integer intervalSec,
                Integer timeoutMs,
                Integer expectedStatusCode,
                String expectedResponseContains,
                boolean enabled
        ) implements CheckEvent {}

        record Deleted(
                Long checkId,
                int deletedAlertRulesCount
        ) implements CheckEvent {}

        record Executed(
                String executionId,
                OffsetDateTime executedAt,
                CheckSnapshot check,
                CheckExecutionSnapshot execution,
                List<AlertRuleSnapshot> alertRules,
                List<IncidentSnapshot> incidents
        ) implements CheckEvent {}
}
