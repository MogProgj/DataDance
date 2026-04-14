package structlab.core.hash;

import structlab.trace.Traceable;

public class HashSetCustom<T> implements Traceable {
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

  public void clear() {
    table.clear();
  }

  @Override
  public String structureName() { return "Hash Set"; }

  @Override
  public String implementationName() { return "HashSetCustom"; }

  @Override
  public boolean checkInvariant() {
    return table != null && table.checkInvariant();
  }

  @Override
  public String snapshot() {
    return "HashSetCustom{" +
      "size=" + size() +
      ", table=" + table.snapshot() +
      '}';
  }
}
