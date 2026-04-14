package structlab.app.comparison;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Analyses a {@link ComparisonOperationResult} to detect divergences
 * between implementations beyond simple pass/fail.
 */
public final class ComparisonAnalysis {

    public enum DivergenceType {
        /** Some implementations succeeded, others failed. */
        STATUS_MISMATCH,
        /** All succeeded but returned different values. */
        VALUE_MISMATCH,
        /** All succeeded but ended in different structural states. */
        STATE_DIVERGENCE
    }

    public record Divergence(DivergenceType type, String detail) {}

    public enum OverallVerdict { MATCHING, DIVERGENT, PARTIAL_FAIL }

    private final OverallVerdict verdict;
    private final List<Divergence> divergences;
    private final long fastestNanos;
    private final long slowestNanos;

    private ComparisonAnalysis(OverallVerdict verdict, List<Divergence> divergences,
                               long fastestNanos, long slowestNanos) {
        this.verdict = verdict;
        this.divergences = List.copyOf(divergences);
        this.fastestNanos = fastestNanos;
        this.slowestNanos = slowestNanos;
    }

    public static ComparisonAnalysis of(ComparisonOperationResult result) {
        List<ComparisonEntryResult> entries = result.entryResults();
        List<Divergence> divergences = new ArrayList<>();

        // Timing extremes
        long fastest = entries.stream().mapToLong(ComparisonEntryResult::durationNanos).min().orElse(0);
        long slowest = entries.stream().mapToLong(ComparisonEntryResult::durationNanos).max().orElse(0);

        // Status check
        boolean allSuccess = result.allSucceeded();
        boolean anyFail = result.anyFailed();
        boolean mixedStatus = anyFail && !entries.stream().allMatch(e -> !e.success());

        if (mixedStatus) {
            String detail = entries.stream()
                    .filter(e -> !e.success())
                    .map(ComparisonEntryResult::implementationName)
                    .collect(Collectors.joining(", ", "Failed: ", ""));
            divergences.add(new Divergence(DivergenceType.STATUS_MISMATCH, detail));
        }

        // Value check (only among successful entries)
        List<ComparisonEntryResult> succeeded = entries.stream()
                .filter(ComparisonEntryResult::success).toList();
        if (succeeded.size() > 1) {
            Set<String> distinctValues = succeeded.stream()
                    .map(ComparisonEntryResult::returnedValue)
                    .map(v -> v == null ? "" : v)
                    .collect(Collectors.toSet());
            if (distinctValues.size() > 1) {
                String detail = succeeded.stream()
                        .map(e -> e.implementationName() + "=" + (e.returnedValue() == null ? "null" : e.returnedValue()))
                        .collect(Collectors.joining(", "));
                divergences.add(new Divergence(DivergenceType.VALUE_MISMATCH, detail));
            }

            // State check
            Set<String> distinctStates = succeeded.stream()
                    .map(ComparisonEntryResult::stateAfter)
                    .map(s -> s == null ? "" : s)
                    .collect(Collectors.toSet());
            if (distinctStates.size() > 1) {
                divergences.add(new Divergence(DivergenceType.STATE_DIVERGENCE,
                        distinctStates.size() + " distinct states observed"));
            }
        }

        OverallVerdict verdict;
        if (mixedStatus) {
            verdict = OverallVerdict.PARTIAL_FAIL;
        } else if (!divergences.isEmpty()) {
            verdict = OverallVerdict.DIVERGENT;
        } else {
            verdict = OverallVerdict.MATCHING;
        }

        return new ComparisonAnalysis(verdict, divergences, fastest, slowest);
    }

    public OverallVerdict getVerdict() { return verdict; }
    public List<Divergence> getDivergences() { return divergences; }
    public boolean hasDivergences() { return !divergences.isEmpty(); }
    public long getFastestNanos() { return fastestNanos; }
    public long getSlowestNanos() { return slowestNanos; }

    /** True if the given entry was the fastest in this operation. */
    public boolean isFastest(ComparisonEntryResult entry) {
        return entry.durationNanos() == fastestNanos && fastestNanos != slowestNanos;
    }

    /** True if the given entry was the slowest in this operation. */
    public boolean isSlowest(ComparisonEntryResult entry) {
        return entry.durationNanos() == slowestNanos && fastestNanos != slowestNanos;
    }

    /** Formats the timing spread as a human-readable string. */
    public String timingSummary() {
        if (fastestNanos == slowestNanos) {
            return formatNanos(fastestNanos);
        }
        return formatNanos(fastestNanos) + " – " + formatNanos(slowestNanos);
    }

    private static String formatNanos(long nanos) {
        if (nanos < 1_000_000) {
            return String.format("%.1f μs", nanos / 1_000.0);
        }
        return String.format("%.2f ms", nanos / 1_000_000.0);
    }
}
