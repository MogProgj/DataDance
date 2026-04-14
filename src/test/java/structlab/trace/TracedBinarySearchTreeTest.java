package structlab.trace;

import org.junit.jupiter.api.Test;
import structlab.core.tree.BinarySearchTree;

import static org.junit.jupiter.api.Assertions.*;

class TracedBinarySearchTreeTest {

    @Test
    void insertProducesTraceStep() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        TraceLog log = new TraceLog();
        TracedBinarySearchTree<Integer> traced = new TracedBinarySearchTree<>(tree, log);

        traced.insert(20);

        assertEquals(1, log.size());
        TraceStep step = log.steps().get(0);
        assertEquals("insert", step.operationName());
        assertEquals("20", step.input());
        assertEquals(InvariantResult.PASSED, step.invariantResult());
        assertTrue(step.explanation().contains("root"));
    }

    @Test
    void containsProducesTraceStep() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        TraceLog log = new TraceLog();
        TracedBinarySearchTree<Integer> traced = new TracedBinarySearchTree<>(tree, log);

        traced.insert(20);
        boolean found = traced.contains(20);

        assertTrue(found);
        assertEquals(2, log.size());
        TraceStep step = log.steps().get(1);
        assertEquals("contains", step.operationName());
        assertEquals("20", step.input());
        assertEquals(step.beforeState(), step.afterState()); // no structural change
    }

    @Test
    void containsNotFoundProducesTraceStep() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        TraceLog log = new TraceLog();
        TracedBinarySearchTree<Integer> traced = new TracedBinarySearchTree<>(tree, log);

        traced.insert(20);
        boolean found = traced.contains(99);

        assertFalse(found);
        TraceStep step = log.steps().get(1);
        assertTrue(step.explanation().contains("not found"));
    }

    @Test
    void removeProducesTraceStep() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        TraceLog log = new TraceLog();
        TracedBinarySearchTree<Integer> traced = new TracedBinarySearchTree<>(tree, log);

        traced.insert(20);
        traced.insert(10);
        traced.remove(10);

        TraceStep step = log.steps().get(2);
        assertEquals("remove", step.operationName());
        assertEquals("10", step.input());
        assertEquals(InvariantResult.PASSED, step.invariantResult());
        assertNotEquals(step.beforeState(), step.afterState());
    }

    @Test
    void removeNonExistentTracesFailureThenThrows() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        TraceLog log = new TraceLog();
        TracedBinarySearchTree<Integer> traced = new TracedBinarySearchTree<>(tree, log);

        traced.insert(20);
        assertThrows(IllegalArgumentException.class, () -> traced.remove(99));

        assertEquals(2, log.size());
        assertTrue(log.steps().get(1).explanation().startsWith("FAILED:"));
    }

    @Test
    void minProducesTraceStep() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        TraceLog log = new TraceLog();
        TracedBinarySearchTree<Integer> traced = new TracedBinarySearchTree<>(tree, log);

        traced.insert(20);
        traced.insert(10);
        int min = traced.min();

        assertEquals(10, min);
        TraceStep step = log.steps().get(2);
        assertEquals("min", step.operationName());
        assertTrue(step.explanation().contains("10"));
    }

    @Test
    void maxProducesTraceStep() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        TraceLog log = new TraceLog();
        TracedBinarySearchTree<Integer> traced = new TracedBinarySearchTree<>(tree, log);

        traced.insert(20);
        traced.insert(30);
        int max = traced.max();

        assertEquals(30, max);
        TraceStep step = log.steps().get(2);
        assertEquals("max", step.operationName());
        assertTrue(step.explanation().contains("30"));
    }

    @Test
    void inorderProducesTraceStep() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        TraceLog log = new TraceLog();
        TracedBinarySearchTree<Integer> traced = new TracedBinarySearchTree<>(tree, log);

        traced.insert(20);
        traced.insert(10);
        traced.insert(30);
        var result = traced.inorder();

        assertEquals(java.util.List.of(10, 20, 30), result);
        TraceStep step = log.steps().get(3);
        assertEquals("inorder", step.operationName());
        assertTrue(step.explanation().contains("sorted"));
    }

    @Test
    void unwrapReturnsOriginalTree() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        TracedBinarySearchTree<Integer> traced = new TracedBinarySearchTree<>(tree, new TraceLog());
        assertSame(tree, traced.unwrap());
    }
}
