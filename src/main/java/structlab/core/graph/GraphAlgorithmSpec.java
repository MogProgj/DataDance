package structlab.core.graph;

/**
 * Typed metadata for a single graph algorithm.
 * Drives control-panel visibility, validation, and dispatch
 * so the controller never reasons from raw display strings.
 *
 * @param id             stable enum identity
 * @param displayLabel   human-readable label for combo boxes
 * @param category       algorithm family (Traversal, Shortest Path, MST, Connectivity)
 * @param sourceRequired whether a source node is required to run
 * @param targetMode     whether / how a target node is used
 * @param directedOk     whether directed graphs are supported
 * @param undirectedOk   whether undirected graphs are supported
 * @param compareSupported whether this algorithm may appear in compare mode
 * @param hint           concise constraint / help text shown in the control panel
 * @param frameType      the corresponding {@link AlgorithmFrame.AlgorithmType}
 */
public record GraphAlgorithmSpec(
        GraphAlgorithmId id,
        String displayLabel,
        Category category,
        boolean sourceRequired,
        TargetMode targetMode,
        boolean directedOk,
        boolean undirectedOk,
        boolean compareSupported,
        String hint,
        AlgorithmFrame.AlgorithmType frameType) {

    /** Broad algorithm family used for grouping and UI hints. */
    public enum Category {
        TRAVERSAL("Traversal"),
        SHORTEST_PATH("Shortest Path"),
        MST("Minimum Spanning Tree"),
        CONNECTIVITY("Connectivity / Diagnostics");

        private final String label;
        Category(String label) { this.label = label; }
        public String label() { return label; }
    }

    /** How the target node is used by an algorithm. */
    public enum TargetMode {
        /** Target is not used at all. */
        NONE,
        /** Target is optional — algorithm runs with or without it. */
        OPTIONAL,
        /** Target is required — algorithm will not run without it. */
        REQUIRED
    }

    /** Returns true if this algorithm can run on the given graph. */
    public boolean supportsGraph(Graph graph) {
        return graph.isDirected() ? directedOk : undirectedOk;
    }
}
