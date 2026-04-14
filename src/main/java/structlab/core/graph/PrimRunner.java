package structlab.core.graph;

import java.util.*;

/**
 * Prim's Minimum Spanning Tree algorithm producing step-by-step
 * {@link AlgorithmFrame}s for playback.
 *
 * <p>Requires non-negative edge weights and an undirected graph.
 * Grows the MST greedily from a source node using a priority queue.</p>
 */
public final class PrimRunner {

    private PrimRunner() {}

    /**
     * Runs Prim's MST algorithm from the given source node.
     *
     * @param graph  undirected graph with non-negative weights
     * @param source starting node
     * @return unmodifiable list of frames
     * @throws IllegalArgumentException if graph is directed, source missing, or negative weights
     */
    public static List<AlgorithmFrame> run(Graph graph, String source) {
        if (graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Prim's algorithm requires an undirected graph.");
        }
        if (!graph.hasNode(source)) {
            throw new IllegalArgumentException("Source node not found: " + source);
        }
        validateNonNegativeWeights(graph);

        List<AlgorithmFrame> frames = new ArrayList<>();
        Set<String> inMST = new LinkedHashSet<>();
        Map<String, Double> bestCost = new LinkedHashMap<>();
        Map<String, String> parentMap = new LinkedHashMap<>();
        Set<AlgorithmFrame.TraversalEdge> treeEdges = new LinkedHashSet<>();
        List<String> addOrder = new ArrayList<>();
        double totalWeight = 0.0;

        // PQ entries: [cost, nodeIndex]
        List<String> nodes = graph.nodes();
        Map<String, Integer> nodeIndex = new LinkedHashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            nodeIndex.put(nodes.get(i), i);
        }

        for (String n : nodes) {
            bestCost.put(n, DijkstraRunner.INF);
        }
        bestCost.put(source, 0.0);

        PriorityQueue<double[]> pq = new PriorityQueue<>(
                Comparator.comparingDouble(a -> a[0]));
        pq.add(new double[]{0.0, nodeIndex.get(source)});

        int step = 0;
        int totalNodes = graph.nodeCount();

        AlgorithmTelemetry initTelemetry = TelemetryBuilder.create("Initialization")
                .metric("In MST", "0/" + totalNodes)
                .metric("MST Weight", "0")
                .section("Frontier (PQ)", frontierWithCosts(pq, nodes, bestCost))
                .event("Source " + source + " added to PQ with key 0")
                .build();

        frames.add(buildFrame(step++, null, inMST, frontierLabels(pq, nodes),
                addOrder, parentMap, treeEdges,
                "Prim started — growing MST from " + source,
                bestCost, totalWeight, initTelemetry));

        while (!pq.isEmpty()) {
            double[] top = pq.poll();
            double cost = top[0];
            String current = nodes.get((int) top[1]);

            if (inMST.contains(current)) continue;
            if (cost > bestCost.get(current)) continue;

            inMST.add(current);
            addOrder.add(current);
            totalWeight += cost;

            if (parentMap.containsKey(current)) {
                treeEdges.add(new AlgorithmFrame.TraversalEdge(
                        parentMap.get(current), current));
            }

            String msg = "Added " + current + " to MST"
                    + (cost > 0 ? " — edge cost " + DijkstraRunner.formatDist(cost) : "")
                    + " — total " + DijkstraRunner.formatDist(totalWeight);

            AlgorithmTelemetry addTelemetry = TelemetryBuilder.create("Extract-Min")
                    .metric("Extracted", current)
                    .metric("Edge Cost", DijkstraRunner.formatDist(cost))
                    .metric("In MST", inMST.size() + "/" + totalNodes)
                    .metric("MST Weight", DijkstraRunner.formatDist(totalWeight))
                    .section("Frontier (PQ)", frontierWithCosts(pq, nodes, bestCost))
                    .section("MST Nodes", List.copyOf(addOrder))
                    .event("Added " + current + " to MST")
                    .build();

            frames.add(buildFrame(step++, current, inMST,
                    frontierLabels(pq, nodes),
                    addOrder, parentMap, treeEdges, msg, bestCost, totalWeight,
                    addTelemetry));

            // Relax neighbors
            for (String neighbor : graph.neighbors(current)) {
                if (inMST.contains(neighbor)) continue;
                double edgeW = graph.edgeWeight(current, neighbor).orElse(1.0);
                if (edgeW < bestCost.get(neighbor)) {
                    double oldKey = bestCost.get(neighbor);
                    bestCost.put(neighbor, edgeW);
                    parentMap.put(neighbor, current);
                    pq.add(new double[]{edgeW, nodeIndex.get(neighbor)});

                    AlgorithmTelemetry keyTelemetry = TelemetryBuilder.create("Key-Update")
                            .metric("Node", neighbor)
                            .metric("Old Key", DijkstraRunner.formatDist(oldKey))
                            .metric("New Key", DijkstraRunner.formatDist(edgeW))
                            .metric("Via", current)
                            .section("Frontier (PQ)", frontierWithCosts(pq, nodes, bestCost))
                            .event("Decreased key of " + neighbor + " from "
                                    + DijkstraRunner.formatDist(oldKey) + " to "
                                    + DijkstraRunner.formatDist(edgeW))
                            .build();

                    frames.add(buildFrame(step++, current, inMST,
                            frontierLabels(pq, nodes),
                            addOrder, parentMap, treeEdges,
                            "Updated key of " + neighbor + " to "
                                    + DijkstraRunner.formatDist(edgeW)
                                    + " via " + current,
                            bestCost, totalWeight, keyTelemetry));
                }
            }
        }

