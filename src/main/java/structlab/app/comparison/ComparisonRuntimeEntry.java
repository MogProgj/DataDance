package structlab.app.comparison;

import structlab.app.runtime.StructureRuntime;

/**
 * One participating implementation inside a comparison session.
 */
public class ComparisonRuntimeEntry {

    private final String implementationId;
    private final String implementationName;
    private final StructureRuntime runtime;

    public ComparisonRuntimeEntry(String implementationId, String implementationName, StructureRuntime runtime) {
        this.implementationId = implementationId;
        this.implementationName = implementationName;
        this.runtime = runtime;
    }

    public String getImplementationId() { return implementationId; }
    public String getImplementationName() { return implementationName; }
    public StructureRuntime getRuntime() { return runtime; }
}
