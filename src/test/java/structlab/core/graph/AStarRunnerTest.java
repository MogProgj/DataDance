package structlab.core.graph;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AStarRunnerTest {

    @Test
    void simpleShortestPath() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 2);
        g.addEdge("A", "C", 10);

        List<AlgorithmFrame> frames = AStarRunner.run(g, "A", "C", null);
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertEquals(AlgorithmFrame.AlgorithmType.A_STAR, last.algorithm());
        assertTrue(last.statusMessage().contains("complete"));
        assertEquals(List.of("A", "B", "C"), last.shortestPath());
    }

    @Test
    void competingRoutes() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 2);
        g.addEdge("C", "D", 3);
        g.addEdge("A", "D", 10);

        List<AlgorithmFrame> frames = AStarRunner.run(g, "A", "D", null);
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertEquals(List.of("A", "B", "C", "D"), last.shortestPath());
        // g-scores should reflect actual costs
        assertEquals(0.0, last.distances().get("A"), 0.001);
    }

    @Test
    void withEuclideanHeuristic() {
        // Positions: A(0,0), B(5,0), C(10,0), D(10,5)
        Graph g = new Graph(false);
        g.addEdge("A", "B", 5);
        g.addEdge("B", "C", 5);
        g.addEdge("C", "D", 5);
        g.addEdge("A", "D", 20);

        Map<String, double[]> positions = Map.of(
                "A", new double[]{0, 0},
                "B", new double[]{5, 0},
                "C", new double[]{10, 0},
                "D", new double[]{10, 5}
        );

        List<AlgorithmFrame> frames = AStarRunner.run(g, "A", "D", positions);
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertEquals(List.of("A", "B", "C", "D"), last.shortestPath());
        assertTrue(last.statusMessage().contains("complete"));
    }

    @Test
    void unreachableTarget() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addNode("X");

        List<AlgorithmFrame> frames = AStarRunner.run(g, "A", "X", null);
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertTrue(last.statusMessage().contains("unreachable"));
        assertTrue(last.shortestPath().isEmpty());
    }

    @Test
    void singleNodeGraph() {
        Graph g = new Graph(false);
        g.addNode("A");

        List<AlgorithmFrame> frames = AStarRunner.run(g, "A", "A", null);
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertTrue(last.statusMessage().contains("complete") || last.statusMessage().contains("reached"));
        assertEquals(List.of("A"), last.shortestPath());
    }

    @Test
    void sourceNotFoundThrows() {
        Graph g = new Graph(false);
        g.addNode("A");

        assertThrows(IllegalArgumentException.class,
                () -> AStarRunner.run(g, "Z", "A", null));
    }

    @Test
    void targetNotFoundThrows() {
        Graph g = new Graph(false);
        g.addNode("A");

        assertThrows(IllegalArgumentException.class,
                () -> AStarRunner.run(g, "A", "Z", null));
    }

    @Test
    void nullTargetThrows() {
        Graph g = new Graph(false);
        g.addNode("A");

        assertThrows(IllegalArgumentException.class,
                () -> AStarRunner.run(g, "A", null, null));
    }

    @Test
    void negativeWeightThrows() {
        Graph g = new Graph(true);
        g.addEdge("A", "B", -1);

        assertThrows(IllegalArgumentException.class,
                () -> AStarRunner.run(g, "A", "B", null));
    }

    @Test
    void directedGraph() {
        Graph g = new Graph(true);
        g.addEdge("S", "A", 3);
        g.addEdge("S", "B", 5);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "T", 2);

        List<AlgorithmFrame> frames = AStarRunner.run(g, "S", "T", null);
        AlgorithmFrame last = frames.get(frames.size() - 1);

        assertEquals(List.of("S", "A", "B", "T"), last.shortestPath());
        assertTrue(last.statusMessage().contains("complete"));
    }

    @Test
    void zeroHeuristicMatchesDijkstra() {
        // With null positions (zero heuristic), A* should find the same path as Dijkstra
        Graph g = new Graph(false);
        g.addEdge("A", "B", 2);
        g.addEdge("A", "C", 5);
        g.addEdge("B", "C", 1);
        g.addEdge("B", "D", 7);
        g.addEdge("C", "D", 3);

        List<AlgorithmFrame> astarFrames = AStarRunner.run(g, "A", "D", null);
        List<AlgorithmFrame> dijkstraFrames = DijkstraRunner.run(g, "A", "D");

        AlgorithmFrame astarLast = astarFrames.get(astarFrames.size() - 1);
        AlgorithmFrame dijkstraLast = dijkstraFrames.get(dijkstraFrames.size() - 1);

        assertEquals(dijkstraLast.shortestPath(), astarLast.shortestPath());
    }

    @Test
    void heuristicHelperWithNullPositions() {
        assertEquals(0.0, AStarRunner.heuristic("A", "B", null));
    }

    @Test
    void heuristicHelperWithPositions() {
        Map<String, double[]> positions = Map.of(
                "A", new double[]{0, 0},
                "B", new double[]{3, 4}
        );
        assertEquals(5.0, AStarRunner.heuristic("A", "B", positions), 0.001);
    }

    @Test
    void heuristicHelperMissingNode() {
        Map<String, double[]> positions = Map.of("A", new double[]{0, 0});
        assertEquals(0.0, AStarRunner.heuristic("A", "B", positions));
    }

    @Test
    void framesHaveCorrectAlgorithmType() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);

        List<AlgorithmFrame> frames = AStarRunner.run(g, "A", "B", null);
        for (AlgorithmFrame f : frames) {
            assertEquals(AlgorithmFrame.AlgorithmType.A_STAR, f.algorithm());
        }
    }

    @Test
    void statusMessagesContainFGH() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 3);
        g.addEdge("B", "C", 2);

        Map<String, double[]> positions = Map.of(
                "A", new double[]{0, 0},
                "B", new double[]{3, 0},
                "C", new double[]{6, 0}
        );

        List<AlgorithmFrame> frames = AStarRunner.run(g, "A", "C", positions);
        // The expand/discover messages should contain f= and g= info
        boolean hasF = frames.stream().anyMatch(f -> f.statusMessage().contains("f="));
        boolean hasG = frames.stream().anyMatch(f -> f.statusMessage().contains("g="));
        assertTrue(hasF, "Expected status messages with f= cost info");
        assertTrue(hasG, "Expected status messages with g= cost info");
    }
}
