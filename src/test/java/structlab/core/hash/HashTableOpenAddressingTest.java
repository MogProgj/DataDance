package structlab.core.hash;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashTableOpenAddressingTest {

    private static final HashTableOpenAddressing.OpenAddressingType[] ALL_TYPES =
            HashTableOpenAddressing.OpenAddressingType.values();

    @Test
    void newTableStartsEmpty() {
        for (var oaType : ALL_TYPES) {
            var table = new HashTableOpenAddressing<Integer, Integer>(oaType);
            assertTrue(table.isEmpty(), oaType.name());
            assertEquals(0, table.size(), oaType.name());
            assertTrue(table.checkInvariant(), oaType.name());
        }
    }

    @Test
    void putAndGetBasic() {
        for (var oaType : ALL_TYPES) {
            var table = new HashTableOpenAddressing<Integer, Integer>(oaType);
            assertNull(table.put(1, 100));
            assertNull(table.put(2, 200));

            assertEquals(100, table.get(1), oaType.name());
            assertEquals(200, table.get(2), oaType.name());
            assertNull(table.get(99), oaType.name());
            assertEquals(2, table.size(), oaType.name());
            assertTrue(table.checkInvariant(), oaType.name());
        }
    }

    @Test
    void putUpdatesExistingKey() {
        for (var oaType : ALL_TYPES) {
            var table = new HashTableOpenAddressing<Integer, Integer>(oaType);
            table.put(1, 100);
            assertEquals(100, table.put(1, 999), oaType.name());
            assertEquals(999, table.get(1), oaType.name());
            assertEquals(1, table.size(), oaType.name());
            assertTrue(table.checkInvariant(), oaType.name());
        }
    }

    @Test
    void containsKeyWorks() {
        for (var oaType : ALL_TYPES) {
            var table = new HashTableOpenAddressing<Integer, Integer>(oaType);
            table.put(5, 50);
            assertTrue(table.containsKey(5), oaType.name());
            assertFalse(table.containsKey(6), oaType.name());
        }
    }

    @Test
    void removeExistingKey() {
        for (var oaType : ALL_TYPES) {
            var table = new HashTableOpenAddressing<Integer, Integer>(oaType);
            table.put(1, 100);
            table.put(2, 200);

            assertEquals(100, table.remove(1), oaType.name());
            assertNull(table.get(1), oaType.name());
            assertEquals(1, table.size(), oaType.name());
            assertTrue(table.checkInvariant(), oaType.name());
        }
    }

    @Test
    void removeNonExistentKeyReturnsNull() {
        for (var oaType : ALL_TYPES) {
            var table = new HashTableOpenAddressing<Integer, Integer>(oaType);
            assertNull(table.remove(42), oaType.name());
            assertTrue(table.checkInvariant(), oaType.name());
        }
    }

    @Test
    void removeAndReinsert() {
        for (var oaType : ALL_TYPES) {
            var table = new HashTableOpenAddressing<Integer, Integer>(oaType);
            table.put(1, 100);
            table.put(9, 900);

            table.remove(1);
            assertEquals(900, table.get(9), oaType.name());
            table.put(1, 111);
            assertEquals(111, table.get(1), oaType.name());
            assertTrue(table.checkInvariant(), oaType.name());
        }
    }

    @Test
    void clearResetsTable() {
        for (var oaType : ALL_TYPES) {
            var table = new HashTableOpenAddressing<Integer, Integer>(oaType);
            table.put(1, 100);
            table.put(2, 200);
            table.clear();

            assertTrue(table.isEmpty(), oaType.name());
            assertEquals(0, table.size(), oaType.name());
            assertNull(table.get(1), oaType.name());
            assertTrue(table.checkInvariant(), oaType.name());
        }
    }

    @Test
    void resizePreservesElements() {
        for (var oaType : ALL_TYPES) {
            var table = new HashTableOpenAddressing<>(4, 0.5f,
                    HashManager.HashType.DIVISION, oaType);
            table.put(1, 10);
            table.put(2, 20);
            table.put(3, 30);
            table.put(4, 40);

            assertEquals(10, table.get(1), oaType.name());
            assertEquals(20, table.get(2), oaType.name());
            assertEquals(30, table.get(3), oaType.name());
            assertEquals(40, table.get(4), oaType.name());
            assertEquals(4, table.size(), oaType.name());
            assertTrue(table.checkInvariant(), oaType.name());
        }
    }

    @Test
    void rejectsNullKeyAndValue() {
        for (var oaType : ALL_TYPES) {
            var table = new HashTableOpenAddressing<Integer, Integer>(oaType);
            assertThrows(IllegalArgumentException.class, () -> table.put(null, 1));
            assertThrows(IllegalArgumentException.class, () -> table.put(1, null));
            assertThrows(IllegalArgumentException.class, () -> table.get(null));
            assertThrows(IllegalArgumentException.class, () -> table.containsKey(null));
            assertThrows(IllegalArgumentException.class, () -> table.remove(null));
        }
    }

    @Test
    void snapshotShowsSlots() {
        for (var oaType : ALL_TYPES) {
            var table = new HashTableOpenAddressing<Integer, Integer>(oaType);
            table.put(1, 100);
            String snap = table.snapshot();
            assertTrue(snap.startsWith("HashTableOpenAddressing{"), oaType.name());
            assertTrue(snap.contains("size=1"), oaType.name());
            assertTrue(snap.contains("oaType=" + oaType), oaType.name());
            assertTrue(snap.contains("(1 -> 100)"), oaType.name());
        }
    }

    @Test
    void deletedSlotsShowInSnapshot() {
        for (var oaType : ALL_TYPES) {
            var table = new HashTableOpenAddressing<Integer, Integer>(oaType);
            table.put(1, 100);
            table.remove(1);
            String snap = table.snapshot();
            assertTrue(snap.contains("DELETED"), oaType.name());
        }
    }

    @Test
    void replaceUpdatesValue() {
        var table = new HashTableOpenAddressing<Integer, Integer>();
        table.put(1, 100);
        assertTrue(table.replace(1, 100, 999));
        assertEquals(999, table.get(1));
        assertFalse(table.replace(1, 100, 888));
    }

    @Test
    void containsValueWorks() {
        var table = new HashTableOpenAddressing<Integer, Integer>();
        table.put(1, 100);
        assertTrue(table.containsValue(100));
        assertFalse(table.containsValue(999));
    }

    @Test
    void keySetAndValuesWork() {
        var table = new HashTableOpenAddressing<Integer, Integer>();
        table.put(1, 100);
        table.put(2, 200);
        assertEquals(2, table.keySet().size());
        assertTrue(table.keySet().contains(1));
        assertTrue(table.keySet().contains(2));
        assertEquals(2, table.values().size());
        assertTrue(table.values().contains(100));
        assertTrue(table.values().contains(200));
    }

    @Test
    void rejectsLoadFactorOneOrAbove() {
        // Open addressing requires α < 1 (Sedgewick §3.4)
        assertThrows(IllegalArgumentException.class,
                () -> new HashTableOpenAddressing<>(8, 1.0f,
                        HashManager.HashType.DIVISION,
                        HashTableOpenAddressing.OpenAddressingType.LINEAR));
        assertThrows(IllegalArgumentException.class,
                () -> new HashTableOpenAddressing<>(8, 1.5f,
                        HashManager.HashType.DIVISION,
                        HashTableOpenAddressing.OpenAddressingType.LINEAR));
    }

    @Test
    void doubleHashingProbesAllSlots() {
        // Regression: old implementation re-probed the home slot at i=0
        var table = new HashTableOpenAddressing<>(8, 0.5f,
                HashManager.HashType.DIVISION,
                HashTableOpenAddressing.OpenAddressingType.DOUBLE_HASHING);
        // Fill to trigger probing — keys that collide at the same index
        for (int k = 0; k < 4; k++) {
            table.put(k, k * 10);
        }
        for (int k = 0; k < 4; k++) {
            assertEquals(k * 10, table.get(k), "key=" + k);
        }
        assertTrue(table.checkInvariant());
    }

    @Test
    void hashManagerTypesWork() {
        for (HashManager.HashType ht : HashManager.HashType.values()) {
            var table = new HashTableOpenAddressing<>(
                    8, 0.5f, ht, HashTableOpenAddressing.OpenAddressingType.LINEAR);
            table.put(1, 100);
            table.put(2, 200);
            assertEquals(100, table.get(1), ht.name());
            assertEquals(200, table.get(2), ht.name());
            assertTrue(table.checkInvariant(), ht.name());
        }
    }

    @Test
    void integerMinValueHashCodeDoesNotCrash() {
        // Regression: Math.abs(Integer.MIN_VALUE) pitfall
        for (var oaType : ALL_TYPES) {
            var table = new HashTableOpenAddressing<Integer, Integer>(oaType);
            assertDoesNotThrow(() -> table.put(Integer.MIN_VALUE, 42), oaType.name());
            assertEquals(42, table.get(Integer.MIN_VALUE), oaType.name());
            assertTrue(table.checkInvariant(), oaType.name());
        }
    }
}
