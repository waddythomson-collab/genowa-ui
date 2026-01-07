package genowa.ui.components;

import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.function.Consumer;

/**
 * TreeView component for selecting elements to insert into algorithms and conditionals.
 * Categories: Database Elements, Rate Keys, Working Storage, Rating Breaks,
 *             User Exits, Rounding, Constants, Date Routine
 */
public class ElementPicker extends VBox {

    public enum ElementType {
        DATABASE_ELEMENT("Database Elements"),
        RATE_KEY("Rate Keys"),
        WORKING_STORAGE("Working Storage"),
        RATING_BREAK("Rating Breaks"),
        USER_EXIT("User Exits"),
        ROUNDING("Rounding"),
        CONSTANT("Constants"),
        DATE_ROUTINE("Date Routine");

        private final String displayName;
        ElementType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    /**
     * Represents an element that can be selected.
     */
    public static class Element {
        private final ElementType type;
        private final String name;
        private final String description;
        private final Object data; // Original entity (DbColumn, RateKey, etc.)

        public Element(ElementType type, String name, String description, Object data) {
            this.type = type;
            this.name = name;
            this.description = description;
            this.data = data;
        }

        public ElementType getType() { return type; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public Object getData() { return data; }

        @Override
        public String toString() {
            return name + (description != null && !description.isEmpty() ? " - " + description : "");
        }
    }

    private final TreeView<Object> treeView;
    private final TreeItem<Object> rootItem;
    private final TextField searchField;
    private Consumer<Element> onElementSelected;

    // Category items for lazy loading
    private final TreeItem<Object> dbElementsItem;
    private final TreeItem<Object> rateKeysItem;
    private final TreeItem<Object> workingStorageItem;
    private final TreeItem<Object> ratingBreaksItem;
    private final TreeItem<Object> userExitsItem;
    private final TreeItem<Object> roundingItem;
    private final TreeItem<Object> constantsItem;
    private final TreeItem<Object> dateRoutineItem;

    public ElementPicker() {
        setSpacing(5);
        setStyle("-fx-padding: 5;");

        // Search field
        searchField = new TextField();
        searchField.setPromptText("Search elements...");
        searchField.textProperty().addListener((obs, old, newVal) -> filterElements(newVal));

        // Tree structure
        rootItem = new TreeItem<>("Elements");
        rootItem.setExpanded(true);

        dbElementsItem = createCategoryItem(ElementType.DATABASE_ELEMENT);
        rateKeysItem = createCategoryItem(ElementType.RATE_KEY);
        workingStorageItem = createCategoryItem(ElementType.WORKING_STORAGE);
        ratingBreaksItem = createCategoryItem(ElementType.RATING_BREAK);
        userExitsItem = createCategoryItem(ElementType.USER_EXIT);
        roundingItem = createCategoryItem(ElementType.ROUNDING);
        constantsItem = createCategoryItem(ElementType.CONSTANT);
        dateRoutineItem = createCategoryItem(ElementType.DATE_ROUTINE);

        rootItem.getChildren().addAll(
            dbElementsItem, rateKeysItem, workingStorageItem, ratingBreaksItem,
            userExitsItem, roundingItem, constantsItem, dateRoutineItem
        );

        treeView = new TreeView<>(rootItem);
        treeView.setShowRoot(false);
        treeView.setCellFactory(tv -> new ElementTreeCell());
        VBox.setVgrow(treeView, Priority.ALWAYS);

        // Double-click to select
        treeView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                selectCurrentItem();
            }
        });

