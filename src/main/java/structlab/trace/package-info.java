/**
 * Operation tracing and logging layer.
 *
 * <p>This layer records before-state, operation description, after-state, and
 * invariant results for any operation performed on a core structure.  It depends
 * only on {@code structlab.core} and produces structured trace objects consumed
 * by {@code structlab.render}.
 */
package structlab.trace;
