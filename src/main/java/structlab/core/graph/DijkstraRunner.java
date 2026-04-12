package structlab.core.graph;

import java.util.*;

/**
 * Dijkstra's shortest-path algorithm producing a list of {@link AlgorithmFrame}s
 * for step-by-step playback.
 *
 * <p>Requires non-negative edge weights. Rejects graphs containing any negative
 * weight with a clear error message.</p>
 */
public final class DijkstraRunner {

    /** Infinity sentinel for unreached nodes. */
    public static final double INF = Double.MAX_VALUE;

    private DijkstraRunner() {}

    /**
     * Runs Dijkstra from the given source node and returns all frames.
     *
     * @param graph  the graph to traverse (non-negative weights required)
     * @param source the starting node label
     * @param target optional target node label (null for full shortest-path tree)
     * @return unmodifiable list of frames, one per step
     * @throws IllegalArgumentException if source doesn't exist, target doesn't exist,
     *                                  or graph has negative edge weights
     */
    public static List<AlgorithmFrame> run(Graph graph, String source, String target) {
        if (!graph.hasNode(source)) {
            throw new IllegalArgumentException("Source node not found: " + source);
        }
        if (target != null && !graph.hasNode(target)) {
            throw new IllegalArgumentException("Target node not found: " + target);
        }
        validateNonNegativeWeights(graph);

        List<AlgorithmFrame> frames = new ArrayList<>();
        Set<String> settled = new LinkedHashSet<>();
        Map<String, Double> dist = new LinkedHashMap<>();
        Map<String, String> parentMap = new LinkedHashMap<>();
        Set<AlgorithmFrame.TraversalEdge> treeEdges = new LinkedHashSet<>();
        List<String> settleOrder = new ArrayList<>();

        // Priority queue: (distance, node)
        PriorityQueue<double[]> pq = new PriorityQueue<>(
                Comparator.comparingDouble(a -> a[0]));
        Map<String, Integer> nodeIndex = new LinkedHashMap<>();
        List<String> nodes = graph.nodes();
        for (int i = 0; i < nodes.size(); i++) {
            nodeIndex.put(nodes.get(i), i);
        }

        // Initialize distances
        for (String node : nodes) {
            dist.put(node, INF);
        }
        dist.put(source, 0.0);
        pq.add(new double[]{0.0, nodeIndex.get(source)});

        int step = 0;

        // Initial frame
        frames.add(buildFrame(step++, null, settled, frontierNodes(pq, nodeIndex, nodes),
                settleOrder, parentMap, treeEdges,
                "Dijkstra started — source " + source + " with distance 0",
                dist, target));

        while (!pq.isEmpty()) {
            double[] top = pq.poll();
            double d = top[0];
            String current = nodes.get((int) top[1]);

            if (settled.contains(current)) continue;
            if (d > dist.get(current)) continue; // stale entry

            settled.add(current);
            settleOrder.add(current);

            // Frame: settling current node
            String settleMsg = "Settled " + current + " with distance " + formatDist(d);
            if (target != null && current.equals(target)) {
                settleMsg = "Target " + target + " settled — shortest distance: " + formatDist(d);
            }

            List<String> path = current.equals(target)
                    ? reconstructPath(parentMap, source, target) : List.of();

            frames.add(buildFrame(step++, current, settled, frontierNodes(pq, nodeIndex, nodes),
                    settleOrder, parentMap, treeEdges, settleMsg, dist, target, path));

            // Early termination if target settled
            if (target != null && current.equals(target)) {
                frames.add(buildFrame(step, null, settled, List.of(),
                        settleOrder, parentMap, treeEdges,
                        "Dijkstra complete — shortest path to " + target + ": " + formatDist(d)
                                + " (" + path.size() + " nodes)",
                        dist, target, path));
                return Collections.unmodifiableList(frames);
            }

            // Relax neighbors
            for (String neighbor : graph.neighbors(current)) {
                if (settled.contains(neighbor)) continue;

                double edgeWeight = graph.edgeWeight(current, neighbor).orElse(1.0);
                double newDist = d + edgeWeight;
                double oldDist = dist.get(neighbor);

                if (newDist < oldDist) {
                    double prevDist = oldDist;
                    dist.put(neighbor, newDist);
                    parentMap.put(neighbor, current);

                    // Update tree edges: remove old edge to neighbor, add new one
                    treeEdges.removeIf(e -> e.to().equals(neighbor));
                    treeEdges.add(new AlgorithmFrame.TraversalEdge(current, neighbor));

                    pq.add(new double[]{newDist, nodeIndex.get(neighbor)});

                    String relaxMsg;
                    if (prevDist == INF) {
                        relaxMsg = "Discovered " + neighbor + " via " + current
                                + " — distance " + formatDist(newDist);
                    } else {
                        relaxMsg = "Relaxed " + neighbor + " via " + current
                                + " — distance " + formatDist(prevDist)
                                + " → " + formatDist(newDist);
                    }

                    frames.add(buildFrame(step++, current, settled,
                            frontierNodes(pq, nodeIndex, nodes),
                            settleOrder, parentMap, treeEdges, relaxMsg, dist, target));
                }
            }
        }

        // Final frame: all reachable nodes settled
        String finalMsg = "Dijkstra complete — settled " + settled.size()
                + " of " + graph.nodeCount() + " nodes";
        if (target != null && !settled.contains(target)) {
            finalMsg = "Dijkstra complete — target " + target + " is unreachable from " + source;
        }

        List<String> finalPath = (target != null && settled.contains(target))
                ? reconstructPath(parentMap, source, target) : List.of();

        frames.add(buildFrame(step, null, settled, List.of(), settleOrder,
                parentMap, treeEdges, finalMsg, dist, target, finalPath));

        return Collections.unmodifiableList(frames);
    }

