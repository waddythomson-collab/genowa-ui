package genowa.ui.components;

import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;

/**
 * A pane that can be docked to the left or right side of its parent,
 * or dragged out to become a floating window.
 * 
 * Usage:
 *   DockablePane elementPicker = new DockablePane("Elements", treeView);
 *   elementPicker.setDockSide(DockSide.RIGHT);
 *   mainContainer.setRight(elementPicker);
 */
public class DockablePane extends VBox {

    public enum DockSide { LEFT, RIGHT, FLOATING }

    private final StringProperty title = new SimpleStringProperty();
    private final ObjectProperty<DockSide> dockSide = new SimpleObjectProperty<>(DockSide.RIGHT);
    private final BooleanProperty collapsed = new SimpleBooleanProperty(false);

    private final HBox titleBar;
    private final Label titleLabel;
    private final Button collapseButton;
    private final Button floatButton;
    private final Button closeButton;
    private final Node content;

    private Stage floatingStage;
    private BorderPane parentContainer;
    private double dragStartX, dragStartY;
    private double prefWidth = 250;

    public DockablePane(String title, Node content) {
        this.content = content;
        this.title.set(title);

        // Style the container
        setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 1;");
        setMinWidth(200);
        setPrefWidth(prefWidth);

        // Title bar
        titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-padding: 2 5;");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);

        // Collapse button (minimize to side)
        collapseButton = new Button("−");
        collapseButton.setStyle("-fx-font-size: 10; -fx-padding: 2 6; -fx-background-radius: 2;");
        collapseButton.setOnAction(e -> toggleCollapse());
        collapseButton.setTooltip(new Tooltip("Collapse"));

        // Float button (pop out to window)
        floatButton = new Button("◱");
        floatButton.setStyle("-fx-font-size: 10; -fx-padding: 2 6; -fx-background-radius: 2;");
        floatButton.setOnAction(e -> toggleFloat());
        floatButton.setTooltip(new Tooltip("Float/Dock"));

        // Close button
        closeButton = new Button("✕");
        closeButton.setStyle("-fx-font-size: 10; -fx-padding: 2 6; -fx-background-radius: 2;");
        closeButton.setOnAction(e -> setVisible(false));
        closeButton.setTooltip(new Tooltip("Close"));

        titleBar = new HBox(5, titleLabel, collapseButton, floatButton, closeButton);
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setStyle("-fx-background-color: linear-gradient(to bottom, #e8e8e8, #d0d0d0); " +
                         "-fx-padding: 3 5; -fx-border-color: #b0b0b0; -fx-border-width: 0 0 1 0;");
        titleBar.setCursor(Cursor.MOVE);

        // Drag handling for title bar
        titleBar.setOnMousePressed(this::onDragStart);
        titleBar.setOnMouseDragged(this::onDragMove);
        titleBar.setOnMouseReleased(this::onDragEnd);

        // Content area
        VBox.setVgrow(content, Priority.ALWAYS);

        getChildren().addAll(titleBar, content);

        // Bind title
        titleLabel.textProperty().bind(this.title);
    }

    private void onDragStart(MouseEvent e) {
        dragStartX = e.getScreenX();
        dragStartY = e.getScreenY();
    }

    private void onDragMove(MouseEvent e) {
        if (dockSide.get() == DockSide.FLOATING && floatingStage != null) {
            // Move the floating window
            floatingStage.setX(e.getScreenX() - titleBar.getWidth() / 2);
            floatingStage.setY(e.getScreenY() - 10);
        }
    }

