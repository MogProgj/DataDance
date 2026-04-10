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
    void isSupportedReturnsFalseForBinaryHeap() {
        assertFalse(VisualStateFactory.isSupported(
                "BinaryHeap{size=2, root=5, elements=[5, 10]}"));
    }
}
