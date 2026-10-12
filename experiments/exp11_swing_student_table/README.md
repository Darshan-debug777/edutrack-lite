# Experiment 11: Java Swing Desktop GUI Application

## Objective
To develop a standalone graphical user interface (GUI) desktop application using Java Swing components: `JFrame`, `JLabel`, `JTextField`, `JButton`, `JTable`, `DefaultTableModel`, and event listeners to add and manage student records.

## Source File
- [`StudentSwingApp.java`](StudentSwingApp.java)

## Concepts Demonstrated
1. **Top-Level Container**: `JFrame` configuring dimensions, titles, and layout managers (`BorderLayout`, `GridLayout`).
2. **Interactive Controls**: `JTextField` for text input, `JComboBox` for dropdown selection, `JButton` for action dispatch.
3. **Data Grid**: `JTable` paired with `DefaultTableModel` (`addRow()`, `removeRow()`).
4. **Validation**: Enforcing mandatory fields and bounds (marks 0-100) before adding rows.
5. **Event Dispatch Thread (EDT)**: Initializing GUI safely using `SwingUtilities.invokeLater()`.

## How to Compile and Run
Run in Windows PowerShell:
```powershell
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-17.0.20.101-hotspot"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path

cd "c:\Users\Darshan jain\OneDrive\Desktop\EDUTRAC"
javac experiments\exp11_swing_student_table\StudentSwingApp.java
java experiments.exp11_swing_student_table.StudentSwingApp
```
Clicking **Add Student to Table** validates fields and adds the student to the active `JTable` view.
