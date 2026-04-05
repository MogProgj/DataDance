package structlab.core.list;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CircularLinkedListTest {

  @Test
  void newListStartsEmpty() {
    CircularLinkedList<Integer> list = new CircularLinkedList<>();

    assertEquals(0, list.size());
    assertTrue(list.isEmpty());
    assertTrue(list.checkInvariant());
  }

  @Test
  void addFirstWorks() {
    CircularLinkedList<Integer> list = new CircularLinkedList<>();

    list.addFirst(20);
    list.addFirst(10);

    assertEquals(2, list.size());
    assertEquals(10, list.getFirst());
    assertEquals(20, list.getLast());
    assertTrue(list.checkInvariant());
  }

  @Test
  void addLastWorks() {
    CircularLinkedList<Integer> list = new CircularLinkedList<>();

    list.addLast(10);
    list.addLast(20);

    assertEquals(2, list.size());
    assertEquals(10, list.getFirst());
    assertEquals(20, list.getLast());
    assertTrue(list.checkInvariant());
  }

  @Test
  void removeFirstWorks() {
    CircularLinkedList<Integer> list = new CircularLinkedList<>();

    list.addLast(10);
    list.addLast(20);

    assertEquals(10, list.removeFirst());
    assertEquals(1, list.size());
    assertEquals(20, list.getFirst());
    assertTrue(list.checkInvariant());
  }

  @Test
  void containsWorks() {
    CircularLinkedList<Integer> list = new CircularLinkedList<>();
    list.addLast(10);
    list.addLast(20);

    assertTrue(list.contains(20));
    assertFalse(list.contains(99));
  }

  @Test
  void removeFirstThrowsOnEmptyList() {
    CircularLinkedList<Integer> list = new CircularLinkedList<>();
    assertThrows(IllegalStateException.class, list::removeFirst);
  }
}
