package stronglab.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stronglab.dto.GoalProgressDto;
import stronglab.dto.KeyStatsDto;
import stronglab.dto.PersonalRecordDto;
import stronglab.dto.StatisticsDto;
import stronglab.model.Goal;
import stronglab.model.Workoutplan;
import stronglab.repository.GoalRepository;
import stronglab.repository.WorkoutplanRepository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    @Autowired
    private WorkoutplanRepository workoutRepository;

    @Autowired
    private GoalRepository goalRepository;

    public StatisticsDto getStatistics(Long athleteId) {
        StatisticsDto dto = new StatisticsDto();

        List<Workoutplan> workouts = workoutRepository.findByAthleteId(athleteId);

        dto.setKeyStats(calculateKeyStats(workouts));

        dto.setPersonalRecords(getPersonalRecords(workouts));

        List<Goal> goals = goalRepository.findByAthleteId(athleteId);
        dto.setGoals(getGoalsWithProgress(goals, workouts));

        return dto;
    }

    private KeyStatsDto calculateKeyStats(List<Workoutplan> workouts) {
        KeyStatsDto stats = new KeyStatsDto();

        stats.setTotalWorkouts(workouts.size());

        long completedCount = workouts.stream()
                .filter(w -> Boolean.TRUE.equals(w.getIsCompleted()))
                .count();
        stats.setCompletedWorkouts((int) completedCount);

        stats.setCompletionPercent(stats.getTotalWorkouts() > 0 ?
                (int) (completedCount * 100 / stats.getTotalWorkouts()) : 0);

        double totalTonnenage = workouts.stream()
                .mapToDouble(w -> {
                    Double load = w.getCalculatedLoad();
                    return load != null ? load : 0.0;
                })
                .sum();
        stats.setTotalTonnenage(totalTonnenage);

        long totalSetsLong = workouts.stream()
                .mapToLong(w -> w.getSets())
                .sum();
        stats.setTotalSets((int) totalSetsLong);

        long totalRepsLong = workouts.stream()
                .mapToLong(w -> w.getReps())
                .sum();
        stats.setTotalReps((int) totalRepsLong);

        workouts.stream()
                .filter(w -> w.getExerciseName() != null && w.getExerciseName() != null && w.getWeight() != null)
                .max(Comparator.comparing(Workoutplan::getWeight))
                .ifPresent(best -> {
                    stats.setBestExercise(best.getExerciseName());
                    stats.setBestWeight(best.getWeight());
                });

        return stats;
    }

    private List<PersonalRecordDto> getPersonalRecords(List<Workoutplan> workouts) {
        Map<String, Workoutplan> bestByExercise = new HashMap<>();

        for (Workoutplan workout : workouts) {
            String exercise = workout.getExerciseName();
            if (exercise == null || exercise.isEmpty()) continue;

            Workoutplan currentBest = bestByExercise.get(exercise);
            Double currentWeight = workout.getWeight();

            if (currentBest == null) {
                bestByExercise.put(exercise, workout);
            } else if (currentWeight != null &&
                    currentWeight > (currentBest.getWeight() != null ? currentBest.getWeight() : 0.0)) {
                bestByExercise.put(exercise, workout);
            }
        }

        return bestByExercise.entrySet().stream()
                .map(entry -> {
                    PersonalRecordDto dto = new PersonalRecordDto();
                    Workoutplan best = entry.getValue();

                    dto.setExerciseName(entry.getKey());
                    dto.setMaxWeight(best.getWeight() != null ? best.getWeight() : 0.0);
                    dto.setMaxReps((int) best.getReps());
                    dto.setAchievedDate(best.getWorkoutDate() != null ?
                            best.getWorkoutDate().toString() : null);
                    return dto;
                })
                .sorted(Comparator.comparing(PersonalRecordDto::getMaxWeight).reversed())
                .collect(Collectors.toList());
    }

    private List<GoalProgressDto> getGoalsWithProgress(List<Goal> goals, List<Workoutplan> workouts) {
        Map<String, Double> maxWeightByExercise = new HashMap<>();

        for (Workoutplan workout : workouts) {
            String exercise = workout.getExerciseName();
            Double weight = workout.getWeight();

            if (exercise == null || exercise.isEmpty()) continue;
            if (weight == null) continue;

            Double currentMax = maxWeightByExercise.get(exercise);
            if (currentMax == null || weight > currentMax) {
                maxWeightByExercise.put(exercise, weight);
            }
        }

        return goals.stream()
                .map(goal -> {
                    GoalProgressDto dto = new GoalProgressDto();
                    dto.setId(goal.getId());
                    dto.setExerciseType(goal.getExerciseType());
                    dto.setTargetWeight(goal.getTargetWeight());
                    dto.setIsStatus(goal.getIsStatus());
                    dto.setDeadline(goal.getDeadline());

                    double currentMax = maxWeightByExercise.getOrDefault(goal.getExerciseType(), 0.0);
                    dto.setCurrentMaxWeight(currentMax);

                    double progress = (currentMax / goal.getTargetWeight()) * 100;
                    dto.setProgressPercent(Math.min(progress, 100.0));

                    return dto;
                })
                .collect(Collectors.toList());
    }
}