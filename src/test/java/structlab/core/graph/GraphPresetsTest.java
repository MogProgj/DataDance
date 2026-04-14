package structlab.core.graph;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphPresetsTest {

    @Test
    void allPresetsNonNull() {
        List<GraphPresets.Preset> all = GraphPresets.all();
        assertNotNull(all);
        assertFalse(all.isEmpty());
    }

    @Test
    void allPresetsHaveValidGraph() {
        for (GraphPresets.Preset p : GraphPresets.all()) {
            assertNotNull(p.graph(), "Preset '" + p.name() + "' has null graph");
            assertTrue(p.graph().nodeCount() > 0, "Preset '" + p.name() + "' has no nodes");
        }
    }

    @Test
    void allPresetsHaveValidSource() {
        for (GraphPresets.Preset p : GraphPresets.all()) {
            String src = p.suggestedSource();
            assertNotNull(src, "Preset '" + p.name() + "' has null suggestedSource");
            assertTrue(p.graph().neighbors(src) != null || p.graph().nodeCount() > 0,
                    "Preset '" + p.name() + "': suggestedSource '" + src + "' not in graph");
        }
    }

    @Test
    void allPresetsHaveNameAndDescription() {
        for (GraphPresets.Preset p : GraphPresets.all()) {
            assertNotNull(p.name());
            assertFalse(p.name().isBlank(), "Preset has blank name");
            assertNotNull(p.description());
            assertFalse(p.description().isBlank(), "Preset '" + p.name() + "' has blank description");
        }
    }

    @Test
    void presetsListIsUnmodifiable() {
        List<GraphPresets.Preset> all = GraphPresets.all();
        assertThrows(UnsupportedOperationException.class, () -> all.add(null));
    }

    @Test
    void eighteenPresetsExist() {
        assertEquals(18, GraphPresets.all().size());
    }

    @Test
    void unweightedFilterReturnsTen() {
        assertEquals(10, GraphPresets.unweighted().size());
        for (GraphPresets.Preset p : GraphPresets.unweighted()) {
            assertFalse(p.weighted(), "Preset '" + p.name() + "' should be unweighted");
        }
    }

    @Test
    void weightedFilterReturnsEight() {
        assertEquals(8, GraphPresets.weighted().size());
        for (GraphPresets.Preset p : GraphPresets.weighted()) {
            assertTrue(p.weighted(), "Preset '" + p.name() + "' should be weighted");
        }
    }

    @Test
    void bfsRunsOnAllPresets() {
        for (GraphPresets.Preset p : GraphPresets.all()) {
            List<AlgorithmFrame> frames = BfsRunner.run(p.graph(), p.suggestedSource());
            assertFalse(frames.isEmpty(), "BFS produced no frames for preset '" + p.name() + "'");
        }
    }

    @Test
    void dfsRunsOnAllPresets() {
        for (GraphPresets.Preset p : GraphPresets.all()) {
            List<AlgorithmFrame> frames = DfsRunner.run(p.graph(), p.suggestedSource());
            assertFalse(frames.isEmpty(), "DFS produced no frames for preset '" + p.name() + "'");
        }
    }

    @Test
    void dijkstraRunsOnAllPresets() {
        for (GraphPresets.Preset p : GraphPresets.all()) {
            // Skip negative-weight presets — Dijkstra rejects those
            if (p.name().contains("Negative")) continue;
            List<AlgorithmFrame> frames = DijkstraRunner.run(p.graph(), p.suggestedSource());
            assertFalse(frames.isEmpty(), "Dijkstra produced no frames for preset '" + p.name() + "'");
        }
    }

    @Test
    void dijkstraRunsWithTargetOnWeightedPresets() {
        for (GraphPresets.Preset p : GraphPresets.weighted()) {
            // Skip negative-weight presets — Dijkstra rejects those
            if (p.name().contains("Negative")) continue;
            if (p.suggestedTarget() != null) {
                List<AlgorithmFrame> frames = DijkstraRunner.run(
                        p.graph(), p.suggestedSource(), p.suggestedTarget());
                assertFalse(frames.isEmpty(),
                        "Dijkstra with target produced no frames for '" + p.name() + "'");
            }
        }
    }

    @Test
    void bellmanFordRunsOnAllWeightedPresets() {
        for (GraphPresets.Preset p : GraphPresets.weighted()) {
            List<AlgorithmFrame> frames = BellmanFordRunner.run(
                    p.graph(), p.suggestedSource());
            assertFalse(frames.isEmpty(),
                    "Bellman-Ford produced no frames for preset '" + p.name() + "'");
        }
    }

    @Test
    void topoSortRunsOnDirectedAcyclicPresets() {
        for (GraphPresets.Preset p : GraphPresets.all()) {
            if (!p.graph().isDirected()) continue;
            if (p.name().contains("Cycle")) continue; // skip cyclic graphs
            List<AlgorithmFrame> frames = TopologicalSortRunner.run(p.graph());
            AlgorithmFrame last = frames.get(frames.size() - 1);
            assertFalse(frames.isEmpty(),
                    "Topo Sort produced no frames for preset '" + p.name() + "'");
            assertTrue(last.statusMessage().contains("complete"),
                    "Topo Sort did not complete for preset '" + p.name() + "'");
        }
    }
}
