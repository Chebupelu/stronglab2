package stronglab.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ExerciseProgressDto {
    private String exerciseName;
    private LocalDate date;
    private double weight;
    private int reps;
    private int sets;
}
