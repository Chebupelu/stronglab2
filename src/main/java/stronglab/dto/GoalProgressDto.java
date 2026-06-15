package stronglab.dto;

import lombok.Data;
import stronglab.model.Goal;

import java.time.LocalDate;
import java.util.Date;

@Data
public class GoalProgressDto {
    private Long id;
    private String exerciseType;
    private double targetWeight;
    private int targetReps;
    private String isStatus;
    private LocalDate deadline;
    private LocalDate createdAt;
    private double currentMaxWeight;
    private int currentMaxReps;
    private double progressPercent;
}