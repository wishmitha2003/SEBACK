package com.ezyenglish.auth.controller;

import com.ezyenglish.auth.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class GameController {

    private final GameService gameService;

    @GetMapping("/play/{id}")
    public ResponseEntity<Map<String, Object>> getGameForPlay(@PathVariable String id) {
        log.info("Request received to fetch game for play with id: {}", id);
        
        Map<String, Object> gameData = gameService.getGameForPlay(id);
        
        // If map is empty, it means the game was not found
        if (gameData.isEmpty()) {
            log.warn("Game not found with id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        log.info("Game data prepared successfully for id: {}", id);
        return ResponseEntity.ok(gameData);
    }
}
