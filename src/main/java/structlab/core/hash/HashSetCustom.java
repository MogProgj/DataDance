package structlab.core.hash;

public class HashSetCustom<T> {
  private static final Object PRESENT = new Object();

  private final HashTableChaining<T, Object> table;

  public HashSetCustom() {
    this.table = new HashTableChaining<>();
  }

  public HashSetCustom(int initialCapacity) {
    this.table = new HashTableChaining<>(initialCapacity);
  }

  public int size() {
    return table.size();
  }

  public boolean isEmpty() {
    return table.isEmpty();
  }

  public boolean contains(T value) {
    return table.containsKey(value);
  }

  public boolean add(T value) {
    Object previous = table.put(value, PRESENT);
    return previous == null;
  }

  public boolean remove(T value) {
    Object removed = table.remove(value);
    return removed != null;
  }

  public boolean checkInvariant() {
    return table != null && table.checkInvariant();
  }

  public String snapshot() {
    return "HashSetCustom{" +
      "size=" + size() +
      ", table=" + table.snapshot() +
      '}';
  }
}
