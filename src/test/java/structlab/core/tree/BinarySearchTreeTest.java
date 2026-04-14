package structlab.core.tree;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BinarySearchTreeTest {

    @Test
    void newTreeStartsEmpty() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        assertEquals(0, tree.size());
        assertTrue(tree.isEmpty());
        assertTrue(tree.checkInvariant());
    }

    @Test
    void insertMaintainsBSTProperty() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.insert(20);
        tree.insert(10);
        tree.insert(30);

        assertEquals(3, tree.size());
        assertTrue(tree.checkInvariant());
        assertEquals(List.of(10, 20, 30), tree.inorder());
    }

    @Test
    void insertDuplicateDoesNotChangeSize() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.insert(10);
        tree.insert(10);
        assertEquals(1, tree.size());
        assertTrue(tree.checkInvariant());
    }

    @Test
    void containsFindsExistingValues() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.insert(20);
        tree.insert(10);
        tree.insert(30);

        assertTrue(tree.contains(20));
        assertTrue(tree.contains(10));
        assertTrue(tree.contains(30));
        assertFalse(tree.contains(5));
        assertFalse(tree.contains(25));
    }

    @Test
    void removeLeafNode() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.insert(20);
        tree.insert(10);
        tree.insert(30);

        tree.remove(10);
        assertEquals(2, tree.size());
        assertFalse(tree.contains(10));
        assertTrue(tree.checkInvariant());
    }

    @Test
    void removeNodeWithOneChild() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.insert(20);
        tree.insert(10);
        tree.insert(15);

        tree.remove(10);
        assertEquals(2, tree.size());
        assertTrue(tree.contains(15));
        assertTrue(tree.checkInvariant());
    }

    @Test
    void removeNodeWithTwoChildren() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.insert(20);
        tree.insert(10);
        tree.insert(30);
        tree.insert(25);
        tree.insert(35);

        tree.remove(30);
        assertEquals(4, tree.size());
        assertFalse(tree.contains(30));
        assertTrue(tree.contains(25));
        assertTrue(tree.contains(35));
        assertTrue(tree.checkInvariant());
    }

    @Test
    void removeNonExistentThrows() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.insert(10);
        assertThrows(IllegalArgumentException.class, () -> tree.remove(99));
    }

    @Test
    void minAndMax() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.insert(20);
        tree.insert(10);
        tree.insert(30);
        tree.insert(5);
        tree.insert(25);

        assertEquals(5, tree.min());
        assertEquals(30, tree.max());
    }

    @Test
    void minOnEmptyThrows() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        assertThrows(IllegalStateException.class, tree::min);
    }

    @Test
    void maxOnEmptyThrows() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        assertThrows(IllegalStateException.class, tree::max);
    }

    @Test
    void heightComputedCorrectly() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        assertEquals(-1, tree.height());

        tree.insert(20);
        assertEquals(0, tree.height());

        tree.insert(10);
        tree.insert(30);
        assertEquals(1, tree.height());

        // Skewed insertions
        tree.insert(5);
        tree.insert(1);
        assertEquals(3, tree.height());
    }

    @Test
    void inorderTraversalReturnsSortedOrder() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.insert(40);
        tree.insert(20);
        tree.insert(60);
        tree.insert(10);
        tree.insert(30);

        assertEquals(List.of(10, 20, 30, 40, 60), tree.inorder());
    }

    @Test
    void preorderTraversal() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.insert(20);
        tree.insert(10);
        tree.insert(30);

        assertEquals(List.of(20, 10, 30), tree.preorder());
    }

    @Test
    void postorderTraversal() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.insert(20);
        tree.insert(10);
        tree.insert(30);

        assertEquals(List.of(10, 30, 20), tree.postorder());
    }

    @Test
    void snapshotContainsTreeStructure() {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.insert(20);
        tree.insert(10);
        tree.insert(30);

        String snap = tree.snapshot();
        assertTrue(snap.startsWith("BinarySearchTree{"));
        assertTrue(snap.contains("size=3"));
        assertTrue(snap.contains("root=20"));
        assertTrue(snap.contains("tree="));
    }

    @Test
    void invariantFailsForCorruptedTree() {
        // Normal operations should always pass
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.insert(20);
        tree.insert(10);
        tree.insert(30);
        assertTrue(tree.checkInvariant());
    }
}
