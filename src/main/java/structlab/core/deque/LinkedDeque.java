package structlab.core.deque;

import structlab.trace.Traceable;

public class LinkedDeque<T> implements Traceable {
  private Node<T> front;
  private Node<T> rear;
  private int size;

  private static class Node<T> {
    private final T value;
    private Node<T> next;
    private Node<T> prev;

    private Node(T value) {
      this.value = value;
    }
  }

  public LinkedDeque() {
    this.front = null;
    this.rear = null;
    this.size = 0;
  }

  public int size() {
    return size;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public void addFirst(T value) {
    Node<T> node = new Node<>(value);

    if (isEmpty()) {
      front = rear = node;
    } else {
      node.next = front;
      front.prev = node;
      front = node;
    }

    size++;
  }

  public void addLast(T value) {
    Node<T> node = new Node<>(value);

    if (isEmpty()) {
      front = rear = node;
    } else {
      rear.next = node;
      node.prev = rear;
      rear = node;
    }

    size++;
  }

  public T removeFirst() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot removeFirst from an empty deque.");
    }

    T value = front.value;
    front = front.next;
    size--;

    if (size == 0) {
      rear = null;
    } else {
      front.prev = null;
    }

    return value;
  }

  public T removeLast() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot removeLast from an empty deque.");
    }

    T value = rear.value;
    rear = rear.prev;
    size--;

    if (size == 0) {
      front = null;
    } else {
      rear.next = null;
    }

    return value;
  }

  public T peekFirst() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot peekFirst into an empty deque.");
    }

    return front.value;
  }

  public T peekLast() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot peekLast into an empty deque.");
    }

    return rear.value;
  }

  @Override
  public String structureName() { return "Deque"; }

  @Override
  public String implementationName() { return "LinkedDeque"; }

  @Override
  public boolean checkInvariant() {
    if (size < 0) {
      return false;
    }

    if (size == 0) {
      return front == null && rear == null;
    }

    if (front == null || rear == null) {
      return false;
    }

    int counted = 0;
    Node<T> current = front;
    Node<T> last = null;

    while (current != null) {
      if (current.next != null && current.next.prev != current) {
        return false;
      }
      last = current;
      counted++;
      current = current.next;
    }

    return counted == size && last == rear;
  }

  @Override
  public String snapshot() {
    StringBuilder sb = new StringBuilder();
    sb.append("LinkedDeque{");
    sb.append("size=").append(size);
    sb.append(", front=").append(isEmpty() ? "null" : front.value);
    sb.append(", rear=").append(isEmpty() ? "null" : rear.value);
    sb.append(", chain=[");

    Node<T> current = front;
    while (current != null) {
      sb.append(current.value);
      current = current.next;
      if (current != null) {
        sb.append(" <-> ");
      }
    }

    sb.append("]}");
    return sb.toString();
  }
}
