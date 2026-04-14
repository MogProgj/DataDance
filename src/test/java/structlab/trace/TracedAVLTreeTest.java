package structlab.trace;

import org.junit.jupiter.api.Test;
import structlab.core.tree.AVLTree;

import static org.junit.jupiter.api.Assertions.*;

class TracedAVLTreeTest {

    @Test
    void insertProducesTraceStep() {
        AVLTree<Integer> tree = new AVLTree<>();
        TraceLog log = new TraceLog();
        TracedAVLTree<Integer> traced = new TracedAVLTree<>(tree, log);

        traced.insert(20);

        assertEquals(1, log.size());
        TraceStep step = log.steps().get(0);
        assertEquals("insert", step.operationName());
        assertEquals("20", step.input());
        assertEquals(InvariantResult.PASSED, step.invariantResult());
        assertTrue(step.explanation().contains("root"));
    }

    @Test
    void insertWithRotationReportsRotationInExplanation() {
        AVLTree<Integer> tree = new AVLTree<>();
        TraceLog log = new TraceLog();
        TracedAVLTree<Integer> traced = new TracedAVLTree<>(tree, log);

        // 3, 2, 1 → right rotation on insert(1)
        traced.insert(3);
        traced.insert(2);
        traced.insert(1);

        TraceStep step = log.steps().get(2);
        assertEquals("insert", step.operationName());
        assertTrue(step.explanation().contains("rotation"),
                "Expected rotation info in: " + step.explanation());
    }

    @Test
    void insertWithoutRotationExplainsNoRotation() {
        AVLTree<Integer> tree = new AVLTree<>();
        TraceLog log = new TraceLog();
        TracedAVLTree<Integer> traced = new TracedAVLTree<>(tree, log);

        traced.insert(20);
        traced.insert(30); // no rotation needed

        TraceStep step = log.steps().get(1);
        assertTrue(step.explanation().contains("No rotation"),
                "Expected 'No rotation' in: " + step.explanation());
    }

    @Test
    void containsProducesTraceStep() {
        AVLTree<Integer> tree = new AVLTree<>();
        TraceLog log = new TraceLog();
        TracedAVLTree<Integer> traced = new TracedAVLTree<>(tree, log);

        traced.insert(20);
        boolean found = traced.contains(20);

        assertTrue(found);
        assertEquals(2, log.size());
        TraceStep step = log.steps().get(1);
        assertEquals("contains", step.operationName());
        assertEquals(step.beforeState(), step.afterState());
    }

    @Test
    void removeProducesTraceStep() {
        AVLTree<Integer> tree = new AVLTree<>();
        TraceLog log = new TraceLog();
        TracedAVLTree<Integer> traced = new TracedAVLTree<>(tree, log);

        traced.insert(20);
        traced.insert(10);
        traced.remove(10);

        TraceStep step = log.steps().get(2);
        assertEquals("remove", step.operationName());
        assertEquals("10", step.input());
        assertEquals(InvariantResult.PASSED, step.invariantResult());
    }

    @Test
    void removeNonExistentTracesFailureThenThrows() {
        AVLTree<Integer> tree = new AVLTree<>();
        TraceLog log = new TraceLog();
        TracedAVLTree<Integer> traced = new TracedAVLTree<>(tree, log);

        traced.insert(20);
        assertThrows(IllegalArgumentException.class, () -> traced.remove(99));

        assertEquals(2, log.size());
        assertTrue(log.steps().get(1).explanation().startsWith("FAILED:"));
    }

    @Test
    void minProducesTraceStep() {
        AVLTree<Integer> tree = new AVLTree<>();
        TraceLog log = new TraceLog();
        TracedAVLTree<Integer> traced = new TracedAVLTree<>(tree, log);

        traced.insert(20);
        traced.insert(10);
        int min = traced.min();

        assertEquals(10, min);
        TraceStep step = log.steps().get(2);
        assertEquals("min", step.operationName());
        assertEquals("O(log N)", step.complexityNote());
    }

    @Test
    void maxProducesTraceStep() {
        AVLTree<Integer> tree = new AVLTree<>();
        TraceLog log = new TraceLog();
        TracedAVLTree<Integer> traced = new TracedAVLTree<>(tree, log);

        traced.insert(20);
        traced.insert(30);
        int max = traced.max();

        assertEquals(30, max);
        TraceStep step = log.steps().get(2);
        assertEquals("max", step.operationName());
        assertEquals("O(log N)", step.complexityNote());
    }

    @Test
    void traversalProducesTraceStep() {
        AVLTree<Integer> tree = new AVLTree<>();
        TraceLog log = new TraceLog();
        TracedAVLTree<Integer> traced = new TracedAVLTree<>(tree, log);

        traced.insert(20);
        traced.insert(10);
        traced.insert(30);
        traced.inorder();
        traced.preorder();
        traced.postorder();

        assertEquals(6, log.size());
        assertEquals("inorder", log.steps().get(3).operationName());
        assertEquals("preorder", log.steps().get(4).operationName());
        assertEquals("postorder", log.steps().get(5).operationName());
        assertEquals("O(N)", log.steps().get(3).complexityNote());
    }

    @Test
    void unwrapReturnsOriginalTree() {
        AVLTree<Integer> tree = new AVLTree<>();
        TracedAVLTree<Integer> traced = new TracedAVLTree<>(tree, new TraceLog());
        assertSame(tree, traced.unwrap());
    }
}
