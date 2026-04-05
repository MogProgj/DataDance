package structlab.trace;

import structlab.core.array.DynamicArray;

/**
 * Traced wrapper around {@link DynamicArray}.  Each mutating operation
 * captures a {@link TraceStep} with before/after snapshots and appends it
 * to the provided {@link TraceLog}.
 */
public class TracedDynamicArray<T> {
  private final DynamicArray<T> array;
  private final TraceLog log;

  public TracedDynamicArray(DynamicArray<T> array, TraceLog log) {
    this.array = array;
    this.log = log;
  }

  public void append(T value) {
    String before = array.snapshot();
    boolean resized = array.size() == array.capacity();

    array.append(value);

    String after = array.snapshot();
    String complexity = resized ? "O(n) - resize triggered" : "O(1) amortised";
    String explanation = resized
        ? "Appended " + value + ". Array was full, so it doubled capacity before inserting."
        : "Appended " + value + " into the next open slot.";

    log.add(new TraceStep(
        array.structureName(), array.implementationName(), "append",
        String.valueOf(value), before, after,
        InvariantResult.fromBoolean(array.checkInvariant()),
        complexity, explanation));
  }

  public void insert(int index, T value) {
    String before = array.snapshot();
    boolean resized = array.size() == array.capacity();

    array.insert(index, value);

    String after = array.snapshot();
    String complexity = resized ? "O(n) - shift + resize" : "O(n) - shift elements right";
    String explanation = "Inserted " + value + " at index " + index
        + ". Elements at index " + index + "..end were shifted right.";

    log.add(new TraceStep(
        array.structureName(), array.implementationName(), "insert",
        "index=" + index + ", value=" + value, before, after,
        InvariantResult.fromBoolean(array.checkInvariant()),
        complexity, explanation));
  }

  public T removeAt(int index) {
    String before = array.snapshot();

    T removed = array.removeAt(index);

    String after = array.snapshot();
    String explanation = "Removed " + removed + " from index " + index
        + ". Elements were shifted left to fill the gap.";

    log.add(new TraceStep(
        array.structureName(), array.implementationName(), "removeAt",
        "index=" + index, before, after,
        InvariantResult.fromBoolean(array.checkInvariant()),
        "O(n) - shift elements left", explanation));

    return removed;
  }

  public T get(int index) {
    String before = array.snapshot();

    T value = array.get(index);

    log.add(new TraceStep(
        array.structureName(), array.implementationName(), "get",
        "index=" + index, before, before,
        InvariantResult.fromBoolean(array.checkInvariant()),
        "O(1)", "Retrieved " + value + " at index " + index + ". No structural change."));

    return value;
  }

  public DynamicArray<T> unwrap() {
    return array;
  }

  public TraceLog traceLog() {
    return log;
  }
}
