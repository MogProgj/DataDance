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
  void rejectsNullKey() {
    HashTableChaining<String, Integer> table = new HashTableChaining<>();

    assertThrows(IllegalArgumentException.class, () -> table.put(null, 123));
    assertThrows(IllegalArgumentException.class, () -> table.get(null));
    assertThrows(IllegalArgumentException.class, () -> table.containsKey(null));
    assertThrows(IllegalArgumentException.class, () -> table.remove(null));
  }

  @Test
  void allowsLoadFactorAboveOne() {
    // Separate chaining permits α > 1 (chains grow arbitrarily)
    HashTableChaining<Integer, Integer> table = new HashTableChaining<>(4, 2.0f,
            HashManager.HashType.DIVISION);

    for (int i = 0; i < 10; i++) {
      table.put(i, i * 10);
    }
    // With α = 2.0, a 4-slot table holds 8 elements before rehashing
    assertEquals(10, table.size());
    for (int i = 0; i < 10; i++) {
      assertEquals(i * 10, table.get(i));
    }
    assertTrue(table.checkInvariant());
  }

  @Test
  void integerMinValueHashCodeDoesNotCrash() {
    // Regression: Math.abs(Integer.MIN_VALUE) returns Integer.MIN_VALUE (negative)
    // which caused ArrayIndexOutOfBoundsException before the & 0x7fffffff fix
    HashTableChaining<Integer, Integer> table = new HashTableChaining<>();
    int keyWithMinHash = Integer.MIN_VALUE; // Integer.hashCode(MIN_VALUE) == MIN_VALUE

    assertDoesNotThrow(() -> table.put(keyWithMinHash, 42));
    assertEquals(42, table.get(keyWithMinHash));
    assertTrue(table.checkInvariant());
  }

  @Test
  void maxChainSizeTracksAccurateLength() {
    // Use small capacity with high load factor to force chains
    HashTableChaining<Integer, Integer> table = new HashTableChaining<>(1, 10.0f,
            HashManager.HashType.DIVISION);

    // All keys hash to index 0 (capacity=1, everything mod 1 = 0)
    table.put(1, 10);
    table.put(2, 20);
    table.put(3, 30);

    assertEquals(3, table.getMaxChainSize());

    // A get() miss should NOT inflate maxChainSize
    table.get(99);
    assertEquals(3, table.getMaxChainSize());
  }
}
