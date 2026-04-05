package structlab.core.queue;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TwoStackQueueTest {

  @Test
  void newQueueStartsEmpty() {
    TwoStackQueue<Integer> queue = new TwoStackQueue<>();

    assertEquals(0, queue.size());
    assertTrue(queue.isEmpty());
    assertTrue(queue.checkInvariant());
  }

  @Test
  void enqueueAddsElementsInOrder() {
    TwoStackQueue<Integer> queue = new TwoStackQueue<>();

    queue.enqueue(10);
    queue.enqueue(20);
    queue.enqueue(30);

    assertEquals(3, queue.size());
    assertEquals(10, queue.peek());
    assertTrue(queue.checkInvariant());
  }

  @Test
  void dequeueRemovesFrontElement() {
    TwoStackQueue<Integer> queue = new TwoStackQueue<>();

    queue.enqueue(10);
    queue.enqueue(20);
    queue.enqueue(30);

    int removed = queue.dequeue();

    assertEquals(10, removed);
    assertEquals(2, queue.size());
    assertEquals(20, queue.peek());
    assertTrue(queue.checkInvariant());
  }

  @Test
  void queueFollowsFifoOrder() {
    TwoStackQueue<Integer> queue = new TwoStackQueue<>();

    queue.enqueue(1);
    queue.enqueue(2);
    queue.enqueue(3);

    assertEquals(1, queue.dequeue());
    assertEquals(2, queue.dequeue());
    assertEquals(3, queue.dequeue());
    assertTrue(queue.isEmpty());
    assertTrue(queue.checkInvariant());
  }

  @Test
  void interleavedEnqueueAndDequeueWorks() {
    TwoStackQueue<Integer> queue = new TwoStackQueue<>();

    queue.enqueue(1);
    queue.enqueue(2);
    assertEquals(1, queue.dequeue());

    queue.enqueue(3);
    queue.enqueue(4);
    assertEquals(2, queue.dequeue());
    assertEquals(3, queue.dequeue());
    assertEquals(4, queue.dequeue());
    assertTrue(queue.isEmpty());
    assertTrue(queue.checkInvariant());
  }

  @Test
  void dequeueLastElementMakesQueueEmpty() {
    TwoStackQueue<Integer> queue = new TwoStackQueue<>();

    queue.enqueue(42);

    assertEquals(42, queue.dequeue());
    assertTrue(queue.isEmpty());
    assertTrue(queue.checkInvariant());
  }

  @Test
  void dequeueThrowsOnEmptyQueue() {
    TwoStackQueue<Integer> queue = new TwoStackQueue<>();

    assertThrows(IllegalStateException.class, queue::dequeue);
  }

  @Test
  void peekThrowsOnEmptyQueue() {
    TwoStackQueue<Integer> queue = new TwoStackQueue<>();

    assertThrows(IllegalStateException.class, queue::peek);
  }
}
