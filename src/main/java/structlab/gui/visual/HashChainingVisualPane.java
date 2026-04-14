package structlab.gui.visual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;

/**
 * Visual state pane for HashTableChaining — shows bucket rows with chain entries,
 * collision density, and educational metrics (load factor, max chain, collisions).
 */
public class HashChainingVisualPane extends VBox {

    private final Label sizeLabel;
    private final HBox metricsStrip;
    private final VBox bucketGrid;
    private final Label emptyLabel;

    public HashChainingVisualPane() {
        setSpacing(8);
        setPadding(new Insets(12));
        getStyleClass().add("visual-state-pane");

        // ── Header ──────────────────────────────────
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Hash Table");
        title.getStyleClass().add("visual-state-title");
        Label implBadge = new Label("chaining");
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

        // ── Bucket grid ─────────────────────────────
        bucketGrid = new VBox(3);
        bucketGrid.getStyleClass().add("hash-bucket-grid");

        // ── Empty state ─────────────────────────────
        emptyLabel = new Label("Empty hash table");
        emptyLabel.getStyleClass().add("visual-empty-state");
        emptyLabel.setMaxWidth(Double.MAX_VALUE);
        emptyLabel.setAlignment(Pos.CENTER);

        getChildren().addAll(header, metricsStrip, bucketGrid);
    }

    public void update(HashChainingStateModel model) {
        metricsStrip.getChildren().clear();
        bucketGrid.getChildren().clear();

        sizeLabel.setText("Size: " + model.size());

        // ── Metrics ─────────────────────────────────
        addMetric(metricsStrip, "Capacity", String.valueOf(model.capacity()));
        addMetric(metricsStrip, "Load", String.format("%.0f%%", model.loadFactor() * 100));
        addMetric(metricsStrip, "Occupied", model.occupiedCount() + "/" + model.capacity());
        if (model.maxChainSize() > 1) {
            addMetric(metricsStrip, "Max chain", String.valueOf(model.maxChainSize()));
        }
        if (model.collisionBuckets() > 0) {
            addMetric(metricsStrip, "Collisions", String.valueOf(model.collisionBuckets()));
        }
        if (!model.hashType().isEmpty()) {
            addMetric(metricsStrip, "Hash", model.hashType().toLowerCase());
        }

        // ── Buckets ─────────────────────────────────
        if (model.isEmpty()) {
            bucketGrid.getChildren().add(emptyLabel);
        }

        for (HashChainingStateModel.Bucket bucket : model.buckets()) {
            HBox row = new HBox(4);
            row.setAlignment(Pos.CENTER_LEFT);
            row.getStyleClass().add("hash-bucket-row");

            // Bucket index label
            Label idxLabel = new Label(String.valueOf(bucket.index()));
            idxLabel.getStyleClass().add("hash-bucket-index");
            idxLabel.setMinWidth(28);
            idxLabel.setAlignment(Pos.CENTER_RIGHT);
            row.getChildren().add(idxLabel);

            if (bucket.isEmpty()) {
                Label emptyBucket = new Label("—");
                emptyBucket.getStyleClass().add("hash-bucket-empty");
                row.getStyleClass().add("hash-bucket-row-empty");
                row.getChildren().add(emptyBucket);
            } else {
                // Chain entries as connected chips
                for (int i = 0; i < bucket.entries().size(); i++) {
                    HashChainingStateModel.Entry entry = bucket.entries().get(i);
                    StackPane chip = new StackPane();
                    chip.getStyleClass().add("hash-entry-chip");
                    if (bucket.entries().size() > 1) {
                        chip.getStyleClass().add("hash-entry-chip-collision");
                    }
                    chip.setMinWidth(40);
                    chip.setMinHeight(24);

                    Label entryLabel = new Label(entry.key() + "→" + entry.value());
                    entryLabel.getStyleClass().add("hash-entry-chip-text");
                    chip.getChildren().add(entryLabel);

                    Tooltip.install(chip, new Tooltip("Key: " + entry.key() + "\nValue: " + entry.value()));

                    row.getChildren().add(chip);

                    // Chain arrow between entries
                    if (i < bucket.entries().size() - 1) {
                        Label arrow = new Label("→");
                        arrow.getStyleClass().add("hash-chain-arrow");
                        row.getChildren().add(arrow);
                    }
                }
            }

            bucketGrid.getChildren().add(row);
        }
    }

    static void addMetric(HBox strip, String label, String value) {
        HBox metric = new HBox(3);
        metric.setAlignment(Pos.CENTER_LEFT);
        metric.getStyleClass().add("hash-metric");

        Label nameLabel = new Label(label);
        nameLabel.getStyleClass().add("hash-metric-label");
        Label valLabel = new Label(value);
        valLabel.getStyleClass().add("hash-metric-value");

        metric.getChildren().addAll(nameLabel, valLabel);
        strip.getChildren().add(metric);
    }
}
