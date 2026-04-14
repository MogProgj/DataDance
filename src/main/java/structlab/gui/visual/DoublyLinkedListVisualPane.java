package structlab.gui.visual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;

/**
 * Visual state pane for DoublyLinkedList.
 * Shows nodes as bidirectionally-linked chips with head and tail markers
 * and dual-direction arrows to distinguish from singly linked lists.
 */
public class DoublyLinkedListVisualPane extends VBox {

    private final Label sizeLabel;
    private final HBox chainContainer;
    private final Label emptyLabel;

    public DoublyLinkedListVisualPane() {
        setSpacing(8);
        setPadding(new Insets(12));
        getStyleClass().add("visual-state-pane");

        // ── Header ──────────────────────────────────
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Doubly Linked List");
        title.getStyleClass().add("visual-state-title");
        Label implBadge = new Label("bidirectional");
        implBadge.getStyleClass().add("list-doubly-impl-badge");
        sizeLabel = new Label("Size: 0");
        sizeLabel.getStyleClass().add("visual-state-meta");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, implBadge, spacer, sizeLabel);

        // ── Chain container ─────────────────────────
        chainContainer = new HBox(0);
        chainContainer.setAlignment(Pos.CENTER_LEFT);
        chainContainer.getStyleClass().add("list-chain-container");
        chainContainer.setPadding(new Insets(8, 0, 8, 0));

        // ── Empty state ─────────────────────────────
        emptyLabel = new Label("Empty list — no nodes");
        emptyLabel.getStyleClass().add("visual-empty-state");
        emptyLabel.setMaxWidth(Double.MAX_VALUE);
        emptyLabel.setAlignment(Pos.CENTER);

        getChildren().addAll(header, chainContainer);
    }

    public void update(DoublyLinkedListStateModel model) {
        chainContainer.getChildren().clear();
        sizeLabel.setText("Size: " + model.size());

        if (model.isEmpty()) {
            chainContainer.getChildren().add(emptyLabel);
            return;
        }

        // Leading null marker
        Label leadNull = new Label("∅");
        leadNull.getStyleClass().add("list-null-terminator");
        leadNull.setPadding(new Insets(10, 4, 0, 0));
        chainContainer.getChildren().add(leadNull);

        for (int i = 0; i < model.nodes().size(); i++) {
            boolean isHead = (i == 0);
            boolean isTail = (i == model.nodes().size() - 1);

            // Bidirectional arrow before node (except first)
            if (i > 0) {
                Label arrow = new Label("⇄");
                arrow.getStyleClass().add("list-bidi-arrow");
                arrow.setPadding(new Insets(10, 2, 0, 2));
                chainContainer.getChildren().add(arrow);
            }

            VBox nodeGroup = new VBox(2);
            nodeGroup.setAlignment(Pos.CENTER);

            // Marker above the node
            Label marker = new Label();
            marker.getStyleClass().add("list-node-marker");
            marker.setMinHeight(16);
            if (isHead) {
                marker.setText("head");
                marker.getStyleClass().add("list-marker-head");
            }
            if (isTail) {
                marker.setText(isHead ? "head/tail" : "tail");
                marker.getStyleClass().add("list-marker-tail");
            }

            // Node chip — distinct doubly-linked style
            StackPane nodeChip = new StackPane();
            nodeChip.getStyleClass().add("list-node-chip");
            nodeChip.getStyleClass().add("list-node-doubly");
            if (isHead) nodeChip.getStyleClass().add("list-node-head");
            if (isTail) nodeChip.getStyleClass().add("list-node-tail");
            nodeChip.setMinWidth(56);
            nodeChip.setMinHeight(36);

            // Inner layout: prev indicator | value | next indicator
            HBox inner = new HBox(0);
            inner.setAlignment(Pos.CENTER);

            Label prevIndicator = new Label("◁");
            prevIndicator.getStyleClass().add("list-link-indicator");
            if (isHead) prevIndicator.getStyleClass().add("list-link-null");

            Label valLabel = new Label(model.nodes().get(i));
            valLabel.getStyleClass().add("list-node-value");
            valLabel.setPadding(new Insets(0, 6, 0, 6));

            Label nextIndicator = new Label("▷");
            nextIndicator.getStyleClass().add("list-link-indicator");
            if (isTail) nextIndicator.getStyleClass().add("list-link-null");

            inner.getChildren().addAll(prevIndicator, valLabel, nextIndicator);
            nodeChip.getChildren().add(inner);

            Tooltip.install(nodeChip, new Tooltip(
                    "Index: " + i + "\nValue: " + model.nodes().get(i)
                            + "\nPrev: " + (isHead ? "null" : model.nodes().get(i - 1))
                            + "\nNext: " + (isTail ? "null" : model.nodes().get(i + 1))));

            nodeGroup.getChildren().addAll(marker, nodeChip);
            chainContainer.getChildren().add(nodeGroup);
        }

        // Trailing null marker
        Label trailNull = new Label("∅");
        trailNull.getStyleClass().add("list-null-terminator");
        trailNull.setPadding(new Insets(10, 0, 0, 4));
        chainContainer.getChildren().add(trailNull);
    }
}
