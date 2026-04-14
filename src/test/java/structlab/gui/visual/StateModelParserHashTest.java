package structlab.gui.visual;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StateModelParserHashTest {

    // ── HashTableChaining parsing ───────────────────────────────

    @Test
    void parseEmptyHashTableChaining() {
        String snap = "HashTableChaining{size=0, capacity=4, hashType=DIVISION, maxChainSize=0, rehashes=0, buckets=[0: empty, 1: empty, 2: empty, 3: empty]}";
        HashChainingStateModel model = StateModelParser.parseHashTableChaining(snap);
        assertEquals(0, model.size());
        assertEquals(4, model.capacity());
        assertEquals("DIVISION", model.hashType());
        assertEquals(0, model.maxChainSize());
        assertEquals(4, model.buckets().size());
        assertTrue(model.buckets().stream().allMatch(HashChainingStateModel.Bucket::isEmpty));
    }

    @Test
    void parsePopulatedHashTableChaining() {
        String snap = "HashTableChaining{size=2, capacity=8, hashType=DIVISION, maxChainSize=1, rehashes=0, buckets=[0: empty, 1: (1 -> 100), 2: (2 -> 200), 3: empty, 4: empty, 5: empty, 6: empty, 7: empty]}";
        HashChainingStateModel model = StateModelParser.parseHashTableChaining(snap);
        assertEquals(2, model.size());
        assertEquals(8, model.capacity());
        assertEquals(8, model.buckets().size());
        assertEquals(2, model.occupiedCount());

        // Bucket 1 has one entry
        var b1 = model.buckets().get(1);
        assertEquals(1, b1.chainLength());
        assertEquals("1", b1.entries().get(0).key());
        assertEquals("100", b1.entries().get(0).value());
    }

    @Test
    void parseChainingWithCollisions() {
        String snap = "HashTableChaining{size=2, capacity=8, hashType=DIVISION, maxChainSize=2, rehashes=0, buckets=[0: empty, 1: (9 -> 900) -> (1 -> 100), 2: empty, 3: empty, 4: empty, 5: empty, 6: empty, 7: empty]}";
        HashChainingStateModel model = StateModelParser.parseHashTableChaining(snap);
        assertEquals(2, model.size());
        assertEquals(1, model.collisionBuckets());

        var b1 = model.buckets().get(1);
        assertEquals(2, b1.chainLength());
        assertEquals("9", b1.entries().get(0).key());
        assertEquals("1", b1.entries().get(1).key());
    }

    @Test
    void parseChainingWithoutMetadataFields() {
        // Older snapshot format without hashType/maxChainSize/rehashes
        String snap = "HashTableChaining{size=0, capacity=4, buckets=[0: empty, 1: empty, 2: empty, 3: empty]}";
        HashChainingStateModel model = StateModelParser.parseHashTableChaining(snap);
        assertEquals(0, model.size());
        assertEquals(4, model.capacity());
        assertEquals(4, model.buckets().size());
        // Missing fields default gracefully
        assertEquals(0, model.maxChainSize());
        assertEquals(0, model.rehashes());
    }

    // ── HashTableOpenAddressing parsing ─────────────────────────

    @Test
    void parseEmptyOpenAddressing() {
        String snap = "HashTableOpenAddressing{size=0, capacity=8, oaType=LINEAR, hashType=DIVISION, rehashes=0, slots=[0: empty, 1: empty, 2: empty, 3: empty, 4: empty, 5: empty, 6: empty, 7: empty]}";
        HashOpenAddressingStateModel model = StateModelParser.parseHashTableOpenAddressing(snap);
        assertEquals(0, model.size());
        assertEquals(8, model.capacity());
        assertEquals("LINEAR", model.oaType());
        assertEquals("DIVISION", model.hashType());
        assertEquals(8, model.slots().size());
        assertTrue(model.isEmpty());
    }

    @Test
    void parsePopulatedOpenAddressing() {
        String snap = "HashTableOpenAddressing{size=2, capacity=8, oaType=LINEAR, hashType=DIVISION, rehashes=0, slots=[0: empty, 1: (1 -> 10), 2: (2 -> 20), 3: empty, 4: empty, 5: empty, 6: empty, 7: empty]}";
        HashOpenAddressingStateModel model = StateModelParser.parseHashTableOpenAddressing(snap);
        assertEquals(2, model.size());
        assertEquals(2, model.occupiedCount());
        assertEquals(6, model.emptyCount());

        var slot1 = model.slots().get(1);
        assertTrue(slot1.isOccupied());
        assertEquals("1", slot1.key());
        assertEquals("10", slot1.value());
    }

    @Test
    void parseOpenAddressingWithTombstones() {
        String snap = "HashTableOpenAddressing{size=1, capacity=8, oaType=QUADRATIC, hashType=DIVISION, rehashes=0, slots=[0: empty, 1: (1 -> 10), 2: DELETED, 3: empty, 4: empty, 5: empty, 6: empty, 7: empty]}";
        HashOpenAddressingStateModel model = StateModelParser.parseHashTableOpenAddressing(snap);
        assertEquals(1, model.size());
        assertEquals(1, model.occupiedCount());
        assertEquals(1, model.deletedCount());

        var slot2 = model.slots().get(2);
        assertTrue(slot2.isDeleted());
    }

    // ── HashSetCustom parsing ───────────────────────────────────

    @Test
    void parseEmptyHashSet() {
        String snap = "HashSetCustom{size=0, table=HashTableChaining{size=0, capacity=8, hashType=DIVISION, maxChainSize=0, rehashes=0, buckets=[0: empty, 1: empty, 2: empty, 3: empty, 4: empty, 5: empty, 6: empty, 7: empty]}}";
        HashSetStateModel model = StateModelParser.parseHashSetCustom(snap);
        assertEquals(0, model.size());
        assertEquals(8, model.capacity());
        assertTrue(model.isEmpty());
        assertEquals(8, model.buckets().size());
    }

    @Test
    void parsePopulatedHashSet() {
        String snap = "HashSetCustom{size=2, table=HashTableChaining{size=2, capacity=8, hashType=DIVISION, maxChainSize=1, rehashes=0, buckets=[0: empty, 1: empty, 2: (2 -> java.lang.Object@abc), 3: (3 -> java.lang.Object@def), 4: empty, 5: empty, 6: empty, 7: empty]}}";
        HashSetStateModel model = StateModelParser.parseHashSetCustom(snap);
        assertEquals(2, model.size());
        assertEquals(8, model.capacity());
        assertEquals(2, model.occupiedCount());

        // Bucket 2 has member "2" (key only, value stripped)
        var b2 = model.buckets().get(2);
        assertFalse(b2.isEmpty());
        assertEquals(1, b2.memberCount());
        assertEquals("2", b2.members().get(0));
    }

    @Test
    void parseHashSetWithoutMetadataFields() {
        String snap = "HashSetCustom{size=0, table=HashTableChaining{size=0, capacity=8, buckets=[0: empty, 1: empty, 2: empty, 3: empty, 4: empty, 5: empty, 6: empty, 7: empty]}}";
        HashSetStateModel model = StateModelParser.parseHashSetCustom(snap);
        assertEquals(0, model.size());
        assertEquals(8, model.capacity());
        assertEquals(8, model.buckets().size());
    }

    // ── structureType detection ─────────────────────────────────

    @Test
    void structureTypeDetectsHashTypes() {
        assertEquals("HashTableChaining", StateModelParser.structureType("HashTableChaining{size=0}"));
        assertEquals("HashTableOpenAddressing", StateModelParser.structureType("HashTableOpenAddressing{size=0}"));
        assertEquals("HashSetCustom", StateModelParser.structureType("HashSetCustom{size=0}"));
    }
}
