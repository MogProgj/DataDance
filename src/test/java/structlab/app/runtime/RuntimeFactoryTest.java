package structlab.app.runtime;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import structlab.app.runtime.adapters.*;
import structlab.registry.StructureMetadata;
import structlab.registry.ImplementationMetadata;

import java.util.List;

public class RuntimeFactoryTest {
    private StructureMetadata sm(String id) {
        return new StructureMetadata(id, id, "cat", java.util.Set.of(), "desc", "behavior", "notes");
    }

    private ImplementationMetadata im(String id) {
        return new ImplementationMetadata(id, id, "structId", "desc", java.util.Map.of(), "O(1)", Object.class);
    }

    @Test
    public void testCreateStackRuntime() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm("stack"), im("impl-array-stack"));
        assertTrue(rt instanceof StackRuntimeAdapter);
    }

    @Test
    public void testCreateQueueRuntime() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm("queue"), im("impl-linked-queue"));
        assertTrue(rt instanceof QueueRuntimeAdapter);
    }

    @Test
    public void testCreateArrayRuntime() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm("array"), im("impl-dynamic-array"));
        assertTrue(rt instanceof ArrayRuntimeAdapter);
    }

    @Test
    public void testCreateListRuntime() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm("list"), im("impl-singly-linked-list"));
        assertTrue(rt instanceof ListRuntimeAdapter);
    }

    @Test
    public void testCreateDequeRuntime() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm("deque"), im("impl-array-deque"));
        assertTrue(rt instanceof DequeRuntimeAdapter);
    }

    @Test
    public void testCreateHeapRuntime() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm("heap"), im("impl-binary-heap"));
        assertTrue(rt instanceof HeapRuntimeAdapter);
    }

    @Test
    public void testCreateHashTableRuntime() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm("hash"), im("impl-hash-table-chaining"));
        assertTrue(rt instanceof HashRuntimeAdapter);
    }

    @Test
    public void testCreateHashSetRuntime() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm("hash"), im("impl-hash-set"));
        assertTrue(rt instanceof HashRuntimeAdapter);
    }

    @Test
    public void testUnknownStructure() {
        assertThrows(UnsupportedOperationException.class, () -> {
            RuntimeFactory.createRuntime(sm("unknown"), im("impl-unknown"));
        });
    }
}
