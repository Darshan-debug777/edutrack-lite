# EduTrack Lite — Comprehensive Viva & Technical Interview Guide

A beginner-friendly guide covering fundamental concepts, architecture walkthroughs, 55+ likely viva questions and answers, and 5 simple live code changes you can demonstrate to the examiner.

---

## 1. End-to-End Request Flow: React to MySQL

```
[1. React Frontend UI (UnifiedDashboard.jsx)]
    User clicks "Save Marks to Database"
    ↓
[2. HTTP Client (api/client.js)]
    Executes: fetch('/api/admin/marks', { method: 'POST', body: JSON.stringify({...}) })
    ↓
[3. Spring Boot DispatcherServlet (Port 8080)]
    Intercepts HTTP request, routes to AdminController based on @RequestMapping("/api/admin")
    ↓
[4. Controller Layer (AdminController.java)]
    @PostMapping("/marks")
    Validates @Valid @RequestBody MarkUpdateRequest
    Calls academicService.updateMark(request)
    ↓
[5. Service Layer (AcademicService.java)]
    Enforces business logic:
    - Verifies studentId exists in database
    - Verifies subjectId exists
    - Ensures marksObtained >= 0 and marksObtained <= maxMarks
    Calls academicRepository.saveOrUpdateMark(studentId, subjectId, marks, maxMarks)
    ↓
[6. JDBC Repository Layer (AcademicJdbcRepository.java)]
    Acquires Connection from DataSource (HikariCP)
    Prepares SQL: "SELECT id FROM marks WHERE student_id = ? AND subject_id = ?"
    If found: "UPDATE marks SET marks_obtained = ? WHERE student_id = ? AND subject_id = ?"
    Else: "INSERT INTO marks (student_id, subject_id, marks_obtained, max_marks) VALUES (?, ?, ?, ?)"
    Uses PreparedStatement.setDouble(), executeUpdate() inside try-with-resources
    ↓
[7. Relational Database (MySQL / H2 in MySQL mode)]
    Stores updated row permanently on disk.
    ↓
[8. HTTP Response (JSON)]
    Returns 200 OK: {"success": true, "message": "Marks saved successfully in MySQL!"}
    ↓
[9. React State Update]
    React receives JSON, sets state, displays green success notification banner!
```

---

## 2. Core Viva Comparisons

### A. Method Overloading vs Method Overriding

| Feature | Method Overloading (Compile-Time) | Method Overriding (Run-Time) |
|---|---|---|
| **Location** | Within the same class | Between superclass and subclass |
| **Method Name** | Must be identical | Must be identical |
| **Parameters** | **Must be different** (count, type, order) | **Must be identical** |
| **Return Type** | Can be different | Must be same or covariant |
| **Keyword** | None | `@Override` annotation recommended |
| **Example in Project** | `computeCGPA(double, double)` vs `computeCGPA(double, double, double)` in Exp 2 | `getRoleDescription()` in `Student` overriding `Person` in Exp 3 |

### B. React Props vs State

| Feature | React `props` | React `state` |
|---|---|---|
| **Definition** | Parameters passed from parent to child | Internal data managed within component |
| **Mutability** | **Immutable** (read-only for child) | **Mutable** (via `setState` / `setCount`) |
| **Ownership** | Owned and controlled by parent | Owned and controlled by local component |
| **Trigger Re-render** | Yes, when parent passes new props | Yes, when `setCount(newValue)` is called |
| **Example in Project** | Passing `title` and `credits` into `CourseCard` in Exp 8 | `const [students, setStudents] = useState([])` in `UnifiedDashboard.jsx` |

### C. JDBC Components Overview

1. **`DriverManager` / `DataSource`**: Manages database drivers and provides physical connections. `DataSource` supports connection pooling (HikariCP).
2. **`Connection`**: Represents the active session/socket to the database server.
3. **`Statement`**: Executes static SQL (vulnerable to SQL injection).
4. **`PreparedStatement`**: Pre-compiles SQL templates with parameter placeholders (`?`). Protects against SQL injection and caches query execution plans.
5. **`ResultSet`**: A cursor pointing to the rows returned by executing a SQL `SELECT` query. `rs.next()` advances to the next row.

---

## 3. Likely Viva Questions & Answers for All 11 Experiments

### Experiment 1: Core Java, Classes, Objects & Scanner
1. **Q: What is a class and what is an object?**  
   *A:* A class is a blueprint or template that defines fields (state) and methods (behavior). An object is an instance of a class created in memory using the `new` keyword.
2. **Q: What is the purpose of a constructor?**  
   *A:* A constructor initializes newly created objects. It has the same name as the class and has no return type.
