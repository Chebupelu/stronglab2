package stronglab.controller;

import stronglab.dto.CreateGoalRequest;
import stronglab.dto.GoalProgressDto;
import stronglab.model.Goal;
import stronglab.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @GetMapping("/athlete/{athleteId}")
    public ResponseEntity<List<GoalProgressDto>> getAthleteGoals(@PathVariable Long athleteId) {
        return ResponseEntity.ok(goalService.getAthleteGoals(athleteId));
    }

    @PostMapping("/athlete/{athleteId}")
    public ResponseEntity<Goal> createGoal(
            @PathVariable Long athleteId,
            @RequestBody CreateGoalRequest request) {
        return ResponseEntity.ok(goalService.createGoal(athleteId, request));
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long goalId) {
        goalService.deleteGoal(goalId);
        return ResponseEntity.noContent().build();
    }
}
