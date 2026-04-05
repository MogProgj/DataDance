package structlab.demo;

import structlab.core.stack.LinkedStack;
import structlab.trace.TraceLog;
import structlab.trace.TracedLinkedStack;

public class TracedLinkedStackDemo {
  public static void main(String[] args) {
    LinkedStack<Integer> stack = new LinkedStack<>();
    TraceLog log = new TraceLog();
    TracedLinkedStack<Integer> traced = new TracedLinkedStack<>(stack, log);

    System.out.println("=== Traced LinkedStack Demo ===\n");

    traced.push(10);
    traced.push(20);
    traced.push(30);
    traced.peek();
    traced.pop();
    traced.pop();
    traced.pop();

    // Demonstrate failure tracing: stack is now empty
    try {
      traced.pop();
    } catch (IllegalStateException e) {
      // failure was traced before the exception
    }

    System.out.println(log.formatAll());
  }
}
