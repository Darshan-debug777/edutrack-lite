package com.edutrack.quiz;

import com.edutrack.model.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Experiment 9 Controller: Demonstrating Spring REST Controller and Dependency Injection
 */
@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    // Dependency Injection: Spring injects QuizService
    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/calculate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calculateGet(
            @RequestParam(defaultValue = "STU101") String studentId,
            @RequestParam(defaultValue = "10") int totalQuestions,
            @RequestParam(defaultValue = "8") int correctAnswers) {
        Map<String, Object> score = quizService.calculateQuizScore(studentId, totalQuestions, correctAnswers);
        return ResponseEntity.ok(ApiResponse.ok("Quiz score calculated via Spring DI Service", score));
    }

    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calculatePost(@RequestBody Map<String, Object> body) {
        String studentId = body.getOrDefault("studentId", "STU101").toString();
        int totalQuestions = Integer.parseInt(body.getOrDefault("totalQuestions", 10).toString());
        int correctAnswers = Integer.parseInt(body.getOrDefault("correctAnswers", 8).toString());

        Map<String, Object> score = quizService.calculateQuizScore(studentId, totalQuestions, correctAnswers);
        return ResponseEntity.ok(ApiResponse.ok("Quiz score calculated via Spring DI Service", score));
    }
}
