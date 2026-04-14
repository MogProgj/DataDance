package structlab.trace;

import org.junit.jupiter.api.Test;
import structlab.core.hash.HashTableChaining;

import static org.junit.jupiter.api.Assertions.*;

class TracedHashTableChainingTest {

  @Test
  void putProducesTraceStep() {
    HashTableChaining<Integer, Integer> table = new HashTableChaining<>();
    TraceLog log = new TraceLog();
    TracedHashTableChaining<Integer, Integer> traced = new TracedHashTableChaining<>(table, log);

    traced.put(1, 100);

    assertEquals(1, log.size());
    TraceStep step = log.steps().get(0);
    assertEquals("put", step.operationName());
    assertEquals("1, 100", step.input());
    assertEquals(InvariantResult.PASSED, step.invariantResult());
    assertTrue(step.explanation().contains("Inserted"));
  }

  @Test
  void putUpdateProducesUpdateExplanation() {
    HashTableChaining<Integer, Integer> table = new HashTableChaining<>();
    TraceLog log = new TraceLog();
    TracedHashTableChaining<Integer, Integer> traced = new TracedHashTableChaining<>(table, log);

    traced.put(1, 100);
    traced.put(1, 200);

    assertEquals(2, log.size());
    TraceStep updateStep = log.steps().get(1);
    assertTrue(updateStep.explanation().contains("Updated"));
    assertTrue(updateStep.explanation().contains("100"));
  }

  @Test
  void getProducesTraceStepWithNoStateChange() {
    HashTableChaining<Integer, Integer> table = new HashTableChaining<>();
    TraceLog log = new TraceLog();
    TracedHashTableChaining<Integer, Integer> traced = new TracedHashTableChaining<>(table, log);

    traced.put(1, 100);
    Integer result = traced.get(1);

    assertEquals(100, result);
    assertEquals(2, log.size());
    TraceStep getStep = log.steps().get(1);
    assertEquals("get", getStep.operationName());
    assertEquals(getStep.beforeState(), getStep.afterState());
    assertTrue(getStep.explanation().contains("Found"));
  }

  @Test
  void getNotFoundProducesCorrectExplanation() {
    HashTableChaining<Integer, Integer> table = new HashTableChaining<>();
    TraceLog log = new TraceLog();
    TracedHashTableChaining<Integer, Integer> traced = new TracedHashTableChaining<>(table, log);

    Integer result = traced.get(99);

    assertNull(result);
    assertEquals(1, log.size());
    assertTrue(log.steps().get(0).explanation().contains("not found"));
  }

  @Test
  void removeProducesTraceStep() {
    HashTableChaining<Integer, Integer> table = new HashTableChaining<>();
    TraceLog log = new TraceLog();
    TracedHashTableChaining<Integer, Integer> traced = new TracedHashTableChaining<>(table, log);

    traced.put(1, 100);
    Integer removed = traced.remove(1);

    assertEquals(100, removed);
    assertEquals(2, log.size());
    TraceStep removeStep = log.steps().get(1);
    assertEquals("remove", removeStep.operationName());
    assertTrue(removeStep.explanation().contains("Removed"));
  }

  @Test
  void removeNotFoundProducesCorrectExplanation() {
    HashTableChaining<Integer, Integer> table = new HashTableChaining<>();
    TraceLog log = new TraceLog();
    TracedHashTableChaining<Integer, Integer> traced = new TracedHashTableChaining<>(table, log);

    Integer removed = traced.remove(99);

    assertNull(removed);
    assertTrue(log.steps().get(0).explanation().contains("not found"));
  }

  @Test
  void containsKeyProducesTraceStepWithNoStateChange() {
    HashTableChaining<Integer, Integer> table = new HashTableChaining<>();
    TraceLog log = new TraceLog();
    TracedHashTableChaining<Integer, Integer> traced = new TracedHashTableChaining<>(table, log);

    traced.put(1, 100);
    boolean found = traced.containsKey(1);

    assertTrue(found);
    assertEquals(2, log.size());
    TraceStep step = log.steps().get(1);
    assertEquals("containsKey", step.operationName());
    assertEquals(step.beforeState(), step.afterState());
    assertTrue(step.explanation().contains("found"));
  }

  @Test
  void containsKeyNotFoundProducesCorrectExplanation() {
    HashTableChaining<Integer, Integer> table = new HashTableChaining<>();
    TraceLog log = new TraceLog();
    TracedHashTableChaining<Integer, Integer> traced = new TracedHashTableChaining<>(table, log);

    boolean found = traced.containsKey(99);

    assertFalse(found);
    assertTrue(log.steps().get(0).explanation().contains("not found"));
  }

  @Test
  void unwrapReturnsOriginalTable() {
    HashTableChaining<Integer, Integer> table = new HashTableChaining<>();
    TracedHashTableChaining<Integer, Integer> traced = new TracedHashTableChaining<>(table, new TraceLog());
    assertSame(table, traced.unwrap());
  }

  @Test
  void invariantPassesAfterOperations() {
    HashTableChaining<Integer, Integer> table = new HashTableChaining<>();
    TraceLog log = new TraceLog();
    TracedHashTableChaining<Integer, Integer> traced = new TracedHashTableChaining<>(table, log);

    traced.put(1, 10);
    traced.put(2, 20);
    traced.put(9, 90);
    traced.remove(2);

    for (TraceStep step : log.steps()) {
      assertEquals(InvariantResult.PASSED, step.invariantResult());
    }
  }
}
