package structlab.demo;

import structlab.core.list.DoublyLinkedList;

public class DoublyLinkedListDemo {
  public static void main(String[] args) {
    DoublyLinkedList<Integer> list = new DoublyLinkedList<>();

    list.addFirst(20);
    list.addFirst(10);
    list.addLast(30);

    System.out.println(list.snapshot());
    System.out.println("removeLast(): " + list.removeLast());
    System.out.println(list.snapshot());
    System.out.println("Invariant check: " + list.checkInvariant());
  }
}
