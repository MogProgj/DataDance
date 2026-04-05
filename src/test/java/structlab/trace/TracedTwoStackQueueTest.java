package structlab.trace;

import org.junit.jupiter.api.Test;
import structlab.core.queue.TwoStackQueue;

import static org.junit.jupiter.api.Assertions.*;

class TracedTwoStackQueueTest {

  @Test
  void enqueueProducesTraceStep() {
    TwoStackQueue<Integer> queue = new TwoStackQueue<>();
    TraceLog log = new TraceLog();
    TracedTwoStackQueue<Integer> traced = new TracedTwoStackQueue<>(queue, log);

    traced.enqueue(10);

    assertEquals(1, log.size());
    TraceStep step = log.steps().get(0);
    assertEquals("enqueue", step.operationName());
    assertEquals("10", step.input());
    assertEquals(InvariantResult.PASSED, step.invariantResult());
    assertTrue(step.explanation().contains("inbox"));
  }

  @Test
  void dequeueTriggersTransferExplanation() {
    TwoStackQueue<Integer> queue = new TwoStackQueue<>();
    TraceLog log = new TraceLog();
    TracedTwoStackQueue<Integer> traced = new TracedTwoStackQueue<>(queue, log);

    traced.enqueue(10);
    traced.enqueue(20);
    int val = traced.dequeue();

    assertEquals(10, val);
    TraceStep deqStep = log.steps().get(2);
    assertEquals("dequeue", deqStep.operationName());
    assertTrue(deqStep.explanation().contains("transfer"));
    assertTrue(deqStep.complexityNote().contains("transfer"));
  }

  @Test
  void dequeueWithoutTransfer() {
    TwoStackQueue<Integer> queue = new TwoStackQueue<>();
    TraceLog log = new TraceLog();
    TracedTwoStackQueue<Integer> traced = new TracedTwoStackQueue<>(queue, log);

    traced.enqueue(10);
    traced.enqueue(20);
    traced.dequeue(); // triggers transfer
    int val = traced.dequeue(); // no transfer needed

    assertEquals(20, val);
    TraceStep step = log.steps().get(3);
    assertEquals("dequeue", step.operationName());
    assertTrue(step.explanation().contains("popping from the outbox"));
  }

  @Test
  void dequeueFromEmptyTracesFailureThenThrows() {
    TwoStackQueue<Integer> queue = new TwoStackQueue<>();
    TraceLog log = new TraceLog();
    TracedTwoStackQueue<Integer> traced = new TracedTwoStackQueue<>(queue, log);

    assertThrows(IllegalStateException.class, traced::dequeue);

    assertEquals(1, log.size());
    assertTrue(log.steps().get(0).explanation().startsWith("FAILED:"));
  }

  @Test
  void peekTriggersTransferExplanation() {
    TwoStackQueue<Integer> queue = new TwoStackQueue<>();
    TraceLog log = new TraceLog();
    TracedTwoStackQueue<Integer> traced = new TracedTwoStackQueue<>(queue, log);

    traced.enqueue(10);
    int val = traced.peek();

    assertEquals(10, val);
    TraceStep step = log.steps().get(1);
    assertEquals("peek", step.operationName());
    assertTrue(step.explanation().contains("transfer"));
  }

  @Test
  void peekOnEmptyTracesFailureThenThrows() {
    TwoStackQueue<Integer> queue = new TwoStackQueue<>();
    TraceLog log = new TraceLog();
    TracedTwoStackQueue<Integer> traced = new TracedTwoStackQueue<>(queue, log);

    assertThrows(IllegalStateException.class, traced::peek);

    assertEquals(1, log.size());
    assertTrue(log.steps().get(0).explanation().startsWith("FAILED:"));
  }

  @Test
  void unwrapReturnsOriginalQueue() {
    TwoStackQueue<Integer> queue = new TwoStackQueue<>();
    TracedTwoStackQueue<Integer> traced = new TracedTwoStackQueue<>(queue, new TraceLog());
    assertSame(queue, traced.unwrap());
  }
}
