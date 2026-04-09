package structlab.trace;

import structlab.core.deque.ArrayDequeCustom;

/**
 * Traced wrapper around {@link ArrayDequeCustom}.  Each operation captures a
 * {@link TraceStep} with before/after snapshots.  Failed operations (e.g.
 * removing from an empty deque) are also traced before re-throwing.
 */
public class TracedArrayDequeCustom<T> {
  private final ArrayDequeCustom<T> deque;
  private final TraceLog log;

  public TracedArrayDequeCustom(ArrayDequeCustom<T> deque, TraceLog log) {
    this.deque = deque;
    this.log = log;
  }

  public void addFirst(T value) {
    String before = deque.snapshot();
    int capacityBefore = deque.capacity();

    deque.addFirst(value);

    String after = deque.snapshot();
    int capacityAfter = deque.capacity();

    String explanation;
    if (capacityAfter > capacityBefore) {
      explanation = "Added " + value + " at front. Array resized from " + capacityBefore + " to " + capacityAfter + ".";
    } else {
      // Parse frontIndex from after snapshot to detect wraparound
      int frontAfter = parseFrontIndex(after);
      explanation = "Added " + value + " at front (index " + frontAfter + ").";
      if (frontAfter == capacityAfter - 1) {
        explanation = "Added " + value + " at front (index " + frontAfter + "). Front index wrapped around.";
      }
    }

    log.add(new TraceStep(
        deque.structureName(), deque.implementationName(), "addFirst",
        String.valueOf(value), before, after,
        InvariantResult.fromBoolean(deque.checkInvariant()),
        "O(1) amortized", explanation));
  }

  public void addLast(T value) {
    String before = deque.snapshot();
    int capacityBefore = deque.capacity();

    deque.addLast(value);

    String after = deque.snapshot();
    int capacityAfter = deque.capacity();

    String explanation;
    if (capacityAfter > capacityBefore) {
      explanation = "Added " + value + " at rear. Array resized from " + capacityBefore + " to " + capacityAfter + ".";
    } else {
      int rearAfter = parseRearIndex(after);
      explanation = "Added " + value + " at rear (index " + rearAfter + ").";
      if (rearAfter == 0 && deque.size() > 1) {
        explanation = "Added " + value + " at rear (index " + rearAfter + "). Rear index wrapped around.";
      }
    }

    log.add(new TraceStep(
        deque.structureName(), deque.implementationName(), "addLast",
        String.valueOf(value), before, after,
        InvariantResult.fromBoolean(deque.checkInvariant()),
        "O(1) amortized", explanation));
  }

  public T removeFirst() {
    String before = deque.snapshot();

    if (deque.isEmpty()) {
      log.add(new TraceStep(
          deque.structureName(), deque.implementationName(), "removeFirst",
          null, before, before,
          InvariantResult.fromBoolean(deque.checkInvariant()),
          "O(1)", "FAILED: Cannot removeFirst from an empty deque."));
      deque.removeFirst(); // let it throw
    }

    boolean willBecomeEmpty = deque.size() == 1;
    int frontBefore = parseFrontIndex(before);
    T value = deque.removeFirst();

    String after = deque.snapshot();
    String explanation;
    if (willBecomeEmpty) {
      explanation = "Removed " + value + " from front (index " + frontBefore + "). Deque is now empty.";
    } else {
      int frontAfter = parseFrontIndex(after);
      explanation = "Removed " + value + " from front (index " + frontBefore + "). Front advanced to index " + frontAfter + ".";
      if (frontAfter == 0) {
        explanation = "Removed " + value + " from front (index " + frontBefore + "). Front index wrapped around to 0.";
      }
    }

    log.add(new TraceStep(
        deque.structureName(), deque.implementationName(), "removeFirst",
        null, before, after,
        InvariantResult.fromBoolean(deque.checkInvariant()),
        "O(1)", explanation));

    return value;
  }

