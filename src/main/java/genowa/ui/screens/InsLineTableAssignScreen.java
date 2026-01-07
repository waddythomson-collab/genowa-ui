package genowa.ui.screens;

import genowa.Database;
import genowa.model.*;
import genowa.repository.WarpCtlRepository;
import genowa.repository.WarpCtlDataRepository;
import jakarta.persistence.EntityManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Insurance Line Table Assignment Screen
 * 
 * Manages the assignment of database tables to Insurance Lines and maps
 * Element Values (rating concepts like Primary Break, Coverage Code) to
 * actual column names in each table.
 * 
 * Left Panel (40%): Tree view showing hierarchy:
 *   - Insurance Line
 *     - Level Type (Primary, Premium, Form, etc.)
 *       - Tables (with parent/child relationships)
 * 
 * Right Panel (60%): Grid showing Element Value → Column mappings
 *   for the selected table
 */
public class InsLineTableAssignScreen extends BorderPane
{
    
    // Left panel - table hierarchy tree
    private TreeView<TreeNodeData> tableTree;
    private TreeItem<TreeNodeData> rootItem;
    
    // Right panel - element value mappings
    private TableView<ElementMapping> mappingTable;
    private ObservableList<ElementMapping> mappings = FXCollections.observableArrayList();
    
    // Toolbar controls
    private ComboBox<String> insLineCombo;
    
    // Currently selected context
    private String currentInsLine;
    private LevelType currentLevelType;
    private String currentTableName;
    
    // Database repositories
    private WarpCtlRepository ctlRepository;
    private WarpCtlDataRepository ctlDataRepository;
    
    // Available columns for the current table (loaded from gen_fields)
    private List<String> availableColumns = new ArrayList<>();
    
    public InsLineTableAssignScreen()
    {
        setPadding(new Insets(10));
        
        // Initialize repositories
        initializeRepositories();
        
        // Top toolbar
        setTop(createToolbar());
        
        // Main content - split pane
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPositions(0.4);
        
        // Left: Table hierarchy tree
        VBox leftPanel = createLeftPanel();
        
        // Right: Element value mappings
        VBox rightPanel = createRightPanel();
        
        splitPane.getItems().addAll(leftPanel, rightPanel);
        setCenter(splitPane);
        
        // Setup keyboard shortcuts
        setupKeyboardShortcuts();
        
        // Load data from database
        loadInsuranceLines();
    }
    
    private void initializeRepositories()
    {
        EntityManager em = Database.getInstance().createEntityManager();
        ctlRepository = new WarpCtlRepository(em);
        ctlDataRepository = new WarpCtlDataRepository(em);
    }
    
    private ToolBar createToolbar()
    {
        ToolBar toolbar = new ToolBar();
        
        // Insurance Line selector
        Label insLineLabel = new Label("Insurance Line:");
        insLineCombo = new ComboBox<>();
        insLineCombo.setPrefWidth(200);
        insLineCombo.setOnAction(e -> onInsLineChanged());
        
        // Action buttons
        Button addTableBtn = new Button("Add Table");
        addTableBtn.setOnAction(e -> onAddTable());
        
        Button changeBtn = new Button("Change");
        changeBtn.setOnAction(e -> onChangeTable());
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> onDeleteTable());
        
        Button copyBtn = new Button("Copy");
        copyBtn.setOnAction(e -> onCopyTable());
        
        Separator sep1 = new Separator(Orientation.VERTICAL);
        
        // Generate buttons
        Button genCtlBtn = new Button("Gen CTL");
        genCtlBtn.setTooltip(new Tooltip("Generate Control File"));
        genCtlBtn.setOnAction(e -> onGenerateCtl());
        
        Button genLkgBtn = new Button("Gen Linkage");
        genLkgBtn.setTooltip(new Tooltip("Generate Linkage Section"));
        genLkgBtn.setOnAction(e -> onGenerateLinkage());
        
        Separator sep2 = new Separator(Orientation.VERTICAL);
        
        Button printBtn = new Button("Print");
        printBtn.setOnAction(e -> onPrint());
        
