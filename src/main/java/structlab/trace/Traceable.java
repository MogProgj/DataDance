package structlab.trace;

/**
 * Minimal interface for structures that can participate in tracing.
 * Provides the two things every trace needs: a snapshot of current state
 * and an invariant check.
 */
public interface Traceable {

  /** Human-readable name of the abstract data type (e.g. "Dynamic Array", "Stack"). */
  String structureName();

  /** Name of the concrete implementation class (e.g. "DynamicArray", "ArrayStack"). */
  String implementationName();

  /** Returns a string snapshot of the current internal state. */
  String snapshot();

  /** Checks all structural invariants; returns true if everything is valid. */
  boolean checkInvariant();
}
