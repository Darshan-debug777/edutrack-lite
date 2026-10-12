# Experiment 3: Inheritance, Abstract Classes, `super`, `final` & Exception Handling

## Objective
To demonstrate single inheritance, multilevel inheritance, hierarchical inheritance, the `super` keyword, abstract classes, the `final` keyword (constants, methods, and classes), and comprehensive exception handling using `try`, `catch`, `finally`, `throw`, and `throws`.

## Source File
- [`Exp3Demo.java`](Exp3Demo.java)

## Concepts Demonstrated
1. **Single Inheritance**: `Student` extends abstract class `Person`.
2. **Multilevel Inheritance**: `GraduateStudent` extends `Student` which extends `Person`.
3. **Hierarchical Inheritance**: Both `Student` and `Faculty` extend base class `Person`.
4. **`super` Keyword**:
   - `super(id, name, email)` calls the superclass constructor.
   - `super.displayBasicInfo()` invokes the superclass method.
5. **Abstract Class**: `Person` defines the contract `public abstract String getRoleDescription()`.
6. **`final` Keyword**:
   - `final` variable: `INSTITUTION_NAME` (cannot be mutated).
   - `final` method: `printInstitutionHeader()` (cannot be overridden).
   - `final` class: `GraduateStudent` (cannot be subclassed).
7. **Exception Handling**:
   - Custom checked exception: `InvalidAttendanceException`.
   - `throws` clause on method signature.
   - `throw new InvalidAttendanceException(...)` when validation fails.
   - `try-catch-finally` block ensuring proper handling and execution of guaranteed cleanup code.

## How to Compile and Run
Run in Windows PowerShell:
```powershell
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path

cd "c:\Users\Darshan jain\OneDrive\Desktop\EDUTRAC"
javac experiments\exp3_inheritance_exceptions\Exp3Demo.java
java experiments.exp3_inheritance_exceptions.Exp3Demo
```
