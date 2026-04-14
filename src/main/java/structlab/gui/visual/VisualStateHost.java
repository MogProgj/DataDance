package structlab.gui.visual;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Reusable host that renders either a visual state pane (primary) or a
 * raw-text fallback (secondary).
 *
 * <p>This component formalizes the visual-first rendering pattern used by
 * both Explore and Compare modes: check whether a snapshot has visual
 * support, render visually if so, otherwise fall back to text.  Having
 * a single, reusable component eliminates the duplicated rendering logic
 * that previously existed in {@code MainWindowController} and
 * {@code ComparisonCardPane}.</p>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 *   VisualStateHost host = new VisualStateHost("No active session.");
 *   parent.getChildren().add(host);
 *   VBox.setVgrow(host, Priority.ALWAYS);
 *
 *   // On state change:
 *   host.render(rawSnapshot, renderedText);
 *
 *   // On session close:
 *   host.showPlaceholder();
 * }</pre>
 */
public class VisualStateHost extends StackPane {

    private final TextArea fallbackArea;
    private final VisualPaneCache paneCache;
    private final String placeholderText;

    /**
     * Creates a host with the given placeholder text and a <em>new</em>
     * pane cache (suitable for Compare cards, where each card needs its
     * own cache instance).
     */
    public VisualStateHost(String placeholder) {
        this(placeholder, new VisualPaneCache());
    }

    /**
     * Creates a host backed by the given pane cache (suitable for Explore
     * mode, which shares a single cache across sessions via
     * {@link VisualStateFactory}).
     */
    public VisualStateHost(String placeholder, VisualPaneCache paneCache) {
        this.placeholderText = placeholder;
        this.paneCache = paneCache;

        fallbackArea = new TextArea();
        fallbackArea.setEditable(false);
        fallbackArea.setWrapText(true);
        fallbackArea.getStyleClass().add("mono-area");
        fallbackArea.setPromptText(placeholder);

        getChildren().add(fallbackArea);
    }

    /**
     * Renders the given snapshot visually if supported, otherwise falls
     * back to the rendered text representation.
     *
     * @param rawSnapshot  the raw snapshot string from the runtime
     * @param renderedText the text rendering (used as fallback)
     */
    public void render(String rawSnapshot, String renderedText) {
        Node visual = tryVisual(rawSnapshot);
        if (visual != null) {
            ScrollPane scroll = new ScrollPane(visual);
            scroll.setFitToWidth(true);
            scroll.getStyleClass().add("visual-scroll");
            getChildren().setAll(scroll);
        } else {
            fallbackArea.setText(renderedText != null ? renderedText : "");
            getChildren().setAll(fallbackArea);
        }
    }

    /**
     * Renders the given snapshot visually if supported, otherwise falls
     * back to the rendered text.  For Compare cards, constrains the
     * scroll height.
     *
     * @param rawSnapshot   the raw snapshot string
     * @param renderedText  the text rendering fallback
     * @param maxScrollHeight max pixel height for the scroll pane (0 = unconstrained)
     */
    public void render(String rawSnapshot, String renderedText, double maxScrollHeight) {
        Node visual = tryVisual(rawSnapshot);
        if (visual != null) {
            ScrollPane scroll = new ScrollPane(visual);
            scroll.setFitToWidth(true);
            scroll.getStyleClass().add("visual-scroll");
            if (maxScrollHeight > 0) {
                scroll.setMaxHeight(maxScrollHeight);
            }
            getChildren().setAll(scroll);
        } else {
            fallbackArea.setText(renderedText != null ? renderedText : "");
            getChildren().setAll(fallbackArea);
        }
    }

    /**
     * Shows the original placeholder in the fallback text area.
     */
    public void showPlaceholder() {
        fallbackArea.clear();
        fallbackArea.setPromptText(placeholderText);
        getChildren().setAll(fallbackArea);
    }

    /**
     * Clears content and shows the fallback area with no text.
     */
    public void clear() {
        fallbackArea.clear();
        getChildren().setAll(fallbackArea);
    }

    /**
     * Resets the underlying pane cache.  Call this when switching sessions
     * to avoid stale visual pane state.
     */
    public void resetCache() {
        paneCache.reset();
    }

    /**
     * Returns the fallback text area for direct access when needed
     * (e.g. setting text directly for non-snapshot content).
     */
    public TextArea getFallbackArea() {
        return fallbackArea;
    }

    /**
     * Returns true if the current display is showing a visual pane
     * (not the text fallback).
     */
    public boolean isShowingVisual() {
        return !getChildren().isEmpty()
                && getChildren().get(0) instanceof ScrollPane;
    }

    // ────────────────────────────────────────────────────────

    private Node tryVisual(String rawSnapshot) {
        if (rawSnapshot == null) return null;

        String type = StateModelParser.structureType(rawSnapshot);
        if ("HeapPriorityQueue".equals(type)) {
            HeapStateModel m = StateModelParser.parseHeapPriorityQueue(rawSnapshot);
            return m == null ? null : paneCache.updateAsPriorityQueue(m);
        }

        VisualState state = StateModelParser.parse(rawSnapshot);
        return state == null ? null : paneCache.update(state);
    }
}
