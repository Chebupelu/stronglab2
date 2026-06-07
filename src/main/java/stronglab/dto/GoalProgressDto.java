package stronglab.dto;

import lombok.Data;
import stronglab.model.Goal;

import java.util.Date;

@Data
public class GoalProgressDto {
    private Long id;
    private String exerciseType;
    private double targetWeight;
    private String isStatus;
    private Date deadline;
    private double currentMaxWeight;
    private double progressPercent;


    private GoalProgressDto toGoalProgressDto(Goal goal, double currentMaxWeight) {
        GoalProgressDto dto = new GoalProgressDto();
        dto.setId(goal.getId());
        dto.setExerciseType(goal.getExerciseType());
        dto.setTargetWeight(goal.getTargetWeight());
        dto.setIsStatus(goal.getIsStatus());
        dto.setDeadline(goal.getDeadline());
        dto.setCurrentMaxWeight(currentMaxWeight);

        // Расчёт процента прогресса
        double progressPercent = (currentMaxWeight / goal.getTargetWeight()) * 100;
        dto.setProgressPercent(Math.min(progressPercent, 100.0)); // Не больше 100%

        return dto;
    }
}