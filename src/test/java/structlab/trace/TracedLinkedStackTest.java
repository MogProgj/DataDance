package structlab.trace;

import org.junit.jupiter.api.Test;
import structlab.core.stack.LinkedStack;

import static org.junit.jupiter.api.Assertions.*;

class TracedLinkedStackTest {

  @Test
  void pushProducesTraceStep() {
    LinkedStack<Integer> stack = new LinkedStack<>();
    TraceLog log = new TraceLog();
    TracedLinkedStack<Integer> traced = new TracedLinkedStack<>(stack, log);

    traced.push(10);

    assertEquals(1, log.size());
    TraceStep step = log.steps().get(0);
    assertEquals("push", step.operationName());
    assertEquals("10", step.input());
    assertEquals(InvariantResult.PASSED, step.invariantResult());
    assertTrue(step.explanation().contains("node"));
  }

  @Test
  void popProducesTraceStep() {
    LinkedStack<Integer> stack = new LinkedStack<>();
    TraceLog log = new TraceLog();
    TracedLinkedStack<Integer> traced = new TracedLinkedStack<>(stack, log);

    traced.push(10);
    int val = traced.pop();

    assertEquals(10, val);
    assertEquals(2, log.size());
    TraceStep popStep = log.steps().get(1);
    assertEquals("pop", popStep.operationName());
    assertNull(popStep.input());
  }

  @Test
  void popFromEmptyTracesFailureThenThrows() {
    LinkedStack<Integer> stack = new LinkedStack<>();
    TraceLog log = new TraceLog();
    TracedLinkedStack<Integer> traced = new TracedLinkedStack<>(stack, log);

    assertThrows(IllegalStateException.class, traced::pop);

    assertEquals(1, log.size());
    TraceStep failStep = log.steps().get(0);
    assertTrue(failStep.explanation().startsWith("FAILED:"));
    assertEquals(failStep.beforeState(), failStep.afterState());
  }

  @Test
  void peekDoesNotChangeState() {
    LinkedStack<Integer> stack = new LinkedStack<>();
    TraceLog log = new TraceLog();
    TracedLinkedStack<Integer> traced = new TracedLinkedStack<>(stack, log);

    traced.push(42);
    int val = traced.peek();

    assertEquals(42, val);
    TraceStep step = log.steps().get(1);
    assertEquals("peek", step.operationName());
    assertEquals(step.beforeState(), step.afterState());
  }

  @Test
  void peekOnEmptyTracesFailureThenThrows() {
    LinkedStack<Integer> stack = new LinkedStack<>();
    TraceLog log = new TraceLog();
    TracedLinkedStack<Integer> traced = new TracedLinkedStack<>(stack, log);

    assertThrows(IllegalStateException.class, traced::peek);

    assertEquals(1, log.size());
    assertTrue(log.steps().get(0).explanation().startsWith("FAILED:"));
  }

  @Test
  void unwrapReturnsOriginalStack() {
    LinkedStack<Integer> stack = new LinkedStack<>();
    TracedLinkedStack<Integer> traced = new TracedLinkedStack<>(stack, new TraceLog());
    assertSame(stack, traced.unwrap());
  }
}