3. **Q: What happens if you do not write any constructor in a Java class?**  
   *A:* The Java compiler automatically provides a default no-argument constructor that initializes fields to their default values (e.g. `0`, `null`, `false`).
4. **Q: How does `Scanner` work for user input?**  
   *A:* `Scanner` breaks input into tokens using delimiters (by default whitespace). `nextInt()` reads integers, `nextDouble()` reads decimals, and `nextLine()` reads full string lines.
5. **Q: Why do we call `scanner.nextLine()` after `scanner.nextInt()`?**  
   *A:* Because `nextInt()` leaves the newline character `\n` in the input stream buffer; `nextLine()` consumes it to prevent skipping subsequent string inputs.

### Experiment 2: Overloading & Packages
1. **Q: What is compile-time polymorphism?**  
   *A:* It is polymorphism resolved at compile time, exemplified by method overloading where the compiler decides which method to call based on method signatures.
2. **Q: Can we overload a method just by changing its return type?**  
   *A:* No. The method signature consists only of the method name and parameter list. Changing only the return type causes a compilation error.
3. **Q: What is a package in Java?**  
   *A:* A package is a namespace that groups related classes and interfaces, prevents naming conflicts, and provides access protection.
4. **Q: What is the difference between built-in packages and user-defined packages?**  
   *A:* Built-in packages are part of the Java standard library (e.g. `java.util`, `java.io`). User-defined packages are created by developers using the `package` statement (e.g. `package experiments.exp2_overloading_packages.academic;`).
5. **Q: What package is imported automatically in every Java program?**  
   *A:* `java.lang` (contains `String`, `System`, `Math`, `Object`, etc.).

### Experiment 3: Inheritance & Exception Handling
1. **Q: What are the three types of inheritance supported in Java classes?**  
   *A:* Single inheritance (A &rarr; B), Multilevel inheritance (A &rarr; B &rarr; C), and Hierarchical inheritance (A &rarr; B and A &rarr; C). Java does not support multiple inheritance with classes to avoid the Diamond Problem.
2. **Q: What are the two uses of the `super` keyword?**  
   *A:* (1) `super(...)` invokes the immediate parent class constructor; (2) `super.methodName()` invokes an overridden parent class method.
3. **Q: What is an abstract class? Can you instantiate it?**  
   *A:* An abstract class is declared with `abstract` and can contain both abstract and concrete methods. It cannot be directly instantiated with `new`.
4. **Q: What does the `final` keyword do when applied to a variable, method, or class?**  
   *A:* `final` variable = constant; `final` method = cannot be overridden; `final` class = cannot be extended/subclassed.
5. **Q: What is the difference between `throw` and `throws`?**  
   *A:* `throw` is used inside a method body to explicitly throw an exception object. `throws` is used in a method declaration to indicate that the method might throw exceptions to its caller.

### Experiment 4: HTML, CSS & JavaScript
1. **Q: What are the three ways to apply CSS to HTML?**  
   *A:* (1) Inline CSS (using the `style` attribute on HTML tags); (2) Internal CSS (using `<style>` tags in `<head>`); (3) External CSS (using `<link rel="stylesheet" href="...">`).
2. **Q: Which CSS method has the highest specificity?**  
   *A:* Inline CSS overrides both internal and external CSS rules (unless `!important` is used).
3. **Q: What is the purpose of semantic HTML elements?**  
   *A:* Tags like `<header>`, `<main>`, `<section>`, and `<nav>` provide meaningful structure to browsers and screen readers rather than generic `<div>` tags.
4. **Q: How does `document.getElementById()` work?**  
   *A:* It searches the DOM tree and returns the unique HTML Element whose `id` matches the specified string.
5. **Q: What is an event listener in JavaScript?**  
   *A:* A method (`addEventListener`) that attaches an event handler function to a target element to respond to events such as `click`, `blur`, or `submit`.

### Experiment 5: Form Validation
1. **Q: Why is client-side form validation important if backend validation also exists?**  
   *A:* Client-side validation gives immediate feedback to users without unnecessary network round trips, reducing server load.
2. **Q: Explain the email validation regex `/^[^\s@]+@[^\s@]+\.[^\s@]+$/`.**  
   *A:* `^` start of string; `[^\s@]+` one or more non-space, non-@ characters (username); `@` literal at-symbol; `[^\s@]+` domain name; `\.` literal dot; `[^\s@]+$` top-level domain at end of string.
