package edu.rutmiit.demo.uptimerobotrest.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRuleRequest;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRuleResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PagedResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PatchAlertRuleRequest;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.AlertRuleConnectionGql;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.AlertRuleFilterGql;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.CreateAlertRuleInputGql;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.PageInfoGql;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.PatchAlertRuleInputGql;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.UpdateAlertRuleInputGql;
import edu.rutmiit.demo.uptimerobotrest.service.AlertRuleService;

@DgsComponent
public class AlertRuleDataFetcher {

    private final AlertRuleService alertRuleService;

    public AlertRuleDataFetcher(AlertRuleService alertRuleService) {
        this.alertRuleService = alertRuleService;
    }

    @DgsQuery
    public AlertRuleResponse alertRule(@InputArgument String id) {
        return alertRuleService.findById(Long.parseLong(id));
    }

    @DgsQuery
    public AlertRuleConnectionGql alertRules(@InputArgument AlertRuleFilterGql filter,
            @InputArgument Integer page, @InputArgument Integer size) {

        int pageNum = page != null ? page : 0;
        int sizeNum = size != null ? size : 20;

        Long alertRuleId = filter != null && filter.alertRuleId() != null
                ? Long.parseLong(filter.alertRuleId())
                : null;

        Long checkId = filter != null && filter.checkId() != null ? Long.parseLong(filter.checkId())
                : null;

        PagedResponse<AlertRuleResponse> paged = alertRuleService.findAll(alertRuleId, checkId,
                filter != null ? filter.ruleType() : null,
                filter != null ? filter.severity() : null, filter != null ? filter.enabled() : null,
                filter != null ? filter.url() : null, pageNum, sizeNum);

        return new AlertRuleConnectionGql(paged.content(), new PageInfoGql(paged.pageNumber(),
                paged.pageSize(), paged.totalPages(), paged.last()),
                Math.toIntExact(paged.totalElements()));
    }

    @DgsQuery
    public AlertRuleConnectionGql alertRulesByCheckId(@InputArgument String checkId,
            @InputArgument Integer page, @InputArgument Integer size) {

        int pageNum = page != null ? page : 0;
        int sizeNum = size != null ? size : 20;

        Long checkIdValue = Long.parseLong(checkId);

        PagedResponse<AlertRuleResponse> paged = alertRuleService.findByCheckId(checkIdValue,
                pageNum, sizeNum, null, null, null, null);

        return new AlertRuleConnectionGql(paged.content(), new PageInfoGql(paged.pageNumber(),
                paged.pageSize(), paged.totalPages(), paged.last()),
                Math.toIntExact(paged.totalElements()));
    }

    @DgsMutation
    public AlertRuleResponse createAlertRule(@InputArgument CreateAlertRuleInputGql input) {
        AlertRuleRequest request = new AlertRuleRequest(Long.parseLong(input.checkId()),
                input.alertName(), input.ruleType(), input.severity(), input.enabled(),
                input.thresholdMs(), input.expectedStatusCode(), input.expectedResponseContains(),
                input.failureCount(), input.message(), input.details());
        return alertRuleService.create(request);
    }

    @DgsMutation
    public AlertRuleResponse updateAlertRule(@InputArgument String id,
            @InputArgument UpdateAlertRuleInputGql input) {
        Long alertRuleId = Long.parseLong(id);
        Long checkId = alertRuleService.findById(alertRuleId).getCheck().getId();

        AlertRuleRequest request = new AlertRuleRequest(checkId, input.alertName(),
                input.ruleType(), input.severity(), input.enabled(), input.thresholdMs(),
                input.expectedStatusCode(), input.expectedResponseContains(), input.failureCount(),
                input.message(), input.details());
        return alertRuleService.update(alertRuleId, request);
    }

    @DgsMutation
    public AlertRuleResponse patchAlertRule(@InputArgument String id,
            @InputArgument PatchAlertRuleInputGql input) {

        Long alertRuleId = Long.parseLong(id);

        PatchAlertRuleRequest request = new PatchAlertRuleRequest(
                input.checkId() != null ? Long.parseLong(input.checkId()) : null, input.alertName(),
                input.severity(), input.message(), input.details());
        return alertRuleService.patchAlertRule(alertRuleId, request);
    }

    @DgsMutation
    public Boolean deleteAlertRule(@InputArgument String id) {
        alertRuleService.delete(Long.parseLong(id));
        return true;
    }
}
