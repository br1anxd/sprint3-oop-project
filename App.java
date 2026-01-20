package com.umt.sprint2.ui;

import com.umt.sprint2.data.DataStore;
import com.umt.sprint2.data.FilePersistence;
import com.umt.sprint2.data.repo.CourseRepository;
import com.umt.sprint2.data.repo.EnrollmentRepository;
import com.umt.sprint2.data.repo.StudentRepository;
import com.umt.sprint2.data.repo.UserRepository;
import com.umt.sprint2.logic.AuthService;
import com.umt.sprint2.logic.AdminService;
import com.umt.sprint2.logic.Bootstrap;
import com.umt.sprint2.logic.EnrollmentService;

import javafx.application.Application;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.nio.file.Paths;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        Path dataFile = Paths.get("data", "app-data.ser");
        FilePersistence persistence = new FilePersistence(dataFile);
        DataStore store = persistence.loadOrCreate();
        Bootstrap.seedIfEmpty(store, persistence);

        // Data layer
        UserRepository userRepo = new UserRepository(store);
        StudentRepository studentRepo = new StudentRepository(store);
        CourseRepository courseRepo = new CourseRepository(store);
        EnrollmentRepository enrollmentRepo = new EnrollmentRepository(store);

        // Logic layer
        AuthService authService = new AuthService(userRepo);
        EnrollmentService enrollmentService = new EnrollmentService(store, studentRepo, courseRepo, enrollmentRepo, persistence);
        AdminService adminService = new AdminService(store, userRepo, studentRepo, courseRepo, enrollmentRepo, persistence);

        SceneManager sceneManager = new SceneManager(primaryStage, authService, enrollmentService, adminService, studentRepo);
        primaryStage.setTitle("UMT Enrollment - Sprint 3");
        sceneManager.showLogin();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
