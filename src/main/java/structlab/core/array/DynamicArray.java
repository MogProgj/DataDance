package structlab.core.array;

import java.util.Arrays;

public class DynamicArray<T> {
  private static final int DEFAULT_CAPACITY = 4;

  private Object[] data;
  private int size;

  public DynamicArray() {
    this(DEFAULT_CAPACITY);
  }

  public DynamicArray(int initialCapacity) {
    if (initialCapacity <= 0) {
      throw new IllegalArgumentException("Initial capacity must be greater than 0.");
    }

    this.data = new Object[initialCapacity];
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

  public void append(T value) {
    ensureCapacityForOneMore();
    data[size] = value;
    size++;
  }

  public void insert(int index, T value) {
    checkPositionIndex(index);

    ensureCapacityForOneMore();

    for (int i = size; i > index; i--) {
      data[i] = data[i - 1];
    }

    data[index] = value;
    size++;
  }

  @SuppressWarnings("unchecked")
  public T get(int index) {
    checkElementIndex(index);
    return (T) data[index];
  }

  public void set(int index, T value) {
    checkElementIndex(index);
    data[index] = value;
  }

  @SuppressWarnings("unchecked")
  public T removeAt(int index) {
    checkElementIndex(index);

    T removed = (T) data[index];

    for (int i = index; i < size - 1; i++) {
      data[i] = data[i + 1];
    }

    data[size - 1] = null;
    size--;

    return removed;
  }

  public boolean checkInvariant() {
    return data != null
      && size >= 0
      && size <= data.length;
  }

  public String snapshot() {
    StringBuilder sb = new StringBuilder();

    sb.append("DynamicArray{");
    sb.append("size=").append(size);
    sb.append(", capacity=").append(data.length);
    sb.append(", elements=[");

    for (int i = 0; i < size; i++) {
      sb.append(data[i]);
      if (i < size - 1) {
        sb.append(", ");
      }
    }

    sb.append("], raw=").append(Arrays.toString(data));
    sb.append("}");

    return sb.toString();
  }

  private void ensureCapacityForOneMore() {
    if (size == data.length) {
      resize(data.length * 2);
    }
  }

  private void resize(int newCapacity) {
    Object[] newData = new Object[newCapacity];

    for (int i = 0; i < size; i++) {
      newData[i] = data[i];
    }

    data = newData;
  }

  private void checkElementIndex(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException(
        "Index: " + index + ", Size: " + size
      );
    }
  }

  private void checkPositionIndex(int index) {
    if (index < 0 || index > size) {
      throw new IndexOutOfBoundsException(
        "Index: " + index + ", Size: " + size
      );
    }
  }
}
