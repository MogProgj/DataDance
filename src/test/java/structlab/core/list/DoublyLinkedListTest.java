package structlab.core.list;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DoublyLinkedListTest {

  @Test
  void newListStartsEmpty() {
    DoublyLinkedList<Integer> list = new DoublyLinkedList<>();

    assertEquals(0, list.size());
    assertTrue(list.isEmpty());
    assertTrue(list.checkInvariant());
  }

  @Test
  void addFirstAndAddLastWork() {
    DoublyLinkedList<Integer> list = new DoublyLinkedList<>();

    list.addFirst(20);
    list.addFirst(10);
    list.addLast(30);

    assertEquals(3, list.size());
    assertEquals(10, list.getFirst());
    assertEquals(30, list.getLast());
    assertTrue(list.checkInvariant());
  }

  @Test
  void removeFirstWorks() {
    DoublyLinkedList<Integer> list = new DoublyLinkedList<>();

    list.addLast(10);
    list.addLast(20);

    assertEquals(10, list.removeFirst());
    assertEquals(20, list.getFirst());
    assertTrue(list.checkInvariant());
  }

  @Test
  void removeLastWorks() {
    DoublyLinkedList<Integer> list = new DoublyLinkedList<>();

    list.addLast(10);
    list.addLast(20);

    assertEquals(20, list.removeLast());
    assertEquals(10, list.getLast());
    assertTrue(list.checkInvariant());
  }

  @Test
  void containsWorks() {
    DoublyLinkedList<Integer> list = new DoublyLinkedList<>();
    list.addLast(10);
    list.addLast(20);

    assertTrue(list.contains(20));
    assertFalse(list.contains(99));
  }

  @Test
  void removeLastThrowsOnEmptyList() {
    DoublyLinkedList<Integer> list = new DoublyLinkedList<>();
    assertThrows(IllegalStateException.class, list::removeLast);
  }
}
