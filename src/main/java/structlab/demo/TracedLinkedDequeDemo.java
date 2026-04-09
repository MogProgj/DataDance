package structlab.demo;

import structlab.core.deque.LinkedDeque;
import structlab.render.ConsoleTraceRenderer;
import structlab.trace.TraceLog;
import structlab.trace.TracedLinkedDeque;

public class TracedLinkedDequeDemo {
  public static void main(String[] args) {
    LinkedDeque<Integer> deque = new LinkedDeque<>();
    TraceLog log = new TraceLog();
    TracedLinkedDeque<Integer> traced = new TracedLinkedDeque<>(deque, log);

    System.out.println("=== Traced LinkedDeque Demo ===\n");

    traced.addFirst(20);
    traced.addLast(30);
    traced.addFirst(10);
    traced.peekFirst();
    traced.peekLast();
    traced.removeLast();
    traced.removeFirst();

    System.out.println(ConsoleTraceRenderer.renderAll(log));
  }
}
