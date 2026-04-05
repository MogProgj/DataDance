package structlab.core.list;

public class CircularLinkedList<T> {
  private Node<T> tail;
  private int size;

  private static class Node<T> {
    private final T value;
    private Node<T> next;

    private Node(T value) {
      this.value = value;
    }
  }

  public CircularLinkedList() {
    this.tail = null;
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
      tail = node;
      node.next = node;
    } else {
      node.next = tail.next;
      tail.next = node;
    }

    size++;
  }

  public void addLast(T value) {
    Node<T> node = new Node<>(value);

    if (isEmpty()) {
      tail = node;
      node.next = node;
    } else {
      node.next = tail.next;
      tail.next = node;
      tail = node;
    }

    size++;
  }

  public T removeFirst() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot removeFirst from an empty circular linked list.");
    }

    Node<T> head = tail.next;
    T value = head.value;

    if (size == 1) {
      tail = null;
    } else {
      tail.next = head.next;
    }

    size--;
    return value;
  }

  public T getFirst() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot getFirst from an empty circular linked list.");
    }

    return tail.next.value;
  }

  public T getLast() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot getLast from an empty circular linked list.");
    }

    return tail.value;
  }

  public boolean contains(T value) {
    if (isEmpty()) {
      return false;
    }

    Node<T> current = tail.next;

    for (int i = 0; i < size; i++) {
      if ((value == null && current.value == null) ||
        (value != null && value.equals(current.value))) {
        return true;
      }
      current = current.next;
    }

    return false;
  }

  public boolean checkInvariant() {
    if (size < 0) {
      return false;
    }

    if (size == 0) {
      return tail == null;
    }

    if (tail == null || tail.next == null) {
      return false;
    }

    Node<T> current = tail.next;

    for (int i = 0; i < size; i++) {
      if (current == null) {
        return false;
      }
      current = current.next;
    }

    return current == tail.next;
  }

  public String snapshot() {
    StringBuilder sb = new StringBuilder();
    sb.append("CircularLinkedList{");
    sb.append("size=").append(size);
    sb.append(", head=").append(isEmpty() ? "null" : tail.next.value);
    sb.append(", tail=").append(isEmpty() ? "null" : tail.value);
    sb.append(", cycle=[");

    if (!isEmpty()) {
      Node<T> current = tail.next;
      for (int i = 0; i < size; i++) {
        sb.append(current.value);
        current = current.next;
        if (i < size - 1) {
          sb.append(" -> ");
        }
      }
      sb.append(" -> (back to head)");
    }

    sb.append("]}");
    return sb.toString();
  }
}
