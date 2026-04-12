package structlab.core.graph;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ArticulationPointsRunnerTest {

    @Test
    void graphWithArticulationPoint() {
        // A-B, B-C, B-D — B is articulation point
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 1);
        g.addEdge("B", "D", 1);

        List<AlgorithmFrame> frames = ArticulationPointsRunner.run(g);
        AlgorithmFrame last = frames.get(frames.size() - 1);
        assertEquals(AlgorithmFrame.AlgorithmType.ARTICULATION_POINTS, last.algorithm());

        Set<String> aps = ArticulationPointsRunner.extractArticulationPoints(frames);
        assertTrue(aps.contains("B"), "B should be an articulation point");
    }

    @Test
    void biconnectedGraphNone() {
        // Triangle A-B, B-C, A-C — no articulation points
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 1);
        g.addEdge("A", "C", 1);

        List<AlgorithmFrame> frames = ArticulationPointsRunner.run(g);
        AlgorithmFrame last = frames.get(frames.size() - 1);
        assertTrue(last.statusMessage().contains("no articulation points")
                || last.statusMessage().contains("biconnected"));

        Set<String> aps = ArticulationPointsRunner.extractArticulationPoints(frames);
        assertTrue(aps.isEmpty());
    }

    @Test
    void linearChain() {
        // A-B-C-D — B and C are articulation points
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 1);
        g.addEdge("C", "D", 1);

        List<AlgorithmFrame> frames = ArticulationPointsRunner.run(g);
        Set<String> aps = ArticulationPointsRunner.extractArticulationPoints(frames);
        assertTrue(aps.contains("B"), "B should be an articulation point");
        assertTrue(aps.contains("C"), "C should be an articulation point");
        assertFalse(aps.contains("A"));
        assertFalse(aps.contains("D"));
    }

    @Test
    void singleNode() {
        Graph g = new Graph(false);
        g.addNode("X");

        List<AlgorithmFrame> frames = ArticulationPointsRunner.run(g);
        Set<String> aps = ArticulationPointsRunner.extractArticulationPoints(frames);
        assertTrue(aps.isEmpty());
    }

    @Test
    void rejectsDirectedGraph() {
        Graph g = new Graph(true);
        g.addEdge("A", "B", 1);
        assertThrows(IllegalArgumentException.class,
                () -> ArticulationPointsRunner.run(g));
    }

    @Test
    void twoTrianglesConnectedByBridge() {
        // Triangle 1: A-B-C-A, bridge C-D, Triangle 2: D-E-F-D
        // C and D are articulation points
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 1);
        g.addEdge("A", "C", 1);
        g.addEdge("C", "D", 1);
        g.addEdge("D", "E", 1);
        g.addEdge("E", "F", 1);
        g.addEdge("D", "F", 1);

        List<AlgorithmFrame> frames = ArticulationPointsRunner.run(g);
        Set<String> aps = ArticulationPointsRunner.extractArticulationPoints(frames);
        assertTrue(aps.contains("C"), "C should be an articulation point");
        assertTrue(aps.contains("D"), "D should be an articulation point");
        assertEquals(2, aps.size());
    }

    @Test
    void allFramesHaveCorrectType() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 1);

        List<AlgorithmFrame> frames = ArticulationPointsRunner.run(g);
        for (AlgorithmFrame f : frames) {
            assertEquals(AlgorithmFrame.AlgorithmType.ARTICULATION_POINTS, f.algorithm());
        }
    }
}
