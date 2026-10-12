package com.edutrack.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("ADMIN_HASH: " + encoder.encode("AdminPassword123!"));
        System.out.println("STUDENT_HASH: " + encoder.encode("StudentPassword123!"));
    }
}
