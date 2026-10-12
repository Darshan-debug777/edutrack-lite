package com.edutrack.quiz;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

/**
 * Experiment 9 Service: Demonstrating Spring Dependency Injection and business logic
 */
@Service
public class QuizService {

    public Map<String, Object> calculateQuizScore(String studentId, int totalQuestions, int correctAnswers) {
        if (totalQuestions <= 0) {
            throw new IllegalArgumentException("Total questions must be greater than zero.");
        }
        if (correctAnswers < 0 || correctAnswers > totalQuestions) {
            throw new IllegalArgumentException("Correct answers cannot exceed total questions or be negative.");
        }

        double scorePercentage = Math.round(((double) correctAnswers / totalQuestions * 100.0) * 10.0) / 10.0;
        String grade = scorePercentage >= 85 ? "A+ (Excellent)"
                     : scorePercentage >= 70 ? "A (Very Good)"
                     : scorePercentage >= 55 ? "B (Good)"
                     : scorePercentage >= 40 ? "C (Pass)" : "F (Fail)";

        Map<String, Object> result = new HashMap<>();
        result.put("studentId", studentId);
        result.put("totalQuestions", totalQuestions);
        result.put("correctAnswers", correctAnswers);
        result.put("percentage", scorePercentage);
        result.put("grade", grade);
        result.put("status", scorePercentage >= 40 ? "QUALIFIED" : "DISQUALIFIED");

        return result;
    }
}