        AlgorithmTelemetry completeTelemetry = TelemetryBuilder.create("Complete")
                .metric("In MST", inMST.size() + "/" + totalNodes)
                .metric("MST Weight", DijkstraRunner.formatDist(totalWeight))
                .metric("MST Edges", treeEdges.size())
                .section("MST Nodes", List.copyOf(addOrder))
                .event("Prim complete")
                .build();

        String finalMsg = "Prim complete — MST weight "
                + DijkstraRunner.formatDist(totalWeight)
                + " (" + treeEdges.size() + " edges, "
                + inMST.size() + " of " + totalNodes + " nodes)";
        frames.add(buildFrame(step, null, inMST, List.of(),
                addOrder, parentMap, treeEdges, finalMsg, bestCost, totalWeight,
                completeTelemetry));

        return Collections.unmodifiableList(frames);
    }

    // ── Helpers ─────────────────────────────────────────────

    private static void validateNonNegativeWeights(Graph graph) {
        for (Graph.Edge edge : graph.edges()) {
            if (edge.weight() < 0) {
                throw new IllegalArgumentException(
                        "Prim requires non-negative edge weights. Edge "
                                + edge.from() + " → " + edge.to()
                                + " has weight " + edge.weight());
            }
        }
    }

    private static AlgorithmFrame buildFrame(
            int stepIndex, String currentNode, Set<String> inMST,
            List<String> frontier, List<String> addOrder,
            Map<String, String> parentMap,
            Set<AlgorithmFrame.TraversalEdge> treeEdges,
            String statusMessage, Map<String, Double> bestCost,
            double totalWeight, AlgorithmTelemetry telemetry) {
        Map<String, Double> distances = new LinkedHashMap<>(bestCost);
        distances.put("__MST_TOTAL__", totalWeight);
        return new AlgorithmFrame(
                AlgorithmFrame.AlgorithmType.PRIM, stepIndex, currentNode,
                Set.copyOf(inMST), List.copyOf(frontier), List.copyOf(addOrder),
                Map.copyOf(parentMap), Set.copyOf(treeEdges), statusMessage, 0,
                Map.copyOf(distances), null, List.of(), telemetry);
    }

    private static List<String> frontierLabels(PriorityQueue<double[]> pq,
                                                List<String> nodes) {
        Set<String> seen = new LinkedHashSet<>();
        PriorityQueue<double[]> copy = new PriorityQueue<>(pq);
        while (!copy.isEmpty()) {
            seen.add(nodes.get((int) copy.poll()[1]));
        }
        return List.copyOf(seen);
    }

    private static List<String> frontierWithCosts(PriorityQueue<double[]> pq,
                                                   List<String> nodes,
                                                   Map<String, Double> bestCost) {
        List<String> result = new ArrayList<>();
        PriorityQueue<double[]> copy = new PriorityQueue<>(pq);
        Set<String> seen = new LinkedHashSet<>();
        while (!copy.isEmpty()) {
            String n = nodes.get((int) copy.poll()[1]);
            if (seen.add(n)) {
                result.add(n + " (key=" + DijkstraRunner.formatDist(bestCost.getOrDefault(n, DijkstraRunner.INF)) + ")");
            }
        }
        return result;
    }
}
