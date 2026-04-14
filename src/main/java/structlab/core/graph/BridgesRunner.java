package structlab.core.graph;

import java.util.*;

/**
 * Bridge detection using Tarjan's algorithm, producing step-by-step
 * {@link AlgorithmFrame}s for playback.
 *
 * <p>A bridge is an edge whose removal disconnects the graph.
 * Uses DFS with discovery/low-link values to identify bridges.</p>
 */
public final class BridgesRunner {

    private BridgesRunner() {}

    /**
     * Finds all bridges in an undirected graph.
     *
     * @param graph undirected graph
     * @return unmodifiable list of frames
     * @throws IllegalArgumentException if graph is directed
     */
    public static List<AlgorithmFrame> run(Graph graph) {
        if (graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Bridge detection requires an undirected graph.");
        }

        List<AlgorithmFrame> frames = new ArrayList<>();
        List<String> nodes = graph.nodes();
        int step = 0;

        Map<String, Integer> disc = new LinkedHashMap<>();
        Map<String, Integer> low = new LinkedHashMap<>();
        Map<String, String> parentMap = new LinkedHashMap<>();
        Set<String> visited = new LinkedHashSet<>();
        Set<AlgorithmFrame.TraversalEdge> treeEdges = new LinkedHashSet<>();
        Set<AlgorithmFrame.TraversalEdge> bridges = new LinkedHashSet<>();
        List<String> discoveryOrder = new ArrayList<>();
        int[] timer = {0};
        int totalNodes = nodes.size();

        AlgorithmTelemetry initTelemetry = TelemetryBuilder.create("Initialization")
                .metric("Nodes", totalNodes)
                .metric("Bridges Found", 0)
                .event("Starting bridge detection via DFS")
                .build();

        frames.add(buildFrame(step++, null, visited, List.of(), discoveryOrder,
                parentMap, treeEdges,
                "Bridge detection — DFS with discovery/low-link values",
                buildDistances(disc, low), bridges, initTelemetry));

        for (String node : nodes) {
            if (!visited.contains(node)) {
                step = dfs(graph, node, visited, disc, low, parentMap,
                        treeEdges, bridges, discoveryOrder, frames, step, timer);
            }
        }

        String finalMsg = bridges.isEmpty()
                ? "Bridge detection complete — no bridges found (graph is 2-edge-connected)"
                : "Bridge detection complete — " + bridges.size() + " bridge"
                + (bridges.size() != 1 ? "s" : "") + " found";

        List<String> bridgeLabels = new ArrayList<>();
        for (AlgorithmFrame.TraversalEdge b : bridges) {
            bridgeLabels.add(b.from() + "—" + b.to());
        }

        AlgorithmTelemetry completeTelemetry = TelemetryBuilder.create("Complete")
                .metric("Bridges", bridges.size())
                .metric("Visited", visited.size() + "/" + totalNodes)
                .section("Bridges Found", bridgeLabels)
                .event("Bridge detection complete")
                .build();

        frames.add(buildFrame(step, null, visited, List.of(), discoveryOrder,
                parentMap, treeEdges, finalMsg,
                buildDistances(disc, low), bridges, completeTelemetry));

        return Collections.unmodifiableList(frames);
    }

    /** Returns the set of bridge edges from the final frame. */
    public static Set<AlgorithmFrame.TraversalEdge> extractBridges(List<AlgorithmFrame> frames) {
        if (frames.isEmpty()) return Set.of();
        AlgorithmFrame last = frames.get(frames.size() - 1);
        // Bridges are stored in shortestPath as "from\0to" pairs
        Set<AlgorithmFrame.TraversalEdge> result = new LinkedHashSet<>();
        List<String> sp = last.shortestPath();
        for (int i = 0; i + 1 < sp.size(); i += 2) {
            result.add(new AlgorithmFrame.TraversalEdge(sp.get(i), sp.get(i + 1)));
        }
        return result;
    }

    // ── DFS ─────────────────────────────────────────────────