        toolbar.getItems().addAll(
            insLineLabel, insLineCombo,
            new Separator(Orientation.VERTICAL),
            addTableBtn, changeBtn, deleteBtn, copyBtn,
            sep1,
            genCtlBtn, genLkgBtn,
            sep2,
            printBtn
        );
        
        return toolbar;
    }
    
    private VBox createLeftPanel()
    {
        VBox panel = new VBox(5);
        panel.setPadding(new Insets(5));
        
        Label titleLabel = new Label("Table Hierarchy");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        // Tree view for table hierarchy
        tableTree = new TreeView<>();
        tableTree.setShowRoot(false);
        tableTree.setCellFactory(tv -> new TreeCell<>()
        {
            @Override
            protected void updateItem(TreeNodeData item, boolean empty)
            {
                super.updateItem(item, empty);
                if (empty || item == null)
                {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                }
                else
                {
                    setText(item.displayText);
                    // Style level types differently
                    if (item.nodeType == NodeType.LEVEL_TYPE)
                    {
                        setStyle("-fx-font-weight: bold; -fx-text-fill: #0066cc;");
                    }
                    else if (item.nodeType == NodeType.TABLE)
                    {
                        setStyle("");
                    }
                }
            }
        });
        
        // Selection listener
        tableTree.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> onTreeSelectionChanged(newVal)
        );
        
        // Context menu for tree
        tableTree.setContextMenu(createTreeContextMenu());
        
        VBox.setVgrow(tableTree, Priority.ALWAYS);
        panel.getChildren().addAll(titleLabel, tableTree);
        
        return panel;
    }
    
    private VBox createRightPanel()
    {
        VBox panel = new VBox(5);
        panel.setPadding(new Insets(5));
        
        Label titleLabel = new Label("Element Value Mappings");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        // Table for element value mappings
        mappingTable = new TableView<>(mappings);
        mappingTable.setEditable(true);
        mappingTable.setPlaceholder(new Label("Select a table to view element mappings"));
        
        // Element Name column (read-only)
        TableColumn<ElementMapping, String> elementNameCol = new TableColumn<>("Element Name");
        elementNameCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getElementLabel()));
        elementNameCol.setPrefWidth(200);
        elementNameCol.setEditable(false);
        
        // Element Value column (editable - column name)
        TableColumn<ElementMapping, String> elementValueCol = new TableColumn<>("Element Value");
        elementValueCol.setCellValueFactory(data -> 
            data.getValue().columnNameProperty());
        elementValueCol.setPrefWidth(200);
        elementValueCol.setEditable(true);
        
        // Make it a combo box for selecting columns
        elementValueCol.setCellFactory(col ->
        {
            ComboBoxTableCell<ElementMapping, String> cell = 
                new ComboBoxTableCell<>(FXCollections.observableArrayList(availableColumns));
            return cell;
        });
        
        elementValueCol.setOnEditCommit(event ->
        {
            event.getRowValue().setColumnName(event.getNewValue());
            saveElementMapping(event.getRowValue());
        });
        
        mappingTable.getColumns().addAll(elementNameCol, elementValueCol);
        
        // Context menu for mappings
        mappingTable.setContextMenu(createMappingContextMenu());
        
        VBox.setVgrow(mappingTable, Priority.ALWAYS);
        panel.getChildren().addAll(titleLabel, mappingTable);
        
        return panel;
    }
    
    private ContextMenu createTreeContextMenu()
    {
        ContextMenu menu = new ContextMenu();
        
        MenuItem addItem = new MenuItem("Add Table...");
        addItem.setOnAction(e -> onAddTable());
        
        MenuItem changeItem = new MenuItem("Change Table...");
        changeItem.setOnAction(e -> onChangeTable());
        
        MenuItem deleteItem = new MenuItem("Delete Table");
        deleteItem.setOnAction(e -> onDeleteTable());
        
        MenuItem copyItem = new MenuItem("Copy Table...");
        copyItem.setOnAction(e -> onCopyTable());
        
        SeparatorMenuItem sep1 = new SeparatorMenuItem();
        
        MenuItem deleteCompItem = new MenuItem("Delete Component");
        deleteCompItem.setOnAction(e -> onDeleteComponent());
        
        MenuItem deleteAllItem = new MenuItem("Delete All for Insurance Line");
        deleteAllItem.setOnAction(e -> onDeleteAll());
        
        SeparatorMenuItem sep2 = new SeparatorMenuItem();
        
        MenuItem genCtlItem = new MenuItem("Generate Control File");
        genCtlItem.setOnAction(e -> onGenerateCtl());
        
        MenuItem genLkgItem = new MenuItem("Generate Linkage Section");
        genLkgItem.setOnAction(e -> onGenerateLinkage());
        
        MenuItem migrateItem = new MenuItem("Migrate CTL Data");
        migrateItem.setOnAction(e -> onMigrate());
        
        menu.getItems().addAll(
            addItem, changeItem, deleteItem, copyItem,
            sep1,
            deleteCompItem, deleteAllItem,
            sep2,
            genCtlItem, genLkgItem, migrateItem
        );
        
        return menu;
    }
    
    private ContextMenu createMappingContextMenu()
    {
        ContextMenu menu = new ContextMenu();
        
        MenuItem clearItem = new MenuItem("Clear Mapping");
        clearItem.setOnAction(e ->
        {
            ElementMapping selected = mappingTable.getSelectionModel().getSelectedItem();
            if (selected != null)
            {
                selected.setColumnName("");
                mappingTable.refresh();
                saveElementMapping(selected);
            }
        });
        
        MenuItem clearAllItem = new MenuItem("Clear All Mappings");
        clearAllItem.setOnAction(e ->
        {
            for (ElementMapping mapping : mappings)
            {
                mapping.setColumnName("");
            }
            mappingTable.refresh();
            // Save all cleared mappings
            saveAllElementMappings();
        });
        
        menu.getItems().addAll(clearItem, clearAllItem);
        
        return menu;
    }
    
    private void setupKeyboardShortcuts()
    {
        // Ctrl+A = Add
        setOnKeyPressed(e ->
        {
            if (new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN).match(e))
            {
                onAddTable();
                e.consume();
            }
            else if (new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN).match(e))
            {
                onChangeTable();
                e.consume();
            }
            else if (new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN).match(e))
            {
                onDeleteTable();
                e.consume();
            }
            else if (e.getCode() == KeyCode.DELETE)
            {
                onDeleteTable();
                e.consume();
            }
        });
    }
    
    /**
     * Load insurance lines from database (distinct values from gen_ctl)
     */
    private void loadInsuranceLines()
    {
        List<String> insLines = Database.getInstance().executeRead(em ->
        {
            return em.createQuery(
                "SELECT DISTINCT c.id.insuranceLineCode FROM WarpCtl c ORDER BY c.id.insuranceLineCode",
                String.class
            ).getResultList();
        });
        
        insLineCombo.getItems().clear();
        insLineCombo.getItems().addAll(insLines);
        
        // Select first and trigger load
        if (!insLines.isEmpty())
        {
            insLineCombo.getSelectionModel().selectFirst();
            onInsLineChanged();
        }
    }
    
    /**
     * Called when insurance line selection changes - reload table tree
     */
    private void onInsLineChanged()
    {
        String selected = insLineCombo.getValue();
        if (selected == null) return;
        
        currentInsLine = selected;
        
        // Build tree structure
        rootItem = new TreeItem<>(new TreeNodeData("Root", NodeType.ROOT));
        rootItem.setExpanded(true);
        
        // Query tables for this insurance line from database
        List<WarpCtl> ctlRecords = ctlRepository.findByInsuranceLine(currentInsLine);
        
        // Group by level type
        Map<String, List<WarpCtl>> byLevelType = ctlRecords.stream()
            .collect(Collectors.groupingBy(c -> c.getLevelType().getCode()));
        
        // Add level types as main branches
        for (LevelType levelType : LevelType.values())
        {
            TreeItem<TreeNodeData> levelItem = new TreeItem<>(
                new TreeNodeData(levelType.getDisplayName(), NodeType.LEVEL_TYPE, levelType)
            );
            levelItem.setExpanded(true);
            
            // Add tables for this level type
            List<WarpCtl> tables = byLevelType.get(levelType.getCode());
            if (tables != null)
            {
                // Sort by level number, then sub-level
                tables.sort(Comparator
                    .comparing((WarpCtl c) -> c.getLevel())
                    .thenComparing(c -> c.getSubLevel()));
                
                // Build parent-child relationships
                addTablesWithHierarchy(levelItem, tables, levelType);
            }
            
            rootItem.getChildren().add(levelItem);
        }
        
        tableTree.setRoot(rootItem);
        
        // Clear mappings
        mappings.clear();
    }
    
    /**
     * Add tables to tree respecting parent-child hierarchy based on db_parent_nm
     */
    private void addTablesWithHierarchy(TreeItem<TreeNodeData> parentItem, 
                                         List<WarpCtl> tables, LevelType levelType)
    {
        // First, find root tables (no parent)
        Map<String, TreeItem<TreeNodeData>> tableItems = new HashMap<>();
        
        for (WarpCtl ctl : tables)
        {
            String tableName = ctl.getId().getTableName().trim();
            String parentName = ctl.getParentTableName();
            
            TreeItem<TreeNodeData> tableItem = new TreeItem<>(
                new TreeNodeData(tableName, NodeType.TABLE, levelType, tableName)
            );
            tableItem.setExpanded(true);
            tableItems.put(tableName, tableItem);
        }
        
        // Now build hierarchy
        for (WarpCtl ctl : tables)
        {
            String tableName = ctl.getId().getTableName().trim();
            String parentName = ctl.getParentTableName();
            TreeItem<TreeNodeData> tableItem = tableItems.get(tableName);
            
            if (parentName == null || parentName.trim().isEmpty())
            {
                // Root level table
                parentItem.getChildren().add(tableItem);
            }
            else
            {
                // Child table - add under parent
                TreeItem<TreeNodeData> parentTableItem = tableItems.get(parentName.trim());
                if (parentTableItem != null)
                {
                    // Indent display
                    tableItem.getValue().displayText = "  └ " + tableName;
                    parentTableItem.getChildren().add(tableItem);
                }
                else
                {
                    // Parent not found, add to root
                    parentItem.getChildren().add(tableItem);
                }
            }
        }
    }
    
    /**
     * Called when a tree node is selected
     */
    private void onTreeSelectionChanged(TreeItem<TreeNodeData> selected)
    {
        if (selected == null)
        {
            mappings.clear();
            return;
        }
        
        TreeNodeData data = selected.getValue();
        if (data.nodeType == NodeType.TABLE)
        {
            currentLevelType = data.levelType;
            currentTableName = data.tableName;
            loadAvailableColumns();
            loadElementMappings();
        }
        else
        {
            mappings.clear();
            currentTableName = null;
        }
    }
    
    /**
     * Load available columns for the current table from gen_fields
     */
    private void loadAvailableColumns()
    {
        availableColumns.clear();
        availableColumns.add(""); // Empty option
        
        List<String> columns = Database.getInstance().executeRead(em ->
        {
            // First find the table_index_nbr for this table
            @SuppressWarnings("unchecked")
            List<String> results = em.createNativeQuery(
                "SELECT f.column_nm FROM gen_fields f " +
                "JOIN gen_tables t ON f.table_index_nbr = t.table_index_nbr " +
                "WHERE t.table_nm = :tableName " +
                "ORDER BY f.key_cct_nbr"
            ).setParameter("tableName", currentTableName.trim())
            .getResultList();
            
            return results.stream()
                .map(String::trim)
                .collect(Collectors.toList());
        });
        
        availableColumns.addAll(columns);
        
        // Refresh the combo box in the mapping table
        mappingTable.refresh();
    }
    
    /**
     * Load element value mappings from gen_ctl_data for the current table
     */
    private void loadElementMappings()
    {
        mappings.clear();
        
        if (currentLevelType == null || currentTableName == null) return;
        
        // Query existing mappings from database
        List<WarpCtlData> existingMappings = ctlDataRepository.findByTable(
            currentInsLine, currentLevelType, currentTableName.trim()
        );
        
        // Create a map of controlCode -> columnName for quick lookup
        Map<String, String> mappingLookup = existingMappings.stream()
            .collect(Collectors.toMap(
                d -> d.getId().getControlCode().trim(),
                d -> d.getColumnName() != null ? d.getColumnName().trim() : "",
                (a, b) -> a // In case of duplicates, keep first
            ));
        
        // Load element values for the current level type
        for (ElementValue ev : ElementValue.getActiveValues(currentLevelType))
        {
            ElementMapping mapping = new ElementMapping(ev, currentLevelType);
            
            // Look up existing mapping from database
            String columnName = mappingLookup.get(ev.getCode());
            if (columnName != null)
            {
                mapping.setColumnName(columnName);
            }
            
            mappings.add(mapping);
        }
    }
    
    /**
     * Save a single element mapping to the database
     */
    private void saveElementMapping(ElementMapping mapping)
    {
        if (currentInsLine == null || currentLevelType == null || currentTableName == null)
        {
            return;
        }
        
        Database.getInstance().executeWrite(em ->
        {
            String controlCode = mapping.getElementValue().getCode();
            String columnName = mapping.getColumnName();
            
            // Try to find existing record
            WarpCtlData existing = em.find(WarpCtlData.class,
                new WarpCtlDataId(currentInsLine, currentLevelType.getCode(),
                    currentTableName.trim(), controlCode));
            
            if (columnName == null || columnName.trim().isEmpty())
            {
                // Delete mapping if column cleared
                if (existing != null)
                {
                    em.remove(existing);
                }
            }
            else
            {
                // Insert or update
                if (existing != null)
                {
                    existing.setColumnName(columnName.trim());
                    em.merge(existing);
                }
                else
                {
                    WarpCtlData newMapping = new WarpCtlData(
                        currentInsLine, currentLevelType.getCode(),
                        currentTableName.trim(), controlCode, columnName.trim()
                    );
                    em.persist(newMapping);
                }
            }
        });
        
        System.out.println("Saved mapping: " + mapping.getElementValue().getCode() + 
            " -> " + mapping.getColumnName());
    }
    
    /**
     * Save all element mappings (used when clearing all)
     */
    private void saveAllElementMappings()
    {
        for (ElementMapping mapping : mappings)
        {
            saveElementMapping(mapping);
        }
    }
    
    // ===== Action handlers =====
    
    private void onAddTable()
    {
        showTableDialog("Add Table Assignment", null);
    }
    
    private void onChangeTable()
    {
        TreeItem<TreeNodeData> selected = tableTree.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getValue().nodeType == NodeType.TABLE)
        {
            showTableDialog("Change Table Assignment", selected.getValue());
        }
    }
    
    private void onDeleteTable()
    {
        TreeItem<TreeNodeData> selected = tableTree.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getValue().nodeType == NodeType.TABLE)
        {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Delete Table");
            confirm.setHeaderText("Delete table assignment?");
            confirm.setContentText("Table: " + selected.getValue().tableName + 
                "\n\nThis will also delete all element mappings for this table.");
            
            confirm.showAndWait().ifPresent(response ->
            {
                if (response == ButtonType.OK)
                {
                    deleteTableFromDatabase(selected.getValue());
                    selected.getParent().getChildren().remove(selected);
                    mappings.clear();
                }
            });
        }
    }
    
    private void deleteTableFromDatabase(TreeNodeData data)
    {
        Database.getInstance().executeWrite(em ->
        {
            // Delete from gen_ctl_data first
            em.createNativeQuery(
                "DELETE FROM gen_ctl_data WHERE warp_ins_line_cd = :insLine " +
                "AND level_type_cd = :levelType AND db_table_nm = :tableName"
            ).setParameter("insLine", currentInsLine)
            .setParameter("levelType", data.levelType.getCode())
            .setParameter("tableName", data.tableName.trim())
            .executeUpdate();
            
            // Then delete from gen_ctl
            em.createNativeQuery(
                "DELETE FROM gen_ctl WHERE warp_ins_line_cd = :insLine " +
                "AND level_type_cd = :levelType AND db_table_nm = :tableName"
            ).setParameter("insLine", currentInsLine)
            .setParameter("levelType", data.levelType.getCode())
            .setParameter("tableName", data.tableName.trim())
            .executeUpdate();
        });
    }
    
    private void onCopyTable()
    {
        TreeItem<TreeNodeData> selected = tableTree.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getValue().nodeType == NodeType.TABLE)
        {
            showCopyTableDialog(selected.getValue());
        }
    }
    
    private void onDeleteComponent()
    {
        showInfo("Delete Component", "Would delete all tables and mappings for the selected level type.");
    }
    
    private void onDeleteAll()
    {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete All");
        confirm.setHeaderText("Delete all table assignments?");
        confirm.setContentText("This will remove ALL table assignments for Insurance Line: " + currentInsLine);
        
        confirm.showAndWait().ifPresent(response ->
        {
            if (response == ButtonType.OK)
            {
                deleteAllForInsuranceLine();
                // Clear all level types in tree
                for (TreeItem<TreeNodeData> levelItem : rootItem.getChildren())
                {
                    levelItem.getChildren().clear();
                }
                mappings.clear();
            }
        });
    }
    
    private void deleteAllForInsuranceLine()
    {
        Database.getInstance().executeWrite(em ->
        {
            // Delete from gen_ctl_data first
            em.createNativeQuery(
                "DELETE FROM gen_ctl_data WHERE warp_ins_line_cd = :insLine"
            ).setParameter("insLine", currentInsLine).executeUpdate();
            
            // Then delete from gen_ctl
            em.createNativeQuery(
                "DELETE FROM gen_ctl WHERE warp_ins_line_cd = :insLine"
            ).setParameter("insLine", currentInsLine).executeUpdate();
        });
    }
    
    private void onGenerateCtl()
    {
        showInfo("Generate Control File", 
            "Would generate the control file for Insurance Line: " + currentInsLine);
    }
    
    private void onGenerateLinkage()
    {
        showInfo("Generate Linkage", 
            "Would generate the COBOL LINKAGE SECTION for Insurance Line: " + currentInsLine);
    }
    
    private void onMigrate()
    {
        showInfo("Migrate CTL Data", 
            "Would migrate table assignment data from another Insurance Line.");
    }
    
    private void onPrint()
    {
        showInfo("Print", "Would print the table assignment report.");
    }
    
    private void showTableDialog(String title, TreeNodeData existingData)
    {
        Dialog<TreeNodeData> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(existingData == null ? "Add a new table assignment" : "Modify table assignment");
        
        // Form fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        ComboBox<LevelType> levelTypeCombo = new ComboBox<>();
        levelTypeCombo.getItems().addAll(LevelType.values());
        levelTypeCombo.setConverter(new javafx.util.StringConverter<>()
        {
            @Override
            public String toString(LevelType lt)
            {
                return lt == null ? "" : lt.getDisplayName();
            }
            @Override
            public LevelType fromString(String s)
            {
                return null;
            }
        });
        
        // Load available tables from gen_tables
        ComboBox<String> tableNameCombo = new ComboBox<>();
        List<String> availableTables = Database.getInstance().executeRead(em ->
        {
            @SuppressWarnings("unchecked")
            List<String> results = em.createNativeQuery(
                "SELECT table_nm FROM gen_tables ORDER BY table_nm"
            ).getResultList();
            return results.stream()
                .map(String::trim)
                .collect(Collectors.toList());
        });
        tableNameCombo.getItems().addAll(availableTables);
        tableNameCombo.setEditable(true);
        tableNameCombo.setPrefWidth(200);
        
        // Parent table - populated with tables from current ins line
        ComboBox<String> parentTableCombo = new ComboBox<>();
        parentTableCombo.getItems().add("");
        List<WarpCtl> existingTables = ctlRepository.findByInsuranceLine(currentInsLine);
        existingTables.stream()
            .map(c -> c.getId().getTableName().trim())
            .distinct()
            .sorted()
            .forEach(t -> parentTableCombo.getItems().add(t));
        parentTableCombo.getSelectionModel().selectFirst();
        
        Spinner<Integer> levelSpinner = new Spinner<>(1, 10, 1);
        Spinner<Integer> subLevelSpinner = new Spinner<>(0, 99, 0);
        
        grid.add(new Label("Level Type:"), 0, 0);
        grid.add(levelTypeCombo, 1, 0);
        grid.add(new Label("Table Name:"), 0, 1);
        grid.add(tableNameCombo, 1, 1);
        grid.add(new Label("Parent Table:"), 0, 2);
        grid.add(parentTableCombo, 1, 2);
        grid.add(new Label("Level:"), 0, 3);
        grid.add(levelSpinner, 1, 3);
        grid.add(new Label("Sub-Level:"), 0, 4);
        grid.add(subLevelSpinner, 1, 4);
        
        // Pre-fill if editing
        if (existingData != null)
        {
            levelTypeCombo.setValue(existingData.levelType);
            tableNameCombo.setValue(existingData.tableName);
        }
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button ->
        {
            if (button == ButtonType.OK)
            {
                LevelType lt = levelTypeCombo.getValue();
                String tableName = tableNameCombo.getValue();
                String parentTable = parentTableCombo.getValue();
                int level = levelSpinner.getValue();
                int subLevel = subLevelSpinner.getValue();
                
                if (lt != null && tableName != null && !tableName.trim().isEmpty())
                {
                    saveTableAssignment(lt, tableName.trim(), parentTable, level, subLevel, existingData);
                    onInsLineChanged(); // Refresh tree
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    private void saveTableAssignment(LevelType levelType, String tableName, 
                                      String parentTable, int level, int subLevel,
                                      TreeNodeData existingData)
    {
        Database.getInstance().executeWrite(em ->
        {
            // If editing, delete old record first
            if (existingData != null)
            {
                em.createNativeQuery(
                    "DELETE FROM gen_ctl WHERE warp_ins_line_cd = :insLine " +
                    "AND level_type_cd = :levelType AND db_table_nm = :tableName"
                ).setParameter("insLine", currentInsLine)
                .setParameter("levelType", existingData.levelType.getCode())
                .setParameter("tableName", existingData.tableName.trim())
                .executeUpdate();
            }
            
            // Insert new record
            em.createNativeQuery(
                "INSERT INTO gen_ctl (warp_ins_line_cd, level_type_cd, db_ctl_level_nbr, " +
                "db_sub_level_nbr, db_table_nm, db_parent_nm) VALUES (:insLine, :levelType, " +
                ":level, :subLevel, :tableName, :parentName)"
            ).setParameter("insLine", currentInsLine)
            .setParameter("levelType", levelType.getCode())
            .setParameter("level", level)
            .setParameter("subLevel", subLevel)
            .setParameter("tableName", tableName)
            .setParameter("parentName", parentTable != null && !parentTable.isEmpty() ? parentTable : null)
            .executeUpdate();
        });
    }
    
    private void showCopyTableDialog(TreeNodeData sourceData)
    {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Copy Table Assignment");
        dialog.setHeaderText("Copy " + sourceData.tableName + " to another Insurance Line");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        // Target insurance line
        ComboBox<String> targetLineCombo = new ComboBox<>();
        targetLineCombo.getItems().addAll(insLineCombo.getItems());
        targetLineCombo.getItems().remove(currentInsLine); // Remove current
        
        TextField newTableName = new TextField(sourceData.tableName);
        
        grid.add(new Label("Target Insurance Line:"), 0, 0);
        grid.add(targetLineCombo, 1, 0);
        grid.add(new Label("New Table Name:"), 0, 1);
        grid.add(newTableName, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(button ->
        {
            if (button == ButtonType.OK)
            {
                String targetLine = targetLineCombo.getValue();
                String newName = newTableName.getText();
                if (targetLine != null && newName != null && !newName.trim().isEmpty())
                {
                    copyTableToInsLine(sourceData, targetLine, newName.trim());
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    private void copyTableToInsLine(TreeNodeData source, String targetInsLine, String newTableName)
    {
        Database.getInstance().executeWrite(em ->
        {
            // Get source record
            List<Object[]> sourceCtl = em.createNativeQuery(
                "SELECT db_ctl_level_nbr, db_sub_level_nbr, db_parent_nm FROM gen_ctl " +
                "WHERE warp_ins_line_cd = :insLine AND level_type_cd = :levelType " +
                "AND db_table_nm = :tableName"
            ).setParameter("insLine", currentInsLine)
            .setParameter("levelType", source.levelType.getCode())
            .setParameter("tableName", source.tableName.trim())
            .getResultList();
            
            if (!sourceCtl.isEmpty())
            {
                Object[] row = sourceCtl.get(0);
                // Insert new record
                em.createNativeQuery(
                    "INSERT INTO gen_ctl (warp_ins_line_cd, level_type_cd, db_ctl_level_nbr, " +
                    "db_sub_level_nbr, db_table_nm, db_parent_nm) VALUES (:insLine, :levelType, " +
                    ":level, :subLevel, :tableName, :parentName)"
                ).setParameter("insLine", targetInsLine)
                .setParameter("levelType", source.levelType.getCode())
                .setParameter("level", row[0])
                .setParameter("subLevel", row[1])
                .setParameter("tableName", newTableName)
                .setParameter("parentName", row[2])
                .executeUpdate();
                
                // Copy element mappings
                em.createNativeQuery(
                    "INSERT INTO gen_ctl_data (warp_ins_line_cd, level_type_cd, db_table_nm, " +
                    "warp_ctl_cd, db_col_nm) SELECT :targetLine, level_type_cd, :newTableName, " +
                    "warp_ctl_cd, db_col_nm FROM gen_ctl_data WHERE warp_ins_line_cd = :sourceInsLine " +
                    "AND db_table_nm = :sourceTableName"
                ).setParameter("targetLine", targetInsLine)
                .setParameter("newTableName", newTableName)
                .setParameter("sourceInsLine", currentInsLine)
                .setParameter("sourceTableName", source.tableName.trim())
                .executeUpdate();
            }
        });
        
        showInfo("Copy Complete", "Table " + source.tableName + " copied to " + 
            targetInsLine + " as " + newTableName);
    }
    
    private void showInfo(String title, String message)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // ===== Inner classes =====
    
    private enum NodeType
    {
        ROOT, LEVEL_TYPE, TABLE
    }
    
    private static class TreeNodeData
    {
        String displayText;
        NodeType nodeType;
        LevelType levelType;
        String tableName;
        
        TreeNodeData(String displayText, NodeType nodeType)
        {
            this.displayText = displayText;
            this.nodeType = nodeType;
        }
        
        TreeNodeData(String displayText, NodeType nodeType, LevelType levelType)
        {
            this(displayText, nodeType);
            this.levelType = levelType;
        }
        
        TreeNodeData(String displayText, NodeType nodeType, LevelType levelType, String tableName)
        {
            this(displayText, nodeType, levelType);
            this.tableName = tableName;
        }
        
        @Override
        public String toString()
        {
            return displayText;
        }
    }
    
    /**
     * Represents a single Element Value → Column Name mapping
     */
    public static class ElementMapping
    {
        private final ElementValue elementValue;
        private final LevelType levelType;
        private final SimpleStringProperty columnName = new SimpleStringProperty("");
        
        public ElementMapping(ElementValue ev, LevelType levelType)
        {
            this.elementValue = ev;
            this.levelType = levelType;
        }
        
        public String getElementLabel()
        {
            return elementValue.getLabel(levelType);
        }
        
        public ElementValue getElementValue()
        {
            return elementValue;
        }
        
        public String getColumnName()
        {
            return columnName.get();
        }
        
        public void setColumnName(String value)
        {
            columnName.set(value == null ? "" : value);
        }
        
        public SimpleStringProperty columnNameProperty()
        {
            return columnName;
        }
    }
}
