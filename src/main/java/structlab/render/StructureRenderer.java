package structlab.render;

import java.util.ArrayList;
import java.util.List;

/**
 * Produces ASCII/text renderings of data structure state from snapshot strings.
 * Each method targets a specific structure type and shows markers, elements,
 * and internal layout in a terminal-friendly format.
 */
public final class StructureRenderer {

  private StructureRenderer() {}

  /**
   * Renders a DynamicArray or FixedArray snapshot.
   * Shows logical elements as boxed cells with index markers,
   * and the raw backing array separately.
   *
   * Example output:
   *   size: 3  capacity: 4
   *   Logical: | 10 | 20 | 30 |
   *   Index:     0    1    2
   *   Backing: [ 10 | 20 | 30 |    ]
   *   Index:      0    1    2    3
   */
  public static String renderArray(String snapshot) {
    String type = SnapshotParser.type(snapshot);
    int size = SnapshotParser.intField(snapshot, "size");
    int capacity = SnapshotParser.intField(snapshot, "capacity");
    List<String> elements = SnapshotParser.listField(snapshot, "elements");
    List<String> raw = SnapshotParser.listField(snapshot, "raw");

    StringBuilder sb = new StringBuilder();
    sb.append("  ").append(type).append("  size: ").append(size)
      .append("  capacity: ").append(capacity);

    if (type.equals("FixedArray")) {
      sb.append("  [").append(size >= capacity ? "FULL" : (size + "/" + capacity)).append("]");
    }
    sb.append("\n");

    // Logical elements row
    if (elements.isEmpty()) {
      sb.append("  Logical: (empty)\n");
    } else {
      sb.append("             ").append(topRow(elements)).append("\n");
      sb.append("  Logical:   ").append(boxRow(elements)).append("\n");
      sb.append("             ").append(botRow(elements)).append("\n");
    }

    // Index row for logical
    if (!elements.isEmpty()) {
      sb.append("  Index:     ").append(indexRow(elements)).append("\n");
    }

    // Backing array row
    if (!raw.isEmpty()) {
      sb.append("             ").append(topRow(raw)).append("\n");
      sb.append("  Backing:   ").append(boxRow(raw)).append("\n");
      sb.append("             ").append(botRow(raw)).append("\n");
      sb.append("  Index:     ").append(indexRow(raw)).append("\n");
    }

    return sb.toString();
  }

  /**
   * Renders an ArrayStack snapshot.
   * Shows stack vertically with top marker.
   *
   * Example output:
   *   ArrayStack  size: 3  top: 30
   *     | 30 | <-- top
   *     | 20 |
   *     | 10 |
   *     +----+
   */
  public static String renderArrayStack(String snapshot) {
    int size = SnapshotParser.intField(snapshot, "size");
    String top = SnapshotParser.stringField(snapshot, "top");

    // Extract the embedded DynamicArray to get elements
    String dynSnap = SnapshotParser.embeddedSnapshot(snapshot, "elements");
    List<String> elements = SnapshotParser.listField(dynSnap, "elements");

    StringBuilder sb = new StringBuilder();
    sb.append("  ArrayStack  size: ").append(size).append("  top: ").append(top).append("\n");

    if (elements.isEmpty()) {
      sb.append("    (empty)\n");
        sb.append("    └───────┘\n");
      } else {
        int cellWidth = maxWidth(elements) + 2;
        String border = "    └" + "─".repeat(cellWidth) + "┘\n";
        String topBorder = "    ┌" + "─".repeat(cellWidth) + "┐\n";
        String innerBorder = "    ├" + "─".repeat(cellWidth) + "┤\n";

        sb.append(topBorder);
        for (int i = elements.size() - 1; i >= 0; i--) {
          String cell = padCenter(elements.get(i), cellWidth);
          sb.append("    │").append(cell).append("│");
          if (i == elements.size() - 1) sb.append(" <-- top");
          sb.append("\n");
          if (i > 0) sb.append(innerBorder);
        }
        sb.append(border);
      }
    return sb.toString();
  }

