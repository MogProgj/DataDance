package structlab.trace;

import org.junit.jupiter.api.Test;
import structlab.core.heap.HeapPriorityQueue;

import static org.junit.jupiter.api.Assertions.*;

class TracedHeapPriorityQueueTest {

  @Test
  void enqueueProducesTraceStep() {
    HeapPriorityQueue<Integer> pq = new HeapPriorityQueue<>();
    TraceLog log = new TraceLog();
    TracedHeapPriorityQueue<Integer> traced = new TracedHeapPriorityQueue<>(pq, log);

    traced.enqueue(5);

    assertEquals(1, log.size());
    assertEquals("enqueue", log.steps().get(0).operationName());
    assertEquals("5", log.steps().get(0).input());
    assertEquals(InvariantResult.PASSED, log.steps().get(0).invariantResult());
  }

  @Test
  void dequeueProducesTraceStep() {
    HeapPriorityQueue<Integer> pq = new HeapPriorityQueue<>();
    TraceLog log = new TraceLog();
    TracedHeapPriorityQueue<Integer> traced = new TracedHeapPriorityQueue<>(pq, log);

    traced.enqueue(3);
    traced.enqueue(5);
    int val = traced.dequeue();

    assertEquals(3, val);
    assertEquals("dequeue", log.steps().get(2).operationName());
  }

  @Test
  void dequeueFromEmptyTracesFailure() {
    HeapPriorityQueue<Integer> pq = new HeapPriorityQueue<>();
    TraceLog log = new TraceLog();
    TracedHeapPriorityQueue<Integer> traced = new TracedHeapPriorityQueue<>(pq, log);

    assertThrows(Exception.class, traced::dequeue);
    assertTrue(log.steps().get(0).explanation().startsWith("FAILED:"));
  }

  @Test
  void peekDoesNotChangeState() {
    HeapPriorityQueue<Integer> pq = new HeapPriorityQueue<>();
    TraceLog log = new TraceLog();
    TracedHeapPriorityQueue<Integer> traced = new TracedHeapPriorityQueue<>(pq, log);

    traced.enqueue(42);
    int val = traced.peek();

    assertEquals(42, val);
    TraceStep step = log.steps().get(1);
    assertEquals(step.beforeState(), step.afterState());
  }

  @Test
  void peekOnEmptyTracesFailure() {
    HeapPriorityQueue<Integer> pq = new HeapPriorityQueue<>();
    TraceLog log = new TraceLog();
    TracedHeapPriorityQueue<Integer> traced = new TracedHeapPriorityQueue<>(pq, log);

    assertThrows(Exception.class, traced::peek);
    assertTrue(log.steps().get(0).explanation().startsWith("FAILED:"));
  }
}
