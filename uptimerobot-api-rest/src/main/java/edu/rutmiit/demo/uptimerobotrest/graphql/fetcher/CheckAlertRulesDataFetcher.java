package edu.rutmiit.demo.uptimerobotrest.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.InputArgument;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRuleResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PagedResponse;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.AlertRuleConnectionGql;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.PageInfoGql;
import edu.rutmiit.demo.uptimerobotrest.service.AlertRuleService;

@DgsComponent
public class CheckAlertRulesDataFetcher {

    private final AlertRuleService alertRuleService;

    public CheckAlertRulesDataFetcher(AlertRuleService alertRuleService) {
        this.alertRuleService = alertRuleService;
    }

    @DgsData(parentType = "Check", field = "alertRules")
    public AlertRuleConnectionGql alertRules(DgsDataFetchingEnvironment dfe,
            @InputArgument Integer page, @InputArgument Integer size) {

        CheckResponse check = dfe.getSource();

        int pageNum = page != null ? page : 0;
        int sizeNum = size != null ? size : 20;

        PagedResponse<AlertRuleResponse> paged = alertRuleService.findByCheckId(check.getId(),
                pageNum, sizeNum, null, null, null, null);

        return new AlertRuleConnectionGql(paged.content(), new PageInfoGql(paged.pageNumber(),
                paged.pageSize(), paged.totalPages(), paged.last()),
                Math.toIntExact(paged.totalElements()));
    }
}
