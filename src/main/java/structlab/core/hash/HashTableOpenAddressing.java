package structlab.core.hash;

import java.util.*;

import structlab.trace.Traceable;

/**
 * Hash table resolving collisions by <b>open addressing</b>.
 *
 * <p>Three probing strategies are supported (Kretinsky, <i>Fundamental Algorithms</i>
 * Ch.&nbsp;5; Sedgewick &amp; Wayne &sect;3.4):</p>
 * <ul>
 *   <li><b>LINEAR</b> &mdash; {@code h(k,i) = (h₀(k) + i) mod m}.  Simple and
 *       cache-friendly, but susceptible to <em>primary clustering</em>.</li>
 *   <li><b>QUADRATIC</b> &mdash; {@code h(k,i) = (h₀(k) + i²) mod m}.  Reduces
 *       primary clustering at the cost of secondary clustering.</li>
 *   <li><b>DOUBLE_HASHING</b> &mdash; {@code h(k,i) = (h₁(k) + i·h₂(k)) mod m}
 *       with {@code h₂} producing an odd step (coprime with power-of-2 m),
 *       guaranteeing full slot coverage.</li>
 * </ul>
 *
 * <h3>Load factor</h3>
 * <p>Open addressing <em>requires</em> &alpha;&nbsp;&lt;&nbsp;1 (more slots than
 * entries).  The default &alpha;&nbsp;=&nbsp;0.5 follows Sedgewick's recommendation
 * to resize when N/M&nbsp;&ge;&nbsp;&frac12;, yielding amortised &Theta;(1)
 * operations with an average of ~&frac32; probes per search hit.</p>
 *
 * <h3>Deletion</h3>
 * <p>Uses a <b>DELETED sentinel</b> (lazy deletion) to preserve probe-chain
 * continuity, as described in Kretinsky Ch.&nbsp;5.</p>
 */
public class HashTableOpenAddressing<K, V> implements Traceable {

    public enum OpenAddressingType {
        LINEAR,
        QUADRATIC,
        DOUBLE_HASHING
    }

    public static final int DEFAULT_INITIAL_CAPACITY = 8;
    public static final float DEFAULT_LOAD_FACTOR = 0.5f;
    public static final HashManager.HashType DEFAULT_HASH_TYPE = HashManager.HashType.DIVISION;
    public static final OpenAddressingType DEFAULT_OA_TYPE = OpenAddressingType.LINEAR;

    private Entry<K, V>[] table;
    private final Entry<K, V> DELETED = new Entry<>();
    private int size = 0;
    private float loadFactor;
    private HashManager.HashType hashType;
    private final OpenAddressingType oaType;

    // Evaluation metrics
    private int rehashesCounter = 0;
    private int lastUpdated = 0;
    private int numberOfOccupied = 0;

    public HashTableOpenAddressing() {
        this(DEFAULT_HASH_TYPE);
    }

    public HashTableOpenAddressing(HashManager.HashType ht) {
        this(DEFAULT_INITIAL_CAPACITY, ht);
    }

