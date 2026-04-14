package structlab.app.comparison;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import structlab.trace.TraceStep;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ComparisonAnalysisTest {

    // ── Helpers ──────────────────────────────────────────────

    private ComparisonEntryResult entry(String name, boolean success, String returned,
                                        String state, long nanos) {
        return new ComparisonEntryResult("id-" + name, name, success, "op", "", returned, state,
                List.of(), nanos);
    }

    private ComparisonOperationResult opResult(ComparisonEntryResult... entries) {
        return new ComparisonOperationResult("op", List.of(), List.of(entries));
    }

    // ── Tests ────────────────────────────────────────────────

    @Nested
    class VerdictTests {
        @Test
        void allMatchingWhenSameResultAndState() {
            ComparisonAnalysis a = ComparisonAnalysis.of(opResult(
                    entry("A", true, "42", "[42]", 1000),
                    entry("B", true, "42", "[42]", 2000)));
            assertEquals(ComparisonAnalysis.OverallVerdict.MATCHING, a.getVerdict());
            assertFalse(a.hasDivergences());
        }

        @Test
        void partialFailWhenMixedSuccess() {
            ComparisonAnalysis a = ComparisonAnalysis.of(opResult(
                    entry("A", true, "42", "[42]", 1000),
                    entry("B", false, null, "[]", 500)));
            assertEquals(ComparisonAnalysis.OverallVerdict.PARTIAL_FAIL, a.getVerdict());
            assertTrue(a.hasDivergences());
            assertTrue(a.getDivergences().stream()
                    .anyMatch(d -> d.type() == ComparisonAnalysis.DivergenceType.STATUS_MISMATCH));
        }

        @Test
        void divergentWhenDifferentReturnedValues() {
            ComparisonAnalysis a = ComparisonAnalysis.of(opResult(
                    entry("A", true, "42", "[42]", 1000),
                    entry("B", true, "99", "[42]", 2000)));
            assertEquals(ComparisonAnalysis.OverallVerdict.DIVERGENT, a.getVerdict());
            assertTrue(a.getDivergences().stream()
                    .anyMatch(d -> d.type() == ComparisonAnalysis.DivergenceType.VALUE_MISMATCH));
        }

        @Test
        void divergentWhenDifferentStates() {
            ComparisonAnalysis a = ComparisonAnalysis.of(opResult(
                    entry("A", true, "42", "[42, 10]", 1000),
                    entry("B", true, "42", "[42, 20]", 2000)));
            assertEquals(ComparisonAnalysis.OverallVerdict.DIVERGENT, a.getVerdict());
            assertTrue(a.getDivergences().stream()
                    .anyMatch(d -> d.type() == ComparisonAnalysis.DivergenceType.STATE_DIVERGENCE));
        }
    }

    @Nested
    class TimingTests {
        @Test
        void fastestAndSlowest() {
            ComparisonEntryResult fast = entry("A", true, "1", "[]", 500);
            ComparisonEntryResult slow = entry("B", true, "1", "[]", 5000);
            ComparisonAnalysis a = ComparisonAnalysis.of(opResult(fast, slow));

            assertTrue(a.isFastest(fast));
            assertFalse(a.isFastest(slow));
            assertTrue(a.isSlowest(slow));
            assertFalse(a.isSlowest(fast));
        }

        @Test
        void equalTimingMeansNeitherIsFastestOrSlowest() {
            ComparisonEntryResult e1 = entry("A", true, "1", "[]", 1000);
            ComparisonEntryResult e2 = entry("B", true, "1", "[]", 1000);
            ComparisonAnalysis a = ComparisonAnalysis.of(opResult(e1, e2));

            assertFalse(a.isFastest(e1));
            assertFalse(a.isSlowest(e1));
        }

        @Test
        void timingSummaryFormatsMicroseconds() {
            ComparisonAnalysis a = ComparisonAnalysis.of(opResult(
                    entry("A", true, "1", "[]", 500_000),
                    entry("B", true, "1", "[]", 500_000)));
            assertTrue(a.timingSummary().contains("μs"));
        }

        @Test
        void timingSummaryFormatsRange() {
            ComparisonAnalysis a = ComparisonAnalysis.of(opResult(
                    entry("A", true, "1", "[]", 100_000),
                    entry("B", true, "1", "[]", 2_000_000)));
            assertTrue(a.timingSummary().contains("–"));
        }
    }

    @Nested
    class EntryResultTests {
        @Test
        void formattedDurationMicroseconds() {
            ComparisonEntryResult er = entry("A", true, "x", "[]", 500_000);
            assertTrue(er.formattedDuration().contains("μs"));
        }

        @Test
        void formattedDurationMilliseconds() {
            ComparisonEntryResult er = entry("A", true, "x", "[]", 5_000_000);
            assertTrue(er.formattedDuration().contains("ms"));
        }
    }
}
