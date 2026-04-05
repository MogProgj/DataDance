package structlab.trace;

import org.junit.jupiter.api.Test;
import structlab.core.stack.ArrayStack;

import static org.junit.jupiter.api.Assertions.*;

class TracedArrayStackTest {

  @Test
  void pushProducesTraceStep() {
    ArrayStack<Integer> stack = new ArrayStack<>();
    TraceLog log = new TraceLog();
    TracedArrayStack<Integer> traced = new TracedArrayStack<>(stack, log);

    traced.push(10);

    assertEquals(1, log.size());
    TraceStep step = log.steps().get(0);
    assertEquals("push", step.operationName());
    assertEquals("10", step.input());
    assertEquals(InvariantResult.PASSED, step.invariantResult());
  }

  @Test
  void popProducesTraceStep() {
    ArrayStack<Integer> stack = new ArrayStack<>();
    TraceLog log = new TraceLog();
    TracedArrayStack<Integer> traced = new TracedArrayStack<>(stack, log);

    traced.push(10);
    int val = traced.pop();

    assertEquals(10, val);
    assertEquals(2, log.size());
    TraceStep popStep = log.steps().get(1);
    assertEquals("pop", popStep.operationName());
    assertNull(popStep.input());
  }

  @Test
  void peekDoesNotChangeState() {
    ArrayStack<Integer> stack = new ArrayStack<>();
    TraceLog log = new TraceLog();
    TracedArrayStack<Integer> traced = new TracedArrayStack<>(stack, log);

    traced.push(42);
    int val = traced.peek();

    assertEquals(42, val);
    TraceStep step = log.steps().get(1);
    assertEquals("peek", step.operationName());
    assertEquals(step.beforeState(), step.afterState());
  }

  @Test
  void popFromEmptyTracesFailureThenThrows() {
    ArrayStack<Integer> stack = new ArrayStack<>();
    TraceLog log = new TraceLog();
    TracedArrayStack<Integer> traced = new TracedArrayStack<>(stack, log);

    assertThrows(IllegalStateException.class, traced::pop);

    assertEquals(1, log.size());
    assertTrue(log.steps().get(0).explanation().startsWith("FAILED:"));
  }

  @Test
  void peekOnEmptyTracesFailureThenThrows() {
    ArrayStack<Integer> stack = new ArrayStack<>();
    TraceLog log = new TraceLog();
    TracedArrayStack<Integer> traced = new TracedArrayStack<>(stack, log);

    assertThrows(IllegalStateException.class, traced::peek);

    assertEquals(1, log.size());
    assertTrue(log.steps().get(0).explanation().startsWith("FAILED:"));
  }

  @Test
  void unwrapReturnsOriginalStack() {
    ArrayStack<Integer> stack = new ArrayStack<>();
    TracedArrayStack<Integer> traced = new TracedArrayStack<>(stack, new TraceLog());
    assertSame(stack, traced.unwrap());
  }
}
