package structlab.core.tree;

import structlab.trace.Traceable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A standard Binary Search Tree with no self-balancing.
 * Supports insert, contains, remove, min, max, inorder/preorder/postorder
 * traversals, height, and size queries.
 */
public class BinarySearchTree<T extends Comparable<T>> implements Traceable {

    private static class Node<T> {
        T value;
        Node<T> left;
        Node<T> right;

        Node(T value) {
            this.value = value;
        }
    }

    private Node<T> root;
    private int size;

    public BinarySearchTree() {
        this.root = null;
        this.size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void insert(T value) {
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
        }
        // duplicate: no-op
        return node;
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
            // Found the node to remove
            if (node.left == null) {
                size--;
                return node.right;
            }
            if (node.right == null) {
                size--;
                return node.left;
            }
            // Two children: replace with inorder successor (min of right subtree)
            Node<T> successor = findMin(node.right);
            node.value = successor.value;
            node.right = remove(node.right, successor.value);
        }
        return node;
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
        return height(root);
    }

    private int height(Node<T> node) {
        if (node == null) return -1;
        return 1 + Math.max(height(node.left), height(node.right));
    }

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

    // ── Traceable ────────────────────────────────────────────

    @Override
    public String structureName() {
        return "Binary Search Tree";
    }

    @Override
    public String implementationName() {
        return "BinarySearchTree";
    }

    @Override
    public boolean checkInvariant() {
        return isBST(root, null, null);
    }

    private boolean isBST(Node<T> node, T lower, T upper) {
        if (node == null) return true;
        if (lower != null && node.value.compareTo(lower) <= 0) return false;
        if (upper != null && node.value.compareTo(upper) >= 0) return false;
        return isBST(node.left, lower, node.value) && isBST(node.right, node.value, upper);
    }

    @Override
    public String snapshot() {
        StringBuilder sb = new StringBuilder();
        sb.append("BinarySearchTree{size=").append(size);
        sb.append(", height=").append(height());
        sb.append(", root=").append(root == null ? "null" : root.value);
        sb.append(", tree=");
        appendTree(sb, root);
        sb.append('}');
        return sb.toString();
    }

    /**
     * Serialises the tree in a pre-order parenthesised format:
     * {@code (value left right)} where null children are {@code _}.
     */
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
