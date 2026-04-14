# Future: Algorithm Lab

> Status: **implemented** — All 11 graph algorithms are working in the
> Algorithm Lab GUI page with Graph.java, playback controls,
> GraphVisualPane, compare mode, and scenario save/load.
> Per-step telemetry (AlgorithmTelemetry) is emitted by every runner
> and displayed in the AlgorithmTrackerPane sidebar widget.
> This document captures the full product vision and remaining ideas.

---

## Why algorithms belong in StructLab

StructLab already lets you **see** data structures change step-by-step and
**compare** two implementations side-by-side.  Algorithm simulation is the
natural next frontier:

| StructLab today | Algorithm Lab tomorrow |
|---|---|
| Insert / remove / peek on a structure | Run BFS / Dijkstra / Prim on a graph |
| Snapshot after each operation | Snapshot after each algorithm step |
| Compare two structures (e.g. ArrayStack vs LinkedStack) | Compare two algorithms (e.g. BFS vs DFS) |
| Visual pane per structure type | Visual pane for graph + overlay |

The same **trace → snapshot → visual state → pane** pipeline that powers
structure exploration can power algorithm simulation with minimal new
plumbing.  The key new ingredient is a **graph model** and a set of
**algorithm drivers** that emit snapshots at each logical step.

---

## Product shape

### Explore mode (single algorithm)

1. User picks an algorithm (e.g. Dijkstra).
2. User configures a graph (size, density, weights, directed/undirected).
3. User selects source (and optionally target) node.
4. Simulation runs step-by-step with full playback controls
   (play / pause / step-forward / step-back / speed slider / reset).
5. Each step highlights the current frontier, visited set, distances, and
   parent pointers on the graph visual pane.

### Compare mode (two algorithms or two configs)

Same split-card layout used for structure comparison:

- **Left card**: BFS on graph G — **Right card**: DFS on graph G.
- Both cards share the same graph, same source node, same step clock.
- User can observe frontier expansion differences in real time.

### Algorithm catalogue (first wave)

| Algorithm | Category | Key constraint |
|---|---|---|
| BFS | Traversal / shortest path (unweighted) | Unweighted graphs only for shortest path |
| DFS | Traversal | No shortest-path guarantee |
| Dijkstra | Shortest path (weighted) | Non-negative edge weights |

### Future waves

| Wave | Algorithms |
|---|---|
| 2 | Bellman-Ford, Topological Sort |
| 3 | Prim, Kruskal (MST) |
| 4 | A*, Floyd-Warshall |
| 5 | Union-Find (as supporting structure) |

---

## How it fits the existing architecture

```
┌─────────────────────────────────────────────────┐
│  AlgorithmDriver  (new)                         │
│  - produces Trace<AlgorithmSnapshot>             │
│  - one driver per algorithm                      │
└────────────────────┬────────────────────────────┘
                     │ snapshots
                     ▼
┌─────────────────────────────────────────────────┐
│  GraphStateModel  (new VisualState record)      │
│  - nodes, edges, visited set, frontier,          │
│    distances, parent map, current node           │
└────────────────────┬────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────┐
│  GraphVisualPane  (new)                          │
│  - renders nodes + edges on Canvas or Pane       │
│  - colour-codes visited / frontier / unvisited   │
│  - animates edge relaxation, queue pops, etc.    │
└─────────────────────────────────────────────────┘
```

New components slot into the existing layered architecture:

| Layer | New addition |
|---|---|
| **core** | Graph model (adjacency list + matrix), graph generators |
| **trace** | `AlgorithmDriver` interface + per-algorithm implementations |
| **render** | `GraphStateModel` record implementing `VisualState` |
| **gui.visual** | `GraphVisualPane`, layout engine (force-directed or grid) |
| **gui.pages** | `AlgorithmLabPage` in Explore and Compare shells |
| **registry** | Algorithm registry (parallel to StructureRegistry) |

---

## Open design questions

1. **Graph layout algorithm** — force-directed (flexible, organic) vs
   grid/circle (deterministic, reproducible)?  Likely configurable.
2. **Step granularity** — one snapshot per queue-pop, or finer (per
   neighbour relaxation)?  Possibly a detail-level toggle.
3. **Graph persistence** — let users save/load graphs for reproducible
   demos?
4. **Shared graph in Compare mode** — must guarantee identical layout
   coordinates on both cards so visual comparison is meaningful.
5. **Integration with existing structures** — e.g. show the internal
   priority queue used by Dijkstra as a side panel, using the existing
   HeapVisualPane.

---

## What this document is NOT

- A specification (see [algorithm-simulation-spec.md](algorithm-simulation-spec.md)).
- A timeline or commitment.
- A reason to change any existing code today.

It is a **north-star vision** that future phases can refine and decompose
into actionable work.
