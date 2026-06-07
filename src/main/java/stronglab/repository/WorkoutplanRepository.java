package stronglab.repository;

import stronglab.model.Athlete;
import stronglab.model.Workoutplan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkoutplanRepository extends JpaRepository<Workoutplan, Long> {

    //найти все планы тренировок конткретного атлета
    List<Workoutplan> findByAthleteId(Long athleteId);;

    List<Workoutplan> findByAthleteUserId(Long userId);

    List<Workoutplan> findByAthleteIdAndIsCompletedFalse(Integer athleteId);

    //найти план по дате
    List<Workoutplan> findByAthleteIdAndWorkoutDate(Long athleteId, LocalDate workoutDate);

}
