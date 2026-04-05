package structlab.trace;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TraceStepTest {

  @Test
  void recordFieldsAreAccessible() {
    TraceStep step = new TraceStep(
        "Dynamic Array", "DynamicArray", "append", "42",
        "before", "after", InvariantResult.PASSED,
        "O(1) amortised", "Appended 42.");

    assertEquals("Dynamic Array", step.structureName());
    assertEquals("DynamicArray", step.implementationName());
    assertEquals("append", step.operationName());
    assertEquals("42", step.input());
    assertEquals("before", step.beforeState());
    assertEquals("after", step.afterState());
    assertEquals(InvariantResult.PASSED, step.invariantResult());
    assertEquals("O(1) amortised", step.complexityNote());
    assertEquals("Appended 42.", step.explanation());
  }

  @Test
  void formatIncludesAllFields() {
    TraceStep step = new TraceStep(
        "Stack", "ArrayStack", "push", "10",
        "before-snap", "after-snap", InvariantResult.PASSED,
        "O(1)", "Pushed 10 onto the stack.");

    String formatted = step.format();
    assertTrue(formatted.contains("push(10)"));
    assertTrue(formatted.contains("ArrayStack"));
    assertTrue(formatted.contains("before-snap"));
    assertTrue(formatted.contains("after-snap"));
    assertTrue(formatted.contains("PASSED"));
    assertTrue(formatted.contains("O(1)"));
    assertTrue(formatted.contains("Pushed 10"));
  }

  @Test
  void formatOmitsComplexityWhenNull() {
    TraceStep step = new TraceStep(
        "Queue", "CircularArrayQueue", "dequeue", null,
        "b", "a", InvariantResult.PASSED, null, "Dequeued.");

    String formatted = step.format();
    assertFalse(formatted.contains("Complexity"));
    assertTrue(formatted.contains("dequeue"));
    assertFalse(formatted.contains("(null)"));
  }
}
