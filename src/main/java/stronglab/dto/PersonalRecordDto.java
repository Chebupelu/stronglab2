package stronglab.dto;

import lombok.Data;

@Data
public class PersonalRecordDto {
    private String exerciseName;
    private double maxWeight;
    private int maxReps;
    private String achievedDate;
}