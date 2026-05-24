package edu.rutmiit.demo.grpcenrichment.publisher;

import edu.rutmiit.demo.events.EventEnvelope;
import edu.rutmiit.demo.events.RoutingKeys;
import edu.rutmiit.demo.events.SlaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class SlaEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(SlaEventPublisher.class);
    private static final String SOURCE = "grpc-sla-enrichment-client";

    private final RabbitTemplate rabbitTemplate;

    public SlaEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishCalculated(SlaEvent.Calculated event) {
        try {
            EventEnvelope<SlaEvent> envelope = EventEnvelope.wrap(
                    event, SOURCE, RoutingKeys.SLA_CALCULATED);

            rabbitTemplate.convertAndSend(
                    RoutingKeys.EXCHANGE,
                    RoutingKeys.SLA_CALCULATED,
                    envelope);

            log.info("Событие отправлено: {} [checkId={}, uptime={}%, status={}, eventId={}]",
                    RoutingKeys.SLA_CALCULATED,
                    event.checkId(),
                    event.uptimePercent(),
                    event.availabilityStatus(),
                    envelope.metadata().eventId());
        } catch (Exception e) {
            log.error("Не удалось отправить событие {}: {}",
                    RoutingKeys.SLA_CALCULATED, e.getMessage(), e);
        }
    }
}
