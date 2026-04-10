package structlab.gui.visual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

/**
 * Visual state component for Heap and Priority Queue structures.
 * Shows a tree-level layout at the top and the backing array below,
 * with the root/min element prominently highlighted.
 */
public class HeapVisualPane extends VBox {

    private final Label sizeLabel;
    private final Label minLabel;
    private final VBox treeLevels;
    private final HBox arrayStrip;
    private final Label emptyLabel;

    public HeapVisualPane() {
        setSpacing(10);
        setPadding(new Insets(12));
        getStyleClass().add("visual-state-pane");

        // ── Header ──────────────────────────────────
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Heap");
        title.getStyleClass().add("visual-state-title");
        sizeLabel = new Label("Size: 0");
        sizeLabel.getStyleClass().add("visual-state-meta");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        minLabel = new Label("");
        minLabel.getStyleClass().addAll("heap-min-badge");
        header.getChildren().addAll(title, spacer, minLabel, sizeLabel);

        // ── Tree level display ──────────────────────
        treeLevels = new VBox(4);
        treeLevels.setAlignment(Pos.TOP_CENTER);
        treeLevels.getStyleClass().add("heap-tree-section");

        // ── Array strip ─────────────────────────────
        arrayStrip = new HBox(2);
        arrayStrip.setAlignment(Pos.CENTER_LEFT);
        arrayStrip.getStyleClass().add("heap-array-section");

        // ── Empty state ─────────────────────────────
        emptyLabel = new Label("Empty heap");
        emptyLabel.getStyleClass().add("visual-empty-state");
        emptyLabel.setMaxWidth(Double.MAX_VALUE);
        emptyLabel.setAlignment(Pos.CENTER);

        getChildren().addAll(header, treeLevels, arrayStrip);
    }

    public void update(HeapStateModel model) {
        treeLevels.getChildren().clear();
        arrayStrip.getChildren().clear();
        sizeLabel.setText("Size: " + model.size());

        if (model.isEmpty()) {
            minLabel.setText("");
            treeLevels.getChildren().add(emptyLabel);
            return;
        }

        minLabel.setText("min = " + model.minValue());

        // ── Build tree-level visualization ──────────
        buildTreeLevels(model);

        // ── Build array strip ───────────────────────
        buildArrayStrip(model);
    }

    /**
     * Renders the heap as level-by-level rows, centered.
     * Level 0: root (1 node)
     * Level 1: 2 nodes
     * Level k: up to 2^k nodes
     */
    private void buildTreeLevels(HeapStateModel model) {
        int levels = model.levels();
        for (int level = 0; level < levels; level++) {
            HBox row = new HBox(6);
            row.setAlignment(Pos.CENTER);

            // Level label
            Label levelLabel = new Label("L" + level);
            levelLabel.getStyleClass().add("heap-level-label");
            levelLabel.setMinWidth(24);
            levelLabel.setAlignment(Pos.CENTER_RIGHT);

            HBox cells = new HBox(4);
            cells.setAlignment(Pos.CENTER);

            int start = HeapStateModel.levelStart(level);
            int cap = HeapStateModel.levelCapacity(level);
            for (int j = 0; j < cap && (start + j) < model.size(); j++) {
                int idx = start + j;
                String value = model.elements().get(idx);

                StackPane cell = new StackPane();
                cell.getStyleClass().add("heap-tree-cell");
                if (idx == 0) {
                    cell.getStyleClass().add("heap-tree-cell-root");
                }
                cell.setMinWidth(38);
                cell.setMinHeight(30);

                Label valLabel = new Label(value);
                valLabel.getStyleClass().add("heap-tree-cell-value");
                cell.getChildren().add(valLabel);

                cells.getChildren().add(cell);
            }

            row.getChildren().addAll(levelLabel, cells);
            treeLevels.getChildren().add(row);
        }
    }

    /**
     * Renders the backing array as a horizontal strip with index markers.
     */
    private void buildArrayStrip(HeapStateModel model) {
        Label arrLabel = new Label("Array");
        arrLabel.getStyleClass().add("heap-array-label");
        arrLabel.setMinWidth(40);
        arrayStrip.getChildren().add(arrLabel);

        for (int i = 0; i < model.elements().size(); i++) {
            VBox slot = new VBox(1);
            slot.setAlignment(Pos.CENTER);

            Label idxLabel = new Label(String.valueOf(i));
            idxLabel.getStyleClass().add("heap-array-index");

            StackPane cell = new StackPane();
            cell.getStyleClass().add("heap-array-cell");
            if (i == 0) {
                cell.getStyleClass().add("heap-array-cell-root");
            }
            cell.setMinWidth(34);
            cell.setMinHeight(26);

            Label valLabel = new Label(model.elements().get(i));
            valLabel.getStyleClass().add("heap-array-cell-value");
            cell.getChildren().add(valLabel);

            slot.getChildren().addAll(idxLabel, cell);
            arrayStrip.getChildren().add(slot);
        }
    }
}
