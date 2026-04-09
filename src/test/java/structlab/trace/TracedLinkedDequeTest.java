package structlab.trace;

import org.junit.jupiter.api.Test;
import structlab.core.deque.LinkedDeque;

import static org.junit.jupiter.api.Assertions.*;

class TracedLinkedDequeTest {

  @Test
  void addFirstProducesTraceStep() {
    LinkedDeque<Integer> deque = new LinkedDeque<>();
    TraceLog log = new TraceLog();
    TracedLinkedDeque<Integer> traced = new TracedLinkedDeque<>(deque, log);

    traced.addFirst(10);

    assertEquals(1, log.size());
    assertEquals("addFirst", log.steps().get(0).operationName());
    assertEquals(InvariantResult.PASSED, log.steps().get(0).invariantResult());
  }

  @Test
  void removeLastProducesTraceStep() {
    LinkedDeque<Integer> deque = new LinkedDeque<>();
    TraceLog log = new TraceLog();
    TracedLinkedDeque<Integer> traced = new TracedLinkedDeque<>(deque, log);

    traced.addFirst(10);
    int val = traced.removeLast();

    assertEquals(10, val);
    assertEquals("removeLast", log.steps().get(1).operationName());
  }

  @Test
  void removeFirstFromEmptyTracesFailure() {
    LinkedDeque<Integer> deque = new LinkedDeque<>();
    TraceLog log = new TraceLog();
    TracedLinkedDeque<Integer> traced = new TracedLinkedDeque<>(deque, log);

    assertThrows(Exception.class, traced::removeFirst);
    assertTrue(log.steps().get(0).explanation().startsWith("FAILED:"));
  }

  @Test
  void peekFirstDoesNotChangeState() {
    LinkedDeque<Integer> deque = new LinkedDeque<>();
    TraceLog log = new TraceLog();
    TracedLinkedDeque<Integer> traced = new TracedLinkedDeque<>(deque, log);

    traced.addFirst(42);
    int val = traced.peekFirst();

    assertEquals(42, val);
    TraceStep step = log.steps().get(1);
    assertEquals(step.beforeState(), step.afterState());
  }

  @Test
  void peekLastOnEmptyTracesFailure() {
    LinkedDeque<Integer> deque = new LinkedDeque<>();
    TraceLog log = new TraceLog();
    TracedLinkedDeque<Integer> traced = new TracedLinkedDeque<>(deque, log);

    assertThrows(Exception.class, traced::peekLast);
    assertTrue(log.steps().get(0).explanation().startsWith("FAILED:"));
  }
}
