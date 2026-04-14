package structlab.trace;

import org.junit.jupiter.api.Test;
import structlab.core.hash.HashSetCustom;

import static org.junit.jupiter.api.Assertions.*;

class TracedHashSetCustomTest {

  @Test
  void addProducesTraceStep() {
    HashSetCustom<Integer> set = new HashSetCustom<>();
    TraceLog log = new TraceLog();
    TracedHashSetCustom<Integer> traced = new TracedHashSetCustom<>(set, log);

    boolean added = traced.add(10);

    assertTrue(added);
    assertEquals(1, log.size());
    TraceStep step = log.steps().get(0);
    assertEquals("add", step.operationName());
    assertEquals("10", step.input());
    assertEquals(InvariantResult.PASSED, step.invariantResult());
    assertTrue(step.explanation().contains("Added"));
  }

  @Test
  void addDuplicateProducesNoChangeExplanation() {
    HashSetCustom<Integer> set = new HashSetCustom<>();
    TraceLog log = new TraceLog();
    TracedHashSetCustom<Integer> traced = new TracedHashSetCustom<>(set, log);

    traced.add(10);
    boolean added = traced.add(10);

    assertFalse(added);
    assertEquals(2, log.size());
    TraceStep dupStep = log.steps().get(1);
    assertTrue(dupStep.explanation().contains("already exists"));
  }

  @Test
  void containsProducesTraceStepWithNoStateChange() {
    HashSetCustom<Integer> set = new HashSetCustom<>();
    TraceLog log = new TraceLog();
    TracedHashSetCustom<Integer> traced = new TracedHashSetCustom<>(set, log);

    traced.add(10);
    boolean found = traced.contains(10);

    assertTrue(found);
    assertEquals(2, log.size());
    TraceStep step = log.steps().get(1);
    assertEquals("contains", step.operationName());
    assertEquals(step.beforeState(), step.afterState());
    assertTrue(step.explanation().contains("found"));
  }

  @Test
  void containsNotFoundProducesCorrectExplanation() {
    HashSetCustom<Integer> set = new HashSetCustom<>();
    TraceLog log = new TraceLog();
    TracedHashSetCustom<Integer> traced = new TracedHashSetCustom<>(set, log);

    boolean found = traced.contains(99);

    assertFalse(found);
    assertTrue(log.steps().get(0).explanation().contains("not found"));
  }

  @Test
  void removeProducesTraceStep() {
    HashSetCustom<Integer> set = new HashSetCustom<>();
    TraceLog log = new TraceLog();
    TracedHashSetCustom<Integer> traced = new TracedHashSetCustom<>(set, log);

    traced.add(10);
    boolean removed = traced.remove(10);

    assertTrue(removed);
    assertEquals(2, log.size());
    TraceStep removeStep = log.steps().get(1);
    assertEquals("remove", removeStep.operationName());
    assertTrue(removeStep.explanation().contains("Removed"));
  }

  @Test
  void removeNotFoundProducesCorrectExplanation() {
    HashSetCustom<Integer> set = new HashSetCustom<>();
    TraceLog log = new TraceLog();
    TracedHashSetCustom<Integer> traced = new TracedHashSetCustom<>(set, log);

    boolean removed = traced.remove(99);

    assertFalse(removed);
    assertTrue(log.steps().get(0).explanation().contains("not found"));
  }

  @Test
  void unwrapReturnsOriginalSet() {
    HashSetCustom<Integer> set = new HashSetCustom<>();
    TracedHashSetCustom<Integer> traced = new TracedHashSetCustom<>(set, new TraceLog());
    assertSame(set, traced.unwrap());
  }

  @Test
  void invariantPassesThroughDiverseOperations() {
    HashSetCustom<Integer> set = new HashSetCustom<>();
    TraceLog log = new TraceLog();
    TracedHashSetCustom<Integer> traced = new TracedHashSetCustom<>(set, log);

    traced.add(10);
    traced.add(20);
    traced.add(10);  // duplicate
    traced.remove(20);
    traced.contains(10);

    for (TraceStep step : log.steps()) {
      assertEquals(InvariantResult.PASSED, step.invariantResult());
    }
  }
}
