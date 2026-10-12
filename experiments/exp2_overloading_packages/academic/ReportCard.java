package experiments.exp2_overloading_packages.academic;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User-defined package: experiments.exp2_overloading_packages.academic
 * Demonstrates package encapsulation and usage of built-in packages (java.util.Date, java.text.SimpleDateFormat).
 */
public class ReportCard {
    private String studentId;
    private String studentName;
    private double gpa;
    private Date issueDate;

    public ReportCard(String studentId, String studentName, double gpa) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.gpa = gpa;
        this.issueDate = new Date(); // Built-in java.util package
    }

    public void printReport() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); // Built-in java.text package
        System.out.println("============== OFFICIAL REPORT CARD ==============");
        System.out.println("Issued By  : Department of Academic Affairs");
        System.out.println("Date/Time  : " + formatter.format(issueDate));
        System.out.println("Student ID : " + studentId);
        System.out.println("Name       : " + studentName);
        System.out.println("Final CGPA : " + String.format("%.2f", gpa) + " / 10.0");
        System.out.println("Status     : " + (gpa >= 5.0 ? "PASSED WITH MERIT" : "NEEDS IMPROVEMENT"));
        System.out.println("==================================================");
    }
}
