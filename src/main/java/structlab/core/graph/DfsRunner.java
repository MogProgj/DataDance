package structlab.core.graph;

import java.util.*;

/**
 * Depth-first search producing a list of {@link AlgorithmFrame}s for step-by-step playback.
 * Uses an iterative stack-based approach for consistent frame generation.
 */
public final class DfsRunner {

    private DfsRunner() {}

    /**
     * Runs DFS from the given source node and returns all frames.
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
        Deque<String> stack = new ArrayDeque<>();
        List<String> discoveryOrder = new ArrayList<>();
        Map<String, String> parentMap = new LinkedHashMap<>();
        Set<AlgorithmFrame.TraversalEdge> treeEdges = new LinkedHashSet<>();
        Map<String, Integer> depthMap = new LinkedHashMap<>();
        int step = 0;
        int totalNodes = graph.nodeCount();

        // Initial frame: source pushed
        stack.push(source);
        depthMap.put(source, 0);

        AlgorithmTelemetry initTelemetry = TelemetryBuilder.create("Initialization")
                .metric("Visited", "0/" + totalNodes)
                .metric("Depth", 0)
                .section("Stack", stackToList(stack))
                .event("Pushed source " + source)
                .build();

        frames.add(frame(step++, null, visited, stack, discoveryOrder,
                parentMap, treeEdges, "DFS started — pushed source " + source,
                0, initTelemetry));

        while (!stack.isEmpty()) {
            String current = stack.pop();

            if (visited.contains(current)) {
                AlgorithmTelemetry btTelemetry = TelemetryBuilder.create("Backtrack")
                        .metric("Popped", current + " (already visited)")
                        .metric("Visited", visited.size() + "/" + totalNodes)
                        .section("Stack", stackToList(stack))
                        .event("Backtrack — " + current + " already visited")
                        .build();

                frames.add(frame(step++, current, visited, stack, discoveryOrder,
                        parentMap, treeEdges,
                        "Popped " + current + " — already visited, backtracking",
                        depthMap.getOrDefault(current, 0), btTelemetry));
                continue;
            }

            visited.add(current);
            discoveryOrder.add(current);
            int depth = depthMap.getOrDefault(current, 0);

            AlgorithmTelemetry visitTelemetry = TelemetryBuilder.create("Visit")
                    .metric("Current", current)
                    .metric("Depth", depth)
                    .metric("Visited", visited.size() + "/" + totalNodes)
                    .section("Stack", stackToList(stack))
                    .event("Visiting " + current + " (depth " + depth + ")")
                    .build();

            frames.add(frame(step++, current, visited, stack, discoveryOrder,
                    parentMap, treeEdges,
                    "Visiting " + current + " (depth " + depth + ")", depth,
                    visitTelemetry));

            // Push neighbors in reverse order so first neighbor is processed first
            List<String> neighbors = graph.neighbors(current);
            List<String> unvisited = new ArrayList<>();
            for (String neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    unvisited.add(neighbor);
                }
            }
            Collections.reverse(unvisited);

            for (String neighbor : unvisited) {
                stack.push(neighbor);
                if (!depthMap.containsKey(neighbor)) {
                    depthMap.put(neighbor, depth + 1);
                }
                if (!parentMap.containsKey(neighbor) && !neighbor.equals(source)) {
                    parentMap.put(neighbor, current);
                    treeEdges.add(new AlgorithmFrame.TraversalEdge(current, neighbor));
                }
            }

            if (!unvisited.isEmpty()) {
                // Reverse back for display in push order
                List<String> pushed = new ArrayList<>(unvisited);
                Collections.reverse(pushed);

                AlgorithmTelemetry pushTelemetry = TelemetryBuilder.create("Push")
                        .metric("From", current)
                        .metric("Pushed", pushed.size() + " neighbor(s)")
                        .metric("Visited", visited.size() + "/" + totalNodes)
                        .section("Stack", stackToList(stack))
                        .section("Pushed Neighbors", pushed)
                        .event("Pushed " + pushed.size() + " unvisited neighbor(s) of " + current)
                        .build();

                frames.add(frame(step++, current, visited, stack, discoveryOrder,
                        parentMap, treeEdges,
                        "Pushed " + unvisited.size() + " unvisited neighbor(s) of " + current,
                        depth, pushTelemetry));
            }
        }

        // Final frame
        int maxDepth = depthMap.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        AlgorithmTelemetry completeTelemetry = TelemetryBuilder.create("Complete")
                .metric("Visited", visited.size() + "/" + totalNodes)
                .metric("Max Depth", maxDepth)
                .section("Discovery Order", List.copyOf(discoveryOrder))
                .event("DFS complete")
                .build();

        frames.add(frame(step, null, visited, stack, discoveryOrder,
                parentMap, treeEdges,
                "DFS complete — visited " + visited.size() + " of " + totalNodes + " nodes",
                maxDepth, completeTelemetry));

        return Collections.unmodifiableList(frames);
    }

    /** Converts stack (Deque) to a list preserving stack order (top first). */
    private static List<String> stackToList(Deque<String> stack) {
        return List.copyOf(stack);
    }

    private static AlgorithmFrame frame(
            int stepIndex, String currentNode,
            Set<String> visited, Deque<String> stack, List<String> discoveryOrder,
            Map<String, String> parentMap, Set<AlgorithmFrame.TraversalEdge> treeEdges,
            String statusMessage, int depth, AlgorithmTelemetry telemetry) {
        return new AlgorithmFrame(AlgorithmFrame.AlgorithmType.DFS, stepIndex, currentNode,
                Set.copyOf(visited), stackToList(stack), List.copyOf(discoveryOrder),
                Map.copyOf(parentMap), Set.copyOf(treeEdges), statusMessage, depth,
                Map.of(), null, List.of(), telemetry);
    }
}
