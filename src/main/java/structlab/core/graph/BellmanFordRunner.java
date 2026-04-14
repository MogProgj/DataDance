package structlab.core.graph;

import java.util.*;

/**
 * Bellman-Ford shortest-path algorithm producing a list of {@link AlgorithmFrame}s
 * for step-by-step playback.
 *
 * <p>Unlike Dijkstra, this algorithm handles negative edge weights and detects
 * negative-weight cycles. Time complexity: O(V·E).</p>
 */
public final class BellmanFordRunner {

    private static final double INF = Double.MAX_VALUE;

    private BellmanFordRunner() {}

    /**
     * Runs Bellman-Ford from the given source node and returns all frames.
     *
     * @param graph  the graph to traverse (negative weights allowed)
     * @param source the starting node label
     * @param target optional target node label (null for full shortest-path tree)
     * @return unmodifiable list of frames, one per step
     * @throws IllegalArgumentException if source or target doesn't exist
     */
    public static List<AlgorithmFrame> run(Graph graph, String source, String target) {
        if (!graph.hasNode(source)) {
            throw new IllegalArgumentException("Source node not found: " + source);
        }
        if (target != null && !graph.hasNode(target)) {
            throw new IllegalArgumentException("Target node not found: " + target);
        }

        List<AlgorithmFrame> frames = new ArrayList<>();
        List<String> nodes = graph.nodes();
        List<Graph.Edge> allEdges = getAllDirectedEdges(graph);
        int V = nodes.size();

        Map<String, Double> dist = new LinkedHashMap<>();
        Map<String, String> parentMap = new LinkedHashMap<>();
        Set<AlgorithmFrame.TraversalEdge> treeEdges = new LinkedHashSet<>();
        Set<String> settled = new LinkedHashSet<>();
        List<String> settleOrder = new ArrayList<>();

        // Initialize distances
        for (String node : nodes) {
            dist.put(node, INF);
        }
        dist.put(source, 0.0);
        settled.add(source);
        settleOrder.add(source);

        int step = 0;
        int totalNodes = nodes.size();

        AlgorithmTelemetry initTelemetry = TelemetryBuilder.create("Initialization")
                .metric("Source", source)
                .metric("Nodes", totalNodes)
                .metric("Edges", allEdges.size())
                .metric("Passes Required", totalNodes - 1)
                .event("Source " + source + " initialized with distance 0")
                .build();

        frames.add(buildFrame(step++, null, settled, List.of(), settleOrder,
                parentMap, treeEdges,
                "Bellman-Ford started — source " + source + " with distance 0",
                dist, target, List.of(), initTelemetry));

        // V-1 relaxation passes
        for (int pass = 1; pass < V; pass++) {
            boolean anyUpdate = false;

            for (Graph.Edge edge : allEdges) {
                double du = dist.get(edge.from());
                if (du == INF) continue;

                double newDist = du + edge.weight();
                double oldDist = dist.get(edge.to());

                if (newDist < oldDist) {
                    anyUpdate = true;
                    dist.put(edge.to(), newDist);

                    String oldParent = parentMap.get(edge.to());
                    if (oldParent != null) {
                        treeEdges.removeIf(e -> e.to().equals(edge.to()));
                    }
                    parentMap.put(edge.to(), edge.from());
                    treeEdges.add(new AlgorithmFrame.TraversalEdge(edge.from(), edge.to()));

                    if (!settled.contains(edge.to())) {
                        settled.add(edge.to());
                        settleOrder.add(edge.to());
                    }

                    String relaxMsg;
                    if (oldDist == INF) {
                        relaxMsg = "Pass " + pass + ": Discovered " + edge.to() + " via "
                                + edge.from() + " — distance " + formatDist(newDist);
                    } else {
                        relaxMsg = "Pass " + pass + ": Relaxed " + edge.from() + "→" + edge.to()
                                + " — distance " + formatDist(oldDist)
                                + " → " + formatDist(newDist);
                    }

                    AlgorithmTelemetry relaxTelemetry = TelemetryBuilder.create("Relax")
                            .metric("Pass", pass + "/" + (V - 1))
                            .metric("Edge", edge.from() + "→" + edge.to())
                            .metric("Old Distance", formatDist(oldDist))
                            .metric("New Distance", formatDist(newDist))
                            .metric("Edge Weight", formatDist(edge.weight()))
                            .metric("Settled", settled.size() + "/" + totalNodes)
                            .event(relaxMsg)
                            .build();

                    frames.add(buildFrame(step++, edge.from(), settled,
                            List.of(edge.to()), settleOrder,
                            parentMap, treeEdges, relaxMsg, dist, target,
                            List.of(), relaxTelemetry));
                }
            }

            if (!anyUpdate) {
                AlgorithmTelemetry convergeTelemetry = TelemetryBuilder.create("Converge")
                        .metric("Pass", pass + "/" + (V - 1))
                        .metric("Settled", settled.size() + "/" + totalNodes)
                        .event("No updates in pass " + pass + " — converged early")
                        .build();

                frames.add(buildFrame(step++, null, settled, List.of(), settleOrder,
                        parentMap, treeEdges,
                        "Pass " + pass + ": No updates — converged early",
                        dist, target, List.of(), convergeTelemetry));
                break;
            }
        }

        // Negative cycle detection: one more pass
        boolean negativeCycle = false;
        for (Graph.Edge edge : allEdges) {
            double du = dist.get(edge.from());
            if (du == INF) continue;
            if (du + edge.weight() < dist.get(edge.to())) {
                negativeCycle = true;
                break;
            }
        }

        if (negativeCycle) {
            AlgorithmTelemetry ncTelemetry = TelemetryBuilder.create("Negative-Cycle")
                    .metric("Settled", settled.size() + "/" + totalNodes)
                    .event("Negative cycle detected — shortest paths undefined")
                    .build();

            frames.add(buildFrame(step, null, settled, List.of(), settleOrder,
                    parentMap, treeEdges,
                    "Negative cycle detected — shortest paths are undefined",
                    dist, target, List.of(), ncTelemetry));
        } else {
            List<String> finalPath = List.of();
            if (target != null && dist.get(target) != INF) {
                finalPath = reconstructPath(parentMap, source, target);
            }

            String finalMsg;
            if (target != null && dist.get(target) == INF) {
                finalMsg = "Bellman-Ford complete — target " + target + " is unreachable from " + source;
            } else if (target != null) {
                finalMsg = "Bellman-Ford complete — shortest path to " + target + ": "
                        + formatDist(dist.get(target))
                        + " (" + finalPath.size() + " nodes)";
            } else {
                finalMsg = "Bellman-Ford complete — settled " + settled.size()
                        + " of " + totalNodes + " reachable nodes";
            }

            AlgorithmTelemetry completeTelemetry = TelemetryBuilder.create("Complete")
                    .metric("Settled", settled.size() + "/" + totalNodes)
                    .metric("No Negative Cycle", "true")
                    .section("Settle Order", List.copyOf(settleOrder))
                    .event("Bellman-Ford complete")
                    .build();

            frames.add(buildFrame(step, null, settled, List.of(), settleOrder,
                    parentMap, treeEdges, finalMsg, dist, target, finalPath,
                    completeTelemetry));
        }

        return Collections.unmodifiableList(frames);
    }

