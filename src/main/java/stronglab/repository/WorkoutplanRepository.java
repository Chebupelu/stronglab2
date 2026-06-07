package stronglab.repository;

import stronglab.model.Athlete;
import stronglab.model.Workoutplan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkoutplanRepository extends JpaRepository<Workoutplan, Long> {

    List<Workoutplan> findByAthleteId(Long athleteId);;

    List<Workoutplan> findByAthleteUserId(Long userId);

    List<Workoutplan> findByAthleteIdAndIsCompletedFalse(Integer athleteId);

    List<Workoutplan> findByAthleteIdAndWorkoutDate(Long athleteId, LocalDate workoutDate);

}
