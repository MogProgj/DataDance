package structlab.trace;

import org.junit.jupiter.api.Test;
import structlab.core.array.FixedArray;

import static org.junit.jupiter.api.Assertions.*;

class TracedFixedArrayTest {

  @Test
  void appendProducesTraceStep() {
    FixedArray<Integer> array = new FixedArray<>(4);
    TraceLog log = new TraceLog();
    TracedFixedArray<Integer> traced = new TracedFixedArray<>(array, log);

    traced.append(42);

    assertEquals(1, log.size());
    TraceStep step = log.steps().get(0);
    assertEquals("append", step.operationName());
    assertEquals("42", step.input());
    assertEquals(InvariantResult.PASSED, step.invariantResult());
    assertTrue(step.explanation().contains("Appended 42"));
  }

  @Test
  void appendToFullArrayTracesFailureThenThrows() {
    FixedArray<Integer> array = new FixedArray<>(1);
    TraceLog log = new TraceLog();
    TracedFixedArray<Integer> traced = new TracedFixedArray<>(array, log);

    traced.append(1); // fills it

    assertThrows(IllegalStateException.class, () -> traced.append(2));

    assertEquals(2, log.size());
    TraceStep failStep = log.steps().get(1);
    assertEquals("append", failStep.operationName());
    assertTrue(failStep.explanation().startsWith("FAILED:"));
    assertEquals(failStep.beforeState(), failStep.afterState());
  }

  @Test
  void insertProducesTraceStep() {
    FixedArray<Integer> array = new FixedArray<>(4);
    TraceLog log = new TraceLog();
    TracedFixedArray<Integer> traced = new TracedFixedArray<>(array, log);

    traced.append(10);
    traced.insert(0, 5);

    assertEquals(2, log.size());
    TraceStep step = log.steps().get(1);
    assertEquals("insert", step.operationName());
    assertTrue(step.input().contains("index=0"));
    assertTrue(step.input().contains("value=5"));
  }

  @Test
  void insertIntoFullArrayTracesFailureThenThrows() {
    FixedArray<Integer> array = new FixedArray<>(1);
    TraceLog log = new TraceLog();
    TracedFixedArray<Integer> traced = new TracedFixedArray<>(array, log);

    traced.append(1);

    assertThrows(IllegalStateException.class, () -> traced.insert(0, 2));

    TraceStep failStep = log.steps().get(1);
    assertTrue(failStep.explanation().startsWith("FAILED:"));
  }

  @Test
  void removeAtProducesTraceStep() {
    FixedArray<Integer> array = new FixedArray<>(4);
    TraceLog log = new TraceLog();
    TracedFixedArray<Integer> traced = new TracedFixedArray<>(array, log);

    traced.append(10);
    traced.append(20);
    int removed = traced.removeAt(0);

    assertEquals(10, removed);
    assertEquals(3, log.size());
    assertEquals("removeAt", log.steps().get(2).operationName());
  }

  @Test
  void getProducesTraceStepWithSameBeforeAndAfter() {
    FixedArray<Integer> array = new FixedArray<>(4);
    TraceLog log = new TraceLog();
    TracedFixedArray<Integer> traced = new TracedFixedArray<>(array, log);

    traced.append(99);
    int val = traced.get(0);

    assertEquals(99, val);
    TraceStep step = log.steps().get(1);
    assertEquals("get", step.operationName());
    assertEquals(step.beforeState(), step.afterState());
  }

  @Test
  void unwrapReturnsOriginalArray() {
    FixedArray<Integer> array = new FixedArray<>(4);
    TracedFixedArray<Integer> traced = new TracedFixedArray<>(array, new TraceLog());
    assertSame(array, traced.unwrap());
  }
}
