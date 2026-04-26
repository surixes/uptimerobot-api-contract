package edu.rutmiit.demo.uptimerobotrest.graphql.types;

import java.util.List;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertResponse;

public record AlertConnectionGql(
    List<AlertResponse> items,
    PageInfoGql pageInfo,
    Integer totalElements
) {}