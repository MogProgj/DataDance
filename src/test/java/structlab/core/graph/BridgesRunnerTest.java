package structlab.core.graph;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BridgesRunnerTest {

    @Test
    void graphWithBridges() {
        // A--B--C : edge B-C is a bridge if we add A-B redundancy removed
        // Actually: linear chain A-B-C, both edges are bridges
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 1);

        List<AlgorithmFrame> frames = BridgesRunner.run(g);
        AlgorithmFrame last = frames.get(frames.size() - 1);
        assertEquals(AlgorithmFrame.AlgorithmType.BRIDGES, last.algorithm());
        assertTrue(last.statusMessage().contains("2 bridges"));

        Set<AlgorithmFrame.TraversalEdge> bridges = BridgesRunner.extractBridges(frames);
        assertEquals(2, bridges.size());
    }

    @Test
    void noBridgesBiconnected() {
        // Triangle: A-B, B-C, A-C — no bridges
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 1);
        g.addEdge("A", "C", 1);

        List<AlgorithmFrame> frames = BridgesRunner.run(g);
        AlgorithmFrame last = frames.get(frames.size() - 1);
        assertTrue(last.statusMessage().contains("no bridges"));

        Set<AlgorithmFrame.TraversalEdge> bridges = BridgesRunner.extractBridges(frames);
        assertTrue(bridges.isEmpty());
    }

    @Test
    void treeAllBridges() {
        // A-B, A-C, A-D — star graph, all edges are bridges
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("A", "C", 1);
        g.addEdge("A", "D", 1);

        List<AlgorithmFrame> frames = BridgesRunner.run(g);
        Set<AlgorithmFrame.TraversalEdge> bridges = BridgesRunner.extractBridges(frames);
        assertEquals(3, bridges.size());
    }

    @Test
    void singleNode() {
        Graph g = new Graph(false);
        g.addNode("X");

        List<AlgorithmFrame> frames = BridgesRunner.run(g);
        AlgorithmFrame last = frames.get(frames.size() - 1);
        assertTrue(last.statusMessage().contains("no bridges"));
    }

    @Test
    void rejectsDirectedGraph() {
        Graph g = new Graph(true);
        g.addEdge("A", "B", 1);
        assertThrows(IllegalArgumentException.class, () -> BridgesRunner.run(g));
    }

    @Test
    void bridgeInLargerGraph() {
        // Two triangles connected by a bridge:
        // Triangle 1: A-B, B-C, A-C
        // Bridge: C-D
        // Triangle 2: D-E, E-F, D-F
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 1);
        g.addEdge("A", "C", 1);
        g.addEdge("C", "D", 1);
        g.addEdge("D", "E", 1);
        g.addEdge("E", "F", 1);
        g.addEdge("D", "F", 1);

        List<AlgorithmFrame> frames = BridgesRunner.run(g);
        Set<AlgorithmFrame.TraversalEdge> bridges = BridgesRunner.extractBridges(frames);
        assertEquals(1, bridges.size());
        // The bridge should be C-D (or D-C depending on DFS direction)
        AlgorithmFrame.TraversalEdge bridge = bridges.iterator().next();
        assertTrue(
                (bridge.from().equals("C") && bridge.to().equals("D"))
                        || (bridge.from().equals("D") && bridge.to().equals("C")),
                "Expected bridge C-D but got " + bridge.from() + "-" + bridge.to());
    }

    @Test
    void discLowEncodedInDistances() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 1);

        List<AlgorithmFrame> frames = BridgesRunner.run(g);
        AlgorithmFrame last = frames.get(frames.size() - 1);
        // Each node should have disc*1000+low encoded
        for (String node : List.of("A", "B", "C")) {
            assertNotNull(last.distances().get(node),
                    "Expected disc/low encoding for " + node);
            assertTrue(last.distances().get(node) >= 0);
        }
    }

    @Test
    void allFramesHaveCorrectType() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);

        List<AlgorithmFrame> frames = BridgesRunner.run(g);
        for (AlgorithmFrame f : frames) {
            assertEquals(AlgorithmFrame.AlgorithmType.BRIDGES, f.algorithm());
        }
    }
}
