package structlab.trace;

import org.junit.jupiter.api.Test;
import structlab.core.heap.BinaryHeap;

import static org.junit.jupiter.api.Assertions.*;

class TracedBinaryHeapTest {

  @Test
  void insertProducesTraceStep() {
    BinaryHeap<Integer> heap = new BinaryHeap<>();
    TraceLog log = new TraceLog();
    TracedBinaryHeap<Integer> traced = new TracedBinaryHeap<>(heap, log);

    traced.insert(5);

    assertEquals(1, log.size());
    assertEquals("insert", log.steps().get(0).operationName());
    assertEquals("5", log.steps().get(0).input());
    assertEquals(InvariantResult.PASSED, log.steps().get(0).invariantResult());
  }

  @Test
  void insertNewMinIsDetected() {
    BinaryHeap<Integer> heap = new BinaryHeap<>();
    TraceLog log = new TraceLog();
    TracedBinaryHeap<Integer> traced = new TracedBinaryHeap<>(heap, log);

    traced.insert(5);
    traced.insert(3);  // new min
    traced.insert(1);  // new min

    assertTrue(log.steps().get(1).explanation().contains("new minimum"));
    assertTrue(log.steps().get(2).explanation().contains("new minimum"));
  }

  @Test
  void extractMinProducesTraceStep() {
    BinaryHeap<Integer> heap = new BinaryHeap<>();
    TraceLog log = new TraceLog();
    TracedBinaryHeap<Integer> traced = new TracedBinaryHeap<>(heap, log);

    traced.insert(3);
    traced.insert(5);
    int val = traced.extractMin();

    assertEquals(3, val);
    TraceStep step = log.steps().get(2);
    assertEquals("extractMin", step.operationName());
    assertTrue(step.explanation().contains("sifted down"));
  }

  @Test
  void extractMinFromEmptyTracesFailure() {
    BinaryHeap<Integer> heap = new BinaryHeap<>();
    TraceLog log = new TraceLog();
    TracedBinaryHeap<Integer> traced = new TracedBinaryHeap<>(heap, log);

    assertThrows(Exception.class, traced::extractMin);
    assertTrue(log.steps().get(0).explanation().startsWith("FAILED:"));
  }

  @Test
  void peekDoesNotChangeState() {
    BinaryHeap<Integer> heap = new BinaryHeap<>();
    TraceLog log = new TraceLog();
    TracedBinaryHeap<Integer> traced = new TracedBinaryHeap<>(heap, log);

    traced.insert(10);
    int val = traced.peek();

    assertEquals(10, val);
    TraceStep step = log.steps().get(1);
    assertEquals(step.beforeState(), step.afterState());
  }

  @Test
  void peekOnEmptyTracesFailure() {
    BinaryHeap<Integer> heap = new BinaryHeap<>();
    TraceLog log = new TraceLog();
    TracedBinaryHeap<Integer> traced = new TracedBinaryHeap<>(heap, log);

    assertThrows(Exception.class, traced::peek);
    assertTrue(log.steps().get(0).explanation().startsWith("FAILED:"));
  }
}
