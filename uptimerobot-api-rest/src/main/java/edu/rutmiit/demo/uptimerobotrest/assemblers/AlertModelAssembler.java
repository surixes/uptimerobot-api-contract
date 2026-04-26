package edu.rutmiit.demo.uptimerobotrest.assemblers;

import edu.rutmiit.demo.uptimerobotapicontract.dto.AlertResponse;
import edu.rutmiit.demo.uptimerobotrest.controllers.AlertController;
import edu.rutmiit.demo.uptimerobotrest.controllers.CheckController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AlertModelAssembler implements RepresentationModelAssembler<AlertResponse, EntityModel<AlertResponse>> {

    @Override
    public EntityModel<AlertResponse> toModel(AlertResponse alert) {
        EntityModel<AlertResponse> model = EntityModel.of(alert,
                linkTo(methodOn(AlertController.class).getAlertById(alert.getId())).withSelfRel(),
                linkTo(methodOn(AlertController.class).getAllAlerts(null, null, null, null, null, 0,
                        20)).withRel("collection"),
                linkTo(methodOn(AlertController.class).updateAlert(alert.getId(), null))
                        .withRel("update"),
                linkTo(AlertController.class).slash(alert.getId()).withRel("delete"),
                linkTo(AlertController.class).slash(alert.getId()).slash("acknowledge")
                        .withRel("acknowledge"),
                linkTo(AlertController.class).slash(alert.getId()).slash("resolve")
                        .withRel("resolve"),
                linkTo(AlertController.class).slash(alert.getId()).slash("close").withRel("close"));

        if (alert.getCheck() != null) {
            model.add(
                    linkTo(methodOn(CheckController.class).getCheckById(alert.getCheck().getId()))
                            .withRel("check"),
                    linkTo(methodOn(CheckController.class)
                            .getAlertsByCheckId(alert.getCheck().getId(), 0, 20, null, null, null, null))
                                    .withRel("check-alerts"));
        }

        return model;
    }
}
