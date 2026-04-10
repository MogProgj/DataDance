package structlab.app.comparison;

import structlab.trace.TraceStep;

import java.util.List;

/**
 * The result of executing one operation on one implementation within a comparison.
 */
public record ComparisonEntryResult(
    String implementationId,
    String implementationName,
    boolean success,
    String operationName,
    String message,
    String returnedValue,
    String stateAfter,
    List<TraceStep> traceSteps
) {}
