package genowa.ui.screens;

import genowa.model.GenField;
import genowa.model.GenTable;
import genowa.repository.GenFieldRepository;
import genowa.repository.GenTableRepository;
import genowa.Database;
import genowa.ui.components.DockablePane;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

import java.util.*;

/**
 * Database Element management screen.
 * Allows users to view, add, edit, and delete database table/column definitions
 * that can be used in algorithms and conditionals.
 */
public class DatabaseElementScreen extends BorderPane
{
    // Header controls
    private ComboBox<String> tableCombo;
    private ComboBox<String> columnDescriptionCombo;
    private CheckBox includeDeletedCheck;
    private CheckBox overrideTemplateCheck;
    private TextField searchField;

    // Main table
    private TableView<ColumnEntry> tableView;
    private ObservableList<ColumnEntry> columns = FXCollections.observableArrayList();

    // Column Options
    private ToggleGroup columnOptionsGroup;
    private RadioButton ioRadio, minRadio, maxRadio, sumRadio, countRadio;
    private RadioButton cursorRadio, readRadio, keysetRadio, existsRadio, cursorResultRadio;

    // Context menu
    private ContextMenu contextMenu;

    // Status bar
    private Label statusLabel;
    private Label countLabel;

    public DatabaseElementScreen()
    {
        setPadding(new Insets(10));
        setStyle("-fx-background-color: #f5f5f5;");

        // Build UI
        setTop(createHeader());
        setCenter(createMainContent());
        setBottom(createBottomPanel());

        // Load data from database
        loadTables();

        // Create context menu
        createContextMenu();
    }

    private VBox createHeader()
    {
        VBox header = new VBox(10);
        header.setPadding(new Insets(0, 0, 10, 0));

        // Title
        Label titleLabel = new Label("Table Column Entry");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        // First row: Table selection + checkboxes
        HBox row1 = new HBox(20);
        row1.setAlignment(Pos.CENTER_LEFT);

        Label tableLabel = new Label("Table:");
        tableCombo = new ComboBox<>();
        tableCombo.setPrefWidth(250);
        tableCombo.setOnAction(e -> loadFieldsForSelectedTable());

        includeDeletedCheck = new CheckBox("Include Deleted Rows");
        overrideTemplateCheck = new CheckBox("Override with Template");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row1.getChildren().addAll(tableLabel, tableCombo, spacer, includeDeletedCheck, overrideTemplateCheck);

        // Second row: Column Description filter + Search
        HBox row2 = new HBox(10);
        row2.setAlignment(Pos.CENTER_LEFT);

        Label colDescLabel = new Label("Column Description:");
        columnDescriptionCombo = new ComboBox<>();
        columnDescriptionCombo.setEditable(true);
        columnDescriptionCombo.setPrefWidth(250);
        columnDescriptionCombo.setOnAction(e -> filterByDescription());

        Label searchLabel = new Label("Search:");
        searchField = new TextField();
        searchField.setPrefWidth(150);
        searchField.setPromptText("Column name...");
        searchField.textProperty().addListener((obs, old, newVal) -> filterBySearch(newVal));

        row2.getChildren().addAll(colDescLabel, columnDescriptionCombo, searchLabel, searchField);

        header.getChildren().addAll(titleLabel, row1, row2);
        return header;
    }