  /**
   * Renders a CircularArrayQueue snapshot.
   * Shows the circular buffer with front/rear markers.
   *
   * Example output:
   *   CircularArrayQueue  size: 3  capacity: 4  front: idx 1
   *   Buffer: [ null | 20 | 30 | 40 ]
   *   Index:     0      1    2    3
   *   Markers:          F              R
   *   Logical: 20 -> 30 -> 40
   */
  public static String renderCircularQueue(String snapshot) {
    int size = SnapshotParser.intField(snapshot, "size");
    int capacity = SnapshotParser.intField(snapshot, "capacity");
    int frontIndex = SnapshotParser.intField(snapshot, "frontIndex");
    List<String> logical = SnapshotParser.listField(snapshot, "logical");
    List<String> raw = SnapshotParser.listField(snapshot, "raw");

    int rearIndex = size > 0 ? (frontIndex + size - 1) % capacity : -1;

    StringBuilder sb = new StringBuilder();
    sb.append("  CircularArrayQueue  size: ").append(size)
      .append("  capacity: ").append(capacity)
      .append("  front: idx ").append(frontIndex).append("\n");

    // Buffer row
    if (!raw.isEmpty()) {
      int cellWidth = maxWidth(raw) + 2;

        sb.append("             ").append(topRow(raw)).append("\n");
        sb.append("  Buffer:    ").append(boxRow(raw)).append("\n");
        sb.append("             ").append(botRow(raw)).append("\n");
        sb.append("  Index:     ").append(indexRow(raw)).append("\n");

        // Marker row showing F and R, aligned to cell centers
        if (size > 0) {
          sb.append("  Markers:    ");
          for (int i = 0; i < raw.size(); i++) {
            String marker = "";
            if (i == frontIndex && i == rearIndex) marker = "F/R";
            else if (i == frontIndex) marker = "F";
            else if (i == rearIndex) marker = "R";
            sb.append(padCenter(marker, cellWidth)).append(" ");
          }
          sb.append("\n");
        }
    }

    // Logical order
    sb.append("  Logical: ");
    if (logical.isEmpty()) {
      sb.append("(empty)");
    } else {
      sb.append(String.join(" -> ", logical));
    }
    sb.append("\n");

    return sb.toString();
  }

  /**
   * Renders a LinkedStack snapshot as a chain with top marker.
   *
   * Example output:
   *   LinkedStack  size: 3  top: 30
   *   top -> [30] -> [20] -> [10] -> null
   */
  public static String renderLinkedStack(String snapshot) {
    int size = SnapshotParser.intField(snapshot, "size");
    String top = SnapshotParser.stringField(snapshot, "top");
    List<String> chain = SnapshotParser.chainField(snapshot, "chain");

    StringBuilder sb = new StringBuilder();
    sb.append("  LinkedStack  size: ").append(size).append("  top: ").append(top).append("\n");

    sb.append("  top -> ");
    if (chain.isEmpty()) {
      sb.append("null");
    } else {
      for (int i = 0; i < chain.size(); i++) {
        sb.append("[").append(chain.get(i)).append("]");
        sb.append(" -> ");
      }
      sb.append("null");
    }
    sb.append("\n");

    return sb.toString();
  }