    private void onDragEnd(MouseEvent e) {
        double deltaX = e.getScreenX() - dragStartX;
        double deltaY = e.getScreenY() - dragStartY;

        // If dragged more than 50 pixels, consider it a dock/undock action
        if (Math.abs(deltaX) > 50 || Math.abs(deltaY) > 50) {
            if (dockSide.get() != DockSide.FLOATING) {
                // Undock to floating
                setDockSide(DockSide.FLOATING);
                if (floatingStage != null) {
                    floatingStage.setX(e.getScreenX() - prefWidth / 2);
                    floatingStage.setY(e.getScreenY() - 20);
                }
            } else if (parentContainer != null) {
                // Check if dragged to an edge for docking
                Scene scene = parentContainer.getScene();
                if (scene != null) {
                    double sceneX = e.getScreenX() - scene.getWindow().getX();
                    if (sceneX < 100) {
                        setDockSide(DockSide.LEFT);
                    } else if (sceneX > scene.getWidth() - 100) {
                        setDockSide(DockSide.RIGHT);
                    }
                }
            }
        }
    }

    private void toggleCollapse() {
        collapsed.set(!collapsed.get());
        content.setVisible(!collapsed.get());
        content.setManaged(!collapsed.get());
        collapseButton.setText(collapsed.get() ? "+" : "−");
        if (collapsed.get()) {
            setPrefWidth(30);
            setMinWidth(30);
        } else {
            setPrefWidth(prefWidth);
            setMinWidth(200);
        }
    }

    private void toggleFloat() {
        if (dockSide.get() == DockSide.FLOATING) {
            // Dock back to original side
            setDockSide(DockSide.RIGHT);
        } else {
            // Float
            setDockSide(DockSide.FLOATING);
        }
    }

    public void setDockSide(DockSide side) {
        DockSide oldSide = dockSide.get();
        dockSide.set(side);

        if (side == DockSide.FLOATING) {
            // Remove from parent and create floating window
            if (parentContainer != null) {
                if (oldSide == DockSide.LEFT) {
                    parentContainer.setLeft(null);
                } else if (oldSide == DockSide.RIGHT) {
                    parentContainer.setRight(null);
                }
            }

            if (floatingStage == null) {
                floatingStage = new Stage();
                floatingStage.initStyle(StageStyle.UTILITY);
                floatingStage.setAlwaysOnTop(true);
                floatingStage.setTitle(title.get());
            }

            Scene scene = new Scene(this, prefWidth, 400);
            floatingStage.setScene(scene);
            floatingStage.show();

            // Update button
            floatButton.setText("◲");
            floatButton.setTooltip(new Tooltip("Dock"));

        } else {
            // Dock to parent
            if (floatingStage != null && floatingStage.isShowing()) {
                floatingStage.hide();
                floatingStage.setScene(null);
            }

            if (parentContainer != null) {
                if (side == DockSide.LEFT) {
                    parentContainer.setLeft(this);
                } else {
                    parentContainer.setRight(this);
                }
            }

            // Update button
            floatButton.setText("◱");
            floatButton.setTooltip(new Tooltip("Float"));
        }
    }

    public void setParentContainer(BorderPane parent) {
        this.parentContainer = parent;
    }

    public void show() {
        setVisible(true);
        setManaged(true);
        if (dockSide.get() == DockSide.FLOATING && floatingStage != null) {
            floatingStage.show();
        }
    }

    public void hide() {
        setVisible(false);
        setManaged(false);
        if (floatingStage != null) {
            floatingStage.hide();
        }
    }
    
    /**
     * Dock the pane back to its parent container.
     * If hidden, also makes it visible.
     */
    public void dock() {
        // If floating, dock it back
        if (dockSide.get() == DockSide.FLOATING) {
            setDockSide(DockSide.RIGHT);
        }
        // Make sure it's visible
        setVisible(true);
        setManaged(true);
        // Ensure it's attached to parent
        if (parentContainer != null && parentContainer.getRight() != this) {
            parentContainer.setRight(this);
        }
    }

    // Properties
    public StringProperty titleProperty() { return title; }
    public String getTitle() { return title.get(); }
    public void setTitle(String value) { title.set(value); }

    public ObjectProperty<DockSide> dockSideProperty() { return dockSide; }
    public DockSide getDockSide() { return dockSide.get(); }

    public BooleanProperty collapsedProperty() { return collapsed; }
    public boolean isCollapsed() { return collapsed.get(); }
}
