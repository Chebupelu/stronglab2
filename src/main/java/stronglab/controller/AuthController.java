package stronglab.controller;

import org.springframework.http.HttpStatus;
import stronglab.dto.AuthResponse;
import stronglab.dto.LoginRequest;
import stronglab.dto.RegisterRequest;
import stronglab.dto.UserDTO;
import stronglab.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stronglab.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request){
        try {
            User user = new User();
            user.setEmail(request.email);
            user.setPasswordHash(request.password);
            user.setRole(request.role);

            User savedUser = authService.registerUser(user);

            UserDTO response = new UserDTO(
                    savedUser.getId(),
                    savedUser.getEmail(),
                    savedUser.getRole(),
                    savedUser.getName()
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest.getEmail(), loginRequest.getPassword())
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Неверный email или пароль"));
    }
}
