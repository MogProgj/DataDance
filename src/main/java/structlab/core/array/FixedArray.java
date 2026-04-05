package structlab.core.array;

import java.util.Arrays;
import structlab.trace.Traceable;

public class FixedArray<T> implements Traceable {
  private final Object[] data;
  private int size;

  public FixedArray(int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("Capacity must be greater than 0.");
    }

    this.data = new Object[capacity];
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

  public boolean isFull() {
    return size == data.length;
  }

  public void append(T value) {
    if (isFull()) {
      throw new IllegalStateException(
        "Cannot append to a full fixed array (capacity=" + data.length + ").");
    }

    data[size] = value;
    size++;
  }

  public void insert(int index, T value) {
    if (isFull()) {
      throw new IllegalStateException(
        "Cannot insert into a full fixed array (capacity=" + data.length + ").");
    }

    checkPositionIndex(index);

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

  @Override
  public String structureName() {
    return "Fixed Array";
  }

  @Override
  public String implementationName() {
    return "FixedArray";
  }

  @Override
  public boolean checkInvariant() {
    return data != null
      && size >= 0
      && size <= data.length;
  }

  @Override
  public String snapshot() {
    StringBuilder sb = new StringBuilder();

    sb.append("FixedArray{");
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
