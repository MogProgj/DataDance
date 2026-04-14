package structlab.trace;

import structlab.core.hash.HashTableOpenAddressing;

/**
 * Traced wrapper around {@link HashTableOpenAddressing}.  Each operation captures a
 * {@link TraceStep} with before/after snapshots.  Non-mutating operations
 * (get, containsKey) record identical before/after state.
 */
public class TracedHashTableOpenAddressing<K, V> {
    private final HashTableOpenAddressing<K, V> table;
    private final TraceLog log;

    public TracedHashTableOpenAddressing(HashTableOpenAddressing<K, V> table, TraceLog log) {
        this.table = table;
        this.log = log;
    }

    public V put(K key, V value) {
        String before = table.snapshot();
        boolean existed = table.containsKey(key);

        V old = table.put(key, value);

        String after = table.snapshot();
        String explanation;
        if (existed) {
            explanation = "Updated key " + key + " in slot. Old value " + old + " replaced with " + value + ".";
        } else {
            explanation = "Inserted (" + key + " -> " + value + ") using " + table.getOpenAddressingType()
                    + " probing. Slot found via open addressing.";
        }

        log.add(new TraceStep(
                table.structureName(), table.implementationName(), "put",
                key + ", " + value, before, after,
                InvariantResult.fromBoolean(table.checkInvariant()),
                "O(1) avg", explanation));

        return old;
    }

    public V get(K key) {
        String before = table.snapshot();

        V value = table.get(key);

        String explanation;
        if (value != null) {
            explanation = "Found key " + key + " with value " + value + ". Probed using " + table.getOpenAddressingType() + ".";
        } else {
            explanation = "Key " + key + " not found. Probed slots using " + table.getOpenAddressingType() + ", hit empty slot.";
        }

        log.add(new TraceStep(
                table.structureName(), table.implementationName(), "get",
                String.valueOf(key), before, before,
                InvariantResult.fromBoolean(table.checkInvariant()),
                "O(1) avg", explanation));

        return value;
    }

    public V remove(K key) {
        String before = table.snapshot();
        boolean existed = table.containsKey(key);

        V removed = table.remove(key);

        String after = table.snapshot();
        String explanation;
        if (existed) {
            explanation = "Removed key " + key + " (value " + removed + "). Slot marked as DELETED for probe chain continuity.";
        } else {
            explanation = "Key " + key + " not found. Nothing removed.";
        }

        log.add(new TraceStep(
                table.structureName(), table.implementationName(), "remove",
                String.valueOf(key), before, after,
                InvariantResult.fromBoolean(table.checkInvariant()),
                "O(1) avg", explanation));

        return removed;
    }

    public boolean containsKey(K key) {
        String before = table.snapshot();

        boolean found = table.containsKey(key);

        log.add(new TraceStep(
                table.structureName(), table.implementationName(), "containsKey",
                String.valueOf(key), before, before,
                InvariantResult.fromBoolean(table.checkInvariant()),
                "O(1) avg",
                "Checked for key " + key + ": " + (found ? "found" : "not found") + ". Probed using " + table.getOpenAddressingType() + "."));

        return found;
    }

    public HashTableOpenAddressing<K, V> unwrap() {
        return table;
    }

    public TraceLog traceLog() {
        return log;
    }
}
