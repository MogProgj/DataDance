package structlab.trace;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TraceLogTest {

  @Test
  void emptyLogHasZeroSize() {
    TraceLog log = new TraceLog();
    assertTrue(log.isEmpty());
    assertEquals(0, log.size());
  }

  @Test
  void addedStepsAreRetrievable() {
    TraceLog log = new TraceLog();
    TraceStep step = makeStep("op1");
    log.add(step);

    assertEquals(1, log.size());
    assertSame(step, log.steps().get(0));
  }

  @Test
  void stepsListIsUnmodifiable() {
    TraceLog log = new TraceLog();
    log.add(makeStep("op1"));

    assertThrows(UnsupportedOperationException.class, () ->
        log.steps().add(makeStep("op2")));
  }

  @Test
  void clearRemovesAllSteps() {
    TraceLog log = new TraceLog();
    log.add(makeStep("op1"));
    log.add(makeStep("op2"));
    log.clear();

    assertTrue(log.isEmpty());
  }

  @Test
  void formatAllProducesMultiLineOutput() {
    TraceLog log = new TraceLog();
    log.add(makeStep("op1"));
    log.add(makeStep("op2"));

    String output = log.formatAll();
    assertTrue(output.contains("op1"));
    assertTrue(output.contains("op2"));
  }

  private TraceStep makeStep(String opName) {
    return new TraceStep("Test", "TestImpl", opName, null,
        "before", "after", InvariantResult.PASSED, null, "did " + opName);
  }
}
