package structlab.core.graph;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KruskalRunnerTest {

    @Test
    void simpleMST() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 2);
        g.addEdge("A", "C", 3);

        List<AlgorithmFrame> frames = KruskalRunner.run(g);
        assertFalse(frames.isEmpty());

        AlgorithmFrame last = frames.get(frames.size() - 1);
        assertEquals(AlgorithmFrame.AlgorithmType.KRUSKAL, last.algorithm());
        assertTrue(last.statusMessage().contains("complete"));
        // MST: A-B(1) + B-C(2) = 3
        assertEquals(2, last.treeEdges().size());
        assertEquals(3.0, last.distances().get("__MST_TOTAL__"), 0.001);
    }

    @Test
    void singleNode() {
        Graph g = new Graph(false);
        g.addNode("X");

        List<AlgorithmFrame> frames = KruskalRunner.run(g);
        AlgorithmFrame last = frames.get(frames.size() - 1);
        assertTrue(last.statusMessage().contains("complete"));
        assertEquals(0, last.treeEdges().size());
    }

    @Test
    void rejectsDirectedGraph() {
        Graph g = new Graph(true);
        g.addEdge("A", "B", 1);
        assertThrows(IllegalArgumentException.class, () -> KruskalRunner.run(g));
    }

    @Test
    void rejectsNegativeWeights() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", -5);
        assertThrows(IllegalArgumentException.class, () -> KruskalRunner.run(g));
    }

    @Test
    void cycleRejection() {
        // A-B(1), B-C(2), A-C(3), C-D(4)
        // Sorted: A-B(1), B-C(2), A-C(3), C-D(4)
        // Kruskal: accept A-B(1), accept B-C(2), reject A-C(3) cycle, accept C-D(4)
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 2);
        g.addEdge("A", "C", 3);
        g.addEdge("C", "D", 4);

        List<AlgorithmFrame> frames = KruskalRunner.run(g);
        AlgorithmFrame last = frames.get(frames.size() - 1);
        // MST has exactly 3 edges for 4 nodes
        assertEquals(3, last.treeEdges().size());
        // MST: A-B(1) + B-C(2) + C-D(4) = 7
        assertEquals(7.0, last.distances().get("__MST_TOTAL__"), 0.001);
        // A-C(3) was rejected
        assertTrue(frames.stream().anyMatch(f ->
                f.statusMessage().contains("Rejected")));
    }

    @Test
    void disconnectedComponents() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("C", "D", 2);

        List<AlgorithmFrame> frames = KruskalRunner.run(g);
        AlgorithmFrame last = frames.get(frames.size() - 1);
        assertTrue(last.statusMessage().contains("2 components"));
        assertEquals(2, last.treeEdges().size());
        assertEquals(3.0, last.distances().get("__MST_TOTAL__"), 0.001);
    }

    @Test
    void choosesLighterEdge() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 10);
        g.addEdge("A", "C", 1);
        g.addEdge("B", "C", 2);

        List<AlgorithmFrame> frames = KruskalRunner.run(g);
        AlgorithmFrame last = frames.get(frames.size() - 1);
        // MST: A-C(1) + B-C(2) = 3, not using A-B(10)
        assertEquals(3.0, last.distances().get("__MST_TOTAL__"), 0.001);
    }

    @Test
    void allFramesHaveCorrectType() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 2);

        List<AlgorithmFrame> frames = KruskalRunner.run(g);
        for (AlgorithmFrame f : frames) {
            assertEquals(AlgorithmFrame.AlgorithmType.KRUSKAL, f.algorithm());
        }
    }
}
