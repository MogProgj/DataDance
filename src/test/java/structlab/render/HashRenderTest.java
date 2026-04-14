package structlab.render;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HashRenderTest {

  // ---- SnapshotParser: bucketEntries ----

  @Test
  void bucketEntriesParsesEmptyBuckets() {
    String snap = "HashTableChaining{size=0, capacity=4, buckets=[0: empty, 1: empty, 2: empty, 3: empty]}";
    List<String> entries = SnapshotParser.bucketEntries(snap);
    assertEquals(4, entries.size());
    assertEquals("[0] empty", entries.get(0));
    assertEquals("[3] empty", entries.get(3));
  }

  @Test
  void bucketEntriesParsesPopulatedBuckets() {
    String snap = "HashTableChaining{size=2, capacity=4, buckets=[0: empty, 1: (1 -> 100), 2: (2 -> 200), 3: empty]}";
    List<String> entries = SnapshotParser.bucketEntries(snap);
    assertEquals(4, entries.size());
    assertEquals("[1] (1 -> 100)", entries.get(1));
    assertEquals("[2] (2 -> 200)", entries.get(2));
  }

  @Test
  void bucketEntriesParsesChainedCollisions() {
    String snap = "HashTableChaining{size=2, capacity=8, buckets=[0: empty, 1: (9 -> 900) -> (1 -> 100), 2: empty, 3: empty, 4: empty, 5: empty, 6: empty, 7: empty]}";
    List<String> entries = SnapshotParser.bucketEntries(snap);
    assertEquals(8, entries.size());
    assertTrue(entries.get(1).contains("(9 -> 900)"));
    assertTrue(entries.get(1).contains("(1 -> 100)"));
  }

  @Test
  void bucketEntriesReturnsEmptyForNonHashSnapshot() {
    String snap = "DynamicArray{size=0, capacity=4}";
    List<String> entries = SnapshotParser.bucketEntries(snap);
    assertTrue(entries.isEmpty());
  }

  // ---- StructureRenderer: hash table ----

  @Test
  void renderHashTableShowsSizeAndCapacity() {
    String snap = "HashTableChaining{size=2, capacity=8, buckets=[0: empty, 1: (1 -> 100), 2: (2 -> 200), 3: empty, 4: empty, 5: empty, 6: empty, 7: empty]}";
    String rendered = StructureRenderer.renderHashTable(snap);
    assertTrue(rendered.contains("size: 2"));
    assertTrue(rendered.contains("capacity: 8"));
  }

  @Test
  void renderHashTableShowsLoadFactor() {
    String snap = "HashTableChaining{size=4, capacity=8, buckets=[0: empty, 1: (1 -> 100), 2: (2 -> 200), 3: (3 -> 300), 4: (4 -> 400), 5: empty, 6: empty, 7: empty]}";
    String rendered = StructureRenderer.renderHashTable(snap);
    assertTrue(rendered.contains("load: 0.50"));
  }

  @Test
  void renderHashTableShowsBucketEntries() {
    String snap = "HashTableChaining{size=1, capacity=4, buckets=[0: empty, 1: (1 -> 100), 2: empty, 3: empty]}";
    String rendered = StructureRenderer.renderHashTable(snap);
    assertTrue(rendered.contains("[0] empty"));
    assertTrue(rendered.contains("[1] (1 -> 100)"));
  }

  @Test
  void renderHashTableCollisionsVisible() {
    String snap = "HashTableChaining{size=2, capacity=8, buckets=[0: empty, 1: (9 -> 900) -> (1 -> 100), 2: empty, 3: empty, 4: empty, 5: empty, 6: empty, 7: empty]}";
    String rendered = StructureRenderer.renderHashTable(snap);
    assertTrue(rendered.contains("(9 -> 900) -> (1 -> 100)"));
  }

  // ---- StructureRenderer: hash set ----

  @Test
  void renderHashSetShowsSizeAndBackingNote() {
    String snap = "HashSetCustom{size=2, table=HashTableChaining{size=2, capacity=8, buckets=[0: empty, 1: empty, 2: (2 -> java.lang.Object@abc), 3: empty, 4: empty, 5: empty, 6: empty, 7: empty]}}";
    String rendered = StructureRenderer.renderHashSet(snap);
    assertTrue(rendered.contains("size: 2"));
    assertTrue(rendered.contains("backed by HashTableChaining"));
  }

  // ---- StructureRenderer: dispatch ----

  @Test
  void renderDispatchesHashTableChaining() {
    String snap = "HashTableChaining{size=0, capacity=8, buckets=[0: empty, 1: empty, 2: empty, 3: empty, 4: empty, 5: empty, 6: empty, 7: empty]}";
    String rendered = StructureRenderer.render(snap);
    assertTrue(rendered.contains("HashTableChaining"));
    assertTrue(rendered.contains("size: 0"));
  }

  @Test
  void renderDispatchesHashSetCustom() {
    String snap = "HashSetCustom{size=0, table=HashTableChaining{size=0, capacity=8, buckets=[0: empty, 1: empty, 2: empty, 3: empty, 4: empty, 5: empty, 6: empty, 7: empty]}}";
    String rendered = StructureRenderer.render(snap);
    assertTrue(rendered.contains("HashSetCustom"));
  }
}
