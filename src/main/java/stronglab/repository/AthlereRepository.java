package stronglab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stronglab.model.Athlete;

import java.util.List;
import java.util.Optional;

public interface AthlereRepository extends JpaRepository<Athlete, Long> {
    Optional<Athlete> findByUserId(Long userId);
     List<Athlete> findByTrainerId(Long trainerId);
}
