package structlab.gui.visual;

import java.util.List;

/**
 * View model for DynamicArray state.
 *
 * @param elements occupied element values (index 0..size-1)
 * @param raw      full backing array including unused capacity slots
 * @param size     number of occupied slots
 * @param capacity current backing array capacity (may grow)
 */
public record DynamicArrayStateModel(List<String> elements, List<String> raw, int size, int capacity) {

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean hasReservedSpace() {
        return capacity > size;
    }

    public int unusedSlots() {
        return capacity - size;
    }
}
