# Experiment 2: Constructor Overloading, Method Overloading & Packages

## Objective
To demonstrate constructor overloading, method overloading (compile-time polymorphism), the use of built-in Java packages (`java.util`, `java.text`), and the creation and import of user-defined packages (`academic.ReportCard`).

## Source Files
- [`Exp2Demo.java`](Exp2Demo.java)
- [`academic/ReportCard.java`](academic/ReportCard.java)

## Concepts Demonstrated
1. **Constructor Overloading**: Three constructors for `StudentScoreEvaluator` with different parameter signatures.
2. **Method Overloading**: Three variants of `computeCGPA(...)` distinguishing by argument count and parameter types.
3. **Built-in Packages**: `java.util.Date` and `java.text.SimpleDateFormat`.
4. **User-defined Package**: `package experiments.exp2_overloading_packages.academic;` compiled and imported into the main driver class.

## How to Compile and Run
Run in Windows PowerShell:
```powershell
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path

cd "c:\Users\Darshan jain\OneDrive\Desktop\EDUTRAC"
javac experiments\exp2_overloading_packages\academic\ReportCard.java
javac experiments\exp2_overloading_packages\Exp2Demo.java
java experiments.exp2_overloading_packages.Exp2Demo
```