3. **Q: How do you enforce a 10-digit mobile number starting with 6, 7, 8, or 9?**  
   *A:* `/^[6-9]\d{9}$/` where `[6-9]` validates the first digit and `\d{9}` validates exactly nine subsequent digits.
4. **Q: What does `event.preventDefault()` do in a form submission handler?**  
   *A:* It stops the browser from submitting the form and reloading the page, allowing JavaScript to validate inputs first.
5. **Q: What is the difference between the `change` and `blur` events?**  
   *A:* `change` fires when the input value is committed and focus is lost; `blur` fires whenever an element loses focus, regardless of whether its value changed.

### Experiment 6: DOM Manipulation & Timer
1. **Q: What is the difference between `setInterval()` and `setTimeout()`?**  
   *A:* `setTimeout()` executes a function once after a specified delay. `setInterval()` executes a function repeatedly at specified time intervals until cancelled.
2. **Q: Why is `clearInterval()` necessary?**  
   *A:* To stop the repeating timer and free browser memory; otherwise the interval continues running in the background.
3. **Q: How do you dynamically create and add an element to the page?**  
   *A:* Use `const el = document.createElement("tagName")`, set its properties (`el.textContent = ...`), and append it with `parent.appendChild(el)`.
4. **Q: How do you remove an element from the DOM?**  
   *A:* Call `element.remove()` or `parent.removeChild(element)`.
5. **Q: What is the difference between `textContent` and `innerHTML`?**  
   *A:* `textContent` sets or returns raw text and does not parse HTML tags (preventing XSS). `innerHTML` parses strings as HTML elements.

### Experiment 7: React `useState` & `useEffect`
1. **Q: What are React Hooks?**  
   *A:* Special functions that let you use state and lifecycle features inside functional components without writing class components.
2. **Q: How does `useState` work?**  
   *A:* It returns an array with two elements: the current state value and a state updater function (e.g. `[count, setCount] = useState(0)`).
3. **Q: What does the dependency array in `useEffect(fn, [deps])` control?**  
   *A:* (1) Omitted: runs on every render; (2) `[]` (empty): runs once on mount; (3) `[dep1]`: runs on mount and whenever `dep1` changes.
4. **Q: Why should you return a cleanup function from `useEffect`?**  
   *A:* To clean up subscriptions, websockets, or timers when the component unmounts, preventing memory leaks.
5. **Q: Can you update state directly without the setter function (e.g. `state = 5`)?**  
   *A:* No. Direct mutation does not notify React to re-render the virtual DOM. Always use the setter function.

### Experiment 8: React Props & Client Routing
1. **Q: What are props in React?**  
   *A:* Short for properties, props are read-only inputs passed from a parent component to a child component to customize its rendering.
2. **Q: Can a child component modify the props it receives?**  
   *A:* No. Props are strictly read-only (unidirectional data flow). To notify a parent of changes, the child calls a callback function passed down as a prop.
3. **Q: What is client-side routing?**  
   *A:* Navigation where the browser URL changes and the view updates dynamically with JavaScript without making a full HTTP page request to the server.
4. **Q: What is the purpose of the `key` prop in React lists?**  
   *A:* It gives elements a stable identity so React's reconciliation algorithm can identify which items changed, were added, or were removed.
5. **Q: What is the difference between a controlled and uncontrolled component?**  
   *A:* A controlled component has its form input value driven by React state; an uncontrolled component stores its value in the traditional DOM.

### Experiment 9: Spring Boot & Dependency Injection
1. **Q: What is Dependency Injection (DI)?**  
   *A:* A design pattern where an object receives its dependencies from an external assembler (Spring IoC container) rather than instantiating them itself.
2. **Q: What is the difference between `@RestController` and `@Controller`?**  
   *A:* `@Controller` is used for traditional MVC returning view names (JSP/Thymeleaf). `@RestController` combines `@Controller` and `@ResponseBody`, returning domain objects serialized directly as JSON.
3. **Q: What is the role of `@Service`?**  
   *A:* It marks a class as a service provider containing business logic and makes it eligible for Spring component scanning and dependency injection.
4. **Q: Why is constructor injection preferred over field injection (`@Autowired`)?**  
   *A:* Constructor injection guarantees that required dependencies are not null, makes classes immutable, and simplifies unit testing with mock objects.
5. **Q: What is Spring Boot's embedded server?**  
   *A:* Spring Boot packages Apache Tomcat as an embedded library inside the JAR, allowing applications to run with `java -jar` without installing a standalone web server.

### Experiment 10: JSP & JDBC Login Validation
1. **Q: What is JSP? How does it differ from a Servlet?**  
   *A:* JSP is a document-centric technology where HTML is primary and Java code is embedded. Servlets are Java classes where Java is primary and HTML is generated in code. The web container compiles JSPs into Servlets automatically.
