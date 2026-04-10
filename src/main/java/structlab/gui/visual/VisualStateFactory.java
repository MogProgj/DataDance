package structlab.gui.visual;

import javafx.scene.Node;

/**
 * Factory that creates and updates visual state components for supported structure types.
 * Returns null for unsupported types, signaling the caller to use text fallback.
 */
public final class VisualStateFactory {

    private VisualStateFactory() {}

    // Reusable pane instances to avoid rebuilding on every state update
    private static StackVisualPane stackPane;
    private static QueueVisualPane queuePane;
    private static CircularQueueVisualPane circularQueuePane;

    /**
     * Returns true if the given snapshot type has a visual state component.
     */
    public static boolean isSupported(String snapshot) {
        String type = StateModelParser.structureType(snapshot);
        return switch (type) {
            case "ArrayStack", "LinkedStack",
                 "LinkedQueue", "TwoStackQueue",
                 "CircularArrayQueue" -> true;
            default -> false;
        };
    }

    /**
     * Creates or updates the visual component for the given raw snapshot.
     * Returns the Node to display, or null if the structure type is unsupported.
     */
    public static Node createOrUpdate(String snapshot) {
        String type = StateModelParser.structureType(snapshot);
        return switch (type) {
            case "ArrayStack" -> {
                if (stackPane == null) stackPane = new StackVisualPane();
                stackPane.update(StateModelParser.parseArrayStack(snapshot));
                yield stackPane;
            }
            case "LinkedStack" -> {
                if (stackPane == null) stackPane = new StackVisualPane();
                stackPane.update(StateModelParser.parseLinkedStack(snapshot));
                yield stackPane;
            }
            case "CircularArrayQueue" -> {
                if (circularQueuePane == null) circularQueuePane = new CircularQueueVisualPane();
                circularQueuePane.update(StateModelParser.parseCircularArrayQueue(snapshot));
                yield circularQueuePane;
            }
            case "LinkedQueue" -> {
                if (queuePane == null) queuePane = new QueueVisualPane();
                queuePane.update(StateModelParser.parseLinkedQueue(snapshot));
                yield queuePane;
            }
            case "TwoStackQueue" -> {
                if (queuePane == null) queuePane = new QueueVisualPane();
                queuePane.update(StateModelParser.parseTwoStackQueue(snapshot));
                yield queuePane;
            }
            default -> null;
        };
    }

    /**
     * Resets cached pane instances. Useful when switching between sessions
     * of different structure types.
     */
    public static void reset() {
        stackPane = null;
        queuePane = null;
        circularQueuePane = null;
    }
}
