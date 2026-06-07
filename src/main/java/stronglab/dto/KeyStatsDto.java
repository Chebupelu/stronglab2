package stronglab.dto;

import lombok.Data;

@Data
public class KeyStatsDto {
    private int totalWorkouts;
    private int completedWorkouts;
    private int completionPercent;
    private double totalTonnenage;
    private String bestExercise;
    private double bestWeight;
    private int totalSets;
    private int totalReps;
}
