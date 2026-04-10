package structlab.gui.visual;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VisualStateFactoryTest {

    @Test
    void isSupportedReturnsTrueForArrayStack() {
        assertTrue(VisualStateFactory.isSupported(
                "ArrayStack{size=1, top=10, elements=DynamicArray{size=1, capacity=4, elements=[10], raw=[10, null, null, null]}}"));
    }

    @Test
    void isSupportedReturnsTrueForLinkedStack() {
        assertTrue(VisualStateFactory.isSupported(
                "LinkedStack{size=1, top=10, chain=[10]}"));
    }

    @Test
    void isSupportedReturnsTrueForCircularArrayQueue() {
        assertTrue(VisualStateFactory.isSupported(
                "CircularArrayQueue{size=0, capacity=4, frontIndex=0, logical=[], raw=[null, null, null, null]}"));
    }

    @Test
    void isSupportedReturnsTrueForLinkedQueue() {
        assertTrue(VisualStateFactory.isSupported(
                "LinkedQueue{size=0, front=null, rear=null, chain=[]}"));
    }

    @Test
    void isSupportedReturnsTrueForTwoStackQueue() {
        assertTrue(VisualStateFactory.isSupported(
                "TwoStackQueue{size=0, inbox=ArrayStack{size=0, top=null, elements=DynamicArray{size=0, capacity=4, elements=[], raw=[null, null, null, null]}}, outbox=ArrayStack{size=0, top=null, elements=DynamicArray{size=0, capacity=4, elements=[], raw=[null, null, null, null]}}}"));
    }

    @Test
    void isSupportedReturnsFalseForUnsupported() {
        assertFalse(VisualStateFactory.isSupported(
                "DynamicArray{size=3, capacity=4, elements=[1, 2, 3], raw=[1, 2, 3, null]}"));
    }

    @Test
    void isSupportedReturnsTrueForBinaryHeap() {
        assertTrue(VisualStateFactory.isSupported(
                "BinaryHeap{size=2, min=5, elements=DynamicArray{size=2, capacity=4, elements=[5, 10], raw=[5, 10, null, null]}}"));
    }

    @Test
    void isSupportedReturnsTrueForHeapPriorityQueue() {
        assertTrue(VisualStateFactory.isSupported(
                "HeapPriorityQueue{size=2, front=5, heap=BinaryHeap{size=2, min=5, elements=DynamicArray{size=2, capacity=4, elements=[5, 10], raw=[5, 10, null, null]}}}"));
    }

    @Test
    void isSupportedReturnsTrueForSinglyLinkedList() {
        assertTrue(VisualStateFactory.isSupported(
                "SinglyLinkedList{size=2, head=5, tail=10, chain=[5 -> 10]}"));
    }

    @Test
    void isSupportedReturnsTrueForDoublyLinkedList() {
        assertTrue(VisualStateFactory.isSupported(
                "DoublyLinkedList{size=2, head=5, tail=10, chain=[5 <-> 10]}"));
    }

    @Test
    void isSupportedReturnsTrueForArrayDequeCustom() {
        assertTrue(VisualStateFactory.isSupported(
                "ArrayDequeCustom{size=0, capacity=4, frontIndex=0, logical=[], raw=[null, null, null, null]}"));
    }

    @Test
    void isSupportedReturnsTrueForLinkedDeque() {
        assertTrue(VisualStateFactory.isSupported(
                "LinkedDeque{size=0, front=null, rear=null, chain=[]}"));
    }

    @Test
    void isSupportedReturnsTrueForHashTableChaining() {
        assertTrue(VisualStateFactory.isSupported(
                "HashTableChaining{size=0, capacity=4, buckets=[0: empty, 1: empty, 2: empty, 3: empty]}"));
    }

    @Test
    void isSupportedReturnsTrueForHashTableOpenAddressing() {
        assertTrue(VisualStateFactory.isSupported(
                "HashTableOpenAddressing{size=0, capacity=8, oaType=LINEAR, hashType=DIVISION, rehashes=0, slots=[0: empty]}"));
    }

    @Test
    void isSupportedReturnsTrueForHashSetCustom() {
        assertTrue(VisualStateFactory.isSupported(
                "HashSetCustom{size=0, table=HashTableChaining{size=0, capacity=8, buckets=[0: empty]}}"));
    }
}