  /**
   * Renders a LinkedQueue snapshot with front/rear markers.
   *
   * Example output:
   *   LinkedQueue  size: 3  front: 10  rear: 30
   *   front -> [10] -> [20] -> [30] -> null
   *            ^                ^
   *          front             rear
   */
  public static String renderLinkedQueue(String snapshot) {
    int size = SnapshotParser.intField(snapshot, "size");
    String front = SnapshotParser.stringField(snapshot, "front");
    String rear = SnapshotParser.stringField(snapshot, "rear");
    List<String> chain = SnapshotParser.chainField(snapshot, "chain");

    StringBuilder sb = new StringBuilder();
    sb.append("  LinkedQueue  size: ").append(size)
      .append("  front: ").append(front)
      .append("  rear: ").append(rear).append("\n");

    sb.append("  front -> ");
    if (chain.isEmpty()) {
      sb.append("null");
    } else {
      for (int i = 0; i < chain.size(); i++) {
        sb.append("[").append(chain.get(i)).append("]");
        sb.append(" -> ");
      }
      sb.append("null");
    }
    sb.append("\n");

    // Pointer markers for front and rear
    if (chain.size() > 1) {
      // Compute position of the center of each node in the chain line.
      // "front -> " prefix is 10 chars, then each node is "[val] -> " (len+2+4)
      String prefix = "  front -> ";
      int frontCenter = prefix.length() + chain.get(0).length() / 2 + 1; // center of [val]
      int rearStart = prefix.length();
      for (int j = 0; j < chain.size() - 1; j++) {
        rearStart += chain.get(j).length() + 2 + 4; // [val] + " -> "
      }
      int rearCenter = rearStart + chain.get(chain.size() - 1).length() / 2 + 1;

      // Caret line
      StringBuilder carets = new StringBuilder();
      for (int i = 0; i < Math.max(frontCenter, rearCenter) + 1; i++) carets.append(' ');
      carets.setCharAt(frontCenter, '^');
      carets.setCharAt(rearCenter, '^');
      sb.append(carets).append("\n");

      // Label line
      String frontLabel = "front";
      String rearLabel = "rear";
      int frontLabelStart = Math.max(0, frontCenter - frontLabel.length() / 2);
      int rearLabelStart = Math.max(0, rearCenter - rearLabel.length() / 2);
      // Ensure no overlap
      if (rearLabelStart < frontLabelStart + frontLabel.length() + 1) {
        rearLabelStart = frontLabelStart + frontLabel.length() + 1;
      }
      StringBuilder labels = new StringBuilder();
      for (int i = 0; i < rearLabelStart + rearLabel.length(); i++) labels.append(' ');
      for (int i = 0; i < frontLabel.length(); i++) labels.setCharAt(frontLabelStart + i, frontLabel.charAt(i));
      for (int i = 0; i < rearLabel.length(); i++) labels.setCharAt(rearLabelStart + i, rearLabel.charAt(i));
      sb.append(labels).append("\n");
    }

    return sb.toString();
  }

  /**
   * Renders a TwoStackQueue snapshot showing both stacks and effective queue order.
   *
   * Example output:
   *   TwoStackQueue  size: 3
   *   Inbox (enqueue here):    Outbox (dequeue here):
   *     | 30 | <-- top           | 10 | <-- top
   *     | 20 |                   | 20 |
   *     +----+                   +----+
   *   Queue order (front to back): 10, 20, 30
   */
  public static String renderTwoStackQueue(String snapshot) {
    int size = SnapshotParser.intField(snapshot, "size");
    String inboxSnap = SnapshotParser.embeddedSnapshot(snapshot, "inbox");
    String outboxSnap = SnapshotParser.embeddedSnapshot(snapshot, "outbox");

    String inboxDyn = SnapshotParser.embeddedSnapshot(inboxSnap, "elements");
    String outboxDyn = SnapshotParser.embeddedSnapshot(outboxSnap, "elements");
    List<String> inboxElems = SnapshotParser.listField(inboxDyn, "elements");
    List<String> outboxElems = SnapshotParser.listField(outboxDyn, "elements");

    StringBuilder sb = new StringBuilder();
    sb.append("  TwoStackQueue  size: ").append(size).append("\n");

    // Build left column (inbox) and right column (outbox)
    List<String> inboxLines = stackLines(inboxElems, "Inbox (push here)");
    List<String> outboxLines = stackLines(outboxElems, "Outbox (pop here)");

    int leftWidth = 0;
    for (String line : inboxLines) leftWidth = Math.max(leftWidth, line.length());
    leftWidth += 4; // gap between columns

    int maxLines = Math.max(inboxLines.size(), outboxLines.size());
    for (int i = 0; i < maxLines; i++) {
      String left = i < inboxLines.size() ? inboxLines.get(i) : "";
      String right = i < outboxLines.size() ? outboxLines.get(i) : "";
      sb.append("  ").append(String.format("%-" + leftWidth + "s", left)).append(right).append("\n");
    }

    // Effective queue order: outbox bottom-to-top then inbox top-to-bottom
    sb.append("  Queue order (front to back): ");
    if (outboxElems.isEmpty() && inboxElems.isEmpty()) {
      sb.append("(empty)");
    } else {
      StringBuilder order = new StringBuilder();
      // outbox top (last element) is next to dequeue; iterate top to bottom
      for (int i = outboxElems.size() - 1; i >= 0; i--) {
        if (order.length() > 0) order.append(", ");
        order.append(outboxElems.get(i));
      }
      // inbox bottom (first element) was enqueued first; iterate bottom to top
      for (int i = 0; i < inboxElems.size(); i++) {
        if (order.length() > 0) order.append(", ");
        order.append(inboxElems.get(i));
      }
      sb.append(order);
    }
    sb.append("\n");

    return sb.toString();
  }

