package structlab.app.comparison;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import structlab.app.runtime.OperationDescriptor;
import structlab.app.service.StructLabService;
import structlab.app.service.StructureSummary;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ComparisonServiceTest {

    private StructLabService service;

    @BeforeEach
    void setUp() {
        service = StructLabService.createDefault();
    }

    // ── Comparable Structures ─────────────────────────────

    @Nested
    class ComparableStructuresTests {

        @Test
        void getComparableStructuresReturnsNonEmpty() {
            List<StructureSummary> comparable = service.getComparableStructures();
            assertFalse(comparable.isEmpty());
        }

        @Test
        void allComparableStructuresHaveTwoPlusImpls() {
            for (StructureSummary s : service.getComparableStructures()) {
                assertTrue(service.getImplementations(s.id()).size() >= 2,
                        s.name() + " should have >= 2 implementations");
            }
        }

        @Test
        void stackIsComparable() {
            assertTrue(service.getComparableStructures().stream()
                    .anyMatch(s -> s.id().equals("struct-stack")));
        }

        @Test
        void queueIsComparable() {
            assertTrue(service.getComparableStructures().stream()
                    .anyMatch(s -> s.id().equals("struct-queue")));
        }

        @Test
        void hashIsComparable() {
            assertTrue(service.getComparableStructures().stream()
                    .anyMatch(s -> s.id().equals("struct-hash")));
        }
    }

    // ── Open Comparison Session ───────────────────────────

    @Nested
    class OpenComparisonSessionTests {

        @Test
        void openComparisonSessionWithAllImpls() {
            ComparisonSession cs = service.openComparisonSession("struct-stack", List.of());
            assertNotNull(cs);
            assertTrue(cs.entryCount() >= 2);
            assertTrue(service.isComparisonMode());
        }

        @Test
        void openComparisonSessionWithShortId() {
            ComparisonSession cs = service.openComparisonSession("stack", List.of());
            assertNotNull(cs);
            assertEquals("struct-stack", cs.getStructureId());
        }

        @Test
        void openComparisonSessionWithSpecificImpls() {
            ComparisonSession cs = service.openComparisonSession("struct-stack",
                    List.of("impl-array-stack", "impl-linked-stack"));
            assertEquals(2, cs.entryCount());
        }

        @Test
        void openComparisonSessionWithShortImplIds() {
            ComparisonSession cs = service.openComparisonSession("stack",
                    List.of("array-stack", "linked-stack"));
            assertEquals(2, cs.entryCount());
        }

        @Test
        void openComparisonSessionThrowsForUnknownStructure() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.openComparisonSession("nonexistent", List.of()));
        }

        @Test
        void openComparisonSessionThrowsForUnknownImpl() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.openComparisonSession("stack", List.of("impl-array-stack", "impl-bogus")));
        }

        @Test
        void openComparisonSessionThrowsForLessThanTwoExplicitImpls() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.openComparisonSession("stack", List.of("impl-array-stack")));
        }

        @Test
        void openComparisonSessionClosesExistingSingleSession() {
            service.openSession("struct-stack", "impl-array-stack");
            assertTrue(service.hasActiveSession());

            service.openComparisonSession("struct-stack", List.of());
            assertTrue(service.isComparisonMode());
            // Single session should no longer be active
            assertFalse(service.hasActiveSession());
        }
    }

    // ── Comparison Mode Operations ────────────────────────

    @Nested
    class ComparisonOperationsTests {

        @BeforeEach
        void openComparison() {
            service.openComparisonSession("struct-stack", List.of());
        }

        @Test
        void executeComparisonOperation() {
            ComparisonOperationResult result = service.executeComparisonOperation("push", List.of("42"));
            assertTrue(result.allSucceeded());
            assertEquals("push", result.operationName());
        }

        @Test
        void comparisonResultsHaveCorrectCount() {
            ComparisonSession cs = service.requireComparisonSession();
            ComparisonOperationResult result = service.executeComparisonOperation("push", List.of("1"));
            assertEquals(cs.entryCount(), result.entryResults().size());
        }

        @Test
        void resetComparisonSession() {
            service.executeComparisonOperation("push", List.of("1"));
            service.executeComparisonOperation("push", List.of("2"));

            service.resetComparisonSession();

            ComparisonSession cs = service.requireComparisonSession();
            assertEquals(0, cs.historySize());
        }

        @Test
        void isComparisonModeReturnsTrueInComparison() {
            assertTrue(service.isComparisonMode());
        }

        @Test
        void isComparisonModeReturnsFalseInSingleSession() {
            service.closeSession();
            service.openSession("struct-stack", "impl-array-stack");
            assertFalse(service.isComparisonMode());
        }
    }

    // ── Single Session Preserved ──────────────────────────

    @Nested
    class SingleSessionPreservedTests {

        @Test
        void singleSessionStillWorks() {
            service.openSession("struct-stack", "impl-array-stack");
            assertTrue(service.hasActiveSession());
            assertFalse(service.isComparisonMode());

            var result = service.executeOperation("push", List.of("10"));
            assertTrue(result.success());
        }

        @Test
        void closeAfterComparisonAllowsSingleSession() {
            service.openComparisonSession("struct-stack", List.of());
            service.closeSession();

            service.openSession("struct-stack", "impl-array-stack");
            assertTrue(service.hasActiveSession());
            assertFalse(service.isComparisonMode());
        }

        @Test
        void requireComparisonSessionThrowsWhenNotInComparisonMode() {
            assertThrows(IllegalStateException.class, () -> service.requireComparisonSession());
        }

        @Test
        void resetComparisonThrowsWhenNotInComparisonMode() {
            assertThrows(IllegalStateException.class, () -> service.resetComparisonSession());
        }
    }

    // ── Hash Table Comparison ─────────────────────────────

    @Nested
    class HashTableComparisonTests {

        @BeforeEach
        void openHashComparison() {
            service.openComparisonSession("struct-hash",
                    List.of("impl-hash-table-chaining", "impl-hash-oa-linear"));
        }

        @Test
        void hashComparisonPut() {
            ComparisonOperationResult result = service.executeComparisonOperation("put", List.of("1", "100"));
            assertTrue(result.allSucceeded());
        }

        @Test
        void hashComparisonGetReturnsConsistentValues() {
            service.executeComparisonOperation("put", List.of("1", "100"));
            ComparisonOperationResult getResult = service.executeComparisonOperation("get", List.of("1"));
            assertTrue(getResult.allSucceeded());
            for (ComparisonEntryResult entry : getResult.entryResults()) {
                assertEquals("100", entry.returnedValue());
            }
        }

        @Test
        void hashComparisonReset() {
            service.executeComparisonOperation("put", List.of("5", "50"));
            service.resetComparisonSession();

            ComparisonOperationResult result = service.executeComparisonOperation("get", List.of("5"));
            // After reset, get should fail or return null for missing key
            for (ComparisonEntryResult entry : result.entryResults()) {
                assertTrue(entry.returnedValue() == null || "null".equals(entry.returnedValue()));
            }
        }
    }

    // ── Queue Comparison ──────────────────────────────────

    @Nested
    class QueueComparisonTests {

        @BeforeEach
        void openQueueComparison() {
            service.openComparisonSession("struct-queue", List.of());
        }

        @Test
        void queueComparisonFIFOBehavior() {
            service.executeComparisonOperation("enqueue", List.of("10"));
            service.executeComparisonOperation("enqueue", List.of("20"));
            service.executeComparisonOperation("enqueue", List.of("30"));

            ComparisonOperationResult deq1 = service.executeComparisonOperation("dequeue", List.of());
            assertTrue(deq1.allSucceeded());
            for (ComparisonEntryResult entry : deq1.entryResults()) {
                assertEquals("10", entry.returnedValue(),
                        "First dequeue should return 10 for " + entry.implementationName());
            }

            ComparisonOperationResult deq2 = service.executeComparisonOperation("dequeue", List.of());
            assertTrue(deq2.allSucceeded());
            for (ComparisonEntryResult entry : deq2.entryResults()) {
                assertEquals("20", entry.returnedValue(),
                        "Second dequeue should return 20 for " + entry.implementationName());
            }
        }

        @Test
        void queueComparisonHasThreeImpls() {
            ComparisonSession cs = service.requireComparisonSession();
            assertEquals(3, cs.entryCount());
        }
    }

    // ── Comparison Grouping ───────────────────────────────

    @Nested
    class ComparisonGroupingTests {

        @Test
        void hashAutoGroupExcludesHashSet() {
            // When opening with empty implIds, Hash Set (add/contains/remove)
            // should be excluded because it's incompatible with Hash Map ops (put/get/remove/contains)
            ComparisonSession cs = service.openComparisonSession("struct-hash", List.of());
            for (ComparisonRuntimeEntry entry : cs.getEntries()) {
                assertNotEquals("impl-hash-set", entry.getImplementationId(),
                        "Hash Set should not be mixed with Hash Map implementations");
            }
        }

        @Test
        void hashAutoGroupSelectsMapImpls() {
            ComparisonSession cs = service.openComparisonSession("struct-hash", List.of());
            // Should select the 4 Hash Map impls (chaining + 3 OA variants)
            assertEquals(4, cs.entryCount(),
                    "Auto-grouping should select 4 compatible Hash Map implementations");
        }

        @Test
        void hashAutoGroupHasFullOperationSet() {
            ComparisonSession cs = service.openComparisonSession("struct-hash", List.of());
            List<String> opNames = cs.getCommonOperations().stream()
                    .map(o -> o.name())
                    .toList();
            assertTrue(opNames.contains("put"), "Should have 'put' operation");
            assertTrue(opNames.contains("get"), "Should have 'get' operation");
            assertTrue(opNames.contains("remove"), "Should have 'remove' operation");
            assertTrue(opNames.contains("contains"), "Should have 'contains' operation");
        }

        @Test
        void stackAutoGroupIncludesAllImpls() {
            // Stack impls (ArrayStack, LinkedStack) share the same ops — all should be included
            ComparisonSession cs = service.openComparisonSession("struct-stack", List.of());
            assertEquals(2, cs.entryCount());
        }

        @Test
        void queueAutoGroupIncludesAllImpls() {
            // All queue impls share the same ops
            ComparisonSession cs = service.openComparisonSession("struct-queue", List.of());
            assertEquals(3, cs.entryCount());
        }

        @Test
        void explicitImplIdsBypassGrouping() {
            // Explicitly requesting Hash Set + Hash Table Chaining should still work
            ComparisonSession cs = service.openComparisonSession("struct-hash",
                    List.of("impl-hash-set", "impl-hash-table-chaining"));
            assertEquals(2, cs.entryCount());
        }
    }
}