2. **Q: Why must `PreparedStatement` be used instead of `Statement`?**  
   *A:* `PreparedStatement` pre-compiles SQL and parameterizes values (`?`), preventing SQL injection attacks and improving performance through statement caching.
3. **Q: What is the purpose of `Class.forName("com.mysql.cj.jdbc.Driver")`?**  
   *A:* It dynamically loads the database driver class into the JVM and registers it with the `DriverManager`.
4. **Q: What is a `ResultSet` cursor?**  
   *A:* A pointer that initially sits before the first row of query results. `rs.next()` moves the cursor to the next row and returns `true` if a row exists.
5. **Q: Why is `try-with-resources` important in JDBC?**  
   *A:* Database connections, statements, and result sets consume external OS sockets and server handles. `try-with-resources` guarantees that `close()` is invoked even if an exception occurs.

### Experiment 11: Java Swing Desktop App
1. **Q: What is Java Swing? Is it heavyweight or lightweight?**  
   *A:* Swing is a Java GUI toolkit built on top of AWT. It is lightweight because its components are written entirely in Java without platform-specific native peers.
2. **Q: What is `DefaultTableModel` in `JTable`?**  
   *A:* An implementation of `TableModel` that stores table data in Vectors of Vectors and notifies `JTable` when rows are added, updated, or removed.
3. **Q: What is an `ActionListener`?**  
   *A:* An interface in `java.awt.event` with the method `actionPerformed(ActionEvent e)`. It receives notification when an action event (like a button click) occurs.
4. **Q: What is the Event Dispatch Thread (EDT)?**  
   *A:* The background thread where Swing handles all GUI rendering and event callbacks. Swing components must be manipulated on the EDT via `SwingUtilities.invokeLater()`.
5. **Q: What layout manager was used in `StudentSwingApp`?**  
   *A:* `BorderLayout` for the main frame (organizing North, Center, South) and `GridLayout(5, 2)` for the student entry form.

---

## 4. Five Simple Live Code Changes You Can Demonstrate to an Examiner

If the examiner asks you to modify code on the fly during your viva:

### Live Change 1: Add a New Engineering Branch
**File:** [`frontend/src/pages/UnifiedDashboard.jsx`](frontend/src/pages/UnifiedDashboard.jsx)  
Find the `<select>` tag inside the Add Student modal and add one option:
```jsx
<option value="Artificial Intelligence & Data Science">AI & Data Science</option>
```
*Effect:* Instantly available in the dropdown without server restart!

### Live Change 2: Adjust Attendance Shortage Warning Threshold
**File:** [`frontend/src/pages/UnifiedDashboard.jsx`](frontend/src/pages/UnifiedDashboard.jsx)  
Change the attendance threshold check from `75%` to `80%`:
```jsx
// Change:
Number(previewAttPct) >= 75 ? 'badge-success' : 'badge-danger'
// To:
Number(previewAttPct) >= 80 ? 'badge-success' : 'badge-danger'
```
*Effect:* Any attendance below 80% immediately displays in red as shortage!

### Live Change 3: Add a New Subject to Database
**File:** [`backend/src/main/resources/data.sql`](backend/src/main/resources/data.sql)  
Add this line:
```sql
INSERT INTO subjects (code, name, credits) VALUES ('CS206', 'Cloud Computing & DevOps', 3);
```
*Effect:* A new academic subject is automatically created and appears in all marks and assignment dropdowns!

### Live Change 4: Modify Grade Calculation Rules in Spring Service
**File:** [`backend/src/main/java/com/edutrack/quiz/QuizService.java`](backend/src/main/java/com/edutrack/quiz/QuizService.java)  
Change the distinction boundary from `85%` to `90%`:
```java
// Change:
String grade = scorePercentage >= 85 ? "A+ (Excellent)"
// To:
String grade = scorePercentage >= 90 ? "A+ (Outstanding)"
```
*Effect:* Demonstrates how Spring Boot business logic changes without touching controllers or frontend!

### Live Change 5: Change Swing App Window Title and Table Header Color
**File:** [`experiments/exp11_swing_student_table/StudentSwingApp.java`](experiments/exp11_swing_student_table/StudentSwingApp.java)  
Change the title in constructor:
```java
setTitle("EduTrack Lite — VIVA DEMONSTRATION 2026");
```
*Effect:* Recompile with `javac experiments\exp11_swing_student_table\StudentSwingApp.java` and show the immediate updated desktop title bar!
