package structlab.gui.visual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;

/**
 * Visual state pane for HashSetCustom — emphasizes set membership and uniqueness
 * rather than key-value mapping. Shows bucket distribution of set elements.
 */
public class HashSetVisualPane extends VBox {

    private final Label sizeLabel;
    private final HBox metricsStrip;
    private final VBox bucketGrid;
    private final Label emptyLabel;

    public HashSetVisualPane() {
        setSpacing(8);
        setPadding(new Insets(12));
        getStyleClass().add("visual-state-pane");

        // ── Header ──────────────────────────────────
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Hash Set");
        title.getStyleClass().add("visual-state-title");
        Label implBadge = new Label("chaining-backed");
        implBadge.getStyleClass().add("hash-set-impl-badge");
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
        emptyLabel = new Label("Empty hash set");
        emptyLabel.getStyleClass().add("visual-empty-state");
        emptyLabel.setMaxWidth(Double.MAX_VALUE);
        emptyLabel.setAlignment(Pos.CENTER);

        getChildren().addAll(header, metricsStrip, bucketGrid);
    }

    public void update(HashSetStateModel model) {
        metricsStrip.getChildren().clear();
        bucketGrid.getChildren().clear();

        sizeLabel.setText("Members: " + model.size());

        // ── Metrics ─────────────────────────────────
        HashChainingVisualPane.addMetric(metricsStrip, "Capacity", String.valueOf(model.capacity()));
        HashChainingVisualPane.addMetric(metricsStrip, "Load", String.format("%.0f%%", model.loadFactor() * 100));
        HashChainingVisualPane.addMetric(metricsStrip, "Occupied", model.occupiedCount() + "/" + model.capacity());
        if (!model.hashType().isEmpty()) {
            HashChainingVisualPane.addMetric(metricsStrip, "Hash", model.hashType().toLowerCase());
        }

        // ── Buckets ─────────────────────────────────
        if (model.isEmpty()) {
            bucketGrid.getChildren().add(emptyLabel);
        }

        for (HashSetStateModel.SetBucket bucket : model.buckets()) {
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
                Label emptyBucketLabel = new Label("—");
                emptyBucketLabel.getStyleClass().add("hash-bucket-empty");
                row.getStyleClass().add("hash-bucket-row-empty");
                row.getChildren().add(emptyBucketLabel);
            } else {
                // Set member chips (value only, no key→value)
                for (int i = 0; i < bucket.members().size(); i++) {
                    String member = bucket.members().get(i);
                    StackPane chip = new StackPane();
                    chip.getStyleClass().add("hash-set-member-chip");
                    chip.setMinWidth(34);
                    chip.setMinHeight(24);

                    Label memberLabel = new Label(member);
                    memberLabel.getStyleClass().add("hash-set-member-text");
                    chip.getChildren().add(memberLabel);

                    Tooltip.install(chip, new Tooltip("Member: " + member));

                    row.getChildren().add(chip);

                    if (i < bucket.members().size() - 1) {
                        Label sep = new Label("·");
                        sep.getStyleClass().add("hash-set-member-sep");
                        row.getChildren().add(sep);
                    }
                }
            }

            bucketGrid.getChildren().add(row);
        }
    }
}
