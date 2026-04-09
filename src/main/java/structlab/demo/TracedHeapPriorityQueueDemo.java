package structlab.demo;

import structlab.core.heap.HeapPriorityQueue;
import structlab.render.ConsoleTraceRenderer;
import structlab.trace.TraceLog;
import structlab.trace.TracedHeapPriorityQueue;

public class TracedHeapPriorityQueueDemo {
  public static void main(String[] args) {
    HeapPriorityQueue<Integer> pq = new HeapPriorityQueue<>();
    TraceLog log = new TraceLog();
    TracedHeapPriorityQueue<Integer> traced = new TracedHeapPriorityQueue<>(pq, log);

    System.out.println("=== Traced HeapPriorityQueue Demo ===\n");

    traced.enqueue(5);
    traced.enqueue(3);
    traced.enqueue(7);
    traced.enqueue(1);   // new highest-priority element
    traced.peek();
    traced.dequeue();    // removes highest priority
    traced.dequeue();

    System.out.println(ConsoleTraceRenderer.renderAll(log));
  }
}
