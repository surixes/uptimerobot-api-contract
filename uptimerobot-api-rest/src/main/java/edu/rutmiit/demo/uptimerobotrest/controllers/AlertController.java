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
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRequest;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertStatusEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PagedResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PatchAlertRequest;
import edu.rutmiit.demo.uptimerobotapicontract.endpoints.AlertApi;
import edu.rutmiit.demo.uptimerobotrest.assemblers.AlertModelAssembler;
import edu.rutmiit.demo.uptimerobotrest.service.AlertService;

@RestController
public class AlertController implements AlertApi {

    private final AlertService alertService;
    private final AlertModelAssembler alertModelAssembler;
    private final PagedResourcesAssembler<AlertResponse> pagedAlertAssembler;

    public AlertController(AlertService alertService, AlertModelAssembler alertModelAssembler, 
            PagedResourcesAssembler<AlertResponse> pagedAlertAssembler) {
        this.alertService = alertService;
        this.alertModelAssembler = alertModelAssembler;
        this.pagedAlertAssembler = pagedAlertAssembler;
    }

    @Override
    public PagedModel<EntityModel<AlertResponse>> getAllAlerts(
            Long alertId,
            LocalDateTime dateOpen,
            LocalDateTime dateClose,
            AlertStatusEnum status,
            String url,
            int page,
            int size) {

        PagedResponse<AlertResponse> paged = alertService.findAll(alertId, dateOpen, dateClose, status, url, page, size);
        Page<AlertResponse> springPage = new PageImpl<>(
                paged.content(),
                PageRequest.of(paged.pageNumber(), paged.pageSize()),
                paged.totalElements()
        );
        return pagedAlertAssembler.toModel(springPage, alertModelAssembler);
    }

    @Override
    public EntityModel<AlertResponse> getAlertById(Long id) {
        return alertModelAssembler.toModel(alertService.findById(id));
    }

    @Override
    public void acknowledgeAlert(Long id) {
        alertService.changeAlertStatus(id, AlertStatusEnum.ACKNOWLEDGED);
    }

    @Override
    public void resolveAlert(Long id) {
        alertService.changeAlertStatus(id, AlertStatusEnum.RESOLVED);
    }

    @Override
    public void closeAlert(Long id) {
        alertService.changeAlertStatus(id, AlertStatusEnum.CLOSED);
    }

    @Override
    public void deleteAlert(Long id) {
        alertService.delete(id);
    }

    @Override
    public ResponseEntity<EntityModel<AlertResponse>> createAlert(AlertRequest request) {
        AlertResponse alert = alertService.create(request);
        EntityModel<AlertResponse> model = alertModelAssembler.toModel(alert);
        return ResponseEntity.created(model.getRequiredLink("self").toUri()).body(model);
    }

    @Override
    public EntityModel<AlertResponse> updateAlert(Long id, AlertRequest request) {
        AlertResponse alert = alertService.update(id, request);
        return alertModelAssembler.toModel(alert);
    }

    @Override
    public EntityModel<AlertResponse> patchAlert(Long id, PatchAlertRequest request) {
        return alertModelAssembler.toModel(alertService.patchAlert(id, request));
    }
    
}
