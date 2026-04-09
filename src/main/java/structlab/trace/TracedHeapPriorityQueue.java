package structlab.trace;

import structlab.core.heap.HeapPriorityQueue;

/**
 * Traced wrapper around {@link HeapPriorityQueue}.  Each operation captures a
 * {@link TraceStep} with before/after snapshots.  Failed operations (e.g.
 * dequeuing from an empty priority queue) are also traced before re-throwing.
 */
public class TracedHeapPriorityQueue<T extends Comparable<T>> {
  private final HeapPriorityQueue<T> queue;
  private final TraceLog log;

  public TracedHeapPriorityQueue(HeapPriorityQueue<T> queue, TraceLog log) {
    this.queue = queue;
    this.log = log;
  }

  public void enqueue(T value) {
    String before = queue.snapshot();
    boolean wasEmpty = queue.isEmpty();

    queue.enqueue(value);

    String after = queue.snapshot();
    boolean isNewFront = queue.peek().equals(value);

    String explanation;
    if (wasEmpty || isNewFront) {
      explanation = "Enqueued " + value + " as new highest-priority element.";
    } else {
      explanation = "Enqueued " + value + " with priority. Underlying heap bubbled up to maintain priority order.";
    }

    log.add(new TraceStep(
        queue.structureName(), queue.implementationName(), "enqueue",
        String.valueOf(value), before, after,
        InvariantResult.fromBoolean(queue.checkInvariant()),
        "O(log n)", explanation));
  }

  public T peek() {
    String before = queue.snapshot();

    if (queue.isEmpty()) {
      log.add(new TraceStep(
          queue.structureName(), queue.implementationName(), "peek",
          null, before, before,
          InvariantResult.fromBoolean(queue.checkInvariant()),
          "O(1)", "FAILED: Cannot peek into an empty priority queue."));
      queue.peek(); // let it throw
    }

    T value = queue.peek();

    log.add(new TraceStep(
        queue.structureName(), queue.implementationName(), "peek",
        null, before, before,
        InvariantResult.fromBoolean(queue.checkInvariant()),
        "O(1)",
        "Peeked at highest-priority element: " + value + ". No structural change."));

    return value;
  }

  public T dequeue() {
    String before = queue.snapshot();

    if (queue.isEmpty()) {
      log.add(new TraceStep(
          queue.structureName(), queue.implementationName(), "dequeue",
          null, before, before,
          InvariantResult.fromBoolean(queue.checkInvariant()),
          "O(log n)", "FAILED: Cannot dequeue from an empty priority queue."));
      queue.dequeue(); // let it throw
    }

    T value = queue.dequeue();

    String after = queue.snapshot();
    String explanation = "Dequeued highest-priority element " + value
        + ". Heap restructured via sift-down.";

    log.add(new TraceStep(
        queue.structureName(), queue.implementationName(), "dequeue",
        null, before, after,
        InvariantResult.fromBoolean(queue.checkInvariant()),
        "O(log n)", explanation));

    return value;
  }

  public HeapPriorityQueue<T> unwrap() {
    return queue;
  }

  public TraceLog traceLog() {
    return log;
  }
}
