package structlab.demo;

import structlab.core.list.SinglyLinkedList;

public class SinglyLinkedListDemo {
  public static void main(String[] args) {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();

    list.addFirst(20);
    list.addFirst(10);
    list.addLast(30);

    System.out.println(list.snapshot());
    System.out.println("removeFirst(): " + list.removeFirst());
    System.out.println(list.snapshot());
    System.out.println("Invariant check: " + list.checkInvariant());
  }
}
