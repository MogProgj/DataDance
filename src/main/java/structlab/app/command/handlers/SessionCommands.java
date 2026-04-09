package structlab.app.command.handlers;

import structlab.app.command.*;
import structlab.app.runtime.OperationExecutionResult;
import structlab.app.runtime.RuntimeFactory;
import structlab.app.runtime.StructureRuntime;
import structlab.app.session.ActiveStructureSession;
import structlab.app.ui.TerminalFormatter;
import structlab.registry.ImplementationMetadata;
import structlab.registry.StructureMetadata;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SessionCommands {

    public static void registerAll(CommandRouter router) {

        CommandHandler openHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                if (!command.hasArgs() || command.arguments().size() < 2) {
                    return CommandResult.error("Usage Error", "Usage: open <structure-id> <implementation-id>");
                }

                String sId = command.arguments().get(0);
                String iId = command.arguments().get(1);

                return openSession(context, sId, iId);
            }
            @Override
            public String getDescription() { return "Start a live interactive session (e.g., open struct-stack impl-array-stack)"; }
        };
        router.register("open", openHandler);
        router.register("use", openHandler);
        router.register("start", openHandler);
        router.register("play", openHandler);

        CommandHandler closeHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                if (context.sessionManager().getActiveStructureSession().isPresent()) {
                    context.sessionManager().clearSession();
                    return CommandResult.success("Session Closed", "Closed active session. Returning to catalog viewer.");
                } else {
                    return CommandResult.ok("No active session to close.");
                }
            }
            @Override
            public String getDescription() { return "Close the active interactive session"; }
        };
        router.register("close", closeHandler);
        router.register("back", closeHandler);

        CommandHandler runHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                if (context.sessionManager().getActiveStructureSession().isEmpty()) {
                    return CommandResult.error("Session Error", "You must 'open' a session before running operations.");
                }
                if (!command.hasArgs()) {
                    return CommandResult.error("Usage Error", "Usage: run <operation> [args]");
                }

                String op = command.arguments().get(0);
                List<String> opArgs = command.arguments().subList(1, command.arguments().size());

                ActiveStructureSession ass = context.sessionManager().getActiveStructureSession().get();
                return executeAndFormatOperation(ass, op, opArgs);
            }
            @Override
            public String getDescription() { return "Run an operation explicitly inside an active session (e.g., run push 10)"; }
        };
        router.register("run", runHandler);
        router.register("do", runHandler);

        CommandHandler stateHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                if (context.sessionManager().getActiveStructureSession().isEmpty()) {
                    return CommandResult.error("Session Error", "You must 'open' a session first.");
                }
                ActiveStructureSession ass = context.sessionManager().getActiveStructureSession().get();
                return CommandResult.success("Current State", ass.getRuntime().renderCurrentState());
            }
            @Override
            public String getDescription() { return "Print the current physical state of the live data structure"; }
        };
        router.register("state", stateHandler);
        router.register("snapshot", stateHandler);

        CommandHandler historyHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                if (context.sessionManager().getActiveStructureSession().isEmpty()) {
                    return CommandResult.error("Session Error", "You must 'open' a session first.");
                }
                ActiveStructureSession ass = context.sessionManager().getActiveStructureSession().get();
                var hist = ass.getHistory();
                if (hist.isEmpty()) {
                    return CommandResult.success("History", "No operations executed yet.");
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < hist.size(); i++) {
                    var r = hist.get(i);
                    String status = r.success() ? "[OK]" : "[FAIL]";
                    sb.append(String.format("%s [%d] %s", status, i + 1, r.operationName()));
                    if (r.success() && r.returnedValue() != null && !r.returnedValue().equals("null")) {
                        sb.append(" -> ").append(r.returnedValue());
                    } else if (!r.success()) {
                        sb.append(" -> ").append(r.message());
                    }
                    sb.append("\n");
                }
                return CommandResult.success("Execution Timeline", sb.toString().trim());
            }
            @Override
            public String getDescription() { return "View the timeline of all executed operations"; }
        };
        router.register("history", historyHandler);
        router.register("log", historyHandler);

        CommandHandler traceHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                if (context.sessionManager().getActiveStructureSession().isEmpty()) {
                    return CommandResult.error("Session Error", "You must 'open' a session first.");
                }
                ActiveStructureSession ass = context.sessionManager().getActiveStructureSession().get();
                var lastResultOpt = ass.getLastResult();
                if (lastResultOpt.isEmpty()) {
                    return CommandResult.success("Trace", "No traces available.");
                }
                var lastResult = lastResultOpt.get();
                if (lastResult.traceSteps() == null || lastResult.traceSteps().isEmpty()) {
                    return CommandResult.success("Trace required", "No rigorous trace steps captured for last operation.");
                }

                StringBuilder sb = new StringBuilder();
                for (var step : lastResult.traceSteps()) {
                    sb.append(structlab.render.ConsoleTraceRenderer.render(step)).append("\n");
                }
                return CommandResult.plain(sb.toString().trim());
            }
            @Override
            public String getDescription() { return "Print the full internal trace of the last executed operation"; }
        };
        router.register("trace", traceHandler);

        CommandHandler resetHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                if (context.sessionManager().getActiveStructureSession().isEmpty()) {
                    return CommandResult.error("Session Error", "You must 'open' a session first.");
                }
                ActiveStructureSession ass = context.sessionManager().getActiveStructureSession().get();
                ass.getRuntime().reset();
                ass.clearHistory();

                StringBuilder sb = new StringBuilder();
                sb.append("Live structure has been reset to an empty state.");
                sb.append("\n\nCurrent State:\n");
                sb.append(ass.getRuntime().renderCurrentState());

                return CommandResult.success("Reset Successful", sb.toString());
            }
            @Override
            public String getDescription() { return "Reset the live structure to its initial empty state and clear history"; }
        };
        router.register("reset", resetHandler);
        router.register("wipe", resetHandler);

        CommandHandler sessionHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                if (context.sessionManager().getActiveStructureSession().isEmpty()) {
                    return CommandResult.error("Session Error", "No active session.");
                }
                ActiveStructureSession ass = context.sessionManager().getActiveStructureSession().get();
                StringBuilder sb = new StringBuilder();
                sb.append("Active Structure: ").append(ass.getStructureId()).append("\n");
                sb.append("Implementation:   ").append(ass.getImplementationId()).append("\n");
                sb.append("Operations count: ").append(ass.historySize()).append("\n");
                return CommandResult.success("Session Info", sb.toString().trim());
            }
            @Override
            public String getDescription() { return "Shows info about the current session"; }
        };
        router.register("session", sessionHandler);

        CommandHandler lastHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                if (context.sessionManager().getActiveStructureSession().isEmpty()) {
                    return CommandResult.error("Session Error", "No active session.");
                }
                ActiveStructureSession ass = context.sessionManager().getActiveStructureSession().get();
                var lastOpt = ass.getLastResult();
                if (lastOpt.isEmpty()) {
                    return CommandResult.success("Last Result", "No operations executed yet.");
                }
                var last = lastOpt.get();
                java.util.Map<String, String> info = new java.util.LinkedHashMap<>();
                info.put("Operation", last.operationName());
                info.put("Status", last.success() ? "Success" : "Failed");
                if (last.returnedValue() != null && !last.returnedValue().equals("null")) {
                    info.put("Returned", last.returnedValue());
                }
                if (last.message() != null) {
                    info.put("Message", last.message());
                }
                int steps = last.traceSteps() == null ? 0 : last.traceSteps().size();
                info.put("Trace Steps", steps > 0 ? steps + " steps recorded" : "None");
                StringBuilder sb = new StringBuilder();
                info.forEach((k, v) -> sb.append(k).append(": ").append(v).append("\n"));
                return CommandResult.success("Last Operation Summary", sb.toString().trim());
            }
            @Override
            public String getDescription() { return "Shows summary of the most recently executed operation"; }
        };
        router.register("last", lastHandler);

        CommandHandler opsHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                if (context.sessionManager().getActiveStructureSession().isEmpty()) {
                    return CommandResult.error("Session Error", "No active session.");
                }
                ActiveStructureSession ass = context.sessionManager().getActiveStructureSession().get();
                return CommandResult.success("Available Operations", getOpsBlock(ass.getRuntime()));
            }
            @Override
            public String getDescription() { return "View all available structure operations for the active session"; }
        };
        router.register("ops", opsHandler);
        router.register("operations", opsHandler);
    }

    public static CommandResult executeAndFormatOperation(ActiveStructureSession ass, String operation, List<String> args) {
        OperationExecutionResult result = ass.getRuntime().execute(operation, args);
        ass.addHistory(result);

        StringBuilder sb = new StringBuilder();
        sb.append(result.message()).append("\n");
        if (result.success() && result.returnedValue() != null) {
            sb.append("\nReturned: ").append(result.returnedValue());
        }
        sb.append("\n\nCurrent State:\n").append(ass.getRuntime().renderCurrentState());

        if (result.success()) {
            return CommandResult.success("Operation Success", sb.toString());
        } else {
            return CommandResult.error("Operation Failed", sb.toString());
        }
    }

    private static String getOpsBlock(StructureRuntime runtime) {
        StringBuilder sb = new StringBuilder();
        for (structlab.app.runtime.OperationDescriptor o : runtime.getAvailableOperations()) {
            sb.append(structlab.app.ui.TerminalTheme.GREEN).append(o.name()).append(structlab.app.ui.TerminalTheme.RESET);
            if (o.aliases() != null && !o.aliases().isEmpty()) {
                sb.append(" (").append(String.join(", ", o.aliases())).append(")");
            }
            sb.append("\n");
            sb.append("  Usage:       ").append(o.usage()).append("\n");
            sb.append("  Complexity:  ").append(o.complexityNote()).append("\n");
            sb.append("  Mutates:     ").append(o.mutates() ? "Yes" : "No").append("\n");
            sb.append("  Description: ").append(o.description()).append("\n\n");
        }
        return sb.toString().trim();
    }

    private static CommandResult openSession(CommandContext context, String sId, String iId) {
        String targetSId = sId.startsWith("struct-") ? sId : "struct-" + sId;
        String targetIId = iId.startsWith("impl-") ? iId : "impl-" + iId;
        Optional<StructureMetadata> smOpt = context.registry().getStructureById(targetSId);
        if (smOpt.isEmpty()) {
            return CommandResult.error("Not Found", "Structure '" + targetSId + "' not found in registry.");
        }

        List<ImplementationMetadata> impls = context.registry().getImplementationsFor(targetSId);
        Optional<ImplementationMetadata> imOpt = impls.stream().filter(im -> im.id().equals(targetIId)).findFirst();

        if (imOpt.isEmpty()) {
            return CommandResult.error("Not Found", "Implementation '" + targetIId + "' not found under structure '" + targetSId + "'.");
        }

        try {
            StructureRuntime runtime = RuntimeFactory.createRuntime(smOpt.get(), imOpt.get());
            ActiveStructureSession session = new ActiveStructureSession(targetSId, targetIId, runtime);
            context.sessionManager().startSession(session);

            StringBuilder sb = new StringBuilder();
            sb.append("Started interactive session for ").append(imOpt.get().name()).append(".\n\n");
            sb.append("Available Operations:\n");
            sb.append(getOpsBlock(runtime));

            return CommandResult.success("Session Started", sb.toString());

        } catch (Exception e) {
            return CommandResult.error("Init Error", "Failed to initialize physical runtime adapter. " + e.getMessage());
        }
    }
}
