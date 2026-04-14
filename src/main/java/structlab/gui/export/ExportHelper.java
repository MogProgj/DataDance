package structlab.gui.export;

import structlab.app.comparison.ComparisonAnalysis;
import structlab.app.comparison.ComparisonEntryResult;
import structlab.app.comparison.ComparisonOperationResult;
import structlab.gui.ActivityLog;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.StringJoiner;

/**
 * Stateless helpers for exporting application data to plain-text / Markdown format.
 * JSON export is built using simple string formatting — no external JSON library needed.
 */
public final class ExportHelper {

    private static final DateTimeFormatter TIMESTAMP_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private ExportHelper() {}

    // ── Compare history ────────────────────────────────────────

    public static String compareHistoryToText(String structureName, List<ComparisonOperationResult> history) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Compare History — ").append(structureName).append("\n\n");
        for (int i = 0; i < history.size(); i++) {
            ComparisonOperationResult op = history.get(i);
            ComparisonAnalysis analysis = ComparisonAnalysis.of(op);
            sb.append("## ").append(i + 1).append(". ").append(op.operationName());
            if (!op.args().isEmpty()) sb.append(" ").append(String.join(" ", op.args()));
            sb.append("  [").append(analysis.getVerdict()).append("]\n");
            for (ComparisonEntryResult er : op.entryResults()) {
                sb.append("  - ").append(er.implementationName());
                sb.append(er.success() ? " OK" : " FAIL");
                if (er.returnedValue() != null) sb.append(" → ").append(er.returnedValue());
                sb.append("  (").append(er.formattedDuration()).append(")");
                sb.append("\n");
            }
            if (analysis.hasDivergences()) {
                for (ComparisonAnalysis.Divergence d : analysis.getDivergences()) {
                    sb.append("  ⚠ ").append(d.type()).append(": ").append(d.detail()).append("\n");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static String compareHistoryToJson(String structureName, List<ComparisonOperationResult> history) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"structure\": ").append(jsonStr(structureName));
        sb.append(",\n  \"operations\": [\n");
        for (int i = 0; i < history.size(); i++) {
            ComparisonOperationResult op = history.get(i);
            ComparisonAnalysis analysis = ComparisonAnalysis.of(op);
            sb.append("    {\n");
            sb.append("      \"operation\": ").append(jsonStr(op.operationName())).append(",\n");
            sb.append("      \"args\": [").append(String.join(", ", op.args().stream().map(ExportHelper::jsonStr).toList())).append("],\n");
            sb.append("      \"verdict\": ").append(jsonStr(analysis.getVerdict().name())).append(",\n");
            sb.append("      \"entries\": [\n");
            for (int j = 0; j < op.entryResults().size(); j++) {
                ComparisonEntryResult er = op.entryResults().get(j);
                sb.append("        {");
                sb.append("\"impl\": ").append(jsonStr(er.implementationName()));
                sb.append(", \"success\": ").append(er.success());
                sb.append(", \"returned\": ").append(jsonStr(er.returnedValue()));
                sb.append(", \"durationNanos\": ").append(er.durationNanos());
                sb.append("}");
                if (j < op.entryResults().size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("      ]\n    }");
            if (i < history.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n}");
        return sb.toString();
    }

    // ── Activity feed ──────────────────────────────────────────

    public static String activityToText(List<ActivityLog.Entry> entries) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Activity Log\n\n");
        for (ActivityLog.Entry e : entries) {
            sb.append("- [").append(e.timestamp().format(TIMESTAMP_FMT)).append("] ");
            sb.append("[").append(e.category()).append("] ");
            sb.append(e.action());
            if (!e.detail().isEmpty()) sb.append(" — ").append(e.detail());
            sb.append("\n");
        }
        return sb.toString();
    }

    public static String activityToJson(List<ActivityLog.Entry> entries) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < entries.size(); i++) {
            ActivityLog.Entry e = entries.get(i);
            sb.append("  {");
            sb.append("\"timestamp\": ").append(jsonStr(e.timestamp().format(TIMESTAMP_FMT)));
            sb.append(", \"category\": ").append(jsonStr(e.category()));
            sb.append(", \"action\": ").append(jsonStr(e.action()));
            sb.append(", \"detail\": ").append(jsonStr(e.detail()));
            sb.append("}");
            if (i < entries.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }

    // ── Utility ────────────────────────────────────────────────

    private static String jsonStr(String s) {
        if (s == null) return "null";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"")
                       .replace("\n", "\\n").replace("\r", "\\r")
                       .replace("\t", "\\t") + "\"";
    }
}
