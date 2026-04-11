# Visual State System

The visual state system is the pipeline that transforms raw structure
snapshot strings into interactive JavaFX visual panes.

---

## Pipeline overview

```
snapshot string
      │
      ▼
SnapshotParser          low-level field extraction (regex)
      │
      ▼
StateModelParser        family-specific parsing → VisualState record
      │
      ▼
VisualPaneCache         dispatches to correct pane, manages instances
      │
      ▼
*VisualPane             family-specific JavaFX rendering
      │
      ▼
Node                    displayed in Explore or Compare host
```

---

## Key types

### VisualState (sealed interface)

All 13 state model records implement `VisualState`.  This sealed
hierarchy gives:
- Compile-time exhaustiveness when adding new families
- Type-safe dispatch without raw string inspection
- A common `isEmpty()` contract

Permitted subtypes:
`StackStateModel`, `QueueStateModel`, `CircularQueueStateModel`,
`HeapStateModel`, `HashChainingStateModel`, `HashOpenAddressingStateModel`,
`HashSetStateModel`, `SinglyLinkedListStateModel`, `DoublyLinkedListStateModel`,
`ArrayDequeStateModel`, `LinkedDequeStateModel`, `FixedArrayStateModel`,
`DynamicArrayStateModel`

### StateModelParser

- `structureType(snapshot)` — extracts the type prefix
- `parse(snapshot)` — unified entry point returning `VisualState` (or null)
- `parseArrayStack(snapshot)`, `parseLinkedStack(snapshot)`, etc. — family-specific parsers

The unified `parse()` method is used by `VisualStateFactory` and
`ComparisonCardPane` to avoid duplicating type-dispatch switches.

### VisualPaneCache

Holds lazy-initialized pane instances and dispatches `VisualState`
updates to the correct pane via `instanceof` checks.

Two usage patterns:
- `VisualStateFactory` holds a single static cache (Explore mode — one session)
- `ComparisonCardPane` holds one cache per card (Compare mode — N implementations)

### VisualStateFactory

Static API for Explore mode:
- `isSupported(snapshot)` — returns true if a visual pane exists
- `createOrUpdate(snapshot)` — parses and updates the cached pane
- `reset()` — clears cached panes on session change

### SnapshotParser

Low-level regex-based extraction from snapshot strings.  Methods:
`type()`, `intField()`, `stringField()`, `listField()`, `chainField()`,
`doublyLinkedChainField()`, `embeddedSnapshot()`, `bucketEntries()`,
`slotEntries()`.

---

## Family-specific panes

| Pane | State Model | Visual approach |
|------|------------|-----------------|
| StackVisualPane | StackStateModel | Vertical stack, top marker |
| QueueVisualPane | QueueStateModel | Horizontal strip, front → rear arrows |
| CircularQueueVisualPane | CircularQueueStateModel | Dual lanes (raw + logical), F/R markers |
| HeapVisualPane | HeapStateModel | Tree levels + backing array strip |
| PriorityQueueVisualPane | HeapStateModel | "Next out" hero, priority-sorted chip strip |
| HashChainingVisualPane | HashChainingStateModel | Bucket rows, collision chain chips |
| HashOpenAddressingVisualPane | HashOpenAddressingStateModel | Flow grid: EMPTY / OCCUPIED / DELETED |
| HashSetVisualPane | HashSetStateModel | Bucket rows, members only |
| SinglyLinkedListVisualPane | SinglyLinkedListStateModel | Forward chain, null terminator |
| DoublyLinkedListVisualPane | DoublyLinkedListStateModel | Bidirectional (⇄), link indicators |
| ArrayDequeVisualPane | ArrayDequeStateModel | Dual lanes, size/capacity boundary |
| LinkedDequeVisualPane | LinkedDequeStateModel | Bidirectional chain, front/rear markers |
| FixedArrayVisualPane | FixedArrayStateModel | Rigid indexed grid, capacity boundary |
| DynamicArrayVisualPane | DynamicArrayStateModel | Indexed grid, size/growable markers |

---

## Fallback behavior

If `VisualStateFactory.isSupported()` returns false or `createOrUpdate()`
returns null, the GUI falls back to a plain `TextArea` showing the text
rendering from `StructureRenderer`.  This ensures every structure is
displayable even without a custom visual pane.

The same fallback applies in Compare mode's `ComparisonCardPane`.

---

## Current architecture vs future direction

### Current
Snapshots are formatted strings produced by `core.*.snapshot()`.
The GUI parses them through `SnapshotParser` → `StateModelParser`.

### Future
Core structures could provide structured state objects directly
(e.g. a `StructureState` interface), eliminating the string round-trip.
The `VisualState` sealed hierarchy is designed to make this transition
incremental — new state providers can feed `VisualState` records directly
without changing any visual pane code.

### Animation hooks
The sealed `VisualState` + `VisualPaneCache` architecture makes it
straightforward to add before/after diffing and animated transitions:
compare the old `VisualState` with the new one, compute a delta, and
animate the relevant pane nodes.
