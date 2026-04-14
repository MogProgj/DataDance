package structlab.app.runtime.adapters;

import structlab.app.runtime.OperationDescriptor;
import structlab.app.runtime.OperationExecutionResult;
import structlab.core.hash.HashSetCustom;
import structlab.core.hash.HashTableChaining;
import structlab.core.hash.HashTableOpenAddressing;
import structlab.trace.TracedHashSetCustom;
import structlab.trace.TracedHashTableChaining;
import structlab.trace.TracedHashTableOpenAddressing;

import java.util.List;

public class HashRuntimeAdapter extends AbstractRuntimeAdapter {

    private final Object activeHash;

    public HashRuntimeAdapter(String implName, Object hash) {
        super("Hash Table", implName);
        this.activeHash = hash;
    }

    @Override
    public List<OperationDescriptor> getAvailableOperations() {
        if (activeHash instanceof TracedHashSetCustom<?>) {
            return List.of(
                    new OperationDescriptor("add", List.of(), "Add a value to the set", 1, "add <value>", true, "add 42", "O(1) avg"),
                    new OperationDescriptor("contains", List.of(), "Check if a value is in the set", 1, "contains <value>", false, "contains 42", "O(1) avg"),
                    new OperationDescriptor("remove", List.of(), "Remove a value from the set", 1, "remove <value>", true, "remove 42", "O(1) avg")
            );
        } else {
            // Both HashTableChaining and HashTableOpenAddressing share the same operations
            return List.of(
                    new OperationDescriptor("put", List.of(), "Put a key-value pair into the table", 2, "put <key> <value>", true, "put 1 100", "O(1) avg"),
                    new OperationDescriptor("get", List.of(), "Get the value for a key", 1, "get <key>", false, "get 1", "O(1) avg"),
                    new OperationDescriptor("remove", List.of(), "Remove a key from the table", 1, "remove <key>", true, "remove 1", "O(1) avg"),
                    new OperationDescriptor("contains", List.of("containsKey"), "Check if a key exists", 1, "contains <key>", false, "contains 1", "O(1) avg")
            );
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public OperationExecutionResult execute(String operation, List<String> args) {
        try {
            if (activeHash instanceof TracedHashTableChaining tht) {
                return executeHashTable(tht, operation, args);
            } else if (activeHash instanceof TracedHashTableOpenAddressing thoa) {
                return executeHashTableOa(thoa, operation, args);
            } else if (activeHash instanceof TracedHashSetCustom ths) {
                return executeHashSet(ths, operation, args);
            }
        } catch (Exception e) {
            return error(operation, e, getTraceLogFrom(activeHash));
        }
        return error(operation, new IllegalStateException("Invalid active hash state"));
    }

    @SuppressWarnings("unchecked")
    private OperationExecutionResult executeHashTable(TracedHashTableChaining tht, String operation, List<String> args) {
        switch (operation.toLowerCase()) {
            case "put":
                if (args.size() < 2) throw new IllegalArgumentException("Usage: put <key> <value>");
                int key = parseArg(args.get(0));
                int value = parseArg(args.get(1));
                Object old = tht.put(key, value);
                return success("put", old, tht.traceLog());
            case "get":
                if (args.isEmpty()) throw new IllegalArgumentException("Usage: get <key>");
                Object result = tht.get(parseArg(args.get(0)));
                return success("get", result, tht.traceLog());
            case "remove":
                if (args.isEmpty()) throw new IllegalArgumentException("Usage: remove <key>");
                Object removed = tht.remove(parseArg(args.get(0)));
                return success("remove", removed, tht.traceLog());
            case "contains":
            case "containskey":
                if (args.isEmpty()) throw new IllegalArgumentException("Usage: contains <key>");
                boolean found = tht.containsKey(parseArg(args.get(0)));
                return success("contains", found, tht.traceLog());
            default:
                throw new UnsupportedOperationException("Unknown hash table operation: " + operation);
        }
    }

    @SuppressWarnings("unchecked")
    private OperationExecutionResult executeHashTableOa(TracedHashTableOpenAddressing thoa, String operation, List<String> args) {
        switch (operation.toLowerCase()) {
            case "put":
                if (args.size() < 2) throw new IllegalArgumentException("Usage: put <key> <value>");
                int key = parseArg(args.get(0));
                int value = parseArg(args.get(1));
                Object old = thoa.put(key, value);
                return success("put", old, thoa.traceLog());
            case "get":
                if (args.isEmpty()) throw new IllegalArgumentException("Usage: get <key>");
                Object result = thoa.get(parseArg(args.get(0)));
                return success("get", result, thoa.traceLog());
            case "remove":
                if (args.isEmpty()) throw new IllegalArgumentException("Usage: remove <key>");
                Object removed = thoa.remove(parseArg(args.get(0)));
                return success("remove", removed, thoa.traceLog());
            case "contains":
            case "containskey":
                if (args.isEmpty()) throw new IllegalArgumentException("Usage: contains <key>");
                boolean found = thoa.containsKey(parseArg(args.get(0)));
                return success("contains", found, thoa.traceLog());
            default:
                throw new UnsupportedOperationException("Unknown hash table OA operation: " + operation);
        }
    }

    @SuppressWarnings("unchecked")
    private OperationExecutionResult executeHashSet(TracedHashSetCustom ths, String operation, List<String> args) {
        switch (operation.toLowerCase()) {
            case "add":
                if (args.isEmpty()) throw new IllegalArgumentException("Usage: add <value>");
                boolean added = ths.add(parseArg(args.get(0)));
                return success("add", added, ths.traceLog());
            case "contains":
                if (args.isEmpty()) throw new IllegalArgumentException("Usage: contains <value>");
                boolean found = ths.contains(parseArg(args.get(0)));
                return success("contains", found, ths.traceLog());
            case "remove":
                if (args.isEmpty()) throw new IllegalArgumentException("Usage: remove <value>");
                boolean removed = ths.remove(parseArg(args.get(0)));
                return success("remove", removed, ths.traceLog());
            default:
                throw new UnsupportedOperationException("Unknown hash set operation: " + operation);
        }
    }

    @Override
    public String getCurrentState() {
        if (activeHash instanceof TracedHashTableChaining<?, ?> tht) return tht.unwrap().snapshot();
        if (activeHash instanceof TracedHashTableOpenAddressing<?, ?> thoa) return thoa.unwrap().snapshot();
        if (activeHash instanceof TracedHashSetCustom<?> ths) return ths.unwrap().snapshot();
        return "{}";
    }

    @Override
    public void clearTraceHistory() {
        if (activeHash instanceof TracedHashTableChaining<?, ?> tht) tht.traceLog().clear();
        if (activeHash instanceof TracedHashTableOpenAddressing<?, ?> thoa) thoa.traceLog().clear();
        if (activeHash instanceof TracedHashSetCustom<?> ths) ths.traceLog().clear();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void reset() {
        if (activeHash instanceof TracedHashTableChaining tht) {
            tht.unwrap().clear();
        }
        if (activeHash instanceof TracedHashTableOpenAddressing thoa) {
            thoa.unwrap().clear();
        }
        if (activeHash instanceof TracedHashSetCustom ths) {
            ths.unwrap().clear();
        }
        clearTraceHistory();
    }
}
