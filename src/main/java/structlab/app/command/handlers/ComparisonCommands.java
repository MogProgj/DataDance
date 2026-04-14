package structlab.app.command.handlers;

import structlab.app.command.*;
import structlab.app.comparison.*;
import structlab.app.runtime.OperationDescriptor;
import structlab.app.runtime.RuntimeFactory;
import structlab.app.runtime.StructureRuntime;
import structlab.app.ui.TerminalTheme;
import structlab.registry.ImplementationMetadata;
import structlab.registry.StructureMetadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Terminal commands for comparison mode.
 */
public class ComparisonCommands {

    public static void registerAll(CommandRouter router) {

        // ── compare <structure-id> ─────────────────────────────
        CommandHandler compareHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                if (!command.hasArgs()) {
                    return listComparableStructures(context);
                }

                String structureId = command.arguments().get(0);
                List<String> implIds = command.arguments().size() > 1
                        ? command.arguments().subList(1, command.arguments().size())
                        : List.of();

                return openComparisonSession(context, structureId, implIds);
            }

            @Override
            public String getDescription() {
                return "Open comparison mode (e.g., compare stack, compare queue impl-linked-queue impl-two-stack-queue)";
            }
        };
        router.register("compare", compareHandler);
        router.register("cmp", compareHandler);

        // ── compare-ops ────────────────────────────────────────
        CommandHandler cmpOpsHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                Optional<ComparisonSession> csOpt = context.sessionManager().getComparisonSession();
                if (csOpt.isEmpty()) {
                    return CommandResult.error("Not in comparison mode", "Open a comparison session first with 'compare <structure>'.");
                }

                ComparisonSession cs = csOpt.get();
                List<OperationDescriptor> ops = cs.getCommonOperations();
                if (ops.isEmpty()) {
                    return CommandResult.success("Common Operations", "No common operations across all implementations.");
                }

                StringBuilder sb = new StringBuilder();
                for (OperationDescriptor o : ops) {
                    sb.append(TerminalTheme.GREEN).append(o.name()).append(TerminalTheme.RESET);
                    if (o.aliases() != null && !o.aliases().isEmpty()) {
                        sb.append(" (").append(String.join(", ", o.aliases())).append(")");
                    }
                    sb.append("\n");
                    sb.append("  Usage:       ").append(o.usage()).append("\n");
                    sb.append("  Description: ").append(o.description()).append("\n\n");
                }
                return CommandResult.success("Common Operations (" + ops.size() + ")", sb.toString().trim());
            }

            @Override
            public String getDescription() {
                return "List operations common to all implementations in comparison mode";
            }
        };
        router.register("compare-ops", cmpOpsHandler);
        router.register("cmp-ops", cmpOpsHandler);

        // ── compare-state ──────────────────────────────────────
        CommandHandler cmpStateHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                Optional<ComparisonSession> csOpt = context.sessionManager().getComparisonSession();
                if (csOpt.isEmpty()) {
                    return CommandResult.error("Not in comparison mode", "Open a comparison session first with 'compare <structure>'.");
                }
                return CommandResult.plain(ComparisonRenderer.renderStates(csOpt.get()));
            }

            @Override
            public String getDescription() {
                return "Show the current state of all implementations side-by-side";
            }
        };
        router.register("compare-state", cmpStateHandler);
        router.register("cmp-state", cmpStateHandler);

        // ── compare-trace ──────────────────────────────────────
        CommandHandler cmpTraceHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                Optional<ComparisonSession> csOpt = context.sessionManager().getComparisonSession();
                if (csOpt.isEmpty()) {
                    return CommandResult.error("Not in comparison mode", "Open a comparison session first with 'compare <structure>'.");
                }
                return CommandResult.plain(ComparisonRenderer.renderTraces(csOpt.get()));
            }

            @Override
            public String getDescription() {
                return "Show trace output from the last operation for all implementations";
            }
        };
        router.register("compare-trace", cmpTraceHandler);
        router.register("cmp-trace", cmpTraceHandler);

        // ── compare-history ────────────────────────────────────
        CommandHandler cmpHistoryHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                Optional<ComparisonSession> csOpt = context.sessionManager().getComparisonSession();
                if (csOpt.isEmpty()) {
                    return CommandResult.error("Not in comparison mode", "Open a comparison session first with 'compare <structure>'.");
                }
                return CommandResult.success("Comparison History",
                        ComparisonRenderer.renderHistory(csOpt.get()));
            }

            @Override
            public String getDescription() {
                return "Show history of comparison operations";
            }
        };
        router.register("compare-history", cmpHistoryHandler);
        router.register("cmp-history", cmpHistoryHandler);

        // ── compare-session ────────────────────────────────────
        CommandHandler cmpSessionHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                Optional<ComparisonSession> csOpt = context.sessionManager().getComparisonSession();
                if (csOpt.isEmpty()) {
                    return CommandResult.error("Not in comparison mode", "Open a comparison session first with 'compare <structure>'.");
                }
                return CommandResult.success("Comparison Session",
                        ComparisonRenderer.renderSessionInfo(csOpt.get()));
            }

            @Override
            public String getDescription() {
                return "Show comparison session info";
            }
        };
        router.register("compare-session", cmpSessionHandler);
        router.register("cmp-session", cmpSessionHandler);

        // ── compare-reset ──────────────────────────────────────
        CommandHandler cmpResetHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                Optional<ComparisonSession> csOpt = context.sessionManager().getComparisonSession();
                if (csOpt.isEmpty()) {
                    return CommandResult.error("Not in comparison mode", "Open a comparison session first with 'compare <structure>'.");
                }
                csOpt.get().resetAll();
                return CommandResult.success("Comparison Reset",
                        "All implementations reset to empty state.\n\n" +
                        ComparisonRenderer.renderStates(csOpt.get()));
            }

            @Override
            public String getDescription() {
                return "Reset all implementations in comparison mode to empty";
            }
        };
        router.register("compare-reset", cmpResetHandler);
        router.register("cmp-reset", cmpResetHandler);
    }

    /**
     * Execute an operation across all implementations in comparison mode.
     * Used by CommandRouter's implicit op forwarding.
     */
    public static CommandResult executeComparisonOperation(ComparisonSession cs, String operation, List<String> args) {
        ComparisonOperationResult result = cs.executeAll(operation, args);
        return CommandResult.plain(ComparisonRenderer.renderOperationResult(result));
    }

    private static CommandResult listComparableStructures(CommandContext context) {
        List<StructureMetadata> all = context.registry().getAllStructures();
        StringBuilder sb = new StringBuilder();
        sb.append("Structure families with 2+ implementations:\n\n");
        boolean found = false;
        for (StructureMetadata sm : all) {
            List<ImplementationMetadata> impls = context.registry().getImplementationsFor(sm.id());
            if (impls.size() >= 2) {
                found = true;
                sb.append("  ").append(TerminalTheme.GREEN).append(sm.name())
                  .append(TerminalTheme.RESET)
                  .append(" (").append(sm.id().replace("struct-", "")).append(")")
                  .append(" — ").append(impls.size()).append(" implementations\n");
                for (ImplementationMetadata im : impls) {
                    sb.append("    ").append(TerminalTheme.GRAY).append("• ")
                      .append(im.name()).append(" [").append(im.id()).append("]")
                      .append(TerminalTheme.RESET).append("\n");
                }
                sb.append("\n");
            }
        }
        if (!found) {
            sb.append("  (no comparable structures found)\n");
        }

        sb.append("Usage: compare <structure-id>             — compare all implementations\n");
        sb.append("       compare <structure-id> <impl1> ... — compare selected implementations");

        return CommandResult.success("Comparison Mode", sb.toString());
    }

    private static CommandResult openComparisonSession(CommandContext context, String structureId, List<String> implIds) {
        String sId = structureId.startsWith("struct-") ? structureId : "struct-" + structureId;

        Optional<StructureMetadata> smOpt = context.registry().getStructureById(sId);
        if (smOpt.isEmpty()) {
            return CommandResult.error("Not Found", "Structure '" + sId + "' not found.",
                    "Use 'compare' without arguments to see eligible structures.");
        }

        List<ImplementationMetadata> allImpls = context.registry().getImplementationsFor(sId);
        if (allImpls.size() < 2) {
            return CommandResult.error("Not Enough Implementations",
                    "Structure '" + smOpt.get().name() + "' has only " + allImpls.size() + " implementation(s).",
                    "Comparison requires at least 2.");
        }

        List<ImplementationMetadata> selected;
        if (implIds.isEmpty()) {
            selected = allImpls;
        } else {
            selected = new ArrayList<>();
            for (String rawId : implIds) {
                String iId = rawId.startsWith("impl-") ? rawId : "impl-" + rawId;
                Optional<ImplementationMetadata> found = allImpls.stream()
                        .filter(im -> im.id().equals(iId))
                        .findFirst();
                if (found.isEmpty()) {
                    return CommandResult.error("Not Found",
                            "Implementation '" + iId + "' not found under '" + smOpt.get().name() + "'.");
                }
                selected.add(found.get());
            }
            if (selected.size() < 2) {
                return CommandResult.error("Not Enough", "Select at least 2 implementations for comparison.");
            }
        }

        try {
            List<ComparisonRuntimeEntry> entries = new ArrayList<>();
            for (ImplementationMetadata im : selected) {
                StructureRuntime runtime = RuntimeFactory.createRuntime(smOpt.get(), im);
                entries.add(new ComparisonRuntimeEntry(im.id(), im.name(), runtime));
            }

            ComparisonSession session = new ComparisonSession(sId, smOpt.get().name(), entries);
            context.sessionManager().startSession(session);

            StringBuilder sb = new StringBuilder();
            sb.append("Comparison session started for ").append(smOpt.get().name())
              .append(" (").append(entries.size()).append(" implementations).\n\n");

            sb.append("Participating implementations:\n");
            for (int i = 0; i < entries.size(); i++) {
                sb.append("  [").append(i + 1).append("] ").append(entries.get(i).getImplementationName())
                  .append("\n");
            }

            List<OperationDescriptor> commonOps = session.getCommonOperations();
            sb.append("\nCommon operations: ");
            sb.append(commonOps.stream()
                    .map(OperationDescriptor::name)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("none"));

            sb.append("\n\nRun operations directly (e.g., 'push 10') or use 'compare-ops' for details.");
            sb.append("\nType 'close' to leave comparison mode.");

            return CommandResult.success("Comparison Mode — " + smOpt.get().name(), sb.toString());

        } catch (Exception e) {
            return CommandResult.error("Init Error", "Failed to initialize comparison session: " + e.getMessage());
        }
    }
}
