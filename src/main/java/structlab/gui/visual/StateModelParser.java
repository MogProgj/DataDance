package structlab.gui.visual;

import structlab.render.SnapshotParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Parses raw snapshot strings into typed view models for the visual state components.
 * Delegates low-level field extraction to {@link SnapshotParser}.
 */
public final class StateModelParser {

    private StateModelParser() {}

    /**
     * Returns the structure type prefix from a snapshot string.
     */
    public static String structureType(String snapshot) {
        return SnapshotParser.type(snapshot);
    }

    /**
     * Parses an ArrayStack snapshot into a StackStateModel.
     * ArrayStack stores elements bottom-to-top in a DynamicArray.
     * Snapshot: ArrayStack{size=N, top=V, elements=DynamicArray{size=N, capacity=N, elements=[...], raw=[...]}}
     */
    public static StackStateModel parseArrayStack(String snapshot) {
        int size = SnapshotParser.intField(snapshot, "size");
        String top = SnapshotParser.stringField(snapshot, "top");
        String embedded = SnapshotParser.embeddedSnapshot(snapshot, "elements");
        List<String> elements = SnapshotParser.listField(embedded, "elements");
        return new StackStateModel(elements, size, top);
    }

    /**
     * Parses a LinkedStack snapshot into a StackStateModel.
     * LinkedStack chain is top-to-bottom, so we reverse for bottom-to-top.
     * Snapshot: LinkedStack{size=N, top=V, chain=[top -> ... -> bottom]}
     */
    public static StackStateModel parseLinkedStack(String snapshot) {
        int size = SnapshotParser.intField(snapshot, "size");
        String top = SnapshotParser.stringField(snapshot, "top");
        List<String> chain = SnapshotParser.chainField(snapshot, "chain");
        List<String> bottomToTop = new ArrayList<>(chain);
        Collections.reverse(bottomToTop);
        return new StackStateModel(bottomToTop, size, top);
    }

    /**
     * Parses a CircularArrayQueue snapshot.
     * Snapshot: CircularArrayQueue{size=N, capacity=N, frontIndex=N, logical=[...], raw=[...]}
     */
    public static CircularQueueStateModel parseCircularArrayQueue(String snapshot) {
        int size = SnapshotParser.intField(snapshot, "size");
        int capacity = SnapshotParser.intField(snapshot, "capacity");
        int frontIndex = SnapshotParser.intField(snapshot, "frontIndex");
        List<String> logical = SnapshotParser.listField(snapshot, "logical");
        List<String> raw = SnapshotParser.listField(snapshot, "raw");
        return new CircularQueueStateModel(raw, logical, size, capacity, frontIndex);
    }

    /**
     * Parses a LinkedQueue snapshot into a QueueStateModel.
     * Snapshot: LinkedQueue{size=N, front=V, rear=V, chain=[front -> ... -> rear]}
     */
    public static QueueStateModel parseLinkedQueue(String snapshot) {
        int size = SnapshotParser.intField(snapshot, "size");
        String front = SnapshotParser.stringField(snapshot, "front");
        String rear = SnapshotParser.stringField(snapshot, "rear");
        List<String> chain = SnapshotParser.chainField(snapshot, "chain");
        return new QueueStateModel(chain, size, front, rear);
    }

    /**
     * Parses a TwoStackQueue snapshot into a QueueStateModel.
     * Snapshot: TwoStackQueue{size=N, inbox=ArrayStack{...}, outbox=ArrayStack{...}}
     * Queue order is: outbox (top-to-bottom) then inbox (bottom-to-top).
     */
    public static QueueStateModel parseTwoStackQueue(String snapshot) {
        int size = SnapshotParser.intField(snapshot, "size");
        String inboxSnap = SnapshotParser.embeddedSnapshot(snapshot, "inbox");
        String outboxSnap = SnapshotParser.embeddedSnapshot(snapshot, "outbox");

        List<String> inboxElements = SnapshotParser.listField(
                SnapshotParser.embeddedSnapshot(inboxSnap, "elements"), "elements");
        List<String> outboxElements = SnapshotParser.listField(
                SnapshotParser.embeddedSnapshot(outboxSnap, "elements"), "elements");

        // Queue order: outbox top-to-bottom (reversed), then inbox bottom-to-top
        List<String> queueOrder = new ArrayList<>();
        List<String> outboxReversed = new ArrayList<>(outboxElements);
        Collections.reverse(outboxReversed);
        queueOrder.addAll(outboxReversed);
        queueOrder.addAll(inboxElements);

        String front = queueOrder.isEmpty() ? "null" : queueOrder.get(0);
        String rear = queueOrder.isEmpty() ? "null" : queueOrder.get(queueOrder.size() - 1);

        return new QueueStateModel(queueOrder, size, front, rear);
    }
}
