package edu.rutmiit.demo.uptimerobotrest.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentRequest;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PagedResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PatchIncidentRequest;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.CreateIncidentInputGql;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.IncidentConnectionGql;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.IncidentFilterGql;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.PageInfoGql;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.PatchIncidentInputGql;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.UpdateIncidentInputGql;
import edu.rutmiit.demo.uptimerobotrest.service.IncidentService;

@DgsComponent
public class IncidentDataFetcher {

    private final IncidentService incidentService;

    public IncidentDataFetcher(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @DgsQuery
    public IncidentResponse incident(@InputArgument String id) {
        return incidentService.findById(Long.parseLong(id));
    }

    @DgsQuery
    public IncidentConnectionGql incidents(@InputArgument IncidentFilterGql filter,
            @InputArgument Integer page, @InputArgument Integer size) {

        int pageNum = page != null ? page : 0;
        int sizeNum = size != null ? size : 20;

        Long incidentId =
                filter != null && filter.incidentId() != null ? Long.parseLong(filter.incidentId())
                        : null;

        Long checkId = filter != null && filter.checkId() != null ? Long.parseLong(filter.checkId())
                : null;

        Long alertRuleId = filter != null && filter.alertRuleId() != null
                ? Long.parseLong(filter.alertRuleId())
                : null;

        PagedResponse<IncidentResponse> paged = incidentService.findAll(incidentId, checkId,
                alertRuleId, filter != null ? filter.status() : null,
                filter != null ? filter.severity() : null, filter != null ? filter.url() : null,
                pageNum, sizeNum);

        return new IncidentConnectionGql(paged.content(), new PageInfoGql(paged.pageNumber(),
                paged.pageSize(), paged.totalPages(), paged.last()),
                Math.toIntExact(paged.totalElements()));
    }

    @DgsMutation
    public Boolean deleteIncident(@InputArgument String id) {
        incidentService.delete(Long.parseLong(id));
        return true;
    }

    @DgsMutation
    public IncidentResponse createIncident(@InputArgument CreateIncidentInputGql input) {
        IncidentRequest request = new IncidentRequest(
                input.checkId() != null ? Long.parseLong(input.checkId()) : null,
                input.alertRuleId() != null ? Long.parseLong(input.alertRuleId()) : null,
                input.status(), input.severity(), input.message(), input.details(),
                input.openedAt(), null);
        return incidentService.create(request);
    }

    @DgsMutation
    public IncidentResponse updateIncident(@InputArgument String id,
            @InputArgument UpdateIncidentInputGql input) {

        IncidentRequest request = new IncidentRequest(
                input.checkId() != null ? Long.parseLong(input.checkId()) : null,
                input.alertRuleId() != null ? Long.parseLong(input.alertRuleId()) : null,
                input.status(), input.severity(), input.message(), input.details(),
                input.openedAt(), null);
        return incidentService.update(Long.parseLong(id), request);
    }

    @DgsMutation
    public IncidentResponse patchIncident(@InputArgument String id,
            @InputArgument PatchIncidentInputGql input) {

        PatchIncidentRequest request = new PatchIncidentRequest(input.status(), input.severity(),
                input.message(), input.details(), input.acknowledgedAt(), input.acknowledgedBy(),
                input.resolvedAt(), input.closedAt());
        return incidentService.patchIncident(Long.parseLong(id), request);
    }
}
