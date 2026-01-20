package com.umt.sprint2.ui;

import com.umt.sprint2.logic.AuthException;
import com.umt.sprint2.logic.AuthService;
import com.umt.sprint2.model.User;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.function.Consumer;

public class LoginController {
    private final VBox root = new VBox(12);

    public LoginController(AuthService authService, Consumer<User> onLoginSuccess) {
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Login");
        title.getStyleClass().add("title");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username (e.g. s001)");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password (e.g. 1234)");

        Label message = new Label();
        message.setWrapText(true);

        Button loginBtn = new Button("Log in");
        loginBtn.setDefaultButton(true);

        loginBtn.setOnAction(evt -> {
            String u = usernameField.getText() == null ? "" : usernameField.getText().trim();
            String p = passwordField.getText() == null ? "" : passwordField.getText();
            if (u.isEmpty() || p.isEmpty()) {
                message.setText("Please enter username and password.");
                return;
            }
            try {
                User user = authService.login(u, p);
                message.setText("");
                onLoginSuccess.accept(user);
            } catch (AuthException e) {
                message.setText(e.getMessage());
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPrefWidth(110);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().addAll(c1, c2);

        form.add(new Label("Username:"), 0, 0);
        form.add(usernameField, 1, 0);
        form.add(new Label("Password:"), 0, 1);
        form.add(passwordField, 1, 1);

        Label demo = new Label("Demo accounts:\n- s001 / 1234\n- s002 / 1234\n- admin / admin");
        demo.setOpacity(0.85);

        root.getChildren().addAll(title, form, loginBtn, message, new Separator(), demo);
    }

    public Parent getView() {
        return root;
    }
}
