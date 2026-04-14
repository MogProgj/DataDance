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
        int totalNodes = graph.nodeCount();

        // Initial frame: source enqueued
        queue.add(source);
        visited.add(source);
        discoveryOrder.add(source);

        AlgorithmTelemetry initTelemetry = TelemetryBuilder.create("Initialization")
                .metric("Visited", "1/" + totalNodes)
                .metric("Layer", 0)
                .section("Queue", List.copyOf(queue))
                .event("Enqueued source " + source)
                .build();

        frames.add(frame(step++, null, visited, queue, discoveryOrder,
                parentMap, treeEdges,
                "BFS started — enqueued source " + source, depth, initTelemetry));

        // Track layer boundaries for depth calculation
        Map<String, Integer> depthMap = new LinkedHashMap<>();
        depthMap.put(source, 0);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            depth = depthMap.getOrDefault(current, 0);

            AlgorithmTelemetry dequeueTelemetry = TelemetryBuilder.create("Dequeue")
                    .metric("Current", current)
                    .metric("Layer", depth)
                    .metric("Visited", visited.size() + "/" + totalNodes)
                    .section("Queue", List.copyOf(queue))
                    .event("Dequeued " + current + " (layer " + depth + ")")
                    .build();

            frames.add(frame(step++, current, visited, queue, discoveryOrder,
                    parentMap, treeEdges,
                    "Dequeued " + current + " — exploring neighbors (layer " + depth + ")",
                    depth, dequeueTelemetry));

            List<String> newlyEnqueued = new ArrayList<>();
            for (String neighbor : graph.neighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    discoveryOrder.add(neighbor);
                    parentMap.put(neighbor, current);
                    treeEdges.add(new AlgorithmFrame.TraversalEdge(current, neighbor));
                    depthMap.put(neighbor, depth + 1);
                    newlyEnqueued.add(neighbor);

                    AlgorithmTelemetry discoverTelemetry = TelemetryBuilder.create("Discover")
                            .metric("Current", current)
                            .metric("Discovered", neighbor)
                            .metric("Layer", depth)
                            .metric("Visited", visited.size() + "/" + totalNodes)
                            .section("Queue", List.copyOf(queue))
                            .section("Newly Enqueued", List.copyOf(newlyEnqueued))
                            .event("Discovered " + neighbor + " via " + current)
                            .build();

                    frames.add(frame(step++, current, visited, queue, discoveryOrder,
                            parentMap, treeEdges,
                            "Discovered " + neighbor + " via " + current + " → enqueued",
                            depth, discoverTelemetry));
                }
            }
        }

        // Final frame
        AlgorithmTelemetry completeTelemetry = TelemetryBuilder.create("Complete")
                .metric("Visited", visited.size() + "/" + totalNodes)
                .metric("Max Layer", depthMap.values().stream().mapToInt(Integer::intValue).max().orElse(0))
                .section("Discovery Order", List.copyOf(discoveryOrder))
                .event("BFS complete")
                .build();

        frames.add(frame(step, null, visited, queue, discoveryOrder,
                parentMap, treeEdges,
                "BFS complete — visited " + visited.size() + " of " + totalNodes + " nodes",
                depth, completeTelemetry));

        return Collections.unmodifiableList(frames);
    }

    private static AlgorithmFrame frame(
            AlgorithmFrame.AlgorithmType ignored, int stepIndex, String currentNode,
            Set<String> visited, List<String> frontier, List<String> discoveryOrder,
            Map<String, String> parentMap, Set<AlgorithmFrame.TraversalEdge> treeEdges,
            String statusMessage, int depth) {
        return frame(stepIndex, currentNode, visited, new ArrayDeque<>(), discoveryOrder,
                parentMap, treeEdges, statusMessage, depth, null);
    }

    private static AlgorithmFrame frame(
            int stepIndex, String currentNode,
            Set<String> visited, Deque<String> queue, List<String> discoveryOrder,
            Map<String, String> parentMap, Set<AlgorithmFrame.TraversalEdge> treeEdges,
            String statusMessage, int depth, AlgorithmTelemetry telemetry) {
        return new AlgorithmFrame(AlgorithmFrame.AlgorithmType.BFS, stepIndex, currentNode,
                Set.copyOf(visited), List.copyOf(queue), List.copyOf(discoveryOrder),
                Map.copyOf(parentMap), Set.copyOf(treeEdges), statusMessage, depth,
                Map.of(), null, List.of(), telemetry);
    }
}
