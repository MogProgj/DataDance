package structlab.app.runtime.adapters;

import structlab.app.runtime.OperationDescriptor;
import structlab.app.runtime.OperationExecutionResult;
import structlab.trace.TracedArrayStack;
import structlab.trace.TracedLinkedStack;

import java.util.List;

public class StackRuntimeAdapter extends AbstractRuntimeAdapter {

    private final Object activeStack; // Holds either TracedArrayStack or TracedLinkedStack

    public StackRuntimeAdapter(String implName, Object stack) {
        super("Stack", implName);
        this.activeStack = stack;
    }

    @Override
    public List<OperationDescriptor> getAvailableOperations() {
        return List.of(
                new OperationDescriptor("push", List.of(), "Push an element onto the stack", 1, "push <value>", true, "push <value>", "O(1)"),
                new OperationDescriptor("pop", List.of(), "Pop an element off the stack", 0, "pop", true, "pop", "O(1)"),
                new OperationDescriptor("peek", List.of("top"), "Look at the top element without removing it", 0, "peek", false, "peek", "O(1)")
        );
    }

    @Override
    public OperationExecutionResult execute(String operation, List<String> args) {
        try {
            switch (operation.toLowerCase()) {
                case "push":
                    if (args.isEmpty()) throw new IllegalArgumentException("Usage: push <value>");
                    int val = parseArg(args.get(0));
                    if (activeStack instanceof TracedArrayStack tas) {
                        tas.push(val);
                        return success("push", null, tas.traceLog());
                    } else if (activeStack instanceof TracedLinkedStack tls) {
                        tls.push(val);
                        return success("push", null, tls.traceLog());
                    }
                    break;
                case "pop":
                    if (activeStack instanceof TracedArrayStack tas) {
                        return success("pop", tas.pop(), tas.traceLog());
                    } else if (activeStack instanceof TracedLinkedStack tls) {
                        return success("pop", tls.pop(), tls.traceLog());
                    }
                    break;
                case "peek":
                case "top":
                    if (activeStack instanceof TracedArrayStack tas) {
                        return success("peek", tas.peek(), tas.traceLog());
                    } else if (activeStack instanceof TracedLinkedStack tls) {
                        return success("peek", tls.peek(), tls.traceLog());
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown stack operation: " + operation);
            }
        } catch (Exception e) {
            structlab.trace.TraceLog tl = null;
            if (activeStack instanceof TracedArrayStack tas) tl = tas.traceLog();
            else if (activeStack instanceof TracedLinkedStack tls) tl = tls.traceLog();
            return error(operation, e, tl);
        }
        return error(operation, new IllegalStateException("Invalid active stack state"));
    }

    @Override
    public String getCurrentState() {
        if (activeStack instanceof TracedArrayStack tas) return tas.unwrap().snapshot();
        if (activeStack instanceof TracedLinkedStack tls) return tls.unwrap().snapshot();
        return "[]";
    }

    @Override
    public void clearTraceHistory() {
        if (activeStack instanceof TracedArrayStack tas) tas.traceLog().clear();
        if (activeStack instanceof TracedLinkedStack tls) tls.traceLog().clear();
    }

    @Override
    public void reset() {
        if (activeStack instanceof TracedArrayStack tas) {
            try {
                while(true) tas.unwrap().pop();
            } catch(Exception ignored) {}
        }
        if (activeStack instanceof TracedLinkedStack tls) {
             try {
                while(true) tls.unwrap().pop();
             } catch(Exception ignored) {}
        }
        clearTraceHistory();
    }
}
