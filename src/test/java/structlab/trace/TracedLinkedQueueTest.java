package structlab.trace;

import org.junit.jupiter.api.Test;
import structlab.core.queue.LinkedQueue;

import static org.junit.jupiter.api.Assertions.*;

class TracedLinkedQueueTest {

  @Test
  void enqueueProducesTraceStep() {
    LinkedQueue<Integer> queue = new LinkedQueue<>();
    TraceLog log = new TraceLog();
    TracedLinkedQueue<Integer> traced = new TracedLinkedQueue<>(queue, log);

    traced.enqueue(10);

    assertEquals(1, log.size());
    TraceStep step = log.steps().get(0);
    assertEquals("enqueue", step.operationName());
    assertEquals("10", step.input());
    assertEquals(InvariantResult.PASSED, step.invariantResult());
    assertTrue(step.explanation().contains("both front and rear"));
  }

  @Test
  void enqueueOnNonEmptyMentionsRear() {
    LinkedQueue<Integer> queue = new LinkedQueue<>();
    TraceLog log = new TraceLog();
    TracedLinkedQueue<Integer> traced = new TracedLinkedQueue<>(queue, log);

    traced.enqueue(10);
    traced.enqueue(20);

    TraceStep step = log.steps().get(1);
    assertTrue(step.explanation().contains("rear"));
  }

  @Test
  void dequeueProducesTraceStep() {
    LinkedQueue<Integer> queue = new LinkedQueue<>();
    TraceLog log = new TraceLog();
    TracedLinkedQueue<Integer> traced = new TracedLinkedQueue<>(queue, log);

    traced.enqueue(10);
    int val = traced.dequeue();

    assertEquals(10, val);
    assertEquals(2, log.size());
    assertEquals("dequeue", log.steps().get(1).operationName());
  }

  @Test
  void dequeueFromEmptyTracesFailureThenThrows() {
    LinkedQueue<Integer> queue = new LinkedQueue<>();
    TraceLog log = new TraceLog();
    TracedLinkedQueue<Integer> traced = new TracedLinkedQueue<>(queue, log);

    assertThrows(IllegalStateException.class, traced::dequeue);

    assertEquals(1, log.size());
    assertTrue(log.steps().get(0).explanation().startsWith("FAILED:"));
  }

  @Test
  void peekDoesNotChangeState() {
    LinkedQueue<Integer> queue = new LinkedQueue<>();
    TraceLog log = new TraceLog();
    TracedLinkedQueue<Integer> traced = new TracedLinkedQueue<>(queue, log);

    traced.enqueue(99);
    int val = traced.peek();

    assertEquals(99, val);
    TraceStep step = log.steps().get(1);
    assertEquals("peek", step.operationName());
    assertEquals(step.beforeState(), step.afterState());
  }

  @Test
  void peekOnEmptyTracesFailureThenThrows() {
    LinkedQueue<Integer> queue = new LinkedQueue<>();
    TraceLog log = new TraceLog();
    TracedLinkedQueue<Integer> traced = new TracedLinkedQueue<>(queue, log);

    assertThrows(IllegalStateException.class, traced::peek);

    assertEquals(1, log.size());
    assertTrue(log.steps().get(0).explanation().startsWith("FAILED:"));
  }

  @Test
  void unwrapReturnsOriginalQueue() {
    LinkedQueue<Integer> queue = new LinkedQueue<>();
    TracedLinkedQueue<Integer> traced = new TracedLinkedQueue<>(queue, new TraceLog());
    assertSame(queue, traced.unwrap());
  }
}
