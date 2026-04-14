package structlab.app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class StructLabServiceTest {

    private StructLabService service;

    @BeforeEach
    void setUp() {
        service = StructLabService.createDefault();
    }

    // ── Discovery ──────────────────────────────────────────────

    @Nested
    class DiscoveryTests {

        @Test
        void getAllStructuresReturnsNonEmptyList() {
            List<StructureSummary> structures = service.getAllStructures();
            assertFalse(structures.isEmpty());
        }

        @Test
        void everyStructureHasIdNameAndCategory() {
            for (StructureSummary s : service.getAllStructures()) {
                assertNotNull(s.id());
                assertNotNull(s.name());
                assertNotNull(s.category());
                assertFalse(s.id().isBlank());
                assertFalse(s.name().isBlank());
            }
        }

        @Test
        void getStructureByIdReturnsCorrectMatch() {
            Optional<StructureSummary> found = service.getStructure("struct-stack");
            assertTrue(found.isPresent());
            assertEquals("Stack", found.get().name());
        }

        @Test
        void getStructureByShortIdNormalizesPrefix() {
            Optional<StructureSummary> found = service.getStructure("stack");
            assertTrue(found.isPresent());
            assertEquals("struct-stack", found.get().id());
        }

        @Test
        void getStructureForUnknownIdReturnsEmpty() {
            assertTrue(service.getStructure("struct-nonexistent").isEmpty());
        }

        @Test
        void structureSummaryIncludesBehaviorAndLearningNotes() {
            StructureSummary s = service.getStructure("struct-stack").orElseThrow();
            assertNotNull(s.behavior());
            assertNotNull(s.learningNotes());
        }

        @Test
        void allStructuresHaveBehaviorAndLearningNotes() {
            for (StructureSummary s : service.getAllStructures()) {
                assertNotNull(s.behavior(), "behavior null for " + s.id());
                assertNotNull(s.learningNotes(), "learningNotes null for " + s.id());
            }
        }

        @Test
        void getImplementationsReturnsPopulatedList() {
            List<ImplementationSummary> impls = service.getImplementations("struct-stack");
            assertFalse(impls.isEmpty());
            for (ImplementationSummary im : impls) {
                assertEquals("struct-stack", im.parentStructureId());
                assertNotNull(im.name());
            }
        }

        @Test
        void getImplementationsForUnknownStructureReturnsEmpty() {
            assertTrue(service.getImplementations("bogus").isEmpty());
        }

        @Test
        void structureSummaryHasDescriptionForGuiDetail() {
            Optional<StructureSummary> found = service.getStructure("struct-stack");
            assertTrue(found.isPresent());
            StructureSummary s = found.get();
            assertNotNull(s.description());
            assertFalse(s.description().isBlank());
            assertNotNull(s.keywords());
        }

        @Test
        void implementationSummaryHasDescriptionAndComplexity() {
            List<ImplementationSummary> impls = service.getImplementations("struct-stack");
            assertFalse(impls.isEmpty());
            ImplementationSummary im = impls.get(0);
            assertNotNull(im.description());
            assertNotNull(im.timeComplexity());
            assertNotNull(im.spaceComplexity());
        }
    }

    // ── Session lifecycle ──────────────────────────────────────

    @Nested
    class SessionTests {

        @Test
        void noActiveSessionByDefault() {
            assertFalse(service.hasActiveSession());
            assertTrue(service.getSessionSnapshot().isEmpty());
        }

        @Test
        void openSessionCreatesActiveSession() {
            SessionSnapshot snap = service.openSession("struct-stack", "impl-array-stack");
            assertTrue(service.hasActiveSession());
            assertEquals("struct-stack", snap.structureId());
            assertEquals("impl-array-stack", snap.implementationId());
            assertEquals(0, snap.operationCount());
        }

        @Test
        void openSessionWithShortIdsNormalizes() {
            SessionSnapshot snap = service.openSession("stack", "array-stack");
            assertEquals("struct-stack", snap.structureId());
            assertEquals("impl-array-stack", snap.implementationId());
        }

        @Test
        void openSessionWithBadStructureThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.openSession("nonexistent", "impl-array-stack"));
        }

        @Test
        void openSessionWithBadImplementationThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> service.openSession("struct-stack", "impl-no-such-thing"));
        }

        @Test
        void closeSessionClearsActiveState() {
            service.openSession("struct-stack", "impl-array-stack");
            service.closeSession();
            assertFalse(service.hasActiveSession());
        }

        @Test
        void closeSessionWhenNoSessionIsHarmless() {
            assertDoesNotThrow(() -> service.closeSession());
        }

        @Test
        void getSessionSnapshotReflectsState() {
            service.openSession("struct-stack", "impl-array-stack");
            Optional<SessionSnapshot> snap = service.getSessionSnapshot();
            assertTrue(snap.isPresent());
            assertNotNull(snap.get().structureName());
            assertNotNull(snap.get().implementationName());
        }
    }

    // ── Operations ─────────────────────────────────────────────

    @Nested
    class OperationTests {

        @BeforeEach
        void open() {
            service.openSession("struct-stack", "impl-array-stack");
        }

        @Test
        void getAvailableOperationsReturnsNonEmpty() {
            List<OperationInfo> ops = service.getAvailableOperations();
            assertFalse(ops.isEmpty());
        }

        @Test
        void eachOperationHasNameAndUsage() {
            for (OperationInfo op : service.getAvailableOperations()) {
                assertNotNull(op.name());
                assertFalse(op.name().isBlank());
                assertNotNull(op.usage());
            }
        }

        @Test
        void executeOperationReturnsResult() {
            ExecutionResult result = service.executeOperation("push", List.of("42"));
            assertTrue(result.success());
            assertEquals("push", result.operationName());
        }

        @Test
        void executeInvalidOperationFails() {
            ExecutionResult result = service.executeOperation("fly", List.of());
            assertFalse(result.success());
        }

        @Test
        void operationsWithoutSessionThrows() {
            StructLabService fresh = StructLabService.createDefault();
            assertThrows(IllegalStateException.class, fresh::getAvailableOperations);
        }
    }

    // ── State and history ──────────────────────────────────────

    @Nested
    class StateAndHistoryTests {

        @BeforeEach
        void open() {
            service.openSession("struct-stack", "impl-array-stack");
        }

        @Test
        void getRenderedStateReturnsNonNull() {
            assertNotNull(service.getRenderedState());
        }

        @Test
        void historyStartsEmpty() {
            assertTrue(service.getHistory().isEmpty());
            assertTrue(service.getLastResult().isEmpty());
        }

        @Test
        void executeAddsToHistory() {
            service.executeOperation("push", List.of("1"));
            service.executeOperation("push", List.of("2"));

            List<ExecutionResult> history = service.getHistory();
            assertEquals(2, history.size());
            assertEquals("push", history.get(0).operationName());
        }

        @Test
        void getLastResultReturnsLatest() {
            service.executeOperation("push", List.of("1"));
            service.executeOperation("push", List.of("2"));
            service.executeOperation("pop", List.of());

            Optional<ExecutionResult> last = service.getLastResult();
            assertTrue(last.isPresent());
            assertEquals("pop", last.get().operationName());
        }

        @Test
        void getLastTraceStepsReturnsTraceData() {
            service.executeOperation("push", List.of("10"));
            // Traced operations should produce trace steps
            assertNotNull(service.getLastTraceSteps());
        }

        @Test
        void getLastTraceRenderedReturnsString() {
            service.executeOperation("push", List.of("10"));
            String rendered = service.getLastTraceRendered();
            assertNotNull(rendered);
            assertFalse(rendered.isBlank());
        }

        @Test
        void sessionSnapshotReflectsOperationCount() {
            service.executeOperation("push", List.of("1"));
            service.executeOperation("push", List.of("2"));

            SessionSnapshot snap = service.getSessionSnapshot().orElseThrow();
            assertEquals(2, snap.operationCount());
        }
    }

    // ── Reset ──────────────────────────────────────────────────

    @Nested
    class ResetTests {

        @Test
        void resetClearsHistoryAndState() {
            service.openSession("struct-stack", "impl-array-stack");
            service.executeOperation("push", List.of("1"));
            service.executeOperation("push", List.of("2"));

            service.resetSession();

            assertTrue(service.getHistory().isEmpty());
            assertTrue(service.getLastResult().isEmpty());
            assertEquals(0, service.getSessionSnapshot().orElseThrow().operationCount());
        }

        @Test
        void resetKeepsSessionOpen() {
            service.openSession("struct-stack", "impl-array-stack");
            service.executeOperation("push", List.of("1"));

            service.resetSession();

            assertTrue(service.hasActiveSession());
            SessionSnapshot snap = service.getSessionSnapshot().orElseThrow();
            assertEquals("struct-stack", snap.structureId());
            assertEquals("impl-array-stack", snap.implementationId());
        }

        @Test
        void resetAllowsNewOperationsAfterClear() {
            service.openSession("struct-stack", "impl-array-stack");
            service.executeOperation("push", List.of("1"));
            service.resetSession();

            ExecutionResult result = service.executeOperation("push", List.of("99"));
            assertTrue(result.success());
            assertEquals(1, service.getHistory().size());
        }

        @Test
        void resetWithoutSessionThrows() {
            assertThrows(IllegalStateException.class, () -> service.resetSession());
        }
    }

    // ── Close session ──────────────────────────────────────────

    @Nested
    class CloseSessionTests {

        @Test
        void closeSessionClearsAllServiceState() {
            service.openSession("struct-stack", "impl-array-stack");
            service.executeOperation("push", List.of("1"));

            service.closeSession();

            assertFalse(service.hasActiveSession());
            assertTrue(service.getSessionSnapshot().isEmpty());
        }

        @Test
        void operationsAfterCloseThrow() {
            service.openSession("struct-stack", "impl-array-stack");
            service.closeSession();

            assertThrows(IllegalStateException.class, () -> service.getAvailableOperations());
            assertThrows(IllegalStateException.class, () -> service.getRenderedState());
            assertThrows(IllegalStateException.class, () -> service.getHistory());
            assertThrows(IllegalStateException.class, () -> service.resetSession());
        }

        @Test
        void canReopenSessionAfterClose() {
            service.openSession("struct-stack", "impl-array-stack");
            service.closeSession();

            SessionSnapshot snap = service.openSession("struct-queue", "impl-circular-array-queue");
            assertTrue(service.hasActiveSession());
            assertEquals("struct-queue", snap.structureId());
        }
    }

    // ── Invalid operations ─────────────────────────────────────

    @Nested
    class InvalidOperationTests {

        @BeforeEach
        void open() {
            service.openSession("struct-stack", "impl-array-stack");
        }

        @Test
        void invalidOperationReturnsFailResult() {
            ExecutionResult result = service.executeOperation("fly", List.of());
            assertFalse(result.success());
            assertNotNull(result.message());
            assertFalse(result.message().isBlank());
        }

        @Test
        void failedOperationAppearsInHistory() {
            service.executeOperation("fly", List.of());
            List<ExecutionResult> history = service.getHistory();
            assertEquals(1, history.size());
            assertFalse(history.get(0).success());
        }

        @Test
        void traceAvailableAfterValidOperation() {
            service.executeOperation("push", List.of("10"));
            List<?> steps = service.getLastTraceSteps();
            assertFalse(steps.isEmpty());
            String rendered = service.getLastTraceRendered();
            assertNotNull(rendered);
            assertFalse(rendered.equals("No trace steps available."));
        }

        @Test
        void traceAvailableAfterFailedOperation() {
            service.executeOperation("push", List.of("10"));
            service.executeOperation("pop", List.of());
            service.executeOperation("pop", List.of()); // pop on empty — should fail

            // The last result should still be traceable
            Optional<ExecutionResult> last = service.getLastResult();
            assertTrue(last.isPresent());
            String rendered = service.getLastTraceRendered();
            assertNotNull(rendered);
        }
    }

    // ── Hash Table session ─────────────────────────────────────

    @Nested
    class HashTableSessionTests {

        @BeforeEach
        void open() {
            service.openSession("struct-hash", "impl-hash-table-chaining");
        }

        @Test
        void hashTableSessionOpens() {
            assertTrue(service.hasActiveSession());
            SessionSnapshot snap = service.getSessionSnapshot().orElseThrow();
            assertEquals("struct-hash", snap.structureId());
            assertEquals("impl-hash-table-chaining", snap.implementationId());
        }

        @Test
        void hashTableOperationsAvailable() {
            List<OperationInfo> ops = service.getAvailableOperations();
            assertFalse(ops.isEmpty());
            assertTrue(ops.stream().anyMatch(o -> o.name().equals("put")));
            assertTrue(ops.stream().anyMatch(o -> o.name().equals("get")));
        }

        @Test
        void hashTablePutExecutes() {
            ExecutionResult result = service.executeOperation("put", List.of("1", "100"));
            assertTrue(result.success());
            assertEquals("put", result.operationName());
        }

        @Test
        void hashTableRenderedStateContainsHashInfo() {
            service.executeOperation("put", List.of("1", "100"));
            String rendered = service.getRenderedState();
            assertNotNull(rendered);
            assertFalse(rendered.isBlank());
        }

        @Test
        void hashTableTraceAvailable() {
            service.executeOperation("put", List.of("1", "100"));
            List<?> steps = service.getLastTraceSteps();
            assertFalse(steps.isEmpty());
            String renderedTrace = service.getLastTraceRendered();
            assertNotNull(renderedTrace);
            assertFalse(renderedTrace.isBlank());
        }

        @Test
        void hashTableHistoryTracksOperations() {
            service.executeOperation("put", List.of("1", "100"));
            service.executeOperation("get", List.of("1"));
            assertEquals(2, service.getHistory().size());
        }

        @Test
        void hashTableResetWorks() {
            service.executeOperation("put", List.of("1", "100"));
            service.resetSession();
            assertTrue(service.getHistory().isEmpty());
            assertEquals(0, service.getSessionSnapshot().orElseThrow().operationCount());
        }
    }

    // ── Hash Set session ───────────────────────────────────────

    @Nested
    class HashSetSessionTests {

        @BeforeEach
        void open() {
            service.openSession("struct-hash", "impl-hash-set");
        }

        @Test
        void hashSetSessionOpens() {
            assertTrue(service.hasActiveSession());
            SessionSnapshot snap = service.getSessionSnapshot().orElseThrow();
            assertEquals("impl-hash-set", snap.implementationId());
        }

        @Test
        void hashSetOperationsAvailable() {
            List<OperationInfo> ops = service.getAvailableOperations();
            assertTrue(ops.stream().anyMatch(o -> o.name().equals("add")));
            assertTrue(ops.stream().anyMatch(o -> o.name().equals("contains")));
            assertTrue(ops.stream().anyMatch(o -> o.name().equals("remove")));
        }

        @Test
        void hashSetAddExecutes() {
            ExecutionResult result = service.executeOperation("add", List.of("42"));
            assertTrue(result.success());
        }

        @Test
        void hashSetTraceAvailable() {
            service.executeOperation("add", List.of("42"));
            String renderedTrace = service.getLastTraceRendered();
            assertNotNull(renderedTrace);
            assertFalse(renderedTrace.isBlank());
        }
    }

    // ── Complexity Matrix & Comparable ─────────────────────────

    @Nested
    class ComplexityMatrixTests {

        @Test
        void buildComplexityMatrixForStack() {
            ComplexityMatrix matrix = service.buildComplexityMatrix("struct-stack");
            assertFalse(matrix.implementationNames().isEmpty());
            assertFalse(matrix.rows().isEmpty());
        }

        @Test
        void buildComplexityMatrixForUnknownReturnsEmpty() {
            ComplexityMatrix matrix = service.buildComplexityMatrix("struct-nonexistent");
            assertTrue(matrix.implementationNames().isEmpty());
            assertTrue(matrix.rows().isEmpty());
        }

        @Test
        void isComparableForStackIsTrue() {
            // Stack has at least 2 implementations (array + linked)
            assertTrue(service.isComparable("struct-stack"));
        }

        @Test
        void isComparableForUnknownIsFalse() {
            assertFalse(service.isComparable("struct-nonexistent"));
        }
    }
}
