package structlab.core.graph;

import java.util.*;

/**
 * Breadth-first search producing a list of {@link AlgorithmFrame}s for step-by-step playback.
 */
public final class BfsRunner {

    private BfsRunner() {}

    /**
     * Runs BFS from the given source node and returns all frames.
     *
     * @param graph  the graph to traverse
     * @param source the starting node label
     * @return unmodifiable list of frames, one per step
     * @throws IllegalArgumentException if source node doesn't exist
     */
    public static List<AlgorithmFrame> run(Graph graph, String source) {
        if (!graph.hasNode(source)) {
            throw new IllegalArgumentException("Source node not found: " + source);
        }

        List<AlgorithmFrame> frames = new ArrayList<>();
        Set<String> visited = new LinkedHashSet<>();
        Deque<String> queue = new ArrayDeque<>();
        List<String> discoveryOrder = new ArrayList<>();
        Map<String, String> parentMap = new LinkedHashMap<>();
        Set<AlgorithmFrame.TraversalEdge> treeEdges = new LinkedHashSet<>();
        int step = 0;
        int depth = 0;

        // Initial frame: source enqueued
        queue.add(source);
        visited.add(source);
        discoveryOrder.add(source);

        frames.add(AlgorithmFrame.traversal(
                AlgorithmFrame.AlgorithmType.BFS, step++, null,
                Set.copyOf(visited), List.copyOf(queue), List.copyOf(discoveryOrder),
                Map.copyOf(parentMap), Set.copyOf(treeEdges),
                "BFS started — enqueued source " + source, depth));

        // Track layer boundaries for depth calculation
        // We track by storing the node that ends the current layer
        Map<String, Integer> depthMap = new LinkedHashMap<>();
        depthMap.put(source, 0);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            depth = depthMap.getOrDefault(current, 0);

            // Frame: processing current node
            frames.add(AlgorithmFrame.traversal(
                    AlgorithmFrame.AlgorithmType.BFS, step++, current,
                    Set.copyOf(visited), List.copyOf(queue), List.copyOf(discoveryOrder),
                    Map.copyOf(parentMap), Set.copyOf(treeEdges),
                    "Dequeued " + current + " — exploring neighbors (layer " + depth + ")",
                    depth));

            for (String neighbor : graph.neighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    discoveryOrder.add(neighbor);
                    parentMap.put(neighbor, current);
                    treeEdges.add(new AlgorithmFrame.TraversalEdge(current, neighbor));
                    depthMap.put(neighbor, depth + 1);

                    // Frame: discovered new neighbor
                    frames.add(AlgorithmFrame.traversal(
                            AlgorithmFrame.AlgorithmType.BFS, step++, current,
                            Set.copyOf(visited), List.copyOf(queue), List.copyOf(discoveryOrder),
                            Map.copyOf(parentMap), Set.copyOf(treeEdges),
                            "Discovered " + neighbor + " via " + current + " → enqueued",
                            depth));
                }
            }
        }

        // Final frame
        frames.add(AlgorithmFrame.traversal(
                AlgorithmFrame.AlgorithmType.BFS, step, null,
                Set.copyOf(visited), List.of(), List.copyOf(discoveryOrder),
                Map.copyOf(parentMap), Set.copyOf(treeEdges),
                "BFS complete — visited " + visited.size() + " of " + graph.nodeCount() + " nodes",
                depth));

        return Collections.unmodifiableList(frames);
    }
}
