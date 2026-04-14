package structlab.gui.visual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

/**
 * Visual state component for Queue structures (LinkedQueue, TwoStackQueue).
 * Shows elements in horizontal order with front and rear clearly marked.
 */
public class QueueVisualPane extends VBox {

    private final HBox queueContainer;
    private final Label sizeLabel;
    private final Label emptyLabel;

    public QueueVisualPane() {
        setSpacing(8);
        setPadding(new Insets(12));
        getStyleClass().add("visual-state-pane");

        // Header
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Queue");
        title.getStyleClass().add("visual-state-title");
        sizeLabel = new Label("Size: 0");
        sizeLabel.getStyleClass().add("visual-state-meta");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, spacer, sizeLabel);

        // Queue container — horizontal flow, front on left, rear on right
        queueContainer = new HBox(0);
        queueContainer.setAlignment(Pos.CENTER_LEFT);
        queueContainer.getStyleClass().add("queue-container");
        queueContainer.setPadding(new Insets(8, 0, 8, 0));

        // Empty state
        emptyLabel = new Label("Empty queue");
        emptyLabel.getStyleClass().add("visual-empty-state");
        emptyLabel.setMaxWidth(Double.MAX_VALUE);
        emptyLabel.setAlignment(Pos.CENTER);

        getChildren().addAll(header, queueContainer);
    }

    public void update(QueueStateModel model) {
        queueContainer.getChildren().clear();
        sizeLabel.setText("Size: " + model.size());

        if (model.isEmpty()) {
            queueContainer.getChildren().add(emptyLabel);
            return;
        }

        // Direction indicators
        Label deqLabel = new Label("dequeue ←");
        deqLabel.getStyleClass().add("queue-direction-label");
        deqLabel.setPadding(new Insets(0, 8, 0, 0));
        queueContainer.getChildren().add(deqLabel);

        for (int i = 0; i < model.elements().size(); i++) {
            boolean isFront = (i == 0);
            boolean isRear = (i == model.elements().size() - 1);
            VBox cellGroup = buildCell(model.elements().get(i), isFront, isRear);
            queueContainer.getChildren().add(cellGroup);

            // Arrow between elements
            if (i < model.elements().size() - 1) {
                Label arrow = new Label("→");
                arrow.getStyleClass().add("queue-arrow");
                arrow.setPadding(new Insets(0, 2, 0, 2));
                queueContainer.getChildren().add(arrow);
            }
        }

        Label enqLabel = new Label("→ enqueue");
        enqLabel.getStyleClass().add("queue-direction-label");
        enqLabel.setPadding(new Insets(0, 0, 0, 8));
        queueContainer.getChildren().add(enqLabel);
    }

    private VBox buildCell(String value, boolean isFront, boolean isRear) {
        VBox group = new VBox(2);
        group.setAlignment(Pos.CENTER);

        // Marker above
        Label marker = new Label();
        marker.getStyleClass().add("queue-marker");
        marker.setMinHeight(16);
        if (isFront) {
            marker.setText("front");
            marker.getStyleClass().add("queue-marker-front");
        } else if (isRear) {
            marker.setText("rear");
            marker.getStyleClass().add("queue-marker-rear");
        }

        // Cell
        StackPane cell = new StackPane();
        cell.getStyleClass().add("queue-cell");
        if (isFront) cell.getStyleClass().add("queue-cell-front");
        if (isRear) cell.getStyleClass().add("queue-cell-rear");
        cell.setMinWidth(50);
        cell.setMinHeight(38);

        Label valLabel = new Label(value);
        valLabel.getStyleClass().add("queue-cell-value");
        cell.getChildren().add(valLabel);

        group.getChildren().addAll(marker, cell);
        return group;
    }
}
