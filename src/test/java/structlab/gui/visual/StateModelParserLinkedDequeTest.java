package structlab.gui.visual;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StateModelParserLinkedDequeTest {

    // ── SinglyLinkedList parsing ────────────────────────────

    @Test
    void parseSinglyLinkedListEmpty() {
        String snap = "SinglyLinkedList{size=0, head=null, tail=null, chain=[]}";
        SinglyLinkedListStateModel model = StateModelParser.parseSinglyLinkedList(snap);
        assertEquals(0, model.size());
        assertEquals("null", model.head());
        assertEquals("null", model.tail());
        assertTrue(model.nodes().isEmpty());
        assertTrue(model.isEmpty());
    }

    @Test
    void parseSinglyLinkedListPopulated() {
        String snap = "SinglyLinkedList{size=3, head=10, tail=30, chain=[10 -> 20 -> 30]}";
        SinglyLinkedListStateModel model = StateModelParser.parseSinglyLinkedList(snap);
        assertEquals(3, model.size());
        assertEquals("10", model.head());
        assertEquals("30", model.tail());
        assertEquals(3, model.nodes().size());
        assertEquals("10", model.nodes().get(0));
        assertEquals("20", model.nodes().get(1));
        assertEquals("30", model.nodes().get(2));
        assertFalse(model.isEmpty());
    }

    @Test
    void parseSinglyLinkedListSingleElement() {
        String snap = "SinglyLinkedList{size=1, head=42, tail=42, chain=[42]}";
        SinglyLinkedListStateModel model = StateModelParser.parseSinglyLinkedList(snap);
        assertEquals(1, model.size());
        assertEquals("42", model.head());
        assertEquals("42", model.tail());
        assertEquals(1, model.nodes().size());
    }

    // ── DoublyLinkedList parsing ────────────────────────────

    @Test
    void parseDoublyLinkedListEmpty() {
        String snap = "DoublyLinkedList{size=0, head=null, tail=null, chain=[]}";
        DoublyLinkedListStateModel model = StateModelParser.parseDoublyLinkedList(snap);
        assertEquals(0, model.size());
        assertEquals("null", model.head());
        assertEquals("null", model.tail());
        assertTrue(model.nodes().isEmpty());
        assertTrue(model.isEmpty());
    }

    @Test
    void parseDoublyLinkedListPopulated() {
        String snap = "DoublyLinkedList{size=3, head=A, tail=C, chain=[A <-> B <-> C]}";
        DoublyLinkedListStateModel model = StateModelParser.parseDoublyLinkedList(snap);
        assertEquals(3, model.size());
        assertEquals("A", model.head());
        assertEquals("C", model.tail());
        assertEquals(3, model.nodes().size());
        assertEquals("A", model.nodes().get(0));
        assertEquals("B", model.nodes().get(1));
        assertEquals("C", model.nodes().get(2));
    }

    // ── ArrayDequeCustom parsing ────────────────────────────

    @Test
    void parseArrayDequeCustomEmpty() {
        String snap = "ArrayDequeCustom{size=0, capacity=4, frontIndex=0, logical=[], raw=[null, null, null, null]}";
        ArrayDequeStateModel model = StateModelParser.parseArrayDequeCustom(snap);
        assertEquals(0, model.size());
        assertEquals(4, model.capacity());
        assertEquals(0, model.frontIndex());
        assertTrue(model.logical().isEmpty());
        assertEquals(4, model.raw().size());
        assertTrue(model.isEmpty());
    }

    @Test
    void parseArrayDequeCustomPopulated() {
        String snap = "ArrayDequeCustom{size=3, capacity=8, frontIndex=2, logical=[10, 20, 30], raw=[null, null, 10, 20, 30, null, null, null]}";
        ArrayDequeStateModel model = StateModelParser.parseArrayDequeCustom(snap);
        assertEquals(3, model.size());
        assertEquals(8, model.capacity());
        assertEquals(2, model.frontIndex());
        assertEquals(3, model.logical().size());
        assertEquals("10", model.logical().get(0));
        assertEquals(8, model.raw().size());
        assertEquals(5, model.rearIndex()); // (2+3) % 8 = 5
    }

    @Test
    void parseArrayDequeCustomWrapped() {
        String snap = "ArrayDequeCustom{size=3, capacity=4, frontIndex=3, logical=[5, 6, 7], raw=[6, 7, null, 5]}";
        ArrayDequeStateModel model = StateModelParser.parseArrayDequeCustom(snap);
        assertEquals(3, model.size());
        assertEquals(4, model.capacity());
        assertEquals(3, model.frontIndex());
        assertEquals(2, model.rearIndex()); // (3+3) % 4 = 2
    }

    // ── LinkedDeque parsing ─────────────────────────────────

    @Test
    void parseLinkedDequeEmpty() {
        String snap = "LinkedDeque{size=0, front=null, rear=null, chain=[]}";
        LinkedDequeStateModel model = StateModelParser.parseLinkedDeque(snap);
        assertEquals(0, model.size());
        assertEquals("null", model.front());
        assertEquals("null", model.rear());
        assertTrue(model.nodes().isEmpty());
        assertTrue(model.isEmpty());
    }

    @Test
    void parseLinkedDequePopulated() {
        String snap = "LinkedDeque{size=3, front=X, rear=Z, chain=[X <-> Y <-> Z]}";
        LinkedDequeStateModel model = StateModelParser.parseLinkedDeque(snap);
        assertEquals(3, model.size());
        assertEquals("X", model.front());
        assertEquals("Z", model.rear());
        assertEquals(3, model.nodes().size());
        assertEquals("X", model.nodes().get(0));
        assertEquals("Y", model.nodes().get(1));
        assertEquals("Z", model.nodes().get(2));
    }
}
