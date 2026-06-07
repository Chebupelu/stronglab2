package stronglab.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.crypto.password.PasswordEncoder;
import stronglab.dto.AuthResponse;
import stronglab.model.Athlete;
import stronglab.model.Trainer;
import stronglab.model.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stronglab.repository.AthlereRepository;
import stronglab.repository.TrainerRepository;
import stronglab.repository.UserRepository;

import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AthlereRepository athlereRepository;
    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String JWT_SECRET = "YourSuperSecretKeyForStrongLabApp2026"; // Секретный ключ (минимум 256 бит)
    private final long EXPIRATION_TIME = 86_400_000;

    @Transactional
    public User registerUser(User user){
        //проверка не занят ли email
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new RuntimeException("Пользователь с таким email уже существует");
        }
        //сохраняем основного пользователя
        User saveUser = userRepository.save(user);

        if("ATHLETE".equalsIgnoreCase(saveUser.getRole())){
            Athlete athlete = new Athlete();
            athlete.setUser(saveUser);
            athlereRepository.save(athlete);
        } else if ("TRAINER".equalsIgnoreCase(saveUser.getRole())) {
            Trainer trainer = new Trainer();
            trainer.setUser(saveUser);
            trainerRepository.save(trainer);
        }

        // Хэшируем пароль перед сохранением
        String encodedPassword = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(encodedPassword);

        return userRepository.save(user);
    }

    public Optional<AuthResponse> login(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPasswordHash()))
                .map(user -> {
                    // Если пароль подошел, генерируем JWT-токен
                    String token = JWT.create()
                            .withSubject(user.getEmail())
                            .withClaim("role", user.getRole())
                            .withIssuedAt(new Date())
                            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                            .sign(Algorithm.HMAC256(JWT_SECRET));

                    // Возвращаем объект со всеми данными и токеном
                    return new AuthResponse(user.getId(), user.getEmail(), user.getRole(), token);
                });
    }
}
