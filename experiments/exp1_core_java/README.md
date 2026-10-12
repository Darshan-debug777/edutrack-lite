# Experiment 1: Core Java - Classes, Objects, Constructors, Methods & Scanner

## Objective
To demonstrate the fundamental object-oriented programming concepts in Java including class definition, instance variables, default and parameterized constructors, member methods, object instantiation, and user keyboard input using `java.util.Scanner`.

## Source File
- [`StudentManagementExp1.java`](StudentManagementExp1.java)

## Concepts Demonstrated
1. **Class and Object**: `Student` blueprint with state (`studentId`, `name`, `branch`, `marks`) and behavior (`calculateGrade()`, `displayDetails()`).
2. **Default Constructor**: Initializes default values (`STU000`, `Unknown`).
3. **Parameterized Constructor**: Accepts user inputs to initialize state.
4. **Scanner Keyboard Input**: Using `Scanner(System.in)`, `nextInt()`, and `nextLine()`.
5. **Array of Objects**: Allocating and iterating over an array of student objects.

## How to Compile and Run
Run in Windows PowerShell:
```powershell
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path

cd "c:\Users\Darshan jain\OneDrive\Desktop\EDUTRAC"
javac experiments\exp1_core_java\StudentManagementExp1.java
java experiments.exp1_core_java.StudentManagementExp1
```
