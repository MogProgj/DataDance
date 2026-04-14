package structlab.gui.visual;

import java.util.List;

/**
 * View model for HashSetCustom — a set-semantic lens on a chaining hash table.
 * Shows membership/unique values rather than key-value mapping.
 */
public record HashSetStateModel(
        int size,
        int capacity,
        String hashType,
        int maxChainSize,
        int rehashes,
        List<SetBucket> buckets
) implements VisualState {
    /**
     * A bucket holding set members (values only, no key-value mapping).
     */
    public record SetBucket(int index, List<String> members) {
        public boolean isEmpty() { return members.isEmpty(); }
        public int memberCount() { return members.size(); }
    }

    public boolean isEmpty() { return size == 0; }

    public double loadFactor() {
        return capacity == 0 ? 0.0 : (double) size / capacity;
    }

    public long occupiedCount() {
        return buckets.stream().filter(b -> !b.isEmpty()).count();
    }
}
