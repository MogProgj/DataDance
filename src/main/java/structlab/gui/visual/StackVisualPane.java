package structlab.gui.visual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

/**
 * Visual state component for Stack structures.
 * Adapts its presentation based on implementation type:
 * <ul>
 *   <li><b>ArrayStack</b>: slot-based vertical view with index markers, capacity framing,
 *       and contiguous-storage feel.</li>
 *   <li><b>LinkedStack</b>: node-chain view with pointer/link arrows between nodes,
 *       emphasizing non-contiguous linked nature.</li>
 * </ul>
 */
public class StackVisualPane extends VBox {

    private final VBox stackContainer;
    private final Label sizeLabel;
    private final Label titleLabel;
    private final Label implBadge;
    private final Label emptyLabel;

    public StackVisualPane() {
        setSpacing(8);
        setPadding(new Insets(12));
        getStyleClass().add("visual-state-pane");

        // Header
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        titleLabel = new Label("Stack");
        titleLabel.getStyleClass().add("visual-state-title");
        implBadge = new Label("");
        implBadge.getStyleClass().add("stack-impl-badge");
        sizeLabel = new Label("Size: 0");
        sizeLabel.getStyleClass().add("visual-state-meta");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(titleLabel, implBadge, spacer, sizeLabel);

        // Stack container — elements stacked bottom-to-top
        stackContainer = new VBox(0);
        stackContainer.setAlignment(Pos.BOTTOM_CENTER);
        stackContainer.getStyleClass().add("stack-container");
        VBox.setVgrow(stackContainer, Priority.ALWAYS);

        // Empty state
        emptyLabel = new Label("Empty stack");
        emptyLabel.getStyleClass().add("visual-empty-state");
        emptyLabel.setMaxWidth(Double.MAX_VALUE);
        emptyLabel.setAlignment(Pos.CENTER);

        getChildren().addAll(header, stackContainer);
    }

    public void update(StackStateModel model) {
        stackContainer.getChildren().clear();
        sizeLabel.setText("Size: " + model.size());

        // Set implementation badge
        if (model.isLinkedBacked()) {
            implBadge.setText("linked");
            implBadge.getStyleClass().setAll("stack-impl-badge", "stack-impl-badge-linked");
        } else {
            implBadge.setText("array-backed");
            implBadge.getStyleClass().setAll("stack-impl-badge", "stack-impl-badge-array");
        }

        if (model.isEmpty()) {
            stackContainer.getChildren().add(emptyLabel);
            return;
        }

        if (model.isLinkedBacked()) {
            buildLinkedView(model);
        } else {
            buildArrayView(model);
        }
    }

    // ── Array Stack: slot-based view with indices ───────────

    private void buildArrayView(StackStateModel model) {
        for (int i = model.elements().size() - 1; i >= 0; i--) {
            boolean isTop = (i == model.elements().size() - 1);
            HBox row = buildArrayCell(model.elements().get(i), isTop, i);
            stackContainer.getChildren().add(row);
        }

        // Bottom plate (rigid base representing array boundary)
        HBox plate = new HBox();
        plate.getStyleClass().add("stack-plate");
        plate.setMinHeight(4);
        plate.setMaxWidth(180);
        plate.setAlignment(Pos.CENTER);
        stackContainer.getChildren().add(plate);
    }

    private HBox buildArrayCell(String value, boolean isTop, int index) {
        HBox row = new HBox(6);
        row.setAlignment(Pos.CENTER);

        // Index label (array-specific)
        Label idxLabel = new Label("[" + index + "]");
        idxLabel.getStyleClass().add("stack-array-index");
        idxLabel.setMinWidth(32);
        idxLabel.setAlignment(Pos.CENTER_RIGHT);

        // Slot cell
        StackPane cell = new StackPane();
        cell.getStyleClass().add("stack-cell");
        cell.getStyleClass().add("stack-cell-array");
        if (isTop) {
            cell.getStyleClass().add("stack-cell-top");
        }
        cell.setMinWidth(140);
        cell.setMaxWidth(180);
        cell.setMinHeight(34);

        Label valLabel = new Label(value);
        valLabel.getStyleClass().add("stack-cell-value");
        cell.getChildren().add(valLabel);

        // Marker
        Label marker = new Label();
        marker.getStyleClass().add("stack-marker");
        marker.setMinWidth(50);
        if (isTop) {
            marker.setText("← top");
            marker.getStyleClass().add("stack-marker-top");
        } else if (index == 0) {
            marker.setText("  bottom");
            marker.getStyleClass().add("stack-marker-bottom");
        }

        row.getChildren().addAll(idxLabel, cell, marker);
        return row;
    }

    // ── Linked Stack: node-chain view with pointers ─────────

    private void buildLinkedView(StackStateModel model) {
        // Top reference pointer
        Label topRef = new Label("top ─▶");
        topRef.getStyleClass().add("stack-linked-ref");
        stackContainer.getChildren().add(topRef);

        for (int i = model.elements().size() - 1; i >= 0; i--) {
            boolean isTop = (i == model.elements().size() - 1);
            boolean isBottom = (i == 0);

            // Node
            HBox nodeRow = buildLinkedNode(model.elements().get(i), isTop);
            stackContainer.getChildren().add(nodeRow);

            // Link arrow between nodes (except after last)
            if (!isBottom) {
                Label link = new Label("│\n▼");
                link.getStyleClass().add("stack-linked-arrow");
                link.setAlignment(Pos.CENTER);
                link.setMaxWidth(Double.MAX_VALUE);
                HBox linkRow = new HBox(link);
                linkRow.setAlignment(Pos.CENTER);
                stackContainer.getChildren().add(linkRow);
            }
        }

        // Null terminator
        Label nullTerm = new Label("│\n∅");
        nullTerm.getStyleClass().add("stack-linked-null");
        nullTerm.setAlignment(Pos.CENTER);
        nullTerm.setMaxWidth(Double.MAX_VALUE);
        HBox nullRow = new HBox(nullTerm);
        nullRow.setAlignment(Pos.CENTER);
        stackContainer.getChildren().add(nullRow);
    }

    private HBox buildLinkedNode(String value, boolean isTop) {
        HBox row = new HBox(6);
        row.setAlignment(Pos.CENTER);

        StackPane node = new StackPane();
        node.getStyleClass().add("stack-cell");
        node.getStyleClass().add("stack-cell-linked");
        if (isTop) {
            node.getStyleClass().add("stack-cell-top");
        }
        node.setMinWidth(120);
        node.setMaxWidth(160);
        node.setMinHeight(34);

        // Value + next pointer indicator
        HBox content = new HBox(6);
        content.setAlignment(Pos.CENTER);
        Label valLabel = new Label(value);
        valLabel.getStyleClass().add("stack-cell-value");
        Label nextLabel = new Label("│ next");
        nextLabel.getStyleClass().add("stack-linked-next-label");
        content.getChildren().addAll(valLabel, nextLabel);
        node.getChildren().add(content);

        row.getChildren().add(node);
        return row;
    }
}