    /** Convenience overload: no target node. */
    public static List<AlgorithmFrame> run(Graph graph, String source) {
        return run(graph, source, null);
    }

    // ── Helpers ─────────────────────────────────────────────

    private static void validateNonNegativeWeights(Graph graph) {
        for (Graph.Edge edge : graph.edges()) {
            if (edge.weight() < 0) {
                throw new IllegalArgumentException(
                        "Dijkstra requires non-negative edge weights. "
                                + "Edge " + edge.from() + " → " + edge.to()
                                + " has weight " + edge.weight());
            }
        }
    }

    private static AlgorithmFrame buildFrame(
            int stepIndex, String currentNode, Set<String> settled,
            List<String> frontier, List<String> settleOrder,
            Map<String, String> parentMap, Set<AlgorithmFrame.TraversalEdge> treeEdges,
            String statusMessage, Map<String, Double> distances, String target) {
        return buildFrame(stepIndex, currentNode, settled, frontier, settleOrder,
                parentMap, treeEdges, statusMessage, distances, target, List.of());
    }

    private static AlgorithmFrame buildFrame(
            int stepIndex, String currentNode, Set<String> settled,
            List<String> frontier, List<String> settleOrder,
            Map<String, String> parentMap, Set<AlgorithmFrame.TraversalEdge> treeEdges,
            String statusMessage, Map<String, Double> distances,
            String target, List<String> shortestPath) {
        return new AlgorithmFrame(
                AlgorithmFrame.AlgorithmType.DIJKSTRA, stepIndex, currentNode,
                Set.copyOf(settled), List.copyOf(frontier), List.copyOf(settleOrder),
                Map.copyOf(parentMap), Set.copyOf(treeEdges), statusMessage, 0,
                Map.copyOf(distances), target, List.copyOf(shortestPath));
    }

    /** Returns labels of nodes currently in the priority queue (unsettled, finite distance). */
    private static List<String> frontierNodes(
            PriorityQueue<double[]> pq, Map<String, Integer> nodeIndex, List<String> nodes) {
        // Build reverse index
        Set<String> seen = new LinkedHashSet<>();
        // Copy to avoid consuming the PQ
        PriorityQueue<double[]> copy = new PriorityQueue<>(pq);
        while (!copy.isEmpty()) {
            double[] entry = copy.poll();
            String label = nodes.get((int) entry[1]);
            seen.add(label);
        }
        return List.copyOf(seen);
    }

    /** Reconstructs the shortest path from source to target using the parent map. */
    static List<String> reconstructPath(Map<String, String> parentMap,
                                        String source, String target) {
        List<String> path = new ArrayList<>();
        String current = target;
        while (current != null) {
            path.add(current);
            if (current.equals(source)) break;
            current = parentMap.get(current);
        }
        if (path.isEmpty() || !path.get(path.size() - 1).equals(source)) {
            return List.of(); // unreachable
        }
        Collections.reverse(path);
        return List.copyOf(path);
    }

    /** Formats a distance value for display. */
    public static String formatDist(double d) {
        if (d == INF) return "∞";
        if (d == Math.floor(d) && !Double.isInfinite(d)) {
            return String.valueOf((long) d);
        }
        return String.format(java.util.Locale.US, "%.1f", d);
    }
}
