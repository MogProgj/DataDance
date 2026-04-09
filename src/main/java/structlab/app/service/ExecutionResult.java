package structlab.app.service;

import structlab.trace.TraceStep;

import java.util.List;

public record ExecutionResult(
    boolean success,
    String operationName,
    String message,
    String returnedValue,
    List<TraceStep> traceSteps
) {}
