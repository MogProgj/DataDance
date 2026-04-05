package structlab.core.hash;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashTableChainingTest {

  @Test
  void newTableStartsEmpty() {
    HashTableChaining<String, Integer> table = new HashTableChaining<>();

    assertEquals(0, table.size());
    assertTrue(table.isEmpty());
    assertTrue(table.checkInvariant());
  }

  @Test
  void putAndGetWork() {
    HashTableChaining<String, Integer> table = new HashTableChaining<>();

    assertNull(table.put("a", 10));
    assertNull(table.put("b", 20));

    assertEquals(2, table.size());
    assertEquals(10, table.get("a"));
    assertEquals(20, table.get("b"));
    assertTrue(table.checkInvariant());
  }

  @Test
  void putUpdatesExistingKey() {
    HashTableChaining<String, Integer> table = new HashTableChaining<>();

    assertNull(table.put("a", 10));
    assertEquals(10, table.put("a", 99));

    assertEquals(1, table.size());
    assertEquals(99, table.get("a"));
    assertTrue(table.checkInvariant());
  }

  @Test
  void containsKeyWorks() {
    HashTableChaining<String, Integer> table = new HashTableChaining<>();

    table.put("x", 1);

    assertTrue(table.containsKey("x"));
    assertFalse(table.containsKey("y"));
  }

  @Test
  void removeWorks() {
    HashTableChaining<String, Integer> table = new HashTableChaining<>();

    table.put("a", 10);
    table.put("b", 20);

    assertEquals(10, table.remove("a"));
    assertNull(table.get("a"));
    assertEquals(1, table.size());
    assertTrue(table.checkInvariant());
  }

  @Test
  void removeMissingKeyReturnsNull() {
    HashTableChaining<String, Integer> table = new HashTableChaining<>();

    assertNull(table.remove("missing"));
    assertTrue(table.checkInvariant());
  }

  @Test
  void resizePreservesEntries() {
    HashTableChaining<Integer, String> table = new HashTableChaining<>(2);

    table.put(1, "one");
    table.put(2, "two");
    table.put(3, "three");
    table.put(4, "four");

    assertEquals("one", table.get(1));
    assertEquals("two", table.get(2));
    assertEquals("three", table.get(3));
    assertEquals("four", table.get(4));
    assertEquals(4, table.size());
    assertTrue(table.checkInvariant());
  }

  @Test
  void supportsNullKey() {
    HashTableChaining<String, Integer> table = new HashTableChaining<>();

    table.put(null, 123);

    assertTrue(table.containsKey(null));
    assertEquals(123, table.get(null));
    assertEquals(123, table.remove(null));
    assertFalse(table.containsKey(null));
    assertTrue(table.checkInvariant());
  }
}
