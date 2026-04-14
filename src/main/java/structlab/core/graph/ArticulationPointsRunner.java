package structlab.core.graph;

import java.util.*;

/**
 * Articulation point detection using Tarjan's algorithm, producing step-by-step
 * {@link AlgorithmFrame}s for playback.
 *
 * <p>An articulation point is a node whose removal disconnects the graph.
 * Uses DFS with discovery/low-link values.</p>
 */
public final class ArticulationPointsRunner {

    private ArticulationPointsRunner() {}

    /**
     * Finds all articulation points in an undirected graph.
     *
     * @param graph undirected graph
     * @return unmodifiable list of frames
     * @throws IllegalArgumentException if graph is directed
     */
    public static List<AlgorithmFrame> run(Graph graph) {
        if (graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Articulation point detection requires an undirected graph.");
        }

        List<AlgorithmFrame> frames = new ArrayList<>();
        List<String> nodes = graph.nodes();
        int step = 0;

        Map<String, Integer> disc = new LinkedHashMap<>();
        Map<String, Integer> low = new LinkedHashMap<>();
        Map<String, String> parentMap = new LinkedHashMap<>();
        Set<String> visited = new LinkedHashSet<>();
        Set<AlgorithmFrame.TraversalEdge> treeEdges = new LinkedHashSet<>();
        Set<String> articulationPoints = new LinkedHashSet<>();
        List<String> discoveryOrder = new ArrayList<>();
        int[] timer = {0};
        int totalNodes = nodes.size();

        AlgorithmTelemetry initTelemetry = TelemetryBuilder.create("Initialization")
                .metric("Nodes", totalNodes)
                .metric("APs Found", 0)
                .event("Starting articulation point detection via DFS")
                .build();

        frames.add(buildFrame(step++, null, visited, List.of(), discoveryOrder,
                parentMap, treeEdges,
                "Articulation point detection — DFS with discovery/low-link values",
                buildDistances(disc, low), articulationPoints, initTelemetry));

        for (String node : nodes) {
            if (!visited.contains(node)) {
                step = dfs(graph, node, visited, disc, low, parentMap,
                        treeEdges, articulationPoints, discoveryOrder,
                        frames, step, timer);
            }
        }

        String finalMsg = articulationPoints.isEmpty()
                ? "Detection complete — no articulation points (graph is biconnected)"
                : "Detection complete — " + articulationPoints.size()
                + " articulation point" + (articulationPoints.size() != 1 ? "s" : "")
                + ": {" + String.join(", ", articulationPoints) + "}";

        AlgorithmTelemetry completeTelemetry = TelemetryBuilder.create("Complete")
                .metric("APs Found", articulationPoints.size())
                .metric("Visited", visited.size() + "/" + totalNodes)
                .section("Articulation Points", List.copyOf(articulationPoints))
                .event("Detection complete")
                .build();

        frames.add(buildFrame(step, null, visited, List.of(), discoveryOrder,
                parentMap, treeEdges, finalMsg,
                buildDistances(disc, low), articulationPoints, completeTelemetry));

        return Collections.unmodifiableList(frames);
    }

    /** Extracts articulation points from the final frame's shortestPath field. */
    public static Set<String> extractArticulationPoints(List<AlgorithmFrame> frames) {
        if (frames.isEmpty()) return Set.of();
        return new LinkedHashSet<>(frames.get(frames.size() - 1).shortestPath());
    }

    // ── DFS ─────────────────────────────────────────────────

