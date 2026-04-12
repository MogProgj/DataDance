package structlab.core.graph;

import java.util.*;

/**
 * A single frame/snapshot of a graph algorithm's execution state.
 * Immutable — algorithms produce a list of these frames for playback.
 *
 * <p>Fields used by all algorithms: algorithm, stepIndex, currentNode, visited,
 * frontier, discoveryOrder, parentMap, treeEdges, statusMessage, depth.</p>
 *
 * <p>Fields used by Dijkstra: distances, targetNode, shortestPath, settled.</p>
 */
public record AlgorithmFrame(
        /** Which algorithm produced this frame. */
        AlgorithmType algorithm,
        /** The current step index (0-based). */
        int stepIndex,
        /** Node currently being processed, or null if not applicable. */
        String currentNode,
        /** Set of fully visited/processed nodes (BFS/DFS) or settled nodes (Dijkstra). */
        Set<String> visited,
        /** Frontier: queue (BFS), stack (DFS), or priority-queue entries (Dijkstra). */
        List<String> frontier,
        /** Discovery order so far (nodes in the order they were first visited). */
        List<String> discoveryOrder,
        /** Parent map: child → parent for the traversal/shortest-path tree. */
        Map<String, String> parentMap,
        /** Edges in the traversal/shortest-path tree (from, to). */
        Set<TraversalEdge> treeEdges,
        /** Short human-readable status message. */
        String statusMessage,
        /** Current depth/layer for BFS, or recursion depth for DFS. Unused by Dijkstra. */
        int depth,
        /** Best-known distances from source (Dijkstra). Empty map for BFS/DFS. */
        Map<String, Double> distances,
        /** Optional target node label, or null if no target. */
        String targetNode,
        /** Shortest path from source to target when target is settled. Empty otherwise. */
        List<String> shortestPath) {

    public enum AlgorithmType {
        BFS, DFS, DIJKSTRA
    }

    /** Directed edge in the traversal tree. */
    public record TraversalEdge(String from, String to) {}

    /** True if this is the final frame (traversal complete). */
    public boolean isComplete() {
        return currentNode == null && frontier.isEmpty()
                && statusMessage != null && statusMessage.contains("complete");
    }

    /** Convenience constructor for BFS/DFS (no Dijkstra-specific fields). */
    public static AlgorithmFrame traversal(
            AlgorithmType algorithm, int stepIndex, String currentNode,
            Set<String> visited, List<String> frontier, List<String> discoveryOrder,
            Map<String, String> parentMap, Set<TraversalEdge> treeEdges,
            String statusMessage, int depth) {
        return new AlgorithmFrame(algorithm, stepIndex, currentNode, visited, frontier,
                discoveryOrder, parentMap, treeEdges, statusMessage, depth,
                Map.of(), null, List.of());
    }
}
