package com.edutrack.controller;

import com.edutrack.model.User;
import com.edutrack.model.dto.ApiResponse;
import com.edutrack.model.dto.LoginRequest;
import com.edutrack.model.dto.RegisterRequest;
import com.edutrack.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody RegisterRequest request) {
        User savedUser = authService.register(request);
        // Clean out password hash before sending response
        savedUser.setPasswordHash(null);
        return ResponseEntity.ok(ApiResponse.ok("Registration completed successfully! You can now log in.", savedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<User>> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        User user = authService.login(request, session);
        user.setPasswordHash(null);
        return ResponseEntity.ok(ApiResponse.ok("Welcome back, " + user.getName() + "!", user));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getCurrentUser(HttpSession session) {
        User user = authService.getCurrentUser(session);
        user.setPasswordHash(null);
        return ResponseEntity.ok(ApiResponse.ok("User session is active", user));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        authService.logout(session);
        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully."));
    }
}
