package structlab.app.session;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import structlab.app.runtime.StructureRuntime;
import structlab.app.runtime.adapters.StackRuntimeAdapter;
import structlab.trace.TracedArrayStack;
import structlab.trace.TraceLog;
import structlab.core.stack.ArrayStack;
import java.util.Optional;

public class SessionManagerTest {

    @Test
    public void testSessionActivationAndClose() {
        SessionManager manager = new SessionManager();
        assertTrue(manager.getActiveSession().isEmpty());

        StructureRuntime runtime = new StackRuntimeAdapter("impl-array-stack", new TracedArrayStack<Integer>(new ArrayStack<Integer>(), new TraceLog()));
        ActiveStructureSession session = new ActiveStructureSession("stack", "impl-array-stack", runtime);

        manager.startSession(session);

        assertTrue(manager.hasActiveSession());
        Optional<ActiveStructureSession> activeOpt = manager.getActiveStructureSession();
        assertTrue(activeOpt.isPresent());

        ActiveStructureSession active = activeOpt.get();
        assertEquals("stack", active.getStructureId());
        assertEquals("impl-array-stack", active.getImplementationId());
        assertEquals(runtime, active.getRuntime());

        manager.clearSession();
        assertFalse(manager.hasActiveSession());
        assertTrue(manager.getActiveStructureSession().isEmpty());
    }
}
