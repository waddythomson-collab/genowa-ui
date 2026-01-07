package com.genowa.ui.screens;

import com.genowa.model.GenTable;
import com.genowa.service.DatabaseService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsLineTableAssignScreen
{
    private VBox view;
    private ComboBox<String> insLineCombo;
    private TableView<GenTable> tableView;
    private ObservableList<GenTable> assignedTables;
    private List<GenTable> allTables;

    public InsLineTableAssignScreen()
    {
        loadAllTables();
        createView();
    }

    private void loadAllTables()
    {
        allTables = new ArrayList<>();
        String sql = "SELECT * FROM gen_tables ORDER BY table_name";
        
        try (PreparedStatement stmt = DatabaseService.getInstance().getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery())
        {
            while (rs.next())
            {
                GenTable table = new GenTable();
                table.setTableId(rs.getInt("table_id"));
                table.setTableName(rs.getString("table_name"));
                table.setTableDesc(rs.getString("table_desc"));
                
                // Handle nullable integer fields
                int parentId = rs.getInt("parent_table_id");
                table.setParentTableId(rs.wasNull() ? null : parentId);
                
                table.setLevelTypeCode(rs.getString("level_type_code"));
                
                int keyLen = rs.getInt("key_length");
                table.setKeyLength(rs.wasNull() ? null : keyLen);
                
                int dataLen = rs.getInt("data_length");
                table.setDataLength(rs.wasNull() ? null : dataLen);
                
                table.setTableType(rs.getString("table_type"));
                table.setRateTableType(rs.getString("rate_table_type"));
                table.setHasDetailYn(rs.getString("has_detail_yn"));
                table.setActiveYn(rs.getString("active_yn"));
                
                allTables.add(table);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private void createView()
    {
        // Initialize assignedTables early - loadInsuranceLines may call loadAssignedTables
        assignedTables = FXCollections.observableArrayList();
        
        view = new VBox(15);
        view.setPadding(new Insets(20));

        // Header
        Label headerLabel = new Label("Insurance Line Table Assignments");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Insurance Line Selector
        HBox selectorBox = new HBox(10);
        selectorBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label insLineLabel = new Label("Insurance Line:");
        insLineCombo = new ComboBox<>();
        insLineCombo.setPrefWidth(300);
        loadInsuranceLines();
        insLineCombo.setOnAction(e -> loadAssignedTables());
        
        selectorBox.getChildren().addAll(insLineLabel, insLineCombo);

        // Buttons
        HBox buttonBox = new HBox(10);
        Button addTableBtn = new Button("Add Table");
        addTableBtn.setOnAction(e -> showAddTableDialog());
        Button removeTableBtn = new Button("Remove Table");
        removeTableBtn.setOnAction(e -> removeSelectedTable());
        Button addMappingBtn = new Button("Add Mapping");
        addMappingBtn.setOnAction(e -> showAddMappingDialog());
        
        buttonBox.getChildren().addAll(addTableBtn, removeTableBtn, addMappingBtn);

        // Table View
        tableView = new TableView<>();
        tableView.setItems(assignedTables);

        TableColumn<GenTable, String> nameCol = new TableColumn<>("Table Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTableName()));
        nameCol.setPrefWidth(200);

        TableColumn<GenTable, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTableDesc()));
        descCol.setPrefWidth(300);

        TableColumn<GenTable, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTableType()));
        typeCol.setPrefWidth(100);

        TableColumn<GenTable, String> levelCol = new TableColumn<>("Level");
        levelCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLevelTypeCode()));
        levelCol.setPrefWidth(100);

        tableView.getColumns().addAll(nameCol, descCol, typeCol, levelCol);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        view.getChildren().addAll(headerLabel, selectorBox, buttonBox, tableView);
    }

    private void loadInsuranceLines()
    {
        insLineCombo.getItems().clear();
        String sql = "SELECT DISTINCT ctl_value FROM gen_ctl WHERE ctl_type = 'INS_LINE' ORDER BY ctl_value";
        
        try (PreparedStatement stmt = DatabaseService.getInstance().getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery())
        {
            while (rs.next())
            {
                insLineCombo.getItems().add(rs.getString("ctl_value"));
            }
        }
        catch (SQLException e)
        {
            // If table doesn't exist, add sample data
            insLineCombo.getItems().addAll("AUTO", "HOME", "LIFE", "HEALTH");
        }
        
        if (!insLineCombo.getItems().isEmpty())
        {
            insLineCombo.getSelectionModel().selectFirst();
            loadAssignedTables();
        }
    }

    private void loadAssignedTables()
    {
        assignedTables.clear();
        String selectedLine = insLineCombo.getValue();
        if (selectedLine == null) return;

        // For now, load first 10 tables as example
        // In production, this would query an assignment table
        int count = 0;
        for (GenTable table : allTables)
        {
            if (count++ < 10)
            {
                assignedTables.add(table);
            }
        }
    }

    private void showAddTableDialog()
    {
        Dialog<GenTable> dialog = new Dialog<>();
        dialog.setTitle("Add Table");
        dialog.setHeaderText("Select a table to add to this insurance line");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        // Searchable ComboBox for tables
        Label tableLabel = new Label("Table Name:");
        ComboBox<GenTable> tableCombo = new ComboBox<>();
        tableCombo.setPrefWidth(400);
        tableCombo.setEditable(true);
        
        // Add all tables to combo
        ObservableList<GenTable> tableList = FXCollections.observableArrayList(allTables);
        FilteredList<GenTable> filteredTables = new FilteredList<>(tableList, p -> true);
        tableCombo.setItems(filteredTables);
        
        // Set up string converter
        tableCombo.setConverter(new StringConverter<GenTable>()
        {
            @Override
            public String toString(GenTable table)
            {
                return table == null ? "" : table.getTableName();
            }

            @Override
            public GenTable fromString(String string)
            {
                if (string == null || string.isEmpty())
                {
                    return null;
                }
                return tableList.stream()
                    .filter(t -> t.getTableName().equalsIgnoreCase(string))
                    .findFirst()
                    .orElse(null);
            }
        });

        // Filter as user types
        tableCombo.getEditor().textProperty().addListener((obs, oldVal, newVal) ->
        {
            final String filterText = newVal == null ? "" : newVal.toLowerCase();
            
            // Don't filter if a selection was just made
            if (tableCombo.getValue() != null && 
                tableCombo.getValue().getTableName().equalsIgnoreCase(filterText))
            {
                return;
            }
            
            filteredTables.setPredicate(table ->
            {
                if (filterText.isEmpty())
                {
                    return true;
                }
                return table.getTableName().toLowerCase().contains(filterText) ||
                       (table.getTableDesc() != null && 
                        table.getTableDesc().toLowerCase().contains(filterText));
            });
            
            if (!tableCombo.isShowing() && !filterText.isEmpty())
            {
                tableCombo.show();
            }
        });

        Label countLabel = new Label("(" + allTables.size() + " tables available)");
        countLabel.setStyle("-fx-text-fill: #666;");

        content.getChildren().addAll(tableLabel, tableCombo, countLabel);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefWidth(500);

        dialog.setResultConverter(dialogButton ->
        {
            if (dialogButton == addButtonType)
            {
                return tableCombo.getValue();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(table ->
        {
            if (table != null && !assignedTables.contains(table))
            {
                assignedTables.add(table);
            }
        });
    }

    private void removeSelectedTable()
    {
        GenTable selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null)
        {
            assignedTables.remove(selected);
        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a table to remove.");
            alert.showAndWait();
        }
    }

    private void showAddMappingDialog()
    {
        GenTable selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null)
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a table first.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Add Mapping");
        alert.setHeaderText("Add Field Mapping for " + selected.getTableName());
        alert.setContentText("Field mapping dialog coming soon...");
        alert.showAndWait();
    }

    public VBox getView()
    {
        return view;
    }
}
