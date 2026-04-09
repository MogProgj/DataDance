package structlab.app.runtime;

import structlab.trace.TraceStep;

import java.util.List;

public record OperationExecutionResult(
    boolean success,
    String message,
    String operationName,
    String returnedValue,
    List<TraceStep> traceSteps
) {}
