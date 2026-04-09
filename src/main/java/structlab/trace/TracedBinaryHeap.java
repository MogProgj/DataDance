package structlab.trace;

import structlab.core.heap.BinaryHeap;

/**
 * Traced wrapper around {@link BinaryHeap}.  Each operation captures a
 * {@link TraceStep} with before/after snapshots.  Failed operations (e.g.
 * extracting from an empty heap) are also traced before re-throwing.
 */
public class TracedBinaryHeap<T extends Comparable<T>> {
  private final BinaryHeap<T> heap;
  private final TraceLog log;

  public TracedBinaryHeap(BinaryHeap<T> heap, TraceLog log) {
    this.heap = heap;
    this.log = log;
  }

  public void insert(T value) {
    String before = heap.snapshot();
    boolean wasEmpty = heap.isEmpty();

    heap.insert(value);

    String after = heap.snapshot();
    boolean isNewMin = heap.peek().equals(value);

    String explanation;
    if (wasEmpty || isNewMin) {
      explanation = "Inserted " + value + " as new minimum.";
    } else {
      explanation = "Inserted " + value + " into heap. Element bubbled up to maintain heap property.";
    }

    log.add(new TraceStep(
        heap.structureName(), heap.implementationName(), "insert",
        String.valueOf(value), before, after,
        InvariantResult.fromBoolean(heap.checkInvariant()),
        "O(log n)", explanation));
  }

  public T peek() {
    String before = heap.snapshot();

    if (heap.isEmpty()) {
      log.add(new TraceStep(
          heap.structureName(), heap.implementationName(), "peek",
          null, before, before,
          InvariantResult.fromBoolean(heap.checkInvariant()),
          "O(1)", "FAILED: Cannot peek into an empty heap."));
      heap.peek(); // let it throw
    }

    T value = heap.peek();

    log.add(new TraceStep(
        heap.structureName(), heap.implementationName(), "peek",
        null, before, before,
        InvariantResult.fromBoolean(heap.checkInvariant()),
        "O(1)",
        "Peeked at minimum: " + value + ". No structural change."));

    return value;
  }

  public T extractMin() {
    String before = heap.snapshot();

    if (heap.isEmpty()) {
      log.add(new TraceStep(
          heap.structureName(), heap.implementationName(), "extractMin",
          null, before, before,
          InvariantResult.fromBoolean(heap.checkInvariant()),
          "O(log n)", "FAILED: Cannot extractMin from an empty heap."));
      heap.extractMin(); // let it throw
    }

    T value = heap.extractMin();

    String after = heap.snapshot();
    String explanation = "Extracted minimum " + value
        + ". Last element moved to root and sifted down to restore heap property.";

    log.add(new TraceStep(
        heap.structureName(), heap.implementationName(), "extractMin",
        null, before, after,
        InvariantResult.fromBoolean(heap.checkInvariant()),
        "O(log n)", explanation));

    return value;
  }

  public BinaryHeap<T> unwrap() {
    return heap;
  }

  public TraceLog traceLog() {
    return log;
  }
}
