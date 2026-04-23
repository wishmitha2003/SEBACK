package com.ezyenglish.auth.service;

import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class PronunciationService {

    @Value("${azure.speech.key}")
    private String speechKey;

    @Value("${azure.speech.region}")
    private String speechRegion;

    private SpeechConfig speechConfig;
    private static final String AUDIO_DIR = "uploads/pronunciation/";

    @PostConstruct
    public void init() {
        log.info("Initializing Azure Speech Service with region: {}", speechRegion);
        
        // Create speech config with subscription key and region
        speechConfig = SpeechConfig.fromSubscription(speechKey, speechRegion);
        
        // Ensure audio directory exists
        try {
            Path audioPath = Paths.get(AUDIO_DIR);
            if (!Files.exists(audioPath)) {
                Files.createDirectories(audioPath);
                log.info("Created pronunciation audio directory: {}", AUDIO_DIR);
            }
        } catch (IOException e) {
            log.error("Failed to create audio directory", e);
        }
    }

    /**
     * Synthesize pronunciation audio for a word
     * @param text The word or phrase to synthesize
     * @return The file path of the generated audio
     */
    public String synthesizePronunciation(String text) {
        log.info("Synthesizing pronunciation for: {}", text);
        
        try (SpeechSynthesizer synthesizer = new SpeechSynthesizer(speechConfig)) {
            // Generate unique filename
            String fileName = UUID.randomUUID().toString() + ".mp3";
            String outputPath = AUDIO_DIR + fileName;
            
            // Synthesize speech - use SpeakTextAsync and get audio data directly
            SpeechSynthesisResult result = synthesizer.SpeakText(text);
            
            if (result.getReason() == com.microsoft.cognitiveservices.speech.ResultReason.SynthesizingAudioCompleted) {
                // Get audio data directly from result
                byte[] audioData = result.getAudioData();
                
                if (audioData != null && audioData.length > 0) {
                    Files.write(Paths.get(outputPath), audioData);
                    log.info("Audio saved to: {} ({} bytes)", outputPath, audioData.length);
                    return outputPath;
                } else {
                    log.error("No audio data returned for text: {}", text);
                    return null;
                }
            } else {
                log.error("Speech synthesis failed: {}", result.getReason());
                return null;
            }
            
        } catch (Exception e) {
            log.error("Failed to synthesize pronunciation for: {}", text, e);
            return null;
        }
    }

    /**
     * Get pronunciation audio URL for a word
     * This method generates audio on-demand and returns the URL
     * @param text The word to pronounce
     * @return URL path to the audio file
     */
    public String getPronunciationUrl(String text) {
        String audioPath = synthesizePronunciation(text);
        if (audioPath != null) {
            // Return relative URL that can be served statically
            return "/api/pronunciation/" + Paths.get(audioPath).getFileName().toString();
        }
        return null;
    }
}