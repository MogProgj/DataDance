package structlab.core.stack;

import structlab.trace.Traceable;

public class LinkedStack<T> implements Traceable {
  private Node<T> top;
  private int size;

  private static class Node<T> {
    private final T value;
    private Node<T> next;

    private Node(T value, Node<T> next) {
      this.value = value;
      this.next = next;
    }
  }

  public LinkedStack() {
    this.top = null;
    this.size = 0;
  }

  public int size() {
    return size;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public void push(T value) {
    top = new Node<>(value, top);
    size++;
  }

  public T pop() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot pop from an empty stack.");
    }

    T value = top.value;
    top = top.next;
    size--;
    return value;
  }

  public T peek() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot peek into an empty stack.");
    }

    return top.value;
  }

  @Override
  public String structureName() {
    return "Stack";
  }

  @Override
  public String implementationName() {
    return "LinkedStack";
  }

  @Override
  public boolean checkInvariant() {
    if (size < 0) {
      return false;
    }

    int counted = 0;
    Node<T> current = top;

    while (current != null) {
      counted++;
      current = current.next;
    }

    return counted == size;
  }

  @Override
  public String snapshot() {
    StringBuilder sb = new StringBuilder();
    sb.append("LinkedStack{");
    sb.append("size=").append(size);
    sb.append(", top=").append(isEmpty() ? "null" : top.value);
    sb.append(", chain=[");

    Node<T> current = top;
    while (current != null) {
      sb.append(current.value);
      current = current.next;
      if (current != null) {
        sb.append(" -> ");
      }
    }

    sb.append("]}");
    return sb.toString();
  }
}
