# Architecture

StructLab is layered so that data-structure logic, tracing, rendering,
metadata, the interactive shell, the GUI, and the visual state system
each have a well-defined role and dependency direction.

---

## Layer map

```
 GUI (structlab.gui)                 Terminal Shell (structlab.app.shell)
        │                                      │
        └──────────┐              ┌────────────┘
                   ▼              ▼
           StructLabService (structlab.app.service)
                   │
        ┌──────────┼──────────────────────┐
        ▼          ▼                      ▼
   Registry    SessionManager       ComparisonSession
 (structlab    (structlab.app       (structlab.app
  .registry)    .session)            .comparison)
                   │
                   ▼
             RuntimeFactory → StructureRuntime adapters
             (structlab.app.runtime)
                   │
                   ▼
            Traced wrappers (structlab.trace)
                   │
                   ▼
            Core structures (structlab.core)
```

---

## 1. Core (`structlab.core`)

Pure Java implementations of data structures.  No dependency on any other
StructLab layer.  No use of built-in Java collection types for the
structures under study.

Sub-packages:
- `core.array` — FixedArray, DynamicArray
- `core.list` — SinglyLinkedList, DoublyLinkedList, CircularLinkedList
- `core.stack` — ArrayStack, LinkedStack
- `core.queue` — CircularArrayQueue, LinkedQueue, TwoStackQueue
- `core.deque` — ArrayDequeCustom, LinkedDeque
- `core.heap` — BinaryHeap, HeapPriorityQueue
- `core.hash` — HashTableChaining, HashTableOpenAddressing, HashSetCustom

Each structure:
- Exposes its own minimal interface
- Has `checkInvariant()` for correctness verification
- Produces a `snapshot()` string encoding its full internal state

## 2. Trace (`structlab.trace`)

Traced wrappers (`TracedArrayStack`, `TracedLinkedQueue`, etc.) that wrap
core structures and produce `TraceStep` records for every operation.  Each
step captures: operation name, input, before-state snapshot, after-state
snapshot, invariant result, complexity note, and human explanation.

`TraceLog` collects ordered steps for a session.

## 3. Render (`structlab.render`)

- `SnapshotParser` — regex-based field extraction from snapshot strings
- `StructureRenderer` — ASCII/text state rendering for console output
- `ConsoleTraceRenderer` — formatted trace step display

## 4. Registry (`structlab.registry`)

- `StructureMetadata` / `ImplementationMetadata` — structure and variant definitions
- `StructureRegistry` / `InMemoryStructureRegistry` — searchable metadata store
- `RegistrySeeder` — populates the registry at startup

## 5. App layer (`structlab.app`)

### Service (`app.service`)
`StructLabService` is the facade for both GUI and programmatic consumers.
Record-based DTOs: `StructureSummary`, `ImplementationSummary`,
`SessionSnapshot`, `OperationInfo`, `ExecutionResult`.

### Session (`app.session`)
`SessionManager` holds at most one `ActiveStructureSession`.
`ActiveStructureSession` owns a `StructureRuntime` and operation history.

### Runtime (`app.runtime`)
`RuntimeFactory` maps registry metadata to a `StructureRuntime` adapter.
Each adapter exposes `getCurrentState()` (raw snapshot), `renderCurrentState()`
(text fallback), `execute(op, args)`, and state management.

### Comparison (`app.comparison`)
`ComparisonSession` manages parallel runtime entries for multiple
implementations and executes the same operation across all of them.

### Shell (`app.shell`)
`AppShell` runs the REPL loop.  `CommandParser` / `CommandRouter` handle
input.  Command handlers live in `app.command`.

## 6. GUI (`structlab.gui`)

### Shell
`StructLabFxApp` loads FXML; `MainWindowController` manages five pages
(Explore, Compare, Learn, Activity, Settings) through `NavigationPage`.

### Visual state system (`gui.visual`)
See [visual-state-system.md](visual-state-system.md) for full detail.

Key types:
- **`VisualState`** — sealed interface implemented by all 13 state model records
- **`StateModelParser`** — parses snapshot strings into `VisualState` subtypes
- **`VisualPaneCache`** — manages pane instances and dispatches updates
- **`VisualStateFactory`** — static entry point for Explore mode
- **`ComparisonCardPane`** — per-card visual rendering for Compare mode
- 14 family-specific visual panes

---

## GUI vs Terminal separation

Both surfaces consume `StructLabService`.  The GUI never touches
`CommandRouter` or `CommandParser`.  The terminal never touches JavaFX.
Session ownership is shared through `SessionManager`.

---

## Visual state flow

```
runtime.getCurrentState()          raw snapshot string
        │
        ▼
StateModelParser.parse(snapshot)   → VisualState (sealed)
        │
        ▼
VisualPaneCache.update(state)      → JavaFX Node
        │
        ▼
ScrollPane in Explore or Compare host
```

The same `VisualPaneCache` logic serves both Explore (static singleton
in `VisualStateFactory`) and Compare (per-card instance in
`ComparisonCardPane`).

---

## State extraction direction

Snapshots are currently produced as formatted strings by core structures
(`snapshot()` method).  The GUI parses these via `SnapshotParser` →
`StateModelParser` → typed `VisualState` records.

Future direction: core structures may provide structured state objects
directly, eliminating the string round-trip for the GUI path while keeping
the text snapshot for console/trace compatibility.
# Architecture

StructLab is designed around strict layer separation so that the project can grow
without collapsing into an undifferentiated mass of Code . 

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

