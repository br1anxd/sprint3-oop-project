package com.umt.sprint2.ui;

import com.umt.sprint2.logic.AdminException;
import com.umt.sprint2.logic.AdminService;
import com.umt.sprint2.model.Course;
import com.umt.sprint2.model.Student;
import com.umt.sprint2.model.User;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Admin dashboard (Sprint 3): manage data and view results through the GUI.
 */
public class AdminDashboardController {
    private final BorderPane root = new BorderPane();
    private final Label status = new Label();

    public AdminDashboardController(AdminService adminService, Runnable onLogout) {
        root.setPadding(new Insets(14));

        Label title = new Label("Admin Dashboard");
        title.getStyleClass().add("title");

        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("danger");
        logoutBtn.setOnAction(e -> onLogout.run());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox top = new HBox(10, title, spacer, logoutBtn);
        top.setAlignment(Pos.CENTER_LEFT);
        root.setTop(top);

        status.getStyleClass().add("status");
        status.setWrapText(true);
        root.setBottom(status);
        BorderPane.setMargin(status, new Insets(12, 0, 0, 0));

        TabPane tabs = new TabPane();
        tabs.getTabs().addAll(
                buildCoursesTab(adminService),
                buildStudentsTab(adminService),
                buildUsersTab(adminService),
                buildEnrollmentsTab(adminService),
                buildReportsTab(adminService)
        );
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        root.setCenter(tabs);
    }

