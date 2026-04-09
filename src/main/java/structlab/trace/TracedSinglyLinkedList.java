package structlab.trace;

import structlab.core.list.SinglyLinkedList;

/**
 * Traced wrapper around {@link SinglyLinkedList}.  Each operation captures a
 * {@link TraceStep} with before/after snapshots.  Failed operations (e.g.
 * removing from an empty list) are also traced before re-throwing.
 */
public class TracedSinglyLinkedList<T> {
  private final SinglyLinkedList<T> list;
  private final TraceLog log;

  public TracedSinglyLinkedList(SinglyLinkedList<T> list, TraceLog log) {
    this.list = list;
    this.log = log;
  }

  public void addFirst(T value) {
    String before = list.snapshot();
    boolean wasEmpty = list.isEmpty();

    list.addFirst(value);

    String after = list.snapshot();
    String explanation = wasEmpty
        ? "Added " + value + " at head. List was empty, so " + value + " is both head and tail."
        : "Added " + value + " at head. New node points to previous head.";

    log.add(new TraceStep(
        list.structureName(), list.implementationName(), "addFirst",
        String.valueOf(value), before, after,
        InvariantResult.fromBoolean(list.checkInvariant()),
        "O(1)", explanation));
  }

  public void addLast(T value) {
    String before = list.snapshot();
    boolean wasEmpty = list.isEmpty();

    list.addLast(value);

    String after = list.snapshot();
    String explanation = wasEmpty
        ? "Added " + value + " at tail. List was empty, so " + value + " is both head and tail."
        : "Added " + value + " at tail. Previous tail now links to new node.";

    log.add(new TraceStep(
        list.structureName(), list.implementationName(), "addLast",
        String.valueOf(value), before, after,
        InvariantResult.fromBoolean(list.checkInvariant()),
        "O(1)", explanation));
  }

  public T removeFirst() {
    String before = list.snapshot();

    if (list.isEmpty()) {
      log.add(new TraceStep(
          list.structureName(), list.implementationName(), "removeFirst",
          null, before, before,
          InvariantResult.fromBoolean(list.checkInvariant()),
          "O(1)", "FAILED: Cannot removeFirst from an empty singly linked list."));
      list.removeFirst(); // let it throw
    }

    boolean willBecomeEmpty = list.size() == 1;
    T value = list.removeFirst();

    String after = list.snapshot();
    String explanation = willBecomeEmpty
        ? "Removed " + value + " from head. List is now empty."
        : "Removed " + value + " from head. Head advanced to next node.";

    log.add(new TraceStep(
        list.structureName(), list.implementationName(), "removeFirst",
        null, before, after,
        InvariantResult.fromBoolean(list.checkInvariant()),
        "O(1)", explanation));

    return value;
  }

  public T getFirst() {
    String before = list.snapshot();

    if (list.isEmpty()) {
      log.add(new TraceStep(
          list.structureName(), list.implementationName(), "getFirst",
          null, before, before,
          InvariantResult.fromBoolean(list.checkInvariant()),
          "O(1)", "FAILED: Cannot getFirst from an empty singly linked list."));
      list.getFirst(); // let it throw
    }

    T value = list.getFirst();

    log.add(new TraceStep(
        list.structureName(), list.implementationName(), "getFirst",
        null, before, before,
        InvariantResult.fromBoolean(list.checkInvariant()),
        "O(1)",
        "Peeked at head: " + value + ". No structural change."));

    return value;
  }

  public T getLast() {
    String before = list.snapshot();

    if (list.isEmpty()) {
      log.add(new TraceStep(
          list.structureName(), list.implementationName(), "getLast",
          null, before, before,
          InvariantResult.fromBoolean(list.checkInvariant()),
          "O(1)", "FAILED: Cannot getLast from an empty singly linked list."));
      list.getLast(); // let it throw
    }

    T value = list.getLast();

    log.add(new TraceStep(
        list.structureName(), list.implementationName(), "getLast",
        null, before, before,
        InvariantResult.fromBoolean(list.checkInvariant()),
        "O(1)",
        "Peeked at tail: " + value + ". No structural change."));

    return value;
  }

  public boolean contains(T value) {
    String before = list.snapshot();

    boolean found = list.contains(value);

    log.add(new TraceStep(
        list.structureName(), list.implementationName(), "contains",
        String.valueOf(value), before, before,
        InvariantResult.fromBoolean(list.checkInvariant()),
        "O(n)",
        "Searched for " + value + ": " + (found ? "found" : "not found") + ". Traversed the chain."));

    return found;
  }

  public SinglyLinkedList<T> unwrap() {
    return list;
  }

  public TraceLog traceLog() {
    return log;
  }
}
