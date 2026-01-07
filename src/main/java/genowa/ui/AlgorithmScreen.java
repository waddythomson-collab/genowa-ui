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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class AlgorithmScreen extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Algorithm");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #C0C0C0;");

        // Header Panel
        VBox headerPanel = createHeaderPanel();
        root.setTop(headerPanel);

        // Main Grid
        TableView<AlgorithmLine> table = createAlgorithmTable();
        root.setCenter(table);

        // Bottom Toolbar
        VBox toolbar = createToolbar();
        root.setBottom(toolbar);

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createHeaderPanel() {
        VBox header = new VBox(5);
        header.setPadding(new Insets(10));
        header.setStyle("-fx-background-color: #C0C0C0;");

        // Row 1: Dates and dropdowns
        HBox row1 = new HBox(20);
        row1.setAlignment(Pos.CENTER_LEFT);

        // Effective Date
        HBox effDateBox = new HBox(5);
        effDateBox.setAlignment(Pos.CENTER_LEFT);
        Label effDateLbl = new Label("Effective Date:");
        TextField effDateField = new TextField("04/01/2017");
        effDateField.setPrefWidth(100);
        effDateBox.getChildren().addAll(effDateLbl, effDateField);

        // Expiration Date
        HBox expDateBox = new HBox(5);
        expDateBox.setAlignment(Pos.CENTER_LEFT);
        Label expDateLbl = new Label("Expiration Date:");
        TextField expDateField = new TextField("01/01/2023");
        expDateField.setPrefWidth(100);
        expDateBox.getChildren().addAll(expDateLbl, expDateField);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Mid-term Code
        HBox midTermBox = new HBox(5);
        midTermBox.setAlignment(Pos.CENTER_LEFT);
        Label midTermLbl = new Label("Mid-term Code:");
        ComboBox<String> midTermCombo = new ComboBox<>();
        midTermCombo.getItems().addAll("No", "Yes");
        midTermCombo.setValue("No");
        midTermCombo.setPrefWidth(150);
        midTermBox.getChildren().addAll(midTermLbl, midTermCombo);

        row1.getChildren().addAll(effDateBox, expDateBox, spacer, midTermBox);

        // Row 2: Coverage Level
        HBox row2 = new HBox(20);
        row2.setAlignment(Pos.CENTER_RIGHT);
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        Label covLevelLbl = new Label("Coverage Level:");
        ComboBox<String> covLevelCombo = new ComboBox<>();
        covLevelCombo.getItems().addAll("Unit Level", "Policy Level", "Coverage Level");
        covLevelCombo.setValue("Unit Level");
        covLevelCombo.setPrefWidth(150);
        row2.getChildren().addAll(spacer2, covLevelLbl, covLevelCombo);

        // Row 3: Comments
        HBox row3 = new HBox(5);
        Label commentsLbl = new Label("Comments:");
        row3.getChildren().add(commentsLbl);

        // Row 4: Module info
        Label moduleInfo = new Label("Bodily Injury Liability  -  Module: PGUOBK3");
        moduleInfo.setFont(Font.font("System", FontWeight.NORMAL, 12));

        header.getChildren().addAll(row1, row2, row3, moduleInfo);
        return header;
    }

    private TableView<AlgorithmLine> createAlgorithmTable() {
        TableView<AlgorithmLine> table = new TableView<>();
        table.setStyle("-fx-background-color: #FFFFFF;");

        // Operator column
        TableColumn<AlgorithmLine, String> opCol = new TableColumn<>("");
        opCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().operator));
        opCol.setPrefWidth(30);
        opCol.setStyle("-fx-alignment: CENTER;");

        // Element column
        TableColumn<AlgorithmLine, String> elemCol = new TableColumn<>("");
        elemCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().element));
        elemCol.setPrefWidth(280);

        // Type column
        TableColumn<AlgorithmLine, String> typeCol = new TableColumn<>("");
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().type));
        typeCol.setPrefWidth(140);

        // Rounding column
        TableColumn<AlgorithmLine, String> roundCol = new TableColumn<>("");
        roundCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().rounding));
        roundCol.setPrefWidth(130);

        // Value column
        TableColumn<AlgorithmLine, String> valCol = new TableColumn<>("");
        valCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().value));
        valCol.setPrefWidth(50);
        valCol.setStyle("-fx-alignment: CENTER-RIGHT;");

        // Indicator column
        TableColumn<AlgorithmLine, String> indCol = new TableColumn<>("");
        indCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().indicator));
        indCol.setPrefWidth(30);
        indCol.setStyle("-fx-alignment: CENTER;");

        table.getColumns().addAll(opCol, elemCol, typeCol, roundCol, valCol, indCol);

        // Sample data matching the screenshot
        ObservableList<AlgorithmLine> data = FXCollections.observableArrayList(
            new AlgorithmLine("", "BI", "[Literal Code]", "<No Rounding>", "0.00", "C"),
            new AlgorithmLine("=", "<CovCode>", "[Working Storage]", "<No Rounding>", "0.00", "R"),
            new AlgorithmLine("", "Determine Auto Base Rate", "[Conditional]", "<No Rounding>", "0.00", "0"),
            new AlgorithmLine("", "<Result 5>", "[Working Storage]", "<No Rounding>", "0.00", "R"),
            new AlgorithmLine("", "Determine Household Composition Factor", "[Conditional]", "<No Rounding>", "0.00", "0"),
            new AlgorithmLine("=", "<Result 6>", "[Working Storage]", "<No Rounding>", "0.00", "R"),
            new AlgorithmLine("", "Determine Financial Stability Discount Percent BI", "[Conditional]", "<No Rounding>", "0.00", "0"),
            new AlgorithmLine("=", "<Result 7>", "[Working Storage]", "<No Rounding>", "0.00", "0"),
            new AlgorithmLine("", "Determine Auto Insurance Score Cap Factor", "[Conditional]", "<No Rounding>", "0.00", "0"),
            new AlgorithmLine("=", "<INSC>", "[Working Storage]", "<No Rounding>", "0.00", "R"),
            new AlgorithmLine("", "BI Environmental Score", "[Database Elements]", "<5 Decimal Place>", "5.00", "0"),
            new AlgorithmLine("×", "100000", "[Literal Numeric]", "<No Rounding>", "0.00", "L"),
            new AlgorithmLine("=", "<Environmental Score x 100000>", "[Working Storage]", "<No Rounding>", "0.00", "R"),
            new AlgorithmLine("", "<Result 5>", "[Working Storage]", "<No Rounding>", "0.00", "R"),
            new AlgorithmLine("×", "Auto Tier Factor (RK)", "[Rate Keys]", "<Whole Dollar Rnd>", "0.00", "0"),
            new AlgorithmLine("×", "Determine Auto Territory Factor", "[Conditional]", "<1 Decimal Place>", "1.00", "0"),
            new AlgorithmLine("×", "Primary Class Code Factor 1 (RK)", "[Rate Keys]", "<1 Decimal Place>", "1.00", "0"),
            new AlgorithmLine("×", "Determine Multi Car Discount Factor", "[Conditional]", "<1 Decimal Place>", "1.00", "0"),
            new AlgorithmLine("=", "<Result 3>", "[Working Storage]", "<No Rounding>", "0.00", "R"),
            new AlgorithmLine("", "Determine Violations/Accidents Factor", "[Conditional]", "<No Rounding>", "0.00", "0"),
            new AlgorithmLine("+", "Determine Excess Vehicle Points Factor", "[Conditional]", "<No Rounding>", "0.00", "0"),
            new AlgorithmLine("=", "<Result 4>", "[Working Storage]", "<No Rounding>", "0.00", "R"),
            new AlgorithmLine("", "<Result 3>", "[Working Storage]", "<No Rounding>", "0.00", "R"),
            new AlgorithmLine("", "<Result 4>", "[Working Storage]", "<1 Decimal Place>", "1.00", "R"),
            new AlgorithmLine("×", "Determine Inexperienced Operator Surcharge", "[Conditional]", "<1 Decimal Place>", "1.00", "0"),
            new AlgorithmLine("×", "Determine Good Student Discount Factor", "[Conditional]", "<1 Decimal Place>", "1.00", "0"),
            new AlgorithmLine("×", "Determine Away At School Discount Factor", "[Conditional]", "<1 Decimal Place>", "1.00", "0")
        );
        table.setItems(data);

        // Color the header row
        table.setRowFactory(tv -> new TableRow<AlgorithmLine>() {
            @Override
            protected void updateItem(AlgorithmLine item, boolean empty) {
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

        HBox toolbar = new HBox(5);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // Row operation buttons
        Button addBtn = createToolbarButton("+");
        Button removeBtn = createToolbarButton("−");
        Button deleteBtn = createToolbarButton("×");
        Button editBtn = createToolbarButton("/");
        Button equalsBtn = createToolbarButton("=");
        Button clearBtn = createToolbarButton("××");
        Button targetBtn = createToolbarButton("⊕");

        // Separator
        Separator sep1 = new Separator();
        sep1.setOrientation(javafx.geometry.Orientation.VERTICAL);

        // Action buttons
        Button codeLiteralBtn = createToolbarButton("Code\nLiteral");
        codeLiteralBtn.setPrefWidth(50);
        Button insertBtn = createToolbarButton("Insert");
        Button gridBtn = createToolbarButton("⊞");
        Button viewBtn = createToolbarButton("☐");
        Button testBtn = createToolbarButton("Test");
        Button saveBtn = createToolbarButton("💾");
        Button genBtn = createToolbarButton("⚙");
        Button exitBtn = createToolbarButton("✕");

        toolbar.getChildren().addAll(
            addBtn, removeBtn, deleteBtn, editBtn, equalsBtn, clearBtn, targetBtn,
            sep1,
            codeLiteralBtn, insertBtn, gridBtn, viewBtn, testBtn, saveBtn, genBtn, exitBtn
        );

        toolbarBox.getChildren().add(toolbar);
        return toolbarBox;
    }

    private Button createToolbarButton(String text) {
        Button btn = new Button(text);
        btn.setPrefSize(35, 30);
        btn.setStyle("-fx-background-color: #D4D0C8; -fx-border-color: #808080; -fx-border-width: 1;");
        return btn;
    }

    // Data model
    public static class AlgorithmLine {
        String operator;
        String element;
        String type;
        String rounding;
        String value;
        String indicator;

        public AlgorithmLine(String operator, String element, String type, String rounding, String value, String indicator) {
            this.operator = operator;
            this.element = element;
            this.type = type;
            this.rounding = rounding;
            this.value = value;
            this.indicator = indicator;
        }
    }
}
