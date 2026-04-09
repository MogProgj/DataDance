package structlab.trace;

import org.junit.jupiter.api.Test;
import structlab.core.deque.ArrayDequeCustom;

import static org.junit.jupiter.api.Assertions.*;

class TracedArrayDequeCustomTest {

  @Test
  void addFirstProducesTraceStep() {
    ArrayDequeCustom<Integer> deque = new ArrayDequeCustom<>();
    TraceLog log = new TraceLog();
    TracedArrayDequeCustom<Integer> traced = new TracedArrayDequeCustom<>(deque, log);

    traced.addFirst(10);

    assertEquals(1, log.size());
    assertEquals("addFirst", log.steps().get(0).operationName());
    assertEquals(InvariantResult.PASSED, log.steps().get(0).invariantResult());
  }

  @Test
  void addLastProducesTraceStep() {
    ArrayDequeCustom<Integer> deque = new ArrayDequeCustom<>();
    TraceLog log = new TraceLog();
    TracedArrayDequeCustom<Integer> traced = new TracedArrayDequeCustom<>(deque, log);

    traced.addLast(10);
    traced.addLast(20);

    assertEquals(2, log.size());
    assertEquals("addLast", log.steps().get(1).operationName());
  }

  @Test
  void removeFirstFromEmptyTracesFailure() {
    ArrayDequeCustom<Integer> deque = new ArrayDequeCustom<>();
    TraceLog log = new TraceLog();
    TracedArrayDequeCustom<Integer> traced = new TracedArrayDequeCustom<>(deque, log);

    assertThrows(Exception.class, traced::removeFirst);
    assertTrue(log.steps().get(0).explanation().startsWith("FAILED:"));
  }

  @Test
  void removeLastProducesTraceStep() {
    ArrayDequeCustom<Integer> deque = new ArrayDequeCustom<>();
    TraceLog log = new TraceLog();
    TracedArrayDequeCustom<Integer> traced = new TracedArrayDequeCustom<>(deque, log);

    traced.addLast(10);
    int val = traced.removeLast();

    assertEquals(10, val);
    assertEquals("removeLast", log.steps().get(1).operationName());
  }

  @Test
  void peekFirstOnEmptyTracesFailure() {
    ArrayDequeCustom<Integer> deque = new ArrayDequeCustom<>();
    TraceLog log = new TraceLog();
    TracedArrayDequeCustom<Integer> traced = new TracedArrayDequeCustom<>(deque, log);

    assertThrows(Exception.class, traced::peekFirst);
    assertTrue(log.steps().get(0).explanation().startsWith("FAILED:"));
  }

  @Test
  void resizeDetected() {
    ArrayDequeCustom<Integer> deque = new ArrayDequeCustom<>();
    TraceLog log = new TraceLog();
    TracedArrayDequeCustom<Integer> traced = new TracedArrayDequeCustom<>(deque, log);

    // Fill to capacity (default 4) then add one more
    traced.addLast(1);
    traced.addLast(2);
    traced.addLast(3);
    traced.addLast(4);
    traced.addLast(5); // triggers resize

    TraceStep resizeStep = log.steps().get(4);
    assertTrue(resizeStep.explanation().contains("resized"));
  }
}
