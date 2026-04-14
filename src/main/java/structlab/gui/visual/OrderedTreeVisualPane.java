package structlab.gui.visual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import structlab.gui.visual.tree.TreeCanvas;

/**
 * Visual state component for ordered tree structures (BST / AVL).
 * Shows the tree shape using {@link TreeCanvas} with metrics in the header.
 */
public class OrderedTreeVisualPane extends VBox {

    private final Label sizeLabel;
    private final Label heightLabel;
    private final Label rootLabel;
    private final Label implLabel;
    private final TreeCanvas treeCanvas;
    private final Label emptyLabel;

    public OrderedTreeVisualPane() {
        setSpacing(10);
        setPadding(new Insets(12));
        getStyleClass().add("visual-state-pane");

        // ── Header ──────────────────────────────────
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        implLabel = new Label("Tree");
        implLabel.getStyleClass().add("visual-state-title");
        sizeLabel = new Label("Size: 0");
        sizeLabel.getStyleClass().add("visual-state-meta");
        heightLabel = new Label("Height: -1");
        heightLabel.getStyleClass().add("visual-state-meta");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        rootLabel = new Label("");
        rootLabel.getStyleClass().addAll("heap-min-badge");
        header.getChildren().addAll(implLabel, spacer, rootLabel, heightLabel, sizeLabel);

        // ── Tree visual ─────────────────────────────
        treeCanvas = new TreeCanvas();
        treeCanvas.getStyleClass().add("ordered-tree-section");

        // ── Empty state ─────────────────────────────
        emptyLabel = new Label("Empty tree");
        emptyLabel.getStyleClass().add("visual-empty-state");
        emptyLabel.setMaxWidth(Double.MAX_VALUE);
        emptyLabel.setAlignment(Pos.CENTER);

        getChildren().addAll(header, treeCanvas);
    }

    public void update(OrderedTreeStateModel model) {
        sizeLabel.setText("Size: " + model.size());
        heightLabel.setText("Height: " + model.height());

        String title = "AVLTree".equals(model.implName()) ? "AVL Tree" : "Binary Search Tree";
        implLabel.setText(title);

        if (model.isEmpty()) {
            rootLabel.setText("");
            treeCanvas.getChildren().clear();
            treeCanvas.getChildren().add(emptyLabel);
            return;
        }

        rootLabel.setText("root = " + model.rootValue());
        treeCanvas.renderOrderedTree(model.nodes());
    }
}
