package structlab.core.list;

import structlab.trace.Traceable;

public class DoublyLinkedList<T> implements Traceable {
  private Node<T> head;
  private Node<T> tail;
  private int size;

  private static class Node<T> {
    private final T value;
    private Node<T> next;
    private Node<T> prev;

    private Node(T value) {
      this.value = value;
    }
  }

  public DoublyLinkedList() {
    this.head = null;
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
      head = tail = node;
    } else {
      node.next = head;
      head.prev = node;
      head = node;
    }

    size++;
  }

  public void addLast(T value) {
    Node<T> node = new Node<>(value);

    if (isEmpty()) {
      head = tail = node;
    } else {
      tail.next = node;
      node.prev = tail;
      tail = node;
    }

    size++;
  }

  public T removeFirst() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot removeFirst from an empty doubly linked list.");
    }

    T value = head.value;
    head = head.next;
    size--;

    if (size == 0) {
      tail = null;
    } else {
      head.prev = null;
    }

    return value;
  }

  public T removeLast() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot removeLast from an empty doubly linked list.");
    }

    T value = tail.value;
    tail = tail.prev;
    size--;

    if (size == 0) {
      head = null;
    } else {
      tail.next = null;
    }

    return value;
  }

  public T getFirst() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot getFirst from an empty doubly linked list.");
    }

    return head.value;
  }

  public T getLast() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot getLast from an empty doubly linked list.");
    }

    return tail.value;
  }

  public boolean contains(T value) {
    Node<T> current = head;

    while (current != null) {
      if ((value == null && current.value == null) ||
        (value != null && value.equals(current.value))) {
        return true;
      }
      current = current.next;
    }

    return false;
  }

  @Override
  public String structureName() { return "Doubly Linked List"; }

  @Override
  public String implementationName() { return "DoublyLinkedList"; }

  @Override
  public boolean checkInvariant() {
    if (size < 0) {
      return false;
    }

    if (size == 0) {
      return head == null && tail == null;
    }

    if (head == null || tail == null) {
      return false;
    }

    if (head.prev != null || tail.next != null) {
      return false;
    }

    int counted = 0;
    Node<T> current = head;
    Node<T> last = null;

    while (current != null) {
      if (current.next != null && current.next.prev != current) {
        return false;
      }
      last = current;
      counted++;
      current = current.next;
    }

    return counted == size && last == tail;
  }

  @Override
  public String snapshot() {
    StringBuilder sb = new StringBuilder();
    sb.append("DoublyLinkedList{");
    sb.append("size=").append(size);
    sb.append(", head=").append(head == null ? "null" : head.value);
    sb.append(", tail=").append(tail == null ? "null" : tail.value);
    sb.append(", chain=[");

    Node<T> current = head;
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
