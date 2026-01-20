package com.umt.sprint2.ui;

import com.umt.sprint2.data.repo.StudentRepository;
import com.umt.sprint2.logic.AdminService;
import com.umt.sprint2.logic.AuthService;
import com.umt.sprint2.logic.EnrollmentService;
import com.umt.sprint2.model.Role;
import com.umt.sprint2.model.User;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
    private final Stage stage;
    private final AuthService authService;
    private final EnrollmentService enrollmentService;
    private final AdminService adminService;
    private final StudentRepository studentRepo;

    private User currentUser;

    public SceneManager(Stage stage,
                        AuthService authService,
                        EnrollmentService enrollmentService,
                        AdminService adminService,
                        StudentRepository studentRepo) {
        this.stage = stage;
        this.authService = authService;
        this.enrollmentService = enrollmentService;
        this.adminService = adminService;
        this.studentRepo = studentRepo;
    }

    public void showLogin() {
        currentUser = null;
        LoginController controller = new LoginController(authService, user -> {
            this.currentUser = user;
            if (user.getRole() == Role.ADMIN) {
                showAdminDashboard();
            } else {
                showStudentDashboard();
            }
        });
        Scene scene = new Scene(controller.getView(), 560, 360);
        applyStyles(scene);
        stage.setScene(scene);
    }

    public void showStudentDashboard() {
        StudentDashboardController controller = new StudentDashboardController(currentUser, enrollmentService, studentRepo, this::showLogin);
        Scene scene = new Scene(controller.getView(), 940, 560);
        applyStyles(scene);
        stage.setScene(scene);
    }

    public void showAdminDashboard() {
        AdminDashboardController controller = new AdminDashboardController(adminService, this::showLogin);
        Scene scene = new Scene(controller.getView(), 980, 620);
        applyStyles(scene);
        stage.setScene(scene);
    }

    private void applyStyles(Scene scene) {
        try {
            var url = getClass().getResource("/styles/app.css");
            if (url != null) {
                scene.getStylesheets().add(url.toExternalForm());
            }
        } catch (Exception ignored) {
            // If CSS isn't on classpath, the app still works.
        }
    }
}
