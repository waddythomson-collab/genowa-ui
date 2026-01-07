package com.genowa.ui.screens;

import com.genowa.service.DatabaseService;
import com.genowa.ui.GenowaApp;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class MainScreen
{
    private BorderPane view;
    private TabPane tabPane;
    private Label statusLabel;

    public MainScreen()
    {
        createView();
    }

    private void createView()
    {
        view = new BorderPane();

        // Menu Bar
        MenuBar menuBar = createMenuBar();
        view.setTop(menuBar);

        // Tab Pane
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);

        // Create tabs
        Tab tablesTab = new Tab("Tables");
        tablesTab.setClosable(false);
        TablesScreen tablesScreen = new TablesScreen(DatabaseService.getInstance());
        tablesTab.setContent(tablesScreen.getView());

        Tab insLineTab = new Tab("Ins Line Table Assign");
        insLineTab.setClosable(false);
        InsLineTableAssignScreen insLineScreen = new InsLineTableAssignScreen();
        insLineTab.setContent(insLineScreen.getView());

        tabPane.getTabs().addAll(tablesTab, insLineTab);
        view.setCenter(tabPane);

        // Status Bar
        HBox statusBar = createStatusBar();
        view.setBottom(statusBar);
    }

    private MenuBar createMenuBar()
    {
        MenuBar menuBar = new MenuBar();

        // File Menu
        Menu fileMenu = new Menu("File");
        MenuItem logoutItem = new MenuItem("Logout");
        logoutItem.setOnAction(e -> GenowaApp.showLoginScreen());
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().addAll(logoutItem, new SeparatorMenuItem(), exitItem);

        // Help Menu
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());
        helpMenu.getItems().add(aboutItem);

        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;
    }

    private HBox createStatusBar()
    {
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        statusBar.setStyle("-fx-background-color: #e0e0e0;");

        statusLabel = new Label("User: " + GenowaApp.getCurrentUser() + " [" + GenowaApp.getCurrentUserRole() + "]");
        statusBar.getChildren().add(statusLabel);

        return statusBar;
    }

    private void showAboutDialog()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Genowa");
        alert.setHeaderText("Genowa - Code Generation Tool");
        alert.setContentText("Version 1.0\n\nA unified code generation tool for APG and Rating.");
        alert.showAndWait();
    }

    public BorderPane getView()
    {
        return view;
    }
}
