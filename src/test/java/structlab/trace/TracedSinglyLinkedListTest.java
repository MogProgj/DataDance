package structlab.trace;

import org.junit.jupiter.api.Test;
import structlab.core.list.SinglyLinkedList;

import static org.junit.jupiter.api.Assertions.*;

class TracedSinglyLinkedListTest {

  @Test
  void addFirstProducesTraceStep() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
    TraceLog log = new TraceLog();
    TracedSinglyLinkedList<Integer> traced = new TracedSinglyLinkedList<>(list, log);

    traced.addFirst(10);

    assertEquals(1, log.size());
    TraceStep step = log.steps().get(0);
    assertEquals("addFirst", step.operationName());
    assertEquals("10", step.input());
    assertEquals(InvariantResult.PASSED, step.invariantResult());
    assertTrue(step.explanation().contains("both head and tail"));
  }

  @Test
  void addLastOnNonEmptyMentionsTail() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
    TraceLog log = new TraceLog();
    TracedSinglyLinkedList<Integer> traced = new TracedSinglyLinkedList<>(list, log);

    traced.addFirst(10);
    traced.addLast(20);

    TraceStep step = log.steps().get(1);
    assertTrue(step.explanation().contains("tail"));
  }

  @Test
  void removeFirstProducesTraceStep() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
    TraceLog log = new TraceLog();
    TracedSinglyLinkedList<Integer> traced = new TracedSinglyLinkedList<>(list, log);

    traced.addFirst(10);
    int val = traced.removeFirst();

    assertEquals(10, val);
    assertEquals(2, log.size());
    assertEquals("removeFirst", log.steps().get(1).operationName());
  }

  @Test
  void removeFirstFromEmptyTracesFailure() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
    TraceLog log = new TraceLog();
    TracedSinglyLinkedList<Integer> traced = new TracedSinglyLinkedList<>(list, log);

    assertThrows(Exception.class, traced::removeFirst);
    assertEquals(1, log.size());
    assertTrue(log.steps().get(0).explanation().startsWith("FAILED:"));
  }

  @Test
  void containsTracesMentionsSearch() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
    TraceLog log = new TraceLog();
    TracedSinglyLinkedList<Integer> traced = new TracedSinglyLinkedList<>(list, log);

    traced.addFirst(10);
    boolean found = traced.contains(10);

    assertTrue(found);
    TraceStep step = log.steps().get(1);
    assertEquals("contains", step.operationName());
    assertTrue(step.explanation().contains("found"));
  }

  @Test
  void getFirstOnEmptyTracesFailure() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
    TraceLog log = new TraceLog();
    TracedSinglyLinkedList<Integer> traced = new TracedSinglyLinkedList<>(list, log);

    assertThrows(Exception.class, traced::getFirst);
    assertTrue(log.steps().get(0).explanation().startsWith("FAILED:"));
  }
}
