package com.genowa.ui;

import com.genowa.service.DatabaseService;
import com.genowa.ui.screens.LoginScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GenowaApp extends Application
{
    private static Stage primaryStage;
    private static String currentUser;
    private static String currentUserRole;

    @Override
    public void start(Stage stage)
    {
        primaryStage = stage;
        primaryStage.setTitle("Genowa - Code Generation Tool");
        
        showLoginScreen();
        
        primaryStage.show();
    }

    public static void showLoginScreen()
    {
        LoginScreen loginScreen = new LoginScreen();
        Scene scene = new Scene(loginScreen.getView(), 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setWidth(400);
        primaryStage.setHeight(300);
        primaryStage.centerOnScreen();
    }

    public static void showMainScreen()
    {
        com.genowa.ui.screens.MainScreen mainScreen = new com.genowa.ui.screens.MainScreen();
        Scene scene = new Scene(mainScreen.getView(), 1024, 768);
        primaryStage.setScene(scene);
        primaryStage.setWidth(1024);
        primaryStage.setHeight(768);
        primaryStage.centerOnScreen();
    }

    public static Stage getPrimaryStage()
    {
        return primaryStage;
    }

    public static void setCurrentUser(String username, String role)
    {
        currentUser = username;
        currentUserRole = role;
    }

    public static String getCurrentUser()
    {
        return currentUser;
    }

    public static String getCurrentUserRole()
    {
        return currentUserRole;
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
