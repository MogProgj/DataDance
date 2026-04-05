package structlab.trace;

import structlab.core.queue.TwoStackQueue;

/**
 * Traced wrapper around {@link TwoStackQueue}.  Each operation captures a
 * {@link TraceStep} with before/after snapshots.  When a dequeue or peek
 * triggers an inbox-to-outbox transfer, the explanation highlights this.
 * Failed operations are also traced before re-throwing.
 */
public class TracedTwoStackQueue<T> {
  private final TwoStackQueue<T> queue;
  private final TraceLog log;

  public TracedTwoStackQueue(TwoStackQueue<T> queue, TraceLog log) {
    this.queue = queue;
    this.log = log;
  }

  public void enqueue(T value) {
    String before = queue.snapshot();

    queue.enqueue(value);

    String after = queue.snapshot();
    log.add(new TraceStep(
        queue.structureName(), queue.implementationName(), "enqueue",
        String.valueOf(value), before, after,
        InvariantResult.fromBoolean(queue.checkInvariant()),
        "O(1)",
        "Enqueued " + value + " by pushing onto the inbox stack."));
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

    // Detect whether a transfer will happen by checking if the snapshot
    // changes beyond just the dequeued element.  We can detect transfer
    // by looking at whether outbox portion of the snapshot is empty-ish
    // before the call.  A simpler heuristic: snapshot before contains
    // an empty outbox pattern.
    boolean transferExpected = before.contains("outbox=ArrayStack{size=0");

    T value = queue.dequeue();

    String after = queue.snapshot();
    String complexity = transferExpected ? "O(n) amortised - transfer triggered" : "O(1) amortised";
    String explanation = transferExpected
        ? "Dequeued " + value + ". Outbox was empty, so all inbox items were transferred "
            + "(popped from inbox, pushed to outbox) to restore FIFO order, then popped from outbox."
        : "Dequeued " + value + " by popping from the outbox stack.";

    log.add(new TraceStep(
        queue.structureName(), queue.implementationName(), "dequeue",
        null, before, after,
        InvariantResult.fromBoolean(queue.checkInvariant()),
        complexity, explanation));

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

    boolean transferExpected = before.contains("outbox=ArrayStack{size=0");

    T value = queue.peek();

    String after = queue.snapshot();
    String complexity = transferExpected ? "O(n) amortised - transfer triggered" : "O(1)";
    String explanation = transferExpected
        ? "Peeked at " + value + ". Outbox was empty, so all inbox items were transferred "
            + "to outbox first, then peeked from outbox top."
        : "Peeked at top of outbox: " + value + ". No structural change.";

    log.add(new TraceStep(
        queue.structureName(), queue.implementationName(), "peek",
        null, before, after,
        InvariantResult.fromBoolean(queue.checkInvariant()),
        complexity, explanation));

    return value;
  }

  public TwoStackQueue<T> unwrap() {
    return queue;
  }

  public TraceLog traceLog() {
    return log;
  }
}
