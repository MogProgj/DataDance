package structlab.app.runtime.adapters;

import structlab.app.runtime.OperationDescriptor;
import structlab.app.runtime.OperationExecutionResult;
import structlab.trace.TracedAVLTree;
import structlab.trace.TracedBinarySearchTree;

import java.util.List;

/**
 * Runtime adapter for ordered tree structures (BST, AVL).
 * Bridges the traced tree wrappers into the generic runtime/service layer.
 */
public class TreeRuntimeAdapter extends AbstractRuntimeAdapter {

    private final Object activeTree; // TracedBinarySearchTree or TracedAVLTree

    public TreeRuntimeAdapter(String implName, Object tree) {
        super("Ordered Tree", implName);
        this.activeTree = tree;
    }

    @Override
    public List<OperationDescriptor> getAvailableOperations() {
        return List.of(
            new OperationDescriptor("insert", List.of("add"), "Insert a value into the tree", 1, "insert <value>", true, "insert <value>", "O(h)"),
            new OperationDescriptor("remove", List.of("delete"), "Remove a value from the tree", 1, "remove <value>", true, "remove <value>", "O(h)"),
            new OperationDescriptor("contains", List.of("search", "find"), "Check if value exists in the tree", 1, "contains <value>", false, "contains <value>", "O(h)"),
            new OperationDescriptor("min", List.of(), "Find the minimum value", 0, "min", false, "min", "O(h)"),
            new OperationDescriptor("max", List.of(), "Find the maximum value", 0, "max", false, "max", "O(h)"),
            new OperationDescriptor("inorder", List.of(), "In-order traversal (sorted)", 0, "inorder", false, "inorder", "O(N)"),
            new OperationDescriptor("preorder", List.of(), "Pre-order traversal", 0, "preorder", false, "preorder", "O(N)"),
            new OperationDescriptor("postorder", List.of(), "Post-order traversal", 0, "postorder", false, "postorder", "O(N)")
        );
    }

