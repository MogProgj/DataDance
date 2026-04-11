package structlab.gui.visual;

import javafx.scene.Node;

/**
 * Manages a set of visual-pane instances and dispatches state model
 * updates to the correct pane using pattern matching on the
 * {@link VisualState} sealed hierarchy.
 *
 * <p>Two usage patterns:</p>
 * <ul>
 *   <li>{@link VisualStateFactory} holds a single static cache (Explore mode)</li>
 *   <li>{@link ComparisonCardPane} holds one cache per card (Compare mode)</li>
 * </ul>
 *
 * <p>This consolidates the formerly-duplicated type-dispatch switch into
 * a single location, making it trivial to add new families or insert
 * animation/comparison hooks in the future.</p>
 */
public final class VisualPaneCache {

    private StackVisualPane stackPane;
    private QueueVisualPane queuePane;
    private CircularQueueVisualPane circularQueuePane;
    private HeapVisualPane heapPane;
    private PriorityQueueVisualPane priorityQueuePane;
    private HashChainingVisualPane hashChainingPane;
    private HashOpenAddressingVisualPane hashOpenAddressingPane;
    private HashSetVisualPane hashSetPane;
    private SinglyLinkedListVisualPane singlyLinkedListPane;
    private DoublyLinkedListVisualPane doublyLinkedListPane;
    private ArrayDequeVisualPane arrayDequePane;
    private LinkedDequeVisualPane linkedDequePane;
    private FixedArrayVisualPane fixedArrayPane;
    private DynamicArrayVisualPane dynamicArrayPane;

    /**
     * Updates (or lazily creates) the visual pane for the given state
     * and returns it as a {@link Node}.
     *
     * @param state a parsed {@link VisualState}; must not be null
     * @return the JavaFX node to display
     */
    public Node update(VisualState state) {
        if (state instanceof StackStateModel m) {
            if (stackPane == null) stackPane = new StackVisualPane();
            stackPane.update(m);
            return stackPane;
        }
        if (state instanceof QueueStateModel m) {
            if (queuePane == null) queuePane = new QueueVisualPane();
            queuePane.update(m);
            return queuePane;
        }
        if (state instanceof CircularQueueStateModel m) {
            if (circularQueuePane == null) circularQueuePane = new CircularQueueVisualPane();
            circularQueuePane.update(m);
            return circularQueuePane;
        }
        if (state instanceof HeapStateModel m) {
            if (heapPane == null) heapPane = new HeapVisualPane();
            heapPane.update(m);
            return heapPane;
        }
        if (state instanceof HashChainingStateModel m) {
            if (hashChainingPane == null) hashChainingPane = new HashChainingVisualPane();
            hashChainingPane.update(m);
            return hashChainingPane;
        }
        if (state instanceof HashOpenAddressingStateModel m) {
            if (hashOpenAddressingPane == null) hashOpenAddressingPane = new HashOpenAddressingVisualPane();
            hashOpenAddressingPane.update(m);
            return hashOpenAddressingPane;
        }
        if (state instanceof HashSetStateModel m) {
            if (hashSetPane == null) hashSetPane = new HashSetVisualPane();
            hashSetPane.update(m);
            return hashSetPane;
        }
        if (state instanceof SinglyLinkedListStateModel m) {
            if (singlyLinkedListPane == null) singlyLinkedListPane = new SinglyLinkedListVisualPane();
            singlyLinkedListPane.update(m);
            return singlyLinkedListPane;
        }
        if (state instanceof DoublyLinkedListStateModel m) {
            if (doublyLinkedListPane == null) doublyLinkedListPane = new DoublyLinkedListVisualPane();
            doublyLinkedListPane.update(m);
            return doublyLinkedListPane;
        }
        if (state instanceof ArrayDequeStateModel m) {
            if (arrayDequePane == null) arrayDequePane = new ArrayDequeVisualPane();
            arrayDequePane.update(m);
            return arrayDequePane;
        }
        if (state instanceof LinkedDequeStateModel m) {
            if (linkedDequePane == null) linkedDequePane = new LinkedDequeVisualPane();
            linkedDequePane.update(m);
            return linkedDequePane;
        }
        if (state instanceof FixedArrayStateModel m) {
            if (fixedArrayPane == null) fixedArrayPane = new FixedArrayVisualPane();
            fixedArrayPane.update(m);
            return fixedArrayPane;
        }
        if (state instanceof DynamicArrayStateModel m) {
            if (dynamicArrayPane == null) dynamicArrayPane = new DynamicArrayVisualPane();
            dynamicArrayPane.update(m);
            return dynamicArrayPane;
        }
        throw new IllegalArgumentException("Unhandled VisualState type: " + state.getClass().getName());
    }

    /**
     * Updates a HeapStateModel into the PriorityQueueVisualPane
     * specifically (used when the snapshot type is HeapPriorityQueue).
     */
    public Node updateAsPriorityQueue(HeapStateModel m) {
        if (priorityQueuePane == null) priorityQueuePane = new PriorityQueueVisualPane();
        priorityQueuePane.update(m);
        return priorityQueuePane;
    }

    /**
     * Releases all cached pane references.
     */
    public void reset() {
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
        fixedArrayPane = null;
        dynamicArrayPane = null;
    }
}
