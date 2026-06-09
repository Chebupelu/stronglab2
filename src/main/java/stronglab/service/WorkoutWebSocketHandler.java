package stronglab.service;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WorkoutWebSocketHandler extends TextWebSocketHandler {

    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long athleteId = getAthleteIdFromSession(session);
        if (athleteId != null) {
            sessions.put(athleteId, session);
            System.out.println("Атлет с ID " + athleteId + " подключился к WebSocket");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long athleteId = getAthleteIdFromSession(session);
        if (athleteId != null) {
            sessions.remove(athleteId);
            System.out.println("Атлет с ID " + athleteId + " отключился");
        }
    }

    public void sendWorkoutNotification(Long athleteId, String jsonPayload) {
        WebSocketSession session = sessions.get(athleteId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(jsonPayload));
                System.out.println("Уведомление о тренировке отправлено атлету: " + athleteId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Long getAthleteIdFromSession(WebSocketSession session) {
        try {
            String query = session.getUri().getQuery();
            if (query != null && query.contains("athleteId=")) {
                String idStr = query.split("athleteId=")[1].split("&")[0];
                return Long.parseLong(idStr);
            }
        } catch (Exception e) {
            System.err.println("Не удалось распарсить athleteId из URI сокета");
        }
        return null;
    }
}