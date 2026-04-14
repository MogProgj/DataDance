package structlab.trace;

import structlab.core.tree.BinarySearchTree;

/**
 * Traced wrapper around {@link BinarySearchTree}. Each operation captures a
 * {@link TraceStep} with before/after snapshots, invariant checks, complexity
 * notes, and human-readable explanations.
 */
public class TracedBinarySearchTree<T extends Comparable<T>> {

    private final BinarySearchTree<T> tree;
    private final TraceLog log;

    public TracedBinarySearchTree(BinarySearchTree<T> tree, TraceLog log) {
        this.tree = tree;
        this.log = log;
    }

    public void insert(T value) {
        String before = tree.snapshot();
        boolean contained = tree.contains(value);

        tree.insert(value);

        String after = tree.snapshot();
        String explanation;
        if (contained) {
            explanation = "Value " + value + " already exists. No change.";
        } else if (tree.size() == 1) {
            explanation = "Inserted " + value + " as the root of an empty tree.";
        } else {
            explanation = "Inserted " + value + " into the tree. Traversed from root to find correct position.";
        }

        log.add(new TraceStep(
                tree.structureName(), tree.implementationName(), "insert",
                String.valueOf(value), before, after,
                InvariantResult.fromBoolean(tree.checkInvariant()),
                "O(h) — h is tree height", explanation));
    }

    public boolean contains(T value) {
        String before = tree.snapshot();
        boolean found = tree.contains(value);

        String explanation = found
                ? "Found " + value + " by traversing from root."
                : "Value " + value + " not found after searching the tree.";

        log.add(new TraceStep(
                tree.structureName(), tree.implementationName(), "contains",
                String.valueOf(value), before, before,
                InvariantResult.fromBoolean(tree.checkInvariant()),
                "O(h) — h is tree height", explanation));

        return found;
    }

    public void remove(T value) {
        String before = tree.snapshot();

        if (!tree.contains(value)) {
            log.add(new TraceStep(
                    tree.structureName(), tree.implementationName(), "remove",
                    String.valueOf(value), before, before,
                    InvariantResult.fromBoolean(tree.checkInvariant()),
                    "O(h) — h is tree height",
                    "FAILED: Value " + value + " not found in tree."));
            tree.remove(value); // let it throw
        }

        tree.remove(value);

        String after = tree.snapshot();
        log.add(new TraceStep(
                tree.structureName(), tree.implementationName(), "remove",
                String.valueOf(value), before, after,
                InvariantResult.fromBoolean(tree.checkInvariant()),
                "O(h) — h is tree height",
                "Removed " + value + ". Tree restructured to maintain BST property."));
    }

    public T min() {
        String before = tree.snapshot();

        if (tree.isEmpty()) {
            log.add(new TraceStep(
                    tree.structureName(), tree.implementationName(), "min",
                    null, before, before,
                    InvariantResult.fromBoolean(tree.checkInvariant()),
                    "O(h)", "FAILED: Cannot find min in an empty tree."));
            tree.min(); // let it throw
        }

        T value = tree.min();
        log.add(new TraceStep(
                tree.structureName(), tree.implementationName(), "min",
                null, before, before,
                InvariantResult.fromBoolean(tree.checkInvariant()),
                "O(h)",
                "Minimum is " + value + ". Followed left pointers from root."));
        return value;
    }

    public T max() {
        String before = tree.snapshot();

        if (tree.isEmpty()) {
            log.add(new TraceStep(
                    tree.structureName(), tree.implementationName(), "max",
                    null, before, before,
                    InvariantResult.fromBoolean(tree.checkInvariant()),
                    "O(h)", "FAILED: Cannot find max in an empty tree."));
            tree.max(); // let it throw
        }

        T value = tree.max();
        log.add(new TraceStep(
                tree.structureName(), tree.implementationName(), "max",
                null, before, before,
                InvariantResult.fromBoolean(tree.checkInvariant()),
                "O(h)",
                "Maximum is " + value + ". Followed right pointers from root."));
        return value;
    }

    public java.util.List<T> inorder() {
        String before = tree.snapshot();
        java.util.List<T> result = tree.inorder();
        log.add(new TraceStep(
                tree.structureName(), tree.implementationName(), "inorder",
                null, before, before,
                InvariantResult.fromBoolean(tree.checkInvariant()),
                "O(N)",
                "In-order traversal: " + result + ". Visits nodes in sorted order (left, root, right)."));
        return result;
    }

    public java.util.List<T> preorder() {
        String before = tree.snapshot();
        java.util.List<T> result = tree.preorder();
        log.add(new TraceStep(
                tree.structureName(), tree.implementationName(), "preorder",
                null, before, before,
                InvariantResult.fromBoolean(tree.checkInvariant()),
                "O(N)",
                "Pre-order traversal: " + result + ". Visits root before subtrees (root, left, right)."));
        return result;
    }

    public java.util.List<T> postorder() {
        String before = tree.snapshot();
        java.util.List<T> result = tree.postorder();
        log.add(new TraceStep(
                tree.structureName(), tree.implementationName(), "postorder",
                null, before, before,
                InvariantResult.fromBoolean(tree.checkInvariant()),
                "O(N)",
                "Post-order traversal: " + result + ". Visits subtrees before root (left, right, root)."));
        return result;
    }

    public BinarySearchTree<T> unwrap() {
        return tree;
    }

    public TraceLog traceLog() {
        return log;
    }
}
