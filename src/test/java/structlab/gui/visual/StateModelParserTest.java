package structlab.gui.visual;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StateModelParserTest {

    // ── structureType ───────────────────────────────────────

    @Test
    void structureTypeExtractsArrayStack() {
        assertEquals("ArrayStack",
                StateModelParser.structureType("ArrayStack{size=0, top=null, elements=DynamicArray{size=0, capacity=4, elements=[], raw=[null, null, null, null]}}"));
    }

    @Test
    void structureTypeExtractsLinkedStack() {
        assertEquals("LinkedStack",
                StateModelParser.structureType("LinkedStack{size=0, top=null, chain=[]}"));
    }

    @Test
    void structureTypeExtractsCircularArrayQueue() {
        assertEquals("CircularArrayQueue",
                StateModelParser.structureType("CircularArrayQueue{size=0, capacity=4, frontIndex=0, logical=[], raw=[null, null, null, null]}"));
    }

    // ── parseArrayStack ─────────────────────────────────────

    @Test
    void parseArrayStackEmpty() {
        String snap = "ArrayStack{size=0, top=null, elements=DynamicArray{size=0, capacity=4, elements=[], raw=[null, null, null, null]}}";
        StackStateModel model = StateModelParser.parseArrayStack(snap);
        assertEquals(0, model.size());
        assertEquals("null", model.topValue());
        assertTrue(model.elements().isEmpty());
        assertTrue(model.isEmpty());
    }

    @Test
    void parseArrayStackWithElements() {
        String snap = "ArrayStack{size=3, top=30, elements=DynamicArray{size=3, capacity=4, elements=[10, 20, 30], raw=[10, 20, 30, null]}}";
        StackStateModel model = StateModelParser.parseArrayStack(snap);
        assertEquals(3, model.size());
        assertEquals("30", model.topValue());
        assertEquals(List.of("10", "20", "30"), model.elements());
        assertFalse(model.isEmpty());
    }

    @Test
    void parseArrayStackSingleElement() {
        String snap = "ArrayStack{size=1, top=42, elements=DynamicArray{size=1, capacity=4, elements=[42], raw=[42, null, null, null]}}";
        StackStateModel model = StateModelParser.parseArrayStack(snap);
        assertEquals(1, model.size());
        assertEquals("42", model.topValue());
        assertEquals(List.of("42"), model.elements());
    }

    // ── parseLinkedStack ────────────────────────────────────

    @Test
    void parseLinkedStackEmpty() {
        String snap = "LinkedStack{size=0, top=null, chain=[]}";
        StackStateModel model = StateModelParser.parseLinkedStack(snap);
        assertEquals(0, model.size());
        assertEquals("null", model.topValue());
        assertTrue(model.elements().isEmpty());
        assertTrue(model.isEmpty());
    }

    @Test
    void parseLinkedStackWithElements() {
        String snap = "LinkedStack{size=3, top=30, chain=[30 -> 20 -> 10]}";
        StackStateModel model = StateModelParser.parseLinkedStack(snap);
        assertEquals(3, model.size());
        assertEquals("30", model.topValue());
        // Elements should be bottom-to-top for visual layout
        assertEquals(List.of("10", "20", "30"), model.elements());
    }

    @Test
    void parseLinkedStackSingleElement() {
        String snap = "LinkedStack{size=1, top=99, chain=[99]}";
        StackStateModel model = StateModelParser.parseLinkedStack(snap);
        assertEquals(1, model.size());
        assertEquals(List.of("99"), model.elements());
    }

    // ── parseCircularArrayQueue ─────────────────────────────

    @Test
    void parseCircularArrayQueueEmpty() {
        String snap = "CircularArrayQueue{size=0, capacity=4, frontIndex=0, logical=[], raw=[null, null, null, null]}";
        CircularQueueStateModel model = StateModelParser.parseCircularArrayQueue(snap);
        assertEquals(0, model.size());
        assertEquals(4, model.capacity());
        assertEquals(0, model.frontIndex());
        assertTrue(model.logical().isEmpty());
        assertEquals(List.of("null", "null", "null", "null"), model.slots());
        assertTrue(model.isEmpty());
    }

    @Test
    void parseCircularArrayQueueWithElements() {
        String snap = "CircularArrayQueue{size=3, capacity=4, frontIndex=1, logical=[10, 20, 30], raw=[null, 10, 20, 30]}";
        CircularQueueStateModel model = StateModelParser.parseCircularArrayQueue(snap);
        assertEquals(3, model.size());
        assertEquals(4, model.capacity());
        assertEquals(1, model.frontIndex());
        assertEquals(List.of("10", "20", "30"), model.logical());
        assertEquals(List.of("null", "10", "20", "30"), model.slots());
        assertFalse(model.isEmpty());
    }

    @Test
    void parseCircularArrayQueueRearIndex() {
        String snap = "CircularArrayQueue{size=3, capacity=4, frontIndex=1, logical=[10, 20, 30], raw=[null, 10, 20, 30]}";
        CircularQueueStateModel model = StateModelParser.parseCircularArrayQueue(snap);
        // rearIndex = (frontIndex + size - 1) % capacity = (1 + 3 - 1) % 4 = 3
        assertEquals(3, model.rearIndex());
    }

    @Test
    void parseCircularArrayQueueWraparound() {
        String snap = "CircularArrayQueue{size=2, capacity=4, frontIndex=3, logical=[10, 20], raw=[20, null, null, 10]}";
        CircularQueueStateModel model = StateModelParser.parseCircularArrayQueue(snap);
        assertEquals(2, model.size());
        assertEquals(3, model.frontIndex());
        // rearIndex = (3 + 2 - 1) % 4 = 0
        assertEquals(0, model.rearIndex());
    }

    // ── parseLinkedQueue ────────────────────────────────────

    @Test
    void parseLinkedQueueEmpty() {
        String snap = "LinkedQueue{size=0, front=null, rear=null, chain=[]}";
        QueueStateModel model = StateModelParser.parseLinkedQueue(snap);
        assertEquals(0, model.size());
        assertEquals("null", model.front());
        assertEquals("null", model.rear());
        assertTrue(model.elements().isEmpty());
        assertTrue(model.isEmpty());
    }

    @Test
    void parseLinkedQueueWithElements() {
        String snap = "LinkedQueue{size=3, front=10, rear=30, chain=[10 -> 20 -> 30]}";
        QueueStateModel model = StateModelParser.parseLinkedQueue(snap);
        assertEquals(3, model.size());
        assertEquals("10", model.front());
        assertEquals("30", model.rear());
        assertEquals(List.of("10", "20", "30"), model.elements());
    }

    // ── parseTwoStackQueue ──────────────────────────────────

    @Test
    void parseTwoStackQueueEmpty() {
        String snap = "TwoStackQueue{size=0, inbox=ArrayStack{size=0, top=null, elements=DynamicArray{size=0, capacity=4, elements=[], raw=[null, null, null, null]}}, outbox=ArrayStack{size=0, top=null, elements=DynamicArray{size=0, capacity=4, elements=[], raw=[null, null, null, null]}}}";
        QueueStateModel model = StateModelParser.parseTwoStackQueue(snap);
        assertEquals(0, model.size());
        assertTrue(model.elements().isEmpty());
        assertTrue(model.isEmpty());
    }

    @Test
    void parseTwoStackQueueWithInboxOnly() {
        // Items enqueued but no dequeue yet: inbox has elements, outbox empty
        String snap = "TwoStackQueue{size=2, inbox=ArrayStack{size=2, top=20, elements=DynamicArray{size=2, capacity=4, elements=[10, 20], raw=[10, 20, null, null]}}, outbox=ArrayStack{size=0, top=null, elements=DynamicArray{size=0, capacity=4, elements=[], raw=[null, null, null, null]}}}";
        QueueStateModel model = StateModelParser.parseTwoStackQueue(snap);
        assertEquals(2, model.size());
        // Queue order: outbox reversed (empty) + inbox = [10, 20]
        assertEquals(List.of("10", "20"), model.elements());
        assertEquals("10", model.front());
        assertEquals("20", model.rear());
    }

    @Test
    void parseTwoStackQueueWithOutboxOnly() {
        // After transfer: outbox has elements top-to-bottom, inbox empty
        String snap = "TwoStackQueue{size=2, inbox=ArrayStack{size=0, top=null, elements=DynamicArray{size=0, capacity=4, elements=[], raw=[null, null, null, null]}}, outbox=ArrayStack{size=2, top=10, elements=DynamicArray{size=2, capacity=4, elements=[20, 10], raw=[20, 10, null, null]}}}";
        QueueStateModel model = StateModelParser.parseTwoStackQueue(snap);
        assertEquals(2, model.size());
        // Queue order: outbox elements=[20, 10] reversed = [10, 20] + inbox (empty)
        assertEquals(List.of("10", "20"), model.elements());
        assertEquals("10", model.front());
        assertEquals("20", model.rear());
    }

    // ── parseBinaryHeap ─────────────────────────────────────

    @Test
    void parseBinaryHeapEmpty() {
        String snap = "BinaryHeap{size=0, min=null, elements=DynamicArray{size=0, capacity=4, elements=[], raw=[null, null, null, null]}}";
        HeapStateModel model = StateModelParser.parseBinaryHeap(snap);
        assertEquals(0, model.size());
        assertEquals("null", model.minValue());
        assertTrue(model.elements().isEmpty());
        assertTrue(model.isEmpty());
    }

    @Test
    void parseBinaryHeapWithElements() {
        String snap = "BinaryHeap{size=3, min=5, elements=DynamicArray{size=3, capacity=4, elements=[5, 10, 15], raw=[5, 10, 15, null]}}";
        HeapStateModel model = StateModelParser.parseBinaryHeap(snap);
        assertEquals(3, model.size());
        assertEquals("5", model.minValue());
        assertEquals(List.of("5", "10", "15"), model.elements());
        assertEquals(4, model.capacity());
    }

    @Test
    void parseBinaryHeapSingleElement() {
        String snap = "BinaryHeap{size=1, min=42, elements=DynamicArray{size=1, capacity=4, elements=[42], raw=[42, null, null, null]}}";
        HeapStateModel model = StateModelParser.parseBinaryHeap(snap);
        assertEquals(1, model.size());
        assertEquals("42", model.minValue());
        assertEquals(List.of("42"), model.elements());
    }

    // ── parseHeapPriorityQueue ──────────────────────────────

    @Test
    void parseHeapPriorityQueueEmpty() {
        String snap = "HeapPriorityQueue{size=0, front=null, heap=BinaryHeap{size=0, min=null, elements=DynamicArray{size=0, capacity=4, elements=[], raw=[null, null, null, null]}}}";
        HeapStateModel model = StateModelParser.parseHeapPriorityQueue(snap);
        assertEquals(0, model.size());
        assertTrue(model.isEmpty());
    }

    @Test
    void parseHeapPriorityQueueWithElements() {
        String snap = "HeapPriorityQueue{size=3, front=5, heap=BinaryHeap{size=3, min=5, elements=DynamicArray{size=3, capacity=4, elements=[5, 10, 15], raw=[5, 10, 15, null]}}}";
        HeapStateModel model = StateModelParser.parseHeapPriorityQueue(snap);
        assertEquals(3, model.size());
        assertEquals("5", model.minValue());
        assertEquals(List.of("5", "10", "15"), model.elements());
    }

    // ── structureType for heaps ─────────────────────────────

    @Test
    void structureTypeExtractsBinaryHeap() {
        assertEquals("BinaryHeap",
                StateModelParser.structureType("BinaryHeap{size=0, min=null, elements=DynamicArray{size=0, capacity=4, elements=[], raw=[null, null, null, null]}}"));
    }

    @Test
    void structureTypeExtractsHeapPriorityQueue() {
        assertEquals("HeapPriorityQueue",
                StateModelParser.structureType("HeapPriorityQueue{size=0, front=null, heap=BinaryHeap{size=0, min=null, elements=DynamicArray{size=0, capacity=4, elements=[], raw=[null, null, null, null]}}}"));
    }
}