  public T removeLast() {
    String before = deque.snapshot();

    if (deque.isEmpty()) {
      log.add(new TraceStep(
          deque.structureName(), deque.implementationName(), "removeLast",
          null, before, before,
          InvariantResult.fromBoolean(deque.checkInvariant()),
          "O(1)", "FAILED: Cannot removeLast from an empty deque."));
      deque.removeLast(); // let it throw
    }

    boolean willBecomeEmpty = deque.size() == 1;
    T value = deque.removeLast();

    String after = deque.snapshot();
    String explanation;
    if (willBecomeEmpty) {
      explanation = "Removed " + value + " from rear. Deque is now empty.";
    } else {
      int rearAfter = parseRearIndex(after);
      explanation = "Removed " + value + " from rear. Rear moved to index " + rearAfter + ".";
    }

    log.add(new TraceStep(
        deque.structureName(), deque.implementationName(), "removeLast",
        null, before, after,
        InvariantResult.fromBoolean(deque.checkInvariant()),
        "O(1)", explanation));

    return value;
  }

  public T peekFirst() {
    String before = deque.snapshot();

    if (deque.isEmpty()) {
      log.add(new TraceStep(
          deque.structureName(), deque.implementationName(), "peekFirst",
          null, before, before,
          InvariantResult.fromBoolean(deque.checkInvariant()),
          "O(1)", "FAILED: Cannot peekFirst into an empty deque."));
      deque.peekFirst(); // let it throw
    }

    T value = deque.peekFirst();

    log.add(new TraceStep(
        deque.structureName(), deque.implementationName(), "peekFirst",
        null, before, before,
        InvariantResult.fromBoolean(deque.checkInvariant()),
        "O(1)",
        "Peeked at front of deque: " + value + ". No structural change."));

    return value;
  }

  public T peekLast() {
    String before = deque.snapshot();

    if (deque.isEmpty()) {
      log.add(new TraceStep(
          deque.structureName(), deque.implementationName(), "peekLast",
          null, before, before,
          InvariantResult.fromBoolean(deque.checkInvariant()),
          "O(1)", "FAILED: Cannot peekLast into an empty deque."));
      deque.peekLast(); // let it throw
    }

    T value = deque.peekLast();

    log.add(new TraceStep(
        deque.structureName(), deque.implementationName(), "peekLast",
        null, before, before,
        InvariantResult.fromBoolean(deque.checkInvariant()),
        "O(1)",
        "Peeked at rear of deque: " + value + ". No structural change."));

    return value;
  }

  public ArrayDequeCustom<T> unwrap() {
    return deque;
  }

  public TraceLog traceLog() {
    return log;
  }

  /** Extracts the frontIndex value from a snapshot string. */
  private int parseFrontIndex(String snapshot) {
    String marker = "frontIndex=";
    int start = snapshot.indexOf(marker);
    if (start < 0) {
      return -1;
    }
    start += marker.length();
    int end = snapshot.indexOf(',', start);
    if (end < 0) {
      end = snapshot.indexOf('}', start);
    }
    return Integer.parseInt(snapshot.substring(start, end).trim());
  }

  /** Computes the rear index from frontIndex and size parsed from the snapshot. */
  private int parseRearIndex(String snapshot) {
    int frontIndex = parseFrontIndex(snapshot);
    String sizeMarker = "size=";
    int start = snapshot.indexOf(sizeMarker);
    if (start < 0) {
      return -1;
    }
    start += sizeMarker.length();
    int end = snapshot.indexOf(',', start);
    int sz = Integer.parseInt(snapshot.substring(start, end).trim());

    String capMarker = "capacity=";
    int cStart = snapshot.indexOf(capMarker);
    if (cStart < 0) {
      return -1;
    }
    cStart += capMarker.length();
    int cEnd = snapshot.indexOf(',', cStart);
    int cap = Integer.parseInt(snapshot.substring(cStart, cEnd).trim());

    return (frontIndex + sz - 1) % cap;
  }
}
