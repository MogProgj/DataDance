package structlab.app.runtime.adapters;

import structlab.app.runtime.OperationDescriptor;
import structlab.app.runtime.OperationExecutionResult;
import structlab.trace.TracedArrayDequeCustom;
import structlab.trace.TracedLinkedDeque;

import java.util.List;

public class DequeRuntimeAdapter extends AbstractRuntimeAdapter {

    private final Object activeDeque;

    public DequeRuntimeAdapter(String implName, Object deque) {
        super("Deque", implName);
        this.activeDeque = deque;
    }

    @Override
    public List<OperationDescriptor> getAvailableOperations() {
        return List.of(
                new OperationDescriptor("addfirst", List.of(), "Add an element to the front of the deque", 1, "addfirst <value>", true, "addfirst <value>", "O(1)"),
                new OperationDescriptor("addlast", List.of(), "Add an element to the end of the deque", 1, "addlast <value>", true, "addlast <value>", "O(1)"),
                new OperationDescriptor("removefirst", List.of(), "Remove the first element of the deque", 0, "removefirst", true, "removefirst", "O(1)"),
                new OperationDescriptor("removelast", List.of(), "Remove the last element of the deque", 0, "removelast", true, "removelast", "O(1)"),
                new OperationDescriptor("peekfirst", List.of(), "Look at the first element of the deque", 0, "peekfirst", false, "peekfirst", "O(1)"),
                new OperationDescriptor("peeklast", List.of(), "Look at the last element of the deque", 0, "peeklast", false, "peeklast", "O(1)")
        );
    }

    @Override
    public OperationExecutionResult execute(String operation, List<String> args) {
        try {
            switch (operation.toLowerCase()) {
                case "addfirst":
                    if (args.isEmpty()) throw new IllegalArgumentException("Usage: addfirst <value>");
                    int valFirst = parseArg(args.get(0));
                    if (activeDeque instanceof TracedArrayDequeCustom tcaq) {
                        tcaq.addFirst(valFirst);
                        return success("addfirst", null, tcaq.traceLog());
                    } else if (activeDeque instanceof TracedLinkedDeque tld) {
                        tld.addFirst(valFirst);
                        return success("addfirst", null, tld.traceLog());
                    }
                    break;
                case "addlast":
                    if (args.isEmpty()) throw new IllegalArgumentException("Usage: addlast <value>");
                    int valLast = parseArg(args.get(0));
                    if (activeDeque instanceof TracedArrayDequeCustom tcaq) {
                        tcaq.addLast(valLast);
                        return success("addlast", null, tcaq.traceLog());
                    } else if (activeDeque instanceof TracedLinkedDeque tld) {
                        tld.addLast(valLast);
                        return success("addlast", null, tld.traceLog());
                    }
                    break;
                case "removefirst":
                    if (activeDeque instanceof TracedArrayDequeCustom tcaq) {
                        return success("removefirst", tcaq.removeFirst(), tcaq.traceLog());
                    } else if (activeDeque instanceof TracedLinkedDeque tld) {
                        return success("removefirst", tld.removeFirst(), tld.traceLog());
                    }
                    break;
                case "removelast":
                    if (activeDeque instanceof TracedArrayDequeCustom tcaq) {
                        return success("removelast", tcaq.removeLast(), tcaq.traceLog());
                    } else if (activeDeque instanceof TracedLinkedDeque tld) {
                        return success("removelast", tld.removeLast(), tld.traceLog());
                    }
                    break;
                case "peekfirst":
                    if (activeDeque instanceof TracedArrayDequeCustom tcaq) {
                        return success("peekfirst", tcaq.peekFirst(), tcaq.traceLog());
                    } else if (activeDeque instanceof TracedLinkedDeque tld) {
                        return success("peekfirst", tld.peekFirst(), tld.traceLog());
                    }
                    break;
                case "peeklast":
                    if (activeDeque instanceof TracedArrayDequeCustom tcaq) {
                        return success("peeklast", tcaq.peekLast(), tcaq.traceLog());
                    } else if (activeDeque instanceof TracedLinkedDeque tld) {
                        return success("peeklast", tld.peekLast(), tld.traceLog());
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown deque operation: " + operation);
            }
        } catch (Exception e) {
            return error(operation, e, getTraceLogFrom(activeDeque));
        }
        return error(operation, new IllegalStateException("Invalid active deque state"));
    }

    @Override
    public String getCurrentState() {
        if (activeDeque instanceof TracedArrayDequeCustom tcaq) return tcaq.unwrap().snapshot();
        if (activeDeque instanceof TracedLinkedDeque tld) return tld.unwrap().snapshot();
        return "[]";
    }

    @Override
    public void clearTraceHistory() {
        if (activeDeque instanceof TracedArrayDequeCustom tcaq) tcaq.traceLog().clear();
        if (activeDeque instanceof TracedLinkedDeque tld) tld.traceLog().clear();
    }

    @Override
    public void reset() {
        if (activeDeque instanceof TracedArrayDequeCustom tcaq) {
            try {
                while (!tcaq.unwrap().isEmpty()) tcaq.unwrap().removeFirst();
            } catch (Exception ignored) {}
        }
        if (activeDeque instanceof TracedLinkedDeque tld) {
             try {
                while (!tld.unwrap().isEmpty()) tld.unwrap().removeFirst();
             } catch (Exception ignored) {}
        }
        clearTraceHistory();
    }
}




