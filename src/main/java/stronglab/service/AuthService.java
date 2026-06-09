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

    private final String JWT_SECRET = "YourSuperSecretKeyForStrongLabApp2026";
    private final long EXPIRATION_TIME = 86_400_000;

    @Transactional
    public User registerUser(User user, String specialization){
        // 1. Проверяем уникальность email
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        // 2. ХЭШИРУЕМ ПАРОЛЬ СРАЗУ (до сохранения в БД!)
        String encodedPassword = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(encodedPassword);

        // 3. Сохраняем пользователя (теперь у него есть сгенерированный ID)
        User savedUser = userRepository.save(user);

        // 4. Создаем дочерние сущности в зависимости от роли
        if("ATHLETE".equalsIgnoreCase(savedUser.getRole())){
            Athlete athlete = new Athlete();
            athlete.setUser(savedUser);
            athlereRepository.save(athlete);
        } else if ("TRAINER".equalsIgnoreCase(savedUser.getRole())) {
            Trainer trainer = new Trainer();
            trainer.setUser(savedUser);
            // Записываем специализацию, которую передали из контроллера
            trainer.setSpecialization(specialization);
            trainerRepository.save(trainer);
        }

        return savedUser;
    }

    public Optional<AuthResponse> login(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPasswordHash()))
                .map(user -> {
                    String token = JWT.create()
                            .withSubject(user.getEmail())
                            .withClaim("role", user.getRole())
                            .withIssuedAt(new Date())
                            .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                            .sign(Algorithm.HMAC256(JWT_SECRET));

                    return new AuthResponse(user.getId(), user.getEmail(), user.getRole(), token);
                });
    }
}
