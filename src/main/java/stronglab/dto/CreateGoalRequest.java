package stronglab.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateGoalRequest {
    private String exerciseType;
    private Double targetWeight;
    private Integer targetReps;
    private LocalDate deadline;
}