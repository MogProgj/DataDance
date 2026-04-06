package structlab.render;

import structlab.trace.TraceLog;
import structlab.trace.TraceStep;

/**
 * Renders {@link TraceStep} entries as formatted console output with
 * structure-aware ASCII visualizations.  This is the main entry point
 * for Phase 3 rendering.
 */
public final class ConsoleTraceRenderer {

  private static final String DIVIDER = "=".repeat(60);
  private static final String THIN_DIVIDER = "-".repeat(40);

  private ConsoleTraceRenderer() {}

  /**
   * Renders a single trace step with before/after structure diagrams.
   */
  public static String render(TraceStep step) {
    StringBuilder sb = new StringBuilder();

    // Header
    sb.append(DIVIDER).append("\n");
    sb.append("  ").append(step.operationName());
    if (step.input() != null) {
      sb.append("(").append(step.input()).append(")");
    }
    sb.append("  on ").append(step.implementationName()).append("\n");
    sb.append(DIVIDER).append("\n");

    // Before state
    sb.append("\n");
    sb.append("  BEFORE:\n");
    sb.append(StructureRenderer.render(step.beforeState()));

    // After state
    sb.append("\n");
    sb.append("  AFTER:\n");
    sb.append(StructureRenderer.render(step.afterState()));

    // Footer: invariant, complexity, explanation
    sb.append("\n");
    sb.append("  ").append(THIN_DIVIDER).append("\n");
    sb.append("  Invariant:  ").append(step.invariantResult()).append("\n");
    if (step.complexityNote() != null) {
      sb.append("  Complexity: ").append(step.complexityNote()).append("\n");
    }
    sb.append("  ").append(step.explanation()).append("\n");

    return sb.toString();
  }

  /**
   * Renders all steps in a trace log with numbered headings.
   */
  public static String renderAll(TraceLog log) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < log.size(); i++) {
      if (i > 0) sb.append("\n");
      sb.append("Step ").append(i + 1).append(" of ").append(log.size()).append("\n");
      sb.append(render(log.steps().get(i)));
    }
    return sb.toString();
  }
}
