package stronglab.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stronglab.dto.BodyParamsRequest;
import stronglab.exception.ResourceNotFoundException;
import stronglab.model.Athlete;
import stronglab.model.Bodyparams;
import stronglab.repository.AthlereRepository;
import stronglab.repository.BodyparamsRepository;

import java.time.LocalDate;

@Service
public class BodyParamsService {

    @Autowired
    private BodyparamsRepository bodyParamsRepository;

    @Autowired
    private AthlereRepository athleteRepository;

    public Bodyparams getLatestParams(Long athleteId) {
        // 🔥 Исправлено: ищем по athlete_id (id таблицы athlete)
        Athlete athlete = athleteRepository.findById(athleteId)
                .orElseThrow(() -> new ResourceNotFoundException("Атлет не найден для athlete_id: " + athleteId));

        return bodyParamsRepository.findByAthleteIdOrderByDateDesc(athleteId)
                .stream()
                .findFirst()
                .orElse(null); // возвращаем null, если параметров нет
    }

    public Bodyparams updateParams(Long athleteId, BodyParamsRequest request) {
        Athlete athlete = athleteRepository.findById(athleteId)
                .orElseThrow(() -> new ResourceNotFoundException("Атлет не найден для athlete_id: " + athleteId));

        Bodyparams params = bodyParamsRepository
                .findByAthleteIdOrderByDateDesc(athleteId)
                .stream()
                .findFirst()
                .orElse(new Bodyparams());

        params.setAthlete(athlete);
        params.setWeight(request.getWeight());
        params.setHeight(request.getHeight());
        params.setDate(LocalDate.now());

        return bodyParamsRepository.save(params);
    }
}
