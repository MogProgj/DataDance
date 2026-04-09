package structlab.core.heap;

import structlab.trace.Traceable;

public class HeapPriorityQueue<T extends Comparable<T>> implements Traceable {
  private final BinaryHeap<T> heap;

  public HeapPriorityQueue() {
    this.heap = new BinaryHeap<>();
  }

  public int size() {
    return heap.size();
  }

  public boolean isEmpty() {
    return heap.isEmpty();
  }

  public void enqueue(T value) {
    heap.insert(value);
  }

  public T dequeue() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot dequeue from an empty priority queue.");
    }

    return heap.extractMin();
  }

  public T peek() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot peek into an empty priority queue.");
    }

    return heap.peek();
  }

  @Override
  public String structureName() { return "Priority Queue"; }

  @Override
  public String implementationName() { return "HeapPriorityQueue"; }

  @Override
  public boolean checkInvariant() {
    return heap != null && heap.checkInvariant();
  }

  @Override
  public String snapshot() {
    return "HeapPriorityQueue{" +
      "size=" + size() +
      ", front=" + (isEmpty() ? "null" : peek()) +
      ", heap=" + heap.snapshot() +
      '}';
  }
}
