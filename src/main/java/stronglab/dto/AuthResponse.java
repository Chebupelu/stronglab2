package stronglab.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AuthResponse {
    private Long id;
    private String email;
    private String role;
    private String token;

    public AuthResponse(Long id, String email, String role, String token) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.token = token;
    }

}