    private static int dfs(Graph graph, String start,
                            Set<String> visited,
                            Map<String, Integer> disc,
                            Map<String, Integer> low,
                            Map<String, String> parentMap,
                            Set<AlgorithmFrame.TraversalEdge> treeEdges,
                            Set<AlgorithmFrame.TraversalEdge> bridges,
                            List<String> discoveryOrder,
                            List<AlgorithmFrame> frames, int step,
                            int[] timer) {
        // Iterative DFS using explicit stack
        Deque<String[]> stack = new ArrayDeque<>();
        // entry: [node, parentNode, neighborIndexStr]
        stack.push(new String[]{start, null, "0"});
        disc.put(start, timer[0]);
        low.put(start, timer[0]);
        timer[0]++;
        visited.add(start);
        discoveryOrder.add(start);

        frames.add(buildFrame(step++, start, visited, List.of(), discoveryOrder,
                parentMap, treeEdges,
                "Visiting " + start + " — disc=" + disc.get(start),
                buildDistances(disc, low), bridges,
                TelemetryBuilder.create("Visit")
                        .metric("Node", start)
                        .metric("disc", disc.get(start))
                        .metric("low", low.get(start))
                        .event("Visiting " + start)
                        .build()));

        while (!stack.isEmpty()) {
            String[] top = stack.peek();
            String u = top[0];
            String parent = top[1];
            int neighborIdx = Integer.parseInt(top[2]);
            List<String> neighbors = graph.neighbors(u);

            if (neighborIdx < neighbors.size()) {
                top[2] = String.valueOf(neighborIdx + 1);
                String v = neighbors.get(neighborIdx);

                if (!visited.contains(v)) {
                    parentMap.put(v, u);
                    disc.put(v, timer[0]);
                    low.put(v, timer[0]);
                    timer[0]++;
                    visited.add(v);
                    discoveryOrder.add(v);
                    treeEdges.add(new AlgorithmFrame.TraversalEdge(u, v));

                    frames.add(buildFrame(step++, v, visited, List.of(u),
                            discoveryOrder, parentMap, treeEdges,
                            "Visiting " + v + " — disc=" + disc.get(v)
                                    + ", low=" + low.get(v),
                            buildDistances(disc, low), bridges,
                            TelemetryBuilder.create("Visit")
                                    .metric("Node", v)
                                    .metric("disc", disc.get(v))
                                    .metric("low", low.get(v))
                                    .metric("Parent", u)
                                    .event("Visiting " + v + " from " + u)
                                    .build()));

                    stack.push(new String[]{v, u, "0"});
                } else if (!v.equals(parent)) {
                    // Back edge — update low
                    int oldLow = low.get(u);
                    low.put(u, Math.min(oldLow, disc.get(v)));

                    if (low.get(u) < oldLow) {
                        frames.add(buildFrame(step++, u, visited, List.of(v),
                                discoveryOrder, parentMap, treeEdges,
                                "Back edge " + u + "-" + v
                                        + " — updated low[" + u + "]="
                                        + low.get(u),
                                buildDistances(disc, low), bridges,
                                TelemetryBuilder.create("Back-Edge")
                                        .metric("Edge", u + "—" + v)
                                        .metric("low[" + u + "]", low.get(u))
                                        .event("Back edge updated low[" + u + "]")
                                        .build()));
                    }
                }
            } else {
                // Done with all neighbors — backtrack
                stack.pop();
                if (parent != null) {
                    int oldLow = low.get(parent);
                    low.put(parent, Math.min(oldLow, low.get(u)));

                    // Check bridge condition: low[u] > disc[parent]
                    if (low.get(u) > disc.get(parent)) {
                        bridges.add(new AlgorithmFrame.TraversalEdge(parent, u));
                        frames.add(buildFrame(step++, u, visited, List.of(parent),
                                discoveryOrder, parentMap, treeEdges,
                                "Bridge found: " + parent + " — " + u
                                        + " (low[" + u + "]=" + low.get(u)
                                        + " > disc[" + parent + "]="
                                        + disc.get(parent) + ")",
                                buildDistances(disc, low), bridges,
                                TelemetryBuilder.create("Bridge-Found")
                                        .metric("Bridge", parent + "—" + u)
                                        .metric("low[" + u + "]", low.get(u))
                                        .metric("disc[" + parent + "]", disc.get(parent))
                                        .metric("Total Bridges", bridges.size())
                                        .event("Bridge " + parent + "—" + u + " found")
                                        .build()));
                    } else if (low.get(parent) < oldLow) {
                        frames.add(buildFrame(step++, parent, visited, List.of(u),
                                discoveryOrder, parentMap, treeEdges,
                                "Backtrack — updated low[" + parent + "]="
                                        + low.get(parent),
                                buildDistances(disc, low), bridges,
                                TelemetryBuilder.create("Backtrack")
                                        .metric("Node", parent)
                                        .metric("low[" + parent + "]", low.get(parent))
                                        .event("Backtrack updated low[" + parent + "]")
                                        .build()));
                    }
                }
            }
        }
        return step;
    }

    // ── Helpers ─────────────────────────────────────────────

    private static Map<String, Double> buildDistances(Map<String, Integer> disc,
                                                       Map<String, Integer> low) {
        // Encode disc and low in distances: node → disc * 1000 + low
        Map<String, Double> distances = new LinkedHashMap<>();
        for (String node : disc.keySet()) {
            int d = disc.get(node);
            int l = low.getOrDefault(node, d);
            distances.put(node, d * 1000.0 + l);
        }
        return distances;
    }

    private static AlgorithmFrame buildFrame(
            int stepIndex, String currentNode, Set<String> visited,
            List<String> frontier, List<String> discoveryOrder,
            Map<String, String> parentMap,
            Set<AlgorithmFrame.TraversalEdge> treeEdges,
            String statusMessage, Map<String, Double> distances,
            Set<AlgorithmFrame.TraversalEdge> bridges,
            AlgorithmTelemetry telemetry) {
        List<String> bridgeList = new ArrayList<>();
        for (AlgorithmFrame.TraversalEdge b : bridges) {
            bridgeList.add(b.from());
            bridgeList.add(b.to());
        }
        return new AlgorithmFrame(
                AlgorithmFrame.AlgorithmType.BRIDGES, stepIndex, currentNode,
                Set.copyOf(visited), List.copyOf(frontier),
                List.copyOf(discoveryOrder),
                Map.copyOf(parentMap), Set.copyOf(treeEdges),
                statusMessage, 0,
                Map.copyOf(distances), null, List.copyOf(bridgeList), telemetry);
    }
}
