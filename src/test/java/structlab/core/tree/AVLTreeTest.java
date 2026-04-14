package structlab.core.tree;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AVLTreeTest {

    @Test
    void newTreeStartsEmpty() {
        AVLTree<Integer> tree = new AVLTree<>();
        assertEquals(0, tree.size());
        assertTrue(tree.isEmpty());
        assertTrue(tree.checkInvariant());
    }

    @Test
    void insertMaintainsBSTProperty() {
        AVLTree<Integer> tree = new AVLTree<>();
        tree.insert(20);
        tree.insert(10);
        tree.insert(30);

        assertEquals(3, tree.size());
        assertTrue(tree.checkInvariant());
        assertEquals(List.of(10, 20, 30), tree.inorder());
    }

    @Test
    void rightRotationOnLeftSkew() {
        // Insert 30, 20, 10 — triggers right rotation
        AVLTree<Integer> tree = new AVLTree<>();
        tree.insert(30);
        tree.insert(20);
        tree.insert(10);

        assertEquals(3, tree.size());
        assertTrue(tree.checkInvariant());
        assertEquals(1, tree.height()); // balanced: height 1
        assertEquals(List.of(10, 20, 30), tree.inorder());
    }

    @Test
    void leftRotationOnRightSkew() {
        // Insert 10, 20, 30 — triggers left rotation
        AVLTree<Integer> tree = new AVLTree<>();
        tree.insert(10);
        tree.insert(20);
        tree.insert(30);

        assertEquals(3, tree.size());
        assertTrue(tree.checkInvariant());
        assertEquals(1, tree.height());
        assertEquals(List.of(10, 20, 30), tree.inorder());
    }

    @Test
    void leftRightRotation() {
        // Insert 30, 10, 20 — triggers left-right rotation
        AVLTree<Integer> tree = new AVLTree<>();
        tree.insert(30);
        tree.insert(10);
        tree.insert(20);

        assertEquals(3, tree.size());
        assertTrue(tree.checkInvariant());
        assertEquals(1, tree.height());
        assertEquals(List.of(10, 20, 30), tree.inorder());
    }

    @Test
    void rightLeftRotation() {
        // Insert 10, 30, 20 — triggers right-left rotation
        AVLTree<Integer> tree = new AVLTree<>();
        tree.insert(10);
        tree.insert(30);
        tree.insert(20);

        assertEquals(3, tree.size());
        assertTrue(tree.checkInvariant());
        assertEquals(1, tree.height());
        assertEquals(List.of(10, 20, 30), tree.inorder());
    }

    @Test
    void balancedAfterManyInsertions() {
        AVLTree<Integer> tree = new AVLTree<>();
        // Sequential insertion — would be O(N) height in BST
        for (int i = 1; i <= 15; i++) {
            tree.insert(i);
        }

        assertEquals(15, tree.size());
        assertTrue(tree.checkInvariant());
        // For 15 nodes, AVL height should be at most 4
        assertTrue(tree.height() <= 4, "Height should be O(log N), got: " + tree.height());
    }

    @Test
    void insertDuplicateDoesNotChangeSize() {
        AVLTree<Integer> tree = new AVLTree<>();
        tree.insert(10);
        tree.insert(10);
        assertEquals(1, tree.size());
        assertTrue(tree.checkInvariant());
    }

    @Test
    void containsFindsExistingValues() {
        AVLTree<Integer> tree = new AVLTree<>();
        tree.insert(20);
        tree.insert(10);
        tree.insert(30);

        assertTrue(tree.contains(20));
        assertTrue(tree.contains(10));
        assertTrue(tree.contains(30));
        assertFalse(tree.contains(5));
    }

    @Test
    void removeLeafNode() {
        AVLTree<Integer> tree = new AVLTree<>();
        tree.insert(20);
        tree.insert(10);
        tree.insert(30);

        tree.remove(10);
        assertEquals(2, tree.size());
        assertFalse(tree.contains(10));
        assertTrue(tree.checkInvariant());
    }

    @Test
    void removeNodeWithTwoChildren() {
        AVLTree<Integer> tree = new AVLTree<>();
        tree.insert(20);
        tree.insert(10);
        tree.insert(30);
        tree.insert(25);
        tree.insert(35);

        tree.remove(30);
        assertEquals(4, tree.size());
        assertFalse(tree.contains(30));
        assertTrue(tree.checkInvariant());
    }

    @Test
    void removeTriggersRebalancing() {
        AVLTree<Integer> tree = new AVLTree<>();
        tree.insert(20);
        tree.insert(10);
        tree.insert(30);
        tree.insert(5);

        // Removing 30 makes left subtree heavier → should rebalance
        tree.remove(30);
        assertEquals(3, tree.size());
        assertTrue(tree.checkInvariant());
        assertTrue(tree.height() <= 1);
    }

    @Test
    void removeNonExistentThrows() {
        AVLTree<Integer> tree = new AVLTree<>();
        tree.insert(10);
        assertThrows(IllegalArgumentException.class, () -> tree.remove(99));
    }

    @Test
    void minAndMax() {
        AVLTree<Integer> tree = new AVLTree<>();
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
        AVLTree<Integer> tree = new AVLTree<>();
        assertThrows(IllegalStateException.class, tree::min);
    }

    @Test
    void maxOnEmptyThrows() {
        AVLTree<Integer> tree = new AVLTree<>();
        assertThrows(IllegalStateException.class, tree::max);
    }

    @Test
    void traversals() {
        AVLTree<Integer> tree = new AVLTree<>();
        tree.insert(40);
        tree.insert(20);
        tree.insert(60);
        tree.insert(10);
        tree.insert(30);

        assertEquals(List.of(10, 20, 30, 40, 60), tree.inorder());
        assertFalse(tree.preorder().isEmpty());
        assertFalse(tree.postorder().isEmpty());
    }

    @Test
    void snapshotContainsTreeStructure() {
        AVLTree<Integer> tree = new AVLTree<>();
        tree.insert(20);
        tree.insert(10);
        tree.insert(30);

        String snap = tree.snapshot();
        assertTrue(snap.startsWith("AVLTree{"));
        assertTrue(snap.contains("size=3"));
        assertTrue(snap.contains("root="));
        assertTrue(snap.contains("tree="));
    }

    @Test
    void lastRotationReportsCorrectly() {
        AVLTree<Integer> tree = new AVLTree<>();
        tree.insert(30);
        tree.insert(20);
        assertNull(tree.lastRotation()); // no rotation yet

        tree.insert(10); // triggers right rotation
        assertNotNull(tree.lastRotation());
    }
}
