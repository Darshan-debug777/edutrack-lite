package experiments.exp2_overloading_packages;

// Importing user-defined package
import experiments.exp2_overloading_packages.academic.ReportCard;

/**
 * Experiment 2: Constructor Overloading, Method Overloading,
 * Built-in Packages, and User-Defined Packages.
 */
public class Exp2Demo {

    static class StudentScoreEvaluator {
        private String studentId;
        private String studentName;
        private int semester;

        // ==========================================
        // 1. CONSTRUCTOR OVERLOADING
        // ==========================================

        // Constructor 1: Default
        public StudentScoreEvaluator() {
            this.studentId = "STU_TEMP";
            this.studentName = "Provisional Student";
            this.semester = 1;
        }

        // Constructor 2: ID and Name only
        public StudentScoreEvaluator(String studentId, String studentName) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.semester = 1; // Default first semester
        }

        // Constructor 3: Full fields
        public StudentScoreEvaluator(String studentId, String studentName, int semester) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.semester = semester;
        }

        // ==========================================
        // 2. METHOD OVERLOADING
        // ==========================================

        // Overload 1: Calculate CGPA from two semester marks
        public double computeCGPA(double sem1Marks, double sem2Marks) {
            System.out.println("-> Executing computeCGPA(double, double) for 2 semesters");
            return ((sem1Marks + sem2Marks) / 2.0) / 10.0;
        }

        // Overload 2: Calculate CGPA from three semester marks
        public double computeCGPA(double sem1, double sem2, double sem3) {
            System.out.println("-> Executing computeCGPA(double, double, double) for 3 semesters");
            return ((sem1 + sem2 + sem3) / 3.0) / 10.0;
        }

        // Overload 3: Calculate CGPA from subject marks array and credits array
        public double computeCGPA(double[] marks, int[] credits) {
            System.out.println("-> Executing computeCGPA(double[], int[]) with credit weighting");
            double weightedSum = 0;
            int totalCredits = 0;
            for (int i = 0; i < marks.length; i++) {
                double gradePoint = marks[i] / 10.0;
                weightedSum += gradePoint * credits[i];
                totalCredits += credits[i];
            }
            return totalCredits > 0 ? (weightedSum / totalCredits) : 0.0;
        }

        public String getStudentId() { return studentId; }
        public String getStudentName() { return studentName; }
        public int getSemester() { return semester; }
    }

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("   EXPERIMENT 2: OVERLOADING & PACKAGES DEMONSTRATION     ");
        System.out.println("==========================================================");

        // 1. Constructor Overloading demonstration
        System.out.println("\n[A] CONSTRUCTOR OVERLOADING:");
        StudentScoreEvaluator s1 = new StudentScoreEvaluator();
        System.out.println("Student 1 (Default): " + s1.getStudentId() + " - " + s1.getStudentName() + " (Sem " + s1.getSemester() + ")");

        StudentScoreEvaluator s2 = new StudentScoreEvaluator("STU101", "John Doe");
        System.out.println("Student 2 (2 args):  " + s2.getStudentId() + " - " + s2.getStudentName() + " (Sem " + s2.getSemester() + ")");

        StudentScoreEvaluator s3 = new StudentScoreEvaluator("STU102", "Jane Smith", 4);
        System.out.println("Student 3 (3 args):  " + s3.getStudentId() + " - " + s3.getStudentName() + " (Sem " + s3.getSemester() + ")");

        // 2. Method Overloading demonstration
        System.out.println("\n[B] METHOD OVERLOADING:");
        double cgpa2Sem = s2.computeCGPA(84.0, 88.0);
        System.out.println("Result (2 Semesters): " + String.format("%.2f", cgpa2Sem));

        double cgpa3Sem = s2.computeCGPA(84.0, 88.0, 92.0);
        System.out.println("Result (3 Semesters): " + String.format("%.2f", cgpa3Sem));

        double[] marks = { 85.0, 90.0, 78.0, 92.0 };
        int[] credits = { 4, 3, 3, 4 };
        double weightedCGPA = s3.computeCGPA(marks, credits);
        System.out.println("Result (Weighted Credits): " + String.format("%.2f", weightedCGPA));

        // 3. User-defined and Built-in Packages demonstration
        System.out.println("\n[C] USER-DEFINED & BUILT-IN PACKAGES:");
        ReportCard card = new ReportCard(s3.getStudentId(), s3.getStudentName(), weightedCGPA);
        card.printReport();

        System.out.println("\nExperiment 2 execution completed successfully!");
    }
}
