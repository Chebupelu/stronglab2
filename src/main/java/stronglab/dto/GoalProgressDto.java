package stronglab.dto;

import lombok.Data;
import stronglab.model.Goal;

import java.util.Date;

@Data
public class GoalProgressDto {
    private Long id;
    private String exerciseType;
    private double targetWeight;
    private int targetReps;
    private String isStatus;
    private Date deadline;
    private Date createdAt;
    private double currentMaxWeight;
    private int currentMaxReps;
    private double progressPercent;
}