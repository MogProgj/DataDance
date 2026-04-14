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

### Phase 3B — Graph Core + Algorithm Lab MVP
- Graph.java — directed/undirected, weighted/unweighted adjacency-list model
- AlgorithmFrame.java — per-step snapshot record (13 fields, 11 AlgorithmType values)
- 11 algorithm runners: BFS, DFS, Dijkstra, Bellman-Ford, Topo Sort, A*,
  Prim, Kruskal, SCC (Kosaraju), Bridges, Articulation Points
- PlaybackController.java — step/play/pause/reset/jumpTo playback over frames
- GraphPresets.java — built-in sample graphs (directed, undirected, weighted, DAG)
- GraphVisualPane.java — force-directed Canvas renderer with node/edge colouring,
  interactive edit mode, bridge/SCC/AP overlays
- AlgorithmLabController.java — sixth GUI page with full algorithm simulation,
  compare mode, and scenario save/load
- GraphAlgorithmCatalog — typed metadata + centralized dispatch for all 11 algorithms
- NavigationPage updated to six pages

### Consolidation — Visual-First Architecture
- UiComponents.java — shared static UI factory methods extracted from
  MainWindowController (styledLabel, card, settingsCard, buttonRow, etc.)
- VisualStateHost.java — reusable StackPane that encapsulates the
  visual-or-text-fallback rendering pattern
- MainWindowController refactored: ~80 lines of private helpers removed,
  Explore rendering delegated to VisualStateHost
- ComparisonCardPane refactored: 3 fields replaced by single VisualStateHost,
  2 private methods eliminated

### Phase 5A — Product Surface Upgrade
- **Compare Intelligence**: Per-implementation execution timing (nanosecond
  precision), ComparisonAnalysis model with divergence detection
  (STATUS_MISMATCH, VALUE_MISMATCH, STATE_DIVERGENCE), fastest/slowest
  annotation, and three-state verdict (MATCHING / DIVERGENT / PARTIAL_FAIL)
- **Learn Page**: Search and category filtering, richer cards showing
  behavior descriptions and learning notes from registry metadata
- **Settings Persistence**: AppSettings backed by java.util.prefs.Preferences —
  compact mode and high-density layout applied as live root style classes
- **Activity Enrichment**: Category-based filtering, clear log, export button
- **Export Flows**: ExportHelper for Compare history and Activity feed in
  both JSON and plain-text/markdown formats via FileChooser dialogs
- CSS additions: divergent/fastest card borders, timing labels, learn
  search bar, activity category badges, compact-mode and high-density
  root-level style effects

### Phase 5B — Teaching Surface Upgrade
- **Learn 2.0**: ComplexityMatrix model — scannable complexity-comparison
  table per structure family (union of operations across implementations).
  Richer Learn cards with complexity matrix GridPane, "Open in Explore" and
  "Compare Implementations" quick-action buttons.
- **Settings 2.0**: Four new AppSettings properties (defaultPlaybackSpeed,
  autoFitGraph, showAlgorithmTracker, trackerExpanded) with Preferences
  persistence. Reorganised Settings page: "Motion & Layout", "Trace &
  Learning", "Algorithm Lab", "Reset & About" cards with Restore Defaults
  button.
- **Algorithm Lab Telemetry**: AlgorithmTelemetry record (phase, typed
  metrics, titled sections, events) added as 14th field to AlgorithmFrame.
  AlgorithmTrackerPane left-panel widget renders telemetry or falls back to
  generic frame data. Tracker visibility bound to AppSettings.
- Test coverage: 1000 tests, 0 failures.

### Phase 5C — Algorithm Mechanics Upgrade
- **Real Per-Step Telemetry**: All 11 graph algorithm runners now emit
  meaningful AlgorithmTelemetry on every frame via TelemetryBuilder —
  phase labels (Initialization, Extract-Min, Relax, Complete, etc.),
  typed metrics (distances, weights, component counts), titled sections
  (frontier contents, MST edges), and descriptive events.
- **Tracker Pane Upgrade**: AlgorithmTrackerPane supports configurable
  default expansion state via constructor parameter, wired to AppSettings.
- **Settings Wiring**: Playback speed slider initialised from
  AppSettings.defaultPlaybackSpeed; auto-fit-graph triggers after preset
  selection, builder changes, and scenario loads; tracker expansion
  respects settings.
- **Learn Page Deepening**: Implementation descriptions shown on Learn
  cards, "Comparable" badge for structures with 2+ implementations,
  complexity matrix header changed to "Time Complexity".
- **Controller Cleanup**: Extracted `resetPlaybackControls()` helper,
  eliminating duplicated 7-line playback reset blocks across 4 methods
  in AlgorithmLabController.
