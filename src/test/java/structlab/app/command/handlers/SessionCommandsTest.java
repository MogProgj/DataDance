package structlab.app.command.handlers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import structlab.app.command.*;
import structlab.app.session.SessionManager;
import structlab.registry.InMemoryStructureRegistry;
import structlab.registry.StructureMetadata;
import structlab.registry.ImplementationMetadata;
import java.util.List;

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
        assertNull(openRes.title());
        assertTrue(openRes.message().contains("Session Started"));

        assertNotNull(ctx.sessionManager().getActiveStructureSession());

        CommandResult closeRes = router.handle(ctx, new ParsedCommand("close", "close", List.of()));
        assertTrue(closeRes.success());
        assertEquals("Session Closed", closeRes.title());

        assertTrue(ctx.sessionManager().getActiveSession().isEmpty());
    }
}
