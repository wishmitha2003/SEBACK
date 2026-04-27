package com.ezyenglish.auth.service;

import com.ezyenglish.auth.model.Game;
import com.ezyenglish.auth.model.Question;
import com.ezyenglish.auth.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    public Map<String, Object> getGameForPlay(String gameId) {
        log.info("Fetching game for play with id: {}", gameId);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Optional<Game> gameOptional = gameRepository.findById(gameId);
            
            if (gameOptional.isEmpty()) {
                log.warn("Game not found with id: {}", gameId);
                return result;
            }
            
            Game game = gameOptional.get();
            
            // Handle null or empty questions list
            if (game.getQuestions() == null || game.getQuestions().isEmpty()) {
                log.warn("Game with id: {} has no questions", gameId);
                return result;
            }
            
            // Select a random question
            Random random = new Random();
            Question randomQuestion = game.getQuestions().get(random.nextInt(game.getQuestions().size()));
            
            // Handle null question
            if (randomQuestion == null) {
                log.warn("Selected question is null for game id: {}", gameId);
                return result;
            }
            
            // Shuffle options if available
            List<String> shuffledOptions = new ArrayList<>();
            if (randomQuestion.getOptions() != null) {
                shuffledOptions.addAll(randomQuestion.getOptions());
                Collections.shuffle(shuffledOptions);
            }
            
            // Build result map
            result.put("english", randomQuestion.getEnglishWord() != null ? randomQuestion.getEnglishWord() : "");
            result.put("correct", randomQuestion.getCorrectAnswer() != null ? randomQuestion.getCorrectAnswer() : "");
            result.put("options", shuffledOptions);
            
            log.info("Successfully prepared game for play with id: {}", gameId);
            
        } catch (Exception e) {
            log.error("Error while fetching game for play with id: {}", gameId, e);
        }
        
        return result;
    }
}