    private static int dfs(Graph graph, String start,
                            Set<String> visited,
                            Map<String, Integer> disc,
                            Map<String, Integer> low,
                            Map<String, String> parentMap,
                            Set<AlgorithmFrame.TraversalEdge> treeEdges,
                            Set<String> articulationPoints,
                            List<String> discoveryOrder,
                            List<AlgorithmFrame> frames, int step,
                            int[] timer) {
        Deque<String[]> stack = new ArrayDeque<>();
        Map<String, Integer> childCount = new LinkedHashMap<>();
        // entry: [node, parentNode, neighborIndexStr]
        stack.push(new String[]{start, null, "0"});
        disc.put(start, timer[0]);
        low.put(start, timer[0]);
        timer[0]++;
        visited.add(start);
        discoveryOrder.add(start);
        childCount.put(start, 0);

        frames.add(buildFrame(step++, start, visited, List.of(), discoveryOrder,
                parentMap, treeEdges,
                "Visiting " + start + " (root) — disc=" + disc.get(start),
                buildDistances(disc, low), articulationPoints,
                TelemetryBuilder.create("Visit")
                        .metric("Node", start + " (root)")
                        .metric("disc", disc.get(start))
                        .metric("low", low.get(start))
                        .event("Visiting root " + start)
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
                    childCount.put(u, childCount.getOrDefault(u, 0) + 1);
                    disc.put(v, timer[0]);
                    low.put(v, timer[0]);
                    timer[0]++;
                    visited.add(v);
                    discoveryOrder.add(v);
                    childCount.put(v, 0);
                    treeEdges.add(new AlgorithmFrame.TraversalEdge(u, v));

                    frames.add(buildFrame(step++, v, visited, List.of(u),
                            discoveryOrder, parentMap, treeEdges,
                            "Visiting " + v + " — disc=" + disc.get(v)
                                    + ", low=" + low.get(v),
                            buildDistances(disc, low), articulationPoints,
                            TelemetryBuilder.create("Visit")
                                    .metric("Node", v)
                                    .metric("disc", disc.get(v))
                                    .metric("low", low.get(v))
                                    .metric("Parent", u)
                                    .event("Visiting " + v + " from " + u)
                                    .build()));

                    stack.push(new String[]{v, u, "0"});
                } else if (!v.equals(parent)) {
                    int oldLow = low.get(u);
                    low.put(u, Math.min(oldLow, disc.get(v)));

                    if (low.get(u) < oldLow) {
                        frames.add(buildFrame(step++, u, visited, List.of(v),
                                discoveryOrder, parentMap, treeEdges,
                                "Back edge " + u + "-" + v
                                        + " — updated low[" + u + "]="
                                        + low.get(u),
                                buildDistances(disc, low), articulationPoints,
                                TelemetryBuilder.create("Back-Edge")
                                        .metric("Edge", u + "—" + v)
                                        .metric("low[" + u + "]", low.get(u))
                                        .event("Back edge updated low[" + u + "]")
                                        .build()));
                    }
                }
            } else {
                stack.pop();
                if (parent != null) {
                    int oldLow = low.get(parent);
                    low.put(parent, Math.min(oldLow, low.get(u)));

                    // Check articulation point conditions
                    boolean isRoot = !parentMap.containsKey(parent);
                    if (isRoot) {
                        // Root is AP if it has 2+ children in DFS tree
                        if (childCount.getOrDefault(parent, 0) >= 2
                                && !articulationPoints.contains(parent)) {
                            articulationPoints.add(parent);
                            frames.add(buildFrame(step++, parent, visited,
                                    List.of(u), discoveryOrder, parentMap, treeEdges,
                                    "Articulation point: " + parent
                                            + " (root with "
                                            + childCount.get(parent) + " DFS children)",
                                    buildDistances(disc, low), articulationPoints,
                                    TelemetryBuilder.create("AP-Found")
                                            .metric("Node", parent + " (root)")
                                            .metric("DFS Children", childCount.get(parent))
                                            .metric("Total APs", articulationPoints.size())
                                            .event("Root AP: " + parent + " with " + childCount.get(parent) + " children")
                                            .build()));
                        }
                    } else {
                        // Non-root is AP if low[u] >= disc[parent]
                        if (low.get(u) >= disc.get(parent)
                                && !articulationPoints.contains(parent)) {
                            articulationPoints.add(parent);
                            frames.add(buildFrame(step++, parent, visited,
                                    List.of(u), discoveryOrder, parentMap, treeEdges,
                                    "Articulation point: " + parent
                                            + " (low[" + u + "]=" + low.get(u)
                                            + " >= disc[" + parent + "]="
                                            + disc.get(parent) + ")",
                                    buildDistances(disc, low), articulationPoints,
                                    TelemetryBuilder.create("AP-Found")
                                            .metric("Node", parent)
                                            .metric("low[" + u + "]", low.get(u))
                                            .metric("disc[" + parent + "]", disc.get(parent))
                                            .metric("Total APs", articulationPoints.size())
                                            .event("AP: " + parent + " (low[" + u + "] >= disc[" + parent + "])")
                                            .build()));
                        }
                    }

                    if (low.get(parent) < oldLow) {
                        frames.add(buildFrame(step++, parent, visited,
                                List.of(u), discoveryOrder, parentMap, treeEdges,
                                "Backtrack — updated low[" + parent + "]="
                                        + low.get(parent),
                                buildDistances(disc, low), articulationPoints,
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
            Set<String> articulationPoints,
            AlgorithmTelemetry telemetry) {
        return new AlgorithmFrame(
                AlgorithmFrame.AlgorithmType.ARTICULATION_POINTS, stepIndex,
                currentNode, Set.copyOf(visited), List.copyOf(frontier),
                List.copyOf(discoveryOrder),
                Map.copyOf(parentMap), Set.copyOf(treeEdges),
                statusMessage, 0,
                Map.copyOf(distances), null, List.copyOf(articulationPoints), telemetry);
    }
}
