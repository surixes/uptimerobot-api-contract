package edu.rutmiit.demo.uptimerobotrest.graphql.types;

import java.util.List;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentResponse;

public record IncidentConnectionGql(
        List<IncidentResponse> items,
        PageInfoGql pageInfo,
        Integer totalElements
) {}
