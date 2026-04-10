package structlab.app.comparison;

import structlab.app.runtime.OperationDescriptor;
import structlab.app.runtime.OperationExecutionResult;
import structlab.app.runtime.StructureRuntime;
import structlab.app.session.StructureSession;
import structlab.trace.TraceStep;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * A comparison session holds multiple runtimes for the same structure family
 * and dispatches operations to all of them in parallel.
 */
public class ComparisonSession implements StructureSession {

    private final String structureId;
    private final String structureName;
    private final List<ComparisonRuntimeEntry> entries;
    private final List<ComparisonOperationResult> history;

    public ComparisonSession(String structureId, String structureName, List<ComparisonRuntimeEntry> entries) {
        if (entries == null || entries.size() < 2) {
            throw new IllegalArgumentException("Comparison mode requires at least 2 implementations.");
        }
        this.structureId = structureId;
        this.structureName = structureName;
        this.entries = List.copyOf(entries);
        this.history = new ArrayList<>();
    }

    @Override
    public String getStructureId() { return structureId; }

    @Override
    public String getImplementationId() { return "compare-all"; }

    @Override
    public void close() {
        // Nothing to clean up
    }

    public String getStructureName() { return structureName; }

    public List<ComparisonRuntimeEntry> getEntries() { return entries; }

    public int entryCount() { return entries.size(); }

    /**
     * Returns the common operations across all participating implementations.
     * Uses alias-aware matching: operations are considered equivalent if they share
     * the same canonical name or any known aliases. This prevents comparison mode
     * from collapsing to a minimal set when implementations use different naming
     * conventions (e.g., "insert" vs "enqueue" in the heap family).
     */
    public List<OperationDescriptor> getCommonOperations() {
        if (entries.isEmpty()) return List.of();

        // Start with the first runtime's operations
        List<OperationDescriptor> common = new ArrayList<>(entries.get(0).getRuntime().getAvailableOperations());

        // Retain only those that have a semantic match in every other runtime
        for (int i = 1; i < entries.size(); i++) {
            StructureRuntime runtime = entries.get(i).getRuntime();
            Set<String> allNames = collectAllNames(runtime);
            common.removeIf(op -> !matchesAny(op, allNames));
        }
        return Collections.unmodifiableList(common);
    }

    /**
     * Collects all operation names AND aliases from a runtime into a lower-case set.
     */
    private Set<String> collectAllNames(StructureRuntime runtime) {
        Set<String> names = new HashSet<>();
        for (OperationDescriptor op : runtime.getAvailableOperations()) {
            names.add(op.name().toLowerCase(Locale.ROOT));
            if (op.aliases() != null) {
                op.aliases().forEach(a -> names.add(a.toLowerCase(Locale.ROOT)));
            }
        }
        return names;
    }

    /**
     * Returns true if the operation's name or any of its aliases appear in the name set.
     */
    private boolean matchesAny(OperationDescriptor op, Set<String> nameSet) {
        if (nameSet.contains(op.name().toLowerCase(Locale.ROOT))) return true;
        if (op.aliases() != null) {
            for (String alias : op.aliases()) {
                if (nameSet.contains(alias.toLowerCase(Locale.ROOT))) return true;
            }
        }
        return false;
    }

    /**
     * Execute one operation across all participating runtimes.
     * Collects results even when some implementations fail.
     */
    public ComparisonOperationResult executeAll(String operation, List<String> args) {
        List<ComparisonEntryResult> results = new ArrayList<>();

        for (ComparisonRuntimeEntry entry : entries) {
            StructureRuntime runtime = entry.getRuntime();
            try {
                OperationExecutionResult execResult = runtime.execute(operation, args);
                String stateAfter = runtime.renderCurrentState();
                List<TraceStep> steps = execResult.traceSteps() != null
                        ? List.copyOf(execResult.traceSteps()) : List.of();

                results.add(new ComparisonEntryResult(
                        entry.getImplementationId(),
                        entry.getImplementationName(),
                        execResult.success(),
                        execResult.operationName(),
                        execResult.message(),
                        execResult.returnedValue(),
                        stateAfter,
                        steps
                ));
            } catch (Exception e) {
                results.add(new ComparisonEntryResult(
                        entry.getImplementationId(),
                        entry.getImplementationName(),
                        false,
                        operation,
                        e.getMessage(),
                        null,
                        runtime.renderCurrentState(),
                        List.of()
                ));
            }
        }

        ComparisonOperationResult opResult = new ComparisonOperationResult(operation, List.copyOf(args), results);
        history.add(opResult);
        return opResult;
    }

    public List<ComparisonOperationResult> getHistory() {
        return List.copyOf(history);
    }

    public int historySize() { return history.size(); }

    public void resetAll() {
        for (ComparisonRuntimeEntry entry : entries) {
            entry.getRuntime().reset();
        }
        history.clear();
    }
}
