package structlab.gui.visual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;

/**
 * Visual state pane for LinkedDeque.
 * Shows nodes as bidirectionally-linked chips with front and rear markers,
 * emphasising two-ended access on a linked chain.
 */
public class LinkedDequeVisualPane extends VBox {

    private final Label sizeLabel;
    private final HBox chainContainer;
    private final Label emptyLabel;

    public LinkedDequeVisualPane() {
        setSpacing(8);
        setPadding(new Insets(12));
        getStyleClass().add("visual-state-pane");

        // ── Header ──────────────────────────────────
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Linked Deque");
        title.getStyleClass().add("visual-state-title");
        Label implBadge = new Label("linked-backed");
        implBadge.getStyleClass().add("deque-linked-impl-badge");
        sizeLabel = new Label("Size: 0");
        sizeLabel.getStyleClass().add("visual-state-meta");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, implBadge, spacer, sizeLabel);

        // ── Chain container ─────────────────────────
        chainContainer = new HBox(0);
        chainContainer.setAlignment(Pos.CENTER_LEFT);
        chainContainer.getStyleClass().add("deque-chain-container");
        chainContainer.setPadding(new Insets(8, 0, 8, 0));

        // ── Empty state ─────────────────────────────
        emptyLabel = new Label("Empty deque — no nodes");
        emptyLabel.getStyleClass().add("visual-empty-state");
        emptyLabel.setMaxWidth(Double.MAX_VALUE);
        emptyLabel.setAlignment(Pos.CENTER);

        getChildren().addAll(header, chainContainer);
    }

    public void update(LinkedDequeStateModel model) {
        chainContainer.getChildren().clear();
        sizeLabel.setText("Size: " + model.size());

        if (model.isEmpty()) {
            chainContainer.getChildren().add(emptyLabel);
            return;
        }

        // Leading null
        Label leadNull = new Label("∅");
        leadNull.getStyleClass().add("deque-null-terminator");
        leadNull.setPadding(new Insets(10, 4, 0, 0));
        chainContainer.getChildren().add(leadNull);

        for (int i = 0; i < model.nodes().size(); i++) {
            boolean isFront = (i == 0);
            boolean isRear = (i == model.nodes().size() - 1);

            // Bidirectional arrow between nodes
            if (i > 0) {
                Label arrow = new Label("⇄");
                arrow.getStyleClass().add("deque-bidi-arrow");
                arrow.setPadding(new Insets(10, 2, 0, 2));
                chainContainer.getChildren().add(arrow);
            }

            VBox nodeGroup = new VBox(2);
            nodeGroup.setAlignment(Pos.CENTER);

            // Marker above
            Label marker = new Label();
            marker.getStyleClass().add("deque-node-marker");
            marker.setMinHeight(16);
            if (isFront) {
                marker.setText("front");
                marker.getStyleClass().add("deque-marker-front");
            }
            if (isRear) {
                marker.setText(isFront ? "front/rear" : "rear");
                marker.getStyleClass().add("deque-marker-back");
            }

            // Node chip
            StackPane nodeChip = new StackPane();
            nodeChip.getStyleClass().add("deque-node-chip");
            if (isFront) nodeChip.getStyleClass().add("deque-node-front");
            if (isRear) nodeChip.getStyleClass().add("deque-node-rear");
            nodeChip.setMinWidth(52);
            nodeChip.setMinHeight(36);

            // Inner: prev indicator | value | next indicator
            HBox inner = new HBox(0);
            inner.setAlignment(Pos.CENTER);

            Label prevInd = new Label("◁");
            prevInd.getStyleClass().add("deque-link-indicator");
            if (isFront) prevInd.getStyleClass().add("deque-link-null");

            Label valLabel = new Label(model.nodes().get(i));
            valLabel.getStyleClass().add("deque-node-value");
            valLabel.setPadding(new Insets(0, 6, 0, 6));

            Label nextInd = new Label("▷");
            nextInd.getStyleClass().add("deque-link-indicator");
            if (isRear) nextInd.getStyleClass().add("deque-link-null");

            inner.getChildren().addAll(prevInd, valLabel, nextInd);
            nodeChip.getChildren().add(inner);

            Tooltip.install(nodeChip, new Tooltip(
                    "Position: " + i + "\nValue: " + model.nodes().get(i)
                            + (isFront ? "\n(front)" : "") + (isRear ? "\n(rear)" : "")));

            nodeGroup.getChildren().addAll(marker, nodeChip);
            chainContainer.getChildren().add(nodeGroup);
        }

        // Trailing null
        Label trailNull = new Label("∅");
        trailNull.getStyleClass().add("deque-null-terminator");
        trailNull.setPadding(new Insets(10, 0, 0, 4));
        chainContainer.getChildren().add(trailNull);
    }
}
