package structlab.trace;

import structlab.core.stack.ArrayStack;

/**
 * Traced wrapper around {@link ArrayStack}.  Each operation captures a
 * {@link TraceStep} with before/after snapshots.
 */
public class TracedArrayStack<T> {
  private final ArrayStack<T> stack;
  private final TraceLog log;

  public TracedArrayStack(ArrayStack<T> stack, TraceLog log) {
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
        "O(1) amortised",
        "Pushed " + value + " onto the top of the stack."));
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
        "Popped " + value + " from the top of the stack."));

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

  public ArrayStack<T> unwrap() {
    return stack;
  }

  public TraceLog traceLog() {
    return log;
  }
}
