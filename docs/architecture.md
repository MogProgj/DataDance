# Architecture

StructLab is designed around strict layer separation so that the project can grow
without collapsing into an undifferentiated mass of code.

---

## Layers

### 1. Core (`structlab.core`)

Pure Java implementations of data structures.

Rules for this layer:
- No dependency on any other StructLab layer.
- No built-in Java collection types for the structure under study.
- Each structure exposes its own minimal interface.
- Invariant-checking methods live here (e.g. `checkInvariant()`).

Sub-packages:
- `core.array` — dynamic array
- `core.list` — singly and doubly linked lists
- `core.stack` — stack implementations
- `core.queue` — queue implementations
- `core.deque` — deque implementations
- `core.heap` — heap and priority-queue implementations
- `core.hash` — hash table implementations

### 2. Trace (`structlab.trace`)

Operation tracing and logging.

Rules for this layer:
- Depends only on `core`.
- Records before-state, operation, after-state, and invariant results.
- No rendering logic.  Produces structured trace objects.

### 3. Render (`structlab.render`)

ASCII / console state visualisation.

Rules for this layer:
- Depends on `core` and `trace`.
- Converts trace objects and structure state into human-readable output.
- No business logic.  Pure presentation.

### 4. Demo (`structlab.demo`)

Scripted scenarios that exercise structures and trace/render their behaviour.

Rules for this layer:
- Depends on `core`, `trace`, and `render`.
- Acts as a high-level script runner, not a framework.
- Each demo class demonstrates a specific structure or comparison.

### 5. App (`structlab.app`)

Entry points and wiring.

Rules for this layer:
- Thin shell that wires the other layers together.
- Contains `main` methods for running demos from the command line.
- No data-structure logic of its own.

---

## Dependency graph

```
app
 └── demo
      ├── render
      │    └── trace
      │         └── core
      └── trace
           └── core
```

No layer may depend on a layer above it in this graph.

---

## Test layout

Tests live under `src/test/java/structlab/` and mirror the source package
structure.  Unit tests target `core` directly.  Integration-style tests may
exercise `demo` scenarios.

---

## Future layers

- A `ui` layer (JavaFX) may be added in Phase 6 as a peer to `demo`.  It would
  depend on `render` and `trace` but never on `demo`.
- A `compare` layer may be added in Phase 5 to run the same operation sequence
  over multiple implementations and produce aligned output.

