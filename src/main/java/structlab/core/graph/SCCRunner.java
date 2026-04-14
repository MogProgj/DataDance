package structlab.core.graph;

import java.util.*;

/**
 * Strongly Connected Components (Kosaraju's algorithm) producing step-by-step
 * {@link AlgorithmFrame}s for playback.
 *
 * <p>Two-pass DFS: first pass records finish order on original graph,
 * second pass on the transposed graph discovers SCCs in reverse finish order.</p>
 */
public final class SCCRunner {

    private SCCRunner() {}

    /**
     * Runs Kosaraju's SCC algorithm on a directed graph.
     *
     * @param graph directed graph
     * @return unmodifiable list of frames
     * @throws IllegalArgumentException if graph is undirected
     */
    public static List<AlgorithmFrame> run(Graph graph) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException(
                    "SCC (Kosaraju) requires a directed graph.");
        }

        List<AlgorithmFrame> frames = new ArrayList<>();
        List<String> nodes = graph.nodes();
        int step = 0;
        int totalNodes = nodes.size();

        AlgorithmTelemetry initTelemetry = TelemetryBuilder.create("Initialization")
                .metric("Nodes", totalNodes)
                .metric("Phase", "Pass 1 — DFS on original graph")
                .event("Starting Kosaraju Pass 1")
                .build();

        frames.add(buildFrame(step++, null, Set.of(), List.of(), List.of(),
                Map.of(), Set.of(),
                "Kosaraju SCC — Pass 1: DFS on original graph to get finish order",
                Map.of(), initTelemetry));

        // ── Pass 1: DFS on original graph, record finish order ──
        Set<String> visited = new LinkedHashSet<>();
        Deque<String> finishStack = new ArrayDeque<>();

        for (String node : nodes) {
            if (!visited.contains(node)) {
                step = dfsPass1(graph, node, visited, finishStack, frames, step);
            }
        }

        List<String> finishOrder = new ArrayList<>(finishStack);

        AlgorithmTelemetry p1DoneTelemetry = TelemetryBuilder.create("Pass1-Complete")
                .metric("Visited", visited.size() + "/" + totalNodes)
                .section("Finish Order", finishOrder)
                .event("Pass 1 complete")
                .build();

        frames.add(buildFrame(step++, null, visited, List.of(), finishOrder,
                Map.of(), Set.of(),
                "Pass 1 complete — finish order: " + String.join(", ", finishOrder),
                Map.of(), p1DoneTelemetry));

        // ── Build transposed graph ──
        Graph transposed = transpose(graph);

        AlgorithmTelemetry p2StartTelemetry = TelemetryBuilder.create("Pass2-Start")
                .metric("Phase", "Pass 2 — DFS on transposed graph")
                .event("Starting Kosaraju Pass 2")
                .build();

        frames.add(buildFrame(step++, null, Set.of(), List.of(), finishOrder,
                Map.of(), Set.of(),
                "Pass 2: DFS on transposed graph in reverse finish order",
                Map.of(), p2StartTelemetry));

        // ── Pass 2: DFS on transposed graph in reverse finish order ──
        Set<String> visited2 = new LinkedHashSet<>();
        Map<String, String> componentMap = new LinkedHashMap<>();
        Set<AlgorithmFrame.TraversalEdge> treeEdges = new LinkedHashSet<>();
        List<String> discoveryOrder = new ArrayList<>();
        int componentId = 0;

        while (!finishStack.isEmpty()) {
            String root = finishStack.pop();
            if (visited2.contains(root)) continue;

            componentId++;
            List<String> component = new ArrayList<>();
            step = dfsPass2(transposed, root, visited2, component,
                    treeEdges, frames, step, componentId, componentMap, discoveryOrder);

            Map<String, Double> compDist = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : componentMap.entrySet()) {
                compDist.put(entry.getKey(),
                        Double.parseDouble(entry.getValue()));
            }

            AlgorithmTelemetry sccTelemetry = TelemetryBuilder.create("SCC-Found")
                    .metric("SCC #", componentId)
                    .metric("Size", component.size())
                    .metric("Total SCCs", componentId)
                    .section("Component", component)
                    .event("SCC #" + componentId + " found: {" + String.join(", ", component) + "}")
                    .build();

            frames.add(buildFrame(step++, null, visited2, List.of(),
                    discoveryOrder, componentMap, treeEdges,
                    "SCC #" + componentId + " found: {"
                            + String.join(", ", component) + "}",
                    compDist, sccTelemetry));
        }

        Map<String, Double> finalDist = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : componentMap.entrySet()) {
            finalDist.put(entry.getKey(), Double.parseDouble(entry.getValue()));
        }

        AlgorithmTelemetry completeTelemetry = TelemetryBuilder.create("Complete")
                .metric("Total SCCs", componentId)
                .metric("Nodes", totalNodes)
                .event("Kosaraju complete")
                .build();

        frames.add(buildFrame(step, null, visited2, List.of(),
                discoveryOrder, componentMap, treeEdges,
                "Kosaraju complete — " + componentId + " SCC"
                        + (componentId != 1 ? "s" : "") + " found in "
                        + totalNodes + " nodes",
                finalDist, completeTelemetry));

        return Collections.unmodifiableList(frames);
    }

    // ── DFS helpers ─────────────────────────────────────────

    private static int dfsPass1(Graph graph, String node, Set<String> visited,
                                 Deque<String> finishStack,
                                 List<AlgorithmFrame> frames, int step) {
        Deque<String[]> stack = new ArrayDeque<>();
        // stack entry: [node, "enter"] or [node, "exit"]
        stack.push(new String[]{node, "enter"});

        while (!stack.isEmpty()) {
            String[] entry = stack.pop();
            String current = entry[0];
            boolean isEnter = "enter".equals(entry[1]);

            if (isEnter) {
                if (visited.contains(current)) continue;
                visited.add(current);

                frames.add(buildFrame(step++, current, visited,
                        stackLabels(stack), List.of(),
                        Map.of(), Set.of(),
                        "Pass 1 — visiting " + current,
                        Map.of(), TelemetryBuilder.create("Pass1-Visit")
                                .metric("Node", current)
                                .event("Visiting " + current + " (Pass 1)")
                                .build()));

                stack.push(new String[]{current, "exit"});
                List<String> neighbors = graph.neighbors(current);
                for (int i = neighbors.size() - 1; i >= 0; i--) {
                    String n = neighbors.get(i);
                    if (!visited.contains(n)) {
                        stack.push(new String[]{n, "enter"});
                    }
                }
            } else {
                finishStack.push(current);
            }
        }
        return step;
    }

    private static int dfsPass2(Graph transposed, String node,
                                 Set<String> visited, List<String> component,
                                 Set<AlgorithmFrame.TraversalEdge> treeEdges,
                                 List<AlgorithmFrame> frames, int step,
                                 int componentId,
                                 Map<String, String> componentMap,
                                 List<String> discoveryOrder) {
        Deque<String> stack = new ArrayDeque<>();
        stack.push(node);

        while (!stack.isEmpty()) {
            String current = stack.pop();
            if (visited.contains(current)) continue;
            visited.add(current);
            component.add(current);
            discoveryOrder.add(current);
            componentMap.put(current, String.valueOf(componentId));

            Map<String, Double> compDist = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : componentMap.entrySet()) {
                compDist.put(entry.getKey(), Double.parseDouble(entry.getValue()));
            }

            frames.add(buildFrame(step++, current, visited,
                    stackToList(stack), discoveryOrder,
                    componentMap, treeEdges,
                    "Pass 2 — " + current + " → SCC #" + componentId,
                    compDist, TelemetryBuilder.create("Pass2-Visit")
                            .metric("Node", current)
                            .metric("SCC #", componentId)
                            .event(current + " assigned to SCC #" + componentId)
                            .build()));

            List<String> neighbors = transposed.neighbors(current);
            for (int i = neighbors.size() - 1; i >= 0; i--) {
                String n = neighbors.get(i);
                if (!visited.contains(n)) {
                    stack.push(n);
                    treeEdges.add(new AlgorithmFrame.TraversalEdge(current, n));
                }
            }
        }
        return step;
    }

    private static Graph transpose(Graph graph) {
        Graph t = new Graph(true);
        for (String node : graph.nodes()) {
            t.addNode(node);
        }
        for (Graph.Edge edge : graph.edges()) {
            t.addEdge(edge.to(), edge.from(), edge.weight());
        }
        return t;
    }

    private static List<String> stackLabels(Deque<String[]> stack) {
        List<String> labels = new ArrayList<>();
        for (String[] entry : stack) {
            if ("enter".equals(entry[1]) && !labels.contains(entry[0])) {
                labels.add(entry[0]);
            }
        }
        return labels;
    }

    private static List<String> stackToList(Deque<String> stack) {
        return List.copyOf(stack);
    }

    // ── Frame builder ───────────────────────────────────────

    private static AlgorithmFrame buildFrame(
            int stepIndex, String currentNode, Set<String> visited,
            List<String> frontier, List<String> discoveryOrder,
            Map<String, String> componentMap,
            Set<AlgorithmFrame.TraversalEdge> treeEdges,
            String statusMessage, Map<String, Double> distances,
            AlgorithmTelemetry telemetry) {
        return new AlgorithmFrame(
                AlgorithmFrame.AlgorithmType.SCC, stepIndex, currentNode,
                Set.copyOf(visited), List.copyOf(frontier),
                List.copyOf(discoveryOrder),
                Map.copyOf(componentMap), Set.copyOf(treeEdges),
                statusMessage, 0,
                Map.copyOf(distances), null, List.of(), telemetry);
    }
}
