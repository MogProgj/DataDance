package structlab.core.graph;

import java.util.*;

/**
 * Topological sort using Kahn's algorithm, producing a list of {@link AlgorithmFrame}s
 * for step-by-step playback.
 *
 * <p>Requires a directed acyclic graph (DAG). Detects and reports cycles.</p>
 */
public final class TopologicalSortRunner {

    private TopologicalSortRunner() {}

    /**
     * Runs topological sort on the given directed graph and returns all frames.
     *
     * @param graph the directed graph to sort
     * @return unmodifiable list of frames, one per step
     * @throws IllegalArgumentException if graph is not directed
     */
    public static List<AlgorithmFrame> run(Graph graph) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Topological sort requires a directed graph");
        }

        List<AlgorithmFrame> frames = new ArrayList<>();
        List<String> nodes = graph.nodes();

        // Compute in-degrees
        Map<String, Integer> inDegree = new LinkedHashMap<>();
        for (String node : nodes) {
            inDegree.put(node, 0);
        }
        for (Graph.Edge edge : graph.edges()) {
            inDegree.merge(edge.to(), 1, Integer::sum);
        }

        // Find initial zero-indegree nodes (ready queue)
        Deque<String> readyQueue = new ArrayDeque<>();
        for (String node : nodes) {
            if (inDegree.get(node) == 0) {
                readyQueue.add(node);
            }
        }

        Set<String> processed = new LinkedHashSet<>();
        List<String> topoOrder = new ArrayList<>();
        Map<String, String> parentMap = new LinkedHashMap<>();
        Set<AlgorithmFrame.TraversalEdge> treeEdges = new LinkedHashSet<>();
        int step = 0;
        int totalNodes = nodes.size();

        // Initial frame
        AlgorithmTelemetry initTelemetry = TelemetryBuilder.create("Initialization")
                .metric("Nodes", totalNodes)
                .metric("Zero-Indegree", readyQueue.size())
                .section("Ready Queue", List.copyOf(readyQueue))
                .section("Indegrees", indegreeLabels(inDegree))
                .event(readyQueue.size() + " node(s) with indegree 0")
                .build();

        frames.add(buildFrame(step++, null, processed, List.copyOf(readyQueue),
                topoOrder, parentMap, treeEdges, inDegree,
                "Topological sort started — " + readyQueue.size()
                        + " node(s) with indegree 0: " + readyQueue,
                initTelemetry));

        while (!readyQueue.isEmpty()) {
            String current = readyQueue.poll();
            processed.add(current);
            topoOrder.add(current);

            AlgorithmTelemetry emitTelemetry = TelemetryBuilder.create("Emit")
                    .metric("Node", current)
                    .metric("Position", topoOrder.size() + "/" + totalNodes)
                    .metric("Remaining", (totalNodes - processed.size()) + " node(s)")
                    .section("Ready Queue", List.copyOf(readyQueue))
                    .section("Output", List.copyOf(topoOrder))
                    .event("Emitted " + current + " (position " + topoOrder.size() + ")")
                    .build();

            frames.add(buildFrame(step++, current, processed, List.copyOf(readyQueue),
                    topoOrder, parentMap, treeEdges, inDegree,
                    "Removed " + current + " (indegree 0) — position "
                            + topoOrder.size() + " in ordering",
                    emitTelemetry));

            // Reduce indegree of neighbors
            for (String neighbor : graph.neighbors(current)) {
                if (processed.contains(neighbor)) continue;
                int newDeg = inDegree.get(neighbor) - 1;
                inDegree.put(neighbor, newDeg);
                treeEdges.add(new AlgorithmFrame.TraversalEdge(current, neighbor));

                if (newDeg == 0) {
                    readyQueue.add(neighbor);
                    parentMap.put(neighbor, current);

                    AlgorithmTelemetry readyTelemetry = TelemetryBuilder.create("Indegree-Update")
                            .metric("Node", neighbor)
                            .metric("New Indegree", 0)
                            .metric("Status", "Ready")
                            .section("Ready Queue", List.copyOf(readyQueue))
                            .event(neighbor + " now has indegree 0 — added to ready queue")
                            .build();

                    frames.add(buildFrame(step++, current, processed, List.copyOf(readyQueue),
                            topoOrder, parentMap, treeEdges, inDegree,
                            "Node " + neighbor + " now has indegree 0 — ready",
                            readyTelemetry));
                } else {
                    AlgorithmTelemetry decTelemetry = TelemetryBuilder.create("Indegree-Update")
                            .metric("Node", neighbor)
                            .metric("New Indegree", newDeg)
                            .event("Reduced indegree of " + neighbor + " to " + newDeg)
                            .build();

                    frames.add(buildFrame(step++, current, processed, List.copyOf(readyQueue),
                            topoOrder, parentMap, treeEdges, inDegree,
                            "Reduced indegree of " + neighbor + " to " + newDeg,
                            decTelemetry));
                }
            }
        }

        // Check: did we process all nodes?
        if (topoOrder.size() < totalNodes) {
            int remaining = totalNodes - topoOrder.size();

            AlgorithmTelemetry cycleTelemetry = TelemetryBuilder.create("Cycle-Detected")
                    .metric("Ordered", topoOrder.size() + "/" + totalNodes)
                    .metric("Stuck", remaining + " node(s)")
                    .section("Partial Order", List.copyOf(topoOrder))
                    .event("Cycle detected — " + remaining + " node(s) could not be ordered")
                    .build();

            frames.add(buildFrame(step, null, processed, List.of(),
                    topoOrder, parentMap, treeEdges, inDegree,
                    "Cycle detected — " + remaining
                            + " node(s) could not be ordered. Topological sort impossible.",
                    cycleTelemetry));
        } else {
            AlgorithmTelemetry completeTelemetry = TelemetryBuilder.create("Complete")
                    .metric("Ordered", topoOrder.size() + "/" + totalNodes)
                    .section("Topological Order", List.copyOf(topoOrder))
                    .event("Topological sort complete")
                    .build();

            frames.add(buildFrame(step, null, processed, List.of(),
                    topoOrder, parentMap, treeEdges, inDegree,
                    "Topological sort complete — order: "
                            + String.join(" → ", topoOrder),
                    completeTelemetry));
        }

        return Collections.unmodifiableList(frames);
    }

    // ── Helpers ─────────────────────────────────────────────

    private static AlgorithmFrame buildFrame(
            int stepIndex, String currentNode, Set<String> processed,
            List<String> readyQueue, List<String> topoOrder,
            Map<String, String> parentMap, Set<AlgorithmFrame.TraversalEdge> treeEdges,
            Map<String, Integer> inDegree, String statusMessage,
            AlgorithmTelemetry telemetry) {
        Map<String, Double> inDegreeAsDouble = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            inDegreeAsDouble.put(entry.getKey(), (double) entry.getValue());
        }

        return new AlgorithmFrame(
                AlgorithmFrame.AlgorithmType.TOPOLOGICAL_SORT, stepIndex, currentNode,
                Set.copyOf(processed), List.copyOf(readyQueue), List.copyOf(topoOrder),
                Map.copyOf(parentMap), Set.copyOf(treeEdges), statusMessage, 0,
                Map.copyOf(inDegreeAsDouble), null, List.of(), telemetry);
    }

    private static List<String> indegreeLabels(Map<String, Integer> inDegree) {
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            result.add(entry.getKey() + " (indeg=" + entry.getValue() + ")");
        }
        return result;
    }
}
