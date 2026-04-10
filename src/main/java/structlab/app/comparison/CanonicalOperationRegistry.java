package structlab.app.comparison;

import java.util.*;

/**
 * Defines canonical comparison operations per structure family.
 * <p>
 * Different implementations of the same structure family may expose operations
 * under different names (e.g., BinaryHeap uses "extractmin" while
 * HeapPriorityQueue uses "dequeue"). This registry maps those runtime-specific
 * names to canonical operations so Compare mode works by shared semantic intent,
 * not just literal operation-name overlap.
 * <p>
 * The registry is extensible: new families can register their canonical mappings
 * by adding entries to the static initializer.
 */
public final class CanonicalOperationRegistry {

    /**
     * A canonical operation definition.
     *
     * @param canonicalName the shared name used in comparison mode
     * @param knownAliases  all runtime-specific names that map to this canonical op
     * @param description   user-facing description
     * @param argCount      expected argument count
     */
    public record CanonicalOperation(
            String canonicalName,
            Set<String> knownAliases,
            String description,
            int argCount
    ) {
        /**
         * Returns true if the given operation name matches this canonical operation
         * (either as the canonical name itself or as a known alias).
         */
        public boolean matches(String operationName) {
            if (operationName == null) return false;
            String lower = operationName.toLowerCase(Locale.ROOT);
            if (canonicalName.toLowerCase(Locale.ROOT).equals(lower)) return true;
            return knownAliases.stream().anyMatch(a -> a.toLowerCase(Locale.ROOT).equals(lower));
        }
    }

    // Family → list of canonical operations
    private static final Map<String, List<CanonicalOperation>> FAMILY_MAP = new HashMap<>();

    static {
        // ── Heap family ──────────────────────────────────────
        FAMILY_MAP.put("heap", List.of(
                new CanonicalOperation("insert", Set.of("enqueue"), "Insert an element", 1),
                new CanonicalOperation("extractmin", Set.of("dequeue", "removemin"), "Remove the minimum element", 0),
                new CanonicalOperation("peek", Set.of(), "View the minimum element without removal", 0)
        ));

        // ── Hash family (map-like) ──────────────────────────
        FAMILY_MAP.put("hash", List.of(
                new CanonicalOperation("put", Set.of("insert", "set"), "Insert or update a key-value pair", 2),
                new CanonicalOperation("get", Set.of("lookup", "retrieve"), "Retrieve value by key", 1),
                new CanonicalOperation("remove", Set.of("delete"), "Remove entry by key", 1),
                new CanonicalOperation("containskey", Set.of("contains", "has"), "Check if key is present", 1)
        ));

        // ── Linked list family ──────────────────────────────
        FAMILY_MAP.put("list", List.of(
                new CanonicalOperation("addfirst", Set.of("prepend", "addhead"), "Add element at head", 1),
                new CanonicalOperation("addlast", Set.of("append", "addtail", "add"), "Add element at tail", 1),
                new CanonicalOperation("removefirst", Set.of("removehead", "pollFirst"), "Remove element at head", 0),
                new CanonicalOperation("removelast", Set.of("removetail", "pollLast"), "Remove element at tail", 0),
                new CanonicalOperation("get", Set.of("getat"), "Get element by index", 1),
                new CanonicalOperation("contains", Set.of("search", "find"), "Check if element exists", 1)
        ));

        // ── Deque family ────────────────────────────────────
        FAMILY_MAP.put("deque", List.of(
                new CanonicalOperation("addfirst", Set.of("pushfront", "offerfirst"), "Add element at front", 1),
                new CanonicalOperation("addlast", Set.of("pushback", "offerlast", "add"), "Add element at back", 1),
                new CanonicalOperation("removefirst", Set.of("popfront", "pollfirst"), "Remove element at front", 0),
                new CanonicalOperation("removelast", Set.of("popback", "polllast"), "Remove element at back", 0),
                new CanonicalOperation("peekfirst", Set.of("front", "first"), "View front element", 0),
                new CanonicalOperation("peeklast", Set.of("back", "last", "rear"), "View back element", 0)
        ));
    }

    private CanonicalOperationRegistry() {}

    /**
     * Returns the canonical operations for a structure family, or empty if no mapping is defined.
     */
    public static List<CanonicalOperation> forFamily(String familyName) {
        if (familyName == null) return List.of();
        return FAMILY_MAP.getOrDefault(familyName.toLowerCase(Locale.ROOT), List.of());
    }

    /**
     * Returns true if the given family has a canonical operation mapping.
     */
    public static boolean hasMapping(String familyName) {
        if (familyName == null) return false;
        return FAMILY_MAP.containsKey(familyName.toLowerCase(Locale.ROOT));
    }

    /**
     * Resolves an operation name to its canonical name within a given family.
     * Returns the original name if no mapping is found.
     */
    public static String resolveCanonical(String familyName, String operationName) {
        if (familyName == null || operationName == null) return operationName;
        List<CanonicalOperation> ops = forFamily(familyName);
        for (CanonicalOperation op : ops) {
            if (op.matches(operationName)) {
                return op.canonicalName();
            }
        }
        return operationName;
    }

    /**
     * Checks whether two operation names are semantically equivalent within a family.
     * For example, "insert" and "enqueue" are equivalent in the heap family.
     */
    public static boolean areEquivalent(String familyName, String opName1, String opName2) {
        if (opName1 == null || opName2 == null) return false;
        if (opName1.equalsIgnoreCase(opName2)) return true;
        String canonical1 = resolveCanonical(familyName, opName1);
        String canonical2 = resolveCanonical(familyName, opName2);
        return canonical1.equalsIgnoreCase(canonical2);
    }

    /**
     * Returns all registered family names.
     */
    public static Set<String> registeredFamilies() {
        return Collections.unmodifiableSet(FAMILY_MAP.keySet());
    }
}
