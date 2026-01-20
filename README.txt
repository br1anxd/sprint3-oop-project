Sprint 2 Update - JavaFX Enrollment App

What you can do end-to-end (GUI workflow):
1) Run the JavaFX app.
2) Login as a student (s001/1234 or s002/1234).
3) On the Dashboard, select a course and click "Enroll".
4) You immediately see the result in "My Enrollments".
5) Close the app and run it again: your enrollments are restored (file persistence).

Architecture / Separation of Concerns:
- com.umt.sprint2.ui     -> JavaFX views/controllers + navigation
- com.umt.sprint2.logic  -> services + use-case rules (AuthService, EnrollmentService)
- com.umt.sprint2.data   -> repositories + file persistence (DataStore, FilePersistence, repo/*)
- com.umt.sprint2.model  -> domain models (Student, Course, User, etc.)

Collections usage:
- HashMap for fast lookup: usersByUsername, studentsById, coursesByCode
- List for ordered enrollments: enrollmentsByStudentId -> List<String>

Persistence:
- Data saved to: data/app-data.ser

Run notes:
- JavaFX must be available on your machine (e.g., OpenJFX).
- Example run (adjust paths):
  javac --module-path <path_to_javafx_lib> --add-modules javafx.controls -d out $(find src/main/java -name "*.java")
  java  --module-path <path_to_javafx_lib> --add-modules javafx.controls -cp out com.umt.sprint2.ui.MainApp
