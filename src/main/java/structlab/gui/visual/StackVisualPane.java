package structlab.gui.visual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

/**
 * Visual state component for Stack structures.
 * Shows elements stacked vertically with the top clearly marked.
 */
public class StackVisualPane extends VBox {

    private final VBox stackContainer;
    private final Label sizeLabel;
    private final Label emptyLabel;

    public StackVisualPane() {
        setSpacing(8);
        setPadding(new Insets(12));
        getStyleClass().add("visual-state-pane");

        // Header
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Stack");
        title.getStyleClass().add("visual-state-title");
        sizeLabel = new Label("Size: 0");
        sizeLabel.getStyleClass().add("visual-state-meta");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, spacer, sizeLabel);

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

        if (model.isEmpty()) {
            stackContainer.getChildren().add(emptyLabel);
            return;
        }

        // Build from top to bottom visually (top of stack appears at the top of the pane)
        // model.elements() is bottom-to-top, so iterate in reverse for visual top-first layout
        for (int i = model.elements().size() - 1; i >= 0; i--) {
            boolean isTop = (i == model.elements().size() - 1);
            HBox cell = buildCell(model.elements().get(i), isTop, i);
            stackContainer.getChildren().add(cell);
        }

        // Bottom plate
        HBox plate = new HBox();
        plate.getStyleClass().add("stack-plate");
        plate.setMinHeight(4);
        plate.setMaxWidth(180);
        plate.setAlignment(Pos.CENTER);
        stackContainer.getChildren().add(plate);
    }

    private HBox buildCell(String value, boolean isTop, int index) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER);
        row.setPadding(new Insets(0, 0, 0, 0));

        // Element cell
        StackPane cell = new StackPane();
        cell.getStyleClass().add("stack-cell");
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

        row.getChildren().addAll(cell, marker);
        return row;
    }
}
