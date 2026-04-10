package structlab.gui.visual;

import java.util.List;

/**
 * View model for ArrayDequeCustom state.
 * Captures both the logical element order and the raw backing-array layout
 * to visualize circular-buffer behaviour.
 *
 * @param logical    elements in logical (front-to-rear) order
 * @param raw        backing array slots (may contain null holes)
 * @param size       number of elements
 * @param capacity   backing array length
 * @param frontIndex physical index of the front element
 */
public record ArrayDequeStateModel(
        List<String> logical,
        List<String> raw,
        int size,
        int capacity,
        int frontIndex
) {

    public boolean isEmpty() {
        return size == 0;
    }

    /** Physical index of the element just past the rear. */
    public int rearIndex() {
        if (capacity == 0) return 0;
        return (frontIndex + size) % capacity;
    }
}
