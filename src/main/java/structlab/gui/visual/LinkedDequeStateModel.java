package structlab.gui.visual;

import java.util.List;

/**
 * View model for LinkedDeque state.
 * Chain is front-to-rear with bidirectional links.
 *
 * @param nodes list of node values from front to rear
 * @param size  number of elements
 * @param front display string for front node, or "null" if empty
 * @param rear  display string for rear node, or "null" if empty
 */
public record LinkedDequeStateModel(List<String> nodes, int size, String front, String rear) {

    public boolean isEmpty() {
        return size == 0;
    }
}
