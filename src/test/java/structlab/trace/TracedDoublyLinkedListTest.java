package structlab.trace;

import org.junit.jupiter.api.Test;
import structlab.core.list.DoublyLinkedList;

import static org.junit.jupiter.api.Assertions.*;

class TracedDoublyLinkedListTest {

  @Test
  void addFirstProducesTraceStep() {
    DoublyLinkedList<Integer> list = new DoublyLinkedList<>();
    TraceLog log = new TraceLog();
    TracedDoublyLinkedList<Integer> traced = new TracedDoublyLinkedList<>(list, log);

    traced.addFirst(10);

    assertEquals(1, log.size());
    assertEquals("addFirst", log.steps().get(0).operationName());
    assertEquals(InvariantResult.PASSED, log.steps().get(0).invariantResult());
  }

  @Test
  void addLastMentionsPrevNext() {
    DoublyLinkedList<Integer> list = new DoublyLinkedList<>();
    TraceLog log = new TraceLog();
    TracedDoublyLinkedList<Integer> traced = new TracedDoublyLinkedList<>(list, log);

    traced.addFirst(10);
    traced.addLast(20);

    TraceStep step = log.steps().get(1);
    assertTrue(step.explanation().contains("prev") || step.explanation().contains("tail"));
  }

  @Test
  void removeLastProducesTraceStep() {
    DoublyLinkedList<Integer> list = new DoublyLinkedList<>();
    TraceLog log = new TraceLog();
    TracedDoublyLinkedList<Integer> traced = new TracedDoublyLinkedList<>(list, log);

    traced.addFirst(10);
    traced.addLast(20);
    int val = traced.removeLast();

    assertEquals(20, val);
    assertEquals("removeLast", log.steps().get(2).operationName());
  }

  @Test
  void removeLastFromEmptyTracesFailure() {
    DoublyLinkedList<Integer> list = new DoublyLinkedList<>();
    TraceLog log = new TraceLog();
    TracedDoublyLinkedList<Integer> traced = new TracedDoublyLinkedList<>(list, log);

    assertThrows(Exception.class, traced::removeLast);
    assertTrue(log.steps().get(0).explanation().startsWith("FAILED:"));
  }

  @Test
  void containsSearchesChain() {
    DoublyLinkedList<Integer> list = new DoublyLinkedList<>();
    TraceLog log = new TraceLog();
    TracedDoublyLinkedList<Integer> traced = new TracedDoublyLinkedList<>(list, log);

    traced.addFirst(10);
    assertFalse(traced.contains(99));
    assertTrue(log.steps().get(1).explanation().contains("not found"));
  }
}
