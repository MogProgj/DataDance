package structlab.app.runtime.adapters;

import structlab.app.runtime.OperationDescriptor;
import structlab.app.runtime.OperationExecutionResult;
import structlab.trace.TracedBinaryHeap;
import structlab.trace.TracedHeapPriorityQueue;

import java.util.List;

public class HeapRuntimeAdapter extends AbstractRuntimeAdapter {

    private final Object activeHeap; // Holds TracedBinaryHeap or TracedHeapPriorityQueue

    public HeapRuntimeAdapter(String implName, Object heap) {
        super("Heap", implName);
        this.activeHeap = heap;
    }

    @Override
    public List<OperationDescriptor> getAvailableOperations() {
        if (activeHeap instanceof TracedHeapPriorityQueue) {
            return List.of(
                    new OperationDescriptor("enqueue", List.of(), "Enqueue an element into the priority queue", 1, "enqueue <value>", true, "enqueue <value>", "O(log N)"),
                    new OperationDescriptor("dequeue", List.of(), "Dequeue the min priority element", 0, "dequeue", true, "dequeue", "O(log N)"),
                    new OperationDescriptor("peek", List.of(), "Look at the min element", 0, "peek", false, "peek", "O(log N)")
            );
        } else {
            return List.of(
                    new OperationDescriptor("insert", List.of(), "Insert an element into the heap", 1, "insert <value>", true, "insert <value>", "O(log N)"),
                    new OperationDescriptor("extractmin", List.of(), "Extract the minimum element", 0, "extractmin", true, "extractmin", "O(log N)"),
                    new OperationDescriptor("peek", List.of(), "Look at the minimum element", 0, "peek", false, "peek", "O(log N)")
            );
        }
    }

    @Override
    public OperationExecutionResult execute(String operation, List<String> args) {
        try {
            switch (operation.toLowerCase()) {
                case "insert":
                case "enqueue":
                    if (args.isEmpty()) throw new IllegalArgumentException("Usage: " + operation + " <value>");
                    int val = parseArg(args.get(0));
                    if (activeHeap instanceof TracedBinaryHeap tbh) {
                        tdbhInsert(tbh, val);
                        return success(operation, null, tbh.traceLog());
                    } else if (activeHeap instanceof TracedHeapPriorityQueue thpq) {
                        thpqEnqueue(thpq, val);
                        return success(operation, null, thpq.traceLog());
                    }
                    break;
                case "extractmin":
                case "dequeue":
                    if (activeHeap instanceof TracedBinaryHeap tbh) {
                        return success(operation, tdbhExtractMin(tbh), tbh.traceLog());
                    } else if (activeHeap instanceof TracedHeapPriorityQueue thpq) {
                        return success(operation, thpqDequeue(thpq), thpq.traceLog());
                    }
                    break;
                case "peek":
                    if (activeHeap instanceof TracedBinaryHeap tbh) {
                        return success("peek", tdbhPeek(tbh), tbh.traceLog());
                    } else if (activeHeap instanceof TracedHeapPriorityQueue thpq) {
                        return success("peek", thpqPeek(thpq), thpq.traceLog());
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown heap operation: " + operation);
            }
        } catch (Exception e) {
            return error(operation, e, getTraceLogFrom(activeHeap));
        }
        return error(operation, new IllegalStateException("Invalid active heap state"));
    }

    @SuppressWarnings("unchecked")
    private void tdbhInsert(TracedBinaryHeap tbh, int val) { tbh.insert(val); }
    @SuppressWarnings("unchecked")
    private Object tdbhExtractMin(TracedBinaryHeap tbh) { return tbh.extractMin(); }
    @SuppressWarnings("unchecked")
    private Object tdbhPeek(TracedBinaryHeap tbh) { return tbh.peek(); }

    @SuppressWarnings("unchecked")
    private void thpqEnqueue(TracedHeapPriorityQueue thpq, int val) { thpq.enqueue(val); }
    @SuppressWarnings("unchecked")
    private Object thpqDequeue(TracedHeapPriorityQueue thpq) { return thpq.dequeue(); }
    @SuppressWarnings("unchecked")
    private Object thpqPeek(TracedHeapPriorityQueue thpq) { return thpq.peek(); }

    @Override
    public String getCurrentState() {
        if (activeHeap instanceof TracedBinaryHeap tbh) return tbh.unwrap().snapshot();
        if (activeHeap instanceof TracedHeapPriorityQueue thpq) return thpq.unwrap().snapshot();
        return "[]";
    }

    @Override
    public void clearTraceHistory() {
        if (activeHeap instanceof TracedBinaryHeap tbh) tbh.traceLog().clear();
        if (activeHeap instanceof TracedHeapPriorityQueue thpq) thpq.traceLog().clear();
    }

    @Override
    public void reset() {
        if (activeHeap instanceof TracedBinaryHeap tbh) {
            try {
                while (!tbh.unwrap().isEmpty()) tbh.unwrap().extractMin();
            } catch(Exception ignored) {}
        }
        if (activeHeap instanceof TracedHeapPriorityQueue thpq) {
             try {
                while (!thpq.unwrap().isEmpty()) thpq.unwrap().dequeue();
             } catch(Exception ignored) {}
        }
        clearTraceHistory();
    }
}