    public HashTableOpenAddressing(int initialCapacity, HashManager.HashType ht) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR, ht, DEFAULT_OA_TYPE);
    }

    public HashTableOpenAddressing(OpenAddressingType oaType) {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, DEFAULT_HASH_TYPE, oaType);
    }

    public HashTableOpenAddressing(int initialCapacity, float loadFactor, HashManager.HashType ht, OpenAddressingType oaType) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }
        if (loadFactor <= 0.0f || loadFactor >= 1.0f) {
            throw new IllegalArgumentException(
                    "Illegal load factor: " + loadFactor + "; open addressing requires 0 < α < 1");
        }
        this.table = newTableArray(initialCapacity);
        this.loadFactor = loadFactor;
        this.hashType = ht;
        this.oaType = oaType;
    }

    @SuppressWarnings("unchecked")
    private Entry<K, V>[] newTableArray(int capacity) {
        return (Entry<K, V>[]) new Entry[capacity];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return table.length;
    }

    public void clear() {
        Arrays.fill(table, null);
        size = 0;
        lastUpdated = 0;
        rehashesCounter = 0;
        numberOfOccupied = 0;
    }

    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null in containsKey(K key)");
        }
        return get(key) != null;
    }

    public V put(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key or value is null in put(K key, V value)");
        }
        int position = findPosition(key, true);
        if (position == -1) {
            rehash();
            return put(key, value);
        }

        if (table[position] == null || table[position] == DELETED) {
            table[position] = new Entry<>(key, value);
            size++;

            if (size > table.length * loadFactor) {
                rehash();
            } else {
                numberOfOccupied++;
                lastUpdated = position;
            }
            return null;
        } else {
            V oldValue = table[position].value;
            table[position].value = value;
            lastUpdated = position;
            return oldValue;
        }
    }

    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null in get(K key)");
        }
        int position = findPosition(key, false);
        if (position != -1 && table[position] != null && table[position] != DELETED) {
            return table[position].value;
        }
        return null;
    }

    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is null in remove(K key)");
        }
        int position = findPosition(key, false);
        if (position != -1 && table[position] != null && table[position] != DELETED
                && Objects.equals(table[position].key, key)) {
            V value = table[position].value;
            table[position] = DELETED;
            size--;
            return value;
        }
        return null;
    }

    public boolean replace(K key, V oldValue, V newValue) {
        for (Entry<K, V> entry : table) {
            if (entry != null && entry != DELETED
                    && Objects.equals(entry.key, key)
                    && Objects.equals(entry.value, oldValue)) {
                entry.value = newValue;
                return true;
            }
        }
        return false;
    }

    public boolean containsValue(V value) {
        for (Entry<K, V> entry : table) {
            if (entry != null && entry != DELETED && Objects.equals(entry.value, value)) {
                return true;
            }
        }
        return false;
    }

    public Set<K> keySet() {
        Set<K> result = new HashSet<>();
        for (Entry<K, V> entry : table) {
            if (entry != null && entry != DELETED) {
                result.add(entry.key);
            }
        }
        return result;
    }

    public List<V> values() {
        List<V> result = new ArrayList<>();
        for (Entry<K, V> entry : table) {
            if (entry != null && entry != DELETED) {
                result.add(entry.value);
            }
        }
        return result;
    }

    // ---- Evaluation metrics ----

    public int getRehashesCounter() { return rehashesCounter; }

    public int getTableCapacity() { return table.length; }

    public int getLastUpdated() { return lastUpdated; }

    public int getNumberOfOccupied() { return numberOfOccupied; }

    public float getLoadFactor() { return loadFactor; }

    public HashManager.HashType getHashType() { return hashType; }

    public OpenAddressingType getOpenAddressingType() { return oaType; }

    // ---- Traceable ----

    @Override
    public String structureName() { return "Hash Table"; }

    @Override
    public String implementationName() { return "HashTableOpenAddressing"; }

    @Override
    public boolean checkInvariant() {
        if (table == null || table.length == 0 || size < 0) {
            return false;
        }
        int counted = 0;
        for (Entry<K, V> entry : table) {
            if (entry != null && entry != DELETED) {
                counted++;
            }
        }
        return counted == size;
    }

    @Override
    public String snapshot() {
        StringBuilder sb = new StringBuilder();
        sb.append("HashTableOpenAddressing{");
        sb.append("size=").append(size);
        sb.append(", capacity=").append(table.length);
        sb.append(", oaType=").append(oaType);
        sb.append(", hashType=").append(hashType);
        sb.append(", rehashes=").append(rehashesCounter);
        sb.append(", slots=[");

        for (int i = 0; i < table.length; i++) {
            sb.append(i).append(": ");
            if (table[i] == null) {
                sb.append("empty");
            } else if (table[i] == DELETED) {
                sb.append("DELETED");
            } else {
                sb.append("(").append(table[i].key).append(" -> ").append(table[i].value).append(")");
            }
            if (i < table.length - 1) {
                sb.append(", ");
            }
        }

        sb.append("]}");
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Entry<K, V> entry : table) {
            if (entry != null && entry != DELETED) {
                result.append(entry).append(System.lineSeparator());
            }
        }
        return result.toString();
    }

    // ---- Internal ----

    private int findPosition(K key, boolean stopAtDeleted) {
        int index = HashManager.hash(key.hashCode(), table.length, hashType);
        int position = index;

        for (int i = 0; i < table.length; i++) {
            if (table[position] == null) {
                return position;
            }
            if (table[position] == DELETED && stopAtDeleted) {
                return position;
            }
            if (table[position] != DELETED && Objects.equals(table[position].key, key)) {
                return position;
            }
            position = calculatePosition(index, i, key);
        }
        return -1;
    }

    private int calculatePosition(int index, int i, K key) {
        switch (oaType) {
            case LINEAR:
                return (index + i + 1) % table.length;
            case QUADRATIC:
                return (index + (i + 1) * (i + 1)) % table.length;
            case DOUBLE_HASHING:
                // Step must be coprime with table.length (always a power of 2),
                // so we force an odd step: h₂(k) = 2·(h mod (m/2)) + 1 ∈ {1,3,…,m-1}.
                // Use (i+1) to avoid re-probing the home slot at i=0.
                int half = Math.max(1, table.length / 2);
                int step = 2 * ((key.hashCode() & 0x7fffffff) % half) + 1;
                return (index + (i + 1) * step) % table.length;
            default:
                return index;
        }
    }

    private void rehash() {
        HashTableOpenAddressing<K, V> newMap = new HashTableOpenAddressing<>(table.length * 2, loadFactor, hashType, oaType);
        for (Entry<K, V> entry : table) {
            if (entry != null && entry != DELETED) {
                newMap.put(entry.key, entry.value);
            }
        }
        table = newMap.table;
        numberOfOccupied = newMap.numberOfOccupied;
        lastUpdated = newMap.lastUpdated;
        rehashesCounter++;
    }

    protected static class Entry<K, V> {
        protected K key;
        protected V value;

        protected Entry() {}

        protected Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }
}
