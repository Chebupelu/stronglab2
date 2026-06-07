package stronglab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import stronglab.dto.BodyParamsRequest;
import stronglab.model.Bodyparams;
import stronglab.service.BodyParamsService;

@RestController
@RequestMapping("/api/profile")
public class BodyParamsController {

    @Autowired
    private BodyParamsService bodyParamsService;

    @GetMapping("/{athleteId}")
    public ResponseEntity<Bodyparams> getLatestParams(@PathVariable Long athleteId) {
        Bodyparams params = bodyParamsService.getLatestParams(athleteId);
        if (params == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(params);
    }

    @PostMapping("/{athleteId}")
    public ResponseEntity<Bodyparams> updateParams(
            @PathVariable Long athleteId,
            @RequestBody BodyParamsRequest request
    ) {
        Bodyparams saved = bodyParamsService.updateParams(athleteId, request);
        return ResponseEntity.ok(saved);
    }
}