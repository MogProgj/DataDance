package structlab.demo;

import structlab.core.hash.HashTableChaining;

public class HashTableChainingDemo {
  public static void main(String[] args) {
    HashTableChaining<String, Integer> table = new HashTableChaining<>(4);

    System.out.println("Initial:");
    System.out.println(table.snapshot());
    System.out.println();

    table.put("apple", 10);
    table.put("banana", 20);
    table.put("cherry", 30);

    System.out.println("After put operations:");
    System.out.println(table.snapshot());
    System.out.println();

    System.out.println("get(\"banana\"): " + table.get("banana"));
    System.out.println("containsKey(\"apple\"): " + table.containsKey("apple"));
    System.out.println();

    System.out.println("remove(\"banana\"): " + table.remove("banana"));
    System.out.println(table.snapshot());
    System.out.println();

    System.out.println("Invariant check: " + table.checkInvariant());
  }
}
