package structlab.core.graph;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrimRunnerTest {

    @Test
    void simpleMST() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 2);
        g.addEdge("A", "C", 3);

        List<AlgorithmFrame> frames = PrimRunner.run(g, "A");
        assertFalse(frames.isEmpty());

        AlgorithmFrame last = frames.get(frames.size() - 1);
        assertEquals(AlgorithmFrame.AlgorithmType.PRIM, last.algorithm());
        assertTrue(last.statusMessage().contains("complete"));
        // MST weight should be 1 + 2 = 3 (edges A-B and B-C)
        assertEquals(2, last.treeEdges().size());
        Double total = last.distances().get("__MST_TOTAL__");
        assertNotNull(total);
        assertEquals(3.0, total, 0.001);
    }

    @Test
    void singleNode() {
        Graph g = new Graph(false);
        g.addNode("X");

        List<AlgorithmFrame> frames = PrimRunner.run(g, "X");
        AlgorithmFrame last = frames.get(frames.size() - 1);
        assertTrue(last.statusMessage().contains("complete"));
        assertEquals(0, last.treeEdges().size());
    }

    @Test
    void disconnectedGraph() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addNode("C");

        List<AlgorithmFrame> frames = PrimRunner.run(g, "A");
        AlgorithmFrame last = frames.get(frames.size() - 1);
        assertTrue(last.statusMessage().contains("complete"));
        // Only A-B reachable, C isolated
        assertEquals(1, last.treeEdges().size());
        assertEquals(2, last.visited().size());
    }

    @Test
    void rejectsDirectedGraph() {
        Graph g = new Graph(true);
        g.addEdge("A", "B", 1);

        assertThrows(IllegalArgumentException.class, () -> PrimRunner.run(g, "A"));
    }

    @Test
    void rejectsMissingSource() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);

        assertThrows(IllegalArgumentException.class, () -> PrimRunner.run(g, "Z"));
    }

    @Test
    void rejectsNegativeWeights() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", -1);

        assertThrows(IllegalArgumentException.class, () -> PrimRunner.run(g, "A"));
    }

    @Test
    void choosesMinimumEdge() {
        // Triangle: A-B=1, A-C=10, B-C=2
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("A", "C", 10);
        g.addEdge("B", "C", 2);

        List<AlgorithmFrame> frames = PrimRunner.run(g, "A");
        AlgorithmFrame last = frames.get(frames.size() - 1);
        // MST: A-B(1) + B-C(2) = 3
        assertEquals(3.0, last.distances().get("__MST_TOTAL__"), 0.001);
        assertEquals(2, last.treeEdges().size());
    }

    @Test
    void allFramesHaveCorrectAlgorithmType() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 3);
        g.addEdge("B", "C", 4);

        List<AlgorithmFrame> frames = PrimRunner.run(g, "A");
        for (AlgorithmFrame f : frames) {
            assertEquals(AlgorithmFrame.AlgorithmType.PRIM, f.algorithm());
        }
    }
}
