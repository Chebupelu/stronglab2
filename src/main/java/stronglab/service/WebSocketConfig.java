package stronglab.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WorkoutWebSocketHandler workoutWebSocketHandler;

    // Внедряем наш обработчик сообщений
    public WebSocketConfig(WorkoutWebSocketHandler workoutWebSocketHandler) {
        this.workoutWebSocketHandler = workoutWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(workoutWebSocketHandler, "/ws/workouts")
                .setAllowedOrigins("*"); // Разрешаем подключения со всех устройств (включая эмуляторы)
    }
}
