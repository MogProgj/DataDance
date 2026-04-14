# Compare Workspace

The Compare page lets users run the same operations on multiple
implementations of the same abstract data type, side by side.

---

## Layout

```
┌────────────────────────────────────────────────────────┐
│  Structure selector       [Operation bar] [Execute]    │
├────────────────────────────────────────────────────────┤
│  ComparisonSummaryPane                                 │
│  "Stack  │  2 implementations  │  3 operations  │ OK" │
├────────────────────────────────────────────────────────┤
│ ┌──────────────────┐  ┌──────────────────┐             │
│ │ ComparisonCard   │  │ ComparisonCard   │             │
│ │ "Array Stack"    │  │ "Linked Stack"   │             │
│ │ [OK]             │  │ [OK]             │             │
│ │ → 30             │  │ → 30             │             │
│ │ ┌──────────┐     │  │ ┌──────────┐     │             │
│ │ │ visual   │     │  │ │ visual   │     │             │
│ │ │ state    │     │  │ │ state    │     │             │
│ │ └──────────┘     │  │ └──────────┘     │             │
│ │ ▸ Show trace     │  │ ▸ Show trace     │             │
│ └──────────────────┘  └──────────────────┘             │
└────────────────────────────────────────────────────────┘
```

---

## Comparison cards

Each `ComparisonCardPane` represents one implementation.

### Card elements
- **Header**: Implementation name + status badge (IDLE / READY / OK / FAIL)
- **Metrics**: Returned value, step count
- **State host**: Visual pane (via per-card `VisualPaneCache`) or text fallback
- **Trace section**: Collapsible trace details for the last operation

### Per-card visual pane caching
Each card owns its own `VisualPaneCache` instance.  This is separate
from the static cache in `VisualStateFactory` (used by Explore mode)
because Compare needs independent rendering per implementation.

---

## Supported comparison families

All 7 structure families and all 17 implementations are supported in
Compare mode.  The operation bar dynamically shows the correct operations
for the selected family.

### Canonical operations by family

| Family | Operations |
|--------|-----------|
| Array | append, insert, removeat, get, set |
| List | addfirst, addlast, removefirst, removelast, get, contains |
| Stack | (push, pop, peek — via runtime ops) |
| Queue | (enqueue, dequeue, peek — via runtime ops) |
| Deque | addfirst, addlast, removefirst, removelast, peekfirst, peeklast |
| Heap | insert, extractmin, peek |
| Hash | put, get, remove, containskey |

The `CanonicalOperationRegistry` normalizes operation names across
implementations so the Compare UI can present a unified interface.

---

## Operation execution flow

1. User selects a structure family and implementations open as cards
2. User enters an operation and arguments in the shared bar
3. `ComparisonSession.executeAll()` runs the operation on every active runtime
4. Each `ComparisonCardPane` receives its result via `updateResult()`
5. The card parses the raw snapshot → `VisualState` → updates its visual pane
6. `ComparisonSummaryPane` updates with overall status (ALL OK / PARTIAL)

---

## Comparison summary

`ComparisonSummaryPane` is a compact header showing:
- Structure family name
- Number of active implementations
- Total operations executed
- Overall status (READY / ALL OK / PARTIAL FAIL)

---

## Drill-down and trace role

Each card's trace section shows the full `TraceStep` output for the last
operation: before-state, after-state, invariant result, complexity note,
and human explanation.  This helps users understand *why* two implementations
produced different internal states for the same operation.

---

## ComparisonAnalysis and intelligence (Phase 5A)

`ComparisonAnalysis` wraps a `ComparisonOperationResult` and provides:

- **Divergence detection** — three types: `STATUS_MISMATCH` (one passed,
  one failed), `VALUE_MISMATCH` (different returned values),
  `STATE_DIVERGENCE` (different state-after snapshots)
- **Overall verdict** — `MATCHING`, `DIVERGENT`, or `PARTIAL_FAIL`
- **Timing analysis** — each entry carries `durationNanos`; analysis
  identifies fastest/slowest and produces a human summary
- **UI effects** — divergent cards get amber borders, fastest cards
  get green borders, timing labels shown on each card, history items
  use verdict icons (✔/↔/⚠)

### Export

Compare history can be exported via `ExportHelper.compareHistoryToText()`
or `compareHistoryToJson()` through a FileChooser dialog.

---

## Future enhancements

- Animated step-through mode
