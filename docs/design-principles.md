# Design Principles

These principles govern every decision made in this project.  They are not
suggestions.  They are constraints.

---

## 1. Implementation before convenience

No built-in Java collection types for the structures being studied.

If you are implementing a stack, you do not secretly use `java.util.Stack` or
`java.util.ArrayDeque`.  That would defeat the entire point.  Reach for the
standard library only when using it as a foundation is explicitly the lesson
(e.g. comparing your implementation to the standard one).

---

## 2. Visibility of internal state

Every structure must be inspectable.

Not just "the operation succeeded", but:
- what changed
- where it changed
- what the underlying storage looks like right now
- whether all invariants still hold

If you cannot see inside the box, you are not learning how the box works.

---

## 3. Multiple implementations of the same ADT

The same abstract data type should be realised in at least two concrete ways
wherever meaningful.

Examples:
- Stack via dynamic array vs stack via linked list
- Queue via circular array vs queue via linked list vs queue via two stacks

The differences between implementations reveal tradeoffs that no single
implementation can expose on its own.

---

## 4. Invariant checking

Every structure must define its own invariants and provide a method to verify
them.

Examples:
- A dynamic array's `size` must always be ≤ `capacity`
- A circular queue's internal `head` and `tail` must stay within bounds
- A heap must satisfy the heap property after every mutation

Invariant checks should be runnable after every operation during tests and demos.

---

## 5. Traceability

Every operation must be explainable.

A trace of an operation includes:
- the before-state of the structure
- a description of the operation
- the after-state of the structure
- a note on expected runtime complexity
- the result of the invariant check

This makes operations teachable, not just executable.

---

## 6. Separation of concerns

Keep these layers distinct:
- core data structure logic
- tracing and logging
- rendering and visualisation
- demo scenarios
- future UI

Do not mix rendering into core logic.  Do not let demo scripts contain data
structure code.  Clear boundaries make the project scalable.

---

## 7. Educational clarity over unnecessary abstraction

Abstraction is welcome when it reflects a real conceptual boundary.

Abstraction for its own sake — elaborate generic hierarchies, over-engineered
factories, unnecessary design patterns — obscures the actual lesson.

If a simpler design teaches the same concept, use the simpler design.
