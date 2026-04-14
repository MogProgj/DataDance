# GUI Shell and Pages

The StructLab desktop application is a JavaFX 21 single-window shell with
six navigation pages.

---

## Shell structure

```
┌────────────────────────────────────────────────────────┐
│  Toolbar         "StructLab"            [status text]  │
├────────────────────────────────────────────────────────┤
│ ┌─────────┐ ┌──────────────────────────────────────┐   │
│ │ Sidebar │ │          Page content                │   │
│ │         │ │                                      │   │
│ │ Explore │ │                                      │   │
│ │ Compare │ │                                      │   │
│ │ Learn   │ │                                      │   │
│ │Activity │ │                                      │   │
│ │Settings │ │                                      │   │
│ │Alg. Lab │ │                                      │   │
│ └─────────┘ └──────────────────────────────────────┘   │
├────────────────────────────────────────────────────────┤
│  Status bar                                            │
└────────────────────────────────────────────────────────┘
```

- **Toolbar**: App title on the left, contextual status on the right
  (e.g. "Discovery Mode" or "Session: Stack / Array Stack")
- **Sidebar**: Navigation buttons for the six pages
- **Page content**: Swapped based on the active `NavigationPage`
- **Status bar**: Feedback messages ("Ready", "Session opened", etc.)

---

## Navigation model

`NavigationPage` is an enum with six values.  The controller swaps
the center content when a sidebar button is clicked.  Only one page
is visible at a time.  Session state is preserved when switching pages.

---

## Page responsibilities

### Explore

The primary single-session workspace.

- **Discovery panel** (left): Structure list, implementation list,
  "Open Session" button.
- **Center panel**: Structure details, visual state pane (or text
  fallback), last trace step.
- **Session panel** (right): Session info, operation list, argument
  field + Execute button, operation history, Reset/Close controls.

The visual state pane is produced by `VisualStateHost`, a reusable
StackPane component that encapsulates the visual-or-text-fallback
pattern.  It owns its own `VisualPaneCache` and delegates to
`StateModelParser` for snapshot parsing.

### Compare

Side-by-side comparison workspace.

- Structure/family selector at the top
- Comparison summary header (`ComparisonSummaryPane`)
- Grid of `ComparisonCardPane` instances — one per implementation
- Shared operation bar for executing the same operation on all cards
- Each card shows: implementation name, status badge, visual state,
  returned value, expandable trace details

See [compare-workspace.md](compare-workspace.md) for full detail.
Each `ComparisonCardPane` uses its own `VisualStateHost` instance
for visual-or-text-fallback rendering.

### Learn

Three-tab reference and guide page with a segmented toggle bar.

**Structures tab** (default):
- **Search bar**: Real-time text filter across structure names,
  categories, keywords, descriptions, behavior, and learning notes
- **Category filter**: ComboBox to narrow by structure category
- **Cards**: Each card shows name, category, keywords, description,
  behavior description, learning notes, implementation descriptions,
  and a complexity matrix table sourced from the registry
- "Comparable" badge shown for structures with 2+ implementations
- Grouped by category when unfiltered

**Algorithms tab**:
- Lists all graph algorithms from `GraphAlgorithmCatalog` grouped by
  category (Traversal, Shortest Path, MST, Connectivity)
- Each card shows display label, category badge, hint text, and
  requirements (source, target, graph type)
- "Try in Algorithm Lab" quick-action button

**How to Use tab**:
- Guided walkthrough of every page (Explore, Compare, Algorithm Lab,
  Learn, Activity, Settings) with step-by-step usage instructions

### Activity

Session history and recent actions log.

- **Category filter**: ComboBox to view only entries of a specific
  category (e.g. "SESSION", "OPERATION", "COMPARE")
- **Clear button**: Empties the activity log
- **Export button**: Saves filtered activity to JSON or text via
  FileChooser
- Shows a timeline of operations performed during the current
  application run, each with a category badge

### Settings

Application preferences persisted via `java.util.prefs.Preferences`.

- **Compact mode**: Toggle that applies the `compact-mode` root style
  class (reduced padding and margins)
- **High-density layout**: Toggle that applies the `high-density` root
  style class (tighter spacing, smaller font sizes)
- **Show raw traces** / **Motion enabled**: Additional preference toggles
- All settings take effect immediately and persist across restarts
- **Reopen Getting Started**: Reopens the first-run onboarding overlay
- **About**: Version info, structure/algorithm counts

### Algorithm Lab

Interactive graph algorithm simulation workspace.

- **Graph panel**: Force-directed Canvas rendering of nodes and edges
  with colour-coded visited/frontier/unvisited states.
- **Algorithm selector**: 11 algorithms — BFS, DFS, Dijkstra, Bellman-Ford,
  Topo Sort, A*, Prim (MST), Kruskal (MST), SCC (Kosaraju), Bridges,
  Articulation Points — driven by a typed metadata catalog.
- **Playback controls**: Step forward, step back, play/pause, speed
  slider (initialised from settings), reset.
- **Telemetry tracker**: Left-panel AlgorithmTrackerPane showing per-step
  phase, metrics, titled sections, and events for the current frame.
  Expansion state configurable via settings.
- **Auto-fit**: Graph automatically fits viewport on preset/builder/
  scenario changes when `autoFitGraph` setting is enabled.
- **Graph presets**: Built-in sample graphs (small, medium, tree, etc.).
- **Configuration**: Node count, source node, graph preset selection.

See [future-algorithm-lab.md](future-algorithm-lab.md) for the full
vision and [algorithm-simulation-spec.md](algorithm-simulation-spec.md)
for the detailed specification.

---

## FXML and CSS

- Layout: `resources/structlab/gui/main-window.fxml`
- Styles: `resources/structlab/gui/styles.css`
- Entry point: `StructLabFxApp` → `MainWindowController`

The CSS uses a dark neutral theme with monospace text areas, rounded
cards, and color-coded status badges.
