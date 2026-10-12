package experiments.exp1_core_java;

import java.util.Scanner;

/**
 * Experiment 1: Student Management using Core Java Classes, Objects,
 * Constructors, Methods, and Keyboard Input with Scanner.
 */
public class StudentManagementExp1 {

    // Student entity representing domain attributes
    static class Student {
        private String studentId;
        private String name;
        private String branch;
        private double marks;

        // Default Constructor
        public Student() {
            this.studentId = "STU000";
            this.name = "Unknown";
            this.branch = "General";
            this.marks = 0.0;
        }

        // Parameterized Constructor
        public Student(String studentId, String name, String branch, double marks) {
            this.studentId = studentId;
            this.name = name;
            this.branch = branch;
            this.marks = marks;
        }

        // Method to calculate academic grade
        public String calculateGrade() {
            if (marks >= 90.0) return "A+ (Outstanding)";
            if (marks >= 80.0) return "A (Excellent)";
            if (marks >= 70.0) return "B (Good)";
            if (marks >= 60.0) return "C (Average)";
            if (marks >= 50.0) return "D (Pass)";
            return "F (Fail)";
        }

        // Method to display student information
        public void displayDetails() {
            System.out.println("----------------------------------------------");
            System.out.println("Student ID : " + studentId);
            System.out.println("Full Name  : " + name);
            System.out.println("Branch     : " + branch);
            System.out.println("Marks      : " + marks + " / 100");
            System.out.println("Grade      : " + calculateGrade());
            System.out.println("----------------------------------------------");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("==================================================");
        System.out.println("   EXPERIMENT 1: JAVA CLASSES, OBJECTS & SCANNER  ");
        System.out.println("==================================================");

        // 1. Create object with Default Constructor
        System.out.println("\n[1] Creating Default Student Object:");
        Student defaultStudent = new Student();
        defaultStudent.displayDetails();

        // 2. Interactive Input using Scanner
        System.out.print("\nEnter number of students to enroll: ");
        int count = 0;
        if (scanner.hasNextInt()) {
            count = scanner.nextInt();
            scanner.nextLine(); // consume newline
        } else {
            count = 2; // fallback for non-interactive test
            scanner.nextLine();
        }

        Student[] roster = new Student[count];

        for (int i = 0; i < count; i++) {
            System.out.println("\nEntering details for Student #" + (i + 1) + ":");
            System.out.print("Enter Student ID (e.g. STU101): ");
            String id = scanner.nextLine().trim();

            System.out.print("Enter Full Name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Enter Branch: ");
            String branch = scanner.nextLine().trim();

            System.out.print("Enter Marks (0-100): ");
            double marks = Double.parseDouble(scanner.nextLine().trim());

            // Creating object with Parameterized Constructor
            roster[i] = new Student(id, name, branch, marks);
        }

        System.out.println("\n==================================================");
        System.out.println("             STUDENT ROSTER SUMMARY               ");
        System.out.println("==================================================");
        for (Student s : roster) {
            s.displayDetails();
        }

        scanner.close();
        System.out.println("Experiment 1 execution completed successfully!");
    }
}
