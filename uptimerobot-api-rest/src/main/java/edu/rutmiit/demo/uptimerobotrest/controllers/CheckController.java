package edu.rutmiit.demo.uptimerobotrest.controllers;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckRequest;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PagedResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PatchCheckRequest;
import edu.rutmiit.demo.uptimerobotapicontract.endpoints.CheckApi;
import edu.rutmiit.demo.uptimerobotrest.assemblers.AlertModelAssembler;
import edu.rutmiit.demo.uptimerobotrest.assemblers.CheckModelAssembler;
import edu.rutmiit.demo.uptimerobotrest.service.CheckService;

@RestController
public class CheckController implements CheckApi {

    private final CheckService checkService;
    private final CheckModelAssembler checkModelAssembler;
    private final AlertModelAssembler alertModelAssembler;
    private final PagedResourcesAssembler<CheckResponse> pagedCheckAssembler;
    private final PagedResourcesAssembler<AlertResponse> pagedAlertAssembler;

    public CheckController(CheckService checkService, 
            PagedResourcesAssembler<CheckResponse> pagedCheckAssembler, 
            CheckModelAssembler checkModelAssembler, 
            PagedResourcesAssembler<AlertResponse> pagedAlertAssembler, 
            AlertModelAssembler alertModelAssembler) {
        this.checkService = checkService;
        this.pagedCheckAssembler = pagedCheckAssembler;
        this.checkModelAssembler = checkModelAssembler;
        this.pagedAlertAssembler = pagedAlertAssembler;
        this.alertModelAssembler = alertModelAssembler;
    }
    
    @Override
    public PagedModel<EntityModel<CheckResponse>> getAllChecks(int page, int size, Long checkId, LocalDateTime date, 
            String url, String searchTitle) {
        PagedResponse<CheckResponse> paged =
                checkService.findAll(checkId, date, searchTitle, url, page, size);
        Page<CheckResponse> springPage = new PageImpl<>(paged.content(),
                PageRequest.of(paged.pageNumber(), paged.pageSize()), paged.totalElements());
        return pagedCheckAssembler.toModel(springPage, checkModelAssembler);
    }
    
    @Override
    public EntityModel<CheckResponse> getCheckById(Long id) {
        return checkModelAssembler.toModel(checkService.findById(id));
    }

    @Override
    public PagedModel<EntityModel<AlertResponse>> getAlertsByCheckId(Long id, int page,
            int size, Long alertId, LocalDateTime date, String titleSearch) {
        checkService.findById(id);
        PagedResponse<AlertResponse> paged =
                checkService.findByCheckId(id, page, size, alertId, date, titleSearch);
        Page<AlertResponse> springPage = new PageImpl<>(paged.content(),
                PageRequest.of(paged.pageNumber(), paged.pageSize()), paged.totalElements());

        return pagedAlertAssembler.toModel(springPage, alertModelAssembler);
    }

    @Override
    public ResponseEntity<EntityModel<CheckResponse>> createCheck(CheckRequest request) {
        CheckResponse check = checkService.create(request);
        EntityModel<CheckResponse> model = checkModelAssembler.toModel(check);
        return ResponseEntity.created(model.getRequiredLink("self").toUri()).body(model);
    }

    @Override
    public EntityModel<CheckResponse> updateCheck(Long id, CheckRequest request) {
        CheckResponse check = checkService.update(id, request);
        return checkModelAssembler.toModel(check);
    }

    @Override
    public EntityModel<CheckResponse> patchCheck(Long id, PatchCheckRequest request) {
        CheckResponse check = checkService.patchCheck(id, request);
        return checkModelAssembler.toModel(check);
    }

    @Override
    public void deleteCheck(Long id) {
        checkService.delete(id);
    }

    @Override
    public EntityModel<CheckResponse> runCheckNow(Long id) {
        CheckResponse check = checkService.runCheckNow(id);
        return checkModelAssembler.toModel(check);
    }
}
