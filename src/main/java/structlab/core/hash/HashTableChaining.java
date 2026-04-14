package structlab.core.hash;

import java.util.*;

import structlab.trace.Traceable;

/**
 * Hash table resolving collisions by <b>separate chaining</b> (linked-list buckets).
 *
 * <p>Each bucket stores a singly-linked list of {@link Entry} nodes.  New entries
 * are <b>prepended</b> (head-insertion) for O(1) insert, following the convention
 * described in Sedgewick &amp; Wayne &sect;3.4 and the CMU 15-121 &ldquo;Hash Tables&rdquo;
 * lecture.</p>
 *
 * <h3>Load factor</h3>
 * <p>The load factor &alpha;&nbsp;= n/m controls when the table rehashes.
 * Unlike open addressing, separate chaining <em>permits</em> &alpha;&nbsp;&gt;&nbsp;1
 * because chains can grow arbitrarily (Kretinsky, <i>Fundamental Algorithms</i>
 * Ch.&nbsp;5).  The default 0.75 balances time and space for most workloads.</p>
 */
public class HashTableChaining<K, V> implements Traceable {
  public static final int DEFAULT_CAPACITY = 8;
  public static final float DEFAULT_LOAD_FACTOR = 0.75f;
  public static final HashManager.HashType DEFAULT_HASH_TYPE = HashManager.HashType.DIVISION;

  private Entry<K, V>[] buckets;
  private int size;
  private float loadFactor;
  private HashManager.HashType hashType;

  // Evaluation metrics
  private int maxChainSize = 0;
  private int rehashesCounter = 0;
  private int lastUpdatedChain = 0;
  private int chainsCounter = 0;

