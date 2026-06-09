package stronglab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stronglab.dto.WorkoutRequest;
import stronglab.model.Athlete;
import stronglab.model.Trainer;
import stronglab.model.Workoutplan;
import stronglab.repository.AthlereRepository;
import stronglab.repository.TrainerRepository;
import stronglab.repository.UserRepository;
import stronglab.repository.WorkoutplanRepository;
import stronglab.service.WorkoutWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/workouts")
public class WorkoutController {

    @Autowired
    private WorkoutplanRepository workoutRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AthlereRepository athlereRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private WorkoutWebSocketHandler webSocketHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/add-athlete")
    public ResponseEntity<?> addAthleteToTrainer(@RequestParam String email, @RequestParam Long trainerId) {
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElse(null);

        if (trainer == null) {
            return ResponseEntity.status(404).body("Тренер с таким ID не найден");
        }

        return userRepository.findByEmail(email).map(user -> {

            Athlete athlete = athlereRepository.findByUserId(user.getId())
                    .orElse(new Athlete());

            athlete.setUser(user);
            athlete.setTrainer(trainer);

            athlereRepository.save(athlete);

            return ResponseEntity.ok("Атлет успешно привязан к тренеру!");
        }).orElse(ResponseEntity.status(404).body("Пользователь с таким Email не найден"));
    }


    @PostMapping("/save")
    public ResponseEntity<?> saveWorkout(@RequestBody WorkoutRequest request) {
        try {
            Athlete athlete = athlereRepository.findById(request.getAthleteId()).orElse(null);

            if (athlete == null) {
                return ResponseEntity.status(404).body("Атлет с ID " + request.getAthleteId() + " не найден в БД");
            }

            Workoutplan plan = new Workoutplan();
            plan.setAthlete(athlete);
            plan.setExerciseName(request.getExerciseName());
            plan.setWorkoutDate(request.getWorkoutDate());
            plan.setSets(request.getSets());
            plan.setReps(request.getReps());
            plan.setWeight(request.getWeight());

            double calculatedLoad = request.getWeight() * request.getSets() * request.getReps();
            plan.setCalculatedLoad(calculatedLoad);

            plan.setIsCompleted(false);
            plan.setStatus("Назначено");

            workoutRepository.save(plan);

            return ResponseEntity.ok("Тренировка успешно сохранена!");

        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Ошибка БД: " + ex.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Server is running!");
    }

    @GetMapping("/athletes")
    public List<Athlete> getAllAthletes(@RequestParam Long trainerId){
        if(trainerId != null){
            return athlereRepository.findByTrainerId(trainerId);
        }
        return new ArrayList<>();
        //return  userRepository.findAll();
    }

    @GetMapping("/athlete/{athleteId}")
    public List<Workoutplan> getMyWorkouts(@PathVariable Long athleteId) {
        return workoutRepository.findByAthleteId(athleteId);
    }

    @PostMapping("/assign")
    public Workoutplan assignWorkout(@RequestBody Workoutplan newPlan) {
        newPlan.setIsCompleted(false);
        newPlan.setStatus("Назначено");

        Workoutplan savedPlan = workoutRepository.save(newPlan);

        try {
            String json = objectMapper.writeValueAsString(savedPlan);
            if (savedPlan.getAthlete() != null) {
                webSocketHandler.sendWorkoutNotification(savedPlan.getAthlete().getId(), json);
            }
        } catch (Exception e) {
            System.err.println("Не удалось отправить пуш через сокет: " + e.getMessage());
        }

        return savedPlan;
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<?> completeWorkout(@PathVariable Long id) {
        return workoutRepository.findById(id).map(plan -> {
            plan.setIsCompleted(true);
            plan.setStatus("Выполнено");
            workoutRepository.save(plan);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/athlete/{athleteId}/date")
    public List<Workoutplan> getWorkoutsByDate(
            @PathVariable Long athleteId,
            @RequestParam String date
    ) {
        LocalDate localDate = LocalDate.parse(date);
        return workoutRepository.findByAthleteIdAndWorkoutDate(athleteId, localDate);
    }
}
