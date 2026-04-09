package structlab.render;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SnapshotParserTest {

  @Test
  void typeExtractsPrefix() {
    assertEquals("DynamicArray", SnapshotParser.type("DynamicArray{size=0}"));
    assertEquals("LinkedStack", SnapshotParser.type("LinkedStack{size=2}"));
  }

  @Test
  void intFieldExtractsInteger() {
    String snap = "DynamicArray{size=3, capacity=4}";
    assertEquals(3, SnapshotParser.intField(snap, "size"));
    assertEquals(4, SnapshotParser.intField(snap, "capacity"));
  }

  @Test
  void intFieldReturnsNegativeOneForMissing() {
    assertEquals(-1, SnapshotParser.intField("DynamicArray{size=0}", "missing"));
  }

  @Test
  void stringFieldExtractsValue() {
    String snap = "ArrayStack{size=2, top=20}";
    assertEquals("20", SnapshotParser.stringField(snap, "top"));
  }

  @Test
  void listFieldExtractsElements() {
    String snap = "DynamicArray{elements=[10, 20, 30], raw=[10, 20, 30, null]}";
    assertEquals(List.of("10", "20", "30"), SnapshotParser.listField(snap, "elements"));
    assertEquals(List.of("10", "20", "30", "null"), SnapshotParser.listField(snap, "raw"));
  }

  @Test
  void listFieldReturnsEmptyForEmptyBrackets() {
    String snap = "DynamicArray{elements=[], raw=[null]}";
    assertEquals(List.of(), SnapshotParser.listField(snap, "elements"));
  }

  @Test
  void chainFieldExtractsLinkedElements() {
    String snap = "LinkedStack{chain=[30 -> 20 -> 10]}";
    assertEquals(List.of("30", "20", "10"), SnapshotParser.chainField(snap, "chain"));
  }

  @Test
  void chainFieldReturnsEmptyForEmptyChain() {
    String snap = "LinkedStack{chain=[]}";
    assertEquals(List.of(), SnapshotParser.chainField(snap, "chain"));
  }

  @Test
  void embeddedSnapshotExtractsNestedBraces() {
    String snap = "ArrayStack{size=2, top=20, elements=DynamicArray{size=2, capacity=4, elements=[10, 20]}}";
    String embedded = SnapshotParser.embeddedSnapshot(snap, "elements");
    assertTrue(embedded.startsWith("DynamicArray{"));
    assertTrue(embedded.endsWith("}"));
    assertEquals(2, SnapshotParser.intField(embedded, "size"));
  }

  @Test
  void doublyLinkedChainFieldExtractsElements() {
    String snap = "DoublyLinkedList{chain=[10 <-> 20 <-> 30]}";
    assertEquals(List.of("10", "20", "30"), SnapshotParser.doublyLinkedChainField(snap, "chain"));
  }

  @Test
  void doublyLinkedChainFieldReturnsEmptyForEmptyChain() {
    String snap = "DoublyLinkedList{chain=[]}";
    assertEquals(List.of(), SnapshotParser.doublyLinkedChainField(snap, "chain"));
  }

  @Test
  void embeddedSnapshotHandlesDoubleNesting() {
    String snap = "TwoStackQueue{size=1, inbox=ArrayStack{size=1, elements=DynamicArray{size=1}}, outbox=ArrayStack{size=0}}";
    String inbox = SnapshotParser.embeddedSnapshot(snap, "inbox");
    assertTrue(inbox.startsWith("ArrayStack{"));
    assertEquals(1, SnapshotParser.intField(inbox, "size"));
  }
}
