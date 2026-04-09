package structlab.app.command.handlers;

import java.util.List;
import java.util.Optional;

import structlab.app.command.*;
import structlab.app.ui.TerminalFormatter;
import structlab.app.ui.TerminalTheme;
import structlab.registry.ImplementationMetadata;
import structlab.registry.StructureMetadata;

public class DiscoveryCommands {

    public static void registerAll(CommandRouter router) {

        // --- QUIT ---
        CommandHandler quitHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                return CommandResult.exit();
            }
            @Override
            public String getDescription() { return "Exit the StructLab simulator"; }
        };
        router.register("quit", quitHandler);
        router.register("exit", quitHandler);
        router.register("q", quitHandler);

        // --- CLEAR ---
        CommandHandler clearHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                return CommandResult.clear();
            }
            @Override
            public String getDescription() { return "Clear the terminal screen"; }
        };
        router.register("clear", clearHandler);
        router.register("cls", clearHandler);

        // --- LIST ---
        CommandHandler listHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                return getAllStructures(context);
            }
            @Override
            public String getDescription() { return "List all abstract structures in the registry"; }
        };
        router.register("list", listHandler);
        router.register("ls", listHandler);
        router.register("catalog", listHandler);

        // --- SEARCH ---
        CommandHandler searchHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                if (!command.hasArgs()) {
                    return CommandResult.error("Missing Argument", "Provide a keyword (e.g., 'search array')");
                }
                return searchStructures(context, command.arguments().get(0));
            }
            @Override
            public String getDescription() { return "Search available structures by keyword"; }
        };
        router.register("search", searchHandler);
        router.register("s", searchHandler);

        // --- INFO ---
        CommandHandler infoHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                if (!command.hasArgs()) {
                    return CommandResult.error("Missing Argument", "Specify a structure (e.g., 'info stack')");
                }
                return getStructureInfo(context, command.arguments().get(0));
            }
            @Override
            public String getDescription() { return "Display details and implementations for a structure"; }
        };
        router.register("info", infoHandler);
        router.register("i", infoHandler);

        // --- STATS ---
        CommandHandler statsHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                return getStats(context);
            }
            @Override
            public String getDescription() { return "Show registry loading statistics"; }
        };
        router.register("stats", statsHandler);

        // --- HELP ---
        CommandHandler helpHandler = new CommandHandler() {
            @Override
            public CommandResult execute(CommandContext context, ParsedCommand command) {
                return getHelp(router);
            }
            @Override
            public String getDescription() { return "Print this help manual"; }
        };
        router.register("help", helpHandler);
        router.register("?", helpHandler);
    }

    private static CommandResult getAllStructures(CommandContext ctx) {
        StringBuilder sb = new StringBuilder();
        List<StructureMetadata> allStructures = ctx.registry().getAllStructures();

        sb.append(String.format("%-20s %-30s %-20s\n", "CATEGORY", "NAME", "REGISTRY ID"));
        sb.append("-".repeat(70)).append("\n");

        allStructures.stream().sorted((a, b) -> a.category().compareTo(b.category())).forEach(structure -> {
            sb.append(String.format(TerminalTheme.YELLOW + "%-20s" + TerminalTheme.RESET + " %-30s " + TerminalTheme.GRAY + "%-20s" + TerminalTheme.RESET + "\n",
                "[" + structure.category() + "]", structure.name(), structure.id()));
        });

        return CommandResult.success("Available Core Data Structures", sb.toString());
    }

    private static CommandResult searchStructures(CommandContext ctx, String keyword) {
        List<StructureMetadata> results = ctx.registry().search(keyword);
        if (results.isEmpty()) {
            return CommandResult.error("No matches", "No structures found for: " + keyword);
        } else {
            StringBuilder sb = new StringBuilder();
            for (StructureMetadata structure : results) {
                sb.append(" \u25b6 ").append(structure.name()).append(" (").append(structure.id()).append(")\n");
            }
            return CommandResult.success("Search Results for '" + keyword + "'", sb.toString());
        }
    }

    private static CommandResult getStructureInfo(CommandContext ctx, String id) {
        String targetId = id.startsWith("struct-") ? id : "struct-" + id;
        Optional<StructureMetadata> opt = ctx.registry().getStructureById(targetId);
        if (opt.isEmpty()) {
            return CommandResult.error("Not Found", "Unknown structure: " + id, "Use 'ls' to see available structures.");
        }
        StructureMetadata meta = opt.get();
        StringBuilder sb = new StringBuilder();

        sb.append("Description: ").append(meta.description()).append("\n");
        sb.append("Category:    ").append(meta.category()).append("\n");
        sb.append("Keywords:    ").append(String.join(", ", meta.keywords())).append("\n\n");

        sb.append("Available Implementations:\n");

        List<ImplementationMetadata> impls = ctx.registry().getImplementationsFor(targetId);
        for (ImplementationMetadata impl : impls) {
            sb.append("\n  \u25A0 ").append(impl.name()).append(" [").append(impl.id()).append("]\n");
            sb.append("    ").append(impl.description()).append("\n");

            sb.append("    [ Complexity ]\n");
            sb.append("      Space: ").append(impl.spaceComplexity()).append("\n");
            sb.append("      Time:  ");

            StringBuilder timeRules = new StringBuilder();
            impl.timeComplexity().forEach((op, cost) -> timeRules.append(String.format("%s = %s, ", op, cost)));
            if (timeRules.length() > 0) timeRules.setLength(timeRules.length() - 2);
            sb.append(timeRules.toString()).append("\n");
        }
        return CommandResult.success("Structure Profile: " + meta.name(), sb.toString());
    }

    private static CommandResult getStats(CommandContext ctx) {
        List<StructureMetadata> all = ctx.registry().getAllStructures();
        int totalImpls = 0;
        for (StructureMetadata meta : all) {
            totalImpls += ctx.registry().getImplementationsFor(meta.id()).size();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Abstract Structures Registered: ").append(all.size()).append("\n");
        sb.append("Concrete Implementations:       ").append(totalImpls).append("\n");

        return CommandResult.success("Registry Statistics", sb.toString());
    }

    private static CommandResult getHelp(CommandRouter router) {
        StringBuilder sb = new StringBuilder();

        // Group by semantic mapping
        sb.append(TerminalTheme.BOLD).append("Discovery Commands:").append(TerminalTheme.RESET).append("\n");
        appendCmd(sb, "list, ls, catalog", "List all structures in the registry");
        appendCmd(sb, "search <s>, s <s>", "Search registry by keyword");
        appendCmd(sb, "info <i>, i <i>", "Get metadata for a structure");
        appendCmd(sb, "stats", "View registry size");

        sb.append("\n").append(TerminalTheme.BOLD).append("Active Session Commands:").append(TerminalTheme.RESET).append("\n");
        appendCmd(sb, "session", "View environment details for the active mounted structure");
        appendCmd(sb, "ops, operations", "View legal operations & complexities");
        appendCmd(sb, "state, snapshot", "View the current visual state of the structure");
        appendCmd(sb, "history, log", "View operation history and count");
        appendCmd(sb, "last", "Display detailed logs for the newest state change");
        appendCmd(sb, "trace", "Step-by-step memory trace of the last executed operation");
        appendCmd(sb, "reset, wipe", "Clear history timeline (Memory usage reset)");
        appendCmd(sb, "close, back", "Dismount current session and return to discovery mode");

        sb.append("\n").append(TerminalTheme.BOLD).append("Shell Controls:").append(TerminalTheme.RESET).append("\n");
        appendCmd(sb, "open, use, play, start", "Mount an implementation and begin simulating (e.g., 'open stack impl-array-stack')");
        appendCmd(sb, "run, do", "Run a structure operation explicitly (e.g., 'run push 10')");
        appendCmd(sb, "<operation> [args]", "Execute a legal operation directly when a session is active (e.g., 'push 10')");
        appendCmd(sb, "quit, exit, q", "Gracefully terminate shell simulation");
        appendCmd(sb, "clear, cls", "Clear visual terminal output");

        appendCmd(sb, "help, ?", "Render this manual");

        return CommandResult.success("Command Reference Manual", sb.toString());
    }

    private static void appendCmd(StringBuilder sb, String names, String desc) {
        sb.append(String.format("  " + TerminalTheme.GREEN + "%-20s" + TerminalTheme.RESET + " - %s\n", names, desc));
    }
}
