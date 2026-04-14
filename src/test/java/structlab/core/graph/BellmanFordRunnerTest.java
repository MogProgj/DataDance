package structlab.core.graph;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BellmanFordRunnerTest {

    @Test
    void simpleShortestPath() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 2);
        g.addEdge("A", "C", 10);

        List<AlgorithmFrame> frames = BellmanFordRunner.run(g, "A", "C");
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertEquals(AlgorithmFrame.AlgorithmType.BELLMAN_FORD, last.algorithm());
        assertTrue(last.statusMessage().contains("complete"));
        assertEquals(List.of("A", "B", "C"), last.shortestPath());
        assertEquals(3.0, last.distances().get("C"), 0.001);
    }

    @Test
    void negativeWeightsHandled() {
        Graph g = new Graph(true);
        g.addEdge("S", "A", 1);
        g.addEdge("S", "B", 4);
        g.addEdge("A", "B", -2);

        List<AlgorithmFrame> frames = BellmanFordRunner.run(g, "S", "B");
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertTrue(last.statusMessage().contains("complete"));
        assertEquals(-1.0, last.distances().get("B"), 0.001);
        assertEquals(List.of("S", "A", "B"), last.shortestPath());
    }

    @Test
    void negativeCycleDetected() {
        Graph g = new Graph(true);
        g.addEdge("S", "A", 1);
        g.addEdge("A", "B", 2);
        g.addEdge("B", "C", -3);
        g.addEdge("C", "B", 1);
        g.addEdge("C", "D", 4);

        List<AlgorithmFrame> frames = BellmanFordRunner.run(g, "S", "D");
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertTrue(last.statusMessage().contains("Negative cycle"));
    }

    @Test
    void fullTreeNoTarget() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 2);
        g.addEdge("A", "C", 5);
        g.addEdge("B", "C", 1);

        List<AlgorithmFrame> frames = BellmanFordRunner.run(g, "A");
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertTrue(last.shortestPath().isEmpty());
        assertNull(last.targetNode());
        assertEquals(0.0, last.distances().get("A"), 0.001);
        assertEquals(2.0, last.distances().get("B"), 0.001);
        assertEquals(3.0, last.distances().get("C"), 0.001);
    }

    @Test
    void unreachableTarget() {
        Graph g = new Graph(true);
        g.addEdge("A", "B", 1);
        g.addNode("X");

        List<AlgorithmFrame> frames = BellmanFordRunner.run(g, "A", "X");
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertTrue(last.statusMessage().contains("unreachable"));
        assertTrue(last.shortestPath().isEmpty());
    }

    @Test
    void singleNode() {
        Graph g = new Graph(false);
        g.addNode("X");

        List<AlgorithmFrame> frames = BellmanFordRunner.run(g, "X");
        assertFalse(frames.isEmpty());
        AlgorithmFrame last = frames.get(frames.size() - 1);
        assertEquals(0.0, last.distances().get("X"), 0.001);
    }

    @Test
    void sourceNotFoundThrows() {
        Graph g = new Graph(false);
        g.addNode("A");

        assertThrows(IllegalArgumentException.class, () -> BellmanFordRunner.run(g, "Z"));
    }

    @Test
    void targetNotFoundThrows() {
        Graph g = new Graph(false);
        g.addNode("A");

        assertThrows(IllegalArgumentException.class, () -> BellmanFordRunner.run(g, "A", "Z"));
    }

    @Test
    void earlyConvergence() {
        // Simple chain: A→B→C — should converge after 1 or 2 passes
        Graph g = new Graph(true);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 1);

        List<AlgorithmFrame> frames = BellmanFordRunner.run(g, "A", "C");
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertTrue(last.statusMessage().contains("complete"));
        assertEquals(2.0, last.distances().get("C"), 0.001);
        // Check for early convergence message somewhere in the frames
        boolean hasConverged = frames.stream()
                .anyMatch(f -> f.statusMessage().contains("converged early"));
        assertTrue(hasConverged, "Expected early convergence for simple chain");
    }

    @Test
    void formatDistHandlesInfinity() {
        assertEquals("∞", BellmanFordRunner.formatDist(Double.MAX_VALUE));
    }

    @Test
    void formatDistHandlesWholeNumbers() {
        assertEquals("5", BellmanFordRunner.formatDist(5.0));
    }

    @Test
    void formatDistHandlesDecimals() {
        assertEquals("3.5", BellmanFordRunner.formatDist(3.5));
    }

    @Test
    void reconstructPathWithCycleGuard() {
        // Verify reconstructPath doesn't loop on a cycle in parentMap
        java.util.Map<String, String> parentMap = new java.util.LinkedHashMap<>();
        parentMap.put("B", "A");
        parentMap.put("C", "B");
        parentMap.put("A", "C"); // cycle

        List<String> path = BellmanFordRunner.reconstructPath(parentMap, "A", "C");
        // cycle guard: should not infinite-loop, returns whatever partial result
        assertNotNull(path);
    }
}
