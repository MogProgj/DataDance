package structlab.trace;

import structlab.core.queue.CircularArrayQueue;

/**
 * Traced wrapper around {@link CircularArrayQueue}.  Each operation captures
 * a {@link TraceStep} with before/after snapshots.
 */
public class TracedCircularArrayQueue<T> {
  private final CircularArrayQueue<T> queue;
  private final TraceLog log;

  public TracedCircularArrayQueue(CircularArrayQueue<T> queue, TraceLog log) {
    this.queue = queue;
    this.log = log;
  }

  public void enqueue(T value) {
    String before = queue.snapshot();
    boolean resized = queue.size() == queue.capacity();

    queue.enqueue(value);

    String after = queue.snapshot();
    String complexity = resized ? "O(n) - resize triggered" : "O(1) amortised";
    String explanation = resized
        ? "Enqueued " + value + ". Queue was full, so it doubled capacity and unwrapped the circular layout."
        : "Enqueued " + value + " at the rear of the queue.";

    log.add(new TraceStep(
        queue.structureName(), queue.implementationName(), "enqueue",
        String.valueOf(value), before, after,
        InvariantResult.fromBoolean(queue.checkInvariant()),
        complexity, explanation));
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

    T value = queue.dequeue();

    String after = queue.snapshot();

    log.add(new TraceStep(
        queue.structureName(), queue.implementationName(), "dequeue",
        null, before, after,
        InvariantResult.fromBoolean(queue.checkInvariant()),
        "O(1)",
        "Dequeued " + value + " from the front. Front pointer advanced circularly."));

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

  public CircularArrayQueue<T> unwrap() {
    return queue;
  }

  public TraceLog traceLog() {
    return log;
  }
}
