package edu.rutmiit.demo.uptimerobotrest.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.InputArgument;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PagedResponse;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.IncidentConnectionGql;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.PageInfoGql;
import edu.rutmiit.demo.uptimerobotrest.service.IncidentService;

@DgsComponent
public class CheckIncidentsDataFetcher {

    private final IncidentService incidentService;

    public CheckIncidentsDataFetcher(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @DgsData(parentType = "Check", field = "incidents")
    public IncidentConnectionGql incidents(DgsDataFetchingEnvironment dfe,
            @InputArgument Integer page, @InputArgument Integer size) {

        CheckResponse check = dfe.getSource();

        int pageNum = page != null ? page : 0;
        int sizeNum = size != null ? size : 20;

        PagedResponse<IncidentResponse> paged = incidentService.findByCheckId(check.getId(),
                pageNum, sizeNum, null, null, null, null);

        return new IncidentConnectionGql(paged.content(), new PageInfoGql(paged.pageNumber(),
                paged.pageSize(), paged.totalPages(), paged.last()),
                Math.toIntExact(paged.totalElements()));
    }
}
