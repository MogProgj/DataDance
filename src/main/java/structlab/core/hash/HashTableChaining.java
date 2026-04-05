package structlab.core.hash;

import java.util.Objects;

public class HashTableChaining<K, V> {
  private static final int DEFAULT_CAPACITY = 8;
  private static final double LOAD_FACTOR_THRESHOLD = 0.75;

  private Entry<K, V>[] buckets;
  private int size;

  @SuppressWarnings("unchecked")
  public HashTableChaining() {
    this.buckets = (Entry<K, V>[]) new Entry[DEFAULT_CAPACITY];
    this.size = 0;
  }

  @SuppressWarnings("unchecked")
  public HashTableChaining(int initialCapacity) {
    if (initialCapacity <= 0) {
      throw new IllegalArgumentException("Initial capacity must be greater than 0.");
    }

    this.buckets = (Entry<K, V>[]) new Entry[initialCapacity];
    this.size = 0;
  }

  private static class Entry<K, V> {
    private final K key;
    private V value;
    private Entry<K, V> next;

    private Entry(K key, V value) {
      this.key = key;
      this.value = value;
    }
  }

  public int size() {
    return size;
  }

  public int capacity() {
    return buckets.length;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public boolean containsKey(K key) {
    return findEntry(key) != null;
  }

  public V get(K key) {
    Entry<K, V> entry = findEntry(key);
    return entry == null ? null : entry.value;
  }

  public V put(K key, V value) {
    ensureCapacityForNextInsert();

    int index = indexFor(key);
    Entry<K, V> current = buckets[index];

    while (current != null) {
      if (Objects.equals(current.key, key)) {
        V oldValue = current.value;
        current.value = value;
        return oldValue;
      }
      current = current.next;
    }

    Entry<K, V> newEntry = new Entry<>(key, value);
    newEntry.next = buckets[index];
    buckets[index] = newEntry;
    size++;
    return null;
  }

  public V remove(K key) {
    int index = indexFor(key);
    Entry<K, V> current = buckets[index];
    Entry<K, V> previous = null;

    while (current != null) {
      if (Objects.equals(current.key, key)) {
        if (previous == null) {
          buckets[index] = current.next;
        } else {
          previous.next = current.next;
        }

        size--;
        return current.value;
      }

      previous = current;
      current = current.next;
    }

    return null;
  }

  public boolean checkInvariant() {
    if (buckets == null || buckets.length == 0 || size < 0) {
      return false;
    }

    int counted = 0;

    for (Entry<K, V> bucket : buckets) {
      Entry<K, V> current = bucket;
      while (current != null) {
        counted++;
        current = current.next;
      }
    }

    return counted == size;
  }

  public String snapshot() {
    StringBuilder sb = new StringBuilder();
    sb.append("HashTableChaining{");
    sb.append("size=").append(size);
    sb.append(", capacity=").append(buckets.length);
    sb.append(", buckets=[");

    for (int i = 0; i < buckets.length; i++) {
      sb.append(i).append(": ");

      Entry<K, V> current = buckets[i];
      if (current == null) {
        sb.append("empty");
      } else {
        while (current != null) {
          sb.append("(").append(current.key).append(" -> ").append(current.value).append(")");
          current = current.next;
          if (current != null) {
            sb.append(" -> ");
          }
        }
      }

      if (i < buckets.length - 1) {
        sb.append(", ");
      }
    }

    sb.append("]}");
    return sb.toString();
  }

  private Entry<K, V> findEntry(K key) {
    int index = indexFor(key);
    Entry<K, V> current = buckets[index];

    while (current != null) {
      if (Objects.equals(current.key, key)) {
        return current;
      }
      current = current.next;
    }

    return null;
  }

  private int indexFor(K key) {
    return Math.floorMod(Objects.hashCode(key), buckets.length);
  }

  private void ensureCapacityForNextInsert() {
    double projectedLoadFactor = (double) (size + 1) / buckets.length;

    if (projectedLoadFactor > LOAD_FACTOR_THRESHOLD) {
      resize(buckets.length * 2);
    }
  }

  @SuppressWarnings("unchecked")
  private void resize(int newCapacity) {
    Entry<K, V>[] oldBuckets = buckets;
    buckets = (Entry<K, V>[]) new Entry[newCapacity];
    int oldSize = size;
    size = 0;

    for (Entry<K, V> bucket : oldBuckets) {
      Entry<K, V> current = bucket;
      while (current != null) {
        put(current.key, current.value);
        current = current.next;
      }
    }

    size = oldSize;
  }
}