    private Tab buildCoursesTab(AdminService adminService) {
        Tab tab = new Tab("Courses");

        TableView<Course> table = new TableView<>();
        TableColumn<Course, String> codeCol = new TableColumn<>("Code");
        codeCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCode()));
        codeCol.setPrefWidth(140);

        TableColumn<Course, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        nameCol.setPrefWidth(420);

        table.getColumns().addAll(codeCol, nameCol);

        TextField code = new TextField();
        code.setPromptText("Code (e.g., OOP101)");
        TextField name = new TextField();
        name.setPromptText("Course name");

        Button addUpdate = new Button("Add / Update");
        addUpdate.setOnAction(e -> {
            try {
                adminService.createOrUpdateCourse(code.getText(), name.getText());
                status.setText("Saved course. Data persisted.");
                refreshCourses(adminService, table);
                code.clear();
                name.clear();
            } catch (AdminException ex) {
                status.setText(ex.getMessage());
            }
        });

        Button delete = new Button("Delete Selected");
        delete.getStyleClass().add("danger");
        delete.setOnAction(e -> {
            Course selected = table.getSelectionModel().getSelectedItem();
            try {
                adminService.deleteCourse(selected == null ? "" : selected.getCode());
                status.setText("Deleted course and cleaned related enrollments.");
                refreshCourses(adminService, table);
            } catch (AdminException ex) {
                status.setText(ex.getMessage());
            }
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                code.setText(newV.getCode());
                name.setText(newV.getName());
            }
        });

        HBox form = new HBox(10, code, name, addUpdate, delete);
        form.setAlignment(Pos.CENTER_LEFT);
        VBox box = new VBox(12, new Label("Manage courses (add, update, delete)"), table, form);
        box.getStyleClass().add("card");
        box.setPadding(new Insets(12));
        tab.setContent(box);

        refreshCourses(adminService, table);
        return tab;
    }

    private void refreshCourses(AdminService adminService, TableView<Course> table) {
        table.setItems(FXCollections.observableArrayList(adminService.listCourses()));
    }

    private Tab buildStudentsTab(AdminService adminService) {
        Tab tab = new Tab("Students");

        TableView<Student> table = new TableView<>();
        TableColumn<Student, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getId()));
        idCol.setPrefWidth(120);
        TableColumn<Student, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFullName()));
        nameCol.setPrefWidth(260);
        TableColumn<Student, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));
        emailCol.setPrefWidth(260);
        table.getColumns().addAll(idCol, nameCol, emailCol);

        TextField studentId = new TextField();
        studentId.setPromptText("Student ID (e.g., S003)");
        TextField fullName = new TextField();
        fullName.setPromptText("Full name");
        TextField email = new TextField();
        email.setPromptText("Email");
        TextField username = new TextField();
        username.setPromptText("Username (e.g., s003)");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        Button create = new Button("Create Student Account");
        create.setOnAction(e -> {
            try {
                adminService.createStudentAccount(studentId.getText(), fullName.getText(), email.getText(), username.getText(), password.getText());
                status.setText("Student + user account created. Data persisted.");
                refreshStudents(adminService, table);
                studentId.clear();
                fullName.clear();
                email.clear();
                username.clear();
                password.clear();
            } catch (AdminException ex) {
                status.setText(ex.getMessage());
            }
        });

        Button delete = new Button("Delete Selected Student");
        delete.getStyleClass().add("danger");
        delete.setOnAction(e -> {
            Student s = table.getSelectionModel().getSelectedItem();
            try {
                adminService.deleteStudentAndUser(s == null ? "" : s.getId());
                status.setText("Deleted student, linked user, and their enrollments.");
                refreshStudents(adminService, table);
            } catch (AdminException ex) {
                status.setText(ex.getMessage());
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.add(new Label("Student ID"), 0, 0);
        form.add(studentId, 1, 0);
        form.add(new Label("Full name"), 0, 1);
        form.add(fullName, 1, 1);
        form.add(new Label("Email"), 0, 2);
        form.add(email, 1, 2);
        form.add(new Label("Username"), 2, 0);
        form.add(username, 3, 0);
        form.add(new Label("Password"), 2, 1);
        form.add(password, 3, 1);

        HBox actions = new HBox(10, create, delete);
        VBox box = new VBox(12,
                new Label("Create student accounts (student + linked login user)"),
                table,
                form,
                actions
        );
        box.getStyleClass().add("card");
        box.setPadding(new Insets(12));
        tab.setContent(box);

        refreshStudents(adminService, table);
        return tab;
    }

    private void refreshStudents(AdminService adminService, TableView<Student> table) {
        table.setItems(FXCollections.observableArrayList(adminService.listStudents()));
    }

    private Tab buildUsersTab(AdminService adminService) {
        Tab tab = new Tab("Users");

        TableView<User> table = new TableView<>();
        TableColumn<User, String> userCol = new TableColumn<>("Username");
        userCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUsername()));
        userCol.setPrefWidth(180);
        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getRole())));
        roleCol.setPrefWidth(120);
        TableColumn<User, String> sidCol = new TableColumn<>("Student ID");
        sidCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getStudentId() == null ? "" : d.getValue().getStudentId()));
        sidCol.setPrefWidth(140);
        table.getColumns().addAll(userCol, roleCol, sidCol);

        TextField selectedUser = new TextField();
        selectedUser.setPromptText("Selected username");
        selectedUser.setDisable(true);
        PasswordField newPassword = new PasswordField();
        newPassword.setPromptText("New password");
        Button reset = new Button("Reset Password");
        reset.setOnAction(e -> {
            try {
                adminService.resetUserPassword(selectedUser.getText(), newPassword.getText());
                status.setText("Password updated. Data persisted.");
                refreshUsers(adminService, table);
                newPassword.clear();
            } catch (AdminException ex) {
                status.setText(ex.getMessage());
            }
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            selectedUser.setText(newV == null ? "" : newV.getUsername());
        });

        HBox actions = new HBox(10, selectedUser, newPassword, reset);
        VBox box = new VBox(12, new Label("Manage users (reset password)"), table, actions);
        box.getStyleClass().add("card");
        box.setPadding(new Insets(12));
        tab.setContent(box);

        refreshUsers(adminService, table);
        return tab;
    }

    private void refreshUsers(AdminService adminService, TableView<User> table) {
        table.setItems(FXCollections.observableArrayList(adminService.listUsers()));
    }

    private Tab buildEnrollmentsTab(AdminService adminService) {
        Tab tab = new Tab("Enrollments");

        ComboBox<String> studentId = new ComboBox<>();
        studentId.setPrefWidth(160);

        ListView<String> courseCodes = new ListView<>();
        courseCodes.setPrefHeight(280);

        Button refresh = new Button("Refresh");
        Button unenroll = new Button("Force Unenroll");
        unenroll.getStyleClass().add("danger");

        Runnable refreshIds = () -> {
            List<String> ids = new ArrayList<>(adminService.allEnrollmentsView().keySet());
            ids.sort(String::compareTo);
            studentId.setItems(FXCollections.observableArrayList(ids));
        };

        Runnable refreshCourses = () -> {
            String id = studentId.getValue();
            Map<String, List<String>> map = adminService.allEnrollmentsView();
            List<String> codes = map.getOrDefault(id, List.of());
            courseCodes.setItems(FXCollections.observableArrayList(codes));
        };

        refresh.setOnAction(e -> {
            refreshIds.run();
            refreshCourses.run();
            status.setText("Enrollments refreshed.");
        });

        studentId.valueProperty().addListener((obs, o, n) -> refreshCourses.run());

        unenroll.setOnAction(e -> {
            String id = studentId.getValue();
            String code = courseCodes.getSelectionModel().getSelectedItem();
            try {
                adminService.forceUnenroll(id == null ? "" : id, code == null ? "" : code);
                status.setText("Removed enrollment for " + id + " -> " + code);
                refreshIds.run();
                refreshCourses.run();
            } catch (AdminException ex) {
                status.setText(ex.getMessage());
            }
        });

        VBox box = new VBox(12,
                new Label("View and modify enrollments"),
                new HBox(10, new Label("Student ID:"), studentId, refresh),
                courseCodes,
                unenroll
        );
        box.getStyleClass().add("card");
        box.setPadding(new Insets(12));
        tab.setContent(box);

        refreshIds.run();
        return tab;
    }

    private Tab buildReportsTab(AdminService adminService) {
        Tab tab = new Tab("Reports");

        Label courses = new Label();
        Label students = new Label();
        Label users = new Label();
        Label enrollments = new Label();

        Button refresh = new Button("Refresh Report");
        refresh.setOnAction(e -> {
            int courseCount = adminService.listCourses().size();
            int studentCount = adminService.listStudents().size();
            int userCount = adminService.listUsers().size();
            int enrollmentCount = adminService.allEnrollmentsView().values().stream().mapToInt(List::size).sum();
            courses.setText("Courses: " + courseCount);
            students.setText("Students: " + studentCount);
            users.setText("Users: " + userCount);
            enrollments.setText("Total enrollments: " + enrollmentCount);
            status.setText("Report updated.");
        });

        refresh.fire();

        VBox box = new VBox(12,
                new Label("Quick summary"),
                courses, students, users, enrollments,
                new Separator(),
                refresh,
                new Label("This tab demonstrates results and aggregates (useful for Sprint 3 presentation).")
        );
        box.getStyleClass().add("card");
        box.setPadding(new Insets(12));
        tab.setContent(box);
        return tab;
    }

    public Parent getView() {
        return root;
    }
}