    private VBox createMainContent()
    {
        VBox content = new VBox(5);

        // Table header label
        Label tableLabel = new Label("Table/Column");
        tableLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        tableLabel.setStyle("-fx-padding: 5 0;");

        // Create table
        tableView = new TableView<>(columns);
        tableView.setEditable(false);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        // Tree indicator column (the ├── prefix)
        TableColumn<ColumnEntry, String> treeCol = new TableColumn<>("");
        treeCol.setCellValueFactory(new PropertyValueFactory<>("treePrefix"));
        treeCol.setPrefWidth(30);
        treeCol.setSortable(false);

        // Column name
        TableColumn<ColumnEntry, String> nameCol = new TableColumn<>("Column Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("columnName"));
        nameCol.setPrefWidth(180);

        // COBOL name
        TableColumn<ColumnEntry, String> cobolCol = new TableColumn<>("COBOL Name");
        cobolCol.setCellValueFactory(new PropertyValueFactory<>("cobolName"));
        cobolCol.setPrefWidth(150);

        // Data type
        TableColumn<ColumnEntry, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("dataType"));
        typeCol.setPrefWidth(80);

        // Length
        TableColumn<ColumnEntry, String> lengthCol = new TableColumn<>("Length");
        lengthCol.setCellValueFactory(new PropertyValueFactory<>("length"));
        lengthCol.setPrefWidth(60);

        // Flags (Required, etc.)
        TableColumn<ColumnEntry, String> flagsCol = new TableColumn<>("Flags");
        flagsCol.setCellValueFactory(new PropertyValueFactory<>("flags"));
        flagsCol.setPrefWidth(100);

        // Description
        TableColumn<ColumnEntry, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(250);

        tableView.getColumns().addAll(treeCol, nameCol, cobolCol, typeCol, lengthCol, flagsCol, descCol);

        // Custom row styling
        tableView.setRowFactory(tv ->
        {
            TableRow<ColumnEntry> row = new TableRow<>()
            {
                @Override
                protected void updateItem(ColumnEntry item, boolean empty)
                {
                    super.updateItem(item, empty);
                    if (item != null && item.isKey())
                    {
                        setStyle("-fx-font-weight: bold; -fx-text-fill: #0066cc;");
                    }
                    else
                    {
                        setStyle("");
                    }
                }
            };

            // Right-click context menu
            row.setOnMouseClicked(event ->
            {
                if (event.getButton() == MouseButton.SECONDARY && !row.isEmpty())
                {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });

            return row;
        });

        content.getChildren().addAll(tableLabel, tableView);
        return content;
    }

    private VBox createBottomPanel()
    {
        VBox bottom = new VBox(10);
        bottom.setPadding(new Insets(10, 0, 0, 0));

        // Column options row
        HBox options = new HBox(15);
        options.setAlignment(Pos.CENTER_LEFT);
        options.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1 0 0 0; -fx-padding: 10 0 0 0;");

        Label label = new Label("Column Options");
        label.setFont(Font.font("System", FontWeight.BOLD, 11));

        columnOptionsGroup = new ToggleGroup();

        ioRadio = new RadioButton("I/O");
        ioRadio.setToggleGroup(columnOptionsGroup);
        ioRadio.setSelected(true);

        minRadio = new RadioButton("Minimum");
        minRadio.setToggleGroup(columnOptionsGroup);

        maxRadio = new RadioButton("Maximum");
        maxRadio.setToggleGroup(columnOptionsGroup);

        sumRadio = new RadioButton("Sum");
        sumRadio.setToggleGroup(columnOptionsGroup);

        countRadio = new RadioButton("Count");
        countRadio.setToggleGroup(columnOptionsGroup);

        cursorRadio = new RadioButton("Cursor");
        cursorRadio.setToggleGroup(columnOptionsGroup);

        readRadio = new RadioButton("Read");
        readRadio.setToggleGroup(columnOptionsGroup);

        keysetRadio = new RadioButton("KeySet Override");
        keysetRadio.setToggleGroup(columnOptionsGroup);

        existsRadio = new RadioButton("Exists");
        existsRadio.setToggleGroup(columnOptionsGroup);

        cursorResultRadio = new RadioButton("Cursor Result");
        cursorResultRadio.setToggleGroup(columnOptionsGroup);

        options.getChildren().addAll(
            label,
            new Separator(Orientation.VERTICAL),
            ioRadio, minRadio, maxRadio, sumRadio, countRadio,
            new Separator(Orientation.VERTICAL),
            cursorRadio, readRadio, keysetRadio, existsRadio, cursorResultRadio
        );

        // Status bar
        HBox statusBar = new HBox(20);
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setPadding(new Insets(5, 0, 0, 0));

        statusLabel = new Label("Ready");
        countLabel = new Label("0 fields");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        statusBar.getChildren().addAll(statusLabel, spacer, countLabel);

        bottom.getChildren().addAll(options, statusBar);
        return bottom;
    }

    private void createContextMenu()
    {
        contextMenu = new ContextMenu();

        MenuItem keyColumnsItem = new MenuItem("Key Columns...");
        keyColumnsItem.setOnAction(e -> showKeyColumnsDialog());
        keyColumnsItem.setAccelerator(javafx.scene.input.KeyCombination.keyCombination("Ctrl+K"));

        MenuItem tableChangeItem = new MenuItem("Table/Column Change...");
        tableChangeItem.setOnAction(e -> showTableColumnChangeDialog());
        tableChangeItem.setAccelerator(javafx.scene.input.KeyCombination.keyCombination("Ctrl+C"));

        MenuItem addItem = new MenuItem("Add Definition...");
        addItem.setOnAction(e -> addDefinition());
        addItem.setAccelerator(javafx.scene.input.KeyCombination.keyCombination("Ctrl+A"));

        MenuItem saveItem = new MenuItem("Save Definition...");
        saveItem.setOnAction(e -> saveDefinition());

        MenuItem deleteItem = new MenuItem("Delete Definition...");
        deleteItem.setOnAction(e -> deleteDefinition());
        deleteItem.setAccelerator(javafx.scene.input.KeyCombination.keyCombination("Ctrl+D"));

        SeparatorMenuItem sep1 = new SeparatorMenuItem();

        MenuItem viewMultipleItem = new MenuItem("View Multiple Elements...");
        viewMultipleItem.setAccelerator(javafx.scene.input.KeyCombination.keyCombination("Ctrl+V"));

        SeparatorMenuItem sep2 = new SeparatorMenuItem();

        MenuItem checkUnusedItem = new MenuItem("Check for Unused Elements");
        checkUnusedItem.setOnAction(e -> checkUnusedElements());

        MenuItem checkUsageItem = new MenuItem("Check to see where element is used");
        checkUsageItem.setOnAction(e -> checkElementUsage());

        SeparatorMenuItem sep3 = new SeparatorMenuItem();

        MenuItem generateItem = new MenuItem("Generate/Compile Affected Modules");
        generateItem.setOnAction(e -> generateAffectedModules());

        MenuItem generateExternalItem = new MenuItem("Generate External Database Element");

        SeparatorMenuItem sep4 = new SeparatorMenuItem();

        MenuItem defaultColumnsItem = new MenuItem("Default Columns for Test Panel...");
        defaultColumnsItem.setAccelerator(javafx.scene.input.KeyCombination.keyCombination("Ctrl+O"));

        MenuItem exitItem = new MenuItem("Exit...");
        exitItem.setOnAction(e -> closeScreen());
        exitItem.setAccelerator(javafx.scene.input.KeyCombination.keyCombination("F3"));

        contextMenu.getItems().addAll(
            keyColumnsItem, tableChangeItem, addItem, saveItem, deleteItem,
            sep1, viewMultipleItem,
            sep2, checkUnusedItem, checkUsageItem,
            sep3, generateItem, generateExternalItem,
            sep4, defaultColumnsItem, exitItem
        );
    }

    /**
     * Load all tables from gen_tables into the combo box.
     */
    private void loadTables()
    {
        statusLabel.setText("Loading tables...");

        try
        {
            List<String> tableNames = Database.getInstance().executeRead(em ->
            {
                GenTableRepository repo = new GenTableRepository(em);
                return repo.findAllTableNames();
            });

            tableCombo.getItems().clear();
            tableCombo.getItems().addAll(tableNames);

            statusLabel.setText("Loaded " + tableNames.size() + " tables");

            // Select first table if available
            if (!tableNames.isEmpty())
            {
                tableCombo.setValue(tableNames.get(0));
                loadFieldsForSelectedTable();
            }
        }
        catch (Exception e)
        {
            statusLabel.setText("Error loading tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load fields for the selected table from gen_fields.
     */
    private void loadFieldsForSelectedTable()
    {
        String selectedTable = tableCombo.getValue();
        if (selectedTable == null || selectedTable.isEmpty())
        {
            return;
        }

        statusLabel.setText("Loading fields for " + selectedTable + "...");

        try
        {
            List<GenField> fields = Database.getInstance().executeRead(em ->
            {
                GenTableRepository tableRepo = new GenTableRepository(em);
                GenFieldRepository fieldRepo = new GenFieldRepository(em);

                // Find the table by name
                return tableRepo.findByName(selectedTable)
                    .map(table -> fieldRepo.findByTableIndex(table.getTableIndexNbr()))
                    .orElse(new ArrayList<>());
            });

            columns.clear();

            // Convert GenField to ColumnEntry
            for (int i = 0; i < fields.size(); i++)
            {
                GenField f = fields.get(i);
                String prefix = (i == fields.size() - 1) ? "└──" : "├──";
                boolean isKey = "Y".equals(f.getKeyFlagCd());

                columns.add(new ColumnEntry(
                    prefix,
                    f.getColumnNm(),
                    f.getCobolNm() != null ? f.getCobolNm() : "",
                    f.getDataTypeDisplay(),
                    f.getLengthDisplay(),
                    f.getFlagsDisplay(),
                    f.getLongAliasNm() != null ? "[" + f.getLongAliasNm() + "]" : "",
                    isKey
                ));
            }

            // Select first row
            if (!columns.isEmpty())
            {
                tableView.getSelectionModel().selectFirst();
            }

            countLabel.setText(fields.size() + " fields");
            statusLabel.setText("Loaded " + fields.size() + " fields for " + selectedTable);

            // Load descriptions for filter combo
            loadDescriptions();
        }
        catch (Exception e)
        {
            statusLabel.setText("Error loading fields: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load distinct descriptions for the filter combo.
     */
    private void loadDescriptions()
    {
        try
        {
            List<String> descriptions = Database.getInstance().executeRead(em ->
            {
                GenFieldRepository repo = new GenFieldRepository(em);
                return repo.findDistinctDescriptions();
            });

            columnDescriptionCombo.getItems().clear();
            columnDescriptionCombo.getItems().addAll(descriptions);
        }
        catch (Exception e)
        {
            // Non-critical, just log
            System.err.println("Error loading descriptions: " + e.getMessage());
        }
    }

    /**
     * Filter columns by description.
     */
    private void filterByDescription()
    {
        String filter = columnDescriptionCombo.getValue();
        if (filter == null || filter.isEmpty())
        {
            loadFieldsForSelectedTable();
            return;
        }

        // Filter the current columns
        ObservableList<ColumnEntry> filtered = columns.filtered(c ->
            c.getDescription().toLowerCase().contains(filter.toLowerCase())
        );
        tableView.setItems(filtered);
        countLabel.setText(filtered.size() + " fields (filtered)");
    }

    /**
     * Filter columns by search text.
     */
    private void filterBySearch(String search)
    {
        if (search == null || search.isEmpty())
        {
            tableView.setItems(columns);
            countLabel.setText(columns.size() + " fields");
            return;
        }

        String lower = search.toLowerCase();
        ObservableList<ColumnEntry> filtered = columns.filtered(c ->
            c.getColumnName().toLowerCase().contains(lower) ||
            c.getCobolName().toLowerCase().contains(lower) ||
            c.getDescription().toLowerCase().contains(lower)
        );
        tableView.setItems(filtered);
        countLabel.setText(filtered.size() + " fields (filtered)");
    }

    // Action handlers
    private void showKeyColumnsDialog()
    {
        ColumnEntry selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null)
        {
            TablePropertiesDialog dialog = new TablePropertiesDialog(selected);
            dialog.showAndWait();
        }
    }

    private void showTableColumnChangeDialog()
    {
        showAlert("Table/Column Change", "Opens dialog to modify table/column definition.");
    }

    private void addDefinition()
    {
        showAlert("Add Definition", "Opens dialog to add a new database element definition.");
    }

    private void saveDefinition()
    {
        showAlert("Save", "Definition saved successfully.");
    }

    private void deleteDefinition()
    {
        ColumnEntry selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null)
        {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Delete Definition");
            confirm.setHeaderText("Delete " + selected.getColumnName() + "?");
            confirm.setContentText("This action cannot be undone.");
            confirm.showAndWait().ifPresent(response ->
            {
                if (response == ButtonType.OK)
                {
                    columns.remove(selected);
                }
            });
        }
    }

    private void checkUnusedElements()
    {
        showAlert("Unused Elements", "Scanning for elements not referenced in any algorithm or conditional...");
    }

    private void checkElementUsage()
    {
        ColumnEntry selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null)
        {
            showAlert("Element Usage",
                "Checking where " + selected.getColumnName() + " is used...\n\n" +
                "(Feature not yet implemented)");
        }
    }

    private void generateAffectedModules()
    {
        showAlert("Generate Modules", "Regenerating code for all modules using selected element...");
    }

    private void closeScreen()
    {
        // In a real app, this would close the screen/window
        showAlert("Exit", "Closing Database Elements screen.");
    }

    private void showAlert(String title, String message)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Data model for a column entry in the table.
     */
    public static class ColumnEntry
    {
        private final StringProperty treePrefix;
        private final StringProperty columnName;
        private final StringProperty cobolName;
        private final StringProperty dataType;
        private final StringProperty length;
        private final StringProperty flags;
        private final StringProperty description;
        private final boolean isKey;

        public ColumnEntry(String treePrefix, String columnName, String cobolName,
                          String dataType, String length, String flags, String description, boolean isKey)
        {
            this.treePrefix = new SimpleStringProperty(treePrefix);
            this.columnName = new SimpleStringProperty(columnName);
            this.cobolName = new SimpleStringProperty(cobolName);
            this.dataType = new SimpleStringProperty(dataType);
            this.length = new SimpleStringProperty(length);
            this.flags = new SimpleStringProperty(flags);
            this.description = new SimpleStringProperty(description);
            this.isKey = isKey;
        }

        public String getTreePrefix() { return treePrefix.get(); }
        public String getColumnName() { return columnName.get(); }
        public String getCobolName() { return cobolName.get(); }
        public String getDataType() { return dataType.get(); }
        public String getLength() { return length.get(); }
        public String getFlags() { return flags.get(); }
        public String getDescription() { return description.get(); }
        public boolean isKey() { return isKey; }

        public StringProperty treePrefixProperty() { return treePrefix; }
        public StringProperty columnNameProperty() { return columnName; }
        public StringProperty cobolNameProperty() { return cobolName; }
        public StringProperty dataTypeProperty() { return dataType; }
        public StringProperty lengthProperty() { return length; }
        public StringProperty flagsProperty() { return flags; }
        public StringProperty descriptionProperty() { return description; }
    }
}
