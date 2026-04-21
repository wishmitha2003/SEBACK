package com.ezyenglish.auth.controller;

import com.ezyenglish.auth.service.PronunciationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping("/api/pronunciation")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class PronunciationController {

    private final PronunciationService pronunciationService;

    /**
     * Get pronunciation audio for a specific word
     * @param text The word to get pronunciation for
     * @return Audio file
     */
    @GetMapping("/play")
    public ResponseEntity<?> playPronunciation(@RequestParam String text) {
        log.info("Request to play pronunciation for: {}", text);
        
        try {
            String audioPath = pronunciationService.synthesizePronunciation(text);
            
            if (audioPath == null) {
                return ResponseEntity.badRequest()
                    .body("Failed to generate pronunciation for: " + text);
            }
            
            Path path = Paths.get(audioPath);
            Resource resource = new UrlResource(path.toUri());
            
            if (resource.exists()) {
                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .contentType(MediaType.parseMediaType("audio/mpeg"))
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (MalformedURLException e) {
            log.error("Invalid audio path", e);
            return ResponseEntity.badRequest().body("Invalid audio path");
        } catch (Exception e) {
            log.error("Error playing pronunciation", e);
            return ResponseEntity.internalServerError()
                .body("Error generating pronunciation: " + e.getMessage());
        }
    }

    /**
     * Get pronunciation URL for a word (returns JSON with URL)
     * @param text The word to get pronunciation for
     * @return JSON with audio URL
     */
    @GetMapping("/url")
    public ResponseEntity<?> getPronunciationUrl(@RequestParam String text) {
        log.info("Request to get pronunciation URL for: {}", text);
        
        try {
            String url = pronunciationService.getPronunciationUrl(text);
            
            if (url != null) {
                return ResponseEntity.ok()
                    .body(java.util.Map.of("url", url, "text", text));
            } else {
                return ResponseEntity.badRequest()
                    .body("Failed to generate pronunciation for: " + text);
            }
            
        } catch (Exception e) {
            log.error("Error getting pronunciation URL", e);
            return ResponseEntity.internalServerError()
                .body("Error generating pronunciation: " + e.getMessage());
        }
    }
}