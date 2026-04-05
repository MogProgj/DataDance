/**
 * Operation tracing and logging layer.
 *
 * <p>This layer records before-state, operation description, after-state, and
 * invariant results for any operation performed on a core structure.  It depends
 * only on {@code structlab.core} and produces structured {@link TraceStep}
 * records collected in a {@link TraceLog}, consumed by demos and eventually
 * by {@code structlab.render}.
 *
 * <p>Core types: {@link TraceStep}, {@link TraceLog}, {@link InvariantResult},
 * {@link Traceable}.
 *
 * <p>Traced wrappers: {@link TracedDynamicArray}, {@link TracedFixedArray},
 * {@link TracedArrayStack}, {@link TracedLinkedStack},
 * {@link TracedCircularArrayQueue}, {@link TracedLinkedQueue},
 * {@link TracedTwoStackQueue}.
 */
package structlab.trace;
