package structlab.core.graph;

import java.util.*;

/**
 * Kruskal's Minimum Spanning Tree algorithm producing step-by-step
 * {@link AlgorithmFrame}s for playback.
 *
 * <p>Sorts all edges by weight, greedily adds edges that don't form cycles
 * using Union-Find for efficient cycle detection.</p>
 */
public final class KruskalRunner {

    private KruskalRunner() {}

    /**
     * Runs Kruskal's MST algorithm.
     *
     * @param graph undirected graph with non-negative weights
     * @return unmodifiable list of frames
     * @throws IllegalArgumentException if graph is directed or has negative weights
     */
    public static List<AlgorithmFrame> run(Graph graph) {
        if (graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Kruskal's algorithm requires an undirected graph.");
        }
        validateNonNegativeWeights(graph);

        List<AlgorithmFrame> frames = new ArrayList<>();
        List<String> nodes = graph.nodes();
        UnionFind uf = new UnionFind(nodes);

        // Sort edges by weight
        List<Graph.Edge> sorted = new ArrayList<>(graph.edges());
        sorted.sort(Comparator.comparingDouble(Graph.Edge::weight));

        Set<String> processed = new LinkedHashSet<>();
        Set<AlgorithmFrame.TraversalEdge> mstEdges = new LinkedHashSet<>();
        Map<String, String> parentMap = new LinkedHashMap<>();
        List<String> addOrder = new ArrayList<>();
        double totalWeight = 0.0;
        int step = 0;
        int totalNodes = nodes.size();

        Map<String, Double> costMap = new LinkedHashMap<>();
        for (String n : nodes) {
            costMap.put(n, DijkstraRunner.INF);
        }

        AlgorithmTelemetry initTelemetry = TelemetryBuilder.create("Initialization")
                .metric("Edges", sorted.size())
                .metric("Nodes", totalNodes)
                .metric("MST Weight", "0")
                .event("Sorted " + sorted.size() + " edges by weight")
                .build();

        frames.add(buildFrame(step++, null, processed, List.of(), addOrder,
                parentMap, mstEdges,
                "Kruskal started — " + sorted.size() + " edges sorted by weight",
                costMap, totalWeight, initTelemetry));

        int edgeCursor = 0;
        for (Graph.Edge edge : sorted) {
            edgeCursor++;
            String from = edge.from();
            String to = edge.to();
            double w = edge.weight();

            if (uf.union(from, to)) {
                mstEdges.add(new AlgorithmFrame.TraversalEdge(from, to));
                parentMap.put(to, from);
                totalWeight += w;

                processed.add(from);
                processed.add(to);
                if (!addOrder.contains(from)) addOrder.add(from);
                if (!addOrder.contains(to)) addOrder.add(to);

                costMap.put(from, 0.0);
                costMap.put(to, 0.0);

                AlgorithmTelemetry acceptTelemetry = TelemetryBuilder.create("Accept")
                        .metric("Edge", from + " — " + to)
                        .metric("Weight", DijkstraRunner.formatDist(w))
                        .metric("MST Edges", mstEdges.size() + "/" + (totalNodes - 1))
                        .metric("MST Weight", DijkstraRunner.formatDist(totalWeight))
                        .metric("Components", uf.componentCount())
                        .metric("Edge Cursor", edgeCursor + "/" + sorted.size())
                        .section("MST Nodes", List.copyOf(addOrder))
                        .event("Accepted " + from + "—" + to + " (no cycle)")
                        .build();

                frames.add(buildFrame(step++, from, processed,
                        List.of(to),
                        addOrder, parentMap, mstEdges,
                        "Accepted edge " + from + " — " + to
                                + " (w=" + DijkstraRunner.formatDist(w)
                                + ") — total " + DijkstraRunner.formatDist(totalWeight),
                        costMap, totalWeight, acceptTelemetry));
            } else {
                AlgorithmTelemetry rejectTelemetry = TelemetryBuilder.create("Reject")
                        .metric("Edge", from + " — " + to)
                        .metric("Weight", DijkstraRunner.formatDist(w))
                        .metric("Reason", "Would form cycle")
                        .metric("Edge Cursor", edgeCursor + "/" + sorted.size())
                        .metric("Components", uf.componentCount())
                        .event("Rejected " + from + "—" + to + " (cycle)")
                        .build();

                frames.add(buildFrame(step++, from, processed,
                        List.of(to),
                        addOrder, parentMap, mstEdges,
                        "Rejected edge " + from + " — " + to
                                + " (w=" + DijkstraRunner.formatDist(w)
                                + ") — would form cycle",
                        costMap, totalWeight, rejectTelemetry));
            }

            if (mstEdges.size() == totalNodes - 1) break;
        }

        AlgorithmTelemetry completeTelemetry = TelemetryBuilder.create("Complete")
                .metric("MST Weight", DijkstraRunner.formatDist(totalWeight))
                .metric("MST Edges", mstEdges.size())
                .metric("Components", uf.componentCount())
                .section("MST Nodes", List.copyOf(addOrder))
                .event("Kruskal complete")
                .build();

        String finalMsg = "Kruskal complete — MST weight "
                + DijkstraRunner.formatDist(totalWeight)
                + " (" + mstEdges.size() + " edges, "
                + uf.componentCount() + " component"
                + (uf.componentCount() != 1 ? "s" : "") + ")";
        frames.add(buildFrame(step, null, processed, List.of(),
                addOrder, parentMap, mstEdges, finalMsg, costMap, totalWeight,
                completeTelemetry));

        return Collections.unmodifiableList(frames);
    }

    // ── Helpers ─────────────────────────────────────────────

    private static void validateNonNegativeWeights(Graph graph) {
        for (Graph.Edge edge : graph.edges()) {
            if (edge.weight() < 0) {
                throw new IllegalArgumentException(
                        "Kruskal requires non-negative edge weights. Edge "
                                + edge.from() + " → " + edge.to()
                                + " has weight " + edge.weight());
            }
        }
    }

    private static AlgorithmFrame buildFrame(
            int stepIndex, String currentNode, Set<String> processed,
            List<String> frontier, List<String> addOrder,
            Map<String, String> parentMap,
            Set<AlgorithmFrame.TraversalEdge> treeEdges,
            String statusMessage, Map<String, Double> costMap,
            double totalWeight, AlgorithmTelemetry telemetry) {
        Map<String, Double> distances = new LinkedHashMap<>(costMap);
        distances.put("__MST_TOTAL__", totalWeight);
        return new AlgorithmFrame(
                AlgorithmFrame.AlgorithmType.KRUSKAL, stepIndex, currentNode,
                Set.copyOf(processed), List.copyOf(frontier), List.copyOf(addOrder),
                Map.copyOf(parentMap), Set.copyOf(treeEdges), statusMessage, 0,
                Map.copyOf(distances), null, List.of(), telemetry);
    }
}
