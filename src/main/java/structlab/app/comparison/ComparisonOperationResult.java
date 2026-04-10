package structlab.app.comparison;

import java.util.List;

/**
 * The result of executing one operation across all participating implementations.
 */
public record ComparisonOperationResult(
    String operationName,
    List<String> args,
    List<ComparisonEntryResult> entryResults
) {
    public boolean allSucceeded() {
        return entryResults.stream().allMatch(ComparisonEntryResult::success);
    }

    public boolean anyFailed() {
        return entryResults.stream().anyMatch(e -> !e.success());
    }
}
