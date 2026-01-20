package com.umt.sprint2.ui;

import com.umt.sprint2.data.repo.StudentRepository;
import com.umt.sprint2.logic.EnrollmentException;
import com.umt.sprint2.logic.EnrollmentService;
import com.umt.sprint2.model.Course;
import com.umt.sprint2.model.Student;
import com.umt.sprint2.model.User;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

/**
 * Student end-to-end workflow screen:
 * Login -> Browse courses -> Enroll/Unenroll -> Persist -> Restore.
 */
public class StudentDashboardController {
    private final BorderPane root = new BorderPane();

    public StudentDashboardController(User currentUser,
                                     EnrollmentService enrollmentService,
                                     StudentRepository studentRepo,
                                     Runnable onLogout) {
        root.setPadding(new Insets(14));

        // Top bar
        Label title = new Label("Student Dashboard");
        title.getStyleClass().add("title");
        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("danger");
        logoutBtn.setOnAction(e -> onLogout.run());
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox top = new HBox(10, title, spacer, logoutBtn);
        top.setAlignment(Pos.CENTER_LEFT);
        root.setTop(top);

        String studentId = currentUser.getStudentId();
        Student student = studentRepo.findById(studentId).orElse(null);
        String displayName = student != null ? student.getFullName() : studentId;

        Label welcome = new Label("Welcome, " + displayName + " (" + currentUser.getUsername() + ")");
        welcome.getStyleClass().add("subtitle");

        Label status = new Label();
        status.getStyleClass().add("status");
        status.setWrapText(true);

        // All courses + search
        List<Course> allCourses = enrollmentService.listAllCourses();
        FilteredList<Course> filtered = new FilteredList<>(FXCollections.observableArrayList(allCourses), c -> true);

        TextField search = new TextField();
        search.setPromptText("Search courses (code or name)...");
        search.textProperty().addListener((obs, oldV, newV) -> {
            String q = newV == null ? "" : newV.trim().toLowerCase();
            filtered.setPredicate(c -> q.isEmpty()
                    || c.getCode().toLowerCase().contains(q)
                    || c.getName().toLowerCase().contains(q));
        });

        ListView<Course> allCoursesList = new ListView<>(filtered);

        // My enrollments
        ListView<Course> myCoursesList = new ListView<>();
        Runnable refreshMy = () -> refreshMyCourses(enrollmentService, myCoursesList, studentId);
        refreshMy.run();

        Button enrollBtn = new Button("Enroll");
        enrollBtn.setOnAction(e -> {
            Course selected = allCoursesList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                status.setText("Please select a course first.");
                return;
            }
            try {
                enrollmentService.enroll(studentId, selected.getCode());
                status.setText("Enrolled in " + selected.getCode() + ". Saved to disk.");
                refreshMy.run();
            } catch (EnrollmentException ex) {
                status.setText(ex.getMessage());
            }
        });

        Button unenrollBtn = new Button("Unenroll");
        unenrollBtn.getStyleClass().add("secondary");
        unenrollBtn.setOnAction(e -> {
            Course selected = myCoursesList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                status.setText("Select a course from 'My Enrollments' to unenroll.");
                return;
            }
            try {
                enrollmentService.unenroll(studentId, selected.getCode());
                status.setText("Unenrolled from " + selected.getCode() + ". Saved to disk.");
                refreshMy.run();
            } catch (EnrollmentException ex) {
                status.setText(ex.getMessage());
            }
        });

        HBox actions = new HBox(10, enrollBtn, unenrollBtn);
        actions.setAlignment(Pos.CENTER_LEFT);

        VBox left = new VBox(10,
                new Label("All Courses"),
                search,
                allCoursesList,
                actions
        );
        left.getStyleClass().add("card");
        left.setPrefWidth(420);

        VBox right = new VBox(10,
                new Label("My Enrollments"),
                myCoursesList,
                new Label("Tip: close and reopen the app to see persistence working.")
        );
        right.getStyleClass().add("card");
        right.setPrefWidth(420);

        HBox center = new HBox(14, left, right);
        root.setCenter(new VBox(12, welcome, center));

        root.setBottom(status);
        BorderPane.setMargin(status, new Insets(12, 0, 0, 0));
    }

    private void refreshMyCourses(EnrollmentService service, ListView<Course> myCoursesList, String studentId) {
        List<Course> myCourses = service.listCoursesForStudent(studentId);
        myCoursesList.setItems(FXCollections.observableArrayList(myCourses));
    }

    public Parent getView() {
        return root;
    }
}
