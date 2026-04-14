package structlab.app.comparison;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import structlab.app.runtime.OperationDescriptor;
import structlab.app.runtime.RuntimeFactory;
import structlab.app.runtime.StructureRuntime;
import structlab.registry.ImplementationMetadata;
import structlab.registry.InMemoryStructureRegistry;
import structlab.registry.RegistrySeeder;
import structlab.registry.StructureMetadata;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ComparisonSessionTest {

    private InMemoryStructureRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new InMemoryStructureRegistry();
        RegistrySeeder.seed(registry);
    }

    private ComparisonSession openStackComparison() {
        StructureMetadata sm = registry.getStructureById("struct-stack").orElseThrow();
        List<ImplementationMetadata> impls = registry.getImplementationsFor("struct-stack");
        List<ComparisonRuntimeEntry> entries = impls.stream()
                .map(im -> new ComparisonRuntimeEntry(im.id(), im.name(), RuntimeFactory.createRuntime(sm, im)))
                .toList();
        return new ComparisonSession("struct-stack", "Stack", entries);
    }

    private ComparisonSession openQueueComparison() {
        StructureMetadata sm = registry.getStructureById("struct-queue").orElseThrow();
        List<ImplementationMetadata> impls = registry.getImplementationsFor("struct-queue");
        List<ComparisonRuntimeEntry> entries = impls.stream()
                .map(im -> new ComparisonRuntimeEntry(im.id(), im.name(), RuntimeFactory.createRuntime(sm, im)))
                .toList();
        return new ComparisonSession("struct-queue", "Queue", entries);
    }

    private ComparisonSession openHashComparison() {
        StructureMetadata sm = registry.getStructureById("struct-hash").orElseThrow();
        // Select chaining and two OA variants for comparison
        List<ImplementationMetadata> allImpls = registry.getImplementationsFor("struct-hash");
        List<ImplementationMetadata> selected = allImpls.stream()
                .filter(im -> im.id().equals("impl-hash-table-chaining")
                        || im.id().equals("impl-hash-oa-linear"))
                .toList();
        List<ComparisonRuntimeEntry> entries = selected.stream()
                .map(im -> new ComparisonRuntimeEntry(im.id(), im.name(), RuntimeFactory.createRuntime(sm, im)))
                .toList();
        return new ComparisonSession("struct-hash", "Hash Table", entries);
    }

    private ComparisonSession openHeapComparison() {
        StructureMetadata sm = registry.getStructureById("struct-heap").orElseThrow();
        List<ImplementationMetadata> impls = registry.getImplementationsFor("struct-heap");
        List<ComparisonRuntimeEntry> entries = impls.stream()
                .map(im -> new ComparisonRuntimeEntry(im.id(), im.name(), RuntimeFactory.createRuntime(sm, im)))
                .toList();
        return new ComparisonSession("struct-heap", "Heap", entries);
    }

    // ── Construction ──────────────────────────────────────

    @Nested
    class ConstructionTests {

        @Test
        void requiresAtLeastTwoEntries() {
            StructureMetadata sm = registry.getStructureById("struct-stack").orElseThrow();
            ImplementationMetadata im = registry.getImplementationsFor("struct-stack").get(0);
            StructureRuntime runtime = RuntimeFactory.createRuntime(sm, im);
            ComparisonRuntimeEntry entry = new ComparisonRuntimeEntry(im.id(), im.name(), runtime);

            assertThrows(IllegalArgumentException.class,
                    () -> new ComparisonSession("struct-stack", "Stack", List.of(entry)));
        }

        @Test
        void requiresNonNullEntries() {
            assertThrows(IllegalArgumentException.class,
                    () -> new ComparisonSession("struct-stack", "Stack", null));
        }

        @Test
        void constructsWithTwoOrMoreEntries() {
            ComparisonSession cs = openStackComparison();
            assertTrue(cs.entryCount() >= 2);
            assertEquals("struct-stack", cs.getStructureId());
            assertEquals("Stack", cs.getStructureName());
            assertEquals("compare-all", cs.getImplementationId());
        }
    }

    // ── Common Operations ─────────────────────────────────

    @Nested
    class CommonOperationsTests {

        @Test
        void stackComparisonHasCommonOps() {
            ComparisonSession cs = openStackComparison();
            List<OperationDescriptor> ops = cs.getCommonOperations();
            assertFalse(ops.isEmpty());
            assertTrue(ops.stream().anyMatch(o -> o.name().equals("push")));
            assertTrue(ops.stream().anyMatch(o -> o.name().equals("pop")));
            assertTrue(ops.stream().anyMatch(o -> o.name().equals("peek")));
        }

        @Test
        void queueComparisonHasCommonOps() {
            ComparisonSession cs = openQueueComparison();
            List<OperationDescriptor> ops = cs.getCommonOperations();
            assertFalse(ops.isEmpty());
            assertTrue(ops.stream().anyMatch(o -> o.name().equals("enqueue")));
            assertTrue(ops.stream().anyMatch(o -> o.name().equals("dequeue")));
        }

        @Test
        void heapComparisonHasAllThreeCanonicalOps() {
            ComparisonSession cs = openHeapComparison();
            List<OperationDescriptor> ops = cs.getCommonOperations();
            // Must NOT collapse to just peek — should have insert, extractmin, peek
            assertEquals(3, ops.size(), "Heap comparison should expose all 3 canonical operations");
            assertTrue(ops.stream().anyMatch(o -> o.name().equals("insert")),
                    "Heap comparison should include insert");
            assertTrue(ops.stream().anyMatch(o -> o.name().equals("extractmin")),
                    "Heap comparison should include extractmin");
            assertTrue(ops.stream().anyMatch(o -> o.name().equals("peek")),
                    "Heap comparison should include peek");
        }

        @Test
        void heapComparisonMatchesByAlias() {
            // Verify alias-aware matching: even though HeapPriorityQueue natively
            // uses enqueue/dequeue, the adapter normalizes to insert/extractmin.
            // The alias-aware getCommonOperations() ensures this works even if
            // adapters use different naming.
            ComparisonSession cs = openHeapComparison();
            List<OperationDescriptor> ops = cs.getCommonOperations();
            // insert should have "enqueue" alias
            OperationDescriptor insertOp = ops.stream()
                    .filter(o -> o.name().equals("insert")).findFirst().orElseThrow();
            assertTrue(insertOp.aliases().contains("enqueue"),
                    "insert op should list enqueue as alias");
        }

        @Test
        void commonOpsAreIntersection() {
            ComparisonSession cs = openStackComparison();
            List<OperationDescriptor> commonOps = cs.getCommonOperations();
            // Every common op must exist in every entry
            for (ComparisonRuntimeEntry entry : cs.getEntries()) {
                List<String> entryOps = entry.getRuntime().getAvailableOperations().stream()
                        .map(OperationDescriptor::name).toList();
                for (OperationDescriptor op : commonOps) {
                    assertTrue(entryOps.contains(op.name()),
                            "Op " + op.name() + " missing from " + entry.getImplementationName());
                }
            }
        }
    }

    // ── Execute All ───────────────────────────────────────

    @Nested
    class ExecuteAllTests {

        @Test
        void executeAllPushOnStack() {
            ComparisonSession cs = openStackComparison();
            ComparisonOperationResult result = cs.executeAll("push", List.of("42"));

            assertTrue(result.allSucceeded());
            assertFalse(result.anyFailed());
            assertEquals("push", result.operationName());
            assertEquals(List.of("42"), result.args());
            assertEquals(cs.entryCount(), result.entryResults().size());
        }

        @Test
        void executeAllRecordsTraceSteps() {
            ComparisonSession cs = openStackComparison();
            ComparisonOperationResult result = cs.executeAll("push", List.of("10"));

            for (ComparisonEntryResult entry : result.entryResults()) {
                assertTrue(entry.success());
                assertNotNull(entry.traceSteps());
                assertFalse(entry.traceSteps().isEmpty(), "Trace should be non-empty for " + entry.implementationName());
            }
        }

        @Test
        void executeAllCapturesStateAfter() {
            ComparisonSession cs = openStackComparison();
            cs.executeAll("push", List.of("99"));

            ComparisonOperationResult result = cs.executeAll("peek", List.of());
            for (ComparisonEntryResult entry : result.entryResults()) {
                assertTrue(entry.success());
                assertNotNull(entry.stateAfter());
                assertFalse(entry.stateAfter().isBlank());
            }
        }

        @Test
        void executeAllReturnedValuesAreConsistent() {
            ComparisonSession cs = openStackComparison();
            cs.executeAll("push", List.of("42"));
            ComparisonOperationResult peekResult = cs.executeAll("peek", List.of());

            assertTrue(peekResult.allSucceeded());
            // All implementations should return the same value for peek after push(42)
            for (ComparisonEntryResult entry : peekResult.entryResults()) {
                assertEquals("42", entry.returnedValue(),
                        "peek should return 42 for " + entry.implementationName());
            }
        }

        @Test
        void executeHandlesPartialFailure() {
            ComparisonSession cs = openStackComparison();
            // Pop on empty stack should fail for all — this is universal failure, not partial
            ComparisonOperationResult result = cs.executeAll("pop", List.of());

            // All should fail since stacks are empty
            assertTrue(result.anyFailed());
            for (ComparisonEntryResult entry : result.entryResults()) {
                assertFalse(entry.success());
            }
        }

        @Test
        void queueComparisonEnqueueAndDequeue() {
            ComparisonSession cs = openQueueComparison();

            cs.executeAll("enqueue", List.of("10"));
            cs.executeAll("enqueue", List.of("20"));

            ComparisonOperationResult deqResult = cs.executeAll("dequeue", List.of());
            assertTrue(deqResult.allSucceeded());
            // FIFO: should all return 10
            for (ComparisonEntryResult entry : deqResult.entryResults()) {
                assertEquals("10", entry.returnedValue(),
                        "dequeue should return 10 for " + entry.implementationName());
            }
        }

        @Test
        void hashTableComparisonPutAndGet() {
            ComparisonSession cs = openHashComparison();

            ComparisonOperationResult putResult = cs.executeAll("put", List.of("1", "100"));
            assertTrue(putResult.allSucceeded());

            ComparisonOperationResult getResult = cs.executeAll("get", List.of("1"));
            assertTrue(getResult.allSucceeded());
            for (ComparisonEntryResult entry : getResult.entryResults()) {
                assertEquals("100", entry.returnedValue(),
                        "get(1) should return 100 for " + entry.implementationName());
            }
        }

        @Test
        void heapComparisonInsertAndExtractMin() {
            ComparisonSession cs = openHeapComparison();

            cs.executeAll("insert", List.of("30"));
            cs.executeAll("insert", List.of("10"));
            cs.executeAll("insert", List.of("20"));

            ComparisonOperationResult peekResult = cs.executeAll("peek", List.of());
            assertTrue(peekResult.allSucceeded());
            for (ComparisonEntryResult entry : peekResult.entryResults()) {
                assertEquals("10", entry.returnedValue(),
                        "peek should return min (10) for " + entry.implementationName());
            }

            ComparisonOperationResult extractResult = cs.executeAll("extractmin", List.of());
            assertTrue(extractResult.allSucceeded());
            for (ComparisonEntryResult entry : extractResult.entryResults()) {
                assertEquals("10", entry.returnedValue(),
                        "extractmin should return 10 for " + entry.implementationName());
            }
        }
    }

    // ── History ───────────────────────────────────────────

    @Nested
    class HistoryTests {

        @Test
        void historyStartsEmpty() {
            ComparisonSession cs = openStackComparison();
            assertTrue(cs.getHistory().isEmpty());
            assertEquals(0, cs.historySize());
        }

        @Test
        void executionAddsToHistory() {
            ComparisonSession cs = openStackComparison();
            cs.executeAll("push", List.of("1"));
            cs.executeAll("push", List.of("2"));
            cs.executeAll("peek", List.of());

            assertEquals(3, cs.historySize());
            List<ComparisonOperationResult> history = cs.getHistory();
            assertEquals("push", history.get(0).operationName());
            assertEquals("push", history.get(1).operationName());
            assertEquals("peek", history.get(2).operationName());
        }

        @Test
        void historyIsImmutableCopy() {
            ComparisonSession cs = openStackComparison();
            cs.executeAll("push", List.of("1"));
            List<ComparisonOperationResult> h1 = cs.getHistory();
            cs.executeAll("push", List.of("2"));
            List<ComparisonOperationResult> h2 = cs.getHistory();

            assertEquals(1, h1.size());
            assertEquals(2, h2.size());
        }
    }

    // ── Reset ─────────────────────────────────────────────

    @Nested
    class ResetTests {

        @Test
        void resetClearsHistory() {
            ComparisonSession cs = openStackComparison();
            cs.executeAll("push", List.of("1"));
            cs.executeAll("push", List.of("2"));

            cs.resetAll();

            assertEquals(0, cs.historySize());
            assertTrue(cs.getHistory().isEmpty());
        }

        @Test
        void resetClearsStructureState() {
            ComparisonSession cs = openStackComparison();
            cs.executeAll("push", List.of("42"));

            cs.resetAll();

            // After reset, pop should fail on all (empty)
            ComparisonOperationResult result = cs.executeAll("pop", List.of());
            assertTrue(result.anyFailed());
        }

        @Test
        void resetAllowsNewOperations() {
            ComparisonSession cs = openStackComparison();
            cs.executeAll("push", List.of("1"));
            cs.resetAll();

            ComparisonOperationResult result = cs.executeAll("push", List.of("99"));
            assertTrue(result.allSucceeded());
            assertEquals(1, cs.historySize());
        }
    }

    // ── Close ─────────────────────────────────────────────

    @Test
    void closeIsHarmless() {
        ComparisonSession cs = openStackComparison();
        assertDoesNotThrow(cs::close);
    }

    // ── Entries ───────────────────────────────────────────

    @Test
    void entriesAreImmutable() {
        ComparisonSession cs = openStackComparison();
        assertThrows(UnsupportedOperationException.class,
                () -> cs.getEntries().add(null));
    }
}
