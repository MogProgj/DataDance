package structlab.trace;

import structlab.core.deque.LinkedDeque;

/**
 * Traced wrapper around {@link LinkedDeque}.  Each operation captures a
 * {@link TraceStep} with before/after snapshots.  Failed operations (e.g.
 * removing from an empty deque) are also traced before re-throwing.
 */
public class TracedLinkedDeque<T> {
  private final LinkedDeque<T> deque;
  private final TraceLog log;

  public TracedLinkedDeque(LinkedDeque<T> deque, TraceLog log) {
    this.deque = deque;
    this.log = log;
  }

  public void addFirst(T value) {
    String before = deque.snapshot();
    boolean wasEmpty = deque.isEmpty();

    deque.addFirst(value);

    String after = deque.snapshot();
    String explanation = wasEmpty
        ? "Added " + value + ". Deque was empty, so " + value + " is both front and rear."
        : "Added " + value + " at front. New node linked before previous front.";

    log.add(new TraceStep(
        deque.structureName(), deque.implementationName(), "addFirst",
        String.valueOf(value), before, after,
        InvariantResult.fromBoolean(deque.checkInvariant()),
        "O(1)", explanation));
  }

  public void addLast(T value) {
    String before = deque.snapshot();
    boolean wasEmpty = deque.isEmpty();

    deque.addLast(value);

    String after = deque.snapshot();
    String explanation = wasEmpty
        ? "Added " + value + ". Deque was empty, so " + value + " is both front and rear."
        : "Added " + value + " at rear. New node linked after previous rear.";

    log.add(new TraceStep(
        deque.structureName(), deque.implementationName(), "addLast",
        String.valueOf(value), before, after,
        InvariantResult.fromBoolean(deque.checkInvariant()),
        "O(1)", explanation));
  }

  public T removeFirst() {
    String before = deque.snapshot();

    if (deque.isEmpty()) {
      log.add(new TraceStep(
          deque.structureName(), deque.implementationName(), "removeFirst",
          null, before, before,
          InvariantResult.fromBoolean(deque.checkInvariant()),
          "O(1)", "FAILED: Cannot removeFirst from an empty deque."));
      deque.removeFirst(); // let it throw
    }

    boolean willBecomeEmpty = deque.size() == 1;
    T value = deque.removeFirst();

    String after = deque.snapshot();
    String explanation = willBecomeEmpty
        ? "Removed " + value + " from front. Deque is now empty."
        : "Removed " + value + " from front. Front advanced to next node.";

    log.add(new TraceStep(
        deque.structureName(), deque.implementationName(), "removeFirst",
        null, before, after,
        InvariantResult.fromBoolean(deque.checkInvariant()),
        "O(1)", explanation));

    return value;
  }

  public T removeLast() {
    String before = deque.snapshot();

    if (deque.isEmpty()) {
      log.add(new TraceStep(
          deque.structureName(), deque.implementationName(), "removeLast",
          null, before, before,
          InvariantResult.fromBoolean(deque.checkInvariant()),
          "O(1)", "FAILED: Cannot removeLast from an empty deque."));
      deque.removeLast(); // let it throw
    }

    boolean willBecomeEmpty = deque.size() == 1;
    T value = deque.removeLast();

    String after = deque.snapshot();
    String explanation = willBecomeEmpty
        ? "Removed " + value + " from rear. Deque is now empty."
        : "Removed " + value + " from rear. Rear moved to previous node.";

    log.add(new TraceStep(
        deque.structureName(), deque.implementationName(), "removeLast",
        null, before, after,
        InvariantResult.fromBoolean(deque.checkInvariant()),
        "O(1)", explanation));

    return value;
  }

  public T peekFirst() {
    String before = deque.snapshot();

    if (deque.isEmpty()) {
      log.add(new TraceStep(
          deque.structureName(), deque.implementationName(), "peekFirst",
          null, before, before,
          InvariantResult.fromBoolean(deque.checkInvariant()),
          "O(1)", "FAILED: Cannot peekFirst into an empty deque."));
      deque.peekFirst(); // let it throw
    }

    T value = deque.peekFirst();

    log.add(new TraceStep(
        deque.structureName(), deque.implementationName(), "peekFirst",
        null, before, before,
        InvariantResult.fromBoolean(deque.checkInvariant()),
        "O(1)",
        "Peeked at front of deque: " + value + ". No structural change."));

    return value;
  }

  public T peekLast() {
    String before = deque.snapshot();

    if (deque.isEmpty()) {
      log.add(new TraceStep(
          deque.structureName(), deque.implementationName(), "peekLast",
          null, before, before,
          InvariantResult.fromBoolean(deque.checkInvariant()),
          "O(1)", "FAILED: Cannot peekLast into an empty deque."));
      deque.peekLast(); // let it throw
    }

    T value = deque.peekLast();

    log.add(new TraceStep(
        deque.structureName(), deque.implementationName(), "peekLast",
        null, before, before,
        InvariantResult.fromBoolean(deque.checkInvariant()),
        "O(1)",
        "Peeked at rear of deque: " + value + ". No structural change."));

    return value;
  }

  public LinkedDeque<T> unwrap() {
    return deque;
  }

  public TraceLog traceLog() {
    return log;
  }
}
