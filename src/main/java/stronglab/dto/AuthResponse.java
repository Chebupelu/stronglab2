package stronglab.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private Long userId;
    private String email;
    private String role;
    private String token;
    private Long profileId; // <-- ДОБАВИЛИ ПОЛЕ

    // Обнови конструктор
    public AuthResponse(Long userId, String email, String role, String token, Long profileId) {
        this.userId = userId;
        this.email = email;
        this.role = role;
        this.token = token;
        this.profileId = profileId;
    }

}