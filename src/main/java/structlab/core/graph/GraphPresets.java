package structlab.core.graph;

import java.util.*;

/**
 * Provides preset graph examples for the algorithm lab.
 * Each preset is designed to demonstrate specific algorithm behaviors.
 * Includes both unweighted (BFS/DFS) and weighted (Dijkstra) examples.
 */
public final class GraphPresets {

    private GraphPresets() {}

    /** A named graph preset with description. */
    public record Preset(String name, String description, Graph graph,
                         String suggestedSource, String suggestedTarget, boolean weighted) {

        /** Convenience constructor for unweighted presets (backward-compatible). */
        public Preset(String name, String description, Graph graph, String suggestedSource) {
            this(name, description, graph, suggestedSource, null, false);
        }
    }

    /** Returns all available presets. */
    public static List<Preset> all() {
        return List.of(
                simpleConnected(),
                binaryTree(),
                graphWithCycles(),
                sparseGraph(),
                directedAcyclic(),
                disconnected(),
                diamond(),
                grid2x3(),
                weightedSimple(),
                weightedCompeting(),
                weightedDijkstraVsBfs(),
                weightedTree(),
                weightedDisconnected(),
                weightedDirected(),
                negativeWeightSimple(),
                negativeWeightCycle(),
                dagClassic(),
                dagDiamond()
        );
    }

    /** Returns only unweighted presets. */
    public static List<Preset> unweighted() {
        return all().stream().filter(p -> !p.weighted()).toList();
    }

    /** Returns only weighted presets. */
    public static List<Preset> weighted() {
        return all().stream().filter(Preset::weighted).toList();
    }

