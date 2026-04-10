package structlab.gui.visual;

import structlab.render.SnapshotParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    // ── Heap family ─────────────────────────────────────────────────

    /**
     * Parses a BinaryHeap snapshot into a HeapStateModel.
     * Snapshot: BinaryHeap{size=N, min=V, elements=DynamicArray{size=N, capacity=C, elements=[...], raw=[...]}}
     */
    public static HeapStateModel parseBinaryHeap(String snapshot) {
        int size = SnapshotParser.intField(snapshot, "size");
        String min = SnapshotParser.stringField(snapshot, "min");
        String embedded = SnapshotParser.embeddedSnapshot(snapshot, "elements");
        List<String> elements = SnapshotParser.listField(embedded, "elements");
        int capacity = SnapshotParser.intField(embedded, "capacity");
        return new HeapStateModel(elements, size, min, capacity);
    }

    /**
     * Parses a HeapPriorityQueue snapshot into a HeapStateModel.
     * Snapshot: HeapPriorityQueue{size=N, front=V, heap=BinaryHeap{size=N, min=V, elements=DynamicArray{...}}}
     */
    public static HeapStateModel parseHeapPriorityQueue(String snapshot) {
        String heapSnap = SnapshotParser.embeddedSnapshot(snapshot, "heap");
        return parseBinaryHeap(heapSnap);
    }

    // ── Hash family ─────────────────────────────────────────────────

    private static final Pattern ENTRY_PATTERN = Pattern.compile("\\((.+?)\\s*->\\s*(.+?)\\)");

    /**
     * Parses a HashTableChaining snapshot into a HashChainingStateModel.
     * Snapshot: HashTableChaining{size=N, capacity=M, hashType=T, maxChainSize=X, rehashes=Y, buckets=[...]}
     */
    public static HashChainingStateModel parseHashTableChaining(String snapshot) {
        int size = SnapshotParser.intField(snapshot, "size");
        int capacity = SnapshotParser.intField(snapshot, "capacity");
        String hashType = SnapshotParser.stringField(snapshot, "hashType");
        int maxChainSize = SnapshotParser.intField(snapshot, "maxChainSize");
        int rehashes = SnapshotParser.intField(snapshot, "rehashes");

        List<String> rawBuckets = SnapshotParser.bucketEntries(snapshot);
        List<HashChainingStateModel.Bucket> buckets = new ArrayList<>();

        for (int i = 0; i < rawBuckets.size(); i++) {
            String raw = rawBuckets.get(i);
            // format: "[idx] empty" or "[idx] (k -> v) -> (k2 -> v2)"
            String content = raw.replaceFirst("^\\[\\d+]\\s*", "");
            List<HashChainingStateModel.Entry> entries = new ArrayList<>();

            if (!"empty".equals(content)) {
                Matcher m = ENTRY_PATTERN.matcher(content);
                while (m.find()) {
                    entries.add(new HashChainingStateModel.Entry(m.group(1).trim(), m.group(2).trim()));
                }
            }
            buckets.add(new HashChainingStateModel.Bucket(i, Collections.unmodifiableList(entries)));
        }

        return new HashChainingStateModel(size, capacity, hashType,
                maxChainSize < 0 ? 0 : maxChainSize,
                rehashes < 0 ? 0 : rehashes,
                Collections.unmodifiableList(buckets));
    }

    /**
     * Parses a HashTableOpenAddressing snapshot into a HashOpenAddressingStateModel.
     * Snapshot: HashTableOpenAddressing{size=N, capacity=M, oaType=T, hashType=H, rehashes=Y, slots=[...]}
     */
    public static HashOpenAddressingStateModel parseHashTableOpenAddressing(String snapshot) {
        int size = SnapshotParser.intField(snapshot, "size");
        int capacity = SnapshotParser.intField(snapshot, "capacity");
        String oaType = SnapshotParser.stringField(snapshot, "oaType");
        String hashType = SnapshotParser.stringField(snapshot, "hashType");
        int rehashes = SnapshotParser.intField(snapshot, "rehashes");

        List<String> rawSlots = SnapshotParser.slotEntries(snapshot);
        List<HashOpenAddressingStateModel.Slot> slots = new ArrayList<>();

        for (int i = 0; i < rawSlots.size(); i++) {
            String raw = rawSlots.get(i);
            String content = raw.replaceFirst("^\\[\\d+]\\s*", "");

            if ("empty".equals(content)) {
                slots.add(new HashOpenAddressingStateModel.Slot(
                        i, HashOpenAddressingStateModel.SlotState.EMPTY, null, null));
            } else if ("DELETED".equals(content)) {
                slots.add(new HashOpenAddressingStateModel.Slot(
                        i, HashOpenAddressingStateModel.SlotState.DELETED, null, null));
            } else {
                Matcher m = ENTRY_PATTERN.matcher(content);
                if (m.find()) {
                    slots.add(new HashOpenAddressingStateModel.Slot(
                            i, HashOpenAddressingStateModel.SlotState.OCCUPIED,
                            m.group(1).trim(), m.group(2).trim()));
                } else {
                    slots.add(new HashOpenAddressingStateModel.Slot(
                            i, HashOpenAddressingStateModel.SlotState.EMPTY, null, null));
                }
            }
        }

        return new HashOpenAddressingStateModel(size, capacity, oaType, hashType,
                rehashes < 0 ? 0 : rehashes,
                Collections.unmodifiableList(slots));
    }

    /**
     * Parses a HashSetCustom snapshot into a HashSetStateModel.
     * Snapshot: HashSetCustom{size=N, table=HashTableChaining{size=N, capacity=M, ..., buckets=[...]}}
     * The backing table stores set elements as keys with a sentinel value object.
     */
    public static HashSetStateModel parseHashSetCustom(String snapshot) {
        int size = SnapshotParser.intField(snapshot, "size");
        String tableSnap = SnapshotParser.embeddedSnapshot(snapshot, "table");

        int capacity = SnapshotParser.intField(tableSnap, "capacity");
        String hashType = SnapshotParser.stringField(tableSnap, "hashType");
        int maxChainSize = SnapshotParser.intField(tableSnap, "maxChainSize");
        int rehashes = SnapshotParser.intField(tableSnap, "rehashes");

        List<String> rawBuckets = SnapshotParser.bucketEntries(tableSnap);
        List<HashSetStateModel.SetBucket> buckets = new ArrayList<>();

        for (int i = 0; i < rawBuckets.size(); i++) {
            String raw = rawBuckets.get(i);
            String content = raw.replaceFirst("^\\[\\d+]\\s*", "");
            List<String> members = new ArrayList<>();

            if (!"empty".equals(content)) {
                // Entries are "(element -> sentinel)", extract just the key as the set member
                Matcher m = ENTRY_PATTERN.matcher(content);
                while (m.find()) {
                    members.add(m.group(1).trim());
                }
            }
            buckets.add(new HashSetStateModel.SetBucket(i, Collections.unmodifiableList(members)));
        }

        return new HashSetStateModel(size, capacity, hashType,
                maxChainSize < 0 ? 0 : maxChainSize,
                rehashes < 0 ? 0 : rehashes,
                Collections.unmodifiableList(buckets));
    }

    // ── Linked-list family ──────────────────────────────────────

    /**
     * Parses a SinglyLinkedList snapshot.
     * Snapshot: SinglyLinkedList{size=N, head=V, tail=V, chain=[v1 -> v2 -> v3]}
     */
    public static SinglyLinkedListStateModel parseSinglyLinkedList(String snapshot) {
        int size = SnapshotParser.intField(snapshot, "size");
        String head = SnapshotParser.stringField(snapshot, "head");
        String tail = SnapshotParser.stringField(snapshot, "tail");
        List<String> chain = SnapshotParser.chainField(snapshot, "chain");
        return new SinglyLinkedListStateModel(
                Collections.unmodifiableList(chain), size, head, tail);
    }

    /**
     * Parses a DoublyLinkedList snapshot.
     * Snapshot: DoublyLinkedList{size=N, head=V, tail=V, chain=[v1 <-> v2 <-> v3]}
     */
    public static DoublyLinkedListStateModel parseDoublyLinkedList(String snapshot) {
        int size = SnapshotParser.intField(snapshot, "size");
        String head = SnapshotParser.stringField(snapshot, "head");
        String tail = SnapshotParser.stringField(snapshot, "tail");
        List<String> chain = SnapshotParser.doublyLinkedChainField(snapshot, "chain");
        return new DoublyLinkedListStateModel(
                Collections.unmodifiableList(chain), size, head, tail);
    }

    // ── Deque family ────────────────────────────────────────────

    /**
     * Parses an ArrayDequeCustom snapshot.
     * Snapshot: ArrayDequeCustom{size=N, capacity=C, frontIndex=F, logical=[...], raw=[...]}
     */
    public static ArrayDequeStateModel parseArrayDequeCustom(String snapshot) {
        int size = SnapshotParser.intField(snapshot, "size");
        int capacity = SnapshotParser.intField(snapshot, "capacity");
        int frontIndex = SnapshotParser.intField(snapshot, "frontIndex");
        List<String> logical = SnapshotParser.listField(snapshot, "logical");
        List<String> raw = SnapshotParser.listField(snapshot, "raw");
        return new ArrayDequeStateModel(
                Collections.unmodifiableList(logical),
                Collections.unmodifiableList(raw),
                size, capacity, frontIndex);
    }

    /**
     * Parses a LinkedDeque snapshot.
     * Snapshot: LinkedDeque{size=N, front=V, rear=V, chain=[v1 <-> v2 <-> v3]}
     */
    public static LinkedDequeStateModel parseLinkedDeque(String snapshot) {
        int size = SnapshotParser.intField(snapshot, "size");
        String front = SnapshotParser.stringField(snapshot, "front");
        String rear = SnapshotParser.stringField(snapshot, "rear");
        List<String> chain = SnapshotParser.doublyLinkedChainField(snapshot, "chain");
        return new LinkedDequeStateModel(
                Collections.unmodifiableList(chain), size, front, rear);
    }
}
