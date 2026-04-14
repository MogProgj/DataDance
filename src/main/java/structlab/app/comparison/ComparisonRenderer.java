package structlab.app.comparison;

import structlab.app.ui.TerminalTheme;
import structlab.render.ConsoleTraceRenderer;
import structlab.trace.TraceStep;

import java.util.List;

/**
 * Renders comparison results in a readable terminal format.
 */
public class ComparisonRenderer {

    private ComparisonRenderer() {}

    /**
     * Render a full comparison operation result with side-by-side sections.
     */
    public static String renderOperationResult(ComparisonOperationResult result) {
        StringBuilder sb = new StringBuilder();

        // Header
        String argsStr = result.args().isEmpty() ? "" : " " + String.join(" ", result.args());
        sb.append(TerminalTheme.BOLD).append(TerminalTheme.CYAN)
          .append("══ Compare: ").append(result.operationName()).append(argsStr)
          .append(" ══").append(TerminalTheme.RESET).append("\n");

        if (result.anyFailed() && result.allSucceeded()) {
            // impossible but safe
        } else if (result.allSucceeded()) {
            sb.append(TerminalTheme.GREEN).append("  All implementations succeeded.")
              .append(TerminalTheme.RESET).append("\n");
        } else if (result.anyFailed()) {
            long failCount = result.entryResults().stream().filter(e -> !e.success()).count();
            sb.append(TerminalTheme.YELLOW).append("  ").append(failCount).append(" of ")
              .append(result.entryResults().size()).append(" implementations failed.")
              .append(TerminalTheme.RESET).append("\n");
        }
        sb.append("\n");

        // Check if returned values differ
        boolean valuesDiffer = checkValuesDiffer(result);
        if (valuesDiffer) {
            sb.append(TerminalTheme.YELLOW).append("  ⚠ Returned values differ across implementations.")
              .append(TerminalTheme.RESET).append("\n\n");
        }

        // Per-implementation sections
        for (int i = 0; i < result.entryResults().size(); i++) {
            ComparisonEntryResult entry = result.entryResults().get(i);
            sb.append(renderEntryResult(entry, i + 1));
            if (i < result.entryResults().size() - 1) {
                sb.append(TerminalTheme.GRAY).append("  ──────────────────────────────────────")
                  .append(TerminalTheme.RESET).append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * Render a single implementation's result within a comparison.
     */
    private static String renderEntryResult(ComparisonEntryResult entry, int index) {
        StringBuilder sb = new StringBuilder();

        String statusIcon = entry.success()
                ? TerminalTheme.GREEN + TerminalTheme.CHECK
                : TerminalTheme.RED + TerminalTheme.CROSS;

        sb.append("  ").append(statusIcon).append(TerminalTheme.RESET).append(" ")
          .append(TerminalTheme.BOLD).append("[").append(index).append("] ")
          .append(entry.implementationName())
          .append(TerminalTheme.RESET).append("\n");

        if (entry.success()) {
            if (entry.returnedValue() != null && !"null".equals(entry.returnedValue())) {
                sb.append("    Returned: ").append(TerminalTheme.CYAN)
                  .append(entry.returnedValue()).append(TerminalTheme.RESET).append("\n");
            }
        } else {
            sb.append("    ").append(TerminalTheme.RED).append("Error: ")
              .append(entry.message()).append(TerminalTheme.RESET).append("\n");
        }

        // State after
        if (entry.stateAfter() != null && !entry.stateAfter().isBlank()) {
            sb.append("    State:\n");
            for (String line : entry.stateAfter().split("\n")) {
                sb.append("      ").append(line).append("\n");
            }
        }

        // Trace summary (not full dump — just step count and first/last)
        if (!entry.traceSteps().isEmpty()) {
            int count = entry.traceSteps().size();
            sb.append("    Trace: ").append(count).append(" step")
              .append(count == 1 ? "" : "s").append("\n");
        }

        return sb.toString();
    }

    /**
     * Render a summary of all implementation states.
     */
    public static String renderStates(ComparisonSession session) {
        StringBuilder sb = new StringBuilder();
        sb.append(TerminalTheme.BOLD).append(TerminalTheme.CYAN)
          .append("══ Comparison States ══").append(TerminalTheme.RESET).append("\n\n");

        List<ComparisonRuntimeEntry> entries = session.getEntries();
        for (int i = 0; i < entries.size(); i++) {
            ComparisonRuntimeEntry entry = entries.get(i);
            sb.append("  ").append(TerminalTheme.BOLD).append("[").append(i + 1).append("] ")
              .append(entry.getImplementationName()).append(TerminalTheme.RESET).append("\n");

            String state = entry.getRuntime().renderCurrentState();
            for (String line : state.split("\n")) {
                sb.append("    ").append(line).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Render the trace for the last operation for all implementations.
     */
    public static String renderTraces(ComparisonSession session) {
        List<ComparisonOperationResult> history = session.getHistory();
        if (history.isEmpty()) {
            return "No comparison operations executed yet.";
        }

        ComparisonOperationResult last = history.get(history.size() - 1);
        StringBuilder sb = new StringBuilder();
        sb.append(TerminalTheme.BOLD).append(TerminalTheme.CYAN)
          .append("══ Comparison Traces (last operation: ")
          .append(last.operationName()).append(") ══")
          .append(TerminalTheme.RESET).append("\n\n");

        for (int i = 0; i < last.entryResults().size(); i++) {
            ComparisonEntryResult entry = last.entryResults().get(i);
            sb.append("  ").append(TerminalTheme.BOLD).append("[").append(i + 1).append("] ")
              .append(entry.implementationName()).append(TerminalTheme.RESET).append("\n");

            if (entry.traceSteps().isEmpty()) {
                sb.append("    (no trace steps)\n");
            } else {
                for (TraceStep step : entry.traceSteps()) {
                    String rendered = ConsoleTraceRenderer.render(step);
                    for (String line : rendered.split("\n")) {
                        sb.append("    ").append(line).append("\n");
                    }
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Render the comparison session info.
     */
    public static String renderSessionInfo(ComparisonSession session) {
        StringBuilder sb = new StringBuilder();
        sb.append("Structure:       ").append(session.getStructureName())
          .append(" (").append(session.getStructureId()).append(")\n");
        sb.append("Implementations: ").append(session.entryCount()).append("\n");
        for (int i = 0; i < session.getEntries().size(); i++) {
            ComparisonRuntimeEntry entry = session.getEntries().get(i);
            sb.append("  [").append(i + 1).append("] ").append(entry.getImplementationName())
              .append(" (").append(entry.getImplementationId()).append(")\n");
        }
        sb.append("Operations:      ").append(session.historySize());
        return sb.toString();
    }

    /**
     * Render comparison history.
     */
    public static String renderHistory(ComparisonSession session) {
        List<ComparisonOperationResult> history = session.getHistory();
        if (history.isEmpty()) {
            return "No comparison operations executed yet.";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < history.size(); i++) {
            ComparisonOperationResult op = history.get(i);
            String status = op.allSucceeded() ? "[OK]" : "[PARTIAL]";
            String argsStr = op.args().isEmpty() ? "" : " " + String.join(" ", op.args());
            sb.append(String.format("%s [%d] %s%s", status, i + 1, op.operationName(), argsStr));
            sb.append("\n");
        }
        return sb.toString().trim();
    }

    private static boolean checkValuesDiffer(ComparisonOperationResult result) {
        List<String> values = result.entryResults().stream()
                .filter(ComparisonEntryResult::success)
                .map(ComparisonEntryResult::returnedValue)
                .toList();
        if (values.size() <= 1) return false;
        String first = values.get(0);
        return values.stream().anyMatch(v -> {
            if (first == null) return v != null;
            return !first.equals(v);
        });
    }
}
