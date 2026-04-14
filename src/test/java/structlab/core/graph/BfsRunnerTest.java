package structlab.core.graph;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BfsRunnerTest {

    @Test
    void bfsSimpleLinear() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("B", "C");

        List<AlgorithmFrame> frames = BfsRunner.run(g, "A");

        assertFalse(frames.isEmpty());

        // First frame — source enqueued
        AlgorithmFrame first = frames.get(0);
        assertEquals(AlgorithmFrame.AlgorithmType.BFS, first.algorithm());
        assertTrue(first.visited().contains("A"));
        assertEquals("A", first.discoveryOrder().get(0));

        // Last frame — traversal complete
        AlgorithmFrame last = frames.get(frames.size() - 1);
        assertTrue(last.statusMessage().contains("complete"));
        assertEquals(3, last.visited().size());
        assertTrue(last.frontier().isEmpty());
    }

    @Test
    void bfsVisitsAllReachableNodes() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("A", "C");
        g.addEdge("B", "D");

        List<AlgorithmFrame> frames = BfsRunner.run(g, "A");
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertEquals(Set.of("A", "B", "C", "D"), last.visited());
        assertEquals(4, last.discoveryOrder().size());
    }

    @Test
    void bfsLayerOrder() {
        // Tree: 1 -> 2, 3; 2 -> 4, 5
        Graph g = new Graph(false);
        g.addEdge("1", "2");
        g.addEdge("1", "3");
        g.addEdge("2", "4");
        g.addEdge("2", "5");

        List<AlgorithmFrame> frames = BfsRunner.run(g, "1");
        AlgorithmFrame last = frames.get(frames.size() - 1);

        // BFS should discover layer by layer: 1, then 2,3, then 4,5
        List<String> order = last.discoveryOrder();
        assertEquals("1", order.get(0));
        assertTrue(order.indexOf("2") < order.indexOf("4"));
        assertTrue(order.indexOf("3") < order.indexOf("5"));
    }

    @Test
    void bfsDisconnectedGraphPartialReach() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addNode("X");

        List<AlgorithmFrame> frames = BfsRunner.run(g, "A");
        AlgorithmFrame last = frames.get(frames.size() - 1);

        // X is unreachable
        assertEquals(2, last.visited().size());
        assertFalse(last.visited().contains("X"));
        assertTrue(last.statusMessage().contains("2 of 3"));
    }

    @Test
    void bfsTraversalTree() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("A", "C");
        g.addEdge("B", "C");

        List<AlgorithmFrame> frames = BfsRunner.run(g, "A");
        AlgorithmFrame last = frames.get(frames.size() - 1);

        // B and C are discovered from A
        assertEquals("A", last.parentMap().get("B"));
        assertEquals("A", last.parentMap().get("C"));
        assertFalse(last.parentMap().containsKey("A")); // source has no parent
    }

    @Test
    void bfsSourceNotFoundThrows() {
        Graph g = new Graph(false);
        g.addNode("A");

        assertThrows(IllegalArgumentException.class, () -> BfsRunner.run(g, "Z"));
    }

    @Test
    void bfsSingleNode() {
        Graph g = new Graph(false);
        g.addNode("X");

        List<AlgorithmFrame> frames = BfsRunner.run(g, "X");
        assertFalse(frames.isEmpty());
        AlgorithmFrame last = frames.get(frames.size() - 1);
        assertEquals(Set.of("X"), last.visited());
        assertTrue(last.statusMessage().contains("complete"));
    }

    @Test
    void bfsWithCycles() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("B", "C");
        g.addEdge("C", "A");

        List<AlgorithmFrame> frames = BfsRunner.run(g, "A");
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertEquals(3, last.visited().size());
        // No infinite loop — frames are finite
        assertTrue(frames.size() < 20);
    }

    @Test
    void bfsDirected() {
        Graph g = new Graph(true);
        g.addEdge("A", "B");
        g.addEdge("B", "C");
        // No edge C→A, so starting from C only reaches C
        List<AlgorithmFrame> frames = BfsRunner.run(g, "C");
        AlgorithmFrame last = frames.get(frames.size() - 1);
        assertEquals(1, last.visited().size());
    }

    @Test
    void bfsFramesAreImmutable() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");

        List<AlgorithmFrame> frames = BfsRunner.run(g, "A");
        // Should not be able to modify returned list
        assertThrows(UnsupportedOperationException.class, () -> frames.add(null));
    }
}
