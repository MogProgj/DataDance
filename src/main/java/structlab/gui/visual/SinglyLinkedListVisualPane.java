package structlab.gui.visual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;

/**
 * Visual state pane for SinglyLinkedList.
 * Shows nodes as forward-linked chips with a clear head marker
 * and unidirectional arrows emphasising one-way traversal.
 */
public class SinglyLinkedListVisualPane extends VBox {

    private final Label sizeLabel;
    private final HBox chainContainer;
    private final Label emptyLabel;

    public SinglyLinkedListVisualPane() {
        setSpacing(8);
        setPadding(new Insets(12));
        getStyleClass().add("visual-state-pane");

        // ── Header ──────────────────────────────────
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Singly Linked List");
        title.getStyleClass().add("visual-state-title");
        Label implBadge = new Label("forward-only");
        implBadge.getStyleClass().add("list-impl-badge");
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

    public void update(SinglyLinkedListStateModel model) {
        chainContainer.getChildren().clear();
        sizeLabel.setText("Size: " + model.size());

        if (model.isEmpty()) {
            chainContainer.getChildren().add(emptyLabel);
            return;
        }

        for (int i = 0; i < model.nodes().size(); i++) {
            boolean isHead = (i == 0);
            boolean isTail = (i == model.nodes().size() - 1);

            VBox nodeGroup = new VBox(2);
            nodeGroup.setAlignment(Pos.CENTER);

            // Marker above the node
            Label marker = new Label();
            marker.getStyleClass().add("list-node-marker");
            marker.setMinHeight(16);
            if (isHead) {
                marker.setText("head");
                marker.getStyleClass().add("list-marker-head");
            } else if (isTail) {
                marker.setText("tail");
                marker.getStyleClass().add("list-marker-tail");
            }

            // Node chip
            StackPane nodeChip = new StackPane();
            nodeChip.getStyleClass().add("list-node-chip");
            nodeChip.getStyleClass().add("list-node-singly");
            if (isHead) nodeChip.getStyleClass().add("list-node-head");
            if (isTail) nodeChip.getStyleClass().add("list-node-tail");
            nodeChip.setMinWidth(50);
            nodeChip.setMinHeight(36);

            Label valLabel = new Label(model.nodes().get(i));
            valLabel.getStyleClass().add("list-node-value");
            nodeChip.getChildren().add(valLabel);

            Tooltip.install(nodeChip, new Tooltip(
                    "Index: " + i + "\nValue: " + model.nodes().get(i)));

            nodeGroup.getChildren().addAll(marker, nodeChip);
            chainContainer.getChildren().add(nodeGroup);

            // Forward arrow
            if (!isTail) {
                Label arrow = new Label("→");
                arrow.getStyleClass().add("list-forward-arrow");
                arrow.setPadding(new Insets(10, 2, 0, 2));
                chainContainer.getChildren().add(arrow);
            }
        }

        // Null terminator
        Label nullTerm = new Label("∅");
        nullTerm.getStyleClass().add("list-null-terminator");
        nullTerm.setPadding(new Insets(10, 0, 0, 4));
        chainContainer.getChildren().add(nullTerm);
    }
}
