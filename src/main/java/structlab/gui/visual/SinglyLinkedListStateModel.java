package structlab.gui.visual;

import java.util.List;

/**
 * View model for SinglyLinkedList state.
 * Chain is head-to-tail (forward-only traversal).
 *
 * @param nodes list of node values from head to tail
 * @param size  number of elements
 * @param head  display string for head node, or "null" if empty
 * @param tail  display string for tail node, or "null" if empty
 */
public record SinglyLinkedListStateModel(List<String> nodes, int size, String head, String tail) {

    public boolean isEmpty() {
        return size == 0;
    }
}
