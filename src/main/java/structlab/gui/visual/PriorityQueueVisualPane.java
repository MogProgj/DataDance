package structlab.gui.visual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import structlab.gui.visual.tree.TreeCanvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Visual state component for HeapPriorityQueue — the ADT/behavior lens.
 *
 * <p>Top section: queue semantics (next-out, priority ordering).</p>
 * <p>Lower section: heap backing — a proper tree visual using {@link TreeCanvas}
 * plus an optional array strip, making the heap-backed implementation visible
 * without duplicating BinaryHeap's emphasis.</p>
 */
public class PriorityQueueVisualPane extends VBox {

    private final Label sizeLabel;
    private final Label nextOutValue;
    private final HBox priorityStrip;
    private final VBox heapBackingSection;
    private final TreeCanvas heapTree;
    private final HBox heapArrayStrip;
    private final Label heapBackingToggle;
    private final VBox heapBackingContent;
    private final Label emptyLabel;
    private boolean heapBackingExpanded = true;

    public PriorityQueueVisualPane() {
        setSpacing(10);
        setPadding(new Insets(12));
        getStyleClass().add("visual-state-pane");

        // ── Header ──────────────────────────────────
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Priority Queue");
        title.getStyleClass().add("visual-state-title");
        sizeLabel = new Label("Size: 0");
        sizeLabel.getStyleClass().add("visual-state-meta");
        Label implBadge = new Label("heap-backed");
        implBadge.getStyleClass().add("pq-impl-badge");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, implBadge, spacer, sizeLabel);

        // ── Next Out hero section ───────────────────
        VBox nextOutSection = new VBox(2);
        nextOutSection.setAlignment(Pos.CENTER);
        nextOutSection.getStyleClass().add("pq-next-out-section");

        Label nextOutLabel = new Label("Next Out");
        nextOutLabel.getStyleClass().add("pq-next-out-label");

        nextOutValue = new Label("—");
        nextOutValue.getStyleClass().add("pq-next-out-value");

        nextOutSection.getChildren().addAll(nextOutLabel, nextOutValue);

        // ── Priority order strip ────────────────────
        VBox stripWrapper = new VBox(4);
        Label stripLabel = new Label("Priority Order");
        stripLabel.getStyleClass().add("pq-strip-label");

        priorityStrip = new HBox(4);
        priorityStrip.setAlignment(Pos.CENTER_LEFT);
        priorityStrip.getStyleClass().add("pq-priority-strip");

        stripWrapper.getChildren().addAll(stripLabel, priorityStrip);

        // ── Heap backing section (expanded by default) ──
        heapBackingToggle = new Label("▾ Heap Backing");
        heapBackingToggle.getStyleClass().add("pq-heap-detail-toggle");
        heapBackingToggle.setOnMouseClicked(e -> toggleHeapBacking());

        Label heapNote = new Label("Implemented with BinaryHeap — tree structure shown below");
        heapNote.getStyleClass().add("pq-heap-note");

        heapTree = new TreeCanvas();
        heapTree.getStyleClass().add("pq-heap-tree");

        heapArrayStrip = new HBox(2);
        heapArrayStrip.setAlignment(Pos.CENTER_LEFT);
        heapArrayStrip.getStyleClass().add("pq-heap-array");

        heapBackingContent = new VBox(6, heapNote, heapTree, heapArrayStrip);
        heapBackingContent.getStyleClass().add("pq-heap-detail-content");

        heapBackingSection = new VBox(2, heapBackingToggle, heapBackingContent);
        heapBackingSection.getStyleClass().add("pq-heap-detail-section");

        // ── Empty state ─────────────────────────────
        emptyLabel = new Label("Empty priority queue");
        emptyLabel.getStyleClass().add("visual-empty-state");
        emptyLabel.setMaxWidth(Double.MAX_VALUE);
        emptyLabel.setAlignment(Pos.CENTER);

        getChildren().addAll(header, nextOutSection, stripWrapper, heapBackingSection);
    }

    public void update(HeapStateModel model) {
        priorityStrip.getChildren().clear();
        heapArrayStrip.getChildren().clear();
        sizeLabel.setText("Size: " + model.size());

        if (model.isEmpty()) {
            nextOutValue.setText("—");
            priorityStrip.getChildren().add(emptyLabel);
            heapTree.getChildren().clear();
            collapseHeapBacking();
            return;
        }

        // ── Next Out ────────────────────────────────
        nextOutValue.setText(model.minValue());

        // ── Build priority order strip ──────────────
        List<String> sorted = sortedByPriority(model.elements());
        for (int i = 0; i < sorted.size(); i++) {
            String value = sorted.get(i);
            StackPane chip = new StackPane();
            chip.getStyleClass().add("pq-chip");
            if (i == 0) {
                chip.getStyleClass().add("pq-chip-front");
            }
            chip.setMinWidth(34);
            chip.setMinHeight(26);

            Label valLabel = new Label(value);
            valLabel.getStyleClass().add("pq-chip-value");
            chip.getChildren().add(valLabel);

            priorityStrip.getChildren().add(chip);

            if (i < sorted.size() - 1) {
                Label arrow = new Label("›");
                arrow.getStyleClass().add("pq-chip-arrow");
                priorityStrip.getChildren().add(arrow);
            }
        }

        // ── Build heap backing tree + array ─────────
        heapTree.renderHeapTree(model.elements(), true);
        buildHeapArray(model);
    }

    /**
     * Sorts elements by numeric value for priority display.
     */
    static List<String> sortedByPriority(List<String> elements) {
        List<String> sorted = new ArrayList<>(elements);
        sorted.sort((a, b) -> {
            try {
                return Integer.compare(Integer.parseInt(a), Integer.parseInt(b));
            } catch (NumberFormatException e) {
                return a.compareTo(b);
            }
        });
        return Collections.unmodifiableList(sorted);
    }

    private void buildHeapArray(HeapStateModel model) {
        Label arrLabel = new Label("Array");
        arrLabel.getStyleClass().add("pq-detail-label");
        arrLabel.setMinWidth(40);
        heapArrayStrip.getChildren().add(arrLabel);

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
            cell.setMinWidth(30);
            cell.setMinHeight(22);

            Label valLabel = new Label(model.elements().get(i));
            valLabel.getStyleClass().add("heap-array-cell-value");
            cell.getChildren().add(valLabel);

            slot.getChildren().addAll(idxLabel, cell);
            heapArrayStrip.getChildren().add(slot);
        }
    }

    private void toggleHeapBacking() {
        heapBackingExpanded = !heapBackingExpanded;
        heapBackingContent.setVisible(heapBackingExpanded);
        heapBackingContent.setManaged(heapBackingExpanded);
        heapBackingToggle.setText(heapBackingExpanded
                ? "▾ Heap Backing" : "▸ Heap Backing");
    }

    private void collapseHeapBacking() {
        heapBackingExpanded = false;
        heapBackingContent.setVisible(false);
        heapBackingContent.setManaged(false);
        heapBackingToggle.setText("▸ Heap Backing");
    }
}
