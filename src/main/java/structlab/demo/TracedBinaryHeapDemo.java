package structlab.demo;

import structlab.core.heap.BinaryHeap;
import structlab.render.ConsoleTraceRenderer;
import structlab.trace.TraceLog;
import structlab.trace.TracedBinaryHeap;

public class TracedBinaryHeapDemo {
  public static void main(String[] args) {
    BinaryHeap<Integer> heap = new BinaryHeap<>();
    TraceLog log = new TraceLog();
    TracedBinaryHeap<Integer> traced = new TracedBinaryHeap<>(heap, log);

    System.out.println("=== Traced BinaryHeap Demo ===\n");

    traced.insert(5);
    traced.insert(3);
    traced.insert(7);
    traced.insert(1);   // new min, bubbles up to root
    traced.insert(4);
    traced.peek();
    traced.extractMin(); // sift-down to restore heap
    traced.extractMin();

    System.out.println(ConsoleTraceRenderer.renderAll(log));
  }
}
