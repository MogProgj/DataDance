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
    private static HeapVisualPane heapPane;
    private static PriorityQueueVisualPane priorityQueuePane;
    private static HashChainingVisualPane hashChainingPane;
    private static HashOpenAddressingVisualPane hashOpenAddressingPane;
    private static HashSetVisualPane hashSetPane;
    private static SinglyLinkedListVisualPane singlyLinkedListPane;
    private static DoublyLinkedListVisualPane doublyLinkedListPane;
    private static ArrayDequeVisualPane arrayDequePane;
    private static LinkedDequeVisualPane linkedDequePane;

    /**
     * Returns true if the given snapshot type has a visual state component.
     */
    public static boolean isSupported(String snapshot) {
        String type = StateModelParser.structureType(snapshot);
        return switch (type) {
            case "ArrayStack", "LinkedStack",
                 "LinkedQueue", "TwoStackQueue",
                 "CircularArrayQueue",
                 "BinaryHeap", "HeapPriorityQueue",
                 "HashTableChaining", "HashTableOpenAddressing",
                 "HashSetCustom",
                 "SinglyLinkedList", "DoublyLinkedList",
                 "ArrayDequeCustom", "LinkedDeque" -> true;
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
            case "BinaryHeap" -> {
                if (heapPane == null) heapPane = new HeapVisualPane();
                heapPane.update(StateModelParser.parseBinaryHeap(snapshot));
                yield heapPane;
            }
            case "HeapPriorityQueue" -> {
                if (priorityQueuePane == null) priorityQueuePane = new PriorityQueueVisualPane();
                priorityQueuePane.update(StateModelParser.parseHeapPriorityQueue(snapshot));
                yield priorityQueuePane;
            }
            case "HashTableChaining" -> {
                if (hashChainingPane == null) hashChainingPane = new HashChainingVisualPane();
                hashChainingPane.update(StateModelParser.parseHashTableChaining(snapshot));
                yield hashChainingPane;
            }
            case "HashTableOpenAddressing" -> {
                if (hashOpenAddressingPane == null) hashOpenAddressingPane = new HashOpenAddressingVisualPane();
                hashOpenAddressingPane.update(StateModelParser.parseHashTableOpenAddressing(snapshot));
                yield hashOpenAddressingPane;
            }
            case "HashSetCustom" -> {
                if (hashSetPane == null) hashSetPane = new HashSetVisualPane();
                hashSetPane.update(StateModelParser.parseHashSetCustom(snapshot));
                yield hashSetPane;
            }
            case "SinglyLinkedList" -> {
                if (singlyLinkedListPane == null) singlyLinkedListPane = new SinglyLinkedListVisualPane();
                singlyLinkedListPane.update(StateModelParser.parseSinglyLinkedList(snapshot));
                yield singlyLinkedListPane;
            }
            case "DoublyLinkedList" -> {
                if (doublyLinkedListPane == null) doublyLinkedListPane = new DoublyLinkedListVisualPane();
                doublyLinkedListPane.update(StateModelParser.parseDoublyLinkedList(snapshot));
                yield doublyLinkedListPane;
            }
            case "ArrayDequeCustom" -> {
                if (arrayDequePane == null) arrayDequePane = new ArrayDequeVisualPane();
                arrayDequePane.update(StateModelParser.parseArrayDequeCustom(snapshot));
                yield arrayDequePane;
            }
            case "LinkedDeque" -> {
                if (linkedDequePane == null) linkedDequePane = new LinkedDequeVisualPane();
                linkedDequePane.update(StateModelParser.parseLinkedDeque(snapshot));
                yield linkedDequePane;
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
        heapPane = null;
        priorityQueuePane = null;
        hashChainingPane = null;
        hashOpenAddressingPane = null;
        hashSetPane = null;
        singlyLinkedListPane = null;
        doublyLinkedListPane = null;
        arrayDequePane = null;
        linkedDequePane = null;
    }
}
