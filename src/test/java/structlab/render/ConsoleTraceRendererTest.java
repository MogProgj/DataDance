package structlab.render;

import org.junit.jupiter.api.Test;
import structlab.trace.InvariantResult;
import structlab.trace.TraceLog;
import structlab.trace.TraceStep;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleTraceRendererTest {

  @Test
  void renderShowsOperationHeader() {
    TraceStep step = new TraceStep(
        "Dynamic Array", "DynamicArray", "append", "42",
        "DynamicArray{size=0, capacity=2, elements=[], raw=[null, null]}",
        "DynamicArray{size=1, capacity=2, elements=[42], raw=[42, null]}",
        InvariantResult.PASSED, "O(1)", "Appended 42.");

    String rendered = ConsoleTraceRenderer.render(step);
    assertTrue(rendered.contains("append(42)"));
    assertTrue(rendered.contains("DynamicArray"));
  }

  @Test
  void renderShowsBeforeAndAfterLabels() {
    TraceStep step = makeStep();
    String rendered = ConsoleTraceRenderer.render(step);
    assertTrue(rendered.contains("BEFORE:"));
    assertTrue(rendered.contains("AFTER:"));
  }

  @Test
  void renderShowsInvariantAndComplexity() {
    TraceStep step = makeStep();
    String rendered = ConsoleTraceRenderer.render(step);
    assertTrue(rendered.contains("Invariant:"));
    assertTrue(rendered.contains("PASSED"));
    assertTrue(rendered.contains("Complexity:"));
    assertTrue(rendered.contains("O(1)"));
  }

  @Test
  void renderShowsExplanation() {
    TraceStep step = makeStep();
    String rendered = ConsoleTraceRenderer.render(step);
    assertTrue(rendered.contains("Appended 42."));
  }

  @Test
  void renderShowsStructureVisualization() {
    TraceStep step = makeStep();
    String rendered = ConsoleTraceRenderer.render(step);
    // Should contain rendered structure elements, not just raw snapshot
    assertTrue(rendered.contains("Logical:"));
    assertTrue(rendered.contains("Backing:"));
  }

  @Test
  void renderAllNumbersSteps() {
    TraceLog log = new TraceLog();
    log.add(makeStep());
    log.add(makeStep());

    String rendered = ConsoleTraceRenderer.renderAll(log);
    assertTrue(rendered.contains("Step 1 of 2"));
    assertTrue(rendered.contains("Step 2 of 2"));
  }

  @Test
  void renderHandlesNullInput() {
    TraceStep step = new TraceStep(
        "Stack", "ArrayStack", "pop", null,
        "ArrayStack{size=1, top=10, elements=DynamicArray{size=1, capacity=4, elements=[10], raw=[10, null, null, null]}}",
        "ArrayStack{size=0, top=null, elements=DynamicArray{size=0, capacity=4, elements=[], raw=[null, null, null, null]}}",
        InvariantResult.PASSED, "O(1)", "Popped 10.");

    String rendered = ConsoleTraceRenderer.render(step);
    assertTrue(rendered.contains("pop"));
    assertFalse(rendered.contains("pop(null)"));
  }

  @Test
  void renderHandlesNullComplexity() {
    TraceStep step = new TraceStep(
        "Dynamic Array", "DynamicArray", "append", "1",
        "DynamicArray{size=0, capacity=2, elements=[], raw=[null, null]}",
        "DynamicArray{size=1, capacity=2, elements=[1], raw=[1, null]}",
        InvariantResult.PASSED, null, "Appended 1.");

    String rendered = ConsoleTraceRenderer.render(step);
    assertFalse(rendered.contains("Complexity:"));
  }

  private TraceStep makeStep() {
    return new TraceStep(
        "Dynamic Array", "DynamicArray", "append", "42",
        "DynamicArray{size=0, capacity=2, elements=[], raw=[null, null]}",
        "DynamicArray{size=1, capacity=2, elements=[42], raw=[42, null]}",
        InvariantResult.PASSED, "O(1)", "Appended 42.");
  }
}
