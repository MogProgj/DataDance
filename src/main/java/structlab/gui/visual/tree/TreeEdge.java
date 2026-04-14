package structlab.gui.visual.tree;

import javafx.animation.FadeTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

/**
 * Reusable edge connector for tree and graph visuals.
 * Draws a gently curved line between two points with optional emphasis styling.
 *
 * <p>Designed to connect {@link TreeNode} instances in heap tree visuals now,
 * and to support graph/algorithm edge rendering in the future.</p>
 */
public class TreeEdge extends CubicCurve {

    private static final Color DEFAULT_COLOR = Color.web("#3b4252");
    private static final Color EMPHASIS_COLOR = Color.web("#7c3aed", 0.6);

    /**
     * Creates an edge connecting two coordinate pairs with a gentle curve.
     *
     * @param startX    source X
     * @param startY    source Y
     * @param endX      target X
     * @param endY      target Y
     * @param emphasized whether to use emphasis (highlight) styling
     */
    public TreeEdge(double startX, double startY, double endX, double endY, boolean emphasized) {
        setStartX(startX);
        setStartY(startY);
        setEndX(endX);
        setEndY(endY);

        // Gentle curve: control points offset vertically between start and end
        double midY = (startY + endY) / 2;
        setControlX1(startX);
        setControlY1(midY);
        setControlX2(endX);
        setControlY2(midY);

        setStroke(emphasized ? EMPHASIS_COLOR : DEFAULT_COLOR);
        setStrokeWidth(emphasized ? 2.0 : 1.5);
        setStrokeLineCap(StrokeLineCap.ROUND);
        setFill(null);
        setMouseTransparent(true);

        getStyleClass().add("tree-edge");
        if (emphasized) {
            getStyleClass().add("tree-edge-emphasis");
        }
    }

    /**
     * Creates a non-emphasized edge.
     */
    public TreeEdge(double startX, double startY, double endX, double endY) {
        this(startX, startY, endX, endY, false);
    }

    /**
     * Plays a subtle fade-in animation on this edge.
     */
    public void animateFadeIn() {
        setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(200), this);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }
}
