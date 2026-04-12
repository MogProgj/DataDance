package structlab.core.graph;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UnionFindTest {

    @Test
    void initiallyAllSeparate() {
        UnionFind uf = new UnionFind(List.of("A", "B", "C"));
        assertEquals(3, uf.componentCount());
        assertFalse(uf.connected("A", "B"));
        assertFalse(uf.connected("B", "C"));
    }

    @Test
    void unionMergesSets() {
        UnionFind uf = new UnionFind(List.of("A", "B", "C"));
        assertTrue(uf.union("A", "B"));
        assertTrue(uf.connected("A", "B"));
        assertEquals(2, uf.componentCount());
    }

    @Test
    void unionSameSetReturnsFalse() {
        UnionFind uf = new UnionFind(List.of("A", "B"));
        uf.union("A", "B");
        assertFalse(uf.union("A", "B"));
        assertEquals(1, uf.componentCount());
    }

    @Test
    void transitiveConnectivity() {
        UnionFind uf = new UnionFind(List.of("A", "B", "C", "D"));
        uf.union("A", "B");
        uf.union("C", "D");
        assertFalse(uf.connected("A", "C"));
        uf.union("B", "C");
        assertTrue(uf.connected("A", "D"));
        assertEquals(1, uf.componentCount());
    }

    @Test
    void findWithPathCompression() {
        UnionFind uf = new UnionFind(List.of("A", "B", "C", "D", "E"));
        uf.union("A", "B");
        uf.union("B", "C");
        uf.union("C", "D");
        uf.union("D", "E");
        // find("E") should compress path
        String root = uf.find("E");
        assertNotNull(root);
        assertEquals(root, uf.find("A"));
        assertEquals(1, uf.componentCount());
    }

    @Test
    void singleElement() {
        UnionFind uf = new UnionFind(List.of("X"));
        assertEquals(1, uf.componentCount());
        assertEquals("X", uf.find("X"));
        assertTrue(uf.connected("X", "X"));
    }
}
