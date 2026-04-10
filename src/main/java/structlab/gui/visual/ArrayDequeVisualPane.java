package structlab.gui.visual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;

/**
 * Visual state pane for ArrayDequeCustom.
 * Shows the raw backing array as a slot strip with front/back markers,
 * and a logical-order lane above it for quick semantic reading.
 */
public class ArrayDequeVisualPane extends VBox {

    private final Label sizeLabel;
    private final HBox metricsStrip;
    private final HBox logicalLane;
    private final FlowPane slotGrid;
    private final Label emptyLabel;

    public ArrayDequeVisualPane() {
        setSpacing(8);
        setPadding(new Insets(12));
        getStyleClass().add("visual-state-pane");

        // ── Header ──────────────────────────────────
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Array Deque");
        title.getStyleClass().add("visual-state-title");
        Label implBadge = new Label("array-backed");
        implBadge.getStyleClass().add("deque-impl-badge");
        sizeLabel = new Label("Size: 0");
        sizeLabel.getStyleClass().add("visual-state-meta");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, implBadge, spacer, sizeLabel);

        // ── Metrics strip ───────────────────────────
        metricsStrip = new HBox(6);
        metricsStrip.setAlignment(Pos.CENTER_LEFT);
        metricsStrip.getStyleClass().add("deque-metrics-strip");

        // ── Logical lane ────────────────────────────
        logicalLane = new HBox(0);
        logicalLane.setAlignment(Pos.CENTER_LEFT);
        logicalLane.getStyleClass().add("deque-logical-lane");
        logicalLane.setPadding(new Insets(4, 0, 4, 0));

        // ── Slot grid (backing array) ───────────────
        slotGrid = new FlowPane(4, 4);
        slotGrid.getStyleClass().add("deque-slot-grid");
        slotGrid.setPadding(new Insets(4));

        // ── Empty state ─────────────────────────────
        emptyLabel = new Label("Empty deque");
        emptyLabel.getStyleClass().add("visual-empty-state");
        emptyLabel.setMaxWidth(Double.MAX_VALUE);
        emptyLabel.setAlignment(Pos.CENTER);

        Label logicalLabel = new Label("Logical order:");
        logicalLabel.getStyleClass().add("deque-section-label");
        Label rawLabel = new Label("Backing array:");
        rawLabel.getStyleClass().add("deque-section-label");

        getChildren().addAll(header, metricsStrip, logicalLabel, logicalLane, rawLabel, slotGrid);
    }

    public void update(ArrayDequeStateModel model) {
        metricsStrip.getChildren().clear();
        logicalLane.getChildren().clear();
        slotGrid.getChildren().clear();
        sizeLabel.setText("Size: " + model.size());

        // ── Metrics ─────────────────────────────────
        HashChainingVisualPane.addMetric(metricsStrip, "Capacity", String.valueOf(model.capacity()));
        HashChainingVisualPane.addMetric(metricsStrip, "Front idx", String.valueOf(model.frontIndex()));
        if (model.capacity() > 0) {
            HashChainingVisualPane.addMetric(metricsStrip, "Usage",
                    String.format("%.0f%%", (double) model.size() / model.capacity() * 100));
        }

        if (model.isEmpty()) {
            logicalLane.getChildren().add(emptyLabel);
            return;
        }

        // ── Logical lane ────────────────────────────
        Label frontDir = new Label("front ←");
        frontDir.getStyleClass().add("deque-direction-label");
        frontDir.setPadding(new Insets(0, 6, 0, 0));
        logicalLane.getChildren().add(frontDir);

        for (int i = 0; i < model.logical().size(); i++) {
            boolean isFront = (i == 0);
            boolean isBack = (i == model.logical().size() - 1);

            VBox cellGroup = new VBox(2);
            cellGroup.setAlignment(Pos.CENTER);

            Label marker = new Label();
            marker.getStyleClass().add("deque-cell-marker");
            marker.setMinHeight(14);
            if (isFront) {
                marker.setText("front");
                marker.getStyleClass().add("deque-marker-front");
            } else if (isBack) {
                marker.setText("back");
                marker.getStyleClass().add("deque-marker-back");
            }

            StackPane cell = new StackPane();
            cell.getStyleClass().add("deque-logical-cell");
            if (isFront) cell.getStyleClass().add("deque-cell-front");
            if (isBack) cell.getStyleClass().add("deque-cell-back");
            cell.setMinWidth(46);
            cell.setMinHeight(32);

            Label valLabel = new Label(model.logical().get(i));
            valLabel.getStyleClass().add("deque-cell-value");
            cell.getChildren().add(valLabel);

            cellGroup.getChildren().addAll(marker, cell);
            logicalLane.getChildren().add(cellGroup);

            if (i < model.logical().size() - 1) {
                Label sep = new Label("·");
                sep.getStyleClass().add("deque-logical-sep");
                sep.setPadding(new Insets(8, 2, 0, 2));
                logicalLane.getChildren().add(sep);
            }
        }

        Label backDir = new Label("→ back");
        backDir.getStyleClass().add("deque-direction-label");
        backDir.setPadding(new Insets(0, 0, 0, 6));
        logicalLane.getChildren().add(backDir);

        // ── Backing-array slot grid ─────────────────
        int rearIdx = model.rearIndex();
        for (int i = 0; i < model.raw().size(); i++) {
            String slotVal = model.raw().get(i);
            boolean occupied = slotVal != null && !"null".equals(slotVal);
            boolean isFrontSlot = (i == model.frontIndex());
            boolean isRearSlot = (i == rearIdx || (rearIdx == 0 && i == model.capacity() - 1 && model.size() == model.capacity()));

            VBox slotGroup = new VBox(1);
            slotGroup.setAlignment(Pos.CENTER);

            Label idxLabel = new Label(String.valueOf(i));
            idxLabel.getStyleClass().add("deque-slot-index");

            StackPane slot = new StackPane();
            slot.getStyleClass().add("deque-slot-cell");
            slot.setMinWidth(40);
            slot.setMinHeight(32);

            if (occupied) {
                slot.getStyleClass().add("deque-slot-occupied");
                Label val = new Label(slotVal);
                val.getStyleClass().add("deque-slot-value");
                slot.getChildren().add(val);
            } else {
                slot.getStyleClass().add("deque-slot-empty");
                Label dot = new Label("·");
                dot.getStyleClass().add("deque-slot-empty-text");
                slot.getChildren().add(dot);
            }

            if (isFrontSlot && occupied) {
                slot.getStyleClass().add("deque-slot-front");
            }

            Tooltip.install(slot, new Tooltip(
                    "Slot [" + i + "]: " + (occupied ? slotVal : "empty")
                            + (isFrontSlot ? " (front)" : "")));

            slotGroup.getChildren().addAll(idxLabel, slot);
            slotGrid.getChildren().add(slotGroup);
        }
    }
}
