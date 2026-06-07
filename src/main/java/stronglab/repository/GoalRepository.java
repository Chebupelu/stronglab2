package stronglab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stronglab.model.Goal;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByAthleteId(Long athleteId);
}
