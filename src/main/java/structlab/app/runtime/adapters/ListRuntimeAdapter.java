package structlab.app.runtime.adapters;

import structlab.app.runtime.OperationDescriptor;
import structlab.app.runtime.OperationExecutionResult;
import structlab.trace.TracedDoublyLinkedList;
import structlab.trace.TracedSinglyLinkedList;

import java.util.List;

public class ListRuntimeAdapter extends AbstractRuntimeAdapter {

    private final Object activeList; // Holds one of the traced lists

    public ListRuntimeAdapter(String implName, Object list) {
        super("Linked List", implName);
        this.activeList = list;
    }

    @Override
    public List<OperationDescriptor> getAvailableOperations() {
        return List.of(
                new OperationDescriptor("addfirst", List.of(), "Add an element to the front of the list", 1, "addfirst <value>", true, "addfirst <value>", "O(1)"),
                new OperationDescriptor("addlast", List.of(), "Add an element to the end of the list", 1, "addlast <value>", true, "addlast <value>", "O(1)"),
                new OperationDescriptor("removefirst", List.of(), "Remove the first element of the list", 0, "removefirst", true, "removefirst", "O(1)"),
                new OperationDescriptor("removelast", List.of(), "Remove the last element of the list (Doubly Linked only)", 0, "removelast", true, "removelast", "O(1)"),
                new OperationDescriptor("getfirst", List.of(), "Get the first element of the list", 0, "getfirst", false, "getfirst", "O(1)"),
                new OperationDescriptor("getlast", List.of(), "Get the last element of the list", 0, "getlast", false, "getlast", "O(1)")
        );
    }

    @Override
    public OperationExecutionResult execute(String operation, List<String> args) {
        try {
            switch (operation.toLowerCase()) {
                case "addfirst":
                    if (args.isEmpty()) throw new IllegalArgumentException("Usage: addfirst <value>");
                    int valFirst = parseArg(args.get(0));
                    if (activeList instanceof TracedSinglyLinkedList tsll) {
                        tsll.addFirst(valFirst);
                        return success("addfirst", null, tsll.traceLog());
                    } else if (activeList instanceof TracedDoublyLinkedList tdll) {
                        tdll.addFirst(valFirst);
                        return success("addfirst", null, tdll.traceLog());
                    }
                    break;
                case "addlast":
                    if (args.isEmpty()) throw new IllegalArgumentException("Usage: addlast <value>");
                    int valLast = parseArg(args.get(0));
                    if (activeList instanceof TracedSinglyLinkedList tsll) {
                        tsll.addLast(valLast);
                        return success("addlast", null, tsll.traceLog());
                    } else if (activeList instanceof TracedDoublyLinkedList tdll) {
                        tdll.addLast(valLast);
                        return success("addlast", null, tdll.traceLog());
                    }
                    break;
                case "removefirst":
                    if (activeList instanceof TracedSinglyLinkedList tsll) {
                        return success("removefirst", tsll.removeFirst(), tsll.traceLog());
                    } else if (activeList instanceof TracedDoublyLinkedList tdll) {
                        return success("removefirst", tdll.removeFirst(), tdll.traceLog());
                    }
                    break;
                case "removelast":
                    if (activeList instanceof TracedDoublyLinkedList tdll) {
                        return success("removelast", tdll.removeLast(), tdll.traceLog());
                    } else if (activeList instanceof TracedSinglyLinkedList) {
                        throw new UnsupportedOperationException("removelast is only supported on Doubly Linked Lists in this simulator");
                    }
                    break;
                case "getfirst":
                    if (activeList instanceof TracedSinglyLinkedList tsll) {
                        return success("getfirst", tsll.getFirst(), tsll.traceLog());
                    } else if (activeList instanceof TracedDoublyLinkedList tdll) {
                        return success("getfirst", tdll.getFirst(), tdll.traceLog());
                    }
                    break;
                case "getlast":
                    if (activeList instanceof TracedSinglyLinkedList tsll) {
                        return success("getlast", tsll.getLast(), tsll.traceLog());
                    } else if (activeList instanceof TracedDoublyLinkedList tdll) {
                        return success("getlast", tdll.getLast(), tdll.traceLog());
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown list operation: " + operation);
            }
        } catch (Exception e) {
            return error(operation, e, getTraceLogFrom(activeList));
        }
        return error(operation, new IllegalStateException("Invalid active list state"));
    }

    @Override
    public String getCurrentState() {
        if (activeList instanceof TracedSinglyLinkedList tsll) return tsll.unwrap().snapshot();
        if (activeList instanceof TracedDoublyLinkedList tdll) return tdll.unwrap().snapshot();
        return "[]";
    }

    @Override
    public void clearTraceHistory() {
        if (activeList instanceof TracedSinglyLinkedList tsll) tsll.traceLog().clear();
        if (activeList instanceof TracedDoublyLinkedList tdll) tdll.traceLog().clear();
    }

    @Override
    public void reset() {
        if (activeList instanceof TracedSinglyLinkedList tsll) {
            try {
                while (!tsll.unwrap().isEmpty()) tsll.unwrap().removeFirst();
            } catch (Exception ignored) {}
        }
        if (activeList instanceof TracedDoublyLinkedList tdll) {
             try {
                while (!tdll.unwrap().isEmpty()) tdll.unwrap().removeFirst();
             } catch (Exception ignored) {}
        }
        clearTraceHistory();
    }
}




