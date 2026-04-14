package structlab.core.graph;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.OptionalDouble;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    @Test
    void emptyGraph() {
        Graph g = new Graph(false);
        assertEquals(0, g.nodeCount());
        assertEquals(0, g.edgeCount());
        assertTrue(g.nodes().isEmpty());
        assertTrue(g.edges().isEmpty());
    }

    @Test
    void addNodesAndEdgesUndirected() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("A", "C");

        assertEquals(3, g.nodeCount());
        assertEquals(2, g.edgeCount());
        assertTrue(g.hasNode("A"));
        assertTrue(g.hasNode("B"));
        assertTrue(g.hasEdge("A", "B"));
        assertTrue(g.hasEdge("B", "A")); // undirected
        assertFalse(g.hasEdge("B", "C"));
    }

    @Test
    void addNodesAndEdgesDirected() {
        Graph g = new Graph(true);
        g.addEdge("A", "B");

        assertTrue(g.hasEdge("A", "B"));
        assertFalse(g.hasEdge("B", "A")); // directed
        assertEquals(1, g.edgeCount());
    }

    @Test
    void neighbors() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("A", "C");
        g.addEdge("B", "C");

        List<String> neighborsA = g.neighbors("A");
        assertEquals(2, neighborsA.size());
        assertTrue(neighborsA.contains("B"));
        assertTrue(neighborsA.contains("C"));
    }

    @Test
    void edgeWeight() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 5.0);

        OptionalDouble weight = g.edgeWeight("A", "B");
        assertTrue(weight.isPresent());
        assertEquals(5.0, weight.getAsDouble());

        assertTrue(g.edgeWeight("A", "Z").isEmpty());
    }

    @Test
    void defaultEdgeWeightIsOne() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");

        assertEquals(1.0, g.edgeWeight("A", "B").orElse(0));
    }

    @Test
    void addNodeIdempotent() {
        Graph g = new Graph(false);
        g.addNode("A");
        g.addNode("A");
        assertEquals(1, g.nodeCount());
    }

    @Test
    void isolatedNode() {
        Graph g = new Graph(false);
        g.addNode("X");
        assertTrue(g.hasNode("X"));
        assertTrue(g.neighbors("X").isEmpty());
        assertEquals(0, g.edgeCount());
    }

    @Test
    void missingNodeNeighbors() {
        Graph g = new Graph(false);
        assertTrue(g.neighbors("Z").isEmpty());
    }

    @Test
    void edgeRecordWeighted() {
        Graph.Edge e = new Graph.Edge("A", "B", 3.5);
        assertTrue(e.isWeighted());
    }

    @Test
    void edgeRecordUnweighted() {
        Graph.Edge e = new Graph.Edge("A", "B", 1.0);
        assertFalse(e.isWeighted());
    }

    @Test
    void undirectedEdgesNoDuplicates() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("B", "C");

        List<Graph.Edge> edges = g.edges();
        assertEquals(2, edges.size());
    }

    @Test
    void directedEdgesAllPresent() {
        Graph g = new Graph(true);
        g.addEdge("A", "B");
        g.addEdge("B", "A");

        assertEquals(2, g.edgeCount());
    }

    @Test
    void isDirected() {
        assertTrue(new Graph(true).isDirected());
        assertFalse(new Graph(false).isDirected());
    }

    @Test
    void removeNodeUndirected() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("A", "C");
        g.addEdge("B", "C");

        assertTrue(g.removeNode("B"));
        assertFalse(g.hasNode("B"));
        assertEquals(2, g.nodeCount());
        assertFalse(g.hasEdge("A", "B"));
        assertFalse(g.hasEdge("B", "A"));
        assertTrue(g.hasEdge("A", "C"));
    }

    @Test
    void removeNodeDirected() {
        Graph g = new Graph(true);
        g.addEdge("A", "B");
        g.addEdge("C", "B");

        assertTrue(g.removeNode("B"));
        assertEquals(2, g.nodeCount());
        assertFalse(g.hasEdge("A", "B"));
        assertFalse(g.hasEdge("C", "B"));
    }

    @Test
    void removeNodeNonExistent() {
        Graph g = new Graph(false);
        g.addNode("A");
        assertFalse(g.removeNode("Z"));
    }

    @Test
    void removeEdgeUndirected() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("A", "C");

        assertTrue(g.removeEdge("A", "B"));
        assertFalse(g.hasEdge("A", "B"));
        assertFalse(g.hasEdge("B", "A"));
        assertTrue(g.hasNode("A"));
        assertTrue(g.hasNode("B"));
        assertTrue(g.hasEdge("A", "C"));
    }

    @Test
    void removeEdgeDirected() {
        Graph g = new Graph(true);
        g.addEdge("A", "B");
        g.addEdge("B", "A");

        assertTrue(g.removeEdge("A", "B"));
        assertFalse(g.hasEdge("A", "B"));
        assertTrue(g.hasEdge("B", "A"));
    }

    @Test
    void removeEdgeNonExistent() {
        Graph g = new Graph(false);
        g.addNode("A");
        g.addNode("B");
        assertFalse(g.removeEdge("A", "B"));
    }

    @Test
    void copyPreservesStructure() {
        Graph g = new Graph(true);
        g.addEdge("A", "B", 3.0);
        g.addEdge("B", "C", 2.0);

        Graph copy = g.copy();
        assertEquals(g.nodeCount(), copy.nodeCount());
        assertEquals(g.edgeCount(), copy.edgeCount());
        assertTrue(copy.isDirected());
        assertEquals(3.0, copy.edgeWeight("A", "B").orElse(0));

        // Mutating copy doesn't affect original
        copy.removeNode("C");
        assertTrue(g.hasNode("C"));
        assertEquals(3, g.nodeCount());
    }

    @Test
    void copyUndirected() {
        Graph g = new Graph(false);
        g.addEdge("X", "Y", 5.0);

        Graph copy = g.copy();
        assertFalse(copy.isDirected());
        assertTrue(copy.hasEdge("X", "Y"));
        assertTrue(copy.hasEdge("Y", "X"));
    }
}
