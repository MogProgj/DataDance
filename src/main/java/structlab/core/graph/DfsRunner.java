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

        // Initial frame: source pushed
        stack.push(source);
        depthMap.put(source, 0);

        frames.add(AlgorithmFrame.traversal(
                AlgorithmFrame.AlgorithmType.DFS, step++, null,
                Set.copyOf(visited), stackToList(stack), List.copyOf(discoveryOrder),
                Map.copyOf(parentMap), Set.copyOf(treeEdges),
                "DFS started — pushed source " + source, 0));

        while (!stack.isEmpty()) {
            String current = stack.pop();

            if (visited.contains(current)) {
                // Already visited via another path — skip (backtrack frame)
                frames.add(AlgorithmFrame.traversal(
                        AlgorithmFrame.AlgorithmType.DFS, step++, current,
                        Set.copyOf(visited), stackToList(stack), List.copyOf(discoveryOrder),
                        Map.copyOf(parentMap), Set.copyOf(treeEdges),
                        "Popped " + current + " — already visited, backtracking",
                        depthMap.getOrDefault(current, 0)));
                continue;
            }

            visited.add(current);
            discoveryOrder.add(current);
            int depth = depthMap.getOrDefault(current, 0);

            // Frame: visiting current node
            frames.add(AlgorithmFrame.traversal(
                    AlgorithmFrame.AlgorithmType.DFS, step++, current,
                    Set.copyOf(visited), stackToList(stack), List.copyOf(discoveryOrder),
                    Map.copyOf(parentMap), Set.copyOf(treeEdges),
                    "Visiting " + current + " (depth " + depth + ")", depth));

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
                // Only record tree edge for first discovery
                if (!parentMap.containsKey(neighbor) && !neighbor.equals(source)) {
                    parentMap.put(neighbor, current);
                    treeEdges.add(new AlgorithmFrame.TraversalEdge(current, neighbor));
                }
            }

            if (!unvisited.isEmpty()) {
                frames.add(AlgorithmFrame.traversal(
                        AlgorithmFrame.AlgorithmType.DFS, step++, current,
                        Set.copyOf(visited), stackToList(stack), List.copyOf(discoveryOrder),
                        Map.copyOf(parentMap), Set.copyOf(treeEdges),
                        "Pushed " + unvisited.size() + " unvisited neighbor(s) of " + current,
                        depth));
            }
        }

        // Final frame
        frames.add(AlgorithmFrame.traversal(
                AlgorithmFrame.AlgorithmType.DFS, step, null,
                Set.copyOf(visited), List.of(), List.copyOf(discoveryOrder),
                Map.copyOf(parentMap), Set.copyOf(treeEdges),
                "DFS complete — visited " + visited.size() + " of " + graph.nodeCount() + " nodes",
                depthMap.values().stream().mapToInt(Integer::intValue).max().orElse(0)));

        return Collections.unmodifiableList(frames);
    }

    /** Converts stack (Deque) to a list preserving stack order (top first). */
    private static List<String> stackToList(Deque<String> stack) {
        return List.copyOf(stack);
    }
}