  /**
   * Renders a SinglyLinkedList snapshot as a chain with head/tail markers.
   *
   * Example output:
   *   SinglyLinkedList  size: 3  head: 10  tail: 30
   *   head -> [10] -> [20] -> [30] -> null
   *                            ^
   *                           tail
   */
  public static String renderSinglyLinkedList(String snapshot) {
    int size = SnapshotParser.intField(snapshot, "size");
    String head = SnapshotParser.stringField(snapshot, "head");
    String tail = SnapshotParser.stringField(snapshot, "tail");
    List<String> chain = SnapshotParser.chainField(snapshot, "chain");

    StringBuilder sb = new StringBuilder();
    sb.append("  SinglyLinkedList  size: ").append(size)
      .append("  head: ").append(head)
      .append("  tail: ").append(tail).append("\n");

    sb.append("  head -> ");
    if (chain.isEmpty()) {
      sb.append("null");
    } else {
      for (String node : chain) {
        sb.append("[").append(node).append("] -> ");
      }
      sb.append("null");
    }
    sb.append("\n");

    // Tail marker under last node (only when multiple nodes)
    if (chain.size() > 1) {
      String prefix = "  head -> ";
      int tailStart = prefix.length();
      for (int j = 0; j < chain.size() - 1; j++) {
        tailStart += chain.get(j).length() + 2 + 4; // [val] + " -> "
      }
      int tailCenter = tailStart + chain.get(chain.size() - 1).length() / 2 + 1;

      StringBuilder carets = new StringBuilder();
      for (int i = 0; i < tailCenter + 1; i++) carets.append(' ');
      carets.setCharAt(tailCenter, '^');
      sb.append(carets).append("\n");

      StringBuilder labels = new StringBuilder();
      String tailLabel = "tail";
      int labelStart = Math.max(0, tailCenter - tailLabel.length() / 2);
      for (int i = 0; i < labelStart + tailLabel.length(); i++) labels.append(' ');
      for (int i = 0; i < tailLabel.length(); i++) labels.setCharAt(labelStart + i, tailLabel.charAt(i));
      sb.append(labels).append("\n");
    }

    return sb.toString();
  }

  /**
   * Renders a DoublyLinkedList snapshot with bidirectional links.
   *
   * Example output:
   *   DoublyLinkedList  size: 3  head: 10  tail: 30
   *   null <-- [10] <--> [20] <--> [30] --> null
   *             ^                   ^
   *            head                tail
   */
  public static String renderDoublyLinkedList(String snapshot) {
    int size = SnapshotParser.intField(snapshot, "size");
    String head = SnapshotParser.stringField(snapshot, "head");
    String tail = SnapshotParser.stringField(snapshot, "tail");
    List<String> chain = SnapshotParser.doublyLinkedChainField(snapshot, "chain");

    StringBuilder sb = new StringBuilder();
    sb.append("  DoublyLinkedList  size: ").append(size)
      .append("  head: ").append(head)
      .append("  tail: ").append(tail).append("\n");

    return appendDoublyLinkedChain(sb, chain, "head", "tail");
  }

  /**
   * Renders a LinkedDeque snapshot with front/rear markers.
   *
   * Example output:
   *   LinkedDeque  size: 3  front: 10  rear: 30
   *   null <-- [10] <--> [20] <--> [30] --> null
   *             ^                   ^
   *            front               rear
   */
  public static String renderLinkedDeque(String snapshot) {
    int size = SnapshotParser.intField(snapshot, "size");
    String front = SnapshotParser.stringField(snapshot, "front");
    String rear = SnapshotParser.stringField(snapshot, "rear");
    List<String> chain = SnapshotParser.doublyLinkedChainField(snapshot, "chain");

    StringBuilder sb = new StringBuilder();
    sb.append("  LinkedDeque  size: ").append(size)
      .append("  front: ").append(front)
      .append("  rear: ").append(rear).append("\n");

    return appendDoublyLinkedChain(sb, chain, "front", "rear");
  }

