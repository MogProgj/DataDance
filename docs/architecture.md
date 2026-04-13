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
- `core.graph` — Graph (directed/undirected, weighted/unweighted adjacency list),
  AlgorithmFrame (14-field execution snapshot including AlgorithmTelemetry),
  AlgorithmTelemetry (phase, metrics, sections, events), 11 algorithm runners

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
`StructLabFxApp` loads FXML; `MainWindowController` manages six pages
(Explore, Compare, Learn, Activity, Settings, Algorithm Lab) through
`NavigationPage`.

### Controllers (`gui.controller`)
- **`AlgorithmLabController`** — self-contained Algorithm Lab workspace with
  preset selection, algorithm selection, playback, compare mode, scenario
  save/load, and `AlgorithmTrackerPane` (telemetry display, visibility
  bound to `AppSettings.showAlgorithmTracker`)
- **`AlgorithmTrackerPane`** — left-panel widget rendering structured
  `AlgorithmTelemetry` (phase, metrics, sections, events) or fallback
  generic frame data

### Visual state system (`gui.visual`)
See [visual-state-system.md](visual-state-system.md) for full detail.

Key types:
- **`VisualState`** — sealed interface implemented by all 13 state model records
- **`StateModelParser`** — parses snapshot strings into `VisualState` subtypes
- **`VisualPaneCache`** — manages pane instances and dispatches updates
- **`VisualStateHost`** — reusable StackPane encapsulating the visual-or-text-
  fallback pattern; used by both Explore and Compare modes
- **`UiComponents`** — shared static UI factory methods (styledLabel, card,
  settingsCard, buttonRow, etc.) extracted from the controller
- **`VisualStateFactory`** — static entry point (legacy; superseded by VisualStateHost)
- **`ComparisonCardPane`** — per-card visual rendering for Compare mode
- 14 family-specific visual panes + GraphVisualPane

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
VisualStateHost                    visual-or-text-fallback host
        │
        ▼
ScrollPane in Explore or Compare
```

`VisualStateHost` owns a `VisualPaneCache` instance and delegates parsing
to `StateModelParser`.  Both Explore mode (single instance in the
controller) and Compare mode (per-card instance in `ComparisonCardPane`)
use `VisualStateHost` for consistent visual/text rendering.

---

## State extraction direction

Snapshots are currently produced as formatted strings by core structures
(`snapshot()` method).  The GUI parses these via `SnapshotParser` →
`StateModelParser` → typed `VisualState` records.

Future direction: core structures may provide structured state objects
directly, eliminating the string round-trip for the GUI path while keeping
the text snapshot for console/trace compatibility.

