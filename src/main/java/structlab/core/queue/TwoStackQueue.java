package structlab.core.queue;

import structlab.core.stack.ArrayStack;
import structlab.trace.Traceable;

public class TwoStackQueue<T> implements Traceable {
  private final ArrayStack<T> inbox;
  private final ArrayStack<T> outbox;

  public TwoStackQueue() {
    this.inbox = new ArrayStack<>();
    this.outbox = new ArrayStack<>();
  }

  public int size() {
    return inbox.size() + outbox.size();
  }

  public boolean isEmpty() {
    return inbox.isEmpty() && outbox.isEmpty();
  }

  public void enqueue(T value) {
    inbox.push(value);
  }

  public T dequeue() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot dequeue from an empty queue.");
    }

    transferIfNeeded();
    return outbox.pop();
  }

  public T peek() {
    if (isEmpty()) {
      throw new IllegalStateException("Cannot peek into an empty queue.");
    }

    transferIfNeeded();
    return outbox.peek();
  }

  @Override
  public String structureName() {
    return "Queue";
  }

  @Override
  public String implementationName() {
    return "TwoStackQueue";
  }

  @Override
  public boolean checkInvariant() {
    return inbox.checkInvariant() && outbox.checkInvariant();
  }

  @Override
  public String snapshot() {
    return "TwoStackQueue{" +
      "size=" + size() +
      ", inbox=" + inbox.snapshot() +
      ", outbox=" + outbox.snapshot() +
      '}';
  }

  private void transferIfNeeded() {
    if (outbox.isEmpty()) {
      while (!inbox.isEmpty()) {
        outbox.push(inbox.pop());
      }
    }
  }
}
