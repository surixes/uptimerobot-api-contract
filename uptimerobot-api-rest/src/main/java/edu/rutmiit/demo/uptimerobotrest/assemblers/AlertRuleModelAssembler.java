package edu.rutmiit.demo.uptimerobotrest.assemblers;

import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertRuleResponse;
import edu.rutmiit.demo.uptimerobotrest.controllers.AlertRuleController;
import edu.rutmiit.demo.uptimerobotrest.controllers.CheckController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AlertRuleModelAssembler implements
                RepresentationModelAssembler<AlertRuleResponse, EntityModel<AlertRuleResponse>> {

        @Override
        public EntityModel<AlertRuleResponse> toModel(AlertRuleResponse alert) {
                EntityModel<AlertRuleResponse> model = EntityModel.of(alert,
                                linkTo(methodOn(AlertRuleController.class)
                                                .getAlertRuleById(alert.getId())).withSelfRel(),
                                linkTo(methodOn(AlertRuleController.class).getAllAlertRules(null, null,
                                                null, null, null, null, 0, 20))
                                                                .withRel("collection"),
                                linkTo(methodOn(AlertRuleController.class)
                                                .updateAlertRule(alert.getId(), null))
                                                                .withRel("update"),
                                linkTo(AlertRuleController.class).slash(alert.getId())
                                                .withRel("delete"),
                                linkTo(AlertRuleController.class).slash(alert.getId())
                                                .slash("acknowledge").withRel("acknowledge"),
                                linkTo(AlertRuleController.class).slash(alert.getId())
                                                .slash("resolve").withRel("resolve"),
                                linkTo(AlertRuleController.class).slash(alert.getId())
                                                .slash("close").withRel("close"));

                if (alert.getCheck() != null) {
                        model.add(linkTo(methodOn(CheckController.class)
                                        .getCheckById(alert.getCheck().getId())).withRel("check"));
                }

                return model;
        }
}
