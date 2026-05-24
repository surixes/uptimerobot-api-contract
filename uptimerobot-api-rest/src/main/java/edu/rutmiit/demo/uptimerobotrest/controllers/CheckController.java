package edu.rutmiit.demo.uptimerobotrest.controllers;

import java.time.OffsetDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRuleResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckRequest;
import edu.rutmiit.demo.uptimerobotapicontract.dto.CheckResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PagedResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PatchCheckRequest;
import edu.rutmiit.demo.uptimerobotapicontract.endpoints.CheckApi;
import edu.rutmiit.demo.uptimerobotrest.assemblers.AlertRuleModelAssembler;
import edu.rutmiit.demo.uptimerobotrest.assemblers.CheckModelAssembler;
import edu.rutmiit.demo.uptimerobotrest.assemblers.IncidentModelAssembler;
import edu.rutmiit.demo.uptimerobotrest.service.CheckService;

@RestController
public class CheckController implements CheckApi {

    private final CheckService checkService;
    private final CheckModelAssembler checkModelAssembler;
    private final AlertRuleModelAssembler alertRuleModelAssembler;
    private final IncidentModelAssembler incidentModelAssembler;
    private final PagedResourcesAssembler<CheckResponse> pagedCheckAssembler;
    private final PagedResourcesAssembler<AlertRuleResponse> pagedAlertRuleAssembler;
    private final PagedResourcesAssembler<IncidentResponse> pagedIncidentAssembler;

    public CheckController(CheckService checkService,
            PagedResourcesAssembler<CheckResponse> pagedCheckAssembler,
            CheckModelAssembler checkModelAssembler,
            PagedResourcesAssembler<AlertRuleResponse> pagedAlertRuleAssembler,
            AlertRuleModelAssembler alertRuleModelAssembler,
            PagedResourcesAssembler<IncidentResponse> pagedIncidentAssembler,
            IncidentModelAssembler incidentModelAssembler) {
        this.checkService = checkService;
        this.pagedCheckAssembler = pagedCheckAssembler;
        this.checkModelAssembler = checkModelAssembler;
        this.pagedAlertRuleAssembler = pagedAlertRuleAssembler;
        this.alertRuleModelAssembler = alertRuleModelAssembler;
        this.pagedIncidentAssembler = pagedIncidentAssembler;
        this.incidentModelAssembler = incidentModelAssembler;
    }

    @Override
    public PagedModel<EntityModel<CheckResponse>> getAllChecks(int page, int size, Long checkId, String name, String url, String method, Boolean enabled) {

        PagedResponse<CheckResponse> paged = checkService.findAll(checkId, name, url, method, enabled, page, size);

        Page<CheckResponse> springPage = new PageImpl<>(paged.content(),
                PageRequest.of(paged.pageNumber(), paged.pageSize()), paged.totalElements());

        return pagedCheckAssembler.toModel(springPage, checkModelAssembler);
    }

    @Override
    public EntityModel<CheckResponse> getCheckById(Long id) {
        return checkModelAssembler.toModel(checkService.findById(id));
    }

    @Override
    public PagedModel<EntityModel<AlertRuleResponse>> getAlertRulesByCheckId(Long id, int page,
            int size, Long alertRuleId, OffsetDateTime date, String titleSearch, String url) {

        checkService.findById(id);

        PagedResponse<AlertRuleResponse> paged = checkService.findAlertRulesByCheckId(id, page,
                size, alertRuleId, date, titleSearch, url);

        Page<AlertRuleResponse> springPage = new PageImpl<>(paged.content(),
                PageRequest.of(paged.pageNumber(), paged.pageSize()), paged.totalElements());

        return pagedAlertRuleAssembler.toModel(springPage, alertRuleModelAssembler);
    }

    @Override
    public PagedModel<EntityModel<IncidentResponse>> getIncidentsByCheckId(Long id, int page,
            int size, Long incidentId, OffsetDateTime date, String titleSearch, String url) {

        checkService.findById(id);

        PagedResponse<IncidentResponse> paged = checkService.findIncidentsByCheckId(id, page, size,
                incidentId, date, titleSearch, url);

        Page<IncidentResponse> springPage = new PageImpl<>(paged.content(),
                PageRequest.of(paged.pageNumber(), paged.pageSize()), paged.totalElements());

        return pagedIncidentAssembler.toModel(springPage, incidentModelAssembler);
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
