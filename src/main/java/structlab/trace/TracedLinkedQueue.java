package structlab.trace;

import structlab.core.queue.LinkedQueue;

/**
 * Traced wrapper around {@link LinkedQueue}.  Each operation captures a
 * {@link TraceStep} with before/after snapshots.  Failed operations (e.g.
 * dequeuing from an empty queue) are also traced before re-throwing.
 */
public class TracedLinkedQueue<T> {
  private final LinkedQueue<T> queue;
  private final TraceLog log;

  public TracedLinkedQueue(LinkedQueue<T> queue, TraceLog log) {
    this.queue = queue;
    this.log = log;
  }

  public void enqueue(T value) {
    String before = queue.snapshot();
    boolean wasEmpty = queue.isEmpty();

    queue.enqueue(value);

    String after = queue.snapshot();
    String explanation = wasEmpty
        ? "Enqueued " + value + ". Queue was empty, so the new node is both front and rear."
        : "Enqueued " + value + " at the rear. Previous rear now links to the new node.";

    log.add(new TraceStep(
        queue.structureName(), queue.implementationName(), "enqueue",
        String.valueOf(value), before, after,
        InvariantResult.fromBoolean(queue.checkInvariant()),
        "O(1)", explanation));
  }

  public T dequeue() {
    String before = queue.snapshot();

    if (queue.isEmpty()) {
      log.add(new TraceStep(
          queue.structureName(), queue.implementationName(), "dequeue",
          null, before, before,
          InvariantResult.fromBoolean(queue.checkInvariant()),
          "O(1)", "FAILED: Cannot dequeue from an empty queue."));
      queue.dequeue(); // let it throw
    }

    boolean willBecomeEmpty = queue.size() == 1;
    T value = queue.dequeue();

    String after = queue.snapshot();
    String explanation = willBecomeEmpty
        ? "Dequeued " + value + " from the front. Queue is now empty; front and rear are null."
        : "Dequeued " + value + " from the front. Front pointer advanced to the next node.";

    log.add(new TraceStep(
        queue.structureName(), queue.implementationName(), "dequeue",
        null, before, after,
        InvariantResult.fromBoolean(queue.checkInvariant()),
        "O(1)", explanation));

    return value;
  }

  public T peek() {
    String before = queue.snapshot();

    if (queue.isEmpty()) {
      log.add(new TraceStep(
          queue.structureName(), queue.implementationName(), "peek",
          null, before, before,
          InvariantResult.fromBoolean(queue.checkInvariant()),
          "O(1)", "FAILED: Cannot peek into an empty queue."));
      queue.peek(); // let it throw
    }

    T value = queue.peek();

    log.add(new TraceStep(
        queue.structureName(), queue.implementationName(), "peek",
        null, before, before,
        InvariantResult.fromBoolean(queue.checkInvariant()),
        "O(1)",
        "Peeked at front of queue: " + value + ". No structural change."));

    return value;
  }

  public LinkedQueue<T> unwrap() {
    return queue;
  }

  public TraceLog traceLog() {
    return log;
  }
}
