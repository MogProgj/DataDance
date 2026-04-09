package structlab.app.runtime;

import java.util.List;

public interface StructureRuntime {
    String getStructureName();
    String getImplementationName();

    List<OperationDescriptor> getAvailableOperations();

    OperationExecutionResult execute(String operation, List<String> args);

    String getCurrentState();

    String renderCurrentState();

    void clearTraceHistory();

    void reset();
}
