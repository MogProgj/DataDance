package structlab.demo;

import structlab.core.list.CircularLinkedList;

public class CircularLinkedListDemo {
  public static void main(String[] args) {
    CircularLinkedList<Integer> list = new CircularLinkedList<>();

    list.addLast(10);
    list.addLast(20);
    list.addFirst(5);

    System.out.println(list.snapshot());
    System.out.println("removeFirst(): " + list.removeFirst());
    System.out.println(list.snapshot());
    System.out.println("Invariant check: " + list.checkInvariant());
  }
}
