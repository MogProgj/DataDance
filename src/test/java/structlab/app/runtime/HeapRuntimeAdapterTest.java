package structlab.app.runtime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import structlab.app.runtime.adapters.HeapRuntimeAdapter;
import structlab.registry.ImplementationMetadata;
import structlab.registry.StructureMetadata;

import java.util.List;
import java.util.Map;
import java.util.Set;

class HeapRuntimeAdapterTest {

    private StructureMetadata sm() {
        return new StructureMetadata("struct-heap", "Heap (Priority Queue)", "Tree",
                Set.of(), "desc", "behavior", "notes");
    }

    private ImplementationMetadata binaryHeapIm() {
        return new ImplementationMetadata("impl-binary-heap", "Binary Heap",
                "struct-heap", "desc", Map.of(), "O(N)", Object.class);
    }

    private ImplementationMetadata heapPqIm() {
        return new ImplementationMetadata("impl-heap-priority-queue", "Heap Priority Queue",
                "struct-heap", "desc", Map.of(), "O(N)", Object.class);
    }

    // ── Factory wiring ──

    @Test
    void factoryCreatesBinaryHeapRuntime() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), binaryHeapIm());
        assertInstanceOf(HeapRuntimeAdapter.class, rt);
    }

    @Test
    void factoryCreatesHeapPriorityQueueRuntime() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), heapPqIm());
        assertInstanceOf(HeapRuntimeAdapter.class, rt);
    }

    // ── Canonical operations: both types expose same op names ──

    @Test
    void binaryHeapExposesCanonicalOperations() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), binaryHeapIm());
        List<String> opNames = rt.getAvailableOperations().stream()
                .map(OperationDescriptor::name).toList();
        assertEquals(List.of("insert", "extractmin", "peek"), opNames);
    }

    @Test
    void heapPriorityQueueExposesCanonicalOperations() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), heapPqIm());
        List<String> opNames = rt.getAvailableOperations().stream()
                .map(OperationDescriptor::name).toList();
        assertEquals(List.of("insert", "extractmin", "peek"), opNames,
                "HeapPriorityQueue must expose the same canonical names as BinaryHeap for comparison");
    }

    @Test
    void canonicalOpsIncludeAliases() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), binaryHeapIm());
        OperationDescriptor insertOp = rt.getAvailableOperations().get(0);
        assertTrue(insertOp.aliases().contains("enqueue"), "insert should list enqueue as alias");

        OperationDescriptor extractOp = rt.getAvailableOperations().get(1);
        assertTrue(extractOp.aliases().contains("dequeue"), "extractmin should list dequeue as alias");
    }

    // ── Operations work on BinaryHeap ──

    @Test
    void binaryHeapInsertAndExtract() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), binaryHeapIm());
        OperationExecutionResult r1 = rt.execute("insert", List.of("5"));
        assertTrue(r1.success());
        rt.execute("insert", List.of("3"));
        rt.execute("insert", List.of("7"));

        OperationExecutionResult peek = rt.execute("peek", List.of());
        assertTrue(peek.success());
        assertEquals("3", peek.returnedValue());

        OperationExecutionResult extract = rt.execute("extractmin", List.of());
        assertTrue(extract.success());
        assertEquals("3", extract.returnedValue());
    }

    // ── Operations work on HeapPriorityQueue via canonical names ──

    @Test
    void heapPqWorksWithCanonicalNames() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), heapPqIm());
        assertTrue(rt.execute("insert", List.of("10")).success());
        assertTrue(rt.execute("insert", List.of("2")).success());
        assertTrue(rt.execute("insert", List.of("8")).success());

        OperationExecutionResult peek = rt.execute("peek", List.of());
        assertTrue(peek.success());
        assertEquals("2", peek.returnedValue());

        OperationExecutionResult extract = rt.execute("extractmin", List.of());
        assertTrue(extract.success());
        assertEquals("2", extract.returnedValue());
    }

    // ── Legacy alias names still work in execute ──

    @Test
    void heapPqAcceptsLegacyEnqueueDequeue() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), heapPqIm());
        assertTrue(rt.execute("enqueue", List.of("4")).success());
        assertTrue(rt.execute("enqueue", List.of("1")).success());

        OperationExecutionResult deq = rt.execute("dequeue", List.of());
        assertTrue(deq.success());
        assertEquals("1", deq.returnedValue());
    }

    @Test
    void binaryHeapRejectsMissingArg() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), binaryHeapIm());
        OperationExecutionResult r = rt.execute("insert", List.of());
        assertFalse(r.success());
    }
}
