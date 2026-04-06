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
    sb.append("  Logical: ");
    if (elements.isEmpty()) {
      sb.append("(empty)");
    } else {
      sb.append(boxRow(elements));
    }
    sb.append("\n");

    // Index row for logical
    if (!elements.isEmpty()) {
      sb.append("  Index:   ").append(indexRow(elements.size())).append("\n");
    }

    // Backing array row
    if (!raw.isEmpty()) {
      sb.append("  Backing: ").append(boxRow(raw)).append("\n");
      sb.append("  Index:   ").append(indexRow(raw.size())).append("\n");
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
      sb.append("    +-------+\n");
    } else {
      int cellWidth = maxWidth(elements) + 2;
      String border = "+" + "-".repeat(cellWidth) + "+";
      for (int i = elements.size() - 1; i >= 0; i--) {
        String cell = padCenter(elements.get(i), cellWidth);
        sb.append("    |").append(cell).append("|");
        if (i == elements.size() - 1) sb.append(" <-- top");
        sb.append("\n");
      }
      sb.append("    ").append(border).append("\n");
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
      sb.append("  Buffer:  ").append(boxRow(raw)).append("\n");
      sb.append("  Index:   ").append(indexRow(raw.size())).append("\n");

      // Marker row showing F and R
      if (size > 0) {
        sb.append("  Markers: ");
        int cellWidth = maxWidth(raw) + 2;
        for (int i = 0; i < raw.size(); i++) {
          String marker = "";
          if (i == frontIndex && i == rearIndex) marker = "F/R";
          else if (i == frontIndex) marker = "F";
          else if (i == rearIndex) marker = "R";
          sb.append(padCenter(marker, cellWidth + 1));
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
      sb.append("           ");
      int pos = 0;
      for (int i = 0; i < chain.size(); i++) {
        int nodeWidth = chain.get(i).length() + 2; // [x]
        if (i == 0) {
          sb.append("^");
          pos = 1;
        } else if (i == chain.size() - 1) {
          int target = 0;
          for (int j = 0; j < i; j++) target += chain.get(j).length() + 2 + 4; // [x] + " -> "
          while (pos < target) { sb.append(" "); pos++; }
          sb.append("^");
          pos++;
        }
      }
      sb.append("\n");
      sb.append("         front");
      if (chain.size() > 1) {
        int target = 0;
        for (int j = 0; j < chain.size() - 1; j++) target += chain.get(j).length() + 2 + 4;
        String padding = " ".repeat(Math.max(0, target - 3));
        sb.append(padding).append("rear");
      }
      sb.append("\n");
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
      default -> "  " + snapshot + "\n";
    };
  }

  // ---- Helpers ----

  /** Builds a boxed row like "| 10 | 20 | null |" */
  static String boxRow(List<String> items) {
    int cellWidth = maxWidth(items) + 2;
    StringBuilder sb = new StringBuilder();
    for (String item : items) {
      sb.append("| ").append(padRight(item, cellWidth - 2)).append(" ");
    }
    sb.append("|");
    return sb.toString();
  }

  /** Builds an index row like "  0    1    2  " aligned under boxed cells. */
  static String indexRow(int count) {
    StringBuilder sb = new StringBuilder();
    // rough alignment — each cell is (maxWidth+2) + 2 for "| " prefix
    for (int i = 0; i < count; i++) {
      sb.append("  ").append(i).append("   ");
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

  static String padCenter(String s, int width) {
    if (s.length() >= width) return s;
    int left = (width - s.length()) / 2;
    int right = width - s.length() - left;
    return " ".repeat(left) + s + " ".repeat(right);
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
