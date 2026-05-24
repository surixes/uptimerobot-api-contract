package edu.rutmiit.demo.uptimerobotrest.graphql.types;

import java.util.List;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRuleResponse;

public record AlertRuleConnectionGql (
        List<AlertRuleResponse> items,
        PageInfoGql pageInfo,
        Integer totalElements
) {}