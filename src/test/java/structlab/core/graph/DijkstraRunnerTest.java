package structlab.core.graph;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DijkstraRunnerTest {

    @Test
    void simpleShortestPath() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 2);
        g.addEdge("A", "C", 10);

        List<AlgorithmFrame> frames = DijkstraRunner.run(g, "A", "C");
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertEquals(AlgorithmFrame.AlgorithmType.DIJKSTRA, last.algorithm());
        assertTrue(last.statusMessage().contains("complete"));
        assertEquals(List.of("A", "B", "C"), last.shortestPath());
        assertEquals(3.0, last.distances().get("C"), 0.001);
    }

    @Test
    void fullTreeNoTarget() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 2);
        g.addEdge("A", "C", 5);
        g.addEdge("B", "C", 1);

        List<AlgorithmFrame> frames = DijkstraRunner.run(g, "A");
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertTrue(last.shortestPath().isEmpty());
        assertNull(last.targetNode());
        assertEquals(0.0, last.distances().get("A"), 0.001);
        assertEquals(2.0, last.distances().get("B"), 0.001);
        assertEquals(3.0, last.distances().get("C"), 0.001);
        assertEquals(3, last.visited().size());
    }

    @Test
    void competingRoutes() {
        // Direct A→D costs 10, A→B→C→D costs 6
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 2);
        g.addEdge("C", "D", 3);
        g.addEdge("A", "D", 10);

        List<AlgorithmFrame> frames = DijkstraRunner.run(g, "A", "D");
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertEquals(List.of("A", "B", "C", "D"), last.shortestPath());
        assertEquals(6.0, last.distances().get("D"), 0.001);
    }

    @Test
    void earlyTerminationWhenTargetSettled() {
        // After settling target, algorithm should stop
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("A", "C", 100);
        g.addEdge("B", "D", 1);
        g.addEdge("C", "D", 1);

        List<AlgorithmFrame> frames = DijkstraRunner.run(g, "A", "B");
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertTrue(last.statusMessage().contains("complete"));
        assertEquals(List.of("A", "B"), last.shortestPath());
        // C should not be settled — early termination
        assertFalse(last.visited().contains("C"));
    }

    @Test
    void unreachableTarget() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addNode("X");

        List<AlgorithmFrame> frames = DijkstraRunner.run(g, "A", "X");
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertTrue(last.statusMessage().contains("unreachable"));
        assertTrue(last.shortestPath().isEmpty());
    }

    @Test
    void negativeWeightRejected() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", -1);

        assertThrows(IllegalArgumentException.class, () -> DijkstraRunner.run(g, "A"));
    }

    @Test
    void sourceNotFoundThrows() {
        Graph g = new Graph(false);
        g.addNode("A");

        assertThrows(IllegalArgumentException.class, () -> DijkstraRunner.run(g, "Z"));
    }

    @Test
    void targetNotFoundThrows() {
        Graph g = new Graph(false);
        g.addNode("A");

        assertThrows(IllegalArgumentException.class, () -> DijkstraRunner.run(g, "A", "Z"));
    }

    @Test
    void singleNode() {
        Graph g = new Graph(false);
        g.addNode("X");

        List<AlgorithmFrame> frames = DijkstraRunner.run(g, "X");
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertEquals(1, last.visited().size());
        assertTrue(last.visited().contains("X"));
        assertEquals(0.0, last.distances().get("X"), 0.001);
    }

    @Test
    void directedGraphRespectsDirection() {
        Graph g = new Graph(true);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 2);
        // No edge C→A, so starting from C only reaches C

        List<AlgorithmFrame> frames = DijkstraRunner.run(g, "C");
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertEquals(1, last.visited().size());
    }

    @Test
    void relaxationProducesDecreasingDistance() {
        // A→B 5, A→C 2, C→B 1 — B should relax from 5 to 3
        Graph g = new Graph(false);
        g.addEdge("A", "B", 5);
        g.addEdge("A", "C", 2);
        g.addEdge("C", "B", 1);

        List<AlgorithmFrame> frames = DijkstraRunner.run(g, "A");
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertEquals(3.0, last.distances().get("B"), 0.001);
        assertEquals("C", last.parentMap().get("B"));

        // Verify that a relaxation message appears in some frame
        boolean foundRelax = frames.stream()
                .anyMatch(f -> f.statusMessage().contains("Relaxed B"));
        assertTrue(foundRelax, "Expected a relaxation message for B");
    }

    @Test
    void frameDistancesContainAllNodes() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 2);

        List<AlgorithmFrame> frames = DijkstraRunner.run(g, "A");
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertEquals(3, last.distances().size());
        assertTrue(last.distances().containsKey("A"));
        assertTrue(last.distances().containsKey("B"));
        assertTrue(last.distances().containsKey("C"));
    }

    @Test
    void framesAreImmutable() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);

        List<AlgorithmFrame> frames = DijkstraRunner.run(g, "A");
        assertThrows(UnsupportedOperationException.class, () -> frames.add(null));
    }

    @Test
    void formatDistInfinity() {
        assertEquals("∞", DijkstraRunner.formatDist(DijkstraRunner.INF));
    }

    @Test
    void formatDistInteger() {
        assertEquals("5", DijkstraRunner.formatDist(5.0));
        assertEquals("0", DijkstraRunner.formatDist(0.0));
    }

    @Test
    void formatDistDecimal() {
        assertEquals("3.5", DijkstraRunner.formatDist(3.5));
    }

    @Test
    void zeroWeightEdges() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 0);
        g.addEdge("B", "C", 0);

        List<AlgorithmFrame> frames = DijkstraRunner.run(g, "A");
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertEquals(0.0, last.distances().get("B"), 0.001);
        assertEquals(0.0, last.distances().get("C"), 0.001);
    }
}
