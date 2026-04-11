package structlab.gui.visual;

/**
 * Sealed marker interface for all typed visual state models.
 *
 * <p>Every structure family produces its own state record that
 * implements this interface.  Consumers can pattern-match on
 * {@code VisualState} to dispatch family-specific rendering
 * without relying on raw snapshot string inspection.</p>
 *
 * <p>This sealed hierarchy gives compile-time exhaustiveness
 * checking on {@code switch} expressions and makes it trivial
 * to add animation hooks, comparison intelligence, or new
 * families in the future.</p>
 */
public sealed interface VisualState permits
        StackStateModel,
        QueueStateModel,
        CircularQueueStateModel,
        HeapStateModel,
        HashChainingStateModel,
        HashOpenAddressingStateModel,
        HashSetStateModel,
        SinglyLinkedListStateModel,
        DoublyLinkedListStateModel,
        ArrayDequeStateModel,
        LinkedDequeStateModel,
        FixedArrayStateModel,
        DynamicArrayStateModel {

    /**
     * Returns true if this state represents an empty structure.
     */
    boolean isEmpty();
}
