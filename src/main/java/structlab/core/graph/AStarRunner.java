package structlab.core.graph;

import java.util.*;

/**
 * A* shortest-path algorithm producing a list of {@link AlgorithmFrame}s
 * for step-by-step playback.
 *
 * <p>Uses f(n) = g(n) + h(n) where g is the actual cost from the source and
 * h is a heuristic estimate to the target. Requires non-negative edge weights
 * and a consistent (admissible) heuristic.</p>
 *
 * <p>Supports two heuristic modes:
 * <ul>
 *   <li><b>Euclidean</b> — uses supplied node positions for straight-line distance.</li>
 *   <li><b>Zero</b> — h(n) = 0, degenerating to Dijkstra (useful when positions unavailable).</li>
 * </ul></p>
 */
public final class AStarRunner {

    public static final double INF = Double.MAX_VALUE;

    private AStarRunner() {}

    /**
     * Runs A* from source to target using the given heuristic positions.
     *
     * @param graph     the graph (non-negative weights required)
     * @param source    starting node label
     * @param target    target node label (required for A*)
     * @param positions node positions for Euclidean heuristic, or null for zero heuristic
     * @return unmodifiable list of frames
     * @throws IllegalArgumentException if source/target missing or negative weights found
     */
    public static List<AlgorithmFrame> run(Graph graph, String source, String target,
                                           Map<String, double[]> positions) {
        if (!graph.hasNode(source)) {
            throw new IllegalArgumentException("Source node not found: " + source);
        }
        if (target == null) {
            throw new IllegalArgumentException("A* requires a target node");
        }
        if (!graph.hasNode(target)) {
            throw new IllegalArgumentException("Target node not found: " + target);
        }
        validateNonNegativeWeights(graph);

        List<AlgorithmFrame> frames = new ArrayList<>();
        List<String> nodes = graph.nodes();
        Map<String, Integer> nodeIndex = new LinkedHashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            nodeIndex.put(nodes.get(i), i);
        }

        Set<String> closed = new LinkedHashSet<>();
        Map<String, Double> gScore = new LinkedHashMap<>();
        Map<String, Double> fScore = new LinkedHashMap<>();
        Map<String, String> parentMap = new LinkedHashMap<>();
        Set<AlgorithmFrame.TraversalEdge> treeEdges = new LinkedHashSet<>();
        List<String> settleOrder = new ArrayList<>();

        // Priority queue ordered by fScore
        PriorityQueue<double[]> openPQ = new PriorityQueue<>(
                Comparator.comparingDouble(a -> a[0]));

        // Initialize
        for (String node : nodes) {
            gScore.put(node, INF);
            fScore.put(node, INF);
        }
        gScore.put(source, 0.0);
        double hSource = heuristic(source, target, positions);
        fScore.put(source, hSource);
        openPQ.add(new double[]{hSource, nodeIndex.get(source)});

        int step = 0;
        int totalNodes = nodes.size();

        AlgorithmTelemetry initTelemetry = TelemetryBuilder.create("Initialization")
                .metric("Source", source)
                .metric("Target", target)
                .metric("h(" + source + ")", DijkstraRunner.formatDist(hSource))
                .section("Open Set", frontierWithF(openPQ, nodes, gScore, fScore))
                .event("Source " + source + " added to open set")
                .build();

        // Build initial distances map showing f-scores for display
        frames.add(buildFrame(step++, null, closed, frontierNodes(openPQ, nodes),
                settleOrder, parentMap, treeEdges,
                "A* started — source " + source + ", target " + target
                        + ", h(" + source + ")=" + DijkstraRunner.formatDist(hSource),
                fScore, target, List.of(), initTelemetry));

