package structlab.render;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StructureRendererTest {

  // ---- DynamicArray ----

  @Test
  void renderArrayShowsSizeAndCapacity() {
    String snap = "DynamicArray{size=2, capacity=4, elements=[10, 20], raw=[10, 20, null, null]}";
    String rendered = StructureRenderer.renderArray(snap);
    assertTrue(rendered.contains("size: 2"));
    assertTrue(rendered.contains("capacity: 4"));
  }

  @Test
  void renderArrayShowsLogicalAndBacking() {
    String snap = "DynamicArray{size=2, capacity=4, elements=[10, 20], raw=[10, 20, null, null]}";
    String rendered = StructureRenderer.renderArray(snap);
    assertTrue(rendered.contains("Logical:"));
    assertTrue(rendered.contains("Backing:"));
    assertTrue(rendered.contains("10"));
    assertTrue(rendered.contains("null"));
  }

  @Test
  void renderArrayShowsEmptyForNoElements() {
    String snap = "DynamicArray{size=0, capacity=2, elements=[], raw=[null, null]}";
    String rendered = StructureRenderer.renderArray(snap);
    assertTrue(rendered.contains("(empty)"));
  }

  // ---- FixedArray ----

  @Test
  void renderFixedArrayShowsFullMarker() {
    String snap = "FixedArray{size=3, capacity=3, elements=[1, 2, 3], raw=[1, 2, 3]}";
    String rendered = StructureRenderer.renderArray(snap);
    assertTrue(rendered.contains("FULL"));
  }

  @Test
  void renderFixedArrayShowsSlotCount() {
    String snap = "FixedArray{size=1, capacity=4, elements=[10], raw=[10, null, null, null]}";
    String rendered = StructureRenderer.renderArray(snap);
    assertTrue(rendered.contains("1/4"));
  }

  // ---- ArrayStack ----

  @Test
  void renderArrayStackShowsTopMarker() {
    String snap = "ArrayStack{size=2, top=20, elements=DynamicArray{size=2, capacity=4, elements=[10, 20], raw=[10, 20, null, null]}}";
    String rendered = StructureRenderer.renderArrayStack(snap);
    assertTrue(rendered.contains("<-- top"));
    assertTrue(rendered.contains("top: 20"));
  }

  @Test
  void renderArrayStackShowsEmptyStack() {
    String snap = "ArrayStack{size=0, top=null, elements=DynamicArray{size=0, capacity=4, elements=[], raw=[null, null, null, null]}}";
    String rendered = StructureRenderer.renderArrayStack(snap);
    assertTrue(rendered.contains("(empty)"));
    assertTrue(rendered.contains("top: null"));
  }

  // ---- CircularArrayQueue ----

  @Test
  void renderCircularQueueShowsFrontAndRearMarkers() {
    String snap = "CircularArrayQueue{size=2, capacity=4, frontIndex=1, logical=[20, 30], raw=[null, 20, 30, null]}";
    String rendered = StructureRenderer.renderCircularQueue(snap);
    assertTrue(rendered.contains("F"));
    assertTrue(rendered.contains("R"));
    assertTrue(rendered.contains("front: idx 1"));
  }

  @Test
  void renderCircularQueueShowsLogicalOrder() {
    String snap = "CircularArrayQueue{size=3, capacity=4, frontIndex=2, logical=[30, 40, 10], raw=[10, null, 30, 40]}";
    String rendered = StructureRenderer.renderCircularQueue(snap);
    assertTrue(rendered.contains("30 -> 40 -> 10"));
  }

  @Test
  void renderCircularQueueShowsEmptyQueue() {
    String snap = "CircularArrayQueue{size=0, capacity=4, frontIndex=0, logical=[], raw=[null, null, null, null]}";
    String rendered = StructureRenderer.renderCircularQueue(snap);
    assertTrue(rendered.contains("(empty)"));
  }

  // ---- LinkedStack ----

  @Test
  void renderLinkedStackShowsChainWithTopMarker() {
    String snap = "LinkedStack{size=3, top=30, chain=[30 -> 20 -> 10]}";
    String rendered = StructureRenderer.renderLinkedStack(snap);
    assertTrue(rendered.contains("top ->"));
    assertTrue(rendered.contains("[30]"));
    assertTrue(rendered.contains("[10]"));
    assertTrue(rendered.contains("null"));
    assertTrue(rendered.contains("top: 30"));
  }

  @Test
  void renderLinkedStackShowsEmptyChain() {
    String snap = "LinkedStack{size=0, top=null, chain=[]}";
    String rendered = StructureRenderer.renderLinkedStack(snap);
    assertTrue(rendered.contains("top -> null"));
  }

  // ---- LinkedQueue ----

  @Test
  void renderLinkedQueueShowsFrontAndRear() {
    String snap = "LinkedQueue{size=3, front=10, rear=30, chain=[10 -> 20 -> 30]}";
    String rendered = StructureRenderer.renderLinkedQueue(snap);
    assertTrue(rendered.contains("front ->"));
    assertTrue(rendered.contains("[10]"));
    assertTrue(rendered.contains("[30]"));
    assertTrue(rendered.contains("front: 10"));
    assertTrue(rendered.contains("rear: 30"));
  }

  @Test
  void renderLinkedQueueShowsPointerMarkers() {
    String snap = "LinkedQueue{size=3, front=10, rear=30, chain=[10 -> 20 -> 30]}";
    String rendered = StructureRenderer.renderLinkedQueue(snap);
    assertTrue(rendered.contains("front"));
    assertTrue(rendered.contains("rear"));
  }

  // ---- TwoStackQueue ----

  @Test
  void renderTwoStackQueueShowsBothStacks() {
    String snap = "TwoStackQueue{size=3, inbox=ArrayStack{size=2, top=30, elements=DynamicArray{size=2, capacity=4, elements=[20, 30], raw=[20, 30, null, null]}}, outbox=ArrayStack{size=1, top=10, elements=DynamicArray{size=1, capacity=4, elements=[10], raw=[10, null, null, null]}}}";
    String rendered = StructureRenderer.renderTwoStackQueue(snap);
    assertTrue(rendered.contains("Inbox"));
    assertTrue(rendered.contains("Outbox"));
  }

  @Test
  void renderTwoStackQueueShowsQueueOrder() {
    String snap = "TwoStackQueue{size=3, inbox=ArrayStack{size=2, top=30, elements=DynamicArray{size=2, capacity=4, elements=[20, 30], raw=[20, 30, null, null]}}, outbox=ArrayStack{size=1, top=10, elements=DynamicArray{size=1, capacity=4, elements=[10], raw=[10, null, null, null]}}}";
    String rendered = StructureRenderer.renderTwoStackQueue(snap);
    assertTrue(rendered.contains("Queue order"));
    // outbox has [10] (top=10, dequeues first), then inbox has [20, 30]
    assertTrue(rendered.contains("10, 20, 30"));
  }

  @Test
  void renderTwoStackQueueShowsEmptyState() {
    String snap = "TwoStackQueue{size=0, inbox=ArrayStack{size=0, top=null, elements=DynamicArray{size=0, capacity=4, elements=[], raw=[null, null, null, null]}}, outbox=ArrayStack{size=0, top=null, elements=DynamicArray{size=0, capacity=4, elements=[], raw=[null, null, null, null]}}}";
    String rendered = StructureRenderer.renderTwoStackQueue(snap);
    assertTrue(rendered.contains("(empty)"));
  }

  // ---- Index alignment ----

  @Test
  void indexRowAlignsCenterUnderCells() {
    // With 10+ elements, multi-digit indexes should still appear
    String snap = "DynamicArray{size=3, capacity=12, elements=[1, 2, 3], raw=[1, 2, 3, null, null, null, null, null, null, null, null, null]}";
    String rendered = StructureRenderer.renderArray(snap);
    // Should contain double-digit indexes without breaking layout
    assertTrue(rendered.contains("10"));
    assertTrue(rendered.contains("11"));
    assertTrue(rendered.contains("Index:"));
  }

  // ---- SinglyLinkedList ----

  @Test
  void renderSinglyLinkedListShowsChain() {
    String snap = "SinglyLinkedList{size=3, head=10, tail=30, chain=[10 -> 20 -> 30]}";
    String rendered = StructureRenderer.renderSinglyLinkedList(snap);
    assertTrue(rendered.contains("head ->"));
    assertTrue(rendered.contains("[10]"));
    assertTrue(rendered.contains("[30]"));
    assertTrue(rendered.contains("null"));
    assertTrue(rendered.contains("tail"));
  }

  @Test
  void renderSinglyLinkedListEmpty() {
    String snap = "SinglyLinkedList{size=0, head=null, tail=null, chain=[]}";
    String rendered = StructureRenderer.renderSinglyLinkedList(snap);
    assertTrue(rendered.contains("head -> null"));
  }

  // ---- DoublyLinkedList ----

  @Test
  void renderDoublyLinkedListShowsBidirectionalChain() {
    String snap = "DoublyLinkedList{size=3, head=10, tail=30, chain=[10 <-> 20 <-> 30]}";
    String rendered = StructureRenderer.renderDoublyLinkedList(snap);
    assertTrue(rendered.contains("<-->"));
    assertTrue(rendered.contains("[10]"));
    assertTrue(rendered.contains("[30]"));
    assertTrue(rendered.contains("head"));
    assertTrue(rendered.contains("tail"));
  }

  // ---- LinkedDeque ----

  @Test
  void renderLinkedDequeShowsFrontRear() {
    String snap = "LinkedDeque{size=3, front=10, rear=30, chain=[10 <-> 20 <-> 30]}";
    String rendered = StructureRenderer.renderLinkedDeque(snap);
    assertTrue(rendered.contains("<-->"));
    assertTrue(rendered.contains("front"));
    assertTrue(rendered.contains("rear"));
  }

  // ---- ArrayDequeCustom ----

  @Test
  void renderArrayDequeShowsBufferAndMarkers() {
    String snap = "ArrayDequeCustom{size=2, capacity=4, frontIndex=1, logical=[20, 30], raw=[null, 20, 30, null]}";
    String rendered = StructureRenderer.renderArrayDeque(snap);
    assertTrue(rendered.contains("Buffer:"));
    assertTrue(rendered.contains("F"));
    assertTrue(rendered.contains("Logical:"));
    assertTrue(rendered.contains("front: idx 1"));
  }

  // ---- BinaryHeap ----

  @Test
  void renderBinaryHeapShowsArrayAndTree() {
    String snap = "BinaryHeap{size=3, min=1, elements=DynamicArray{size=3, capacity=4, elements=[1, 3, 2], raw=[1, 3, 2, null]}}";
    String rendered = StructureRenderer.renderBinaryHeap(snap);
    assertTrue(rendered.contains("Array:"));
    assertTrue(rendered.contains("Tree:"));
    assertTrue(rendered.contains("Level 0:"));
    assertTrue(rendered.contains("min: 1"));
  }

  @Test
  void renderBinaryHeapEmpty() {
    String snap = "BinaryHeap{size=0, min=null, elements=DynamicArray{size=0, capacity=4, elements=[], raw=[null, null, null, null]}}";
    String rendered = StructureRenderer.renderBinaryHeap(snap);
    assertTrue(rendered.contains("(empty)"));
  }

  // ---- HeapPriorityQueue ----

  @Test
  void renderHeapPriorityQueueShowsBackedByHeap() {
    String snap = "HeapPriorityQueue{size=3, front=1, heap=BinaryHeap{size=3, min=1, elements=DynamicArray{size=3, capacity=4, elements=[1, 3, 2], raw=[1, 3, 2, null]}}}";
    String rendered = StructureRenderer.renderHeapPriorityQueue(snap);
    assertTrue(rendered.contains("HeapPriorityQueue"));
    assertTrue(rendered.contains("front: 1"));
    assertTrue(rendered.contains("backed by BinaryHeap"));
    assertTrue(rendered.contains("Array:"));
  }

  // ---- New type dispatch ----

  @Test
  void renderDispatchesSinglyLinkedList() {
    String snap = "SinglyLinkedList{size=1, head=5, tail=5, chain=[5]}";
    String rendered = StructureRenderer.render(snap);
    assertTrue(rendered.contains("SinglyLinkedList"));
  }

  @Test
  void renderDispatchesBinaryHeap() {
    String snap = "BinaryHeap{size=1, min=5, elements=DynamicArray{size=1, capacity=4, elements=[5], raw=[5, null, null, null]}}";
    String rendered = StructureRenderer.render(snap);
    assertTrue(rendered.contains("BinaryHeap"));
    assertTrue(rendered.contains("Array:"));
  }

  // ---- Dispatch ----

  @Test
  void renderDispatchesByType() {
    String dynSnap = "DynamicArray{size=1, capacity=2, elements=[5], raw=[5, null]}";
    String rendered = StructureRenderer.render(dynSnap);
    assertTrue(rendered.contains("DynamicArray"));
    assertTrue(rendered.contains("Logical:"));
  }

  @Test
  void renderFallsBackForUnknownType() {
    String unknown = "SomeOtherType{data=hello}";
    String rendered = StructureRenderer.render(unknown);
    assertTrue(rendered.contains("SomeOtherType{data=hello}"));
  }
}
