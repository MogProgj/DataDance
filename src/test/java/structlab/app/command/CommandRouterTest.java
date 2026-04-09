package structlab.app.command;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import structlab.app.session.SessionManager;
import structlab.registry.InMemoryStructureRegistry;

public class CommandRouterTest {

    @Test
    public void testEmptyCommand() {
        CommandRouter router = new CommandRouter();
        CommandContext ctx = new CommandContext(new InMemoryStructureRegistry(), new SessionManager());

        CommandResult result = router.handle(ctx, new ParsedCommand("", "", List.of()));
        assertTrue(result.success());
        assertEquals("", result.message());
    }

    @Test
    public void testRegisterAndHandle() {
        CommandRouter router = new CommandRouter();
        CommandContext ctx = new CommandContext(new InMemoryStructureRegistry(), new SessionManager());

        router.register("ping", new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                return CommandResult.ok("pong");
            }
            @Override
            public String getDescription() {
                return "Responds with pong";
            }
        });

        assertTrue(router.getHandlers().containsKey("ping"));
        CommandResult result = router.handle(ctx, new ParsedCommand("ping", "ping", List.of()));
        assertTrue(result.success());
        assertEquals("pong", result.message());
    }

    @Test
    public void testUnknownCommandNoActiveSession() {
        CommandRouter router = new CommandRouter();
        CommandContext ctx = new CommandContext(new InMemoryStructureRegistry(), new SessionManager());

        CommandResult result = router.handle(ctx, new ParsedCommand("unknown", "unknown", List.of()));
        
        // Return an error result if it's unknown
        assertFalse(result.success());
        assertEquals("Unknown command", result.title());
    }

    @Test
    public void testHandlerExceptionIsCaught() {
        CommandRouter router = new CommandRouter();
        CommandContext ctx = new CommandContext(new InMemoryStructureRegistry(), new SessionManager());

        router.register("crash", new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                throw new RuntimeException("Simulated crash");
            }
            @Override
            public String getDescription() {
                return "Crashes the handler";
            }
        });

        CommandResult result = router.handle(ctx, new ParsedCommand("crash", "crash", List.of()));
        assertFalse(result.success());
        assertTrue(result.message().contains("Simulated crash"));
    }

    @Test
    public void testAliasAndCasingRegistration() {
        CommandRouter router = new CommandRouter();
        CommandContext ctx = new CommandContext(new InMemoryStructureRegistry(), new SessionManager());

        CommandHandler h = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                return CommandResult.ok("OK!");
            }
            @Override
            public String getDescription() { return "desc"; }
        };

        router.register("HELLO", h); // should be stored as hello

        CommandResult result = router.handle(ctx, new ParsedCommand("HELLO", "hello", List.of()));
        assertTrue(result.success());
        assertEquals("OK!", result.message());
    }
}
