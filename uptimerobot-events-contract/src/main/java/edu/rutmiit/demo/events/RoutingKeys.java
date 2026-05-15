package edu.rutmiit.demo.events;

/**
 * Константы для маршрутизации событий в RabbitMQ.
 *
 * Routing key в topic exchange работает как почтовый индекс:
 * - "book.created" — конкретное событие
 * - "book.*"       — все события книг
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
    public static final String ALERT_OPENED = "alert.opened";
    public static final String ALERT_RESOLVED = "alert.resolved";
    public static final String ALERT_ACKNOWLEDGED = "alert.acknowledged";
    public static final String ALERT_CLOSED = "alert.closed";

    // Routing keys для событий чеков
    public static final String CHECK_CREATED = "check.created";
    public static final String CHECK_UPDATED = "check.updated";
    public static final String CHECK_DELETED = "check.deleted";
    public static final String CHECK_EXECUTED = "check.executed";

    // Паттерны для подписки (wildcard)
    public static final String ALL_ALERTS_EVENTS = "alert.*";
    public static final String ALL_CHECK_EVENTS = "check.*";
    public static final String ALL_EVENTS = "#";
}
