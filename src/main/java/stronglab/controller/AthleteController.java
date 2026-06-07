package stronglab.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stronglab.model.Athlete;
import stronglab.repository.AthlereRepository;

@RestController
@RequestMapping("/api/athletes")
public class AthleteController {

    private final AthlereRepository athlereRepository;

    public AthleteController(AthlereRepository athlereRepository) {
        this.athlereRepository = athlereRepository;
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<Athlete> getAthleteByUserId(@PathVariable Long userId) {
        return athlereRepository.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
