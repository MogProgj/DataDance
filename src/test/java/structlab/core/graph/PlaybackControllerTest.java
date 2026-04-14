package structlab.core.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlaybackControllerTest {

    @Test
    void emptyController() {
        PlaybackController pc = new PlaybackController();
        assertFalse(pc.isLoaded());
        assertNull(pc.current());
        assertEquals(-1, pc.currentIndex());
        assertEquals(0, pc.frameCount());
    }

    @Test
    void loadAndNavigate() {
        PlaybackController pc = new PlaybackController();
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("B", "C");

        var frames = BfsRunner.run(g, "A");
        pc.load(frames);

        assertTrue(pc.isLoaded());
        assertEquals(0, pc.currentIndex());
        assertNotNull(pc.current());
        assertEquals(frames.size(), pc.frameCount());
    }

    @Test
    void nextAndPrevious() {
        PlaybackController pc = new PlaybackController();
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        var frames = BfsRunner.run(g, "A");
        pc.load(frames);

        assertTrue(pc.hasNext());
        assertFalse(pc.hasPrevious());

        assertTrue(pc.next());
        assertEquals(1, pc.currentIndex());
        assertTrue(pc.hasPrevious());

        assertTrue(pc.previous());
        assertEquals(0, pc.currentIndex());
        assertFalse(pc.hasPrevious());
    }

    @Test
    void nextAtEndReturnsFalse() {
        PlaybackController pc = new PlaybackController();
        Graph g = new Graph(false);
        g.addNode("X");
        var frames = BfsRunner.run(g, "X");
        pc.load(frames);

        // Navigate to end
        while (pc.hasNext()) pc.next();
        assertFalse(pc.next());
    }

    @Test
    void previousAtStartReturnsFalse() {
        PlaybackController pc = new PlaybackController();
        Graph g = new Graph(false);
        g.addNode("X");
        var frames = BfsRunner.run(g, "X");
        pc.load(frames);

        assertFalse(pc.previous());
    }

    @Test
    void reset() {
        PlaybackController pc = new PlaybackController();
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        var frames = BfsRunner.run(g, "A");
        pc.load(frames);

        pc.next();
        pc.next();
        assertTrue(pc.currentIndex() > 0);

        pc.reset();
        assertEquals(0, pc.currentIndex());
    }

    @Test
    void jumpToEnd() {
        PlaybackController pc = new PlaybackController();
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("B", "C");
        var frames = BfsRunner.run(g, "A");
        pc.load(frames);

        pc.jumpToEnd();
        assertEquals(frames.size() - 1, pc.currentIndex());
        assertFalse(pc.hasNext());
    }

    @Test
    void clearResetsEverything() {
        PlaybackController pc = new PlaybackController();
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        pc.load(BfsRunner.run(g, "A"));
        assertTrue(pc.isLoaded());

        pc.clear();
        assertFalse(pc.isLoaded());
        assertNull(pc.current());
        assertEquals(-1, pc.currentIndex());
    }

    @Test
    void jumpToInRange() {
        PlaybackController pc = new PlaybackController();
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        g.addEdge("B", "C");
        var frames = BfsRunner.run(g, "A");
        pc.load(frames);

        assertTrue(pc.jumpTo(2));
        assertEquals(2, pc.currentIndex());
        assertNotNull(pc.current());
    }

    @Test
    void jumpToClampedHigh() {
        PlaybackController pc = new PlaybackController();
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        var frames = BfsRunner.run(g, "A");
        pc.load(frames);

        assertFalse(pc.jumpTo(9999));
        assertEquals(frames.size() - 1, pc.currentIndex());
    }

    @Test
    void jumpToClampedLow() {
        PlaybackController pc = new PlaybackController();
        Graph g = new Graph(false);
        g.addEdge("A", "B");
        var frames = BfsRunner.run(g, "A");
        pc.load(frames);
        pc.jumpToEnd();

        assertFalse(pc.jumpTo(-5));
        assertEquals(0, pc.currentIndex());
    }

    @Test
    void jumpToOnEmptyReturnsFalse() {
        PlaybackController pc = new PlaybackController();
        assertFalse(pc.jumpTo(0));
        assertEquals(-1, pc.currentIndex());
    }
}
