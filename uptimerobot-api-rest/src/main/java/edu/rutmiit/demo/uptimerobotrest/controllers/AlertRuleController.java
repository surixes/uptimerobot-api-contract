package edu.rutmiit.demo.uptimerobotrest.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRuleRequest;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRuleResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRuleTypeEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.IncidentSeverityEnum;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PagedResponse;
import edu.rutmiit.demo.uptimerobotapicontract.dto.PatchAlertRuleRequest;
import edu.rutmiit.demo.uptimerobotapicontract.endpoints.AlertRuleApi;
import edu.rutmiit.demo.uptimerobotrest.assemblers.AlertRuleModelAssembler;
import edu.rutmiit.demo.uptimerobotrest.service.AlertRuleService;

@RestController
public class AlertRuleController implements AlertRuleApi {

        private final AlertRuleService alertRuleService;
        private final AlertRuleModelAssembler alertRuleModelAssembler;
        private final PagedResourcesAssembler<AlertRuleResponse> pagedAlertRuleAssembler;

        public AlertRuleController(AlertRuleService alertRuleService,
                        AlertRuleModelAssembler alertRuleModelAssembler,
                        PagedResourcesAssembler<AlertRuleResponse> pagedAlertRuleAssembler) {
                this.alertRuleService = alertRuleService;
                this.alertRuleModelAssembler = alertRuleModelAssembler;
                this.pagedAlertRuleAssembler = pagedAlertRuleAssembler;
        }

        @Override
        public PagedModel<EntityModel<AlertRuleResponse>> getAllAlertRules(Long alertRuleId,
                        Long checkId, AlertRuleTypeEnum ruleType, IncidentSeverityEnum severity,
                        Boolean enabled, String url, int page, int size) {

                PagedResponse<AlertRuleResponse> paged = alertRuleService.findAll(alertRuleId,
                                checkId, ruleType, severity, enabled, url, page, size);

                Page<AlertRuleResponse> springPage = new PageImpl<>(paged.content(),
                                PageRequest.of(paged.pageNumber(), paged.pageSize()),
                                paged.totalElements());

                return pagedAlertRuleAssembler.toModel(springPage, alertRuleModelAssembler);
        }

        @Override
        public EntityModel<AlertRuleResponse> getAlertRuleById(Long id) {
                return alertRuleModelAssembler.toModel(alertRuleService.findById(id));
        }

        @Override
        public ResponseEntity<EntityModel<AlertRuleResponse>> createAlertRule(
                        AlertRuleRequest request) {
                AlertRuleResponse rule = alertRuleService.create(request);
                EntityModel<AlertRuleResponse> model = alertRuleModelAssembler.toModel(rule);
                return ResponseEntity.created(model.getRequiredLink("self").toUri()).body(model);
        }

        @Override
        public EntityModel<AlertRuleResponse> updateAlertRule(Long id, AlertRuleRequest request) {
                AlertRuleResponse rule = alertRuleService.update(id, request);
                return alertRuleModelAssembler.toModel(rule);
        }

        @Override
        public EntityModel<AlertRuleResponse> patchAlertRule(Long id,
                        PatchAlertRuleRequest request) {
                return alertRuleModelAssembler
                                .toModel(alertRuleService.patchAlertRule(id, request));
        }

        @Override
        public void deleteAlertRule(Long id) {
                alertRuleService.delete(id);
        }
}
