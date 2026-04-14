package structlab.app.runtime;

import org.junit.jupiter.api.Test;
import structlab.app.runtime.adapters.TreeRuntimeAdapter;
import structlab.registry.ImplementationMetadata;
import structlab.registry.StructureMetadata;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TreeRuntimeAdapterTest {

    private StructureMetadata sm() {
        return new StructureMetadata("struct-tree", "Ordered Tree", "Tree",
                Set.of(), "desc", "behavior", "notes");
    }

    private ImplementationMetadata bstIm() {
        return new ImplementationMetadata("impl-bst", "Binary Search Tree",
                "struct-tree", "desc", Map.of(), "O(h)", Object.class);
    }

    private ImplementationMetadata avlIm() {
        return new ImplementationMetadata("impl-avl", "AVL Tree",
                "struct-tree", "desc", Map.of(), "O(log N)", Object.class);
    }

    // ── Factory wiring ──

    @Test
    void factoryCreatesBstRuntime() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), bstIm());
        assertInstanceOf(TreeRuntimeAdapter.class, rt);
    }

    @Test
    void factoryCreatesAvlRuntime() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), avlIm());
        assertInstanceOf(TreeRuntimeAdapter.class, rt);
    }

    // ── Operations ──

    @Test
    void bstExposesExpectedOperations() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), bstIm());
        List<String> opNames = rt.getAvailableOperations().stream()
                .map(OperationDescriptor::name).toList();
        assertEquals(List.of("insert", "remove", "contains", "min", "max",
                "inorder", "preorder", "postorder"), opNames);
    }

    @Test
    void insertAndContainsBst() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), bstIm());
        OperationExecutionResult r1 = rt.execute("insert", List.of("20"));
        assertTrue(r1.success());

        OperationExecutionResult r2 = rt.execute("contains", List.of("20"));
        assertTrue(r2.success());
        assertEquals("true", r2.returnedValue());
    }

    @Test
    void insertAndContainsAvl() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), avlIm());
        rt.execute("insert", List.of("20"));

        OperationExecutionResult r = rt.execute("contains", List.of("20"));
        assertTrue(r.success());
        assertEquals("true", r.returnedValue());
    }

    @Test
    void insertAndRemoveBst() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), bstIm());
        rt.execute("insert", List.of("20"));
        rt.execute("insert", List.of("10"));

        OperationExecutionResult r = rt.execute("remove", List.of("10"));
        assertTrue(r.success());

        OperationExecutionResult check = rt.execute("contains", List.of("10"));
        assertEquals("false", check.returnedValue());
    }

    @Test
    void removeNonExistentReturnsError() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), bstIm());
        rt.execute("insert", List.of("20"));

        OperationExecutionResult r = rt.execute("remove", List.of("99"));
        assertFalse(r.success());
    }

    @Test
    void minAndMax() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), bstIm());
        rt.execute("insert", List.of("20"));
        rt.execute("insert", List.of("10"));
        rt.execute("insert", List.of("30"));

        OperationExecutionResult minResult = rt.execute("min", List.of());
        assertTrue(minResult.success());
        assertEquals("10", minResult.returnedValue());

        OperationExecutionResult maxResult = rt.execute("max", List.of());
        assertTrue(maxResult.success());
        assertEquals("30", maxResult.returnedValue());
    }

    @Test
    void traversalsReturnLists() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), bstIm());
        rt.execute("insert", List.of("20"));
        rt.execute("insert", List.of("10"));
        rt.execute("insert", List.of("30"));

        OperationExecutionResult inR = rt.execute("inorder", List.of());
        assertTrue(inR.success());
        assertEquals("[10, 20, 30]", inR.returnedValue());

        OperationExecutionResult preR = rt.execute("preorder", List.of());
        assertTrue(preR.success());

        OperationExecutionResult postR = rt.execute("postorder", List.of());
        assertTrue(postR.success());
    }

    @Test
    void aliasesWork() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), bstIm());
        // "add" is alias for "insert"
        OperationExecutionResult r = rt.execute("add", List.of("42"));
        assertTrue(r.success());

        // "search" is alias for "contains"
        OperationExecutionResult r2 = rt.execute("search", List.of("42"));
        assertTrue(r2.success());
        assertEquals("true", r2.returnedValue());

        // "delete" is alias for "remove"
        OperationExecutionResult r3 = rt.execute("delete", List.of("42"));
        assertTrue(r3.success());
    }

    @Test
    void getCurrentStateReturnsSnapshot() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), bstIm());
        rt.execute("insert", List.of("20"));
        String state = rt.getCurrentState();
        assertTrue(state.startsWith("BinarySearchTree{"));
    }

    @Test
    void resetClearsTree() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), bstIm());
        rt.execute("insert", List.of("20"));
        rt.execute("insert", List.of("10"));
        rt.reset();

        String state = rt.getCurrentState();
        assertTrue(state.contains("size=0"));
    }

    @Test
    void unknownOperationReturnsError() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), bstIm());
        OperationExecutionResult r = rt.execute("foobar", List.of());
        assertFalse(r.success());
    }

    @Test
    void insertWithoutArgReturnsError() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), bstIm());
        OperationExecutionResult r = rt.execute("insert", List.of());
        assertFalse(r.success());
    }

    @Test
    void traceStepsAccumulateAcrossOperations() {
        StructureRuntime rt = RuntimeFactory.createRuntime(sm(), bstIm());
        rt.execute("insert", List.of("20"));
        rt.execute("insert", List.of("10"));
        rt.execute("contains", List.of("20"));

        OperationExecutionResult lastResult = rt.execute("min", List.of());
        assertNotNull(lastResult.traceSteps());
        assertFalse(lastResult.traceSteps().isEmpty());
    }
}
