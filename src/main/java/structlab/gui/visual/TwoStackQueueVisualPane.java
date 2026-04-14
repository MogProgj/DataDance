package structlab.gui.visual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.List;

/**
 * Visual pane for TwoStackQueue — shows the two-stack implementation truthfully.
 *
 * <p>Three-zone layout:</p>
 * <ol>
 *   <li><b>Input Stack</b> (inbox) — where enqueued elements accumulate</li>
 *   <li><b>Output Stack</b> (outbox) — where dequeue/peek draws from</li>
 *   <li><b>Effective Queue</b> — the logical queue order the user experiences</li>
 * </ol>
 */
public class TwoStackQueueVisualPane extends VBox {

    private final Label sizeLabel;
    private final VBox inboxStack;
    private final VBox outboxStack;
    private final HBox effectiveQueue;
    private final Label emptyLabel;
    private final Label inboxCountLabel;
    private final Label outboxCountLabel;

    public TwoStackQueueVisualPane() {
        setSpacing(10);
        setPadding(new Insets(12));
        getStyleClass().add("visual-state-pane");

        // ── Header ──────────────────────────────────
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Two-Stack Queue");
        title.getStyleClass().add("visual-state-title");
        Label implBadge = new Label("2-stack impl");
        implBadge.getStyleClass().add("tsq-impl-badge");
        sizeLabel = new Label("Size: 0");
        sizeLabel.getStyleClass().add("visual-state-meta");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, implBadge, spacer, sizeLabel);

        // ── Two-stack zone (side by side) ───────────
        // Inbox (enqueue) stack
        VBox inboxSection = new VBox(4);
        inboxSection.getStyleClass().add("tsq-stack-section");
        HBox inboxHeader = new HBox(4);
        inboxHeader.setAlignment(Pos.CENTER_LEFT);
        Label inboxLabel = new Label("Enqueue Stack");
        inboxLabel.getStyleClass().add("tsq-stack-label");
        Label inboxArrow = new Label("→ push");
        inboxArrow.getStyleClass().add("tsq-stack-direction");
        inboxCountLabel = new Label("");
        inboxCountLabel.getStyleClass().add("tsq-stack-count");
        Region inboxSpacer = new Region();
        HBox.setHgrow(inboxSpacer, Priority.ALWAYS);
        inboxHeader.getChildren().addAll(inboxLabel, inboxSpacer, inboxArrow, inboxCountLabel);

        inboxStack = new VBox(0);
        inboxStack.setAlignment(Pos.BOTTOM_CENTER);
        inboxStack.getStyleClass().add("tsq-stack-container");
        inboxSection.getChildren().addAll(inboxHeader, inboxStack);

        // Outbox (dequeue) stack
        VBox outboxSection = new VBox(4);
        outboxSection.getStyleClass().add("tsq-stack-section");
        HBox outboxHeader = new HBox(4);
        outboxHeader.setAlignment(Pos.CENTER_LEFT);
        Label outboxLabel = new Label("Dequeue Stack");
        outboxLabel.getStyleClass().add("tsq-stack-label");
        Label outboxArrow = new Label("pop ←");
        outboxArrow.getStyleClass().add("tsq-stack-direction");
        outboxCountLabel = new Label("");
        outboxCountLabel.getStyleClass().add("tsq-stack-count");
        Region outboxSpacer = new Region();
        HBox.setHgrow(outboxSpacer, Priority.ALWAYS);
        outboxHeader.getChildren().addAll(outboxLabel, outboxSpacer, outboxArrow, outboxCountLabel);

        outboxStack = new VBox(0);
        outboxStack.setAlignment(Pos.BOTTOM_CENTER);
        outboxStack.getStyleClass().add("tsq-stack-container");
        outboxSection.getChildren().addAll(outboxHeader, outboxStack);

        // Transfer arrow between stacks
        VBox transferZone = new VBox(2);
        transferZone.setAlignment(Pos.CENTER);
        transferZone.setPadding(new Insets(20, 4, 0, 4));
        Label transferArrow = new Label("⇒");
        transferArrow.getStyleClass().add("tsq-transfer-arrow");
        Label transferNote = new Label("transfer");
        transferNote.getStyleClass().add("tsq-transfer-note");
        transferZone.getChildren().addAll(transferArrow, transferNote);

        HBox stacksRow = new HBox(6);
        stacksRow.setAlignment(Pos.TOP_CENTER);
        HBox.setHgrow(inboxSection, Priority.ALWAYS);
        HBox.setHgrow(outboxSection, Priority.ALWAYS);
        stacksRow.getChildren().addAll(inboxSection, transferZone, outboxSection);

        // ── Effective queue zone ────────────────────
        VBox queueSection = new VBox(4);
        queueSection.getStyleClass().add("tsq-queue-section");
        Label queueLabel = new Label("Effective Queue Order");
        queueLabel.getStyleClass().add("tsq-queue-label");

