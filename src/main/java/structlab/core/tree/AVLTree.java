package structlab.core.tree;

import structlab.trace.Traceable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AVL Tree — a self-balancing Binary Search Tree where the height difference
 * between left and right subtrees of any node is at most 1.
 * Supports insert, contains, remove, min, max, inorder/preorder/postorder
 * traversals, height, and size queries.
 */
public class AVLTree<T extends Comparable<T>> implements Traceable {

    private static class Node<T> {
        T value;
        Node<T> left;
        Node<T> right;
        int height;

        Node(T value) {
            this.value = value;
            this.height = 0;
        }
    }

    private Node<T> root;
    private int size;
    private String lastRotation;

    public AVLTree() {
        this.root = null;
        this.size = 0;
        this.lastRotation = null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the description of the last rotation performed, or null if none.
     * Reset on each insert/remove call.
     */
    public String lastRotation() {
        return lastRotation;
    }

    // ── Core operations ─────────────────────────────────────

    public void insert(T value) {
        lastRotation = null;
        root = insert(root, value);
    }

    private Node<T> insert(Node<T> node, T value) {
        if (node == null) {
            size++;
            return new Node<>(value);
        }
        int cmp = value.compareTo(node.value);
        if (cmp < 0) {
            node.left = insert(node.left, value);
        } else if (cmp > 0) {
            node.right = insert(node.right, value);
        } else {
            return node; // duplicate: no-op
        }
        return rebalance(node);
    }

    public boolean contains(T value) {
        return contains(root, value);
    }

    private boolean contains(Node<T> node, T value) {
        if (node == null) return false;
        int cmp = value.compareTo(node.value);
        if (cmp < 0) return contains(node.left, value);
        if (cmp > 0) return contains(node.right, value);
        return true;
    }

    public void remove(T value) {
        lastRotation = null;
        int before = size;
        root = remove(root, value);
        if (size == before) {
            throw new IllegalArgumentException("Value not found: " + value);
        }
    }

    private Node<T> remove(Node<T> node, T value) {
        if (node == null) return null;
        int cmp = value.compareTo(node.value);
        if (cmp < 0) {
            node.left = remove(node.left, value);
        } else if (cmp > 0) {
            node.right = remove(node.right, value);
        } else {
            if (node.left == null) { size--; return node.right; }
            if (node.right == null) { size--; return node.left; }
            Node<T> successor = findMin(node.right);
            node.value = successor.value;
            node.right = remove(node.right, successor.value);
        }
        return rebalance(node);
    }

    public T min() {
        if (isEmpty()) throw new IllegalStateException("Cannot find min in an empty tree.");
        return findMin(root).value;
    }

    private Node<T> findMin(Node<T> node) {
        while (node.left != null) node = node.left;
        return node;
    }

    public T max() {
        if (isEmpty()) throw new IllegalStateException("Cannot find max in an empty tree.");
        Node<T> current = root;
        while (current.right != null) current = current.right;
        return current.value;
    }

    public int height() {
        return nodeHeight(root);
    }

    // ── Traversals ──────────────────────────────────────────

    public List<T> inorder() {
        List<T> result = new ArrayList<>();
        inorder(root, result);
        return Collections.unmodifiableList(result);
    }

    private void inorder(Node<T> node, List<T> result) {
        if (node == null) return;
        inorder(node.left, result);
        result.add(node.value);
        inorder(node.right, result);
    }

    public List<T> preorder() {
        List<T> result = new ArrayList<>();
        preorder(root, result);
        return Collections.unmodifiableList(result);
    }

    private void preorder(Node<T> node, List<T> result) {
        if (node == null) return;
        result.add(node.value);
        preorder(node.left, result);
        preorder(node.right, result);
    }

    public List<T> postorder() {
        List<T> result = new ArrayList<>();
        postorder(root, result);
        return Collections.unmodifiableList(result);
    }

    private void postorder(Node<T> node, List<T> result) {
        if (node == null) return;
        postorder(node.left, result);
        postorder(node.right, result);
        result.add(node.value);
    }

    // ── AVL rotations ───────────────────────────────────────

    private int nodeHeight(Node<T> node) {
        return node == null ? -1 : node.height;
    }

    private int balanceFactor(Node<T> node) {
        return node == null ? 0 : nodeHeight(node.left) - nodeHeight(node.right);
    }

    private void updateHeight(Node<T> node) {
        node.height = 1 + Math.max(nodeHeight(node.left), nodeHeight(node.right));
    }

    private Node<T> rebalance(Node<T> node) {
        updateHeight(node);
        int bf = balanceFactor(node);

        if (bf > 1) {
            if (balanceFactor(node.left) < 0) {
                // Left-Right case
                node.left = rotateLeft(node.left);
                lastRotation = "Left-Right";
            } else {
                lastRotation = "Right";
            }
            return rotateRight(node);
        }
        if (bf < -1) {
            if (balanceFactor(node.right) > 0) {
                // Right-Left case
                node.right = rotateRight(node.right);
                lastRotation = "Right-Left";
            } else {
                lastRotation = "Left";
            }
            return rotateLeft(node);
        }
        return node;
    }

    private Node<T> rotateRight(Node<T> y) {
        Node<T> x = y.left;
        y.left = x.right;
        x.right = y;
        updateHeight(y);
        updateHeight(x);
        return x;
    }

    private Node<T> rotateLeft(Node<T> x) {
        Node<T> y = x.right;
        x.right = y.left;
        y.left = x;
        updateHeight(x);
        updateHeight(y);
        return y;
    }

    // ── Traceable ────────────────────────────────────────────

    @Override
    public String structureName() {
        return "AVL Tree";
    }

    @Override
    public String implementationName() {
        return "AVLTree";
    }

    @Override
    public boolean checkInvariant() {
        return isBST(root, null, null) && isBalanced(root);
    }

    private boolean isBST(Node<T> node, T lower, T upper) {
        if (node == null) return true;
        if (lower != null && node.value.compareTo(lower) <= 0) return false;
        if (upper != null && node.value.compareTo(upper) >= 0) return false;
        return isBST(node.left, lower, node.value) && isBST(node.right, node.value, upper);
    }

    private boolean isBalanced(Node<T> node) {
        if (node == null) return true;
        int bf = balanceFactor(node);
        if (bf < -1 || bf > 1) return false;
        return isBalanced(node.left) && isBalanced(node.right);
    }

    @Override
    public String snapshot() {
        StringBuilder sb = new StringBuilder();
        sb.append("AVLTree{size=").append(size);
        sb.append(", height=").append(height());
        sb.append(", root=").append(root == null ? "null" : root.value);
        sb.append(", tree=");
        appendTree(sb, root);
        sb.append('}');
        return sb.toString();
    }

    private void appendTree(StringBuilder sb, Node<T> node) {
        if (node == null) {
            sb.append('_');
            return;
        }
        sb.append('(').append(node.value).append(' ');
        appendTree(sb, node.left);
        sb.append(' ');
        appendTree(sb, node.right);
        sb.append(')');
    }
}
