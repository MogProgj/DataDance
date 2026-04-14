package structlab.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses snapshot strings produced by core structures into structured fields.
 * Keeps all parsing logic isolated so renderers don't embed regex directly.
 */
public final class SnapshotParser {

  private SnapshotParser() {}

  /** Extracts the type prefix from a snapshot, e.g. "DynamicArray" from "DynamicArray{...}". */
  public static String type(String snapshot) {
    int brace = snapshot.indexOf('{');
    return brace > 0 ? snapshot.substring(0, brace) : "";
  }

  /** Extracts a named integer field, e.g. field("size=3, ...", "size") returns 3. */
  public static int intField(String snapshot, String name) {
    Pattern p = Pattern.compile(name + "=(\\d+)");
    Matcher m = p.matcher(snapshot);
    return m.find() ? Integer.parseInt(m.group(1)) : -1;
  }

  /** Extracts a named value field (non-comma, non-brace), e.g. "top=null" returns "null". */
  public static String stringField(String snapshot, String name) {
    Pattern p = Pattern.compile(name + "=([^,}]+)");
    Matcher m = p.matcher(snapshot);
    return m.find() ? m.group(1).trim() : "";
  }

  /**
   * Extracts a bracketed list following a field name, e.g.
   * "elements=[10, 20, null]" returns ["10", "20", "null"].
   */
  public static List<String> listField(String snapshot, String name) {
    Pattern p = Pattern.compile(name + "=\\[([^\\]]*)]");
    Matcher m = p.matcher(snapshot);
    if (!m.find()) return Collections.emptyList();
    String inner = m.group(1).trim();
    if (inner.isEmpty()) return Collections.emptyList();
    List<String> items = new ArrayList<>();
    for (String item : inner.split(",\\s*")) {
      items.add(item.trim());
    }
    return items;
  }

  /**
   * Extracts a chain list using " -> " separator, e.g.
   * "chain=[10 -> 20 -> 30]" returns ["10", "20", "30"].
   */
  public static List<String> chainField(String snapshot, String name) {
    Pattern p = Pattern.compile(name + "=\\[([^\\]]*)]");
    Matcher m = p.matcher(snapshot);
    if (!m.find()) return Collections.emptyList();
    String inner = m.group(1).trim();
    if (inner.isEmpty()) return Collections.emptyList();
    List<String> items = new ArrayList<>();
    for (String item : inner.split("\\s*->\\s*")) {
      items.add(item.trim());
    }
    return items;
  }

  /**
   * Extracts a doubly-linked chain list using " &lt;-&gt; " separator, e.g.
   * "chain=[10 &lt;-&gt; 20 &lt;-&gt; 30]" returns ["10", "20", "30"].
   */
  public static List<String> doublyLinkedChainField(String snapshot, String name) {
    Pattern p = Pattern.compile(name + "=\\[([^\\]]*)]");
    Matcher m = p.matcher(snapshot);
    if (!m.find()) return Collections.emptyList();
    String inner = m.group(1).trim();
    if (inner.isEmpty()) return Collections.emptyList();
    List<String> items = new ArrayList<>();
    for (String item : inner.split("\\s*<->\\s*")) {
      items.add(item.trim());
    }
    return items;
  }

  /**
   * Extracts an embedded snapshot (nested braces), e.g.
   * "inbox=ArrayStack{size=0, ...}" returns "ArrayStack{size=0, ...}".
   */
  public static String embeddedSnapshot(String snapshot, String name) {
    int start = snapshot.indexOf(name + "=");
    if (start < 0) return "";
    start += name.length() + 1; // skip past "name="
    int depth = 0;
    int end = start;
    for (int i = start; i < snapshot.length(); i++) {
      char c = snapshot.charAt(i);
      if (c == '{') depth++;
      else if (c == '}') {
        depth--;
        if (depth == 0) { end = i + 1; break; }
      }
    }
    return snapshot.substring(start, end);
  }

  /**
   * Parses bucket entries from a HashTableChaining snapshot.
   * Input format: "buckets=[0: empty, 1: (k -> v) -> (k2 -> v2), 2: empty]"
   * Returns list like ["[0] empty", "[1] (k -> v) -> (k2 -> v2)", "[2] empty"].
   */
  public static List<String> bucketEntries(String snapshot) {
    return parseIndexedEntries(snapshot, "buckets");
  }

  /**
   * Parses slot entries from a HashTableOpenAddressing snapshot.
   * Input format: "slots=[0: empty, 1: (k -> v), 2: DELETED, 3: empty]"
   * Returns list like ["[0] empty", "[1] (k -> v)", "[2] DELETED", "[3] empty"].
   */
  public static List<String> slotEntries(String snapshot) {
    return parseIndexedEntries(snapshot, "slots");
  }

  /**
   * Generic parser for indexed entries (buckets or slots).
   */
  private static List<String> parseIndexedEntries(String snapshot, String fieldName) {
    int start = snapshot.indexOf(fieldName + "=[");
    if (start < 0) return Collections.emptyList();
    start += (fieldName + "=[").length();

    // Find matching closing bracket, accounting for nested parens
    int depth = 1;
    int end = start;
    for (int i = start; i < snapshot.length(); i++) {
      char c = snapshot.charAt(i);
      if (c == '[') depth++;
      else if (c == ']') {
        depth--;
        if (depth == 0) { end = i; break; }
      }
    }

    String inner = snapshot.substring(start, end);
    List<String> entries = new ArrayList<>();

    // Split on pattern "N: " where N is the index at start of each entry
    String[] parts = inner.split(",\\s*(?=\\d+:\\s)");
    for (String part : parts) {
      String trimmed = part.trim();
      if (trimmed.isEmpty()) continue;
      // Convert "0: empty" to "[0] empty"
      int colonPos = trimmed.indexOf(": ");
      if (colonPos > 0) {
        String idx = trimmed.substring(0, colonPos);
        String content = trimmed.substring(colonPos + 2);
        entries.add("[" + idx + "] " + content);
      } else {
        entries.add(trimmed);
      }
    }

    return entries;
  }
}
