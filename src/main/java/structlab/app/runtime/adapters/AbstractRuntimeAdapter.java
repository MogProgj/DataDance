package structlab.app.runtime.adapters;

import structlab.app.runtime.OperationDescriptor;
import structlab.app.runtime.OperationExecutionResult;
import structlab.app.runtime.StructureRuntime;
import structlab.trace.TraceLog;
import structlab.trace.TraceStep;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Base adapter managing common logic for parsing and returning generic operation results.
 */
public abstract class AbstractRuntimeAdapter implements StructureRuntime {

    private final String structureName;
    private final String implementationName;

    public AbstractRuntimeAdapter(String structureName, String implementationName) {
        this.structureName = structureName;
        this.implementationName = implementationName;
    }

    @Override
    public String getStructureName() {
        return structureName;
    }

    @Override
    public String getImplementationName() {
        return implementationName;
    }

    @Override
    public String renderCurrentState() {
        return structlab.render.StructureRenderer.render(getCurrentState());
    }

    protected OperationExecutionResult success(String operation, Object returnedValue, TraceLog traceLog) {
        List<TraceStep> steps = traceLog != null ? new ArrayList<>(traceLog.steps()) : new ArrayList<>();
        if (traceLog != null) {
            traceLog.clear();
        }
        return new OperationExecutionResult(
                true,
                "Executed " + operation + " successfully.",
                operation,
                returnedValue != null ? returnedValue.toString() : null,
                steps
        );
    }

    protected OperationExecutionResult error(String operation, Exception e, TraceLog traceLog) {
        List<TraceStep> steps = traceLog != null ? new ArrayList<>(traceLog.steps()) : new ArrayList<>();
        if (traceLog != null) {
            traceLog.clear();
        }
        return new OperationExecutionResult(
                false,
                e.getMessage(),
                operation,
                null,
                steps
        );
    }

    protected structlab.trace.TraceLog getTraceLogFrom(Object activeThing) { try { return (structlab.trace.TraceLog) activeThing.getClass().getMethod("traceLog").invoke(activeThing); } catch (Exception ignores) { return null; } }

    protected OperationExecutionResult error(String operation, Exception e) {
        return error(operation, e, null);
    }

    protected Integer parseArg(String arg) {
        return Integer.parseInt(arg.trim());
    }
}