        effectiveQueue = new HBox(0);
        effectiveQueue.setAlignment(Pos.CENTER_LEFT);
        effectiveQueue.getStyleClass().add("tsq-queue-container");

        queueSection.getChildren().addAll(queueLabel, effectiveQueue);

        // ── Empty state ─────────────────────────────
        emptyLabel = new Label("Empty queue");
        emptyLabel.getStyleClass().add("visual-empty-state");
        emptyLabel.setMaxWidth(Double.MAX_VALUE);
        emptyLabel.setAlignment(Pos.CENTER);

        getChildren().addAll(header, stacksRow, queueSection);
    }

    public void update(TwoStackQueueStateModel model) {
        inboxStack.getChildren().clear();
        outboxStack.getChildren().clear();
        effectiveQueue.getChildren().clear();
        sizeLabel.setText("Size: " + model.size());

        if (model.isEmpty()) {
            inboxCountLabel.setText("");
            outboxCountLabel.setText("");
            buildEmptyStack(inboxStack);
            buildEmptyStack(outboxStack);
            effectiveQueue.getChildren().add(emptyLabel);
            return;
        }

        inboxCountLabel.setText("(" + model.inboxElements().size() + ")");
        outboxCountLabel.setText("(" + model.outboxElements().size() + ")");

        // Build inbox stack (bottom to top, displayed top-first)
        buildStack(inboxStack, model.inboxElements(), "inbox");

        // Build outbox stack (bottom to top, displayed top-first)
        buildStack(outboxStack, model.outboxElements(), "outbox");

        // Build effective queue strip
        buildEffectiveQueue(model);
    }

    private void buildStack(VBox container, List<String> elements, String cssPrefix) {
        if (elements.isEmpty()) {
            buildEmptyStack(container);
            return;
        }

        for (int i = elements.size() - 1; i >= 0; i--) {
            boolean isTop = (i == elements.size() - 1);
            HBox row = new HBox(6);
            row.setAlignment(Pos.CENTER);

            StackPane cell = new StackPane();
            cell.getStyleClass().add("tsq-cell");
            cell.getStyleClass().add("tsq-cell-" + cssPrefix);
            if (isTop) {
                cell.getStyleClass().add("tsq-cell-top");
            }
            cell.setMinWidth(60);
            cell.setMinHeight(30);

            Label valLabel = new Label(elements.get(i));
            valLabel.getStyleClass().add("tsq-cell-value");
            cell.getChildren().add(valLabel);

            Label marker = new Label();
            marker.getStyleClass().add("tsq-marker");
            marker.setMinWidth(30);
            if (isTop) {
                marker.setText("top");
                marker.getStyleClass().add("tsq-marker-top");
            } else if (i == 0) {
                marker.setText("btm");
                marker.getStyleClass().add("tsq-marker-bottom");
            }

            row.getChildren().addAll(cell, marker);
            container.getChildren().add(row);
        }

        // Bottom plate
        HBox plate = new HBox();
        plate.getStyleClass().add("tsq-plate");
        plate.setMinHeight(3);
        plate.setMaxWidth(80);
        plate.setAlignment(Pos.CENTER);
        container.getChildren().add(plate);
    }

    private void buildEmptyStack(VBox container) {
        Label empty = new Label("(empty)");
        empty.getStyleClass().add("tsq-stack-empty");
        container.getChildren().add(empty);
    }

    private void buildEffectiveQueue(TwoStackQueueStateModel model) {
        Label deqLabel = new Label("front ←");
        deqLabel.getStyleClass().add("tsq-queue-direction");
        deqLabel.setPadding(new Insets(0, 6, 0, 0));
        effectiveQueue.getChildren().add(deqLabel);

        List<String> order = model.queueOrder();
        for (int i = 0; i < order.size(); i++) {
            boolean isFront = (i == 0);
            boolean isRear = (i == order.size() - 1);

            StackPane chip = new StackPane();
            chip.getStyleClass().add("tsq-queue-chip");
            if (isFront) chip.getStyleClass().add("tsq-queue-chip-front");
            if (isRear) chip.getStyleClass().add("tsq-queue-chip-rear");
            chip.setMinWidth(36);
            chip.setMinHeight(28);

            Label valLabel = new Label(order.get(i));
            valLabel.getStyleClass().add("tsq-queue-chip-value");
            chip.getChildren().add(valLabel);
            effectiveQueue.getChildren().add(chip);

            if (i < order.size() - 1) {
                Label arrow = new Label("→");
                arrow.getStyleClass().add("tsq-queue-arrow");
                arrow.setPadding(new Insets(0, 2, 0, 2));
                effectiveQueue.getChildren().add(arrow);
            }
        }

        Label enqLabel = new Label("→ rear");
        enqLabel.getStyleClass().add("tsq-queue-direction");
        enqLabel.setPadding(new Insets(0, 0, 0, 6));
        effectiveQueue.getChildren().add(enqLabel);
    }
}
