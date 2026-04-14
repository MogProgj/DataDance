package structlab.gui.visual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import structlab.gui.visual.tree.TreeCanvas;

/**
 * Visual state component for Heap structures.
 * Shows a proper tree visual using the reusable {@link TreeCanvas} at the top
 * and the backing array strip below, with the root/min element prominently highlighted.
 */
public class HeapVisualPane extends VBox {

    private final Label sizeLabel;
    private final Label minLabel;
    private final TreeCanvas treeCanvas;
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

        // ── Tree visual (using reusable TreeCanvas) ──
        treeCanvas = new TreeCanvas();
        treeCanvas.getStyleClass().add("heap-tree-section");

        // ── Array strip ─────────────────────────────
        arrayStrip = new HBox(2);
        arrayStrip.setAlignment(Pos.CENTER_LEFT);
        arrayStrip.getStyleClass().add("heap-array-section");

        // ── Empty state ─────────────────────────────
        emptyLabel = new Label("Empty heap");
        emptyLabel.getStyleClass().add("visual-empty-state");
        emptyLabel.setMaxWidth(Double.MAX_VALUE);
        emptyLabel.setAlignment(Pos.CENTER);

        getChildren().addAll(header, treeCanvas, arrayStrip);
    }

    public void update(HeapStateModel model) {
        arrayStrip.getChildren().clear();
        sizeLabel.setText("Size: " + model.size());

        if (model.isEmpty()) {
            minLabel.setText("");
            treeCanvas.getChildren().clear();
            treeCanvas.getChildren().add(emptyLabel);
            return;
        }

        minLabel.setText("min = " + model.minValue());

        // ── Render tree using TreeCanvas ────────────
        treeCanvas.renderHeapTree(model.elements(), true);

        // ── Build array strip ───────────────────────
        buildArrayStrip(model);
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
