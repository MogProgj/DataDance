package structlab.app.runtime;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import structlab.app.runtime.adapters.HashRuntimeAdapter;
import structlab.registry.ImplementationMetadata;
import structlab.registry.StructureMetadata;

import java.util.List;
import java.util.Map;
import java.util.Set;

class HashRuntimeAdapterTest {

    private StructureMetadata sm() {
        return new StructureMetadata("struct-hash", "Hash Table", "Associative",
                Set.of(), "desc", "behavior", "notes");
    }

    private ImplementationMetadata htIm() {
        return new ImplementationMetadata("impl-hash-table-chaining", "Hash Table Chaining",
                "struct-hash", "desc", Map.of(), "O(N)", Object.class);
    }

    private ImplementationMetadata hsIm() {
        return new ImplementationMetadata("impl-hash-set", "Hash Set",
                "struct-hash", "desc", Map.of(), "O(N)", Object.class);
    }

    // ---- Factory wiring ----

    @Test
    void factoryCreatesHashTableRuntime() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), htIm());
        assertInstanceOf(HashRuntimeAdapter.class, rt);
    }

    @Test
    void factoryCreatesHashSetRuntime() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), hsIm());
        assertInstanceOf(HashRuntimeAdapter.class, rt);
    }

    // ---- Hash Table operations ----

    @Test
    void hashTablePutAndGetWork() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), htIm());

        OperationExecutionResult putResult = rt.execute("put", List.of("1", "100"));
        assertTrue(putResult.success());

        OperationExecutionResult getResult = rt.execute("get", List.of("1"));
        assertTrue(getResult.success());
        assertEquals("100", getResult.returnedValue());
    }

    @Test
    void hashTableRemoveWorks() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), htIm());
        rt.execute("put", List.of("1", "100"));

        OperationExecutionResult result = rt.execute("remove", List.of("1"));
        assertTrue(result.success());
        assertEquals("100", result.returnedValue());
    }

    @Test
    void hashTableContainsWorks() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), htIm());
        rt.execute("put", List.of("5", "50"));

        OperationExecutionResult result = rt.execute("contains", List.of("5"));
        assertTrue(result.success());
        assertEquals("true", result.returnedValue());
    }

    @Test
    void hashTablePutMissingArgsReturnsError() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), htIm());
        OperationExecutionResult result = rt.execute("put", List.of("1"));
        assertFalse(result.success());
    }

    @Test
    void hashTableUnknownOperationReturnsError() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), htIm());
        OperationExecutionResult result = rt.execute("fly", List.of());
        assertFalse(result.success());
    }

    @Test
    void hashTableOperationDescriptorsPresent() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), htIm());
        List<OperationDescriptor> ops = rt.getAvailableOperations();
        assertFalse(ops.isEmpty());
        assertTrue(ops.stream().anyMatch(o -> o.name().equals("put")));
        assertTrue(ops.stream().anyMatch(o -> o.name().equals("get")));
        assertTrue(ops.stream().anyMatch(o -> o.name().equals("remove")));
        assertTrue(ops.stream().anyMatch(o -> o.name().equals("contains")));
    }

    @Test
    void hashTableRenderCurrentStateReturnsNonEmpty() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), htIm());
        rt.execute("put", List.of("1", "100"));
        String rendered = rt.renderCurrentState();
        assertNotNull(rendered);
        assertFalse(rendered.isBlank());
        assertTrue(rendered.contains("HashTableChaining"));
    }

    @Test
    void hashTableResetClearsState() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), htIm());
        rt.execute("put", List.of("1", "100"));
        rt.reset();
        String state = rt.getCurrentState();
        assertTrue(state.contains("size=0"));
    }

    // ---- Hash Set operations ----

    @Test
    void hashSetAddAndContainsWork() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), hsIm());

        OperationExecutionResult addResult = rt.execute("add", List.of("42"));
        assertTrue(addResult.success());
        assertEquals("true", addResult.returnedValue());

        OperationExecutionResult containsResult = rt.execute("contains", List.of("42"));
        assertTrue(containsResult.success());
        assertEquals("true", containsResult.returnedValue());
    }

    @Test
    void hashSetRemoveWorks() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), hsIm());
        rt.execute("add", List.of("42"));

        OperationExecutionResult result = rt.execute("remove", List.of("42"));
        assertTrue(result.success());
        assertEquals("true", result.returnedValue());
    }

    @Test
    void hashSetDuplicateAddReturnsFalse() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), hsIm());
        rt.execute("add", List.of("42"));

        OperationExecutionResult result = rt.execute("add", List.of("42"));
        assertTrue(result.success());
        assertEquals("false", result.returnedValue());
    }

    @Test
    void hashSetOperationDescriptorsPresent() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), hsIm());
        List<OperationDescriptor> ops = rt.getAvailableOperations();
        assertFalse(ops.isEmpty());
        assertTrue(ops.stream().anyMatch(o -> o.name().equals("add")));
        assertTrue(ops.stream().anyMatch(o -> o.name().equals("contains")));
        assertTrue(ops.stream().anyMatch(o -> o.name().equals("remove")));
    }

    @Test
    void hashSetResetClearsState() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), hsIm());
        rt.execute("add", List.of("1"));
        rt.execute("add", List.of("2"));
        rt.reset();
        String state = rt.getCurrentState();
        assertTrue(state.contains("size=0"));
    }
}
