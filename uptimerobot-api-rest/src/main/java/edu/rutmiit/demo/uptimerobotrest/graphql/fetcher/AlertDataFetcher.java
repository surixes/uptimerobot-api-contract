package edu.rutmiit.demo.uptimerobotrest.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRequest;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertStatusEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PagedResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PatchAlertRequest;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.AlertConnectionGql;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.AlertFilterGql;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.CreateAlertInputGql;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.PageInfoGql;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.PatchAlertInputGql;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.UpdateAlertInputGql;
import edu.rutmiit.demo.uptimerobotrest.service.AlertService;
import edu.rutmiit.demo.uptimerobotrest.service.CheckService;

@DgsComponent
public class AlertDataFetcher {

    private final AlertService alertService;
    private final CheckService checkService;

    public AlertDataFetcher(AlertService alertService, CheckService checkService) {
        this.alertService = alertService;
        this.checkService = checkService;
    }
    
    @DgsQuery
    public AlertResponse alert(@InputArgument String id) {
        return alertService.findById(Long.parseLong(id));
    }

    @DgsQuery
    public AlertConnectionGql alerts(@InputArgument AlertFilterGql filter,
            @InputArgument Integer page, @InputArgument Integer size) {
        int pageNum = page != null ? page : 0;
        int sizeNum = size != null ? size : 20;

        Long alertId = filter != null && filter.alertId() != null ? Long.parseLong(filter.alertId())
                : null;

        PagedResponse<AlertResponse> paged = alertService.findAll(alertId,
                filter != null ? filter.dateOpen() : null,
                filter != null ? filter.dateClose() : null, filter != null ? filter.status() : null,
                filter != null ? filter.url() : null, pageNum, sizeNum);

        return new AlertConnectionGql(paged.content(), new PageInfoGql(paged.pageNumber(), paged.pageSize(), paged.totalPages(), paged.last()), 
                    (int) paged.totalElements());
    }

    @DgsQuery
    public AlertConnectionGql alertsByCheckId(@InputArgument String checkId, @InputArgument Integer page,
            @InputArgument Integer size) {

        int pageNum = page != null ? page : 0;
        int sizeNum = size != null ? size : 20;

        Long checkIdValue = Long.parseLong(checkId);

        PagedResponse<AlertResponse> paged =
                checkService.findByCheckId(checkIdValue, pageNum, sizeNum, null, null, null, null);

        return new AlertConnectionGql(paged.content(), new PageInfoGql(paged.pageNumber(),
                paged.pageSize(), paged.totalPages(), paged.last()), (int) paged.totalElements());
    }
    
    @DgsMutation
    public AlertResponse createAlert(@InputArgument String checkId,
            @InputArgument CreateAlertInputGql input) {
        AlertRequest request = new AlertRequest(Long.parseLong(checkId), input.alertName(),
                input.severity(), input.message(), input.details());
        return alertService.create(request);
    }
    
    @DgsMutation
    public AlertResponse updateAlert(@InputArgument String id,
            @InputArgument UpdateAlertInputGql input) {
        Long checkId = alertService.findById(Long.parseLong(id)).getCheck().getId();
        AlertRequest request = new AlertRequest(checkId, input.alertName(), input.severity(),
                input.message(), input.details());
        return alertService.update(Long.parseLong(id), request);
    }

    @DgsMutation
    public AlertResponse patchAlert(@InputArgument String id,
            @InputArgument PatchAlertInputGql input) {
        PatchAlertRequest request = new PatchAlertRequest(Long.parseLong(id), input.alertName(),
                input.severity(), input.message(), input.details());
        return alertService.patchAlert(Long.parseLong(id), request);
    }

    @DgsMutation
    public Boolean acknowledgeAlert(@InputArgument String id) {
        alertService.changeAlertStatus(Long.parseLong(id), AlertStatusEnum.ACKNOWLEDGED);
        return true;
    }

    @DgsMutation
    public Boolean resolveAlert(@InputArgument String id) {
        alertService.changeAlertStatus(Long.parseLong(id), AlertStatusEnum.RESOLVED);
        return true;
    }

    @DgsMutation
    public Boolean closeAlert(@InputArgument String id) {
        alertService.changeAlertStatus(Long.parseLong(id), AlertStatusEnum.CLOSED);
        return true;
    }

    @DgsMutation
    public Boolean deleteAlert(@InputArgument String id) {
        alertService.delete(Long.parseLong(id));
        return true;
    }
}
