package stronglab.dto;

import lombok.Data;
import java.util.List;

@Data
public class StatisticsDto {
    private List<GoalProgressDto> goals;           // Список целей с прогрессом
    private KeyStatsDto keyStats;                  // Ключевая статистика
    private List<PersonalRecordDto> personalRecords; // Персональные рекорды

    private List<ExerciseProgressDto> exerciseProgress;
}