        while (!openPQ.isEmpty()) {
            double[] top = openPQ.poll();
            double f = top[0];
            String current = nodes.get((int) top[1]);

            if (closed.contains(current)) continue;
            if (f > fScore.get(current)) continue; // stale entry

            closed.add(current);
            settleOrder.add(current);

            double g = gScore.get(current);
            double h = heuristic(current, target, positions);
            String expandMsg = "Expanded " + current + " — f=" + DijkstraRunner.formatDist(f)
                    + ", g=" + DijkstraRunner.formatDist(g)
                    + ", h=" + DijkstraRunner.formatDist(h);

            // Check if target reached
            if (current.equals(target)) {
                List<String> path = reconstructPath(parentMap, source, target);

                AlgorithmTelemetry targetTelemetry = TelemetryBuilder.create("Target-Found")
                        .metric("Distance", DijkstraRunner.formatDist(g))
                        .metric("Path Length", path.size() + " nodes")
                        .metric("Expanded", closed.size() + "/" + totalNodes)
                        .section("Shortest Path", path)
                        .event("Target " + target + " reached")
                        .build();

                frames.add(buildFrame(step++, current, closed, List.of(),
                        settleOrder, parentMap, treeEdges,
                        "Target " + target + " reached — shortest distance: "
                                + DijkstraRunner.formatDist(g),
                        gScore, target, path, targetTelemetry));

                AlgorithmTelemetry completeTelemetry = TelemetryBuilder.create("Complete")
                        .metric("Distance", DijkstraRunner.formatDist(g))
                        .metric("Path Length", path.size() + " nodes")
                        .metric("Expanded", closed.size() + "/" + totalNodes)
                        .section("Shortest Path", path)
                        .event("A* complete")
                        .build();

                frames.add(buildFrame(step, null, closed, List.of(),
                        settleOrder, parentMap, treeEdges,
                        "A* complete — shortest path to " + target + ": "
                                + DijkstraRunner.formatDist(g)
                                + " (" + path.size() + " nodes)",
                        gScore, target, path, completeTelemetry));
                return Collections.unmodifiableList(frames);
            }

            AlgorithmTelemetry expandTelemetry = TelemetryBuilder.create("Expand")
                    .metric("Node", current)
                    .metric("g", DijkstraRunner.formatDist(g))
                    .metric("h", DijkstraRunner.formatDist(h))
                    .metric("f", DijkstraRunner.formatDist(f))
                    .metric("Expanded", closed.size() + "/" + totalNodes)
                    .section("Open Set", frontierWithF(openPQ, nodes, gScore, fScore))
                    .event("Expanded " + current)
                    .build();

            frames.add(buildFrame(step++, current, closed,
                    frontierNodes(openPQ, nodes),
                    settleOrder, parentMap, treeEdges, expandMsg, gScore, target,
                    List.of(), expandTelemetry));

            // Relax neighbors
            for (String neighbor : graph.neighbors(current)) {
                if (closed.contains(neighbor)) continue;

                double edgeWeight = graph.edgeWeight(current, neighbor).orElse(1.0);
                double tentativeG = g + edgeWeight;
                double oldG = gScore.get(neighbor);

                if (tentativeG < oldG) {
                    gScore.put(neighbor, tentativeG);
                    double hNeighbor = heuristic(neighbor, target, positions);
                    double newF = tentativeG + hNeighbor;
                    fScore.put(neighbor, newF);
                    parentMap.put(neighbor, current);

                    treeEdges.removeIf(e -> e.to().equals(neighbor));
                    treeEdges.add(new AlgorithmFrame.TraversalEdge(current, neighbor));

                    openPQ.add(new double[]{newF, nodeIndex.get(neighbor)});

                    String relaxMsg;
                    if (oldG == INF) {
                        relaxMsg = "Discovered " + neighbor + " via " + current
                                + " — g=" + DijkstraRunner.formatDist(tentativeG)
                                + ", h=" + DijkstraRunner.formatDist(hNeighbor)
                                + ", f=" + DijkstraRunner.formatDist(newF);
                    } else {
                        relaxMsg = "Relaxed " + neighbor + " via " + current
                                + " — g: " + DijkstraRunner.formatDist(oldG)
                                + " → " + DijkstraRunner.formatDist(tentativeG)
                                + ", f=" + DijkstraRunner.formatDist(newF);
                    }

                    AlgorithmTelemetry relaxTelemetry = TelemetryBuilder.create(
                            oldG == INF ? "Discover" : "Relax")
                            .metric("Node", neighbor)
                            .metric("g", DijkstraRunner.formatDist(tentativeG))
                            .metric("h", DijkstraRunner.formatDist(hNeighbor))
                            .metric("f", DijkstraRunner.formatDist(newF))
                            .metric("Via", current)
                            .section("Open Set", frontierWithF(openPQ, nodes, gScore, fScore))
                            .event(relaxMsg)
                            .build();

                    frames.add(buildFrame(step++, current, closed,
                            frontierNodes(openPQ, nodes),
                            settleOrder, parentMap, treeEdges, relaxMsg,
                            gScore, target, List.of(), relaxTelemetry));
                }
            }
        }

