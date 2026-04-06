package structlab.demo;

import structlab.core.array.FixedArray;
import structlab.render.ConsoleTraceRenderer;
import structlab.trace.TraceLog;
import structlab.trace.TracedFixedArray;

public class TracedFixedArrayDemo {
  public static void main(String[] args) {
    FixedArray<Integer> array = new FixedArray<>(4);
    TraceLog log = new TraceLog();
    TracedFixedArray<Integer> traced = new TracedFixedArray<>(array, log);

    System.out.println("=== Traced FixedArray Demo (capacity=4) ===\n");

    traced.append(10);
    traced.append(20);
    traced.insert(1, 15);
    traced.get(0);
    traced.removeAt(1);
    traced.append(30);
    traced.append(40);

    // Demonstrate failure tracing: array is now full
    try {
      traced.append(50);
    } catch (IllegalStateException e) {
      // failure was traced before the exception
    }

    System.out.println(ConsoleTraceRenderer.renderAll(log));
  }
}
