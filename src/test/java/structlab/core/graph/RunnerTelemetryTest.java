package structlab.core.graph;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies that every graph algorithm runner attaches non-null telemetry
 * with correct phases to each frame.
 */
class RunnerTelemetryTest {

    // ── Dijkstra ─────────────────────────────────────────────

    @Test
    void dijkstraFramesHaveTelemetry() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 2);

        List<AlgorithmFrame> frames = DijkstraRunner.run(g, "A", "C");
        assertAllFramesHaveTelemetry(frames);
        assertFirstPhase(frames, "Initialization");
        assertLastPhase(frames, "Complete");
    }

    @Test
    void dijkstraHasExtractMinPhase() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 2);

        List<AlgorithmFrame> frames = DijkstraRunner.run(g, "A");
        assertTrue(frames.stream().anyMatch(f -> "Extract-Min".equals(f.telemetry().phase())));
    }

    // ── BFS ──────────────────────────────────────────────────

    @Test
    void bfsFramesHaveTelemetry() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 1);

        List<AlgorithmFrame> frames = BfsRunner.run(g, "A");
        assertAllFramesHaveTelemetry(frames);
        assertFirstPhase(frames, "Initialization");
        assertLastPhase(frames, "Complete");
    }

    // ── DFS ──────────────────────────────────────────────────

    @Test
    void dfsFramesHaveTelemetry() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 1);

        List<AlgorithmFrame> frames = DfsRunner.run(g, "A");
        assertAllFramesHaveTelemetry(frames);
        assertFirstPhase(frames, "Initialization");
        assertLastPhase(frames, "Complete");
    }

    // ── Prim ─────────────────────────────────────────────────

    @Test
    void primFramesHaveTelemetry() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 2);
        g.addEdge("A", "C", 3);

        List<AlgorithmFrame> frames = PrimRunner.run(g, "A");
        assertAllFramesHaveTelemetry(frames);
        assertFirstPhase(frames, "Initialization");
        assertLastPhase(frames, "Complete");
        assertTrue(frames.stream().anyMatch(f -> "Extract-Min".equals(f.telemetry().phase())));
    }

    // ── Kruskal ──────────────────────────────────────────────

    @Test
    void kruskalFramesHaveTelemetry() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 2);
        g.addEdge("A", "C", 3);

        List<AlgorithmFrame> frames = KruskalRunner.run(g);
        assertAllFramesHaveTelemetry(frames);
        assertFirstPhase(frames, "Initialization");
        assertLastPhase(frames, "Complete");
    }

    // ── Bellman–Ford ─────────────────────────────────────────

    @Test
    void bellmanFordFramesHaveTelemetry() {
        Graph g = new Graph(true);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 2);

        List<AlgorithmFrame> frames = BellmanFordRunner.run(g, "A", "C");
        assertAllFramesHaveTelemetry(frames);
        assertFirstPhase(frames, "Initialization");
        assertLastPhase(frames, "Complete");
    }

    // ── A* ───────────────────────────────────────────────────

    @Test
    void astarFramesHaveTelemetry() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 1);

        Map<String, double[]> pos = Map.of(
                "A", new double[]{0, 0},
                "B", new double[]{1, 0},
                "C", new double[]{2, 0});

        List<AlgorithmFrame> frames = AStarRunner.run(g, "A", "C", pos);
        assertAllFramesHaveTelemetry(frames);
        assertFirstPhase(frames, "Initialization");
        assertLastPhase(frames, "Complete");
    }

    // ── Topological Sort ─────────────────────────────────────

    @Test
    void topoSortFramesHaveTelemetry() {
        Graph g = new Graph(true);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 1);

        List<AlgorithmFrame> frames = TopologicalSortRunner.run(g);
        assertAllFramesHaveTelemetry(frames);
        assertFirstPhase(frames, "Initialization");
        assertLastPhase(frames, "Complete");
    }

    // ── SCC (Kosaraju) ───────────────────────────────────────

    @Test
    void sccFramesHaveTelemetry() {
        Graph g = new Graph(true);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "A", 1);
        g.addEdge("B", "C", 1);

        List<AlgorithmFrame> frames = SCCRunner.run(g);
        assertAllFramesHaveTelemetry(frames);
        assertFirstPhase(frames, "Initialization");
        assertLastPhase(frames, "Complete");
        assertTrue(frames.stream().anyMatch(f -> "SCC-Found".equals(f.telemetry().phase())));
    }

    // ── Bridges ──────────────────────────────────────────────

    @Test
    void bridgesFramesHaveTelemetry() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 1);

        List<AlgorithmFrame> frames = BridgesRunner.run(g);
        assertAllFramesHaveTelemetry(frames);
        assertFirstPhase(frames, "Initialization");
        assertLastPhase(frames, "Complete");
    }

    // ── Articulation Points ──────────────────────────────────

    @Test
    void articulationPointsFramesHaveTelemetry() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1);
        g.addEdge("B", "C", 1);

        List<AlgorithmFrame> frames = ArticulationPointsRunner.run(g);
        assertAllFramesHaveTelemetry(frames);
        assertFirstPhase(frames, "Initialization");
        assertLastPhase(frames, "Complete");
    }

    // ── Helpers ──────────────────────────────────────────────

    private void assertAllFramesHaveTelemetry(List<AlgorithmFrame> frames) {
        assertFalse(frames.isEmpty(), "Expected at least one frame");
        for (int i = 0; i < frames.size(); i++) {
            AlgorithmTelemetry t = frames.get(i).telemetry();
            assertNotNull(t, "Frame " + i + " has null telemetry");
            assertNotNull(t.phase(), "Frame " + i + " has null phase");
            assertFalse(t.phase().isBlank(), "Frame " + i + " has blank phase");
        }
    }

    private void assertFirstPhase(List<AlgorithmFrame> frames, String expected) {
        assertEquals(expected, frames.get(0).telemetry().phase(),
                "First frame should have phase '" + expected + "'");
    }

    private void assertLastPhase(List<AlgorithmFrame> frames, String expected) {
        assertEquals(expected, frames.get(frames.size() - 1).telemetry().phase(),
                "Last frame should have phase '" + expected + "'");
    }
}
