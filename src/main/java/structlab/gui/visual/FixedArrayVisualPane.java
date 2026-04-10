package structlab.gui.visual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;

/**
 * Visual state pane for FixedArray.
 * Shows indexed cells in a rigid grid with clear capacity boundary,
 * occupied vs empty slots, and a prominent "FIXED" badge
 * emphasising that the array cannot grow.
 */
public class FixedArrayVisualPane extends VBox {

    private final Label sizeLabel;
    private final Label capacityLabel;
    private final HBox cellContainer;
    private final Label emptyLabel;

    public FixedArrayVisualPane() {
        setSpacing(8);
        setPadding(new Insets(12));
        getStyleClass().add("visual-state-pane");

        // ── Header ──────────────────────────────────
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Fixed Array");
        title.getStyleClass().add("visual-state-title");
        Label implBadge = new Label("FIXED");
        implBadge.getStyleClass().add("array-impl-badge-fixed");
        sizeLabel = new Label("Size: 0");
        sizeLabel.getStyleClass().add("visual-state-meta");
        capacityLabel = new Label("Capacity: 0");
        capacityLabel.getStyleClass().add("visual-state-meta");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, implBadge, spacer, sizeLabel, capacityLabel);

        // ── Metrics strip ───────────────────────────
        HBox metricsStrip = new HBox(10);
        metricsStrip.setAlignment(Pos.CENTER_LEFT);
        metricsStrip.getStyleClass().add("array-metrics-strip");

        // ── Cell container ──────────────────────────
        cellContainer = new HBox(0);
        cellContainer.setAlignment(Pos.CENTER_LEFT);
        cellContainer.getStyleClass().add("array-cell-container");
        cellContainer.setPadding(new Insets(8, 0, 8, 0));

        // ── Empty state ─────────────────────────────
        emptyLabel = new Label("Empty fixed array — all slots unused");
        emptyLabel.getStyleClass().add("visual-empty-state");
        emptyLabel.setMaxWidth(Double.MAX_VALUE);
        emptyLabel.setAlignment(Pos.CENTER);

        getChildren().addAll(header, cellContainer);
    }

    public void update(FixedArrayStateModel model) {
        cellContainer.getChildren().clear();
        sizeLabel.setText("Size: " + model.size());
        capacityLabel.setText("Capacity: " + model.capacity());

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
            cell.getStyleClass().add("array-cell-fixed");
            cell.setMinWidth(44);
            cell.setMinHeight(36);

            if (occupied && !isNull) {
                cell.getStyleClass().add("array-cell-occupied");
                Label valLabel = new Label(rawValue);
                valLabel.getStyleClass().add("array-cell-value");
                cell.getChildren().add(valLabel);
                Tooltip.install(cell, new Tooltip(
                        "Index: " + i + "\nValue: " + rawValue));
            } else {
                cell.getStyleClass().add("array-cell-empty");
                Label emptySlot = new Label("·");
                emptySlot.getStyleClass().add("array-cell-empty-marker");
                cell.getChildren().add(emptySlot);
                Tooltip.install(cell, new Tooltip(
                        "Index: " + i + "\nEmpty slot"));
            }

            cellGroup.getChildren().addAll(indexLabel, cell);
            cellContainer.getChildren().add(cellGroup);

            // Thin separator between cells (not after last)
            if (i < model.capacity() - 1) {
                Region sep = new Region();
                sep.getStyleClass().add("array-cell-separator");
                sep.setMinWidth(2);
                sep.setPrefWidth(2);
                sep.setMinHeight(36);
                sep.setPadding(new Insets(18, 0, 0, 0));
                cellContainer.getChildren().add(sep);
            }
        }

        // Capacity boundary marker
        Label boundaryMarker = new Label("│");
        boundaryMarker.getStyleClass().add("array-boundary-fixed");
        boundaryMarker.setPadding(new Insets(16, 0, 0, 4));
        cellContainer.getChildren().add(boundaryMarker);
    }
}
