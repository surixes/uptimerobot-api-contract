package edu.rutmiit.demo.uptimerobotrest.graphql.types;

import java.util.List;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckResponse;

public record CheckConnectionGql(
    List<CheckResponse> items,
    PageInfoGql pageInfo,
    Integer totalElements    
) {}
