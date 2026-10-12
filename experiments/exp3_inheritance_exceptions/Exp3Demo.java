package experiments.exp3_inheritance_exceptions;

/**
 * Experiment 3: Single, Multilevel, and Hierarchical Inheritance,
 * `super`, Abstract Classes, `final` keyword, and Exception Handling
 * (`try`, `catch`, `finally`, `throw`, and `throws`).
 */
public class Exp3Demo {

    // ========================================================
    // Custom Exception for Attendance Validation
    // ========================================================
    static class InvalidAttendanceException extends Exception {
        public InvalidAttendanceException(String message) {
            super(message);
        }
    }

    // ========================================================
    // Abstract Class: Person (Hierarchical & Multilevel base)
    // ========================================================
    abstract static class Person {
        // final variable: cannot be reassigned
        public static final String INSTITUTION_NAME = "EduTrack National Institute of Technology";

        protected String id;
        protected String name;
        protected String email;

        public Person(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        // Abstract method: must be implemented by subclasses
        public abstract String getRoleDescription();

        // final method: cannot be overridden by subclasses
        public final void printInstitutionHeader() {
            System.out.println("Affiliated to: " + INSTITUTION_NAME);
        }

        public void displayBasicInfo() {
            System.out.println("ID    : " + id);
            System.out.println("Name  : " + name);
            System.out.println("Email : " + email);
        }
    }

    // ========================================================
    // Single Inheritance: Student extends Person
    // ========================================================
    static class Student extends Person {
        protected String branch;
        protected int semester;
        protected double attendancePercentage;

        public Student(String id, String name, String email, String branch, int semester) {
            super(id, name, email); // `super` calling parent constructor
            this.branch = branch;
            this.semester = semester;
            this.attendancePercentage = 0.0;
        }

        @Override
        public String getRoleDescription() {
            return "Undergraduate Engineering Student";
        }

        // Method demonstrating `throw` and `throws`
        public void setAttendance(double percentage) throws InvalidAttendanceException {
            if (percentage < 0.0 || percentage > 100.0) {
                // `throw` keyword creating and propagating custom checked exception
                throw new InvalidAttendanceException("Attendance percentage must be between 0.0 and 100.0%. Provided: " + percentage);
            }
            this.attendancePercentage = percentage;
            System.out.println("Attendance recorded successfully: " + percentage + "%");
        }

        @Override
        public void displayBasicInfo() {
            super.displayBasicInfo(); // `super` calling parent method
            System.out.println("Branch: " + branch + " | Semester: " + semester);
            System.out.println("Role  : " + getRoleDescription());
        }
    }

    // ========================================================
    // Multilevel Inheritance: GraduateStudent extends Student
    // ========================================================
    static final class GraduateStudent extends Student {
        private String thesisTopic;

        public GraduateStudent(String id, String name, String email, String branch, int semester, String thesisTopic) {
            super(id, name, email, branch, semester); // `super` invoking Student constructor
            this.thesisTopic = thesisTopic;
        }

        @Override
        public String getRoleDescription() {
            return "Postgraduate Research Scholar (M.Tech / Ph.D.)";
        }

        public void displayResearchInfo() {
            displayBasicInfo();
            System.out.println("Thesis Topic: " + thesisTopic);
        }
    }

    // ========================================================
    // Hierarchical Inheritance: Faculty extends Person
    // ========================================================
    static class Faculty extends Person {
        private String department;
        private String designation;

        public Faculty(String id, String name, String email, String department, String designation) {
            super(id, name, email);
            this.department = department;
            this.designation = designation;
        }

        @Override
        public String getRoleDescription() {
            return "Academic Faculty Member (" + designation + ")";
        }

        @Override
        public void displayBasicInfo() {
            super.displayBasicInfo();
            System.out.println("Dept  : " + department + " | Title: " + designation);
            System.out.println("Role  : " + getRoleDescription());
        }
    }

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("   EXPERIMENT 3: INHERITANCE & EXCEPTION HANDLING         ");
        System.out.println("==========================================================");

        // 1. Single Inheritance & Abstract class
        System.out.println("\n[A] SINGLE INHERITANCE (Person -> Student):");
        Student student = new Student("STU101", "John Doe", "john.doe@edutrack.edu", "Computer Science", 4);
        student.printInstitutionHeader();
        student.displayBasicInfo();

        // 2. Multilevel Inheritance
        System.out.println("\n[B] MULTILEVEL INHERITANCE (Person -> Student -> GraduateStudent):");
        GraduateStudent grad = new GraduateStudent("GRAD501", "Grace Hopper", "grace@edutrack.edu",
                "Computer Science", 1, "Compiler Optimizations in Relational Databases");
        grad.displayResearchInfo();

        // 3. Hierarchical Inheritance
        System.out.println("\n[C] HIERARCHICAL INHERITANCE (Person -> Faculty):");
        Faculty faculty = new Faculty("FAC001", "Dr. Alan Turing", "alan.t@edutrack.edu", "Computer Science", "Professor");
        faculty.displayBasicInfo();

        // 4. Exception Handling: try, catch, finally, throw, throws
        System.out.println("\n[D] EXCEPTION HANDLING (try, catch, finally, throw, throws):");

        // Case 1: Valid attendance
        try {
            System.out.println("\n-- Testing Valid Attendance Input (85.5%):");
            student.setAttendance(85.5);
        } catch (InvalidAttendanceException e) {
            System.err.println("Caught exception: " + e.getMessage());
        } finally {
            System.out.println("Finally block executed: Database connection/resource verified.");
        }

        // Case 2: Invalid attendance triggering custom exception
        try {
            System.out.println("\n-- Testing Invalid Attendance Input (125.0%):");
            student.setAttendance(125.0);
        } catch (InvalidAttendanceException e) {
            System.out.println(">> CAUGHT CHECKED EXCEPTION successfully: " + e.getMessage());
        } finally {
            System.out.println("Finally block executed: Cleanup completed.");
        }

        System.out.println("\nExperiment 3 execution completed successfully!");
    }
}
