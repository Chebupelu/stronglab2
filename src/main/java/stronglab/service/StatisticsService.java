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

        // Получаем все тренировки атлета
        List<Workoutplan> workouts = workoutRepository.findByAthleteId(athleteId);

        // 1. Ключевая статистика
        dto.setKeyStats(calculateKeyStats(workouts));

        // 2. Персональные рекорды
        dto.setPersonalRecords(getPersonalRecords(workouts));

        // 3. Цели с прогрессом
        List<Goal> goals = goalRepository.findByAthleteId(athleteId);
        dto.setGoals(getGoalsWithProgress(goals, workouts));

        return dto;
    }

    private KeyStatsDto calculateKeyStats(List<Workoutplan> workouts) {
        KeyStatsDto stats = new KeyStatsDto();

        // Общее количество тренировок
        stats.setTotalWorkouts(workouts.size());

        // Количество выполненных тренировок
        long completedCount = workouts.stream()
                .filter(w -> Boolean.TRUE.equals(w.getIsCompleted()))
                .count();
        stats.setCompletedWorkouts((int) completedCount);

        // Процент выполнения
        stats.setCompletionPercent(stats.getTotalWorkouts() > 0 ?
                (int) (completedCount * 100 / stats.getTotalWorkouts()) : 0);

        // 🔥 Исправлено: totalTonnenage (calculatedLoad может быть null, так как Double)
        double totalTonnenage = workouts.stream()
                .mapToDouble(w -> {
                    Double load = w.getCalculatedLoad();
                    return load != null ? load : 0.0;
                })
                .sum();
        stats.setTotalTonnenage(totalTonnenage);

        // 🔥 Исправлено: totalSets (sets — это int примитив, не может быть null)
        long totalSetsLong = workouts.stream()
                .mapToLong(w -> w.getSets())  // ← убрали проверку на null
                .sum();
        stats.setTotalSets((int) totalSetsLong);

        // 🔥 Исправлено: totalReps (reps — это int примитив, не может быть null)
        long totalRepsLong = workouts.stream()
                .mapToLong(w -> w.getReps())  // ← убрали проверку на null
                .sum();
        stats.setTotalReps((int) totalRepsLong);

        // Лучшее упражнение по весу (weight — это Double, может быть null)
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
                    // 🔥 Исправлено: reps — это int примитив, не может быть null
                    dto.setMaxReps((int) best.getReps());  // ← убрали проверку на null
                    dto.setAchievedDate(best.getWorkoutDate() != null ?
                            best.getWorkoutDate().toString() : null);
                    return dto;
                })
                .sorted(Comparator.comparing(PersonalRecordDto::getMaxWeight).reversed())
                .collect(Collectors.toList());
    }

    private List<GoalProgressDto> getGoalsWithProgress(List<Goal> goals, List<Workoutplan> workouts) {
        // Группируем тренировки по упражнениям и находим максимальный вес
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