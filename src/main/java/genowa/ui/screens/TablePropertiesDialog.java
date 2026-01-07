package genowa.ui.screens;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.*;

/**
 * Dialog for configuring table key columns and their values.
 * This defines how SQL WHERE clauses are generated for database element access.
 */
public class TablePropertiesDialog extends Dialog<ButtonType> {

    private ComboBox<String> tableNamesCombo;
    private ComboBox<String> columnValuesCombo;
    private TextField literalField;
    private TableView<KeyColumnEntry> tableView;
    private ObservableList<KeyColumnEntry> keyColumns = FXCollections.observableArrayList();

    private final DatabaseElementScreen.ColumnEntry sourceColumn;

    public TablePropertiesDialog(DatabaseElementScreen.ColumnEntry column) {
        this.sourceColumn = column;

        setTitle("Table Properties");
        setHeaderText(null);
        setResizable(true);

        // Build dialog content
        DialogPane dialogPane = getDialogPane();
        dialogPane.setContent(createContent());
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setPrefSize(900, 500);

        // Load sample data
        loadSampleData();
    }

    private VBox createContent() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        // Header row: Table Names + Column Values + Literal
        HBox headerRow = new HBox(20);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label tableLabel = new Label("Table Names:");
        tableNamesCombo = new ComboBox<>();
        tableNamesCombo.getItems().addAll("PA_VEHICLE_V", "PA_DRIVER_V", "PA_POLICY_V", "PA_COVERAGE_V");
        tableNamesCombo.setValue("PA_VEHICLE_V");
        tableNamesCombo.setPrefWidth(150);

        Label colValLabel = new Label("Column Values:");
        columnValuesCombo = new ComboBox<>();
        columnValuesCombo.getItems().addAll(
            "*Not Valued*",
            "— Insurance Line Code —",
            "— Primary Rating Break —",
            "— Peril Code —",
            "— Coverage Code —"
        );
        columnValuesCombo.setPrefWidth(200);

        Label literalLabel = new Label("Literal:");
        literalField = new TextField();
        literalField.setPrefWidth(150);

        headerRow.getChildren().addAll(
            tableLabel, tableNamesCombo,
            colValLabel, columnValuesCombo,
            literalLabel, literalField
        );

        // Instructions
        Label instructions = new Label(
            "Make selections and/or enter in fields above and 'Apply' (Ctrl+A) to modify column values.\n" +
            "for: " + sourceColumn.getColumnName() + " (" + sourceColumn.getDescription().replace("[", "").replace("]", "") + ")"
        );
        instructions.setStyle("-fx-font-style: italic; -fx-text-fill: #666666;");

        // Create table
        tableView = new TableView<>(keyColumns);
        tableView.setEditable(false);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        // Key/Required column
        TableColumn<KeyColumnEntry, String> keyCol = new TableColumn<>("Key/Required");
        keyCol.setCellValueFactory(new PropertyValueFactory<>("keyRequired"));
        keyCol.setPrefWidth(80);

