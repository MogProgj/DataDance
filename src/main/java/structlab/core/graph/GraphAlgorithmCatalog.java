package structlab.core.graph;

import java.util.*;

import static structlab.core.graph.GraphAlgorithmId.*;
import static structlab.core.graph.GraphAlgorithmSpec.Category.*;
import static structlab.core.graph.GraphAlgorithmSpec.TargetMode.*;

/**
 * Singleton catalog of all graph algorithms supported by the workbench.
 * The single source of truth for algorithm metadata — used by the
 * controller, combo boxes, validation, and dispatch.
 */
public final class GraphAlgorithmCatalog {

    private static final List<GraphAlgorithmSpec> SPECS = List.of(
            new GraphAlgorithmSpec(BFS, "BFS", TRAVERSAL,
                    true, NONE, true, true, true,
                    "Breadth-first search from a source node.",
                    AlgorithmFrame.AlgorithmType.BFS),

            new GraphAlgorithmSpec(DFS, "DFS", TRAVERSAL,
                    true, NONE, true, true, true,
                    "Depth-first search from a source node.",
                    AlgorithmFrame.AlgorithmType.DFS),

            new GraphAlgorithmSpec(DIJKSTRA, "Dijkstra", SHORTEST_PATH,
                    true, OPTIONAL, true, true, true,
                    "Shortest paths from source. Set target for single-pair mode.",
                    AlgorithmFrame.AlgorithmType.DIJKSTRA),

            new GraphAlgorithmSpec(BELLMAN_FORD, "Bellman-Ford", SHORTEST_PATH,
                    true, OPTIONAL, true, true, true,
                    "Shortest paths from source. Handles negative weights.",
                    AlgorithmFrame.AlgorithmType.BELLMAN_FORD),

            new GraphAlgorithmSpec(TOPOLOGICAL_SORT, "Topo Sort", TRAVERSAL,
                    false, NONE, true, false, true,
                    "Topological ordering of a directed acyclic graph.",
                    AlgorithmFrame.AlgorithmType.TOPOLOGICAL_SORT),

            new GraphAlgorithmSpec(A_STAR, "A*", SHORTEST_PATH,
                    true, REQUIRED, true, true, true,
                    "Heuristic shortest path — requires both source and target.",
                    AlgorithmFrame.AlgorithmType.A_STAR),

            new GraphAlgorithmSpec(PRIM, "Prim (MST)", MST,
                    true, NONE, false, true, true,
                    "Prim's MST from a source node. Undirected graphs only.",
                    AlgorithmFrame.AlgorithmType.PRIM),

            new GraphAlgorithmSpec(KRUSKAL, "Kruskal (MST)", MST,
                    false, NONE, false, true, true,
                    "Kruskal's MST via sorted edges. Undirected graphs only.",
                    AlgorithmFrame.AlgorithmType.KRUSKAL),

            new GraphAlgorithmSpec(SCC, "SCC (Kosaraju)", CONNECTIVITY,
                    false, NONE, true, false, true,
                    "Strongly connected components. Directed graphs only.",
                    AlgorithmFrame.AlgorithmType.SCC),

            new GraphAlgorithmSpec(BRIDGES, "Bridges", CONNECTIVITY,
                    false, NONE, false, true, true,
                    "Find bridge edges whose removal disconnects the graph.",
                    AlgorithmFrame.AlgorithmType.BRIDGES),

            new GraphAlgorithmSpec(ARTICULATION_POINTS, "Articulation Points", CONNECTIVITY,
                    false, NONE, false, true, true,
                    "Find cut vertices whose removal disconnects the graph.",
                    AlgorithmFrame.AlgorithmType.ARTICULATION_POINTS)
    );

    private static final Map<GraphAlgorithmId, GraphAlgorithmSpec> BY_ID;
    private static final Map<String, GraphAlgorithmSpec> BY_LABEL;

    static {
        Map<GraphAlgorithmId, GraphAlgorithmSpec> byId = new LinkedHashMap<>();
        Map<String, GraphAlgorithmSpec> byLabel = new LinkedHashMap<>();
        for (GraphAlgorithmSpec spec : SPECS) {
            byId.put(spec.id(), spec);
            byLabel.put(spec.displayLabel(), spec);
        }
        BY_ID = Collections.unmodifiableMap(byId);
        BY_LABEL = Collections.unmodifiableMap(byLabel);
    }

    private GraphAlgorithmCatalog() {}

    /** Returns all algorithm specs in display order. */
    public static List<GraphAlgorithmSpec> all() {
        return SPECS;
    }

    /** Returns display labels in order (for combo boxes). */
    public static List<String> displayLabels() {
        return SPECS.stream().map(GraphAlgorithmSpec::displayLabel).toList();
    }

    /** Looks up a spec by stable ID. */
    public static GraphAlgorithmSpec byId(GraphAlgorithmId id) {
        return BY_ID.get(id);
    }

    /** Looks up a spec by display label (for backward compat with combo values). */
    public static GraphAlgorithmSpec byLabel(String label) {
        return BY_LABEL.get(label);
    }

    /**
     * Runs the given algorithm and returns the frame list.
     * Centralises dispatch so the controller never needs a string-based if/else chain.
     *
     * @param spec   the algorithm spec
     * @param graph  the graph to run on
     * @param source source node (may be null for source-free algorithms)
     * @param target target node (may be null)
     * @param nodePositions node positions (needed by A* heuristic)
     * @return the list of frames, or null if validation failed
     * @throws IllegalArgumentException on graph/config mismatch
     */
    public static List<AlgorithmFrame> run(GraphAlgorithmSpec spec,
                                            Graph graph,
                                            String source,
                                            String target,
                                            Map<String, double[]> nodePositions) {
        return switch (spec.id()) {
            case BFS -> BfsRunner.run(graph, source);
            case DFS -> DfsRunner.run(graph, source);
            case DIJKSTRA -> DijkstraRunner.run(graph, source, target);
            case BELLMAN_FORD -> BellmanFordRunner.run(graph, source, target);
            case TOPOLOGICAL_SORT -> TopologicalSortRunner.run(graph);
            case A_STAR -> AStarRunner.run(graph, source, target, nodePositions);
            case PRIM -> PrimRunner.run(graph, source);
            case KRUSKAL -> KruskalRunner.run(graph);
            case SCC -> SCCRunner.run(graph);
            case BRIDGES -> BridgesRunner.run(graph);
            case ARTICULATION_POINTS -> ArticulationPointsRunner.run(graph);
        };
    }
}
