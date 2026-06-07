package stronglab.dto;

import lombok.Data;
import java.util.List;

@Data
public class StatisticsDto {
    private List<GoalProgressDto> goals;
    private KeyStatsDto keyStats;
    private List<PersonalRecordDto> personalRecords;

    private List<ExerciseProgressDto> exerciseProgress;
}
