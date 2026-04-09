package structlab.app.command.handlers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import structlab.app.command.*;
import structlab.app.session.SessionManager;
import structlab.registry.InMemoryStructureRegistry;
import structlab.registry.StructureMetadata;
import structlab.registry.ImplementationMetadata;
import java.util.List;
import structlab.core.stack.ArrayStack;
import structlab.trace.TracedArrayStack;
import structlab.app.runtime.adapters.StackRuntimeAdapter;
import structlab.app.session.ActiveStructureSession;

public class SessionCommandsTest {

    @Test
    public void testOpenValidation() {
        CommandRouter router = new CommandRouter();
        SessionCommands.registerAll(router);
        CommandContext ctx = new CommandContext(new InMemoryStructureRegistry(), new SessionManager());
        CommandResult res = router.handle(ctx, new ParsedCommand("open", "open", List.of()));
        assertFalse(res.success());
        assertEquals("Usage Error", res.title());
        assertTrue(res.message().contains("Usage: open"));
    }

    @Test
    public void testOpenAndCloseSession() {
        CommandRouter router = new CommandRouter();
        SessionCommands.registerAll(router);
        InMemoryStructureRegistry registry = new InMemoryStructureRegistry();
        StructureMetadata sm = new StructureMetadata("struct-stack", "Stack", "linear", java.util.Set.of(), "desc", "behavior", "notes");
        ImplementationMetadata im = new ImplementationMetadata("impl-array-stack", "Array Stack", "struct-stack", "desc", java.util.Map.of(), "O(1)", Object.class);
        registry.registerStructure(sm);
        registry.registerImplementation(im);
        CommandContext ctx = new CommandContext(registry, new SessionManager());
        CommandResult openRes = router.handle(ctx, new ParsedCommand("open", "open", List.of("stack", "impl-array-stack")));
        assertTrue(openRes.success());
        assertEquals("Session Started", openRes.title());
        assertNotNull(ctx.sessionManager().getActiveStructureSession());
        CommandResult closeRes = router.handle(ctx, new ParsedCommand("close", "close", List.of()));
        assertTrue(closeRes.success());
        assertEquals("Session Closed", closeRes.title());
        assertTrue(ctx.sessionManager().getActiveSession().isEmpty());
    }

    @Test
    public void testFailedOpPreservesHistoryAndTrace() {
        CommandRouter router = new CommandRouter();
        SessionCommands.registerAll(router);
        CommandContext ctx = new CommandContext(new InMemoryStructureRegistry(), new SessionManager());

        TracedArrayStack<Integer> tas = new TracedArrayStack<Integer>(new ArrayStack<Integer>(), new structlab.trace.TraceLog());
        StackRuntimeAdapter sra = new StackRuntimeAdapter("Array Stack", tas);
        ActiveStructureSession session = new ActiveStructureSession("struct-stack", "impl-array-stack", sra);
        ctx.sessionManager().startSession(session);

        CommandResult failRes = router.handle(ctx, new ParsedCommand("pop", "pop", List.of()));
        assertFalse(failRes.success());
        assertEquals(1, session.getHistory().size());

        var failureEntry = session.getHistory().get(0);
        assertFalse(failureEntry.success());
        assertNotNull(failureEntry.traceSteps());

        CommandResult traceRes = router.handle(ctx, new ParsedCommand("trace", "trace", List.of()));
        assertTrue(traceRes.success());
        assertTrue(traceRes.message().contains("pop") || traceRes.message().contains("empty"));
    }

    @Test
    public void testDirectOpsAndRunHaveSameEffects() {
        CommandRouter router = new CommandRouter();
        SessionCommands.registerAll(router);
        CommandContext ctx = new CommandContext(new InMemoryStructureRegistry(), new SessionManager());

        TracedArrayStack<Integer> tas = new TracedArrayStack<Integer>(new ArrayStack<Integer>(), new structlab.trace.TraceLog());
        ActiveStructureSession session = new ActiveStructureSession("struct-stack", "impl-array-stack", new StackRuntimeAdapter("", tas));
        ctx.sessionManager().startSession(session);

        // direct
        CommandResult r1 = router.handle(ctx, new ParsedCommand("push", "push", List.of("10")));
        assertEquals(1, session.getHistory().size());

        // via run
        CommandResult r2 = router.handle(ctx, new ParsedCommand("run", "run", List.of("push", "20")));
        assertEquals(2, session.getHistory().size());

        assertTrue(r1.success());
        assertTrue(r2.success());
    }

    @Test
    public void testResetClearsStructureAndHistory() {
        CommandRouter router = new CommandRouter();
        SessionCommands.registerAll(router);
        CommandContext ctx = new CommandContext(new InMemoryStructureRegistry(), new SessionManager());

        TracedArrayStack<Integer> tas = new TracedArrayStack<Integer>(new ArrayStack<Integer>(), new structlab.trace.TraceLog());
        ActiveStructureSession session = new ActiveStructureSession("struct-stack", "impl-array-stack", new StackRuntimeAdapter("", tas));
        ctx.sessionManager().startSession(session);

        router.handle(ctx, new ParsedCommand("push", "push", List.of("10")));
        assertEquals(1, session.getHistory().size());
        assertTrue(session.getRuntime().renderCurrentState().contains("10"));

        CommandResult resetRes = router.handle(ctx, new ParsedCommand("reset", "reset", List.of()));
        assertTrue(resetRes.success());
        assertEquals(0, session.getHistory().size());
        assertTrue(session.getRuntime().renderCurrentState().contains("empty"));
    }
}

