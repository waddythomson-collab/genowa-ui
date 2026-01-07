package genowa.ui;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ConditionalScreen extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Conditional Panel");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #C0C0C0;");

        // Header Panel
        VBox headerPanel = createHeaderPanel();
        root.setTop(headerPanel);

        // Main content with table and floaty
        StackPane centerPane = new StackPane();
        TableView<ConditionalLine> table = createConditionalTable();
        centerPane.getChildren().add(table);
        root.setCenter(centerPane);

        // Bottom Toolbar (two rows)
        VBox toolbar = createToolbar();
        root.setBottom(toolbar);

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Show the element picker floaty
        showElementPicker(primaryStage);
    }

    private VBox createHeaderPanel() {
        VBox header = new VBox(5);
        header.setPadding(new Insets(10));
        header.setStyle("-fx-background-color: #C0C0C0;");

        // Conditional Name
        HBox nameRow = new HBox(5);
        nameRow.setAlignment(Pos.CENTER_LEFT);
        Label nameLbl = new Label("Conditional Name:");
        nameLbl.setStyle("-fx-font-weight: bold;");
        nameRow.getChildren().add(nameLbl);

        TextField nameField = new TextField("Determine Auto Insurance Score Cap Factor");
        nameField.setPrefWidth(400);
        nameField.setStyle("-fx-background-color: white;");

        // Comments
        HBox commentsRow = new HBox(5);
        Label commentsLbl = new Label("Comments:");
        commentsLbl.setStyle("-fx-font-weight: bold;");
        TextField commentsField = new TextField();
        commentsField.setPrefWidth(400);
        commentsRow.getChildren().addAll(commentsLbl, commentsField);

        // Centered "Conditionals" label
        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER);
        Label titleLbl = new Label("Conditionals");
        titleLbl.setStyle("-fx-font-size: 14px;");
        titleRow.getChildren().add(titleLbl);

        header.getChildren().addAll(nameRow, nameField, commentsRow, titleRow);
        return header;
    }

    private TableView<ConditionalLine> createConditionalTable() {
        TableView<ConditionalLine> table = new TableView<>();
        table.setStyle("-fx-background-color: #FFFFFF;");

        // Operand column (WHEN, GREATER THAN, USE, OTHERWISE)
        TableColumn<ConditionalLine, String> operandCol = new TableColumn<>("");
        operandCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().operand));
        operandCol.setPrefWidth(120);
        operandCol.setStyle("-fx-text-fill: #000080; -fx-font-weight: bold;");

        // Element column
        TableColumn<ConditionalLine, String> elemCol = new TableColumn<>("");
        elemCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().element));
        elemCol.setPrefWidth(250);

        // Type column
        TableColumn<ConditionalLine, String> typeCol = new TableColumn<>("");
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().type));
        typeCol.setPrefWidth(140);

        // Rounding column
        TableColumn<ConditionalLine, String> roundCol = new TableColumn<>("");
        roundCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().rounding));
        roundCol.setPrefWidth(120);

        // Value column
        TableColumn<ConditionalLine, String> valCol = new TableColumn<>("");
        valCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().value));
        valCol.setPrefWidth(50);
        valCol.setStyle("-fx-alignment: CENTER-RIGHT;");

        // Flag1 column
        TableColumn<ConditionalLine, String> flag1Col = new TableColumn<>("");
        flag1Col.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().flag1));
        flag1Col.setPrefWidth(30);
        flag1Col.setStyle("-fx-alignment: CENTER;");

        // Flag2 column
        TableColumn<ConditionalLine, String> flag2Col = new TableColumn<>("");
        flag2Col.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().flag2));
        flag2Col.setPrefWidth(30);
        flag2Col.setStyle("-fx-alignment: CENTER;");

        table.getColumns().addAll(operandCol, elemCol, typeCol, roundCol, valCol, flag1Col, flag2Col);

        // Sample data matching the screenshot
        ObservableList<ConditionalLine> data = FXCollections.observableArrayList(
            new ConditionalLine("WHEN", "Auto Insurance Score Cap Factor", "[Database Elements]", "<No Rounding>", "0.00", "0", "0"),
            new ConditionalLine("GREATER THAN", "0", "[Literal Numeric]", "<No Rounding>", "0.00", "L", "0"),
            new ConditionalLine("USE", "Auto Insurance Score Cap Factor", "[Database Elements]", "<No Rounding>", "0.00", "0", "0"),
            new ConditionalLine("OTHERWISE", "1.00", "[Literal Numeric]", "<No Rounding>", "0.00", "L", "0")
        );
        table.setItems(data);

        // Style first row with blue background
        table.setRowFactory(tv -> new TableRow<ConditionalLine>() {
            @Override
            protected void updateItem(ConditionalLine item, boolean empty) {
                super.updateItem(item, empty);
                if (getIndex() == 0 && item != null) {
                    setStyle("-fx-background-color: #000080; -fx-text-fill: white;");
                } else {
                    setStyle("");
                }
            }
        });

        return table;
    }

    private VBox createToolbar() {
        VBox toolbarBox = new VBox(2);
        toolbarBox.setPadding(new Insets(5));
        toolbarBox.setStyle("-fx-background-color: #C0C0C0;");

        // Row 1: Standard operations
        HBox row1 = new HBox(5);
        row1.setAlignment(Pos.CENTER_LEFT);

        Button addBtn = createToolbarButton("+");
        Button removeBtn = createToolbarButton("−");
        Button deleteBtn = createToolbarButton("×");
        Button editBtn = createToolbarButton("/");
        Button equalsBtn = createToolbarButton("=");
        Button clearBtn = createToolbarButton("××");
        Button targetBtn = createToolbarButton("⊕");
        Button moveBtn = createToolbarButton("Move");
        moveBtn.setPrefWidth(45);

        Separator sep1 = new Separator();
        sep1.setOrientation(javafx.geometry.Orientation.VERTICAL);

        Button codeLiteralBtn = createToolbarButton("Code\nLiteral");
        codeLiteralBtn.setPrefWidth(50);
        Button insertBtn = createToolbarButton("Insert");
        insertBtn.setPrefWidth(45);
        Button gridBtn = createToolbarButton("⊞");
        Button viewBtn = createToolbarButton("☐");
        Button testBtn = createToolbarButton("✓");
        Button saveBtn = createToolbarButton("💾");
        Button genBtn = createToolbarButton("⚙");

        row1.getChildren().addAll(
            addBtn, removeBtn, deleteBtn, editBtn, equalsBtn, clearBtn, targetBtn, moveBtn,
            sep1,
            codeLiteralBtn, insertBtn, gridBtn, viewBtn, testBtn, saveBtn, genBtn
        );

        // Row 2: Conditional-specific operations
        HBox row2 = new HBox(5);
        row2.setAlignment(Pos.CENTER_LEFT);

        Button ltBtn = createToolbarButton("<");
        Button gtBtn = createToolbarButton(">");
        Button leBtn = createToolbarButton("≤");
        Button geBtn = createToolbarButton("≥");
        Button openParenBtn = createToolbarButton("(");
        Button closeParenBtn = createToolbarButton(")");
        Button noOpenBtn = createRedCircleButton();
        Button noCloseBtn = createRedCircleButton();

        Separator sep2 = new Separator();
        sep2.setOrientation(javafx.geometry.Orientation.VERTICAL);

        Button whenBtn = createToolbarButton("When");
        whenBtn.setPrefWidth(50);
        whenBtn.setStyle("-fx-background-color: #D4D0C8; -fx-border-color: #808080;");

        Button otherWhenBtn = createToolbarButton("Other\nWhen");
        otherWhenBtn.setPrefWidth(50);

        Button otherwiseBtn = createToolbarButton("Other\nWise");
        otherwiseBtn.setPrefWidth(50);

        Separator sep3 = new Separator();
        sep3.setOrientation(javafx.geometry.Orientation.VERTICAL);

        Button useBtn = createHighlightButton("Use");
        Button equalsBtn2 = createHighlightButton("Equals");
        Button notBtn = createHighlightButton("Not");
        Button andBtn = createHighlightButton("And");
        Button orBtn = createHighlightButton("Or");

        row2.getChildren().addAll(
            ltBtn, gtBtn, leBtn, geBtn, openParenBtn, closeParenBtn, noOpenBtn, noCloseBtn,
            sep2,
            whenBtn, otherWhenBtn, otherwiseBtn,
            sep3,
            useBtn, equalsBtn2, notBtn, andBtn, orBtn
        );

        toolbarBox.getChildren().addAll(row1, row2);
        return toolbarBox;
    }

    private Button createToolbarButton(String text) {
        Button btn = new Button(text);
        btn.setPrefSize(35, 30);
        btn.setStyle("-fx-background-color: #D4D0C8; -fx-border-color: #808080; -fx-border-width: 1;");
        return btn;
    }

    private Button createRedCircleButton() {
        Button btn = new Button("⛔");
        btn.setPrefSize(35, 30);
        btn.setStyle("-fx-background-color: #D4D0C8; -fx-border-color: #808080; -fx-text-fill: red;");
        return btn;
    }

    private Button createHighlightButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(50);
        btn.setPrefHeight(30);
        btn.setStyle("-fx-background-color: #4169E1; -fx-text-fill: white; -fx-border-color: #2F4F8F;");
        return btn;
    }

    private void showElementPicker(Stage owner) {
        Stage floaty = new Stage();
        floaty.setTitle("Conditional Elements");
        floaty.initOwner(owner);

        TreeItem<String> root = new TreeItem<>("Elements");
        root.setExpanded(true);

        TreeItem<String> dbElements = new TreeItem<>("Database Elements");
        TreeItem<String> rateKeys = new TreeItem<>("Rate Keys");
        TreeItem<String> workingStorage = new TreeItem<>("Working Storage");
        TreeItem<String> ratingBreaks = new TreeItem<>("Rating Breaks");
        TreeItem<String> userExits = new TreeItem<>("User Exits");
        TreeItem<String> rounding = new TreeItem<>("Rounding");
        TreeItem<String> constants = new TreeItem<>("Constants");
        TreeItem<String> dateRoutine = new TreeItem<>("Date Routine");

        // Add some sample children
        dbElements.getChildren().addAll(
            new TreeItem<>("Auto Insurance Score Cap Factor"),
            new TreeItem<>("BI Environmental Score"),
            new TreeItem<>("Driver Age"),
            new TreeItem<>("Vehicle Year")
        );

        rateKeys.getChildren().addAll(
            new TreeItem<>("Auto Tier Factor (RK)"),
            new TreeItem<>("Primary Class Code Factor 1 (RK)"),
            new TreeItem<>("Bodily Injury Limit Factor (RK)")
        );

        workingStorage.getChildren().addAll(
            new TreeItem<>("<Result 1>"),
            new TreeItem<>("<Result 2>"),
            new TreeItem<>("<Result 3>"),
            new TreeItem<>("<Result 4>"),
            new TreeItem<>("<Result 5>"),
            new TreeItem<>("<CovCode>")
        );

        root.getChildren().addAll(dbElements, rateKeys, workingStorage, ratingBreaks, userExits, rounding, constants, dateRoutine);

        TreeView<String> treeView = new TreeView<>(root);
        treeView.setShowRoot(false);
        treeView.setPrefSize(280, 300);

        // Select Database Elements by default
        treeView.getSelectionModel().select(dbElements);

        VBox content = new VBox(treeView);
        content.setPadding(new Insets(5));

        Scene scene = new Scene(content, 300, 320);
        floaty.setScene(scene);
        floaty.setX(200);
        floaty.setY(300);
        floaty.show();
    }

    // Data model
    public static class ConditionalLine {
        String operand;
        String element;
        String type;
        String rounding;
        String value;
        String flag1;
        String flag2;

        public ConditionalLine(String operand, String element, String type, String rounding, String value, String flag1, String flag2) {
            this.operand = operand;
            this.element = element;
            this.type = type;
            this.rounding = rounding;
            this.value = value;
            this.flag1 = flag1;
            this.flag2 = flag2;
        }
    }
}
