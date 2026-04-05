package structlab.demo;

import structlab.core.stack.ArrayStack;
import structlab.trace.TraceLog;
import structlab.trace.TracedArrayStack;

public class TracedArrayStackDemo {
  public static void main(String[] args) {
    ArrayStack<Integer> stack = new ArrayStack<>();
    TraceLog log = new TraceLog();
    TracedArrayStack<Integer> traced = new TracedArrayStack<>(stack, log);

    System.out.println("=== Traced ArrayStack Demo ===\n");

    traced.push(10);
    traced.push(20);
    traced.push(30);
    traced.peek();
    traced.pop();
    traced.pop();

    System.out.println(log.formatAll());
  }
}
