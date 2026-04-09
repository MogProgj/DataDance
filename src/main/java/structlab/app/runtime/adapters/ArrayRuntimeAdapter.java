package structlab.app.runtime.adapters;

import structlab.app.runtime.OperationDescriptor;
import structlab.app.runtime.OperationExecutionResult;
import structlab.trace.TracedDynamicArray;
import structlab.trace.TracedFixedArray;

import java.util.List;

public class ArrayRuntimeAdapter extends AbstractRuntimeAdapter {

    private final Object activeArray; // Holds TracedFixedArray or TracedDynamicArray

    public ArrayRuntimeAdapter(String implName, Object array) {
        super("Array", implName);
        this.activeArray = array;
    }

    @Override
    public List<OperationDescriptor> getAvailableOperations() {
        return List.of(
                new OperationDescriptor("append", List.of(), "Append an element to the end", 1, "append <value>", true, "append <value>", "O(N)"),
                new OperationDescriptor("insert", List.of(), "Insert an element at the specified index", 2, "insert <index> <value>", true, "insert <index> <value>", "O(N)"),
                new OperationDescriptor("removeat", List.of(), "Remove an element at the specified index", 1, "removeat <index>", true, "removeat <index>", "O(N)"),
                new OperationDescriptor("get", List.of(), "Get the element at the specified index", 1, "get <index>", false, "get <index>", "O(N)")
        );
    }

    @Override
    public OperationExecutionResult execute(String operation, List<String> args) {
        try {
            switch (operation.toLowerCase()) {
                case "append":
                    if (args.isEmpty()) throw new IllegalArgumentException("Usage: append <value>");
                    int val = parseArg(args.get(0));
                    if (activeArray instanceof TracedFixedArray tfa) {
                        tfa.append(val);
                        return success("append", null, tfa.traceLog());
                    } else if (activeArray instanceof TracedDynamicArray tda) {
                        tda.append(val);
                        return success("append", null, tda.traceLog());
                    }
                    break;
                case "insert":
                    if (args.size() < 2) throw new IllegalArgumentException("Usage: insert <index> <value>");
                    int idx = parseArg(args.get(0));
                    int v = parseArg(args.get(1));
                    if (activeArray instanceof TracedFixedArray tfa) {
                        tfa.insert(idx, v);
                        return success("insert", null, tfa.traceLog());
                    } else if (activeArray instanceof TracedDynamicArray tda) {
                        tda.insert(idx, v);
                        return success("insert", null, tda.traceLog());
                    }
                    break;
                case "removeat":
                    if (args.isEmpty()) throw new IllegalArgumentException("Usage: removeat <index>");
                    int removeIdx = parseArg(args.get(0));
                    if (activeArray instanceof TracedFixedArray tfa) {
                        return success("removeat", tfa.removeAt(removeIdx), tfa.traceLog());
                    } else if (activeArray instanceof TracedDynamicArray tda) {
                        return success("removeat", tda.removeAt(removeIdx), tda.traceLog());
                    }
                    break;
                case "get":
                    if (args.isEmpty()) throw new IllegalArgumentException("Usage: get <index>");
                    int getIdx = parseArg(args.get(0));
                    if (activeArray instanceof TracedFixedArray tfa) {
                        return success("get", tfa.get(getIdx), tfa.traceLog());
                    } else if (activeArray instanceof TracedDynamicArray tda) {
                        return success("get", tda.get(getIdx), tda.traceLog());
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown array operation: " + operation);
            }
        } catch (Exception e) {
            return error(operation, e, getTraceLogFrom(activeArray));
        }
        return error(operation, new IllegalStateException("Invalid active array state"));
    }

    @Override
    public String getCurrentState() {
        if (activeArray instanceof TracedFixedArray tfa) return tfa.unwrap().snapshot();
        if (activeArray instanceof TracedDynamicArray tda) return tda.unwrap().snapshot();
        return "[]";
    }

    @Override
    public void clearTraceHistory() {
        if (activeArray instanceof TracedFixedArray tfa) tfa.traceLog().clear();
        if (activeArray instanceof TracedDynamicArray tda) tda.traceLog().clear();
    }

    @Override
    public void reset() {
        if (activeArray instanceof TracedFixedArray tfa) {
            try {
                while (tfa.unwrap().size() > 0) tfa.unwrap().removeAt(tfa.unwrap().size() - 1);
            } catch(Exception ignored) {}
        }
        if (activeArray instanceof TracedDynamicArray tda) {
             try {
                while (tda.unwrap().size() > 0) tda.unwrap().removeAt(tda.unwrap().size() - 1);
             } catch(Exception ignored) {}
        }
        clearTraceHistory();
    }
}




