package structlab.demo;

import structlab.core.queue.LinkedQueue;
import structlab.render.ConsoleTraceRenderer;
import structlab.trace.TraceLog;
import structlab.trace.TracedLinkedQueue;

public class TracedLinkedQueueDemo {
  public static void main(String[] args) {
    LinkedQueue<Integer> queue = new LinkedQueue<>();
    TraceLog log = new TraceLog();
    TracedLinkedQueue<Integer> traced = new TracedLinkedQueue<>(queue, log);

    System.out.println("=== Traced LinkedQueue Demo ===\n");

    traced.enqueue(10);
    traced.enqueue(20);
    traced.enqueue(30);
    traced.peek();
    traced.dequeue();
    traced.dequeue();
    traced.dequeue();

    // Demonstrate failure tracing: queue is now empty
    try {
      traced.dequeue();
    } catch (IllegalStateException e) {
      // failure was traced before the exception
    }

    System.out.println(ConsoleTraceRenderer.renderAll(log));
  }
}
