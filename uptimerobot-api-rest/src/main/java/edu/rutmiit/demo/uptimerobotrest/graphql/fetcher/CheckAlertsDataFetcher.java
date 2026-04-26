package edu.rutmiit.demo.uptimerobotrest.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.InputArgument;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PagedResponse;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.AlertConnectionGql;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.PageInfoGql;
import edu.rutmiit.demo.uptimerobotrest.service.AlertService;

@DgsComponent
public class CheckAlertsDataFetcher {

    private final AlertService alertService;

    public CheckAlertsDataFetcher(AlertService alertService) {
        this.alertService = alertService;
    }

    @DgsData(parentType = "Check", field = "alerts")
    public AlertConnectionGql alerts(DgsDataFetchingEnvironment dfe, @InputArgument Integer page,
            @InputArgument Integer size) {

        CheckResponse check = dfe.getSource();

        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 20;

        PagedResponse<AlertResponse> paged =
                alertService.findByCheckId(check.getId(), pageNum, pageSize, null, null, null);

        return new AlertConnectionGql(paged.content(), new PageInfoGql(paged.pageNumber(),
                paged.pageSize(), paged.totalPages(), paged.last()), (int) paged.totalElements());
    }
}
