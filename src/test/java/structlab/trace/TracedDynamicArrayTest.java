package structlab.trace;

import org.junit.jupiter.api.Test;
import structlab.core.array.DynamicArray;

import static org.junit.jupiter.api.Assertions.*;

class TracedDynamicArrayTest {

  @Test
  void appendProducesTraceStep() {
    DynamicArray<Integer> array = new DynamicArray<>(4);
    TraceLog log = new TraceLog();
    TracedDynamicArray<Integer> traced = new TracedDynamicArray<>(array, log);

    traced.append(42);

    assertEquals(1, log.size());
    TraceStep step = log.steps().get(0);
    assertEquals("append", step.operationName());
    assertEquals("42", step.input());
    assertEquals(InvariantResult.PASSED, step.invariantResult());
  }

  @Test
  void appendWithResizeNotesComplexity() {
    DynamicArray<Integer> array = new DynamicArray<>(1);
    TraceLog log = new TraceLog();
    TracedDynamicArray<Integer> traced = new TracedDynamicArray<>(array, log);

    traced.append(1); // fills capacity
    traced.append(2); // triggers resize

    assertEquals(2, log.size());
    assertEquals("O(1) amortised", log.steps().get(0).complexityNote());
    assertEquals("O(n) - resize triggered", log.steps().get(1).complexityNote());
  }

  @Test
  void insertProducesTraceStep() {
    DynamicArray<Integer> array = new DynamicArray<>(4);
    TraceLog log = new TraceLog();
    TracedDynamicArray<Integer> traced = new TracedDynamicArray<>(array, log);

    traced.append(10);
    traced.insert(0, 5);

    assertEquals(2, log.size());
    TraceStep step = log.steps().get(1);
    assertEquals("insert", step.operationName());
    assertTrue(step.input().contains("index=0"));
  }

  @Test
  void removeAtProducesTraceStep() {
    DynamicArray<Integer> array = new DynamicArray<>(4);
    TraceLog log = new TraceLog();
    TracedDynamicArray<Integer> traced = new TracedDynamicArray<>(array, log);

    traced.append(10);
    traced.append(20);
    int removed = traced.removeAt(0);

    assertEquals(10, removed);
    assertEquals(3, log.size());
    assertEquals("removeAt", log.steps().get(2).operationName());
  }

  @Test
  void getProducesTraceStepWithSameBeforeAndAfter() {
    DynamicArray<Integer> array = new DynamicArray<>(4);
    TraceLog log = new TraceLog();
    TracedDynamicArray<Integer> traced = new TracedDynamicArray<>(array, log);

    traced.append(99);
    int val = traced.get(0);

    assertEquals(99, val);
    TraceStep step = log.steps().get(1);
    assertEquals("get", step.operationName());
    assertEquals(step.beforeState(), step.afterState());
  }

  @Test
  void unwrapReturnsOriginalArray() {
    DynamicArray<Integer> array = new DynamicArray<>();
    TracedDynamicArray<Integer> traced = new TracedDynamicArray<>(array, new TraceLog());
    assertSame(array, traced.unwrap());
  }
}
