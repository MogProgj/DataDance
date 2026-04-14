package structlab.app.command;

import java.util.HashMap;
import java.util.Map;

import structlab.app.comparison.ComparisonSession;
import structlab.app.ui.TerminalFormatter;
import structlab.app.session.ActiveStructureSession;
import structlab.app.runtime.OperationExecutionResult;

public class CommandRouter {
    private final Map<String, CommandHandler> handlers = new HashMap<>();

    public void register(String alias, CommandHandler handler) {
        handlers.put(alias.toLowerCase(), handler);
    }

    public CommandResult handle(CommandContext context, ParsedCommand parsed) {
        if (parsed.name().isEmpty()) {
            return CommandResult.ok(); // Ignore empty
        }

        CommandHandler handler = handlers.get(parsed.name());
        if (handler != null) {
            try {
                return handler.execute(context, parsed);
            } catch (Exception e) {
                return CommandResult.error("Unexpected error executing " + parsed.name() + ": " + e.getMessage());
            }
        }

        // Implicit operation forwarder for comparison sessions
        if (context.sessionManager().getComparisonSession().isPresent()) {
            ComparisonSession cs = context.sessionManager().getComparisonSession().get();
            var ops = cs.getCommonOperations();
            boolean isKnownOp = ops.stream().anyMatch(o ->
                    o.name().equalsIgnoreCase(parsed.name()) ||
                    o.aliases().contains(parsed.name().toLowerCase()));

            if (isKnownOp) {
                return structlab.app.command.handlers.ComparisonCommands.executeComparisonOperation(
                        cs, parsed.name(), parsed.arguments());
            }
        }

        // Implicit Operation forwarder for active sessions
        if (context.sessionManager().getActiveStructureSession().isPresent()) {
            ActiveStructureSession ass = context.sessionManager().getActiveStructureSession().get();
            var ops = ass.getRuntime().getAvailableOperations();
            boolean isKnownOp = ops.stream().anyMatch(o -> o.name().equalsIgnoreCase(parsed.name()) || o.aliases().contains(parsed.name().toLowerCase()));

            if (isKnownOp) {
                return structlab.app.command.handlers.SessionCommands.executeAndFormatOperation(ass, parsed.name(), parsed.arguments());
            }
        }

        return CommandResult.error("Unknown command", "Unknown command/operation: '" + parsed.name() + "'", "Try typing 'help' to see valid commands.");
    }

    public Map<String, CommandHandler> getHandlers() {
        return handlers;
    }
}
