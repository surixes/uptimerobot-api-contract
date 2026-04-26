package edu.rutmiit.demo.uptimerobotrest.graphql.fetcher;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckRequest;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PagedResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PatchCheckRequest;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.CheckConnectionGql;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.CheckFilterGql;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.CreateCheckInputGql;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.PageInfoGql;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.PatchCheckInputGql;
import edu.rutmiit.demo.uptimerobotrest.graphql.types.UpdateCheckInputGql;
import edu.rutmiit.demo.uptimerobotrest.service.CheckService;

@DgsComponent
public class CheckDataFetcher {

    private final CheckService checkService;

    public CheckDataFetcher(CheckService checkService) {
        this.checkService = checkService;
    }
    
    @DgsQuery
    public CheckResponse check(@InputArgument String id) {
        return checkService.findById(Long.parseLong(id));
    }

    @DgsQuery
    public CheckConnectionGql checks(
            @InputArgument CheckFilterGql filter,
            @InputArgument Integer page,
            @InputArgument Integer size) {

        int pageNum = page != null ? page : 0;
        int sizeNum = size != null ? size : 20;

        Long checkId = filter != null && filter.checkId() != null ? Long.parseLong(filter.checkId()) : null;

        PagedResponse<CheckResponse> paged = checkService.findAll(
            checkId,
            filter != null ? filter.date() : null,
            filter != null ? filter.url() : null,
            filter != null ? filter.titleSearch() : null,
            pageNum,
            sizeNum
        );

        return new CheckConnectionGql(
                    paged.content(),
                    new PageInfoGql(paged.pageNumber(), paged.pageSize(), paged.totalPages(),paged.last()),
                    (int) paged.totalElements()
                );
    }
    
    @DgsMutation
    public CheckResponse createCheck(@InputArgument CreateCheckInputGql input) {
        CheckRequest request = new CheckRequest(input.name(), input.url(), input.method(),
                input.intervalSec(), input.timeoutMs(), input.enabled(), input.expectedStatusCode(),
                input.expectedResponseContains());
        return checkService.create(request);
    }

    @DgsMutation
    public CheckResponse updateCheck(@InputArgument String id, @InputArgument UpdateCheckInputGql input) {
        CheckRequest request = new CheckRequest(input.name(), input.url(), input.method(),
                input.intervalSec(), input.timeoutMs(), input.enabled(), input.expectedStatusCode(),
                input.expectedResponseContains());
        return checkService.update(Long.parseLong(id), request);
    }

    @DgsMutation
    public boolean deleteCheck(@InputArgument String id) {
        checkService.delete(Long.parseLong(id));
        return true;
    }

    @DgsMutation
    public CheckResponse patchCheck(@InputArgument String id, @InputArgument PatchCheckInputGql input) {
        PatchCheckRequest request = new PatchCheckRequest(input.name(), input.url(), input.method(),
                input.intervalSec(), input.timeoutMs(), input.enabled(), input.expectedStatusCode(),
                input.expectedResponseContains());
        return checkService.patchCheck(Long.parseLong(id), request);
    }

    @DgsMutation
    public CheckResponse runCheckNow(@InputArgument String id) {
        return checkService.runCheckNow(Long.parseLong(id));
    }

}
