package structlab.demo;

import structlab.core.hash.HashSetCustom;

public class HashSetCustomDemo {
  public static void main(String[] args) {
    HashSetCustom<Integer> set = new HashSetCustom<>(4);

    System.out.println("Initial:");
    System.out.println(set.snapshot());
    System.out.println();

    System.out.println("add(10): " + set.add(10));
    System.out.println("add(20): " + set.add(20));
    System.out.println("add(10) again: " + set.add(10));
    System.out.println(set.snapshot());
    System.out.println();

    System.out.println("contains(20): " + set.contains(20));
    System.out.println("remove(10): " + set.remove(10));
    System.out.println(set.snapshot());
    System.out.println();

    System.out.println("Invariant check: " + set.checkInvariant());
  }
}
