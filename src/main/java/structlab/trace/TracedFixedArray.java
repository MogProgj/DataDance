package structlab.trace;

import structlab.core.array.FixedArray;

/**
 * Traced wrapper around {@link FixedArray}.  Each operation captures a
 * {@link TraceStep} with before/after snapshots.  Failed operations (e.g.
 * appending to a full array) are also traced before re-throwing.
 */
public class TracedFixedArray<T> {
  private final FixedArray<T> array;
  private final TraceLog log;

  public TracedFixedArray(FixedArray<T> array, TraceLog log) {
    this.array = array;
    this.log = log;
  }

  public void append(T value) {
    String before = array.snapshot();

    if (array.isFull()) {
      log.add(new TraceStep(
          array.structureName(), array.implementationName(), "append",
          String.valueOf(value), before, before,
          InvariantResult.fromBoolean(array.checkInvariant()),
          "O(1)", "FAILED: Cannot append " + value
              + " because the array is full (capacity=" + array.capacity() + ")."));
      array.append(value); // let it throw
    }

    array.append(value);

    String after = array.snapshot();
    log.add(new TraceStep(
        array.structureName(), array.implementationName(), "append",
        String.valueOf(value), before, after,
        InvariantResult.fromBoolean(array.checkInvariant()),
        "O(1)",
        "Appended " + value + " into slot " + (array.size() - 1)
            + ". " + array.size() + "/" + array.capacity() + " slots used."));
  }

  public void insert(int index, T value) {
    String before = array.snapshot();

    if (array.isFull()) {
      log.add(new TraceStep(
          array.structureName(), array.implementationName(), "insert",
          "index=" + index + ", value=" + value, before, before,
          InvariantResult.fromBoolean(array.checkInvariant()),
          "O(n)", "FAILED: Cannot insert " + value
              + " because the array is full (capacity=" + array.capacity() + ")."));
      array.insert(index, value); // let it throw
    }

    array.insert(index, value);

    String after = array.snapshot();
    log.add(new TraceStep(
        array.structureName(), array.implementationName(), "insert",
        "index=" + index + ", value=" + value, before, after,
        InvariantResult.fromBoolean(array.checkInvariant()),
        "O(n) - shift elements right",
        "Inserted " + value + " at index " + index
            + ". Elements at index " + index + "..end were shifted right."
            + " " + array.size() + "/" + array.capacity() + " slots used."));
  }

  public T removeAt(int index) {
    String before = array.snapshot();

    T removed = array.removeAt(index);

    String after = array.snapshot();
    log.add(new TraceStep(
        array.structureName(), array.implementationName(), "removeAt",
        "index=" + index, before, after,
        InvariantResult.fromBoolean(array.checkInvariant()),
        "O(n) - shift elements left",
        "Removed " + removed + " from index " + index
            + ". Elements were shifted left to fill the gap."
            + " " + array.size() + "/" + array.capacity() + " slots used."));

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

  public FixedArray<T> unwrap() {
    return array;
  }

  public TraceLog traceLog() {
    return log;
  }
}