  /**
   * Renders an ArrayDequeCustom snapshot with circular buffer, F/R markers.
   * Very similar to CircularArrayQueue rendering.
   */
  public static String renderArrayDeque(String snapshot) {
    int size = SnapshotParser.intField(snapshot, "size");
    int capacity = SnapshotParser.intField(snapshot, "capacity");
    int frontIndex = SnapshotParser.intField(snapshot, "frontIndex");
    List<String> logical = SnapshotParser.listField(snapshot, "logical");
    List<String> raw = SnapshotParser.listField(snapshot, "raw");

    int rearIndex = size > 0 ? (frontIndex + size - 1) % capacity : -1;

    StringBuilder sb = new StringBuilder();
    sb.append("  ArrayDequeCustom  size: ").append(size)
      .append("  capacity: ").append(capacity)
      .append("  front: idx ").append(frontIndex).append("\n");

    if (!raw.isEmpty()) {
      int cellWidth = maxWidth(raw) + 2;

        sb.append("             ").append(topRow(raw)).append("\n");
        sb.append("  Buffer:    ").append(boxRow(raw)).append("\n");
        sb.append("             ").append(botRow(raw)).append("\n");
        sb.append("  Index:     ").append(indexRow(raw)).append("\n");

        if (size > 0) {
          sb.append("  Markers:    "); // 14 spaces prefix for index row equivalence
          for (int i = 0; i < raw.size(); i++) {
            String marker = "";
            if (i == frontIndex && i == rearIndex) marker = "F/R";
            else if (i == frontIndex) marker = "F";
            else if (i == rearIndex) marker = "R";
            sb.append(padCenter(marker, cellWidth)).append(" ");
          }
          sb.append("\n");
        }
    }

    sb.append("  Logical: ");
    if (logical.isEmpty()) {
      sb.append("(empty)");
    } else {
      sb.append(String.join(" -> ", logical));
    }
    sb.append("\n");

    return sb.toString();
  }

  /**
   * Renders a BinaryHeap snapshot with array view and tree-level view.
   *
   * Example output:
   *   BinaryHeap  size: 5  min: 1
   *   Array: | 1 | 3 | 2 | 7 | 4 |
   *   Index:   0   1   2   3   4
   *   Tree:
   *     Level 0:         1
   *     Level 1:      3     2
   *     Level 2:    7   4
   */
  public static String renderBinaryHeap(String snapshot) {
    int size = SnapshotParser.intField(snapshot, "size");
    String min = SnapshotParser.stringField(snapshot, "min");

    String dynSnap = SnapshotParser.embeddedSnapshot(snapshot, "elements");
    List<String> elements = SnapshotParser.listField(dynSnap, "elements");

    StringBuilder sb = new StringBuilder();
    sb.append("  BinaryHeap  size: ").append(size)
      .append("  min: ").append(min).append("\n");

    if (elements.isEmpty()) {
      sb.append("  (empty)\n");
      return sb.toString();
    }

    // Array view
      sb.append("         ").append(topRow(elements)).append("\n");
      sb.append("  Array: ").append(boxRow(elements)).append("\n");
      sb.append("         ").append(botRow(elements)).append("\n");

    // Tree-level view
    sb.append("  Tree:\n");
    int level = 0;
    int idx = 0;
    int totalLevels = 0;
    int temp = elements.size();
    while (temp > 0) { totalLevels++; temp = (temp - 1) / 2; }

    while (idx < elements.size()) {
      int nodesAtLevel = 1 << level; // 2^level
      int indent = Math.max(1, (1 << (totalLevels - level)) - 1);
      int spacing = Math.max(1, (1 << (totalLevels - level + 1)) - 1);

      sb.append("    Level ").append(level).append(": ");
      sb.append(" ".repeat(indent));
      for (int i = 0; i < nodesAtLevel && idx < elements.size(); i++) {
        if (i > 0) sb.append(" ".repeat(spacing));
        sb.append(elements.get(idx));
        idx++;
      }
      sb.append("\n");
      level++;
    }

    return sb.toString();
  }

