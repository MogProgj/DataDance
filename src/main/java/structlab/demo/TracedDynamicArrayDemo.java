package structlab.demo;

import structlab.core.array.DynamicArray;
import structlab.trace.TraceLog;
import structlab.trace.TracedDynamicArray;

public class TracedDynamicArrayDemo {
  public static void main(String[] args) {
    DynamicArray<Integer> array = new DynamicArray<>(2);
    TraceLog log = new TraceLog();
    TracedDynamicArray<Integer> traced = new TracedDynamicArray<>(array, log);

    System.out.println("=== Traced DynamicArray Demo ===");
    System.out.println("Starting with capacity 2\n");

    traced.append(10);
    traced.append(20);
    traced.append(30);  // triggers resize
    traced.insert(1, 99);
    traced.get(0);
    traced.removeAt(2);

    System.out.println(log.formatAll());
  }
}
