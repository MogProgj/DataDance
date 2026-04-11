package structlab.gui.visual;

import java.util.List;

/**
 * View model for hash tables using open addressing (linear, quadratic, double-hashing).
 * Parsed from HashTableOpenAddressing snapshots.
 */
public record HashOpenAddressingStateModel(
        int size,
        int capacity,
        String oaType,
        String hashType,
        int rehashes,
        List<Slot> slots
) implements VisualState {
    public enum SlotState { EMPTY, OCCUPIED, DELETED }

    /**
     * A single slot in the open-addressing table.
     */
    public record Slot(int index, SlotState state, String key, String value) {
        public boolean isOccupied() { return state == SlotState.OCCUPIED; }
        public boolean isEmpty() { return state == SlotState.EMPTY; }
        public boolean isDeleted() { return state == SlotState.DELETED; }
    }

    public boolean isEmpty() { return size == 0; }

    public double loadFactor() {
        return capacity == 0 ? 0.0 : (double) size / capacity;
    }

    public long occupiedCount() {
        return slots.stream().filter(Slot::isOccupied).count();
    }

    public long deletedCount() {
        return slots.stream().filter(Slot::isDeleted).count();
    }

    public long emptyCount() {
        return slots.stream().filter(Slot::isEmpty).count();
    }

    /**
     * Returns the longest contiguous run of occupied slots (cluster length).
     */
    public int maxClusterSize() {
        int max = 0;
        int current = 0;
        for (Slot s : slots) {
            if (s.isOccupied()) {
                current++;
                max = Math.max(max, current);
            } else {
                current = 0;
            }
        }
        return max;
    }
}
