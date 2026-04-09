package structlab.demo;

import structlab.core.deque.ArrayDequeCustom;
import structlab.render.ConsoleTraceRenderer;
import structlab.trace.TraceLog;
import structlab.trace.TracedArrayDequeCustom;

public class TracedArrayDequeDemo {
  public static void main(String[] args) {
    ArrayDequeCustom<Integer> deque = new ArrayDequeCustom<>();
    TraceLog log = new TraceLog();
    TracedArrayDequeCustom<Integer> traced = new TracedArrayDequeCustom<>(deque, log);

    System.out.println("=== Traced ArrayDequeCustom Demo ===\n");

    traced.addLast(10);
    traced.addLast(20);
    traced.addLast(30);
    traced.addFirst(5);    // may trigger wraparound
    traced.addFirst(1);    // may trigger resize
    traced.peekFirst();
    traced.peekLast();
    traced.removeFirst();
    traced.removeLast();

    System.out.println(ConsoleTraceRenderer.renderAll(log));
  }
}
