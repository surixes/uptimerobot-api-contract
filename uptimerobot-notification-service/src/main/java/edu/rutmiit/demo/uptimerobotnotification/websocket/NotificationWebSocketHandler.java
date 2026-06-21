package edu.rutmiit.demo.uptimerobotnotification.websocket;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(NotificationWebSocketHandler.class);

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        log.info("websocket connected: sessionId={} total={}", session.getId(), sessions.size());
        String welcome = "{\"type\":\"CONNECTED\",\"message\":\"Подключено к UptimeRobot Notification Service\",\"activeConnections\":"
                + sessions.size() + "}";
        session.sendMessage(new TextMessage(welcome));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        log.info("websocket closed: sessionId={} status={} total={}", session.getId(), status, sessions.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        if ("ping".equalsIgnoreCase(message.getPayload().trim())) {
            session.sendMessage(new TextMessage("{\"type\":\"PONG\"}"));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        sessions.remove(session);
        log.warn("websocket transport error: sessionId={} error={}", session.getId(), exception.getMessage());
    }

    public void broadcast(String json) {
        if (sessions.isEmpty()) {
            return;
        }

        TextMessage message = new TextMessage(json);

        for (WebSocketSession session : sessions) {
            if (!session.isOpen()) {
                sessions.remove(session);
                continue;
            }
            try {
                session.sendMessage(message);
            } catch (IOException e) {
                sessions.remove(session);
                log.warn("websocket send failed: sessionId={} error={}", session.getId(), e.getMessage());
            }
        }
    }

    public int getActiveConnectionCount() {
        return sessions.size();
    }
}
