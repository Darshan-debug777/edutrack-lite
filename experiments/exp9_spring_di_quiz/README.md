# Experiment 9: Spring Boot Controller, Service & Dependency Injection

## Objective
To demonstrate Spring Boot REST architecture, separation of concerns using `@RestController` and `@Service`, loose coupling via Constructor-based Dependency Injection (DI), and exposing a live quiz score calculation endpoint.

## Source Files
- [`QuizService.java`](QuizService.java) — Business logic annotated with `@Service`.
- [`QuizController.java`](QuizController.java) — REST endpoints annotated with `@RestController` receiving injected service.

## Concepts Demonstrated
1. **Inversion of Control (IoC) & Dependency Injection**: Spring manages the lifecycle of `QuizService` and injects it into `QuizController` constructor.
2. **REST Endpoints**:
   - `GET /api/quiz/calculate?studentId=STU101&totalQuestions=10&correctAnswers=8`
   - `POST /api/quiz/calculate`
3. **Automatic JSON Serialization**: Jackson library mapping Java `Map` to HTTP JSON response.

## How to Test
When the EduTrack backend is running:
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/quiz/calculate?studentId=STU101&totalQuestions=10&correctAnswers=9"
```
Sample Response:
```json
{
  "success": true,
  "message": "Quiz score calculated via Spring DI Service",
  "data": {
    "studentId": "STU101",
    "totalQuestions": 10,
    "correctAnswers": 9,
    "percentage": 90.0,
    "grade": "A+ (Excellent)",
    "status": "QUALIFIED"
  }
}
```
