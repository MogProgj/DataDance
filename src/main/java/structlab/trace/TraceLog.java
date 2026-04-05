package structlab.trace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An ordered collection of {@link TraceStep} entries produced during a traced
 * session.  Steps are appended as operations execute and can be retrieved or
 * printed afterwards.
 */
public class TraceLog {
  private final List<TraceStep> steps = new ArrayList<>();

  public void add(TraceStep step) {
    steps.add(step);
  }

  public List<TraceStep> steps() {
    return Collections.unmodifiableList(steps);
  }

  public int size() {
    return steps.size();
  }

  public boolean isEmpty() {
    return steps.isEmpty();
  }

  public void clear() {
    steps.clear();
  }

  /**
   * Formats all steps as a multi-line string, separated by blank lines.
   */
  public String formatAll() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < steps.size(); i++) {
      if (i > 0) {
        sb.append("\n\n");
      }
      sb.append(steps.get(i).format());
    }
    return sb.toString();
  }
}