        // Column Name
        TableColumn<KeyColumnEntry, String> nameCol = new TableColumn<>("Column Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("columnName"));
        nameCol.setPrefWidth(150);

        // Qualifier
        TableColumn<KeyColumnEntry, String> qualCol = new TableColumn<>("Qualifier");
        qualCol.setCellValueFactory(new PropertyValueFactory<>("qualifier"));
        qualCol.setPrefWidth(70);

        // Column Value
        TableColumn<KeyColumnEntry, String> valueCol = new TableColumn<>("Column Value");
        valueCol.setCellValueFactory(new PropertyValueFactory<>("columnValue"));
        valueCol.setPrefWidth(180);

        // COBOL Name
        TableColumn<KeyColumnEntry, String> cobolCol = new TableColumn<>("Cobol Name");
        cobolCol.setCellValueFactory(new PropertyValueFactory<>("cobolName"));
        cobolCol.setPrefWidth(150);

        // Column Variable Type
        TableColumn<KeyColumnEntry, String> varTypeCol = new TableColumn<>("Column Variable Type");
        varTypeCol.setCellValueFactory(new PropertyValueFactory<>("variableType"));
        varTypeCol.setPrefWidth(120);

        // Length
        TableColumn<KeyColumnEntry, String> lengthCol = new TableColumn<>("Length");
        lengthCol.setCellValueFactory(new PropertyValueFactory<>("length"));
        lengthCol.setPrefWidth(60);

        tableView.getColumns().addAll(keyCol, nameCol, qualCol, valueCol, cobolCol, varTypeCol, lengthCol);

        // Custom row styling for Key rows
        tableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(KeyColumnEntry item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && "Key".equals(item.getKeyRequired())) {
                    setStyle("-fx-font-weight: bold;");
                } else {
                    setStyle("");
                }
            }
        });

        // Apply button
        Button applyButton = new Button("Apply (Ctrl+A)");
        applyButton.setOnAction(e -> applyColumnValue());

        HBox buttonRow = new HBox(10, applyButton);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        content.getChildren().addAll(headerRow, instructions, tableView, buttonRow);
        return content;
    }

    private void applyColumnValue() {
        KeyColumnEntry selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String value = columnValuesCombo.getValue();
            if (value == null || value.isEmpty()) {
                value = literalField.getText();
            }
            if (value != null && !value.isEmpty()) {
                selected.setColumnValue(value);
                if (!"*Not Valued*".equals(value)) {
                    selected.setQualifier("Equal to");
                }
                tableView.refresh();
            }
        }
    }

    private void loadSampleData() {
        keyColumns.addAll(
            new KeyColumnEntry("Key", "POLICY_ID", "", "*Not Valued*", "POLICY-ID", "", "16"),
            new KeyColumnEntry("Key", "INSURANCE_LINE_CD", "Equal to", "— Insurance Line Code —", "INSURANCE-LINE-CD", "Rating Breaks", "3"),
            new KeyColumnEntry("Key", "EFFECTIVE_DT", "", "*Not Valued*", "EFFECTIVE-DT", "", "10"),
            new KeyColumnEntry("Key", "VEH_UNIT_NBR", "Equal to", "— Primary Rating Break —", "VEH-UNIT-NBR", "Rating Breaks", "0"),
            new KeyColumnEntry("Key", "HISTORY_VLD_NBR", "", "*Not Valued*", "HISTORY-VLD-NBR", "", "0"),
            new KeyColumnEntry("Key", "QUOTE_SEQUENCE_NBR", "", "*Not Valued*", "QUOTE-SEQUENCE-NBR", "", "0"),
            new KeyColumnEntry("", "CED_IND", "", "*Not Valued*", "CED-IND", "", "1"),
            new KeyColumnEntry("", "PA_BODY_CD", "", "*Not Valued*", "PA-BODY-CD", "", "2"),
            new KeyColumnEntry("Required", "PAVE_CRP_IND", "", "*Not Valued*", "PAVE-CRP-IND", "", "1"),
            new KeyColumnEntry("", "PAVE_CST_NEW_AMT", "", "*Not Valued*", "PAVE-CST-NEW-AMT", "", "7"),
            new KeyColumnEntry("", "PAVE_DPW_NBR", "", "*Not Valued*", "PAVE-DPW-NBR", "", "0"),
            new KeyColumnEntry("", "PA_DRV_VEH_CLS_CD", "", "*Not Valued*", "PA-DRV-VEH-CLS-CD", "", "6"),
            new KeyColumnEntry("", "PA_EGE_SZ_CC_CD", "", "*Not Valued*", "PA-EGE-SZ-CC-CD", "", "2"),
            new KeyColumnEntry("", "PA_MAKE_CD", "", "*Not Valued*", "PA-MAKE-CD", "", "4"),
            new KeyColumnEntry("", "PA_MODEL_CD", "", "*Not Valued*", "PA-MODEL-CD", "", "10"),
            new KeyColumnEntry("", "PA_MODEL_YR_NBR", "", "*Not Valued*", "PA-MODEL-YR-NBR", "", "0"),
            new KeyColumnEntry("", "PAVE_MUC_DSC_CD", "", "*Not Valued*", "PAVE-MUC-DSC-CD", "", "1"),
            new KeyColumnEntry("", "PAVE_SR_DRV_DSC_CD", "", "*Not Valued*", "PAVE-SR-DRV-DSC-CD", "", "1"),
            new KeyColumnEntry("", "PA_VEH_TYPE_CD", "Equal to", "— Peril Code —", "PA-VEH-TYPE-CD", "Rating Breaks", "2"),
            new KeyColumnEntry("Required", "PA_VEH_USE_CD", "", "*Not Valued*", "PA-VEH-USE-CD", "", "2")
        );

        // Select first row
        tableView.getSelectionModel().selectFirst();
    }

    /**
     * Data model for key column entries.
     */
    public static class KeyColumnEntry {
        private final StringProperty keyRequired;
        private final StringProperty columnName;
        private final StringProperty qualifier;
        private final StringProperty columnValue;
        private final StringProperty cobolName;
        private final StringProperty variableType;
        private final StringProperty length;

        public KeyColumnEntry(String keyRequired, String columnName, String qualifier,
                             String columnValue, String cobolName, String variableType, String length) {
            this.keyRequired = new SimpleStringProperty(keyRequired);
            this.columnName = new SimpleStringProperty(columnName);
            this.qualifier = new SimpleStringProperty(qualifier);
            this.columnValue = new SimpleStringProperty(columnValue);
            this.cobolName = new SimpleStringProperty(cobolName);
            this.variableType = new SimpleStringProperty(variableType);
            this.length = new SimpleStringProperty(length);
        }

        // Getters
        public String getKeyRequired() { return keyRequired.get(); }
        public String getColumnName() { return columnName.get(); }
        public String getQualifier() { return qualifier.get(); }
        public String getColumnValue() { return columnValue.get(); }
        public String getCobolName() { return cobolName.get(); }
        public String getVariableType() { return variableType.get(); }
        public String getLength() { return length.get(); }

        // Setters
        public void setQualifier(String value) { qualifier.set(value); }
        public void setColumnValue(String value) { columnValue.set(value); }

        // Properties
        public StringProperty keyRequiredProperty() { return keyRequired; }
        public StringProperty columnNameProperty() { return columnName; }
        public StringProperty qualifierProperty() { return qualifier; }
        public StringProperty columnValueProperty() { return columnValue; }
        public StringProperty cobolNameProperty() { return cobolName; }
        public StringProperty variableTypeProperty() { return variableType; }
        public StringProperty lengthProperty() { return length; }
    }
}
