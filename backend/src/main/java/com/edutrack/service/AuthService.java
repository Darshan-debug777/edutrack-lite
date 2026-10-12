package com.edutrack.service;

import com.edutrack.exception.BadRequestException;
import com.edutrack.exception.UnauthorizedException;
import com.edutrack.model.User;
import com.edutrack.model.dto.LoginRequest;
import com.edutrack.model.dto.RegisterRequest;
import com.edutrack.repository.UserJdbcRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    public static final String SESSION_USER_KEY = "LOGGED_IN_USER";

    private final UserJdbcRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserJdbcRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User login(LoginRequest request, HttpSession session) {
        if (request.getIdentifier() == null || request.getIdentifier().trim().isEmpty() ||
            request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new BadRequestException("Please enter both Student ID/Email and Password.");
        }

        Optional<User> userOpt = userRepository.findByIdentifier(request.getIdentifier().trim());
        if (userOpt.isEmpty()) {
            throw new UnauthorizedException("Invalid credentials. Please check your Student ID/email and password.");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials. Please check your Student ID/email and password.");
        }

        // Store user in HTTP session
        session.setAttribute(SESSION_USER_KEY, user);
        return user;
    }

    public User register(RegisterRequest request) {
        String studentId = request.getStudentId().trim();
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByStudentId(studentId)) {
            throw new BadRequestException("Student ID '" + studentId + "' is already registered. Please choose another or login.");
        }

        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email address '" + email + "' is already registered. Please login instead.");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = new User();
        newUser.setStudentId(studentId);
        newUser.setName(request.getName().trim());
        newUser.setEmail(email);
        newUser.setPasswordHash(hashedPassword);
        newUser.setRole("STUDENT");
        newUser.setBranch(request.getBranch().trim());
        newUser.setSemester(request.getSemester());

        return userRepository.save(newUser);
    }

    public User getCurrentUser(HttpSession session) {
        if (session == null) {
            throw new UnauthorizedException("No active session found. Please log in.");
        }
        User user = (User) session.getAttribute(SESSION_USER_KEY);
        if (user == null) {
            throw new UnauthorizedException("Session expired or unauthorized. Please log in.");
        }
        // Refresh latest state from DB in case profile was updated
        return userRepository.findByStudentId(user.getStudentId()).orElse(user);
    }

    public void logout(HttpSession session) {
        if (session != null) {
            session.removeAttribute(SESSION_USER_KEY);
            session.invalidate();
        }
    }
}
