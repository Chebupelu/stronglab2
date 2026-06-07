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
    private TrainerRepository trainerRepository; // Потребуется для поиска тренера

    @PostMapping("/add-athlete")
    public ResponseEntity<?> addAthleteToTrainer(@RequestParam String email, @RequestParam Long trainerId) {
        // 1. Сначала ищем тренера в базе по его ID
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElse(null);

        if (trainer == null) {
            return ResponseEntity.status(404).body("Тренер с таким ID не найден");
        }

        // 2. Ищем пользователя (атлета) по его Email
        return userRepository.findByEmail(email).map(user -> {

            // 3. Ищем существующую запись в таблице athlete по user_id
            Athlete athlete = athlereRepository.findByUserId(user.getId())
                    .orElse(new Athlete()); // Если записи еще нет, создаем новый объект

            // 4. Заполняем связи объектами
            athlete.setUser(user);
            athlete.setTrainer(trainer); // Передаем найденный объект Trainer (Lombok сеттер)

            // 5. Сохраняем атлета с обновленной привязкой
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
            plan.setExerciseName(request.getExerciseName());      // НОВОЕ поле
            plan.setWorkoutDate(request.getWorkoutDate());
            plan.setSets(request.getSets());
            plan.setReps(request.getReps());
            plan.setWeight(request.getWeight());

            // Рассчитываем нагрузку
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

    // Метод для Атлета: получить свои тренировки
    @GetMapping("/athlete/{athleteId}")
    public List<Workoutplan> getMyWorkouts(@PathVariable Long athleteId) {
        return workoutRepository.findByAthleteId(athleteId);
    }

    // Метод для Тренера (вызывается из WPF): назначить новую тренировку
    @PostMapping("/assign")
    public Workoutplan assignWorkout(@RequestBody Workoutplan newPlan) {
        newPlan.setIsCompleted(false);
        newPlan.setStatus("Назначено");
        return workoutRepository.save(newPlan);
    }

    // Обновление статуса (когда атлет нажал "Выполнить")
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