  /**
   * Renders a HeapPriorityQueue snapshot, showing priority info
   * and delegating to the embedded BinaryHeap rendering.
   */
  public static String renderHeapPriorityQueue(String snapshot) {
    int size = SnapshotParser.intField(snapshot, "size");
    String front = SnapshotParser.stringField(snapshot, "front");

    StringBuilder sb = new StringBuilder();
    sb.append("  HeapPriorityQueue  size: ").append(size)
      .append("  front: ").append(front).append("\n");
    sb.append("  (backed by BinaryHeap)\n");

    String heapSnap = SnapshotParser.embeddedSnapshot(snapshot, "heap");
    if (!heapSnap.isEmpty()) {
      // Render the embedded heap, stripping its header (first line)
      String heapRendered = renderBinaryHeap(heapSnap);
      String[] lines = heapRendered.split("\n");
      for (int i = 1; i < lines.length; i++) {
        sb.append(lines[i]).append("\n");
      }
    }

    return sb.toString();
  }

  /**
   * Dispatches to the appropriate renderer based on the snapshot type prefix.
   */
  public static String render(String snapshot) {
    String type = SnapshotParser.type(snapshot);
    return switch (type) {
      case "DynamicArray", "FixedArray" -> renderArray(snapshot);
      case "ArrayStack" -> renderArrayStack(snapshot);
      case "CircularArrayQueue" -> renderCircularQueue(snapshot);
      case "LinkedStack" -> renderLinkedStack(snapshot);
      case "LinkedQueue" -> renderLinkedQueue(snapshot);
      case "TwoStackQueue" -> renderTwoStackQueue(snapshot);
      case "SinglyLinkedList" -> renderSinglyLinkedList(snapshot);
      case "DoublyLinkedList" -> renderDoublyLinkedList(snapshot);
      case "LinkedDeque" -> renderLinkedDeque(snapshot);
      case "ArrayDequeCustom" -> renderArrayDeque(snapshot);
      case "BinaryHeap" -> renderBinaryHeap(snapshot);
      case "HeapPriorityQueue" -> renderHeapPriorityQueue(snapshot);
      default -> "  " + snapshot + "\n";
    };
  }

  // ---- Helpers ----

    /** Builds a top border for a multi-line boxed row like "┌────┬────┐" */
    static String topRow(List<String> items) {
      if (items.isEmpty()) return "";
      int cellWidth = maxWidth(items) + 2;
      StringBuilder sb = new StringBuilder();
      sb.append("┌");
      for (int i = 0; i < items.size(); i++) {
        sb.append("─".repeat(cellWidth));
        sb.append(i < items.size() - 1 ? "┬" : "┐");
      }
      return sb.toString();
    }

    /** Builds the middle content for a boxed row like "│ 10 │ 20 │" */
    static String boxRow(List<String> items) {
      if (items.isEmpty()) return "(empty)";
      int cellWidth = maxWidth(items) + 2;
      StringBuilder sb = new StringBuilder();
      sb.append("│");
      for (String item : items) {
        sb.append(" ").append(padLeftSpace(item, cellWidth - 2)).append(" │");
      }
      return sb.toString();
    }

    /** Builds a bottom border for a multi-line boxed row like "└────┴────┘" */
    static String botRow(List<String> items) {
      if (items.isEmpty()) return "";
      int cellWidth = maxWidth(items) + 2;
      StringBuilder sb = new StringBuilder();
      sb.append("└");
      for (int i = 0; i < items.size(); i++) {
        sb.append("─".repeat(cellWidth));
        sb.append(i < items.size() - 1 ? "┴" : "┘");
      }
      return sb.toString();
    }

    /** Builds an index row aligned to the center of each boxed cell. */
    static String indexRow(List<String> items) {
      if (items.isEmpty()) return "";
      int cellWidth = maxWidth(items) + 2;
      StringBuilder sb = new StringBuilder();
      sb.append(" ");
      for (int i = 0; i < items.size(); i++) {
        String idx = String.valueOf(i);
        sb.append(padCenter(idx, cellWidth)).append(" ");
      }
      return sb.toString();
    }

    static int maxWidth(List<String> items) {
      int max = 1;
      for (String item : items) max = Math.max(max, item.length());
      return max;
    }

    static String padRight(String s, int width) {
      return s.length() >= width ? s : s + " ".repeat(width - s.length());
    }

