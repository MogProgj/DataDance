package structlab.core.list;

import structlab.trace.Traceable;

public class SinglyLinkedList<T> implements Traceable {
  private Node<T> head;
  private Node<T> tail;
  private int size;

  private static class Node<T> {
    private final T value;
    private Node<T> next;

    private Node(T value) {
      this.value = value;
    }
  }

  public SinglyLinkedList() {
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
    node.next = head;
    head = node;

    if (tail == null) {
      tail = node;
    }

    size++;
  }

  public void addLast(T value) {
    Node<T> node = new Node<>(value);

    if (isEmpty()) {
      head = tail = node;
    } else {
      tail.next = node;
      tail = node;
    }

    size++;
  }

  public T removeFirst() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot removeFirst from an empty singly linked list.");
    }

    T value = head.value;
    head = head.next;
    size--;

    if (size == 0) {
      tail = null;
    }

    return value;
  }

  public T getFirst() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot getFirst from an empty singly linked list.");
    }

    return head.value;
  }

  public T getLast() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot getLast from an empty singly linked list.");
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
  public String structureName() { return "Singly Linked List"; }

  @Override
  public String implementationName() { return "SinglyLinkedList"; }

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

    int counted = 0;
    Node<T> current = head;
    Node<T> last = null;

    while (current != null) {
      counted++;
      last = current;
      current = current.next;
    }

    return counted == size && last == tail && tail.next == null;
  }

  @Override
  public String snapshot() {
    StringBuilder sb = new StringBuilder();
    sb.append("SinglyLinkedList{");
    sb.append("size=").append(size);
    sb.append(", head=").append(head == null ? "null" : head.value);
    sb.append(", tail=").append(tail == null ? "null" : tail.value);
    sb.append(", chain=[");

    Node<T> current = head;
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
