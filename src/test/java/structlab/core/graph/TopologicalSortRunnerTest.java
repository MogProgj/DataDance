package structlab.core.graph;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TopologicalSortRunnerTest {

    @Test
    void simpleDag() {
        Graph g = new Graph(true);
        g.addEdge("A", "B");
        g.addEdge("A", "C");
        g.addEdge("B", "D");
        g.addEdge("C", "D");

        List<AlgorithmFrame> frames = TopologicalSortRunner.run(g);
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertEquals(AlgorithmFrame.AlgorithmType.TOPOLOGICAL_SORT, last.algorithm());
        assertTrue(last.statusMessage().contains("complete"));

        // A must come before B and C; B and C must come before D
        List<String> order = last.discoveryOrder();
        assertEquals(4, order.size());
        assertTrue(order.indexOf("A") < order.indexOf("B"));
        assertTrue(order.indexOf("A") < order.indexOf("C"));
        assertTrue(order.indexOf("B") < order.indexOf("D"));
        assertTrue(order.indexOf("C") < order.indexOf("D"));
    }

    @Test
    void linearChain() {
        Graph g = new Graph(true);
        g.addEdge("A", "B");
        g.addEdge("B", "C");
        g.addEdge("C", "D");

        List<AlgorithmFrame> frames = TopologicalSortRunner.run(g);
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertEquals(List.of("A", "B", "C", "D"), last.discoveryOrder());
    }

    @Test
    void cycleDetected() {
        Graph g = new Graph(true);
        g.addEdge("A", "B");
        g.addEdge("B", "C");
        g.addEdge("C", "A");

        List<AlgorithmFrame> frames = TopologicalSortRunner.run(g);
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertTrue(last.statusMessage().contains("Cycle detected"));
        assertTrue(last.discoveryOrder().size() < 3);
    }

    @Test
    void undirectedGraphThrows() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");

        assertThrows(IllegalArgumentException.class, () -> TopologicalSortRunner.run(g));
    }

    @Test
    void singleNode() {
        Graph g = new Graph(true);
        g.addNode("X");

        List<AlgorithmFrame> frames = TopologicalSortRunner.run(g);
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertTrue(last.statusMessage().contains("complete"));
        assertEquals(List.of("X"), last.discoveryOrder());
    }

    @Test
    void disconnectedDag() {
        Graph g = new Graph(true);
        g.addEdge("A", "B");
        g.addEdge("C", "D");

        List<AlgorithmFrame> frames = TopologicalSortRunner.run(g);
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertTrue(last.statusMessage().contains("complete"));
        List<String> order = last.discoveryOrder();
        assertEquals(4, order.size());
        assertTrue(order.indexOf("A") < order.indexOf("B"));
        assertTrue(order.indexOf("C") < order.indexOf("D"));
    }

    @Test
    void indegreeStoredInDistances() {
        Graph g = new Graph(true);
        g.addEdge("A", "C");
        g.addEdge("B", "C");

        List<AlgorithmFrame> frames = TopologicalSortRunner.run(g);
        // First frame should show initial indegrees
        AlgorithmFrame first = frames.get(0);
        assertEquals(0.0, first.distances().get("A"), 0.001);
        assertEquals(0.0, first.distances().get("B"), 0.001);
        assertEquals(2.0, first.distances().get("C"), 0.001);
    }

    @Test
    void dagClassicPreset() {
        GraphPresets.Preset preset = GraphPresets.dagClassic();
        List<AlgorithmFrame> frames = TopologicalSortRunner.run(preset.graph());
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertTrue(last.statusMessage().contains("complete"));
        assertEquals(6, last.discoveryOrder().size());
    }

    @Test
    void dagDiamondPreset() {
        GraphPresets.Preset preset = GraphPresets.dagDiamond();
        List<AlgorithmFrame> frames = TopologicalSortRunner.run(preset.graph());
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertTrue(last.statusMessage().contains("complete"));
        assertEquals(6, last.discoveryOrder().size());
    }
}
