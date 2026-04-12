package structlab.core.graph;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class SCCRunnerTest {

    @Test
    void singleSCC() {
        // Fully connected directed cycle: A→B→C→A (one SCC)
        Graph g = new Graph(true);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 1);
        g.addEdge("C", "A", 1);

        List<AlgorithmFrame> frames = SCCRunner.run(g);
        AlgorithmFrame last = frames.get(frames.size() - 1);
        assertEquals(AlgorithmFrame.AlgorithmType.SCC, last.algorithm());
        assertTrue(last.statusMessage().contains("1 SCC"));

        // All nodes are in the same component
        Map<String, Double> dist = last.distances();
        double compA = dist.get("A");
        assertEquals(compA, dist.get("B"));
        assertEquals(compA, dist.get("C"));
    }

    @Test
    void multipleSCCs() {
        // A→B→A forms one SCC; C→D→C forms another; B→C crosses
        Graph g = new Graph(true);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "A", 1);
        g.addEdge("B", "C", 1);
        g.addEdge("C", "D", 1);
        g.addEdge("D", "C", 1);

        List<AlgorithmFrame> frames = SCCRunner.run(g);
        AlgorithmFrame last = frames.get(frames.size() - 1);
        assertTrue(last.statusMessage().contains("2 SCCs"));

        Map<String, Double> dist = last.distances();
        assertEquals(dist.get("A"), dist.get("B")); // same SCC
        assertEquals(dist.get("C"), dist.get("D")); // same SCC
        assertNotEquals(dist.get("A"), dist.get("C")); // different SCCs
    }

    @Test
    void dagEachNodeIsSCC() {
        // A→B→C (DAG — each node is its own SCC)
        Graph g = new Graph(true);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 1);

        List<AlgorithmFrame> frames = SCCRunner.run(g);
        AlgorithmFrame last = frames.get(frames.size() - 1);
        assertTrue(last.statusMessage().contains("3 SCCs"));

        Map<String, Double> dist = last.distances();
        Set<Double> ids = dist.values().stream().collect(Collectors.toSet());
        assertEquals(3, ids.size());
    }

    @Test
    void singleNode() {
        Graph g = new Graph(true);
        g.addNode("X");

        List<AlgorithmFrame> frames = SCCRunner.run(g);
        AlgorithmFrame last = frames.get(frames.size() - 1);
        assertTrue(last.statusMessage().contains("1 SCC"));
    }

    @Test
    void rejectsUndirectedGraph() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        assertThrows(IllegalArgumentException.class, () -> SCCRunner.run(g));
    }

    @Test
    void allFramesHaveCorrectType() {
        Graph g = new Graph(true);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "A", 1);

        List<AlgorithmFrame> frames = SCCRunner.run(g);
        for (AlgorithmFrame f : frames) {
            assertEquals(AlgorithmFrame.AlgorithmType.SCC, f.algorithm());
        }
    }

    @Test
    void completionMessageMentionsNodeCount() {
        Graph g = new Graph(true);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 1);
        g.addEdge("C", "A", 1);

        List<AlgorithmFrame> frames = SCCRunner.run(g);
        AlgorithmFrame last = frames.get(frames.size() - 1);
        assertTrue(last.statusMessage().contains("3 nodes"));
    }
}
