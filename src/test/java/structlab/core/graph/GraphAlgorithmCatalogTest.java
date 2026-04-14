package structlab.core.graph;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GraphAlgorithmCatalogTest {

    // ── Catalog structure ────────────────────────────────────

    @Test
    void allReturnsElevenSpecs() {
        assertEquals(11, GraphAlgorithmCatalog.all().size());
    }

    @Test
    void displayLabelsMatchSpecOrder() {
        List<String> labels = GraphAlgorithmCatalog.displayLabels();
        List<GraphAlgorithmSpec> specs = GraphAlgorithmCatalog.all();
        assertEquals(specs.size(), labels.size());
        for (int i = 0; i < specs.size(); i++) {
            assertEquals(specs.get(i).displayLabel(), labels.get(i));
        }
    }

    @Test
    void byIdCoversEveryEnumValue() {
        for (GraphAlgorithmId id : GraphAlgorithmId.values()) {
            GraphAlgorithmSpec spec = GraphAlgorithmCatalog.byId(id);
            assertNotNull(spec, "Missing spec for " + id);
            assertEquals(id, spec.id());
        }
    }

    @Test
    void byLabelReturnsCorrectSpec() {
        GraphAlgorithmSpec bfs = GraphAlgorithmCatalog.byLabel("BFS");
        assertNotNull(bfs);
        assertEquals(GraphAlgorithmId.BFS, bfs.id());
    }

    @Test
    void byLabelReturnsNullForUnknown() {
        assertNull(GraphAlgorithmCatalog.byLabel("NonExistentAlgo"));
    }

    // ── Metadata correctness ─────────────────────────────────

    @Test
    void topoSortRequiresNoSourceAndDirectedOnly() {
        GraphAlgorithmSpec topo = GraphAlgorithmCatalog.byId(GraphAlgorithmId.TOPOLOGICAL_SORT);
        assertFalse(topo.sourceRequired());
        assertTrue(topo.directedOk());
        assertFalse(topo.undirectedOk());
    }

    @Test
    void kruskalRequiresNoSourceAndUndirectedOnly() {
        GraphAlgorithmSpec kruskal = GraphAlgorithmCatalog.byId(GraphAlgorithmId.KRUSKAL);
        assertFalse(kruskal.sourceRequired());
        assertFalse(kruskal.directedOk());
        assertTrue(kruskal.undirectedOk());
    }

    @Test
    void aStarRequiresTarget() {
        GraphAlgorithmSpec aStar = GraphAlgorithmCatalog.byId(GraphAlgorithmId.A_STAR);
        assertEquals(GraphAlgorithmSpec.TargetMode.REQUIRED, aStar.targetMode());
        assertTrue(aStar.sourceRequired());
    }

    @Test
    void dijkstraHasOptionalTarget() {
        GraphAlgorithmSpec dij = GraphAlgorithmCatalog.byId(GraphAlgorithmId.DIJKSTRA);
        assertEquals(GraphAlgorithmSpec.TargetMode.OPTIONAL, dij.targetMode());
    }

    @Test
    void sccDirectedOnly() {
        GraphAlgorithmSpec scc = GraphAlgorithmCatalog.byId(GraphAlgorithmId.SCC);
        assertTrue(scc.directedOk());
        assertFalse(scc.undirectedOk());
        assertEquals(GraphAlgorithmSpec.Category.CONNECTIVITY, scc.category());
    }

    @Test
    void bridgesUndirectedOnly() {
        GraphAlgorithmSpec br = GraphAlgorithmCatalog.byId(GraphAlgorithmId.BRIDGES);
        assertFalse(br.directedOk());
        assertTrue(br.undirectedOk());
    }

    // ── supportsGraph ────────────────────────────────────────

    @Test
    void supportsGraphDirected() {
        Graph directed = new Graph(true);
        directed.addEdge("A", "B");

        assertTrue(GraphAlgorithmCatalog.byId(GraphAlgorithmId.BFS).supportsGraph(directed));
        assertTrue(GraphAlgorithmCatalog.byId(GraphAlgorithmId.SCC).supportsGraph(directed));
        assertFalse(GraphAlgorithmCatalog.byId(GraphAlgorithmId.KRUSKAL).supportsGraph(directed));
        assertFalse(GraphAlgorithmCatalog.byId(GraphAlgorithmId.BRIDGES).supportsGraph(directed));
    }

    @Test
    void supportsGraphUndirected() {
        Graph undirected = new Graph(false);
        undirected.addEdge("A", "B");

        assertTrue(GraphAlgorithmCatalog.byId(GraphAlgorithmId.BFS).supportsGraph(undirected));
        assertTrue(GraphAlgorithmCatalog.byId(GraphAlgorithmId.KRUSKAL).supportsGraph(undirected));
        assertFalse(GraphAlgorithmCatalog.byId(GraphAlgorithmId.SCC).supportsGraph(undirected));
        assertFalse(GraphAlgorithmCatalog.byId(GraphAlgorithmId.TOPOLOGICAL_SORT).supportsGraph(undirected));
    }

    // ── Dispatch ─────────────────────────────────────────────

    @Test
    void runBfsDispatch() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("B", "C");
        GraphAlgorithmSpec spec = GraphAlgorithmCatalog.byId(GraphAlgorithmId.BFS);
        List<AlgorithmFrame> frames = GraphAlgorithmCatalog.run(spec, g, "A", null, Map.of());
        assertNotNull(frames);
        assertFalse(frames.isEmpty());
        assertEquals(AlgorithmFrame.AlgorithmType.BFS, frames.get(0).algorithm());
    }

    @Test
    void runDfsDispatch() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        GraphAlgorithmSpec spec = GraphAlgorithmCatalog.byId(GraphAlgorithmId.DFS);
        List<AlgorithmFrame> frames = GraphAlgorithmCatalog.run(spec, g, "A", null, Map.of());
        assertNotNull(frames);
        assertFalse(frames.isEmpty());
        assertEquals(AlgorithmFrame.AlgorithmType.DFS, frames.get(0).algorithm());
    }

    @Test
    void runDijkstraDispatch() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 2.0);
        g.addEdge("B", "C", 3.0);
        GraphAlgorithmSpec spec = GraphAlgorithmCatalog.byId(GraphAlgorithmId.DIJKSTRA);
        List<AlgorithmFrame> frames = GraphAlgorithmCatalog.run(spec, g, "A", "C", Map.of());
        assertNotNull(frames);
        assertFalse(frames.isEmpty());
    }

    @Test
    void runTopoSortDispatch() {
        Graph g = new Graph(true);
        g.addEdge("A", "B");
        g.addEdge("B", "C");
        GraphAlgorithmSpec spec = GraphAlgorithmCatalog.byId(GraphAlgorithmId.TOPOLOGICAL_SORT);
        List<AlgorithmFrame> frames = GraphAlgorithmCatalog.run(spec, g, null, null, Map.of());
        assertNotNull(frames);
        assertFalse(frames.isEmpty());
    }

    @Test
    void runKruskalDispatch() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1.0);
        g.addEdge("B", "C", 2.0);
        GraphAlgorithmSpec spec = GraphAlgorithmCatalog.byId(GraphAlgorithmId.KRUSKAL);
        List<AlgorithmFrame> frames = GraphAlgorithmCatalog.run(spec, g, null, null, Map.of());
        assertNotNull(frames);
        assertFalse(frames.isEmpty());
    }

    @Test
    void runPrimDispatch() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1.0);
        g.addEdge("B", "C", 2.0);
        GraphAlgorithmSpec spec = GraphAlgorithmCatalog.byId(GraphAlgorithmId.PRIM);
        List<AlgorithmFrame> frames = GraphAlgorithmCatalog.run(spec, g, "A", null, Map.of());
        assertNotNull(frames);
        assertFalse(frames.isEmpty());
    }

    @Test
    void runSccDispatch() {
        Graph g = new Graph(true);
        g.addEdge("A", "B");
        g.addEdge("B", "A");
        GraphAlgorithmSpec spec = GraphAlgorithmCatalog.byId(GraphAlgorithmId.SCC);
        List<AlgorithmFrame> frames = GraphAlgorithmCatalog.run(spec, g, null, null, Map.of());
        assertNotNull(frames);
        assertFalse(frames.isEmpty());
    }

    @Test
    void runBridgesDispatch() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("B", "C");
        GraphAlgorithmSpec spec = GraphAlgorithmCatalog.byId(GraphAlgorithmId.BRIDGES);
        List<AlgorithmFrame> frames = GraphAlgorithmCatalog.run(spec, g, null, null, Map.of());
        assertNotNull(frames);
        assertFalse(frames.isEmpty());
    }

    @Test
    void runArticulationPointsDispatch() {
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("B", "C");
        GraphAlgorithmSpec spec = GraphAlgorithmCatalog.byId(GraphAlgorithmId.ARTICULATION_POINTS);
        List<AlgorithmFrame> frames = GraphAlgorithmCatalog.run(spec, g, null, null, Map.of());
        assertNotNull(frames);
        assertFalse(frames.isEmpty());
    }

    @Test
    void runBellmanFordDispatch() {
        Graph g = new Graph(true);
        g.addEdge("A", "B", 4.0);
        g.addEdge("B", "C", -1.0);
        GraphAlgorithmSpec spec = GraphAlgorithmCatalog.byId(GraphAlgorithmId.BELLMAN_FORD);
        List<AlgorithmFrame> frames = GraphAlgorithmCatalog.run(spec, g, "A", "C", Map.of());
        assertNotNull(frames);
        assertFalse(frames.isEmpty());
    }

    @Test
    void runAStarDispatch() {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 1.0);
        g.addEdge("B", "C", 1.0);
        Map<String, double[]> positions = Map.of(
                "A", new double[]{0, 0},
                "B", new double[]{1, 0},
                "C", new double[]{2, 0});
        GraphAlgorithmSpec spec = GraphAlgorithmCatalog.byId(GraphAlgorithmId.A_STAR);
        List<AlgorithmFrame> frames = GraphAlgorithmCatalog.run(spec, g, "A", "C", positions);
        assertNotNull(frames);
        assertFalse(frames.isEmpty());
    }

    // ── All specs have non-null, non-empty metadata ─────────

    @Test
    void allSpecsHaveValidMetadata() {
        for (GraphAlgorithmSpec spec : GraphAlgorithmCatalog.all()) {
            assertNotNull(spec.id(), "null id");
            assertNotNull(spec.displayLabel(), "null displayLabel");
            assertFalse(spec.displayLabel().isBlank(), "blank displayLabel for " + spec.id());
            assertNotNull(spec.category(), "null category for " + spec.id());
            assertNotNull(spec.targetMode(), "null targetMode for " + spec.id());
            assertNotNull(spec.hint(), "null hint for " + spec.id());
            assertFalse(spec.hint().isBlank(), "blank hint for " + spec.id());
            assertNotNull(spec.frameType(), "null frameType for " + spec.id());
            // At least one graph type must be supported
            assertTrue(spec.directedOk() || spec.undirectedOk(),
                    spec.id() + " supports neither directed nor undirected");
        }
    }
}
