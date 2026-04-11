# Roadmap

Current state of the project and planned future work.

---

## Completed phases

### Phase 0 — Foundation
Repository scaffold, package structure, principles, naming conventions.

### Phase 1 — Core Data Structure Engine
FixedArray, DynamicArray, ArrayStack, LinkedStack, CircularArrayQueue,
LinkedQueue, TwoStackQueue — all with invariant checking and snapshot
export.

### Phase 2 — Trace and Explanation Layer
Traced wrappers for all structures.  TraceStep records with before/after
state, invariant results, complexity notes, human explanations.

### Phase 3 — Console Rendering
ASCII/text renderers for all structure families via SnapshotParser and
StructureRenderer.

### Phase 4 — Registry and Metadata System
StructureMetadata, ImplementationMetadata, searchable registry, RegistrySeeder.

### Phase 5 — Terminal Interactive Simulator
Full REPL shell with command parsing, routing, discovery mode, session
mode, operation execution, trace display, and terminal formatting.

### Phase 6 — Broader Structure Families
Added: SinglyLinkedList, DoublyLinkedList, ArrayDequeCustom, LinkedDeque,
BinaryHeap, HeapPriorityQueue, HashTableChaining, HashTableOpenAddressing
(linear/quadratic/double), HashSetCustom.  All with traced wrappers,
renderers, and registry entries.

### Phase 7 — JavaFX GUI Shell
StructLabFxApp, MainWindowController, five-page navigation, FXML layout,
dark theme CSS, service layer facade.

### Phase 2A — Stack/Queue Visual Panes
StackVisualPane, QueueVisualPane, CircularQueueVisualPane.

### Phase 2B — Compare Workspace
ComparisonSession, ComparisonCardPane, ComparisonSummaryPane,
CanonicalOperationRegistry, full side-by-side comparison with visual
state panes per card.

### Phase 2C — Visual Pane Families
- 2C1: Heap visuals (HeapVisualPane, PriorityQueueVisualPane)
- 2C2: Hash visuals (HashChainingVisualPane, HashOpenAddressingVisualPane, HashSetVisualPane)
- 2C3: Linked list and deque visuals (SinglyLinkedListVisualPane,
  DoublyLinkedListVisualPane, ArrayDequeVisualPane, LinkedDequeVisualPane)
- 2C4: Array visuals (FixedArrayVisualPane, DynamicArrayVisualPane)

### CI hardening
Xvfb for headless JavaFX, @Timeout(10) on all visual tests,
JavaFxToolkitExtension with bounded startup timeout.

### Phase 2D — Structured UI State Models + Documentation Overhaul
- VisualState sealed interface for all 13 state models
- Unified StateModelParser.parse() entry point
- VisualPaneCache to eliminate duplicated dispatch logic
- Refactored VisualStateFactory and ComparisonCardPane
- Full documentation reorganization
- Future algorithm lab planning docs

---

## Current state

- 7 structure families, 17 implementations
- 14 visual panes covering all families
- Full Explore and Compare modes with visual rendering
- 741 tests, 0 failures
- Clean CI with Xvfb and coverage reporting
- Structured visual state architecture (VisualState sealed hierarchy)

---

## Next phases

### Phase 3A — Animation and Transition System
Add animated transitions between visual states.  The VisualState sealed
hierarchy and VisualPaneCache already provide the hooks:
- Diff old vs new VisualState
- Animate element additions, removals, swaps
- Configurable speed / step-through mode

### Phase 3B — Enhanced Compare Intelligence
- Diff highlighting between comparison cards
- Performance timing per implementation
- Divergence detection and annotation
- Export comparison results

### Phase 3C — Learn Page Content
Populate the Learn page with structured reference content generated
from the registry, including complexity tables, invariant descriptions,
and implementation comparison summaries.

### Phase 4 — Algorithm Lab (Future)
Algorithm simulation workspace for graph traversal and pathfinding
algorithms.  See [future-algorithm-lab.md](future-algorithm-lab.md) and
[algorithm-simulation-spec.md](algorithm-simulation-spec.md).

First wave: BFS, DFS, Dijkstra.
Future: A*, Bellman-Ford, Prim, Kruskal, Topological Sort, Floyd-Warshall.

### Ongoing
- Additional structure families (BST, AVL, Trie, Graph)
- Performance experiment mode
- Trace export (JSON/text)
- Activity page enrichment
- Settings page implementation
