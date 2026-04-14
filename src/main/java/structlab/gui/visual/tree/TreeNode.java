package structlab.gui.visual.tree;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Reusable styled node component for tree and graph visuals.
 * Renders a circular/rounded node with a value label, optional emphasis,
 * and subtle fade-in animation.
 *
 * <p>Designed to be used by heap tree visuals now and graph/algorithm
 * visuals in the future.</p>
 */
public class TreeNode extends StackPane {

    private final Label valueLabel;
    private boolean emphasized;

    /**
     * Creates a tree node displaying the given value.
     *
     * @param value    the text to display
     * @param emphasized whether this node should be highlighted (e.g. root, min)
     */
    public TreeNode(String value, boolean emphasized) {
        this.emphasized = emphasized;
        getStyleClass().add("tree-node");
        if (emphasized) {
            getStyleClass().add("tree-node-emphasis");
        }
        setAlignment(Pos.CENTER);
        setMinSize(36, 36);
        setPrefSize(40, 40);
        setMaxSize(48, 48);

        valueLabel = new Label(value);
        valueLabel.getStyleClass().add("tree-node-value");
        if (emphasized) {
            valueLabel.getStyleClass().add("tree-node-value-emphasis");
        }
        getChildren().add(valueLabel);
    }

    /**
     * Creates a non-emphasized tree node.
     */
    public TreeNode(String value) {
        this(value, false);
    }

    /**
     * Updates the displayed value.
     */
    public void setValue(String value) {
        valueLabel.setText(value);
    }

    /**
     * Sets or clears emphasis styling (e.g. root highlight).
     */
    public void setEmphasized(boolean emphasized) {
        if (this.emphasized == emphasized) return;
        this.emphasized = emphasized;
        if (emphasized) {
            if (!getStyleClass().contains("tree-node-emphasis"))
                getStyleClass().add("tree-node-emphasis");
            if (!valueLabel.getStyleClass().contains("tree-node-value-emphasis"))
                valueLabel.getStyleClass().add("tree-node-value-emphasis");
        } else {
            getStyleClass().remove("tree-node-emphasis");
            valueLabel.getStyleClass().remove("tree-node-value-emphasis");
        }
    }

    /**
     * Plays a subtle fade-in animation on this node.
     */
    public void animateFadeIn() {
        setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(250), this);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }
}
