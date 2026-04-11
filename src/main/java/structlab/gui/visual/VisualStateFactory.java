package structlab.gui.visual;

import javafx.scene.Node;

/**
 * Factory that creates and updates visual state components for supported structure types.
 * Returns null for unsupported types, signaling the caller to use text fallback.
 *
 * <p>Internally delegates pane caching and dispatch to {@link VisualPaneCache},
 * and uses {@link StateModelParser#parse(String)} for type-safe snapshot parsing.</p>
 */
public final class VisualStateFactory {

    private VisualStateFactory() {}

    private static final VisualPaneCache cache = new VisualPaneCache();

    /**
     * Returns true if the given snapshot type has a visual state component.
     */
    public static boolean isSupported(String snapshot) {
        return StateModelParser.parse(snapshot) != null;
    }

    /**
     * Creates or updates the visual component for the given raw snapshot.
     * Returns the Node to display, or null if the structure type is unsupported.
     */
    public static Node createOrUpdate(String snapshot) {
        // HeapPriorityQueue needs special handling: it produces a HeapStateModel
        // but must render via PriorityQueueVisualPane, not HeapVisualPane.
        String type = StateModelParser.structureType(snapshot);
        if ("HeapPriorityQueue".equals(type)) {
            HeapStateModel m = StateModelParser.parseHeapPriorityQueue(snapshot);
            return cache.updateAsPriorityQueue(m);
        }

        VisualState state = StateModelParser.parse(snapshot);
        if (state == null) return null;
        return cache.update(state);
    }

    /**
     * Resets cached pane instances. Useful when switching between sessions
     * of different structure types.
     */
    public static void reset() {
        cache.reset();
    }
}
