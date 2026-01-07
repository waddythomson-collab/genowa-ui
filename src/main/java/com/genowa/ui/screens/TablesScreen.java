package com.genowa.ui.screens;

import com.genowa.model.GenField;
import com.genowa.model.GenTable;
import com.genowa.service.DatabaseService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Tables Management Screen - View and edit table definitions and their fields.
 */
public class TablesScreen
{
    private BorderPane view;
    private DatabaseService dbService;
    private TableView<GenTable> tablesTableView;
    private TableView<GenField> fieldsTableView;
    private ObservableList<GenTable> tablesList;
    private ObservableList<GenField> fieldsList;
    private TextField searchField;
    private GenTable selectedTable;

    public TablesScreen(DatabaseService dbService)
    {
        this.dbService = dbService;
        this.tablesList = FXCollections.observableArrayList();
        this.fieldsList = FXCollections.observableArrayList();
        createView();
        loadTables();
    }

    private void createView()
    {
        view = new BorderPane();
        view.setPadding(new Insets(10));

        // Title
        Label titleLabel = new Label("Tables Management");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Search bar
        searchField = new TextField();
        searchField.setPromptText("Search tables...");
        searchField.setPrefWidth(300);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterTables(newVal));

        HBox topBar = new HBox(20, titleLabel, searchField);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 0, 10, 0));

        // Split pane for tables list and fields
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.4);

        // Left side - Tables list
        VBox tablesBox = createTablesPanel();

        // Right side - Fields for selected table
        VBox fieldsBox = createFieldsPanel();

        splitPane.getItems().addAll(tablesBox, fieldsBox);

        view.setTop(topBar);
        view.setCenter(splitPane);
    }

    private VBox createTablesPanel()
    {
        VBox box = new VBox(10);
        box.setPadding(new Insets(5));

        Label label = new Label("Tables");
        label.setStyle("-fx-font-weight: bold;");

        // Tables TableView
        tablesTableView = new TableView<>();
        tablesTableView.setItems(tablesList);

        TableColumn<GenTable, String> nameCol = new TableColumn<>("Table Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTableName()));
        nameCol.setPrefWidth(200);

        TableColumn<GenTable, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
        descCol.setPrefWidth(200);

        tablesTableView.getColumns().addAll(nameCol, descCol);

        // Selection listener
        tablesTableView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSel, newSel) ->
            {
                selectedTable = newSel;
                loadFieldsForTable(newSel);
            }
        );

        // Buttons
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> loadTables());

        HBox btnBox = new HBox(10, refreshBtn);

        // Stats label
        Label statsLabel = new Label();
        tablesList.addListener((javafx.collections.ListChangeListener<GenTable>) c ->
            statsLabel.setText(tablesList.size() + " tables")
        );

        box.getChildren().addAll(label, tablesTableView, btnBox, statsLabel);
        VBox.setVgrow(tablesTableView, Priority.ALWAYS);

        return box;
    }

    private VBox createFieldsPanel()
    {
        VBox box = new VBox(10);
        box.setPadding(new Insets(5));

        Label label = new Label("Fields");
        label.setStyle("-fx-font-weight: bold;");

        // Fields TableView
        fieldsTableView = new TableView<>();
        fieldsTableView.setItems(fieldsList);

        TableColumn<GenField, String> seqCol = new TableColumn<>("#");
        seqCol.setCellValueFactory(data ->
            new SimpleStringProperty(String.valueOf(data.getValue().getSeqNo()))
        );
        seqCol.setPrefWidth(40);

        TableColumn<GenField, String> nameCol = new TableColumn<>("Field Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFieldName()));
        nameCol.setPrefWidth(180);

        TableColumn<GenField, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFieldType()));
        typeCol.setPrefWidth(100);

        TableColumn<GenField, String> lengthCol = new TableColumn<>("Length");
        lengthCol.setCellValueFactory(data ->
        {
            Integer len = data.getValue().getFieldLength();
            return new SimpleStringProperty(len != null ? len.toString() : "");
        });
        lengthCol.setPrefWidth(60);

        TableColumn<GenField, String> keyCol = new TableColumn<>("Key");
        keyCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKeyFieldYn()));
        keyCol.setPrefWidth(40);

        fieldsTableView.getColumns().addAll(seqCol, nameCol, typeCol, lengthCol, keyCol);

        // Stats label
        Label fieldStatsLabel = new Label("Select a table to view fields");
        fieldsList.addListener((javafx.collections.ListChangeListener<GenField>) c ->
        {
            if (selectedTable != null)
            {
                fieldStatsLabel.setText(fieldsList.size() + " fields in " + selectedTable.getTableName());
            }
        });

        box.getChildren().addAll(label, fieldsTableView, fieldStatsLabel);
        VBox.setVgrow(fieldsTableView, Priority.ALWAYS);

        return box;
    }

    private void loadTables()
    {
        tablesList.clear();
        String sql = "SELECT table_id, table_name, table_desc, table_type, active_yn FROM gen_tables ORDER BY table_name";

        try (Connection conn = dbService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery())
        {
            while (rs.next())
            {
                GenTable table = new GenTable();
                table.setId(rs.getInt("table_id"));
                table.setTableName(rs.getString("table_name"));
                table.setDescription(rs.getString("table_desc"));
                tablesList.add(table);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            showError("Error loading tables: " + e.getMessage());
        }
    }

    private void loadFieldsForTable(GenTable table)
    {
        fieldsList.clear();
        if (table == null)
        {
            return;
        }

        String sql = "SELECT field_id, field_name, field_type, field_length, field_decimal, " +
                     "seq_no, key_field_yn, active_yn FROM gen_fields " +
                     "WHERE table_id = ? ORDER BY seq_no";

        try (Connection conn = dbService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, table.getId());
            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next())
                {
                    GenField field = new GenField();
                    field.setId(rs.getInt("field_id"));
                    field.setTableId(table.getId());
                    field.setFieldName(rs.getString("field_name"));
                    field.setFieldType(rs.getString("field_type"));
                    field.setFieldLength(rs.getObject("field_length") != null ? rs.getInt("field_length") : null);
                    field.setSeqNo(rs.getInt("seq_no"));
                    field.setKeyFieldYn(rs.getString("key_field_yn"));
                    fieldsList.add(field);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            showError("Error loading fields: " + e.getMessage());
        }
    }

    private void filterTables(String searchText)
    {
        if (searchText == null || searchText.trim().isEmpty())
        {
            loadTables();
            return;
        }

        String filter = "%" + searchText.trim().toUpperCase() + "%";
        tablesList.clear();

        String sql = "SELECT table_id, table_name, table_desc FROM gen_tables " +
                     "WHERE UPPER(table_name) LIKE ? OR UPPER(table_desc) LIKE ? ORDER BY table_name";

        try (Connection conn = dbService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setString(1, filter);
            stmt.setString(2, filter);
            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next())
                {
                    GenTable table = new GenTable();
                    table.setId(rs.getInt("table_id"));
                    table.setTableName(rs.getString("table_name"));
                    table.setDescription(rs.getString("table_desc"));
                    tablesList.add(table);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void showError(String message)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public BorderPane getView()
    {
        return view;
    }
}
