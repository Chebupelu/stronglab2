package stronglab.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {
    private Long id;
    private String email;
    private String token;

    public JwtResponse(Long id, String email, String token) {
        this.id = id;
        this.email = email;
        this.token = token;
    }


}