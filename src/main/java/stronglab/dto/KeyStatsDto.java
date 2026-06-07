package stronglab.dto;

import lombok.Data;

@Data
public class KeyStatsDto {
    private int totalWorkouts;           // Всего тренировок
    private int completedWorkouts;       // Выполнено тренировок
    private int completionPercent;       // Процент выполнения
    private double totalTonnenage;       // Общий поднятый тоннаж (кг)
    private String bestExercise;         // Лучшее упражнение (название)
    private double bestWeight;           // Лучший вес в этом упражнении
    private int totalSets;               // Всего подходов
    private int totalReps;               // Всего повторений
}
