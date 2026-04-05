package structlab.trace;

import structlab.core.stack.LinkedStack;

/**
 * Traced wrapper around {@link LinkedStack}.  Each operation captures a
 * {@link TraceStep} with before/after snapshots.  Failed operations (e.g.
 * popping from an empty stack) are also traced before re-throwing.
 */
public class TracedLinkedStack<T> {
  private final LinkedStack<T> stack;
  private final TraceLog log;

  public TracedLinkedStack(LinkedStack<T> stack, TraceLog log) {
    this.stack = stack;
    this.log = log;
  }

  public void push(T value) {
    String before = stack.snapshot();

    stack.push(value);

    String after = stack.snapshot();
    log.add(new TraceStep(
        stack.structureName(), stack.implementationName(), "push",
        String.valueOf(value), before, after,
        InvariantResult.fromBoolean(stack.checkInvariant()),
        "O(1)",
        "Pushed " + value + " onto the top. New node points to previous top."));
  }

  public T pop() {
    String before = stack.snapshot();

    if (stack.isEmpty()) {
      log.add(new TraceStep(
          stack.structureName(), stack.implementationName(), "pop",
          null, before, before,
          InvariantResult.fromBoolean(stack.checkInvariant()),
          "O(1)", "FAILED: Cannot pop from an empty stack."));
      stack.pop(); // let it throw
    }

    T value = stack.pop();

    String after = stack.snapshot();
    log.add(new TraceStep(
        stack.structureName(), stack.implementationName(), "pop",
        null, before, after,
        InvariantResult.fromBoolean(stack.checkInvariant()),
        "O(1)",
        "Popped " + value + " from the top. Head now points to the next node."));

    return value;
  }

  public T peek() {
    String before = stack.snapshot();

    if (stack.isEmpty()) {
      log.add(new TraceStep(
          stack.structureName(), stack.implementationName(), "peek",
          null, before, before,
          InvariantResult.fromBoolean(stack.checkInvariant()),
          "O(1)", "FAILED: Cannot peek into an empty stack."));
      stack.peek(); // let it throw
    }

    T value = stack.peek();

    log.add(new TraceStep(
        stack.structureName(), stack.implementationName(), "peek",
        null, before, before,
        InvariantResult.fromBoolean(stack.checkInvariant()),
        "O(1)",
        "Peeked at top of stack: " + value + ". No structural change."));

    return value;
  }

  public LinkedStack<T> unwrap() {
    return stack;
  }

  public TraceLog traceLog() {
    return log;
  }
}
