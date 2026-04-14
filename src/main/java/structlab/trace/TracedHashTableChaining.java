package structlab.trace;

import structlab.core.hash.HashTableChaining;

/**
 * Traced wrapper around {@link HashTableChaining}.  Each operation captures a
 * {@link TraceStep} with before/after snapshots.  Non-mutating operations
 * (get, containsKey) record identical before/after state.  Failed operations
 * are traced before exceptions propagate.
 */
public class TracedHashTableChaining<K, V> {
  private final HashTableChaining<K, V> table;
  private final TraceLog log;

  public TracedHashTableChaining(HashTableChaining<K, V> table, TraceLog log) {
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
      explanation = "Updated key " + key + " in bucket. Old value " + old + " replaced with " + value + ".";
    } else {
      explanation = "Inserted (" + key + " -> " + value + ") into bucket. New entry added at head of chain.";
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
      explanation = "Found key " + key + " with value " + value + ". Hashed to bucket, traversed chain.";
    } else {
      explanation = "Key " + key + " not found. Hashed to bucket, chain traversal returned null.";
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
      explanation = "Removed key " + key + " (value " + removed + ") from bucket chain.";
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
        "Checked for key " + key + ": " + (found ? "found" : "not found") + ". No structural change."));

    return found;
  }

  public HashTableChaining<K, V> unwrap() {
    return table;
  }

  public TraceLog traceLog() {
    return log;
  }
}
