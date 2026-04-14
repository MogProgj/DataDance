package structlab.trace;

import structlab.core.hash.HashSetCustom;

/**
 * Traced wrapper around {@link HashSetCustom}.  Each operation captures a
 * {@link TraceStep} with before/after snapshots.  Non-mutating operations
 * (contains) record identical before/after state.
 */
public class TracedHashSetCustom<T> {
  private final HashSetCustom<T> set;
  private final TraceLog log;

  public TracedHashSetCustom(HashSetCustom<T> set, TraceLog log) {
    this.set = set;
    this.log = log;
  }

  public boolean add(T value) {
    String before = set.snapshot();

    boolean added = set.add(value);

    String after = set.snapshot();
    String explanation;
    if (added) {
      explanation = "Added " + value + " to the set. Backed by hash table put with a sentinel value.";
    } else {
      explanation = "Value " + value + " already exists in the set. No change (set uniqueness enforced).";
    }

    log.add(new TraceStep(
        set.structureName(), set.implementationName(), "add",
        String.valueOf(value), before, after,
        InvariantResult.fromBoolean(set.checkInvariant()),
        "O(1) avg", explanation));

    return added;
  }

  public boolean contains(T value) {
    String before = set.snapshot();

    boolean found = set.contains(value);

    log.add(new TraceStep(
        set.structureName(), set.implementationName(), "contains",
        String.valueOf(value), before, before,
        InvariantResult.fromBoolean(set.checkInvariant()),
        "O(1) avg",
        "Checked for " + value + ": " + (found ? "found" : "not found") + ". Delegates to backing hash table containsKey."));

    return found;
  }

  public boolean remove(T value) {
    String before = set.snapshot();

    boolean removed = set.remove(value);

    String after = set.snapshot();
    String explanation;
    if (removed) {
      explanation = "Removed " + value + " from the set. Backing hash table entry deleted.";
    } else {
      explanation = "Value " + value + " not found in the set. Nothing removed.";
    }

    log.add(new TraceStep(
        set.structureName(), set.implementationName(), "remove",
        String.valueOf(value), before, after,
        InvariantResult.fromBoolean(set.checkInvariant()),
        "O(1) avg", explanation));

    return removed;
  }

  public HashSetCustom<T> unwrap() {
    return set;
  }

  public TraceLog traceLog() {
    return log;
  }
}
