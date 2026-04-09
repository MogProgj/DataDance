package structlab.demo;

import structlab.core.list.SinglyLinkedList;
import structlab.render.ConsoleTraceRenderer;
import structlab.trace.TraceLog;
import structlab.trace.TracedSinglyLinkedList;

public class TracedSinglyLinkedListDemo {
  public static void main(String[] args) {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
    TraceLog log = new TraceLog();
    TracedSinglyLinkedList<Integer> traced = new TracedSinglyLinkedList<>(list, log);

    System.out.println("=== Traced SinglyLinkedList Demo ===\n");

    traced.addFirst(20);
    traced.addFirst(10);
    traced.addLast(30);
    traced.getFirst();
    traced.getLast();
    traced.contains(20);
    traced.contains(99);
    traced.removeFirst();

    System.out.println(ConsoleTraceRenderer.renderAll(log));
  }
}
