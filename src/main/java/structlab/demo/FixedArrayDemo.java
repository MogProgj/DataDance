package structlab.demo;

import structlab.core.array.FixedArray;

public class FixedArrayDemo {
  public static void main(String[] args) {
    FixedArray<Integer> array = new FixedArray<>(4);

    System.out.println("Initial (capacity=4):");
    System.out.println(array.snapshot());
    System.out.println();

    array.append(10);
    array.append(20);
    array.append(30);
    System.out.println("After append(10), append(20), append(30):");
    System.out.println(array.snapshot());
    System.out.println();

    array.insert(1, 99);
    System.out.println("After insert(1, 99):");
    System.out.println(array.snapshot());
    System.out.println("isFull(): " + array.isFull());
    System.out.println();

    int removed = array.removeAt(2);
    System.out.println("After removeAt(2), removed = " + removed + ":");
    System.out.println(array.snapshot());
    System.out.println();

    System.out.println("Invariant check: " + array.checkInvariant());
  }
}
