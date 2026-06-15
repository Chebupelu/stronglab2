package stronglab.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stronglab.dto.CreateGoalRequest;
import stronglab.dto.GoalProgressDto;
import stronglab.model.Athlete;
import stronglab.model.Goal;
import stronglab.model.Workoutplan;
import stronglab.repository.AthlereRepository;
import stronglab.repository.GoalRepository;
import stronglab.repository.WorkoutplanRepository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final WorkoutplanRepository workoutRepository;
    private final AthlereRepository athlereRepository;

    // Получить все цели атлета с рассчитанным прогрессом
    public List<GoalProgressDto> getAthleteGoals(Long athleteId) {
        List<Goal> goals = goalRepository.findByAthleteId(athleteId);
        Map<String, Workoutplan> bestResults = getBestResultsByExercise(athleteId);

        return goals.stream()
                .map(goal -> toGoalProgressDto(goal, bestResults.get(goal.getExerciseType())))
                .collect(Collectors.toList());
    }

    // Создать новую цель
    public Goal createGoal(Long athleteId, CreateGoalRequest request) {
        Athlete athlete = athlereRepository.findById(athleteId)
                .orElseThrow(() -> new RuntimeException("Атлет не найден"));

        Goal goal = new Goal();
        goal.setAthlete(athlete);
        goal.setExerciseType(request.getExerciseType());
        goal.setTargetWeight(request.getTargetWeight());
        goal.setTargetReps(request.getTargetReps());
        goal.setDeadline(java.sql.Date.valueOf(request.getDeadline()));
        goal.setIsStatus("ACTIVE");
        goal.setCreatedAt((java.sql.Date) new Date());

        return goalRepository.save(goal);
    }

    // Обновить прогресс всех целей (вызывать после сохранения тренировки)
    public void refreshProgress(Long athleteId) {
        List<Goal> goals = goalRepository.findByAthleteId(athleteId);
        Map<String, Workoutplan> bestResults = getBestResultsByExercise(athleteId);

        for (Goal goal : goals) {
            Workoutplan best = bestResults.get(goal.getExerciseType());
            if (best != null) {
                goal.setCurrentMaxWeight(best.getWeight());
                goal.setCurrentMaxReps((int) best.getReps());

                // Проверка достижения цели
                if (goal.getCurrentMaxWeight() >= goal.getTargetWeight() &&
                        goal.getCurrentMaxReps() >= goal.getTargetReps()) {
                    goal.setIsStatus("ACHIEVED");
                }
            }
            goalRepository.save(goal);
        }
    }

    private Map<String, Workoutplan> getBestResultsByExercise(Long athleteId) {
        List<Workoutplan> workouts = workoutRepository.findByAthleteId(athleteId);
        return workouts.stream()
                .filter(w -> w.getExerciseName() != null && w.getWeight() != null)
                .collect(Collectors.toMap(
                        Workoutplan::getExerciseName,
                        w -> w,
                        (existing, replacement) -> existing.getWeight() > replacement.getWeight() ? existing : replacement
                ));
    }

    private GoalProgressDto toGoalProgressDto(Goal goal, Workoutplan best) {
        GoalProgressDto dto = new GoalProgressDto();
        dto.setId(goal.getId());
        dto.setExerciseType(goal.getExerciseType());
        dto.setTargetWeight(goal.getTargetWeight());
        dto.setTargetReps(goal.getTargetReps());
        dto.setIsStatus(goal.getIsStatus());
        dto.setDeadline(goal.getDeadline());
        dto.setCreatedAt(goal.getCreatedAt());

        if (best != null) {
            dto.setCurrentMaxWeight(best.getWeight());
            dto.setCurrentMaxReps((int) best.getReps());

            double weightProgress = best.getWeight() / goal.getTargetWeight();
            double repsProgress = (double) best.getReps() / goal.getTargetReps();
            double totalProgress = (weightProgress + repsProgress) / 2;
            dto.setProgressPercent(Math.min(totalProgress * 100, 100.0));
        } else {
            dto.setCurrentMaxWeight(0);
            dto.setCurrentMaxReps(0);
            dto.setProgressPercent(0.0);
        }

        return dto;
    }

    public void deleteGoal(Long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new RuntimeException("Цель с ID " + goalId + " не найдена");
        }
        goalRepository.deleteById(goalId);
    }
}