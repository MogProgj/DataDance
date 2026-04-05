package structlab.core.queue;

import structlab.trace.Traceable;

public class CircularArrayQueue<T> implements Traceable {
  private static final int DEFAULT_CAPACITY = 4;

  private Object[] data;
  private int front;
  private int size;

  public CircularArrayQueue() {
    this(DEFAULT_CAPACITY);
  }

  public CircularArrayQueue(int initialCapacity) {
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

  public void enqueue(T value) {
    ensureCapacityForOneMore();
    int rearIndex = (front + size) % data.length;
    data[rearIndex] = value;
    size++;
  }

  @SuppressWarnings("unchecked")
  public T dequeue() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot dequeue from an empty queue.");
    }

    T value = (T) data[front];
    data[front] = null;
    front = (front + 1) % data.length;
    size--;
    return value;
  }

  @SuppressWarnings("unchecked")
  public T peek() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot peek into an empty queue.");
    }

    return (T) data[front];
  }

  @Override
  public String structureName() {
    return "Queue";
  }

  @Override
  public String implementationName() {
    return "CircularArrayQueue";
  }

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

    return "CircularArrayQueue{" +
      "size=" + size +
      ", capacity=" + data.length +
      ", frontIndex=" + front +
      ", logical=" + logical +
      ", raw=" + java.util.Arrays.toString(data) +
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
}
