package structlab.gui.visual;

import java.util.List;

/**
 * View model for FixedArray state.
 *
 * @param elements occupied element values (index 0..size-1)
 * @param raw      full backing array including null slots
 * @param size     number of occupied slots
 * @param capacity total fixed capacity
 */
public record FixedArrayStateModel(List<String> elements, List<String> raw, int size, int capacity) implements VisualState {

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }
}
