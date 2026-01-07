package com.genowa.ui.screens;

import com.genowa.service.DatabaseService;
import com.genowa.ui.GenowaApp;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginScreen
{
    private TextField usernameField;
    private PasswordField passwordField;
    private Label errorLabel;

    public LoginScreen()
    {
    }

    public Parent getView()
    {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f5f5f5;");

        Label titleLabel = new Label("Genowa");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 32));

        Label subtitleLabel = new Label("Code Generation Tool");
        subtitleLabel.setFont(Font.font(14));

        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(200);

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(200);

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        loginButton.setOnAction(e -> handleLogin());

        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        root.getChildren().addAll(titleLabel, subtitleLabel, usernameField, passwordField, loginButton, errorLabel);

        return root;
    }

    private void handleLogin()
    {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Check using DatabaseService
        if (DatabaseService.getInstance().validateLogin(username, password))
        {
            String role = DatabaseService.getInstance().getUserRole(username);
            GenowaApp.setCurrentUser(username, role);
            GenowaApp.showMainScreen();
        }
        else
        {
            errorLabel.setText("Invalid username or password");
        }
    }
}