        // Enter key to select
        treeView.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ENTER) {
                selectCurrentItem();
            }
        });

        getChildren().addAll(searchField, treeView);

        // Load sample data
        loadSampleData();
    }

    private TreeItem<Object> createCategoryItem(ElementType type) {
        TreeItem<Object> item = new TreeItem<>(type);
        // Expand listener for lazy loading
        item.expandedProperty().addListener((obs, wasExpanded, isExpanded) -> {
            if (isExpanded && item.getChildren().isEmpty()) {
                loadElementsForCategory(type, item);
            }
        });
        return item;
    }

    private void loadElementsForCategory(ElementType type, TreeItem<Object> categoryItem) {
        // This will be replaced with actual database queries
        // For now, loaded via loadSampleData
    }

    private void selectCurrentItem() {
        TreeItem<Object> selected = treeView.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getValue() instanceof Element element) {
            if (onElementSelected != null) {
                onElementSelected.accept(element);
            }
        }
    }

    private void filterElements(String filter) {
        if (filter == null || filter.isEmpty()) {
            // Show all
            for (TreeItem<Object> category : rootItem.getChildren()) {
                category.setExpanded(false);
                showAllChildren(category);
            }
        } else {
            String lowerFilter = filter.toLowerCase();
            for (TreeItem<Object> category : rootItem.getChildren()) {
                boolean hasMatch = false;
                for (TreeItem<Object> child : category.getChildren()) {
                    boolean matches = child.getValue().toString().toLowerCase().contains(lowerFilter);
                    // Can't hide TreeItems directly, but we can manage visibility via filtering
                    if (matches) hasMatch = true;
                }
                if (hasMatch) {
                    category.setExpanded(true);
                }
            }
        }
    }

    private void showAllChildren(TreeItem<Object> item) {
        // Reset visibility (TreeView doesn't support hiding, so this is a placeholder)
    }

    public void setOnElementSelected(Consumer<Element> handler) {
        this.onElementSelected = handler;
    }

    public void addElement(ElementType type, String name, String description, Object data) {
        Element element = new Element(type, name, description, data);
        TreeItem<Object> categoryItem = getCategoryItem(type);
        categoryItem.getChildren().add(new TreeItem<>(element));
    }

    private TreeItem<Object> getCategoryItem(ElementType type) {
        return switch (type) {
            case DATABASE_ELEMENT -> dbElementsItem;
            case RATE_KEY -> rateKeysItem;
            case WORKING_STORAGE -> workingStorageItem;
            case RATING_BREAK -> ratingBreaksItem;
            case USER_EXIT -> userExitsItem;
            case ROUNDING -> roundingItem;
            case CONSTANT -> constantsItem;
            case DATE_ROUTINE -> dateRoutineItem;
        };
    }

    public void clearElements(ElementType type) {
        getCategoryItem(type).getChildren().clear();
    }

    public void clearAllElements() {
        for (ElementType type : ElementType.values()) {
            clearElements(type);
        }
    }

    private void loadSampleData() {
        // Database Elements - sample auto insurance fields
        addElement(ElementType.DATABASE_ELEMENT, "PA_DRV_VEH_CLS_CD", "Vehicle Class Code", null);
        addElement(ElementType.DATABASE_ELEMENT, "PA_MODEL_YR_NBR", "Model Year Code", null);
        addElement(ElementType.DATABASE_ELEMENT, "PAVE_STATED_AMT", "Classic Maximum Stated Amount", null);
        addElement(ElementType.DATABASE_ELEMENT, "PA_SYMBOL_CD", "Vehicle Symbol Code", null);
        addElement(ElementType.DATABASE_ELEMENT, "PA_VEH_TYPE_CD", "Vehicle Type Code", null);
        addElement(ElementType.DATABASE_ELEMENT, "PA_VEH_USE_CD", "Vehicle Use Code", null);
        addElement(ElementType.DATABASE_ELEMENT, "BI_ENVIRONMENTAL_SCORE", "BI Environmental Score", null);

        // Rate Keys
        addElement(ElementType.RATE_KEY, "Auto Tier Factor", "Tier-based rate factor", null);
        addElement(ElementType.RATE_KEY, "Primary Class Code Factor 1", "Primary driver class factor", null);
        addElement(ElementType.RATE_KEY, "Territory Factor", "Geographic rating factor", null);
        addElement(ElementType.RATE_KEY, "Multi-Car Discount", "Discount for multiple vehicles", null);

        // Working Storage
        addElement(ElementType.WORKING_STORAGE, "<CovCode>", "Current coverage code", null);
        addElement(ElementType.WORKING_STORAGE, "<Result 1>", "Calculation result 1", null);
        addElement(ElementType.WORKING_STORAGE, "<Result 2>", "Calculation result 2", null);
        addElement(ElementType.WORKING_STORAGE, "<Result 3>", "Calculation result 3", null);
        addElement(ElementType.WORKING_STORAGE, "<Result 4>", "Calculation result 4", null);
        addElement(ElementType.WORKING_STORAGE, "<Result 5>", "Calculation result 5", null);
        addElement(ElementType.WORKING_STORAGE, "<INSC>", "Insurance score", null);

        // Rounding options
        addElement(ElementType.ROUNDING, "<No Rounding>", "No rounding applied", null);
        addElement(ElementType.ROUNDING, "<Whole Dollar Rnd>", "Round to whole dollar", null);
        addElement(ElementType.ROUNDING, "<1 Decimal Place>", "Round to 1 decimal", null);
        addElement(ElementType.ROUNDING, "<2 Decimal Places>", "Round to 2 decimals", null);
        addElement(ElementType.ROUNDING, "<5 Decimal Places>", "Round to 5 decimals", null);

        // Constants
        addElement(ElementType.CONSTANT, "0", "Zero", null);
        addElement(ElementType.CONSTANT, "1", "One", null);
        addElement(ElementType.CONSTANT, "100", "Hundred", null);
        addElement(ElementType.CONSTANT, "1000", "Thousand", null);

        // Expand Database Elements by default
        dbElementsItem.setExpanded(true);
    }

    /**
     * Custom cell renderer for the tree.
     */
    private static class ElementTreeCell extends TreeCell<Object> {
        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setStyle("");
            } else if (item instanceof ElementType type) {
                setText(type.getDisplayName());
                setStyle("-fx-font-weight: bold;");
            } else if (item instanceof Element element) {
                setText(element.getName());
                setStyle("");
                setTooltip(new Tooltip(element.getDescription()));
            } else {
                setText(item.toString());
                setStyle("");
            }
        }
    }
}
