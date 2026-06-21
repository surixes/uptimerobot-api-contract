package edu.rutmiit.demo.events;

/**
 * Константы для маршрутизации событий в RabbitMQ.
 *
 * Routing key в topic exchange работает как почтовый индекс:
 * - "check.created" — конкретное событие
 * - "check.*"      — все события проверок
 * - "#"            — все события вообще
 *
 * Вынесены в контракт, чтобы publisher и consumer использовали одни и те же строки.
 * Рассогласование routing key - частая ошибка, которую трудно отследить.
 */
public final class RoutingKeys {

    private RoutingKeys() {
        // утилитарный класс — экземпляры не создаём
    }

    // Имя общего topic exchange для доменных событий
    public static final String EXCHANGE = "uptimerobot.events";

    // Routing keys для событий алертов
    public static final String ALERT_RULE_CREATED = "alertrule.created";
    public static final String ALERT_RULE_UPDATED = "alertrule.updated";
    public static final String ALERT_RULE_DELETED = "alertrule.deleted";

    public static final String INCIDENT_OPENED = "incident.opened";
    public static final String INCIDENT_RESOLVED = "incident.resolved";
    public static final String INCIDENT_ACKNOWLEDGED = "incident.acknowledged";
    public static final String INCIDENT_CLOSED = "incident.closed";

    // Routing keys для событий чеков
    public static final String CHECK_CREATED = "check.created";
    public static final String CHECK_UPDATED = "check.updated";
    public static final String CHECK_DELETED = "check.deleted";
    public static final String CHECK_EXECUTED = "check.executed";

    public static final String SLA_CALCULATED = "sla.calculated";

    // Паттерны для подписки (wildcard)
    public static final String ALL_ALERT_RULE_EVENTS = "alertrule.*";
    public static final String ALL_INCIDENT_EVENTS = "incident.*";
    public static final String ALL_CHECK_EVENTS = "check.*";

    public static final String ALL_SLA_EVENTS = "sla.*";

    public static final String ALL_EVENTS = "#";
}
