package structlab.demo;

import structlab.core.list.SinglyLinkedList;

public class SinglyLinkedListDemo {
  public static void main(String[] args) {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();

    System.out.println("Initial:");
    System.out.println(list.snapshot());
    System.out.println();

    list.addFirst(20);
    list.addFirst(10);
    list.addLast(30);
    list.addLast(40);

    System.out.println("After addFirst(20), addFirst(10), addLast(30), addLast(40):");
    System.out.println(list.snapshot());
    System.out.println();

    System.out.println("contains(30): " + list.contains(30));
    System.out.println("contains(99): " + list.contains(99));
    System.out.println();

    System.out.println("removeFirst(): " + list.removeFirst());
    System.out.println("After removeFirst():");
    System.out.println(list.snapshot());
    System.out.println();

    System.out.println("getFirst(): " + list.getFirst());
    System.out.println("getLast(): " + list.getLast());
    System.out.println("Invariant check: " + list.checkInvariant());
  }
}
