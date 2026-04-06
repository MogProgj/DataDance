package structlab.demo;

import structlab.core.queue.CircularArrayQueue;
import structlab.render.ConsoleTraceRenderer;
import structlab.trace.TraceLog;
import structlab.trace.TracedCircularArrayQueue;

public class TracedCircularArrayQueueDemo {
  public static void main(String[] args) {
    CircularArrayQueue<Integer> queue = new CircularArrayQueue<>(4);
    TraceLog log = new TraceLog();
    TracedCircularArrayQueue<Integer> traced = new TracedCircularArrayQueue<>(queue, log);

    System.out.println("=== Traced CircularArrayQueue Demo ===\n");

    traced.enqueue(10);
    traced.enqueue(20);
    traced.enqueue(30);
    traced.dequeue();
    traced.enqueue(40);
    traced.enqueue(50);  // wraps around circularly
    traced.peek();
    traced.dequeue();

    System.out.println(ConsoleTraceRenderer.renderAll(log));
  }
}
