package structlab.gui.visual;

import java.util.List;

/**
 * View model for circular array queue state.
 *
 * @param slots      raw backing array values (includes nulls for empty slots)
 * @param logical    logical queue order (front to rear)
 * @param size       number of elements
 * @param capacity   backing array length
 * @param frontIndex physical index of the front element
 */
public record CircularQueueStateModel(
        List<String> slots,
        List<String> logical,
        int size,
        int capacity,
        int frontIndex) {

    public boolean isEmpty() {
        return size == 0;
    }

    public int rearIndex() {
        if (size == 0) return -1;
        return (frontIndex + size - 1) % capacity;
    }
}
