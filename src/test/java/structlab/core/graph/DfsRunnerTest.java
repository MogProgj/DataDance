package structlab.core.graph;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DfsRunnerTest {

    @Test
    void dfsSimpleLinear() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("B", "C");

        List<AlgorithmFrame> frames = DfsRunner.run(g, "A");

        assertFalse(frames.isEmpty());

        AlgorithmFrame first = frames.get(0);
        assertEquals(AlgorithmFrame.AlgorithmType.DFS, first.algorithm());

        AlgorithmFrame last = frames.get(frames.size() - 1);
        assertTrue(last.statusMessage().contains("complete"));
        assertEquals(3, last.visited().size());
    }

    @Test
    void dfsVisitsAllReachableNodes() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("A", "C");
        g.addEdge("B", "D");

        List<AlgorithmFrame> frames = DfsRunner.run(g, "A");
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertEquals(Set.of("A", "B", "C", "D"), last.visited());
    }

    @Test
    void dfsDepthFirstBehavior() {
        // Tree: A -> B -> D; A -> C
        // DFS from A should go deep (A, B, D) before backtracking to C
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("A", "C");
        g.addEdge("B", "D");

        List<AlgorithmFrame> frames = DfsRunner.run(g, "A");
        AlgorithmFrame last = frames.get(frames.size() - 1);

        List<String> order = last.discoveryOrder();
        assertEquals("A", order.get(0));
        // B should come before C (depth-first into B→D before visiting C)
        assertTrue(order.indexOf("B") < order.indexOf("C"));
        // D should come before C (explored deeper first)
        assertTrue(order.indexOf("D") < order.indexOf("C"));
    }

    @Test
    void dfsDisconnectedGraphPartialReach() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addNode("X");

        List<AlgorithmFrame> frames = DfsRunner.run(g, "A");
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertEquals(2, last.visited().size());
        assertFalse(last.visited().contains("X"));
    }

    @Test
    void dfsTraversalTree() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("B", "C");

        List<AlgorithmFrame> frames = DfsRunner.run(g, "A");
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertEquals("A", last.parentMap().get("B"));
        assertEquals("B", last.parentMap().get("C"));
    }

    @Test
    void dfsSourceNotFoundThrows() {
        Graph g = new Graph(false);
        g.addNode("A");

        assertThrows(IllegalArgumentException.class, () -> DfsRunner.run(g, "Z"));
    }

    @Test
    void dfsSingleNode() {
        Graph g = new Graph(false);
        g.addNode("X");

        List<AlgorithmFrame> frames = DfsRunner.run(g, "X");
        AlgorithmFrame last = frames.get(frames.size() - 1);
        assertEquals(Set.of("X"), last.visited());
    }

    @Test
    void dfsWithCycles() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("B", "C");
        g.addEdge("C", "A");

        List<AlgorithmFrame> frames = DfsRunner.run(g, "A");
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertEquals(3, last.visited().size());
        assertTrue(frames.size() < 30);
    }

    @Test
    void dfsDirectedLimitedReach() {
        Graph g = new Graph(true);
        g.addEdge("A", "B");
        g.addEdge("B", "C");

        List<AlgorithmFrame> frames = DfsRunner.run(g, "C");
        AlgorithmFrame last = frames.get(frames.size() - 1);
        assertEquals(1, last.visited().size());
    }

    @Test
    void dfsFramesAreImmutable() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");

        List<AlgorithmFrame> frames = DfsRunner.run(g, "A");
        assertThrows(UnsupportedOperationException.class, () -> frames.add(null));
    }
}
