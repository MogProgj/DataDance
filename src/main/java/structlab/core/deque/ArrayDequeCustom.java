package structlab.core.deque;

import java.util.Arrays;
import structlab.trace.Traceable;

public class ArrayDequeCustom<T> implements Traceable {
  private static final int DEFAULT_CAPACITY = 4;

  private Object[] data;
  private int front;
  private int size;

  public ArrayDequeCustom() {
    this(DEFAULT_CAPACITY);
  }

  public ArrayDequeCustom(int initialCapacity) {
    if (initialCapacity <= 0) {
      throw new IllegalArgumentException("Initial capacity must be greater than 0.");
    }

    this.data = new Object[initialCapacity];
    this.front = 0;
    this.size = 0;
  }

  public int size() {
    return size;
  }

  public int capacity() {
    return data.length;
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public void addFirst(T value) {
    ensureCapacityForOneMore();
    front = mod(front - 1, data.length);
    data[front] = value;
    size++;
  }

  public void addLast(T value) {
    ensureCapacityForOneMore();
    int rearIndex = (front + size) % data.length;
    data[rearIndex] = value;
    size++;
  }

  @SuppressWarnings("unchecked")
  public T removeFirst() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot removeFirst from an empty deque.");
    }

    T value = (T) data[front];
    data[front] = null;
    front = (front + 1) % data.length;
    size--;
    return value;
  }

  @SuppressWarnings("unchecked")
  public T removeLast() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot removeLast from an empty deque.");
    }

    int rearIndex = (front + size - 1) % data.length;
    T value = (T) data[rearIndex];
    data[rearIndex] = null;
    size--;
    return value;
  }

  @SuppressWarnings("unchecked")
  public T peekFirst() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot peekFirst into an empty deque.");
    }

    return (T) data[front];
  }

  @SuppressWarnings("unchecked")
  public T peekLast() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot peekLast into an empty deque.");
    }

    int rearIndex = (front + size - 1) % data.length;
    return (T) data[rearIndex];
  }

  @Override
  public String structureName() { return "Deque"; }

  @Override
  public String implementationName() { return "ArrayDequeCustom"; }

  @Override
  public boolean checkInvariant() {
    return data != null
      && data.length > 0
      && front >= 0
      && front < data.length
      && size >= 0
      && size <= data.length;
  }

  @Override
  public String snapshot() {
    StringBuilder logical = new StringBuilder();
    logical.append("[");

    for (int i = 0; i < size; i++) {
      int index = (front + i) % data.length;
      logical.append(data[index]);
      if (i < size - 1) {
        logical.append(", ");
      }
    }

    logical.append("]");

    return "ArrayDequeCustom{" +
      "size=" + size +
      ", capacity=" + data.length +
      ", frontIndex=" + front +
      ", logical=" + logical +
      ", raw=" + Arrays.toString(data) +
      '}';
  }

  private void ensureCapacityForOneMore() {
    if (size == data.length) {
      resize(data.length * 2);
    }
  }

  private void resize(int newCapacity) {
    Object[] newData = new Object[newCapacity];

    for (int i = 0; i < size; i++) {
      newData[i] = data[(front + i) % data.length];
    }

    data = newData;
    front = 0;
  }

  private int mod(int value, int modulus) {
    int result = value % modulus;
    return result < 0 ? result + modulus : result;
  }
}