        // Target unreachable
        AlgorithmTelemetry unreachTelemetry = TelemetryBuilder.create("Complete")
                .metric("Expanded", closed.size() + "/" + totalNodes)
                .metric("Target", target + " unreachable")
                .event("A* exhausted open set — target unreachable")
                .build();

        frames.add(buildFrame(step, null, closed, List.of(), settleOrder,
                parentMap, treeEdges,
                "A* complete — target " + target + " is unreachable from " + source,
                gScore, target, List.of(), unreachTelemetry));

        return Collections.unmodifiableList(frames);
    }

    // ── Heuristic ───────────────────────────────────────────

    static double heuristic(String node, String target,
                            Map<String, double[]> positions) {
        if (positions == null) return 0.0;
        double[] np = positions.get(node);
        double[] tp = positions.get(target);
        if (np == null || tp == null) return 0.0;
        double dx = np[0] - tp[0];
        double dy = np[1] - tp[1];
        return Math.sqrt(dx * dx + dy * dy);
    }

    // ── Helpers ─────────────────────────────────────────────

    private static void validateNonNegativeWeights(Graph graph) {
        for (Graph.Edge edge : graph.edges()) {
            if (edge.weight() < 0) {
                throw new IllegalArgumentException(
                        "A* requires non-negative edge weights. "
                                + "Edge " + edge.from() + " → " + edge.to()
                                + " has weight " + edge.weight());
            }
        }
    }

    private static AlgorithmFrame buildFrame(
            int stepIndex, String currentNode, Set<String> closed,
            List<String> frontier, List<String> settleOrder,
            Map<String, String> parentMap, Set<AlgorithmFrame.TraversalEdge> treeEdges,
            String statusMessage, Map<String, Double> distances,
            String target, List<String> shortestPath,
            AlgorithmTelemetry telemetry) {
        return new AlgorithmFrame(
                AlgorithmFrame.AlgorithmType.A_STAR, stepIndex, currentNode,
                Set.copyOf(closed), List.copyOf(frontier), List.copyOf(settleOrder),
                Map.copyOf(parentMap), Set.copyOf(treeEdges), statusMessage, 0,
                Map.copyOf(distances), target, List.copyOf(shortestPath), telemetry);
    }

    private static List<String> frontierNodes(PriorityQueue<double[]> pq,
                                              List<String> nodes) {
        Set<String> seen = new LinkedHashSet<>();
        PriorityQueue<double[]> copy = new PriorityQueue<>(pq);
        while (!copy.isEmpty()) {
            double[] entry = copy.poll();
            seen.add(nodes.get((int) entry[1]));
        }
        return List.copyOf(seen);
    }

    private static List<String> frontierWithF(PriorityQueue<double[]> pq,
                                               List<String> nodes,
                                               Map<String, Double> gScore,
                                               Map<String, Double> fScore) {
        List<String> result = new ArrayList<>();
        PriorityQueue<double[]> copy = new PriorityQueue<>(pq);
        Set<String> seen = new LinkedHashSet<>();
        while (!copy.isEmpty()) {
            String n = nodes.get((int) copy.poll()[1]);
            if (seen.add(n)) {
                result.add(n + " (g=" + DijkstraRunner.formatDist(gScore.getOrDefault(n, INF))
                        + ", f=" + DijkstraRunner.formatDist(fScore.getOrDefault(n, INF)) + ")");
            }
        }
        return result;
    }

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
            return List.of();
        }
        Collections.reverse(path);
        return List.copyOf(path);
    }
}
