package genowa.ui;

import genowa.Database;
import genowa.ui.components.DockablePane;
import genowa.ui.components.ElementPicker;
import genowa.ui.screens.DatabaseElementScreen;
import genowa.ui.screens.InsLineTableAssignScreen;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

/**
 * Main application for Genowa - Insurance Code Generation Tool
 */
public class GenowaApp extends Application {

    private TabPane mainTabPane;
    private DockablePane elementPickerPane;

    @Override
    public void start(Stage primaryStage) {
        // Initialize database
        try {
            Database.getInstance().initialize();
        } catch (Exception e) {
            showAlert("Database Error", "Failed to connect to database:\n" + e.getMessage());
            e.printStackTrace();
        }
        
        primaryStage.setTitle("Genowa - Code Generation Tool");

        // Create main layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #e8e8e8;");

        // Create Element Picker with dockable pane FIRST (menu bar depends on it)
        ElementPicker elementPicker = new ElementPicker();
        elementPicker.setOnElementSelected(element -> {
            showAlert("Element Selected", 
                "Selected: " + element.getName() + "\n" +
                "Type: " + element.getType().getDisplayName() + "\n" +
                "Description: " + element.getDescription());
        });

        elementPickerPane = new DockablePane("Elements", elementPicker);
        elementPickerPane.setParentContainer(root);

        // Menu bar with atom button
        VBox topContainer = new VBox();
        topContainer.getChildren().add(createMenuBar());
        topContainer.getChildren().add(createMainToolbar());
        root.setTop(topContainer);

        // Main content area with tabs
        mainTabPane = new TabPane();
        mainTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);

        // Create screens
        Tab dbElementsTab = new Tab("Database Elements", new DatabaseElementScreen());
        dbElementsTab.setClosable(false);

        Tab algorithmsTab = new Tab("Algorithms", createAlgorithmScreen());
        algorithmsTab.setClosable(false);

        Tab conditionalsTab = new Tab("Conditionals", createConditionalScreen());
        conditionalsTab.setClosable(false);
        
        Tab tableAssignTab = new Tab("Table Assignment", new InsLineTableAssignScreen());
        tableAssignTab.setClosable(false);
        
        Tab generationTab = new Tab("Code Generation", new genowa.ui.screens.GenerationScreen());
        generationTab.setClosable(false);

        mainTabPane.getTabs().addAll(dbElementsTab, algorithmsTab, conditionalsTab, tableAssignTab, generationTab);

        // Center content with element picker on right
        root.setCenter(mainTabPane);
        root.setRight(elementPickerPane);

        // Status bar
        root.setBottom(createStatusBar());

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private ToolBar createMainToolbar() {
        ToolBar toolbar = new ToolBar();
        
        // Atom button to show/hide Element Picker
        Button atomButton = new Button("\u269B"); // ⚛ atom symbol
        atomButton.setTooltip(new Tooltip("Show Element Picker"));
        atomButton.setStyle("-fx-font-size: 16px;");
        atomButton.setOnAction(e -> {
            if (elementPickerPane.isVisible()) {
                elementPickerPane.hide();
            } else {
                elementPickerPane.dock();
            }
        });
        
        // Standard toolbar buttons
        Button newBtn = new Button("New");
        Button openBtn = new Button("Open");
        Button saveBtn = new Button("Save");
        
        Separator sep1 = new Separator(Orientation.VERTICAL);
        
        Button generateBtn = new Button("Generate");
        generateBtn.setStyle("-fx-background-color: #4a90d9; -fx-text-fill: white;");
        Button compileBtn = new Button("Compile");
        
        Separator sep2 = new Separator(Orientation.VERTICAL);
        
        Button testBtn = new Button("Test");
        
        // Spacer to push atom button to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        toolbar.getItems().addAll(
            newBtn, openBtn, saveBtn,
            sep1,
            generateBtn, compileBtn,
            sep2,
            testBtn,
            spacer,
            atomButton
        );
        
