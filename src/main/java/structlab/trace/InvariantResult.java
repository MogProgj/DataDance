package structlab.trace;

/**
 * Result of a post-operation invariant check.
 */
public enum InvariantResult {
  PASSED,
  FAILED,
  SKIPPED;

  public static InvariantResult fromBoolean(boolean passed) {
    return passed ? PASSED : FAILED;
  }
}
