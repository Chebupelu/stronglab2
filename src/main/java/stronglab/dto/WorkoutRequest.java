package stronglab.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class WorkoutRequest {
    private Long athleteId;
    private String exerciseName;
    private LocalDate workoutDate;
    private Integer sets;
    private Integer reps;
    private Double weight;

}