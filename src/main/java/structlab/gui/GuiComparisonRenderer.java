package structlab.gui;

import structlab.app.comparison.ComparisonEntryResult;
import structlab.app.comparison.ComparisonOperationResult;
import structlab.app.comparison.ComparisonRuntimeEntry;
import structlab.app.comparison.ComparisonSession;
import structlab.trace.TraceStep;

import java.util.List;

/**
 * GUI-safe comparison renderer — produces plain text (no ANSI codes)
 * with a compact trace summary suitable for JavaFX TextAreas.
 */
public final class GuiComparisonRenderer {

    private static final String DIVIDER = "══════════════════════════════════════════";
    private static final String THIN_DIVIDER = "──────────────────────────────────────";

    private static final String CHECK = "✔";
    private static final String CROSS = "✖";
    private static final String WARN = "⚠";

    private GuiComparisonRenderer() {}

    // ── States ──────────────────────────────────────────────

    /**
     * Render a summary of all implementation states (no ANSI).
     */
    public static String renderStates(ComparisonSession session) {
        StringBuilder sb = new StringBuilder();
        sb.append(DIVIDER).append("\n");
        sb.append("  Comparison States\n");
        sb.append(DIVIDER).append("\n\n");

        List<ComparisonRuntimeEntry> entries = session.getEntries();
        for (int i = 0; i < entries.size(); i++) {
            ComparisonRuntimeEntry entry = entries.get(i);
            sb.append("  [").append(i + 1).append("] ").append(entry.getImplementationName()).append("\n");

            String state = entry.getRuntime().renderCurrentState();
            for (String line : state.split("\n")) {
                sb.append("    ").append(line).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // ── Compact Traces ──────────────────────────────────────

    /**
     * Render a compact trace summary for the last comparison operation.
     * Shows per-implementation: status icon, name, returned value, step count.
     */
    public static String renderCompactTraces(ComparisonSession session) {
        List<ComparisonOperationResult> history = session.getHistory();
        if (history.isEmpty()) {
            return "No comparison operations executed yet.";
        }

        ComparisonOperationResult last = history.get(history.size() - 1);
        StringBuilder sb = new StringBuilder();

        String argsStr = last.args().isEmpty() ? "" : " " + String.join(" ", last.args());
        sb.append(DIVIDER).append("\n");
        sb.append("  Trace: ").append(last.operationName()).append(argsStr).append("\n");
        sb.append(DIVIDER).append("\n\n");

        // Summary status
        if (last.allSucceeded()) {
            sb.append("  ").append(CHECK).append(" All implementations succeeded.\n\n");
        } else {
            long failCount = last.entryResults().stream().filter(e -> !e.success()).count();
            sb.append("  ").append(WARN).append(" ").append(failCount).append(" of ")
              .append(last.entryResults().size()).append(" implementations failed.\n\n");
        }

        // Check if returned values differ
        if (checkValuesDiffer(last)) {
            sb.append("  ").append(WARN).append(" Returned values differ across implementations.\n\n");
        }

        // Per-implementation compact row
        for (int i = 0; i < last.entryResults().size(); i++) {
            ComparisonEntryResult entry = last.entryResults().get(i);
            String icon = entry.success() ? CHECK : CROSS;

            sb.append("  ").append(icon).append(" [").append(i + 1).append("] ")
              .append(entry.implementationName());

            if (entry.success()) {
                if (entry.returnedValue() != null && !"null".equals(entry.returnedValue())) {
                    sb.append("  →  ").append(entry.returnedValue());
                }
                if (!entry.traceSteps().isEmpty()) {
                    sb.append("  (").append(entry.traceSteps().size()).append(" step")
                      .append(entry.traceSteps().size() == 1 ? "" : "s").append(")");
                }
            } else {
                sb.append("  —  ").append(entry.message());
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    // ── Full Operation Result ───────────────────────────────

    /**
     * Render a full comparison operation result without ANSI codes.
     */
    public static String renderOperationResult(ComparisonOperationResult result) {
        StringBuilder sb = new StringBuilder();

        String argsStr = result.args().isEmpty() ? "" : " " + String.join(" ", result.args());
        sb.append(DIVIDER).append("\n");
        sb.append("  Compare: ").append(result.operationName()).append(argsStr).append("\n");
        sb.append(DIVIDER).append("\n");

        if (result.allSucceeded()) {
            sb.append("  ").append(CHECK).append(" All implementations succeeded.\n");
        } else if (result.anyFailed()) {
            long failCount = result.entryResults().stream().filter(e -> !e.success()).count();
            sb.append("  ").append(WARN).append(" ").append(failCount).append(" of ")
              .append(result.entryResults().size()).append(" implementations failed.\n");
        }
        sb.append("\n");

        if (checkValuesDiffer(result)) {
            sb.append("  ").append(WARN).append(" Returned values differ across implementations.\n\n");
        }

        for (int i = 0; i < result.entryResults().size(); i++) {
            ComparisonEntryResult entry = result.entryResults().get(i);
            sb.append(renderEntryResult(entry, i + 1));
            if (i < result.entryResults().size() - 1) {
                sb.append("  ").append(THIN_DIVIDER).append("\n");
            }
        }

        return sb.toString();
    }

    private static String renderEntryResult(ComparisonEntryResult entry, int index) {
        StringBuilder sb = new StringBuilder();

        String icon = entry.success() ? CHECK : CROSS;

        sb.append("  ").append(icon).append(" [").append(index).append("] ")
          .append(entry.implementationName()).append("\n");

        if (entry.success()) {
            if (entry.returnedValue() != null && !"null".equals(entry.returnedValue())) {
                sb.append("    Returned: ").append(entry.returnedValue()).append("\n");
            }
        } else {
            sb.append("    Error: ").append(entry.message()).append("\n");
        }

        if (entry.stateAfter() != null && !entry.stateAfter().isBlank()) {
            sb.append("    State:\n");
            for (String line : entry.stateAfter().split("\n")) {
                sb.append("      ").append(line).append("\n");
            }
        }

        if (!entry.traceSteps().isEmpty()) {
            sb.append("    Trace: ").append(entry.traceSteps().size()).append(" step")
              .append(entry.traceSteps().size() == 1 ? "" : "s").append("\n");
        }

        return sb.toString();
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
