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
    public User registerUser(User user, String additionalInfo) {
        // 1. Проверяем уникальность email
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        String encodedPassword = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(encodedPassword);

        User savedUser = userRepository.save(user);

        if ("ATHLETE".equalsIgnoreCase(savedUser.getRole())) {
            Athlete athlete = new Athlete();
            athlete.setUser(savedUser);
            athlete.setFitnessLevel(additionalInfo);
            athlereRepository.save(athlete);

        } else if ("TRAINER".equalsIgnoreCase(savedUser.getRole())) {
            Trainer trainer = new Trainer();
            trainer.setUser(savedUser);
            trainer.setSpecialization(additionalInfo);
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
                    
                    Long profileId = -1L;
                    if ("TRAINER".equalsIgnoreCase(user.getRole())) {
                        profileId = trainerRepository.findByUserId(user.getId())
                                .map(Trainer::getId)
                                .orElse(-1L);
                    } else if ("ATHLETE".equalsIgnoreCase(user.getRole())) {
                        profileId = athlereRepository.findByUserId(user.getId())
                                .map(Athlete::getId)
                                .orElse(-1L);
                    }

                    return new AuthResponse(user.getId(), user.getEmail(), user.getRole(), token, profileId);
                });
    }
}