- **Test Coverage**: TelemetryBuilderTest (10 tests), RunnerTelemetryTest
  (13 tests verifying all 11 runners emit non-null telemetry with correct
  Init/Complete phases).

### Phase 6A — Ordered Tree Structures
- **Core structures**: BinarySearchTree, AVLTree in `structlab.core.tree`
  — insert, contains, remove, min, max, height, inorder/preorder/postorder
  traversals, Traceable interface, BST-property invariant.
- **AVL rotations**: All four rotation types (Left, Right, Left-Right,
  Right-Left) with `lastRotation()` reporting.
- **Trace wrappers**: TracedBinarySearchTree, TracedAVLTree — full
  before/after snapshots, invariant checks, rotation-aware explanations.
- **Visual state**: OrderedTreeStateModel (pre-order parenthesised tree
  format), OrderedTreeVisualPane, TreeCanvas.renderOrderedTree() with
  inorder-offset layout algorithm.
- **Runtime adapter**: TreeRuntimeAdapter — 8 operations with alias
  support (add/delete/search/find).
- **Registry**: struct-tree + impl-bst + impl-avl seeded; tree canonical
  operation family (8 ops) in CanonicalOperationRegistry.
- **Compare support**: Full — BST vs AVL side-by-side comparison reveals
  shape differences and rotation behaviour.

### Phase 6B — Release-Ready Productization
- **GUI-first launcher**: StructLabLauncher entry point defaults to GUI;
  `--terminal` / `-t` for REPL, `--help` / `-h` for usage. Non-Application
  class avoids JavaFX shaded-JAR module-path check.
- **Release artifacts**: Run scripts (run-gui.bat/sh, run-terminal.bat/sh)
  bundled in GitHub Release alongside the uber-JAR.
- **First-run onboarding**: Welcome overlay on first launch with page
  descriptions and a suggested first action. Persisted via AppSettings
  `onboardingDismissed` property; reopenable from Settings.
- **Empty-state polish**: Improved placeholder text across Explore,
  Compare, Learn, Activity, and Settings pages — actionable guidance
  instead of generic placeholders.
- **Learn tab upgrade**: Three-tab structure (Structures / Algorithms /
  How to Use) with segmented toggle bar. Algorithms tab shows all 11
  graph algorithms grouped by category with metadata cards. How to Use
  tab provides a guided tour of every page.
- **About/help surfaces**: Expanded Settings About card with algorithm
  count and "Reopen Getting Started" button.
- **Tests**: StructLabLauncherTest (9 tests), AppSettingsTest updated
  for onboarding property.

---

## Current state

- 8 structure families, 19 implementations
- 15 visual panes covering all families + GraphVisualPane
- Full Explore and Compare modes with visual rendering and compare intelligence
- Algorithm Lab with 11 graph algorithms, compare mode, and scenario save/load
- Six-page GUI shell (Explore, Compare, Learn, Activity, Settings, Algorithm Lab)
- GUI-first launcher: double-click the JAR to open the GUI; `--terminal` for REPL
- First-run onboarding overlay with page tour and suggested first steps
- Learn page with three tabs: Structures (search/filter/cards), Algorithms
  (11 graph algorithms grouped by category), and How to Use (guided page tour)
- Improved empty-state text across all pages with actionable guidance
- Settings with expanded About card, algorithm count, and "Reopen Getting Started"
- Settings persisted via Preferences with live compact/high-density effects,
  Algorithm Lab preferences, and Restore Defaults
- Activity page with category filter, clear, and export
- Compare and Activity export (JSON + text) via FileChooser
- Algorithm Lab tracker pane with structured per-step telemetry display
  (phase, metrics, sections, events) for all 11 runners
- Run scripts (bat/sh) for GUI and terminal modes, bundled in releases
- 1000+ tests, 0 failures
- Clean CI with Xvfb and coverage reporting
- Structured visual state architecture (VisualState sealed hierarchy)
- Reusable visual primitives (UiComponents, VisualStateHost)

---

## Next phases

### Phase 3A — Animation and Transition System
Add animated transitions between visual states.  The VisualState sealed
hierarchy and VisualPaneCache already provide the hooks:
- Diff old vs new VisualState
- Animate element additions, removals, swaps
- Configurable speed / step-through mode

### Phase 3A — Animation and Transition System
Add animated transitions between visual states.  The VisualState sealed
hierarchy and VisualPaneCache already provide the hooks:
- Diff old vs new VisualState
- Animate element additions, removals, swaps
- Configurable speed / step-through mode

### Ongoing
- Additional structure families (Trie, Red-Black Tree)
- Performance experiment mode
- Animated visual transitions
- Complexity-table expansion in Learn cards
