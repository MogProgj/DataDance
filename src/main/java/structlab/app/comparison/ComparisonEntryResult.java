package structlab.app.comparison;

import structlab.trace.TraceStep;

import java.util.List;

/**
 * The result of executing one operation on one implementation within a comparison.
 *
 * @param durationNanos wall-clock execution time in nanoseconds (informational, not a rigorous benchmark)
 */
public record ComparisonEntryResult(
    String implementationId,
    String implementationName,
    boolean success,
    String operationName,
    String message,
    String returnedValue,
    String stateAfter,
    List<TraceStep> traceSteps,
    long durationNanos
) {
    /** Formats duration as a human-readable string (μs or ms). */
    public String formattedDuration() {
        if (durationNanos < 1_000_000) {
            return String.format("%.1f μs", durationNanos / 1_000.0);
        }
        return String.format("%.2f ms", durationNanos / 1_000_000.0);
    }
}
