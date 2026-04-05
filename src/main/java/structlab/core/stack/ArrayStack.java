package structlab.core.stack;

import structlab.core.array.DynamicArray;
import structlab.trace.Traceable;

public class ArrayStack<T> implements Traceable {
  private final DynamicArray<T> elements;

  public ArrayStack() {
    this.elements = new DynamicArray<>();
  }

  public int size() {
    return elements.size();
  }

  public boolean isEmpty() {
    return elements.isEmpty();
  }

  public void push(T value) {
    elements.append(value);
  }

  public T pop() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot pop from an empty stack.");
    }

    return elements.removeAt(elements.size() - 1);
  }

  public T peek() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot peek into an empty stack.");
    }

    return elements.get(elements.size() - 1);
  }

  @Override
  public String structureName() {
    return "Stack";
  }

  @Override
  public String implementationName() {
    return "ArrayStack";
  }

  @Override
  public boolean checkInvariant() {
    return elements != null && elements.checkInvariant();
  }

  @Override
  public String snapshot() {
    return "ArrayStack{" +
      "size=" + size() +
      ", top=" + (isEmpty() ? "null" : peek()) +
      ", elements=" + elements.snapshot() +
      '}';
  }
}
