package structlab.trace;

/**
 * An immutable record of a single traced operation on a data structure.
 *
 * @param structureName    abstract type (e.g. "Stack", "Queue", "DynamicArray")
 * @param implementationName concrete class (e.g. "ArrayStack", "CircularArrayQueue")
 * @param operationName    the operation performed (e.g. "push", "enqueue", "append")
 * @param input            string description of arguments, or null if none
 * @param beforeState      snapshot string captured before the operation
 * @param afterState       snapshot string captured after the operation
 * @param invariantResult  result of checkInvariant() after the operation
 * @param complexityNote   optional big-O note (e.g. "O(1) amortised"), or null
 * @param explanation      short human-readable description of what happened
 */
public record TraceStep(
    String structureName,
    String implementationName,
    String operationName,
    String input,
    String beforeState,
    String afterState,
    InvariantResult invariantResult,
    String complexityNote,
    String explanation
) {

  /**
   * Returns a formatted multi-line representation suitable for console output.
   */
  public String format() {
    StringBuilder sb = new StringBuilder();
    sb.append("--- ").append(operationName);
    if (input != null) {
      sb.append("(").append(input).append(")");
    }
    sb.append(" on ").append(implementationName).append(" ---\n");
    sb.append("  Before:    ").append(beforeState).append("\n");
    sb.append("  After:     ").append(afterState).append("\n");
    sb.append("  Invariant: ").append(invariantResult).append("\n");
    if (complexityNote != null) {
      sb.append("  Complexity: ").append(complexityNote).append("\n");
    }
    sb.append("  ").append(explanation);
    return sb.toString();
  }
}
