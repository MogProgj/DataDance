package structlab.app.session;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import structlab.app.command.CommandResult;
import structlab.app.runtime.OperationExecutionResult;
import structlab.app.runtime.StructureRuntime;
import structlab.app.runtime.adapters.StackRuntimeAdapter;
import structlab.trace.TracedArrayStack;
import structlab.trace.TraceLog;
import structlab.core.stack.ArrayStack;
import java.util.Optional;
import java.util.List;

public class ActiveStructureSessionTest {

    @Test
    public void testHistoryAndState() {
        StructureRuntime runtime = new StackRuntimeAdapter("impl-array-stack", new TracedArrayStack<Integer>(new ArrayStack<Integer>(), new TraceLog()));
        ActiveStructureSession session = new ActiveStructureSession("stack", "impl-array-stack", runtime);

        assertEquals("stack", session.getStructureId());
        assertEquals("impl-array-stack", session.getImplementationId());

        assertEquals(0, session.historySize());
        assertTrue(session.getLastResult().isEmpty());

        OperationExecutionResult res1 = new OperationExecutionResult(true, "Success", "push", "10", List.of());
        session.addHistory(res1);
        assertEquals(1, session.historySize());
        assertEquals(res1, session.getLastResult().get());

        session.clearHistory();
        assertEquals(0, session.historySize());
        assertTrue(session.getLastResult().isEmpty());
    }

    @Test
    public void testDelegationToRuntime() {
        StructureRuntime runtime = new StackRuntimeAdapter("impl-array-stack", new TracedArrayStack<Integer>(new ArrayStack<Integer>(), new TraceLog()));
        ActiveStructureSession session = new ActiveStructureSession("stack", "impl-array-stack", runtime);
        assertEquals(runtime, session.getRuntime());
    }
}
