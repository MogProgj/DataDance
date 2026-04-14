package structlab.core.hash;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashSetCustomTest {

  @Test
  void newSetStartsEmpty() {
    HashSetCustom<Integer> set = new HashSetCustom<>();

    assertEquals(0, set.size());
    assertTrue(set.isEmpty());
    assertTrue(set.checkInvariant());
  }

  @Test
  void addAndContainsWork() {
    HashSetCustom<Integer> set = new HashSetCustom<>();

    assertTrue(set.add(10));
    assertTrue(set.add(20));

    assertTrue(set.contains(10));
    assertTrue(set.contains(20));
    assertFalse(set.contains(99));
    assertTrue(set.checkInvariant());
  }

  @Test
  void addingDuplicateReturnsFalse() {
    HashSetCustom<Integer> set = new HashSetCustom<>();

    assertTrue(set.add(10));
    assertFalse(set.add(10));

    assertEquals(1, set.size());
    assertTrue(set.checkInvariant());
  }

  @Test
  void removeWorks() {
    HashSetCustom<Integer> set = new HashSetCustom<>();

    set.add(10);
    set.add(20);

    assertTrue(set.remove(10));
    assertFalse(set.contains(10));
    assertEquals(1, set.size());
    assertTrue(set.checkInvariant());
  }

  @Test
  void removeMissingValueReturnsFalse() {
    HashSetCustom<Integer> set = new HashSetCustom<>();

    assertFalse(set.remove(999));
    assertTrue(set.checkInvariant());
  }

  @Test
  void rejectsNullValue() {
    HashSetCustom<String> set = new HashSetCustom<>();

    assertThrows(IllegalArgumentException.class, () -> set.add(null));
  }
}
