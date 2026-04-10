package structlab.gui.visual;

import java.util.List;

/**
 * View model for queue state — works for LinkedQueue, TwoStackQueue,
 * and other queue implementations that present standard queue semantics.
 *
 * @param elements queue elements from front to rear
 * @param size     number of elements
 * @param front    display string for the front element, or "null" if empty
 * @param rear     display string for the rear element, or "null" if empty
 */
public record QueueStateModel(List<String> elements, int size, String front, String rear) {

    public boolean isEmpty() {
        return size == 0;
    }
}
