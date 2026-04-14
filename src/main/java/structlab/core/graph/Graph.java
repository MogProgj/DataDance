package structlab.core.graph;

import java.util.*;

/**
 * Adjacency-list graph supporting directed/undirected, unweighted/weighted edges.
 * Nodes are identified by string labels for clear visualization.
 */
public class Graph {

    private final boolean directed;
    private final Map<String, Map<String, Double>> adjacency = new LinkedHashMap<>();

    public Graph(boolean directed) {
        this.directed = directed;
    }

    public boolean isDirected() {
        return directed;
    }

    /** Adds a node if not already present. */
    public void addNode(String label) {
        adjacency.putIfAbsent(label, new LinkedHashMap<>());
    }

    /** Adds an unweighted edge (weight = 1.0). */
    public void addEdge(String from, String to) {
        addEdge(from, to, 1.0);
    }

    /** Adds a weighted edge. Creates nodes if missing. */
    public void addEdge(String from, String to, double weight) {
        addNode(from);
        addNode(to);
        adjacency.get(from).put(to, weight);
        if (!directed) {
            adjacency.get(to).put(from, weight);
        }
    }

    /** Returns an unmodifiable view of all node labels in insertion order. */
    public List<String> nodes() {
        return List.copyOf(adjacency.keySet());
    }

    /** Returns the number of nodes. */
    public int nodeCount() {
        return adjacency.size();
    }

    /** Returns true if the node exists. */
    public boolean hasNode(String label) {
        return adjacency.containsKey(label);
    }

    /** Returns neighbors of a node in insertion order, or empty if node missing. */
    public List<String> neighbors(String node) {
        Map<String, Double> adj = adjacency.get(node);
        return adj == null ? List.of() : List.copyOf(adj.keySet());
    }

    /** Returns edge weight, or OptionalDouble.empty() if no edge exists. */
    public OptionalDouble edgeWeight(String from, String to) {
        Map<String, Double> adj = adjacency.get(from);
        if (adj == null) return OptionalDouble.empty();
        Double w = adj.get(to);
        return w == null ? OptionalDouble.empty() : OptionalDouble.of(w);
    }

    /** Returns true if an edge from→to exists. */
    public boolean hasEdge(String from, String to) {
        Map<String, Double> adj = adjacency.get(from);
        return adj != null && adj.containsKey(to);
    }

    /** Removes a node and all its incident edges. Returns true if the node existed. */
    public boolean removeNode(String label) {
        if (!adjacency.containsKey(label)) return false;
        adjacency.remove(label);
        // Remove all edges pointing to this node
        for (Map<String, Double> neighbors : adjacency.values()) {
            neighbors.remove(label);
        }
        return true;
    }

    /** Removes an edge. Returns true if the edge existed. */
    public boolean removeEdge(String from, String to) {
        Map<String, Double> adj = adjacency.get(from);
        if (adj == null || !adj.containsKey(to)) return false;
        adj.remove(to);
        if (!directed) {
            Map<String, Double> adjRev = adjacency.get(to);
            if (adjRev != null) adjRev.remove(from);
        }
        return true;
    }

    /** Creates a deep copy of this graph. */
    public Graph copy() {
        Graph g = new Graph(this.directed);
        for (Map.Entry<String, Map<String, Double>> entry : adjacency.entrySet()) {
            g.adjacency.put(entry.getKey(), new LinkedHashMap<>(entry.getValue()));
        }
        return g;
    }

    /** Returns all edges as (from, to, weight) triples. */
    public List<Edge> edges() {
        List<Edge> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (Map.Entry<String, Map<String, Double>> entry : adjacency.entrySet()) {
            String from = entry.getKey();
            for (Map.Entry<String, Double> neighbor : entry.getValue().entrySet()) {
                String to = neighbor.getKey();
                double w = neighbor.getValue();
                if (!directed) {
                    // avoid duplicates for undirected
                    String key = from.compareTo(to) < 0 ? from + "\0" + to : to + "\0" + from;
                    if (!seen.add(key)) continue;
                }
                result.add(new Edge(from, to, w));
            }
        }
        return result;
    }

    /** Returns total edge count. */
    public int edgeCount() {
        return edges().size();
    }

    /** Simple edge record. */
    public record Edge(String from, String to, double weight) {
        public boolean isWeighted() {
            return weight != 1.0;
        }
    }
}
