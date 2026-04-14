package structlab.core.graph;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * A saveable/loadable graph scenario containing the graph, layout, and algorithm config.
 * Uses a simple line-based text format (no external JSON library required).
 *
 * <p>Format:
 * <pre>
 * #DATADANCE_SCENARIO v1
 * name=My Graph
 * directed=true
 * algorithm=Dijkstra
 * source=A
 * target=B
 * #NODES
 * A 100.0 200.0
 * B 300.0 200.0
 * #EDGES
 * A B 3.0
 * C D 1.0
 * #END
 * </pre>
 */
public final class GraphScenario {

    private final String name;
    private final Graph graph;
    private final boolean weighted;
    private final Map<String, double[]> nodePositions;
    private final String algorithm;
    private final String source;
    private final String target;

    public GraphScenario(String name, Graph graph, boolean weighted,
                          Map<String, double[]> nodePositions,
                          String algorithm, String source, String target) {
        this.name = name != null ? name : "Untitled";
        this.graph = graph;
        this.weighted = weighted;
        this.nodePositions = nodePositions != null
                ? new LinkedHashMap<>(nodePositions) : new LinkedHashMap<>();
        this.algorithm = algorithm;
        this.source = source;
        this.target = target;
    }

    public String name() { return name; }
    public Graph graph() { return graph; }
    public boolean weighted() { return weighted; }
    public Map<String, double[]> nodePositions() { return Collections.unmodifiableMap(nodePositions); }
    public String algorithm() { return algorithm; }
    public String source() { return source; }
    public String target() { return target; }

    // ── Serialize ───────────────────────────────────────────

    /** Saves this scenario to a file. */
    public void saveTo(Path path) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            w.write("#DATADANCE_SCENARIO v1");
            w.newLine();
            w.write("name=" + name);
            w.newLine();
            w.write("directed=" + graph.isDirected());
            w.newLine();
            w.write("weighted=" + weighted);
            w.newLine();
            if (algorithm != null) {
                w.write("algorithm=" + algorithm);
                w.newLine();
            }
            if (source != null) {
                w.write("source=" + source);
                w.newLine();
            }
            if (target != null) {
                w.write("target=" + target);
                w.newLine();
            }

            w.write("#NODES");
            w.newLine();
            for (String node : graph.nodes()) {
                double[] pos = nodePositions.get(node);
                if (pos != null) {
                    w.write(node + " " + pos[0] + " " + pos[1]);
                } else {
                    w.write(node + " 0.0 0.0");
                }
                w.newLine();
            }

            w.write("#EDGES");
            w.newLine();
            for (Graph.Edge edge : graph.edges()) {
                w.write(edge.from() + " " + edge.to() + " " + edge.weight());
                w.newLine();
            }

            w.write("#END");
            w.newLine();
        }
    }

    // ── Deserialize ─────────────────────────────────────────

    /** Loads a scenario from a file. */
    public static GraphScenario loadFrom(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        if (lines.isEmpty() || !lines.get(0).startsWith("#DATADANCE_SCENARIO")) {
            throw new IOException("Invalid scenario file — missing header.");
        }

        String name = "Untitled";
        boolean directed = false;
        boolean weighted = false;
        String algorithm = null;
        String source = null;
        String target = null;
        Map<String, double[]> positions = new LinkedHashMap<>();
        List<String[]> edgeData = new ArrayList<>();

        String section = "header";
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;

            if ("#NODES".equals(line)) { section = "nodes"; continue; }
            if ("#EDGES".equals(line)) { section = "edges"; continue; }
            if ("#END".equals(line)) break;

            if ("header".equals(section)) {
                int eq = line.indexOf('=');
                if (eq < 0) continue;
                String key = line.substring(0, eq).trim();
                String val = line.substring(eq + 1).trim();
                switch (key) {
                    case "name": name = val; break;
                    case "directed": directed = Boolean.parseBoolean(val); break;
                    case "weighted": weighted = Boolean.parseBoolean(val); break;
                    case "algorithm": algorithm = val; break;
                    case "source": source = val; break;
                    case "target": target = val; break;
                    default: break;
                }
            } else if ("nodes".equals(section)) {
                String[] parts = line.split("\\s+", 3);
                String nodeLabel = parts[0];
                double x = parts.length > 1 ? Double.parseDouble(parts[1]) : 0;
                double y = parts.length > 2 ? Double.parseDouble(parts[2]) : 0;
                positions.put(nodeLabel, new double[]{x, y});
            } else if ("edges".equals(section)) {
                String[] parts = line.split("\\s+", 3);
                if (parts.length >= 2) {
                    edgeData.add(parts);
                }
            }
        }

        Graph graph = new Graph(directed);
        for (String node : positions.keySet()) {
            graph.addNode(node);
        }
        for (String[] ed : edgeData) {
            double w = ed.length >= 3 ? Double.parseDouble(ed[2]) : 1.0;
            graph.addEdge(ed[0], ed[1], w);
        }

        return new GraphScenario(name, graph, weighted, positions,
                algorithm, source, target);
    }
}
