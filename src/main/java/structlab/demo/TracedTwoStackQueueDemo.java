package structlab.demo;

import structlab.core.queue.TwoStackQueue;
import structlab.render.ConsoleTraceRenderer;
import structlab.trace.TraceLog;
import structlab.trace.TracedTwoStackQueue;

public class TracedTwoStackQueueDemo {
  public static void main(String[] args) {
    TwoStackQueue<Integer> queue = new TwoStackQueue<>();
    TraceLog log = new TraceLog();
    TracedTwoStackQueue<Integer> traced = new TracedTwoStackQueue<>(queue, log);

    System.out.println("=== Traced TwoStackQueue Demo ===\n");

    traced.enqueue(10);
    traced.enqueue(20);
    traced.enqueue(30);

    // First dequeue triggers inbox-to-outbox transfer
    traced.dequeue();

    // Second dequeue pops directly from outbox (no transfer)
    traced.dequeue();

    // Enqueue more while outbox still has items
    traced.enqueue(40);
    traced.enqueue(50);

    // This peek triggers another transfer
    traced.peek();

    System.out.println(ConsoleTraceRenderer.renderAll(log));
  }
}