  @SuppressWarnings("unchecked")
  public HashTableChaining() {
    this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, DEFAULT_HASH_TYPE);
  }

  public HashTableChaining(HashManager.HashType ht) {
    this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR, ht);
  }

  @SuppressWarnings("unchecked")
  public HashTableChaining(int initialCapacity) {
    this(initialCapacity, DEFAULT_LOAD_FACTOR, DEFAULT_HASH_TYPE);
  }

  public HashTableChaining(int initialCapacity, HashManager.HashType ht) {
    this(initialCapacity, DEFAULT_LOAD_FACTOR, ht);
  }

  public HashTableChaining(int initialCapacity, float loadFactor, HashManager.HashType ht) {
    if (initialCapacity <= 0) {
      throw new IllegalArgumentException("Initial capacity must be greater than 0.");
    }
    if (loadFactor <= 0.0f || Float.isNaN(loadFactor)) {
      throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
    }
    this.buckets = newBucketArray(initialCapacity);
    this.size = 0;
    this.loadFactor = loadFactor;
    this.hashType = ht;
  }

  @SuppressWarnings("unchecked")
  private Entry<K, V>[] newBucketArray(int capacity) {
    return (Entry<K, V>[]) new Entry[capacity];
  }

  private static class Entry<K, V> {
    private final K key;
    private V value;
    private Entry<K, V> next;

    private Entry(K key, V value) {
      this.key = key;
      this.value = value;
    }

    private Entry(K key, V value, Entry<K, V> next) {
      this.key = key;
      this.value = value;
      this.next = next;
    }

    @Override
    public String toString() {
      return key + "=" + value;
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

  public void clear() {
    Arrays.fill(buckets, null);
    size = 0;
    lastUpdatedChain = 0;
    maxChainSize = 0;
    rehashesCounter = 0;
    chainsCounter = 0;
  }

  public boolean containsKey(K key) {
    if (key == null) {
      throw new IllegalArgumentException("Key is null in containsKey(K key)");
    }
    return get(key) != null;
  }

  public V get(K key) {
    if (key == null) {
      throw new IllegalArgumentException("Key is null in get(K key)");
    }
    int index = indexFor(key);
    Entry<K, V> node = getInChain(key, buckets[index]);
    return node == null ? null : node.value;
  }

  public V put(K key, V value) {
    if (key == null || value == null) {
      throw new IllegalArgumentException("Key or value is null in put(K key, V value)");
    }
    int index = indexFor(key);
    if (buckets[index] == null) {
      chainsCounter++;
    }

    Entry<K, V> node = getInChain(key, buckets[index]);
    if (node == null) {
      buckets[index] = new Entry<>(key, value, buckets[index]);
      size++;

      // Track max chain length after insertion (accurate measurement)
      int chainLen = 0;
      for (Entry<K, V> n = buckets[index]; n != null; n = n.next) { chainLen++; }
      maxChainSize = Math.max(maxChainSize, chainLen);

      if (size > buckets.length * loadFactor) {
        rehash();
      } else {
        lastUpdatedChain = index;
      }

      return null;
    } else {
      V oldValue = node.value;
      node.value = value;
      lastUpdatedChain = index;
      return oldValue;
    }
  }

  public V remove(K key) {
    if (key == null) {
      throw new IllegalArgumentException("Key is null in remove(K key)");
    }
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

  public boolean replace(K key, V oldValue, V newValue) {
    for (Entry<K, V> node : buckets) {
      for (Entry<K, V> n = node; n != null; n = n.next) {
        if (Objects.equals(n.key, key) && Objects.equals(n.value, oldValue)) {
          n.value = newValue;
          return true;
        }
      }
    }
    return false;
  }

  public boolean containsValue(V value) {
    for (Entry<K, V> node : buckets) {
      for (Entry<K, V> n = node; n != null; n = n.next) {
        if (Objects.equals(n.value, value)) {
          return true;
        }
      }
    }
    return false;
  }

  public Set<K> keySet() {
    Set<K> result = new HashSet<>();
    for (Entry<K, V> node : buckets) {
      for (Entry<K, V> n = node; n != null; n = n.next) {
        result.add(n.key);
      }
    }
    return result;
  }

  public List<V> values() {
    List<V> result = new ArrayList<>();
    for (Entry<K, V> node : buckets) {
      for (Entry<K, V> n = node; n != null; n = n.next) {
        result.add(n.value);
      }
    }
    return result;
  }

  public int getNumberOfCollisions() {
    int counter = 0;
    for (Entry<K, V> node : buckets) {
      if (node != null && node.next != null) {
        for (Entry<K, V> n = node.next; n != null; n = n.next) {
          counter++;
        }
      }
    }
    return counter;
  }

  // ---- Evaluation metrics ----

  public int getMaxChainSize() { return maxChainSize; }

  public int getRehashesCounter() { return rehashesCounter; }

  public int getTableCapacity() { return buckets.length; }

  public int getLastUpdated() { return lastUpdatedChain; }

  public int getNumberOfOccupied() { return chainsCounter; }

  public float getLoadFactor() { return loadFactor; }

  public HashManager.HashType getHashType() { return hashType; }

  // ---- Traceable ----

  @Override
  public String structureName() { return "Hash Table"; }

  @Override
  public String implementationName() { return "HashTableChaining"; }

  @Override
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

  @Override
  public String snapshot() {
    StringBuilder sb = new StringBuilder();
    sb.append("HashTableChaining{");
    sb.append("size=").append(size);
    sb.append(", capacity=").append(buckets.length);
    sb.append(", hashType=").append(hashType);
    sb.append(", maxChainSize=").append(maxChainSize);
    sb.append(", rehashes=").append(rehashesCounter);
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

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    for (Entry<K, V> node : buckets) {
      for (Entry<K, V> n = node; n != null; n = n.next) {
        result.append(n).append(System.lineSeparator());
      }
    }
    return result.toString();
  }

  // ---- Internal ----

  private Entry<K, V> getInChain(K key, Entry<K, V> node) {
    for (Entry<K, V> n = node; n != null; n = n.next) {
      if (Objects.equals(n.key, key)) {
        return n;
      }
    }
    return null;
  }

  private int indexFor(K key) {
    return HashManager.hash(Objects.hashCode(key), buckets.length, hashType);
  }

  private void rehash() {
    HashTableChaining<K, V> newMap = new HashTableChaining<>(buckets.length * 2, loadFactor, hashType);
    for (Entry<K, V> bucket : buckets) {
      Entry<K, V> current = bucket;
      while (current != null) {
        newMap.put(current.key, current.value);
        current = current.next;
      }
    }
    buckets = newMap.buckets;
    maxChainSize = newMap.maxChainSize;
    chainsCounter = newMap.chainsCounter;
    lastUpdatedChain = newMap.lastUpdatedChain;
    rehashesCounter++;
  }
}
