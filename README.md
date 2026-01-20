# Sprint 3 - JavaFX Enrollment System (Admin + Student)

This project is a **complete, GUI-driven JavaFX application** that supports an end-to-end workflow for both **students** and an **admin**, with **data saved and restored across restarts**.

## ✅ Main workflows

### Student workflow (end-to-end)
1. Login (Student account)
2. Browse/search available courses
3. Enroll and/or unenroll through the GUI
4. Close + reopen the app -> enrollments are restored from disk

### Admin workflow (end-to-end)
1. Login (Admin account)
2. Manage courses (add/update/delete)
3. Manage students (create student + linked login user, delete student)
4. Manage users (reset password)
5. View/modify enrollments (force unenroll)
6. View reports (counts + totals)

## Demo accounts
- `s001 / 1234`
- `s002 / 1234`
- `admin / admin`

## Persistence
Data is stored using Java serialization in:
- `data/app-data.ser`

Delete this file to reset demo data.

## Architecture (separation of concerns)
- `com.umt.sprint2.ui` — JavaFX controllers/views + navigation
- `com.umt.sprint2.logic` — services (use cases + rules)
- `com.umt.sprint2.data` + `com.umt.sprint2.data.repo` — repositories + file persistence
- `com.umt.sprint2.model` — domain entities (Student, Course, User, Role)

Collections usage:
- **HashMap**: users by username, students by id, courses by code, enrollments by student id
- **List**: ordered course codes per student (enrollments)

## Running in Eclipse (Windows example)
1. Import as a Java project (File -> New -> Java Project) and point it to this folder.
2. Add JavaFX SDK jars to the build path:
   - Project -> Properties -> Java Build Path -> Libraries -> Add External JARs...
   - Select all jars in: `C:\javafx\javafx-sdk-25.0.1\lib`
3. Run `com.umt.sprint2.ui.App` with VM arguments:

```text
--module-path "C:\javafx\javafx-sdk-25.0.1\lib" --add-modules javafx.controls,javafx.fxml
```

> If you want the CSS styling, ensure `src/main/resources` is on the classpath (Eclipse typically includes it if you import the project as a Maven-style project; otherwise you can right-click `src/main/resources` -> Build Path -> Use as Source Folder).
