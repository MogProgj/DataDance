package structlab.core.array;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FixedArrayTest {

  @Test
  void newArrayStartsEmpty() {
    FixedArray<Integer> array = new FixedArray<>(4);

    assertEquals(0, array.size());
    assertEquals(4, array.capacity());
    assertTrue(array.isEmpty());
    assertFalse(array.isFull());
    assertTrue(array.checkInvariant());
  }

  @Test
  void appendAddsElementsInOrder() {
    FixedArray<Integer> array = new FixedArray<>(4);

    array.append(10);
    array.append(20);
    array.append(30);

    assertEquals(3, array.size());
    assertEquals(10, array.get(0));
    assertEquals(20, array.get(1));
    assertEquals(30, array.get(2));
    assertTrue(array.checkInvariant());
  }

  @Test
  void appendThrowsWhenFull() {
    FixedArray<Integer> array = new FixedArray<>(2);

    array.append(1);
    array.append(2);

    assertTrue(array.isFull());
    assertThrows(IllegalStateException.class, () -> array.append(3));
  }

  @Test
  void insertAtMiddleShiftsElementsRight() {
    FixedArray<Integer> array = new FixedArray<>(4);

    array.append(1);
    array.append(3);
    array.insert(1, 2);

    assertEquals(3, array.size());
    assertEquals(1, array.get(0));
    assertEquals(2, array.get(1));
    assertEquals(3, array.get(2));
    assertTrue(array.checkInvariant());
  }

  @Test
  void insertThrowsWhenFull() {
    FixedArray<Integer> array = new FixedArray<>(2);

    array.append(1);
    array.append(2);

    assertThrows(IllegalStateException.class, () -> array.insert(0, 99));
  }

  @Test
  void setReplacesElement() {
    FixedArray<String> array = new FixedArray<>(4);

    array.append("a");
    array.set(0, "b");

    assertEquals("b", array.get(0));
    assertTrue(array.checkInvariant());
  }

  @Test
  void removeAtReturnsRemovedElementAndShiftsLeft() {
    FixedArray<Integer> array = new FixedArray<>(4);

    array.append(1);
    array.append(2);
    array.append(3);

    int removed = array.removeAt(1);

    assertEquals(2, removed);
    assertEquals(2, array.size());
    assertEquals(1, array.get(0));
    assertEquals(3, array.get(1));
    assertFalse(array.isFull());
    assertTrue(array.checkInvariant());
  }

  @Test
  void getThrowsOnInvalidIndex() {
    FixedArray<Integer> array = new FixedArray<>(4);

    assertThrows(IndexOutOfBoundsException.class, () -> array.get(0));
  }

  @Test
  void insertThrowsOnInvalidIndex() {
    FixedArray<Integer> array = new FixedArray<>(4);

    assertThrows(IndexOutOfBoundsException.class, () -> array.insert(1, 99));
  }

  @Test
  void removeThrowsOnInvalidIndex() {
    FixedArray<Integer> array = new FixedArray<>(4);

    assertThrows(IndexOutOfBoundsException.class, () -> array.removeAt(0));
  }
}
