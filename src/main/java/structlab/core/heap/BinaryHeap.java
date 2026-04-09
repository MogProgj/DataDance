package structlab.core.heap;

import structlab.core.array.DynamicArray;
import structlab.trace.Traceable;

public class BinaryHeap<T extends Comparable<T>> implements Traceable {
  private final DynamicArray<T> elements;

  public BinaryHeap() {
    this.elements = new DynamicArray<>();
  }

  public int size() {
    return elements.size();
  }

  public boolean isEmpty() {
    return elements.isEmpty();
  }

  public void insert(T value) {
    elements.append(value);
    heapifyUp(size() - 1);
  }

  public T peek() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot peek into an empty heap.");
    }

    return elements.get(0);
  }

  public T extractMin() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot extractMin from an empty heap.");
    }

    T min = elements.get(0);

    if (size() == 1) {
      elements.removeAt(0);
      return min;
    }

    T last = elements.removeAt(size() - 1);
    elements.set(0, last);
    heapifyDown(0);

    return min;
  }

  @Override
  public String structureName() { return "Binary Heap"; }

  @Override
  public String implementationName() { return "BinaryHeap"; }

  @Override
  public boolean checkInvariant() {
    if (!elements.checkInvariant()) {
      return false;
    }

    for (int i = 0; i < size(); i++) {
      int left = leftChildIndex(i);
      int right = rightChildIndex(i);

      if (left < size() && elements.get(i).compareTo(elements.get(left)) > 0) {
        return false;
      }

      if (right < size() && elements.get(i).compareTo(elements.get(right)) > 0) {
        return false;
      }
    }

    return true;
  }

  @Override
  public String snapshot() {
    return "BinaryHeap{" +
      "size=" + size() +
      ", min=" + (isEmpty() ? "null" : peek()) +
      ", elements=" + elements.snapshot() +
      '}';
  }

  private void heapifyUp(int index) {
    while (index > 0) {
      int parent = parentIndex(index);

      if (elements.get(index).compareTo(elements.get(parent)) >= 0) {
        break;
      }

      swap(index, parent);
      index = parent;
    }
  }

  private void heapifyDown(int index) {
    while (true) {
      int left = leftChildIndex(index);
      int right = rightChildIndex(index);
      int smallest = index;

      if (left < size() && elements.get(left).compareTo(elements.get(smallest)) < 0) {
        smallest = left;
      }

      if (right < size() && elements.get(right).compareTo(elements.get(smallest)) < 0) {
        smallest = right;
      }

      if (smallest == index) {
        break;
      }

      swap(index, smallest);
      index = smallest;
    }
  }

  private void swap(int i, int j) {
    T temp = elements.get(i);
    elements.set(i, elements.get(j));
    elements.set(j, temp);
  }

  private int parentIndex(int index) {
    return (index - 1) / 2;
  }

  private int leftChildIndex(int index) {
    return 2 * index + 1;
  }

  private int rightChildIndex(int index) {
    return 2 * index + 2;
  }
}
