package structlab.demo;

import structlab.core.queue.TwoStackQueue;

public class TwoStackQueueDemo {
  public static void main(String[] args) {
    TwoStackQueue<Integer> queue = new TwoStackQueue<>();

    System.out.println("Initial:");
    System.out.println(queue.snapshot());
    System.out.println();

    queue.enqueue(10);
    queue.enqueue(20);
    queue.enqueue(30);
    System.out.println("After enqueue(10), enqueue(20), enqueue(30):");
    System.out.println(queue.snapshot());
    System.out.println();

    System.out.println("dequeue(): " + queue.dequeue());
    System.out.println("After dequeue — transfer from inbox to outbox:");
    System.out.println(queue.snapshot());
    System.out.println();

    queue.enqueue(40);
    System.out.println("After enqueue(40) — goes to inbox while outbox still has elements:");
    System.out.println(queue.snapshot());
    System.out.println();

    System.out.println("dequeue(): " + queue.dequeue());
    System.out.println(queue.snapshot());
    System.out.println();

    System.out.println("peek(): " + queue.peek());
    System.out.println("Invariant check: " + queue.checkInvariant());
  }
}