    static String padLeftSpace(String s, int width) {
      return s.length() >= width ? s : " ".repeat((width - s.length()) / 2) + s + " ".repeat(width - s.length() - (width - s.length()) / 2);
    }
  static String padCenter(String s, int width) {
    if (s.length() >= width) return s;
    int left = (width - s.length()) / 2;
    int right = width - s.length() - left;
    return " ".repeat(left) + s + " ".repeat(right);
  }

  /**
   * Appends a doubly-linked chain rendering with markers for first/last node.
   * Used by DoublyLinkedList and LinkedDeque.
   */
  private static String appendDoublyLinkedChain(StringBuilder sb,
      List<String> chain, String firstLabel, String lastLabel) {
    sb.append("  ");
    if (chain.isEmpty()) {
      sb.append("(empty)\n");
      return sb.toString();
    }

    // Build chain: null <-- [10] <--> [20] <--> [30] --> null
    String prefix = "null <-- ";
    sb.append(prefix);
    for (int i = 0; i < chain.size(); i++) {
      sb.append("[").append(chain.get(i)).append("]");
      if (i < chain.size() - 1) sb.append(" <--> ");
    }
    sb.append(" --> null\n");

    // Markers under first and last node
    if (chain.size() >= 1) {
      int startOffset = 2 + prefix.length(); // "  " + "null <-- "
      int firstCenter = startOffset + chain.get(0).length() / 2 + 1; // center of [val]
      int lastStart = startOffset;
      for (int j = 0; j < chain.size() - 1; j++) {
        lastStart += chain.get(j).length() + 2 + 6; // [val] + " <--> "
      }
      int lastCenter = lastStart + chain.get(chain.size() - 1).length() / 2 + 1;

      if (chain.size() == 1) {
        // Single node: show both labels centered
        StringBuilder carets = new StringBuilder();
        for (int i = 0; i < firstCenter + 1; i++) carets.append(' ');
        carets.setCharAt(firstCenter, '^');
        sb.append(carets).append("\n");

        String label = firstLabel + "/" + lastLabel;
        int labelStart = Math.max(0, firstCenter - label.length() / 2);
        StringBuilder labels = new StringBuilder();
        for (int i = 0; i < labelStart + label.length(); i++) labels.append(' ');
        for (int i = 0; i < label.length(); i++) labels.setCharAt(labelStart + i, label.charAt(i));
        sb.append(labels).append("\n");
      } else {
        // Caret line
        StringBuilder carets = new StringBuilder();
        for (int i = 0; i < Math.max(firstCenter, lastCenter) + 1; i++) carets.append(' ');
        carets.setCharAt(firstCenter, '^');
        carets.setCharAt(lastCenter, '^');
        sb.append(carets).append("\n");

        // Label line
        int firstLabelStart = Math.max(0, firstCenter - firstLabel.length() / 2);
        int lastLabelStart = Math.max(0, lastCenter - lastLabel.length() / 2);
        if (lastLabelStart < firstLabelStart + firstLabel.length() + 1) {
          lastLabelStart = firstLabelStart + firstLabel.length() + 1;
        }
        StringBuilder labels = new StringBuilder();
        for (int i = 0; i < lastLabelStart + lastLabel.length(); i++) labels.append(' ');
        for (int i = 0; i < firstLabel.length(); i++) labels.setCharAt(firstLabelStart + i, firstLabel.charAt(i));
        for (int i = 0; i < lastLabel.length(); i++) labels.setCharAt(lastLabelStart + i, lastLabel.charAt(i));
        sb.append(labels).append("\n");
      }
    }

    return sb.toString();
  }

  /** Builds vertical stack lines for TwoStackQueue rendering. */
  private static List<String> stackLines(List<String> elements, String header) {
    List<String> lines = new ArrayList<>();
    lines.add(header);
    if (elements.isEmpty()) {
      lines.add("  (empty)");
      lines.add("  +-------+");
    } else {
      int cellWidth = maxWidth(elements) + 2;
      String border = "+" + "-".repeat(cellWidth) + "+";
      for (int i = elements.size() - 1; i >= 0; i--) {
        String cell = padCenter(elements.get(i), cellWidth);
        String line = "  |" + cell + "|";
        if (i == elements.size() - 1) line += " <-- top";
        lines.add(line);
      }
      lines.add("  " + border);
    }
    return lines;
  }
}