    /** Simple connected undirected graph — good baseline demo. */
    public static Preset simpleConnected() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("A", "C");
        g.addEdge("B", "D");
        g.addEdge("B", "E");
        g.addEdge("C", "F");
        g.addEdge("D", "E");
        g.addEdge("E", "F");
        return new Preset("Simple Connected",
                "6 nodes, 7 edges — undirected, good for basic BFS/DFS comparison",
                g, "A");
    }

    /** Binary tree structure — shows BFS layer-by-layer vs DFS depth-first behavior. */
    public static Preset binaryTree() {
        Graph g = new Graph(false);
        g.addEdge("1", "2");
        g.addEdge("1", "3");
        g.addEdge("2", "4");
        g.addEdge("2", "5");
        g.addEdge("3", "6");
        g.addEdge("3", "7");
        return new Preset("Binary Tree",
                "7 nodes, tree structure — highlights BFS breadth vs DFS depth",
                g, "1");
    }

    /** Graph with cycles — shows visited-node skipping. */
    public static Preset graphWithCycles() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("A", "C");
        g.addEdge("B", "C");
        g.addEdge("B", "D");
        g.addEdge("C", "D");
        g.addEdge("C", "E");
        g.addEdge("D", "E");
        g.addEdge("D", "F");
        g.addEdge("E", "F");
        return new Preset("Graph with Cycles",
                "6 nodes, 9 edges — dense with multiple cycles",
                g, "A");
    }

    /** Sparse graph — long paths, few branches. */
    public static Preset sparseGraph() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("B", "C");
        g.addEdge("C", "D");
        g.addEdge("D", "E");
        g.addEdge("A", "F");
        g.addEdge("F", "G");
        return new Preset("Sparse / Linear",
                "7 nodes, 6 edges — two long branches from A",
                g, "A");
    }

    /** Directed acyclic graph — topological structure. */
    public static Preset directedAcyclic() {
        Graph g = new Graph(true);
        g.addEdge("S", "A");
        g.addEdge("S", "B");
        g.addEdge("A", "C");
        g.addEdge("A", "D");
        g.addEdge("B", "D");
        g.addEdge("B", "E");
        g.addEdge("C", "F");
        g.addEdge("D", "F");
        g.addEdge("E", "F");
        return new Preset("Directed Acyclic (DAG)",
                "7 nodes, 9 directed edges — shows directed traversal behavior",
                g, "S");
    }

    /** Disconnected graph — some nodes unreachable. */
    public static Preset disconnected() {
        Graph g = new Graph(false);
        // Component 1
        g.addEdge("A", "B");
        g.addEdge("B", "C");
        g.addEdge("A", "C");
        // Component 2 — isolated
        g.addEdge("X", "Y");
        g.addEdge("Y", "Z");
        return new Preset("Disconnected",
                "6 nodes, 2 components — shows partial reachability",
                g, "A");
    }

    /** Diamond/converging graph — multiple paths to same node. */
    public static Preset diamond() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("A", "C");
        g.addEdge("B", "D");
        g.addEdge("C", "D");
        g.addEdge("D", "E");
        return new Preset("Diamond",
                "5 nodes — converging paths from A to D to E",
                g, "A");
    }

    /** 2×3 grid graph — regular structure showing systematic traversal. */
    public static Preset grid2x3() {
        Graph g = new Graph(false);
        // Row 1: A-B-C, Row 2: D-E-F
        g.addEdge("A", "B");
        g.addEdge("B", "C");
        g.addEdge("D", "E");
        g.addEdge("E", "F");
        // Vertical connections
        g.addEdge("A", "D");
        g.addEdge("B", "E");
        g.addEdge("C", "F");
        return new Preset("2×3 Grid",
                "6 nodes, grid structure — shows systematic layer expansion",
                g, "A");
    }

    // ── Weighted presets ────────────────────────────────────

    /**
     * Simple weighted graph — good baseline for Dijkstra.
     * Direct A→D path (weight 10) is longer than A→B→C→D (weight 6).
     */
    public static Preset weightedSimple() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 2);
        g.addEdge("B", "C", 1);
        g.addEdge("C", "D", 3);
        g.addEdge("A", "D", 10);
        g.addEdge("B", "D", 5);
        return new Preset("Weighted Simple",
                "4 nodes — direct path A→D costs 10, but A→B→C→D costs only 6",
                g, "A", "D", true);
    }

    /**
     * Weighted graph with multiple competing routes.
     * Several paths from S to T with different total costs.
     */
    public static Preset weightedCompeting() {
        Graph g = new Graph(false);
        g.addEdge("S", "A", 4);
        g.addEdge("S", "B", 2);
        g.addEdge("A", "C", 5);
        g.addEdge("B", "A", 1);
        g.addEdge("B", "C", 8);
        g.addEdge("B", "D", 10);
        g.addEdge("C", "D", 2);
        g.addEdge("C", "T", 6);
        g.addEdge("D", "T", 3);
        return new Preset("Weighted Competing Routes",
                "6 nodes — multiple S→T paths with different costs; shows relaxation",
                g, "S", "T", true);
    }

    /**
     * Weighted graph where BFS shortest path (fewest hops) differs from
     * Dijkstra shortest path (lowest cost).
     * BFS from A would reach D in 1 hop via A→D, but Dijkstra finds A→B→C→D (cost 6 vs 15).
     */
    public static Preset weightedDijkstraVsBfs() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 2);
        g.addEdge("C", "D", 3);
        g.addEdge("A", "D", 15);
        g.addEdge("A", "E", 4);
        g.addEdge("E", "D", 1);
        return new Preset("Dijkstra vs BFS",
                "5 nodes — BFS reaches D in 1 hop (cost 15), Dijkstra finds cheaper A→E→D (cost 5)",
                g, "A", "D", true);
    }

    /**
     * Weighted tree — no cycles, unique path between every pair.
     * Good for showing that Dijkstra works on trees without relaxation.
     */
    public static Preset weightedTree() {
        Graph g = new Graph(false);
        g.addEdge("R", "A", 3);
        g.addEdge("R", "B", 7);
        g.addEdge("A", "C", 2);
        g.addEdge("A", "D", 4);
        g.addEdge("B", "E", 1);
        g.addEdge("B", "F", 5);
        return new Preset("Weighted Tree",
                "7 nodes, tree — unique paths, no relaxation needed",
                g, "R", "F", true);
    }

    /**
     * Weighted graph with disconnected component — target may be unreachable.
     */
    public static Preset weightedDisconnected() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 3);
        g.addEdge("B", "C", 2);
        g.addEdge("A", "C", 8);
        // Isolated component
        g.addEdge("X", "Y", 4);
        return new Preset("Weighted Disconnected",
                "5 nodes, 2 components — target Y unreachable from A",
                g, "A", "Y", true);
    }

    /**
     * Directed weighted graph — shows asymmetric shortest paths.
     */
    public static Preset weightedDirected() {
        Graph g = new Graph(true);
        g.addEdge("S", "A", 3);
        g.addEdge("S", "B", 5);
        g.addEdge("A", "B", 1);
        g.addEdge("A", "C", 7);
        g.addEdge("B", "C", 2);
        g.addEdge("B", "D", 6);
        g.addEdge("C", "D", 1);
        g.addEdge("C", "T", 4);
        g.addEdge("D", "T", 2);
        return new Preset("Directed Weighted",
                "6 nodes, 9 directed edges — shows directed shortest-path behavior",
                g, "S", "T", true);
    }

    // ── Negative-weight presets (Bellman-Ford) ──────────────

    /**
     * Directed graph with negative edges — Dijkstra fails, Bellman-Ford correct.
     * S→A→C costs 5, but S→B→C costs 3 using negative edge B→C(-2).
     */
    public static Preset negativeWeightSimple() {
        Graph g = new Graph(true);
        g.addEdge("S", "A", 4);
        g.addEdge("S", "B", 5);
        g.addEdge("A", "C", 1);
        g.addEdge("B", "C", -2);
        g.addEdge("B", "D", 3);
        g.addEdge("C", "D", 2);
        return new Preset("Negative Weights",
                "4 nodes, directed — has negative edge B→C(-2); Dijkstra invalid, use Bellman-Ford",
                g, "S", "D", true);
    }

    /**
     * Directed graph with a negative-weight cycle — shortest paths undefined.
     * Cycle: B→C→B with total weight -1.
     */
    public static Preset negativeWeightCycle() {
        Graph g = new Graph(true);
        g.addEdge("S", "A", 1);
        g.addEdge("A", "B", 2);
        g.addEdge("B", "C", -3);
        g.addEdge("C", "B", 1);
        g.addEdge("C", "D", 4);
        return new Preset("Negative Cycle",
                "5 nodes, directed — cycle B→C→B has weight -2; Bellman-Ford detects it",
                g, "S", "D", true);
    }

    // ── DAG presets (Topological Sort) ──────────────────────

    /**
     * Classic DAG for demonstrating topological sort.
     * Multiple valid orderings exist.
     */
    public static Preset dagClassic() {
        Graph g = new Graph(true);
        g.addEdge("A", "C");
        g.addEdge("A", "D");
        g.addEdge("B", "D");
        g.addEdge("B", "E");
        g.addEdge("C", "F");
        g.addEdge("D", "F");
        g.addEdge("E", "F");
        return new Preset("DAG Classic",
                "6 nodes, directed acyclic — good for topological sort demo",
                g, "A");
    }

    /**
     * DAG with diamond-shaped dependency chains — shows convergent dependencies.
     */
    public static Preset dagDiamond() {
        Graph g = new Graph(true);
        g.addEdge("S", "A");
        g.addEdge("S", "B");
        g.addEdge("A", "C");
        g.addEdge("B", "C");
        g.addEdge("A", "D");
        g.addEdge("C", "E");
        g.addEdge("D", "E");
        return new Preset("DAG Diamond",
                "6 nodes — convergent dependencies, multiple valid topo orderings",
                g, "S");
    }
}
