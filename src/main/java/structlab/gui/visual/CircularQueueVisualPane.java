package structlab.gui.visual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

/**
 * Visual state component for Circular Array Queue.
 * Shows the backing array as a horizontal lane of cells with front/rear markers,
 * capacity, and logical order beneath.
 */
public class CircularQueueVisualPane extends VBox {

    private final HBox slotContainer;
    private final HBox logicalContainer;
    private final Label sizeLabel;
    private final Label capacityLabel;
    private final Label emptyLabel;

    public CircularQueueVisualPane() {
        setSpacing(10);
        setPadding(new Insets(12));
        getStyleClass().add("visual-state-pane");

        // Header
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Circular Array Queue");
        title.getStyleClass().add("visual-state-title");
        sizeLabel = new Label("Size: 0");
        sizeLabel.getStyleClass().add("visual-state-meta");
        capacityLabel = new Label("Capacity: 0");
        capacityLabel.getStyleClass().add("visual-state-meta");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, spacer, sizeLabel, capacityLabel);

        // Backing array: slot lane
        Label bufferLabel = new Label("BUFFER");
        bufferLabel.getStyleClass().addAll("visual-section-label");
        slotContainer = new HBox(0);
        slotContainer.setAlignment(Pos.CENTER_LEFT);
        slotContainer.getStyleClass().add("cq-slot-container");
        slotContainer.setPadding(new Insets(4, 0, 4, 0));

        // Logical order
        Label logLabel = new Label("LOGICAL ORDER");
        logLabel.getStyleClass().addAll("visual-section-label");
        logicalContainer = new HBox(0);
        logicalContainer.setAlignment(Pos.CENTER_LEFT);
        logicalContainer.getStyleClass().add("cq-logical-container");
        logicalContainer.setPadding(new Insets(4, 0, 0, 0));

        // Empty state
        emptyLabel = new Label("Empty queue — all slots available");
        emptyLabel.getStyleClass().add("visual-empty-state");
        emptyLabel.setMaxWidth(Double.MAX_VALUE);
        emptyLabel.setAlignment(Pos.CENTER);

        getChildren().addAll(header, bufferLabel, slotContainer, logLabel, logicalContainer);
    }

    public void update(CircularQueueStateModel model) {
        slotContainer.getChildren().clear();
        logicalContainer.getChildren().clear();
        sizeLabel.setText("Size: " + model.size());
        capacityLabel.setText("Capacity: " + model.capacity());

        if (model.isEmpty()) {
            // Show empty slots
            for (int i = 0; i < model.capacity(); i++) {
                slotContainer.getChildren().add(buildSlot("", i, false, false, false));
            }
            logicalContainer.getChildren().add(emptyLabel);
            return;
        }

        int front = model.frontIndex();
        int rear = model.rearIndex();

        // Backing array slots
        for (int i = 0; i < model.slots().size(); i++) {
            String val = model.slots().get(i);
            boolean isEmpty = "null".equals(val);
            boolean isFront = (i == front);
            boolean isRear = (i == rear);
            slotContainer.getChildren().add(buildSlot(
                    isEmpty ? "" : val, i, isFront, isRear, !isEmpty));
        }

        // Logical order
        Label deqLabel = new Label("dequeue ←");
        deqLabel.getStyleClass().add("queue-direction-label");
        deqLabel.setPadding(new Insets(0, 8, 0, 0));
        logicalContainer.getChildren().add(deqLabel);

        for (int i = 0; i < model.logical().size(); i++) {
            StackPane cell = new StackPane();
            cell.getStyleClass().add("cq-logical-cell");
            cell.setMinWidth(44);
            cell.setMinHeight(30);
            if (i == 0) cell.getStyleClass().add("queue-cell-front");
            if (i == model.logical().size() - 1) cell.getStyleClass().add("queue-cell-rear");

            Label valLabel = new Label(model.logical().get(i));
            valLabel.getStyleClass().add("queue-cell-value");
            cell.getChildren().add(valLabel);
            logicalContainer.getChildren().add(cell);

            if (i < model.logical().size() - 1) {
                Label arrow = new Label("→");
                arrow.getStyleClass().add("queue-arrow");
                arrow.setPadding(new Insets(0, 2, 0, 2));
                logicalContainer.getChildren().add(arrow);
            }
        }

        Label enqLabel = new Label("→ enqueue");
        enqLabel.getStyleClass().add("queue-direction-label");
        enqLabel.setPadding(new Insets(0, 0, 0, 8));
        logicalContainer.getChildren().add(enqLabel);
    }

    private VBox buildSlot(String value, int index, boolean isFront, boolean isRear, boolean occupied) {
        VBox group = new VBox(2);
        group.setAlignment(Pos.CENTER);

        // Marker above
        Label marker = new Label();
        marker.getStyleClass().add("cq-slot-marker");
        marker.setMinHeight(16);
        marker.setAlignment(Pos.CENTER);
        if (isFront && isRear) {
            marker.setText("F/R");
            marker.getStyleClass().add("cq-marker-front");
        } else if (isFront) {
            marker.setText("F");
            marker.getStyleClass().add("cq-marker-front");
        } else if (isRear) {
            marker.setText("R");
            marker.getStyleClass().add("cq-marker-rear");
        }

        // Cell
        StackPane cell = new StackPane();
        cell.getStyleClass().add("cq-slot");
        if (occupied) {
            cell.getStyleClass().add("cq-slot-occupied");
        } else {
            cell.getStyleClass().add("cq-slot-empty");
        }
        if (isFront) cell.getStyleClass().add("cq-slot-front");
        if (isRear) cell.getStyleClass().add("cq-slot-rear");
        cell.setMinWidth(52);
        cell.setMinHeight(38);

        Label valLabel = new Label(value.isEmpty() ? "·" : value);
        valLabel.getStyleClass().add(occupied ? "cq-slot-value" : "cq-slot-empty-value");
        cell.getChildren().add(valLabel);

        // Index below
        Label idxLabel = new Label(String.valueOf(index));
        idxLabel.getStyleClass().add("cq-slot-index");

        group.getChildren().addAll(marker, cell, idxLabel);
        return group;
    }
}