        return toolbar;
    }
    
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        // File menu
        Menu fileMenu = new Menu("File");
        MenuItem newItem = new MenuItem("New...");
        MenuItem openItem = new MenuItem("Open...");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem saveAsItem = new MenuItem("Save As...");
        SeparatorMenuItem sep1 = new SeparatorMenuItem();
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().addAll(newItem, openItem, saveItem, saveAsItem, sep1, exitItem);

        // Edit menu
        Menu editMenu = new Menu("Edit");
        MenuItem undoItem = new MenuItem("Undo");
        MenuItem redoItem = new MenuItem("Redo");
        SeparatorMenuItem sep2 = new SeparatorMenuItem();
        MenuItem cutItem = new MenuItem("Cut");
        MenuItem copyItem = new MenuItem("Copy");
        MenuItem pasteItem = new MenuItem("Paste");
        editMenu.getItems().addAll(undoItem, redoItem, sep2, cutItem, copyItem, pasteItem);

        // View menu
        Menu viewMenu = new Menu("View");
        CheckMenuItem showElementsItem = new CheckMenuItem("Element Picker");
        showElementsItem.setSelected(true);
        showElementsItem.setOnAction(e -> {
            if (showElementsItem.isSelected()) {
                elementPickerPane.dock();
            } else {
                elementPickerPane.hide();
            }
        });
        
        // Sync menu checkbox when element picker is hidden/shown externally
        elementPickerPane.visibleProperty().addListener((obs, wasVisible, isVisible) -> {
            showElementsItem.setSelected(isVisible);
        });
        
        viewMenu.getItems().addAll(showElementsItem);

        // Tools menu
        Menu toolsMenu = new Menu("Tools");
        MenuItem generateItem = new MenuItem("Generate Code...");
        MenuItem compileItem = new MenuItem("Compile Module...");
        MenuItem testItem = new MenuItem("Test Panel...");
        SeparatorMenuItem sep3 = new SeparatorMenuItem();
        MenuItem optionsItem = new MenuItem("Options...");
        toolsMenu.getItems().addAll(generateItem, compileItem, testItem, sep3, optionsItem);

        // Help menu
        Menu helpMenu = new Menu("Help");
        MenuItem helpTopicsItem = new MenuItem("Help Topics");
        MenuItem aboutItem = new MenuItem("About Genowa");
        aboutItem.setOnAction(e -> showAbout());
        helpMenu.getItems().addAll(helpTopicsItem, aboutItem);

        menuBar.getMenus().addAll(fileMenu, editMenu, viewMenu, toolsMenu, helpMenu);
        return menuBar;
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        statusBar.setStyle("-fx-background-color: #d0d0d0; -fx-border-color: #b0b0b0; -fx-border-width: 1 0 0 0;");

        Label statusLabel = new Label("Ready");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label versionLabel = new Label("Genowa v1.0");

        statusBar.getChildren().addAll(statusLabel, spacer, versionLabel);
        return statusBar;
    }

    private BorderPane createAlgorithmScreen() {
        BorderPane screen = new BorderPane();
        screen.setPadding(new Insets(10));

        // Header
        VBox header = createAlgorithmHeader();
        screen.setTop(header);

        // Grid placeholder
        TableView<String> grid = new TableView<>();
        grid.setPlaceholder(new Label("Algorithm Editor - Coming Soon"));
        screen.setCenter(grid);

        // Toolbar
        HBox toolbar = createAlgorithmToolbar();
        screen.setBottom(toolbar);

        return screen;
    }

    private VBox createAlgorithmHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(0, 0, 10, 0));

        // Title
        Label title = new Label("Algorithm Editor");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));

        // Dates row
        HBox datesRow = new HBox(20);
        datesRow.setAlignment(Pos.CENTER_LEFT);

        Label effLabel = new Label("Effective:");
        DatePicker effDate = new DatePicker();
        Label expLabel = new Label("Expiration:");
        DatePicker expDate = new DatePicker();

        Label midTermLabel = new Label("Mid-term Code:");
        ComboBox<String> midTermCombo = new ComboBox<>();
        midTermCombo.getItems().addAll("N/A", "Pro-rata", "Short-rate");

        Label covLevelLabel = new Label("Coverage Level:");
        ComboBox<String> covLevelCombo = new ComboBox<>();
        covLevelCombo.getItems().addAll("Policy", "Vehicle", "Driver", "Coverage");

        datesRow.getChildren().addAll(
            effLabel, effDate, expLabel, expDate,
            midTermLabel, midTermCombo, covLevelLabel, covLevelCombo
        );

        // Comments
        HBox commentsRow = new HBox(10);
        Label commentsLabel = new Label("Comments:");
        TextField commentsField = new TextField();
        commentsField.setPrefWidth(500);
        commentsRow.getChildren().addAll(commentsLabel, commentsField);

        header.getChildren().addAll(title, datesRow, commentsRow);
        return header;
    }

    private HBox createAlgorithmToolbar() {
        HBox toolbar = new HBox(5);
        toolbar.setPadding(new Insets(10, 0, 0, 0));
        toolbar.setAlignment(Pos.CENTER_LEFT);

        String[] operators = {"+", "−", "×", "/", "=", "**"};
        for (String op : operators) {
            Button btn = new Button(op);
            btn.setPrefWidth(40);
            btn.setStyle("-fx-font-weight: bold;");
            toolbar.getChildren().add(btn);
        }

        toolbar.getChildren().add(new Separator(Orientation.VERTICAL));

        Button codeLiteralBtn = new Button("Code Literal");
        Button insertBtn = new Button("Insert");
        Button testBtn = new Button("Test");
        Button saveBtn = new Button("Save");

        toolbar.getChildren().addAll(codeLiteralBtn, insertBtn, testBtn, saveBtn);

        return toolbar;
    }

    private BorderPane createConditionalScreen() {
        BorderPane screen = new BorderPane();
        screen.setPadding(new Insets(10));

        // Header
        VBox header = new VBox(10);
        header.setPadding(new Insets(0, 0, 10, 0));

        Label title = new Label("Conditional Editor");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));

        HBox nameRow = new HBox(10);
        Label nameLabel = new Label("Conditional Name:");
        TextField nameField = new TextField();
        nameField.setPrefWidth(300);
        Label commentsLabel = new Label("Comments:");
        TextField commentsField = new TextField();
        commentsField.setPrefWidth(400);
        nameRow.getChildren().addAll(nameLabel, nameField, commentsLabel, commentsField);

        header.getChildren().addAll(title, nameRow);
        screen.setTop(header);

        // Grid placeholder
        TableView<String> grid = new TableView<>();
        grid.setPlaceholder(new Label("Conditional Editor - Coming Soon"));
        screen.setCenter(grid);

        // Toolbars
        VBox toolbars = createConditionalToolbars();
        screen.setBottom(toolbars);

        return screen;
    }

    private VBox createConditionalToolbars() {
        VBox toolbars = new VBox(5);
        toolbars.setPadding(new Insets(10, 0, 0, 0));

        // Operators row
        HBox row1 = new HBox(5);
        String[] operators = {"+", "−", "×", "/", "=", "**"};
        for (String op : operators) {
            Button btn = new Button(op);
            btn.setPrefWidth(40);
            btn.setStyle("-fx-font-weight: bold;");
            row1.getChildren().add(btn);
        }
        row1.getChildren().add(new Separator(Orientation.VERTICAL));
        Button codeLiteralBtn = new Button("Code Literal");
        Button insertBtn = new Button("Insert");
        Button testBtn = new Button("Test");
        Button saveBtn = new Button("Save");
        row1.getChildren().addAll(codeLiteralBtn, insertBtn, testBtn, saveBtn);

        // Conditional operators row
        HBox row2 = new HBox(5);
        String[] compOps = {"<", ">", "≤", "≥"};
        for (String op : compOps) {
            Button btn = new Button(op);
            btn.setPrefWidth(40);
            row2.getChildren().add(btn);
        }
        row2.getChildren().add(new Separator(Orientation.VERTICAL));

        Button openParenBtn = new Button("(");
        Button closeParenBtn = new Button(")");
        row2.getChildren().addAll(openParenBtn, closeParenBtn);

        row2.getChildren().add(new Separator(Orientation.VERTICAL));

        String[] keywords = {"When", "Other When", "Otherwise", "Use", "Equals", "Not", "And", "Or"};
        for (String kw : keywords) {
            Button btn = new Button(kw);
            btn.setStyle("-fx-background-color: #4a90d9; -fx-text-fill: white;");
            row2.getChildren().add(btn);
        }

        toolbars.getChildren().addAll(row1, row2);
        return toolbars;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAbout() {
        Alert about = new Alert(Alert.AlertType.INFORMATION);
        about.setTitle("About Genowa");
        about.setHeaderText("Genowa - Insurance Code Generation Tool");
        about.setContentText(
            "Version 1.0\n\n" +
            "A modern replacement for legacy IBM VisualAge\n" +
            "COBOL/Java code generation tools.\n\n" +
            "Built with JavaFX 21"
        );
        about.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
