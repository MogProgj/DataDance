package structlab.trace;

import org.junit.jupiter.api.Test;
import structlab.core.queue.CircularArrayQueue;

import static org.junit.jupiter.api.Assertions.*;

class TracedCircularArrayQueueTest {

  @Test
  void enqueueProducesTraceStep() {
    CircularArrayQueue<Integer> queue = new CircularArrayQueue<>(4);
    TraceLog log = new TraceLog();
    TracedCircularArrayQueue<Integer> traced = new TracedCircularArrayQueue<>(queue, log);

    traced.enqueue(10);

    assertEquals(1, log.size());
    TraceStep step = log.steps().get(0);
    assertEquals("enqueue", step.operationName());
    assertEquals("10", step.input());
    assertEquals(InvariantResult.PASSED, step.invariantResult());
  }

  @Test
  void enqueueWithResizeNotesComplexity() {
    CircularArrayQueue<Integer> queue = new CircularArrayQueue<>(2);
    TraceLog log = new TraceLog();
    TracedCircularArrayQueue<Integer> traced = new TracedCircularArrayQueue<>(queue, log);

    traced.enqueue(1);
    traced.enqueue(2);
    traced.enqueue(3); // triggers resize

    assertEquals("O(1) amortised", log.steps().get(0).complexityNote());
    assertEquals("O(n) - resize triggered", log.steps().get(2).complexityNote());
  }

  @Test
  void dequeueProducesTraceStep() {
    CircularArrayQueue<Integer> queue = new CircularArrayQueue<>(4);
    TraceLog log = new TraceLog();
    TracedCircularArrayQueue<Integer> traced = new TracedCircularArrayQueue<>(queue, log);

    traced.enqueue(10);
    int val = traced.dequeue();

    assertEquals(10, val);
    assertEquals(2, log.size());
    assertEquals("dequeue", log.steps().get(1).operationName());
  }

  @Test
  void peekDoesNotChangeState() {
    CircularArrayQueue<Integer> queue = new CircularArrayQueue<>(4);
    TraceLog log = new TraceLog();
    TracedCircularArrayQueue<Integer> traced = new TracedCircularArrayQueue<>(queue, log);

    traced.enqueue(99);
    int val = traced.peek();

    assertEquals(99, val);
    TraceStep step = log.steps().get(1);
    assertEquals("peek", step.operationName());
    assertEquals(step.beforeState(), step.afterState());
  }

  @Test
  void dequeueFromEmptyTracesFailureThenThrows() {
    CircularArrayQueue<Integer> queue = new CircularArrayQueue<>(4);
    TraceLog log = new TraceLog();
    TracedCircularArrayQueue<Integer> traced = new TracedCircularArrayQueue<>(queue, log);

    assertThrows(IllegalStateException.class, traced::dequeue);

    assertEquals(1, log.size());
    assertTrue(log.steps().get(0).explanation().startsWith("FAILED:"));
  }

  @Test
  void peekOnEmptyTracesFailureThenThrows() {
    CircularArrayQueue<Integer> queue = new CircularArrayQueue<>(4);
    TraceLog log = new TraceLog();
    TracedCircularArrayQueue<Integer> traced = new TracedCircularArrayQueue<>(queue, log);

    assertThrows(IllegalStateException.class, traced::peek);

    assertEquals(1, log.size());
    assertTrue(log.steps().get(0).explanation().startsWith("FAILED:"));
  }

  @Test
  void unwrapReturnsOriginalQueue() {
    CircularArrayQueue<Integer> queue = new CircularArrayQueue<>();
    TracedCircularArrayQueue<Integer> traced = new TracedCircularArrayQueue<>(queue, new TraceLog());
    assertSame(queue, traced.unwrap());
  }
}
