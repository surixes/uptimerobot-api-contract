
package edu.rutmiit.demo.uptimerobotrest.graphql.types;

import java.time.OffsetDateTime;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRuleTypeEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentSeverityEnum;

public record AlertRuleFilterGql(
        String alertRuleId,
        String checkId,
        AlertRuleTypeEnum ruleType,
        IncidentSeverityEnum severity,
        Boolean enabled,
        String url,
        OffsetDateTime createdAtFrom,
        OffsetDateTime createdAtTo
) {}
