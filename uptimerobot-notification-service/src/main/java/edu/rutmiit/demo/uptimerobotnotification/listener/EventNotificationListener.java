package edu.rutmiit.demo.uptimerobotnotification.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.rutmiit.demo.events.AlertRuleEvent;
import edu.rutmiit.demo.events.CheckEvent;
import edu.rutmiit.demo.events.EventMetadata;
import edu.rutmiit.demo.events.IncidentEvent;
import edu.rutmiit.demo.events.RoutingKeys;
import edu.rutmiit.demo.events.SlaEvent;
import edu.rutmiit.demo.uptimerobotnotification.config.RabbitMQConfig;
import edu.rutmiit.demo.uptimerobotnotification.websocket.NotificationWebSocketHandler;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EventNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(EventNotificationListener.class);

    private final NotificationWebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;
    private final Set<String> processedEventIds = ConcurrentHashMap.newKeySet();

    public EventNotificationListener(NotificationWebSocketHandler webSocketHandler,
            ObjectMapper objectMapper) {
        this.webSocketHandler = webSocketHandler;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATIONS_QUEUE)
    public void handleEvent(Message message) {
        try {
            JsonNode root = objectMapper.readTree(message.getBody());
            EventMetadata metadata = objectMapper.treeToValue(root.get("metadata"), EventMetadata.class);

            if (!processedEventIds.add(metadata.eventId())) {
                log.warn("Duplicate notification skipped: eventId={}", metadata.eventId());
                return;
            }

            JsonNode payload = root.get("payload");
            String title = buildTitle(metadata.eventType());
            String description = buildDescription(metadata.eventType(), payload);
            String icon = resolveIcon(metadata.eventType());
            String level = resolveLevel(metadata.eventType(), payload);
            String notificationJson = objectMapper.writeValueAsString(new NotificationPayload(
                    "NOTIFICATION",
                    metadata.eventId(),
                    metadata.eventType(),
                    title,
                    description,
                    icon,
                    level,
                    metadata.source(),
                    metadata.timestamp().toString(),
                    Instant.now().toString()
            ));

            webSocketHandler.broadcast(notificationJson);
            log.info("[NOTIFY] {} | {} (clients: {})",
                    metadata.eventType(), description, webSocketHandler.getActiveConnectionCount());
        } catch (Exception e) {
            log.error("Failed to process notification event: {}", e.getMessage(), e);
            throw new RuntimeException("Could not process notification event", e);
        }
    }

    private String buildTitle(String eventType) {
        return switch (eventType) {
            case RoutingKeys.CHECK_CREATED -> "Новая проверка";
            case RoutingKeys.CHECK_UPDATED -> "Проверка обновлена";
            case RoutingKeys.CHECK_DELETED -> "Проверка удалена";
            case RoutingKeys.CHECK_EXECUTED -> "Проверка выполнена";
            case RoutingKeys.ALERT_RULE_CREATED -> "Новое правило оповещения";
            case RoutingKeys.ALERT_RULE_UPDATED -> "Правило оповещения обновлено";
            case RoutingKeys.ALERT_RULE_DELETED -> "Правило оповещения удалено";
            case RoutingKeys.INCIDENT_OPENED -> "Инцидент открыт";
            case RoutingKeys.INCIDENT_ACKNOWLEDGED -> "Инцидент подтверждён";
            case RoutingKeys.INCIDENT_RESOLVED -> "Инцидент решён";
            case RoutingKeys.INCIDENT_CLOSED -> "Инцидент закрыт";
            case RoutingKeys.SLA_CALCULATED -> "SLA рассчитан";
            default -> "Событие: " + eventType;
        };
    }

    private String buildDescription(String eventType, JsonNode payload) {
        try {
            return switch (eventType) {
                case RoutingKeys.CHECK_CREATED -> {
                    CheckEvent.Created e = objectMapper.treeToValue(payload, CheckEvent.Created.class);
                    yield "Создана проверка «%s» для %s (%s, timeout %s ms)".formatted(
                            e.name(), e.url(), e.method(), value(e.timeoutMs()));
                }
                case RoutingKeys.CHECK_UPDATED -> {
                    CheckEvent.Updated e = objectMapper.treeToValue(payload, CheckEvent.Updated.class);
                    yield "Обновлена проверка id=%d «%s», интервал %s сек, активна: %s".formatted(
                            e.checkId(), e.name(), value(e.intervalSec()), e.enabled());
                }
                case RoutingKeys.CHECK_DELETED -> {
                    CheckEvent.Deleted e = objectMapper.treeToValue(payload, CheckEvent.Deleted.class);
                    yield "Удалена проверка id=%d, удалено правил: %d".formatted(
                            e.checkId(), e.deletedAlertRulesCount());
                }
                case RoutingKeys.CHECK_EXECUTED -> {
                    CheckEvent.Executed e = objectMapper.treeToValue(payload, CheckEvent.Executed.class);
                    String status = e.execution().success() ? "успешно" : "ошибка";
                    yield "Проверка «%s» выполнена: %s, код %s, время %s ms".formatted(
                            e.check().name(), status, value(e.execution().responseCode()),
                            value(e.execution().responseTimeMs()));
                }
                case RoutingKeys.ALERT_RULE_CREATED -> {
                    AlertRuleEvent.Created e = objectMapper.treeToValue(payload, AlertRuleEvent.Created.class);
                    yield "Создано правило «%s» для checkId=%d, тип %s, важность %s".formatted(
                            e.alertName(), e.checkId(), e.ruleType(), e.severity());
                }
                case RoutingKeys.ALERT_RULE_UPDATED -> {
                    AlertRuleEvent.Updated e = objectMapper.treeToValue(payload, AlertRuleEvent.Updated.class);
                    yield "Обновлено правило id=%d «%s», включено: %s".formatted(
                            e.alertRuleId(), e.alertName(), e.enabled());
                }
                case RoutingKeys.ALERT_RULE_DELETED -> {
                    AlertRuleEvent.Deleted e = objectMapper.treeToValue(payload, AlertRuleEvent.Deleted.class);
                    yield "Удалено правило id=%d для checkId=%d".formatted(e.alertRuleId(), e.checkId());
                }
                case RoutingKeys.INCIDENT_OPENED -> {
                    IncidentEvent.Opened e = objectMapper.treeToValue(payload, IncidentEvent.Opened.class);
                    yield "Открыт инцидент id=%d для checkId=%d: %s".formatted(
                            e.incidentId(), e.checkId(), e.message());
                }
                case RoutingKeys.INCIDENT_ACKNOWLEDGED -> {
                    IncidentEvent.Acknowledged e = objectMapper.treeToValue(payload, IncidentEvent.Acknowledged.class);
                    yield "Инцидент id=%d подтверждён пользователем %s".formatted(
                            e.incidentId(), e.acknowledgedBy());
                }
                case RoutingKeys.INCIDENT_RESOLVED -> {
                    IncidentEvent.Resolved e = objectMapper.treeToValue(payload, IncidentEvent.Resolved.class);
                    yield "Инцидент id=%d помечен как решённый".formatted(e.incidentId());
                }
                case RoutingKeys.INCIDENT_CLOSED -> {
                    IncidentEvent.Closed e = objectMapper.treeToValue(payload, IncidentEvent.Closed.class);
                    yield "Инцидент id=%d закрыт".formatted(e.incidentId());
                }
                case RoutingKeys.SLA_CALCULATED -> {
                    SlaEvent.Calculated e = objectMapper.treeToValue(payload, SlaEvent.Calculated.class);
                    yield "SLA для «%s»: %.2f%%, статус %s, успешно %d/%d".formatted(
                            e.checkName(), e.uptimePercent(), e.availabilityStatus(),
                            e.successCount(), e.totalExecutions());
                }
                default -> "Получено событие " + eventType;
            };
        } catch (Exception e) {
            return "Получено событие " + eventType + " (payload не распознан)";
        }
    }

    private String resolveIcon(String eventType) {
        return switch (eventType) {
            case RoutingKeys.CHECK_CREATED -> "check-plus";
            case RoutingKeys.CHECK_UPDATED -> "check-edit";
            case RoutingKeys.CHECK_DELETED -> "check-remove";
            case RoutingKeys.CHECK_EXECUTED -> "check-run";
            case RoutingKeys.ALERT_RULE_CREATED, RoutingKeys.ALERT_RULE_UPDATED -> "rule";
            case RoutingKeys.ALERT_RULE_DELETED -> "rule-remove";
            case RoutingKeys.INCIDENT_OPENED -> "incident";
            case RoutingKeys.INCIDENT_ACKNOWLEDGED -> "incident-ack";
            case RoutingKeys.INCIDENT_RESOLVED, RoutingKeys.INCIDENT_CLOSED -> "incident-ok";
            case RoutingKeys.SLA_CALCULATED -> "sla";
            default -> "bell";
        };
    }

    private String resolveLevel(String eventType, JsonNode payload) {
        if (RoutingKeys.CHECK_EXECUTED.equals(eventType)) {
            try {
                CheckEvent.Executed e = objectMapper.treeToValue(payload, CheckEvent.Executed.class);
                return e.execution().success() ? "success" : "danger";
            } catch (Exception e) {
                return "info";
            }
        }
        return switch (eventType) {
            case RoutingKeys.CHECK_DELETED, RoutingKeys.ALERT_RULE_DELETED -> "warning";
            case RoutingKeys.INCIDENT_OPENED -> "danger";
            case RoutingKeys.INCIDENT_ACKNOWLEDGED -> "warning";
            case RoutingKeys.SLA_CALCULATED -> "info";
            default -> "success";
        };
    }

    private String value(Object value) {
        return value == null ? "-" : value.toString();
    }

    record NotificationPayload(
            String type,
            String eventId,
            String eventType,
            String title,
            String description,
            String icon,
            String level,
            String source,
            String eventTimestamp,
            String receivedAt
    ) {}
}
