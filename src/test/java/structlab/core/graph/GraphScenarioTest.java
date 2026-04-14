package structlab.core.graph;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GraphScenarioTest {

    @Test
    void saveAndLoadRoundTrip(@TempDir Path tempDir) throws IOException {
        Graph g = new Graph(true);
        g.addEdge("A", "B", 3.5);
        g.addEdge("B", "C", 1.0);

        Map<String, double[]> positions = new LinkedHashMap<>();
        positions.put("A", new double[]{100.0, 200.0});
        positions.put("B", new double[]{300.0, 200.0});
        positions.put("C", new double[]{200.0, 400.0});

        GraphScenario original = new GraphScenario(
                "Test Graph", g, true, positions,
                "Dijkstra", "A", "C");

        Path file = tempDir.resolve("test.dds");
        original.saveTo(file);

        GraphScenario loaded = GraphScenario.loadFrom(file);

        assertEquals("Test Graph", loaded.name());
        assertTrue(loaded.graph().isDirected());
        assertTrue(loaded.weighted());
        assertEquals("Dijkstra", loaded.algorithm());
        assertEquals("A", loaded.source());
        assertEquals("C", loaded.target());
        assertEquals(3, loaded.graph().nodeCount());
        assertEquals(3.5, loaded.graph().edgeWeight("A", "B").getAsDouble(), 0.001);
    }

    @Test
    void positionsPreserved(@TempDir Path tempDir) throws IOException {
        Graph g = new Graph(false);
        g.addEdge("X", "Y", 1);

        Map<String, double[]> positions = new LinkedHashMap<>();
        positions.put("X", new double[]{42.5, 99.3});
        positions.put("Y", new double[]{150.0, 250.0});

        GraphScenario sc = new GraphScenario("Pos Test", g, false,
                positions, null, null, null);
        Path file = tempDir.resolve("pos.dds");
        sc.saveTo(file);

        GraphScenario loaded = GraphScenario.loadFrom(file);
        Map<String, double[]> loadedPos = loaded.nodePositions();
        assertArrayEquals(new double[]{42.5, 99.3}, loadedPos.get("X"), 0.01);
        assertArrayEquals(new double[]{150.0, 250.0}, loadedPos.get("Y"), 0.01);
    }

    @Test
    void nullFieldsHandled(@TempDir Path tempDir) throws IOException {
        Graph g = new Graph(false);
        g.addNode("A");

        GraphScenario sc = new GraphScenario(null, g, false,
                null, null, null, null);
        Path file = tempDir.resolve("null.dds");
        sc.saveTo(file);

        GraphScenario loaded = GraphScenario.loadFrom(file);
        assertEquals("Untitled", loaded.name());
        assertNull(loaded.algorithm());
        assertNull(loaded.source());
        assertNull(loaded.target());
    }

    @Test
    void invalidFileThrows(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("bad.dds");
        java.nio.file.Files.writeString(file, "this is not a valid scenario");

        assertThrows(IOException.class, () -> GraphScenario.loadFrom(file));
    }

    @Test
    void emptyFileThrows(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("empty.dds");
        java.nio.file.Files.writeString(file, "");

        assertThrows(IOException.class, () -> GraphScenario.loadFrom(file));
    }

    @Test
    void undirectedGraphEdges(@TempDir Path tempDir) throws IOException {
        Graph g = new Graph(false);
        g.addEdge("A", "B", 5.0);

        GraphScenario sc = new GraphScenario("Undirected", g, true,
                null, "BFS", "A", null);
        Path file = tempDir.resolve("undirected.dds");
        sc.saveTo(file);

        GraphScenario loaded = GraphScenario.loadFrom(file);
        assertFalse(loaded.graph().isDirected());
        assertEquals("BFS", loaded.algorithm());
        assertEquals("A", loaded.source());
    }

    @Test
    void accessors() {
        Graph g = new Graph(true);
        g.addEdge("A", "B", 1);
        Map<String, double[]> pos = Map.of("A", new double[]{0, 0});

        GraphScenario sc = new GraphScenario("N", g, true, pos, "DFS", "A", "B");
        assertEquals("N", sc.name());
        assertSame(g, sc.graph());
        assertTrue(sc.weighted());
        assertEquals("DFS", sc.algorithm());
        assertEquals("A", sc.source());
        assertEquals("B", sc.target());
        // nodePositions is unmodifiable copy
        assertThrows(UnsupportedOperationException.class,
                () -> sc.nodePositions().put("Z", new double[]{0, 0}));
    }
}