    /** Convenience overload: no target node. */
    public static List<AlgorithmFrame> run(Graph graph, String source) {
        return run(graph, source, null);
    }

    // ── Helpers ─────────────────────────────────────────────

    /**
     * Returns all directed edges. For undirected graphs, each undirected edge
     * becomes two directed edges.
     */
    private static List<Graph.Edge> getAllDirectedEdges(Graph graph) {
        if (graph.isDirected()) {
            return graph.edges();
        }
        List<Graph.Edge> result = new ArrayList<>();
        for (Graph.Edge e : graph.edges()) {
            result.add(e);
            result.add(new Graph.Edge(e.to(), e.from(), e.weight()));
        }
        return result;
    }

    private static AlgorithmFrame buildFrame(
            int stepIndex, String currentNode, Set<String> settled,
            List<String> frontier, List<String> settleOrder,
            Map<String, String> parentMap, Set<AlgorithmFrame.TraversalEdge> treeEdges,
            String statusMessage, Map<String, Double> distances, String target,
            List<String> shortestPath, AlgorithmTelemetry telemetry) {
        return new AlgorithmFrame(
                AlgorithmFrame.AlgorithmType.BELLMAN_FORD, stepIndex, currentNode,
                Set.copyOf(settled), List.copyOf(frontier), List.copyOf(settleOrder),
                Map.copyOf(parentMap), Set.copyOf(treeEdges), statusMessage, 0,
                Map.copyOf(distances), target, List.copyOf(shortestPath), telemetry);
    }

    static List<String> reconstructPath(Map<String, String> parentMap,
                                        String source, String target) {
        List<String> path = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        String current = target;
        while (current != null && seen.add(current)) {
            path.add(current);
            if (current.equals(source)) break;
            current = parentMap.get(current);
        }
        if (path.isEmpty() || !path.get(path.size() - 1).equals(source)) {
            return List.of();
        }
        Collections.reverse(path);
        return List.copyOf(path);
    }

    static String formatDist(double d) {
        if (d == INF) return "∞";
        if (d == Math.floor(d) && !Double.isInfinite(d)) {
            return String.valueOf((long) d);
        }
        return String.format(Locale.US, "%.1f", d);
    }
}
