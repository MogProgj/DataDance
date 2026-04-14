package structlab.gui.visual;

import java.util.List;

/**
 * View model for hash tables using separate chaining.
 * Parsed from HashTableChaining snapshots.
 */
public record HashChainingStateModel(
        int size,
        int capacity,
        String hashType,
        int maxChainSize,
        int rehashes,
        List<Bucket> buckets
) implements VisualState {
    /**
     * A single bucket in the hash table.
     */
    public record Bucket(int index, List<Entry> entries) {
        public boolean isEmpty() { return entries.isEmpty(); }
        public int chainLength() { return entries.size(); }
    }

    /**
     * A key-value entry in a chaining bucket.
     */
    public record Entry(String key, String value) {}

    public boolean isEmpty() { return size == 0; }

    public double loadFactor() {
        return capacity == 0 ? 0.0 : (double) size / capacity;
    }

    public long occupiedCount() {
        return buckets.stream().filter(b -> !b.isEmpty()).count();
    }

    public long collisionBuckets() {
        return buckets.stream().filter(b -> b.chainLength() > 1).count();
    }
}
