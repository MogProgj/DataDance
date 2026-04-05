package structlab.core.list;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SinglyLinkedListTest {

  @Test
  void newListStartsEmpty() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();

    assertEquals(0, list.size());
    assertTrue(list.isEmpty());
    assertTrue(list.checkInvariant());
  }

  @Test
  void addFirstWorks() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();

    list.addFirst(20);
    list.addFirst(10);

    assertEquals(2, list.size());
    assertEquals(10, list.getFirst());
    assertEquals(20, list.getLast());
    assertTrue(list.checkInvariant());
  }

  @Test
  void addLastWorks() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();

    list.addLast(10);
    list.addLast(20);

    assertEquals(2, list.size());
    assertEquals(10, list.getFirst());
    assertEquals(20, list.getLast());
    assertTrue(list.checkInvariant());
  }

  @Test
  void removeFirstWorks() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();

    list.addLast(10);
    list.addLast(20);

    assertEquals(10, list.removeFirst());
    assertEquals(1, list.size());
    assertEquals(20, list.getFirst());
    assertTrue(list.checkInvariant());
  }

  @Test
  void containsWorks() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
    list.addLast(10);
    list.addLast(20);

    assertTrue(list.contains(10));
    assertFalse(list.contains(99));
  }

  @Test
  void removeFirstThrowsOnEmptyList() {
    SinglyLinkedList<Integer> list = new SinglyLinkedList<>();
    assertThrows(IllegalStateException.class, list::removeFirst);
  }
}
