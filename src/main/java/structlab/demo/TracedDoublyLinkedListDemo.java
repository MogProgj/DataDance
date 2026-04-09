package structlab.demo;

import structlab.core.list.DoublyLinkedList;
import structlab.render.ConsoleTraceRenderer;
import structlab.trace.TraceLog;
import structlab.trace.TracedDoublyLinkedList;

public class TracedDoublyLinkedListDemo {
  public static void main(String[] args) {
    DoublyLinkedList<Integer> list = new DoublyLinkedList<>();
    TraceLog log = new TraceLog();
    TracedDoublyLinkedList<Integer> traced = new TracedDoublyLinkedList<>(list, log);

    System.out.println("=== Traced DoublyLinkedList Demo ===\n");

    traced.addFirst(20);
    traced.addLast(30);
    traced.addFirst(10);
    traced.getFirst();
    traced.getLast();
    traced.removeLast();
    traced.removeFirst();

    System.out.println(ConsoleTraceRenderer.renderAll(log));
  }
}
