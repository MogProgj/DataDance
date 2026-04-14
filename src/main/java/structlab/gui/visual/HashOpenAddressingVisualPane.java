package structlab.gui.visual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;

/**
 * Visual state pane for HashTableOpenAddressing — shows a slot grid with
 * occupancy states, clustering visibility, and educational metrics (load factor,
 * tombstones, probing strategy).
 */
public class HashOpenAddressingVisualPane extends VBox {

    private final Label sizeLabel;
    private final HBox metricsStrip;
    private final FlowPane slotGrid;
    private final Label emptyLabel;

    public HashOpenAddressingVisualPane() {
        setSpacing(8);
        setPadding(new Insets(12));
        getStyleClass().add("visual-state-pane");

        // ── Header ──────────────────────────────────
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Hash Table");
        title.getStyleClass().add("visual-state-title");
        Label implBadge = new Label("open addressing");
        implBadge.getStyleClass().add("hash-impl-badge");
        sizeLabel = new Label("Size: 0");
        sizeLabel.getStyleClass().add("visual-state-meta");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, implBadge, spacer, sizeLabel);

        // ── Metrics strip ───────────────────────────
        metricsStrip = new HBox(6);
        metricsStrip.setAlignment(Pos.CENTER_LEFT);
        metricsStrip.getStyleClass().add("hash-metrics-strip");

        // ── Slot grid ───────────────────────────────
        slotGrid = new FlowPane(4, 4);
        slotGrid.setAlignment(Pos.TOP_LEFT);
        slotGrid.getStyleClass().add("hash-slot-grid");

        // ── Empty state ─────────────────────────────
        emptyLabel = new Label("Empty hash table");
        emptyLabel.getStyleClass().add("visual-empty-state");
        emptyLabel.setMaxWidth(Double.MAX_VALUE);
        emptyLabel.setAlignment(Pos.CENTER);

        getChildren().addAll(header, metricsStrip, slotGrid);
    }

    public void update(HashOpenAddressingStateModel model) {
        metricsStrip.getChildren().clear();
        slotGrid.getChildren().clear();

        sizeLabel.setText("Size: " + model.size());

        // ── Metrics ─────────────────────────────────
        HashChainingVisualPane.addMetric(metricsStrip, "Capacity", String.valueOf(model.capacity()));
        HashChainingVisualPane.addMetric(metricsStrip, "Load", String.format("%.0f%%", model.loadFactor() * 100));
        HashChainingVisualPane.addMetric(metricsStrip, "Occupied", model.occupiedCount() + "/" + model.capacity());
        if (model.deletedCount() > 0) {
            HashChainingVisualPane.addMetric(metricsStrip, "Tombstones", String.valueOf(model.deletedCount()));
        }
        if (model.maxClusterSize() > 1) {
            HashChainingVisualPane.addMetric(metricsStrip, "Max cluster", String.valueOf(model.maxClusterSize()));
        }
        if (!model.oaType().isEmpty()) {
            HashChainingVisualPane.addMetric(metricsStrip, "Probing", model.oaType().toLowerCase());
        }
        if (!model.hashType().isEmpty()) {
            HashChainingVisualPane.addMetric(metricsStrip, "Hash", model.hashType().toLowerCase());
        }

        // ── Slot grid ───────────────────────────────
        if (model.isEmpty() && model.deletedCount() == 0) {
            slotGrid.getChildren().add(emptyLabel);
        }

        for (HashOpenAddressingStateModel.Slot slot : model.slots()) {
            VBox cell = new VBox(1);
            cell.setAlignment(Pos.CENTER);
            cell.getStyleClass().add("hash-slot-cell");
            cell.setMinWidth(42);
            cell.setMinHeight(36);

            // Index label
            Label idxLabel = new Label(String.valueOf(slot.index()));
            idxLabel.getStyleClass().add("hash-slot-index");

            // Content
            Label contentLabel;
            switch (slot.state()) {
                case EMPTY -> {
                    cell.getStyleClass().add("hash-slot-empty");
                    contentLabel = new Label("·");
                    contentLabel.getStyleClass().add("hash-slot-empty-text");
                }
                case OCCUPIED -> {
                    cell.getStyleClass().add("hash-slot-occupied");
                    contentLabel = new Label(slot.key() + "→" + slot.value());
                    contentLabel.getStyleClass().add("hash-slot-occupied-text");
                    Tooltip.install(cell, new Tooltip("Key: " + slot.key() + "\nValue: " + slot.value()));
                }
                case DELETED -> {
                    cell.getStyleClass().add("hash-slot-deleted");
                    contentLabel = new Label("✕");
                    contentLabel.getStyleClass().add("hash-slot-deleted-text");
                    Tooltip.install(cell, new Tooltip("Tombstone (deleted)"));
                }
                default -> {
                    contentLabel = new Label("?");
                }
            }

            cell.getChildren().addAll(idxLabel, contentLabel);
            slotGrid.getChildren().add(cell);
        }
    }
}
