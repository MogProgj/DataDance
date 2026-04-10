package structlab.gui.visual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;

/**
 * Visual state pane for DynamicArray.
 * Shows indexed cells with a clear distinction between occupied slots
 * (0..size-1) and reserved/unused capacity slots (size..capacity-1).
 * Communicates growable, elastic storage with size/capacity awareness.
 */
public class DynamicArrayVisualPane extends VBox {

    private final Label sizeLabel;
    private final Label capacityLabel;
    private final Label reservedLabel;
    private final HBox cellContainer;
    private final Label emptyLabel;

    public DynamicArrayVisualPane() {
        setSpacing(8);
        setPadding(new Insets(12));
        getStyleClass().add("visual-state-pane");

        // ── Header ──────────────────────────────────
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Dynamic Array");
        title.getStyleClass().add("visual-state-title");
        Label implBadge = new Label("GROWABLE");
        implBadge.getStyleClass().add("array-impl-badge-dynamic");
        sizeLabel = new Label("Size: 0");
        sizeLabel.getStyleClass().add("visual-state-meta");
        capacityLabel = new Label("Capacity: 0");
        capacityLabel.getStyleClass().add("visual-state-meta");
        reservedLabel = new Label("");
        reservedLabel.getStyleClass().add("array-reserved-meta");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, implBadge, spacer, sizeLabel, capacityLabel, reservedLabel);

        // ── Cell container ──────────────────────────
        cellContainer = new HBox(0);
        cellContainer.setAlignment(Pos.CENTER_LEFT);
        cellContainer.getStyleClass().add("array-cell-container");
        cellContainer.setPadding(new Insets(8, 0, 8, 0));

        // ── Empty state ─────────────────────────────
        emptyLabel = new Label("Empty dynamic array — no elements");
        emptyLabel.getStyleClass().add("visual-empty-state");
        emptyLabel.setMaxWidth(Double.MAX_VALUE);
        emptyLabel.setAlignment(Pos.CENTER);

        getChildren().addAll(header, cellContainer);
    }

    public void update(DynamicArrayStateModel model) {
        cellContainer.getChildren().clear();
        sizeLabel.setText("Size: " + model.size());
        capacityLabel.setText("Capacity: " + model.capacity());

        if (model.hasReservedSpace()) {
            reservedLabel.setText("+" + model.unusedSlots() + " reserved");
            reservedLabel.setVisible(true);
            reservedLabel.setManaged(true);
        } else {
            reservedLabel.setVisible(false);
            reservedLabel.setManaged(false);
        }

        if (model.capacity() == 0) {
            cellContainer.getChildren().add(emptyLabel);
            return;
        }

        for (int i = 0; i < model.capacity(); i++) {
            boolean occupied = i < model.size();
            String rawValue = i < model.raw().size() ? model.raw().get(i) : "null";
            boolean isNull = "null".equals(rawValue);

            VBox cellGroup = new VBox(2);
            cellGroup.setAlignment(Pos.CENTER);

            // Index label above cell
            Label indexLabel = new Label(String.valueOf(i));
            indexLabel.getStyleClass().add("array-cell-index");

            // Cell
            StackPane cell = new StackPane();
            cell.getStyleClass().add("array-cell");
            cell.getStyleClass().add("array-cell-dynamic");
            cell.setMinWidth(44);
            cell.setMinHeight(36);

            if (occupied && !isNull) {
                cell.getStyleClass().add("array-cell-occupied");
                Label valLabel = new Label(rawValue);
                valLabel.getStyleClass().add("array-cell-value");
                cell.getChildren().add(valLabel);
                Tooltip.install(cell, new Tooltip(
                        "Index: " + i + "\nValue: " + rawValue));
            } else if (occupied) {
                // Occupied slot with null value (valid state)
                cell.getStyleClass().add("array-cell-occupied");
                Label valLabel = new Label("null");
                valLabel.getStyleClass().add("array-cell-value");
                cell.getChildren().add(valLabel);
                Tooltip.install(cell, new Tooltip(
                        "Index: " + i + "\nValue: null"));
            } else {
                // Reserved capacity slot (unused)
                cell.getStyleClass().add("array-cell-reserved");
                Label reservedSlot = new Label("—");
                reservedSlot.getStyleClass().add("array-cell-reserved-marker");
                cell.getChildren().add(reservedSlot);
                Tooltip.install(cell, new Tooltip(
                        "Index: " + i + "\nReserved capacity (unused)"));
            }

            cellGroup.getChildren().addAll(indexLabel, cell);
            cellContainer.getChildren().add(cellGroup);

            // Size boundary indicator between occupied and reserved
            if (i == model.size() - 1 && model.hasReservedSpace()) {
                Label sizeBoundary = new Label("▸");
                sizeBoundary.getStyleClass().add("array-size-boundary");
                sizeBoundary.setPadding(new Insets(16, 2, 0, 2));
                Tooltip.install(sizeBoundary, new Tooltip("← size | capacity →"));
                cellContainer.getChildren().add(sizeBoundary);
            } else if (i < model.capacity() - 1) {
                // Thin separator between cells
                Region sep = new Region();
                sep.getStyleClass().add("array-cell-separator");
                sep.setMinWidth(2);
                sep.setPrefWidth(2);
                sep.setMinHeight(36);
                sep.setPadding(new Insets(18, 0, 0, 0));
                cellContainer.getChildren().add(sep);
            }
        }

        // Growable boundary marker (dashed feel)
        Label boundaryMarker = new Label("⋯");
        boundaryMarker.getStyleClass().add("array-boundary-dynamic");
        boundaryMarker.setPadding(new Insets(16, 0, 0, 4));
        Tooltip.install(boundaryMarker, new Tooltip("Array can grow beyond current capacity"));
        cellContainer.getChildren().add(boundaryMarker);
    }
}
