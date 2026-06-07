package stronglab.controller;

import stronglab.dto.StatisticsDto;
import stronglab.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/{athleteId}")
    public ResponseEntity<StatisticsDto> getStatistics(@PathVariable Long athleteId) {
        StatisticsDto statistics = statisticsService.getStatistics(athleteId);
        return ResponseEntity.ok(statistics);
    }
}