    @Override
    public OperationExecutionResult execute(String operation, List<String> args) {
        try {
            switch (operation.toLowerCase()) {
                case "insert":
                case "add":
                    if (args.isEmpty()) throw new IllegalArgumentException("Usage: insert <value>");
                    int insertVal = parseArg(args.get(0));
                    if (activeTree instanceof TracedBinarySearchTree<?> tbst) {
                        @SuppressWarnings("unchecked")
                        TracedBinarySearchTree<Integer> t = (TracedBinarySearchTree<Integer>) tbst;
                        t.insert(insertVal);
                        return success(operation, null, t.traceLog());
                    } else if (activeTree instanceof TracedAVLTree<?> tavl) {
                        @SuppressWarnings("unchecked")
                        TracedAVLTree<Integer> t = (TracedAVLTree<Integer>) tavl;
                        t.insert(insertVal);
                        return success(operation, null, t.traceLog());
                    }
                    break;

                case "remove":
                case "delete":
                    if (args.isEmpty()) throw new IllegalArgumentException("Usage: remove <value>");
                    int removeVal = parseArg(args.get(0));
                    if (activeTree instanceof TracedBinarySearchTree<?> tbst) {
                        @SuppressWarnings("unchecked")
                        TracedBinarySearchTree<Integer> t = (TracedBinarySearchTree<Integer>) tbst;
                        t.remove(removeVal);
                        return success(operation, null, t.traceLog());
                    } else if (activeTree instanceof TracedAVLTree<?> tavl) {
                        @SuppressWarnings("unchecked")
                        TracedAVLTree<Integer> t = (TracedAVLTree<Integer>) tavl;
                        t.remove(removeVal);
                        return success(operation, null, t.traceLog());
                    }
                    break;

                case "contains":
                case "search":
                case "find":
                    if (args.isEmpty()) throw new IllegalArgumentException("Usage: contains <value>");
                    int searchVal = parseArg(args.get(0));
                    if (activeTree instanceof TracedBinarySearchTree<?> tbst) {
                        @SuppressWarnings("unchecked")
                        TracedBinarySearchTree<Integer> t = (TracedBinarySearchTree<Integer>) tbst;
                        boolean found = t.contains(searchVal);
                        return success(operation, found, t.traceLog());
                    } else if (activeTree instanceof TracedAVLTree<?> tavl) {
                        @SuppressWarnings("unchecked")
                        TracedAVLTree<Integer> t = (TracedAVLTree<Integer>) tavl;
                        boolean found = t.contains(searchVal);
                        return success(operation, found, t.traceLog());
                    }
                    break;

                case "min":
                    if (activeTree instanceof TracedBinarySearchTree<?> tbst) {
                        @SuppressWarnings("unchecked")
                        TracedBinarySearchTree<Integer> t = (TracedBinarySearchTree<Integer>) tbst;
                        return success("min", t.min(), t.traceLog());
                    } else if (activeTree instanceof TracedAVLTree<?> tavl) {
                        @SuppressWarnings("unchecked")
                        TracedAVLTree<Integer> t = (TracedAVLTree<Integer>) tavl;
                        return success("min", t.min(), t.traceLog());
                    }
                    break;

                case "max":
                    if (activeTree instanceof TracedBinarySearchTree<?> tbst) {
                        @SuppressWarnings("unchecked")
                        TracedBinarySearchTree<Integer> t = (TracedBinarySearchTree<Integer>) tbst;
                        return success("max", t.max(), t.traceLog());
                    } else if (activeTree instanceof TracedAVLTree<?> tavl) {
                        @SuppressWarnings("unchecked")
                        TracedAVLTree<Integer> t = (TracedAVLTree<Integer>) tavl;
                        return success("max", t.max(), t.traceLog());
                    }
                    break;

                case "inorder":
                    if (activeTree instanceof TracedBinarySearchTree<?> tbst) {
                        @SuppressWarnings("unchecked")
                        TracedBinarySearchTree<Integer> t = (TracedBinarySearchTree<Integer>) tbst;
                        return success("inorder", t.inorder(), t.traceLog());
                    } else if (activeTree instanceof TracedAVLTree<?> tavl) {
                        @SuppressWarnings("unchecked")
                        TracedAVLTree<Integer> t = (TracedAVLTree<Integer>) tavl;
                        return success("inorder", t.inorder(), t.traceLog());
                    }
                    break;

                case "preorder":
                    if (activeTree instanceof TracedBinarySearchTree<?> tbst) {
                        @SuppressWarnings("unchecked")
                        TracedBinarySearchTree<Integer> t = (TracedBinarySearchTree<Integer>) tbst;
                        return success("preorder", t.preorder(), t.traceLog());
                    } else if (activeTree instanceof TracedAVLTree<?> tavl) {
                        @SuppressWarnings("unchecked")
                        TracedAVLTree<Integer> t = (TracedAVLTree<Integer>) tavl;
                        return success("preorder", t.preorder(), t.traceLog());
                    }
                    break;

                case "postorder":
                    if (activeTree instanceof TracedBinarySearchTree<?> tbst) {
                        @SuppressWarnings("unchecked")
                        TracedBinarySearchTree<Integer> t = (TracedBinarySearchTree<Integer>) tbst;
                        return success("postorder", t.postorder(), t.traceLog());
                    } else if (activeTree instanceof TracedAVLTree<?> tavl) {
                        @SuppressWarnings("unchecked")
                        TracedAVLTree<Integer> t = (TracedAVLTree<Integer>) tavl;
                        return success("postorder", t.postorder(), t.traceLog());
                    }
                    break;

                default:
                    throw new UnsupportedOperationException("Unknown tree operation: " + operation);
            }
        } catch (Exception e) {
            return error(operation, e, getTraceLogFrom(activeTree));
        }
        return error(operation, new IllegalStateException("Invalid active tree state"));
    }

    @Override
    public String getCurrentState() {
        if (activeTree instanceof TracedBinarySearchTree<?> tbst) return tbst.unwrap().snapshot();
        if (activeTree instanceof TracedAVLTree<?> tavl) return tavl.unwrap().snapshot();
        return "[]";
    }

    @Override
    public void clearTraceHistory() {
        if (activeTree instanceof TracedBinarySearchTree<?> tbst) tbst.traceLog().clear();
        if (activeTree instanceof TracedAVLTree<?> tavl) tavl.traceLog().clear();
    }

    @Override
    public void reset() {
        if (activeTree instanceof TracedBinarySearchTree<?> tbst) {
            @SuppressWarnings("unchecked")
            TracedBinarySearchTree<Integer> t = (TracedBinarySearchTree<Integer>) tbst;
            while (!t.unwrap().isEmpty()) {
                try { t.unwrap().remove(t.unwrap().min()); } catch (Exception ignored) { break; }
            }
        }
        if (activeTree instanceof TracedAVLTree<?> tavl) {
            @SuppressWarnings("unchecked")
            TracedAVLTree<Integer> t = (TracedAVLTree<Integer>) tavl;
            while (!t.unwrap().isEmpty()) {
                try { t.unwrap().remove(t.unwrap().min()); } catch (Exception ignored) { break; }
            }
        }
        clearTraceHistory();
    }
}
