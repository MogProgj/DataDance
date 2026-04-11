# Algorithm Simulation — Specification (Draft)

> Status: **draft** — captures requirements for the first-wave algorithms
> (BFS, DFS, Dijkstra).  No code exists yet.  This document is the
> companion to [future-algorithm-lab.md](future-algorithm-lab.md).

---

## 1. Graph settings & configuration

### 1.1 Graph properties

| Setting | Options | Default |
|---|---|---|
| Direction | Undirected · Directed | Undirected |
| Weights | Unweighted · Weighted | Unweighted |
| Connectivity | Connected · Disconnected | Connected |
| Cycles | Cyclic allowed · Acyclic (DAG) | Cyclic allowed |
| Representation | Adjacency list · Adjacency matrix | Adjacency list |

### 1.2 Size & density

| Setting | Range | Default |
|---|---|---|
| Node count | 4 – 30 | 8 |
| Edge density | Sparse (≈ E = 1.2 V) · Medium (≈ E = 2 V) · Dense (≈ E = V²/3) | Medium |
| Edge weight range (weighted only) | 1 – 99 | 1 – 20 |

### 1.3 Graph generation

| Mode | Description |
|---|---|
| **Auto-generate** | Random graph satisfying the above constraints.  Seed is displayed so the user can reproduce it. |
| **Manual** | Click-to-add nodes, drag-to-add edges, type weight on edge.  Good for classroom demos. |
| **Random seed** | Integer seed field; same seed + same settings = identical graph. |

---

## 2. Simulation controls

### 2.1 Pre-run configuration

| Control | Description |
|---|---|
| Source node | Click a node or type its label.  Required for all three first-wave algorithms. |
| Target node | Optional.  When set, simulation stops early on reaching target (BFS, Dijkstra). |

### 2.2 Playback controls

| Control | Behaviour |
|---|---|
| **Step forward** | Advance exactly one algorithm step (see §2.3 for step definition). |
| **Step back** | Rewind one step (replay from snapshot history, not true undo). |
| **Play / Pause** | Auto-advance at the current speed; pause freezes the display. |
| **Speed slider** | 0.25× – 4× (logarithmic).  Default 1×. |
| **Reset** | Return to step 0 (graph + source selected, nothing visited). |
| **Replay** | Equivalent to Reset → Play. |

### 2.3 Step definition per algorithm

| Algorithm | One step = |
|---|---|
| BFS | Dequeue one node from the frontier queue; enqueue all unvisited neighbours. |
| DFS | Pop one node from the frontier stack; push all unvisited neighbours. |
| Dijkstra | Extract-min from the priority queue; relax all outgoing edges of that node. |

---

## 3. Visual encoding

### 3.1 Node colours

| State | Colour | Meaning |
|---|---|---|
| Unvisited | Grey | Not yet discovered. |
| In frontier | Amber | Discovered but not yet processed. |
| Current | Blue | Being processed this step. |
| Visited | Green | Fully processed. |
| Target (when set) | Red outline | Destination node. |

### 3.2 Edge colours

| State | Colour |
|---|---|
| Unexplored | Light grey |
| Tree edge (used by algorithm) | Dark blue |
| Relaxed (Dijkstra, weight updated) | Orange flash → Dark blue |
| Cross / back edge (DFS) | Dashed grey |

### 3.3 Overlay data

| Algorithm | Overlay per node |
|---|---|
| BFS | Distance (hop count) from source. |
| DFS | Discovery time / finish time. |
| Dijkstra | Tentative distance from source; parent pointer arrow. |

---

## 4. Algorithm-specific constraints & correctness

### 4.1 BFS

- Guarantees shortest path **only on unweighted** graphs.
- If graph is weighted, UI shows a warning: *"BFS does not account
  for weights — distances shown are hop counts, not weighted distances."*
- Works on directed and undirected graphs.

### 4.2 DFS

- Does **not** guarantee shortest path; UI never shows "shortest path"
  label.
- Useful for: connectivity, cycle detection, topological pre-order.
- Tie-breaking: neighbours processed in label-ascending order (left to
  right visually) unless randomised tie-breaking is toggled on.

### 4.3 Dijkstra

- **Requires non-negative edge weights.**  If user enables weights and
  enters a negative value, the edge input is rejected with an inline
  error.
- If graph is unweighted, Dijkstra behaves identically to BFS (all
  weights = 1).  UI may note: *"All weights are 1 — Dijkstra reduces
  to BFS."*
- Uses a binary min-heap internally (the same `BinaryHeap` already in
  StructLab's core layer).  Optionally display the heap state in a
  collapsible side panel using the existing `HeapVisualPane`.

---

## 5. Compare-mode scenarios

Compare mode places two algorithm cards side-by-side on the **same
graph** (same node positions, same edges, same source node).  Both
advance in lock-step (one step button advances both).

### 5.1 Planned comparison pairs

| Left | Right | Insight |
|---|---|---|
| BFS | DFS | Frontier expansion: level-order vs depth-first. |
| Dijkstra | BFS | Weighted vs unweighted shortest path on a weighted graph. |
| Prim | Kruskal | (Wave 3) Two MST strategies on the same graph. |
| Adjacency list | Adjacency matrix | Same algorithm, different representations — performance counters. |

### 5.2 Tie-breaking comparison

A powerful teaching tool: run the **same algorithm twice** with different
tie-breaking rules (alphabetical vs reverse-alphabetical vs random).
Demonstrates that multiple valid traversal orders exist.

---

## 6. Snapshot format (strawman)

Extending the existing snapshot string convention:

```
type=BFS
source=A
step=4
frontier=[C, D]
visited={A, B, E, F}
current=B
distances={A:0, B:1, E:1, F:2, C:?, D:?}
parent={B:A, E:A, F:B}
edges=A-B,A-E,B-F,B-C,E-D
```

The `GraphStateModel` record mirrors these fields:

```java
public record GraphStateModel(
    String algorithmName,
    String sourceNode,
    int step,
    List<String> frontier,
    Set<String> visited,
    String currentNode,
    Map<String, Integer> distances,
    Map<String, String> parentMap,
    List<Edge> edges,
    List<Node> nodes
) implements VisualState { ... }
```

---

## 7. Non-goals (for first wave)

- Animated edge traversal (smooth node-to-node movement) — step-based
  highlighting is sufficient initially.
- Weighted graph auto-layout that factors edge weights into distances.
- Algorithm code panel (showing pseudocode with highlighted current line)
  — valuable, but deferred.
- Export to image / GIF.

---

## 8. Product fit

Algorithm simulation is not a separate product — it is StructLab's
**second act**.  The same student who explores a BinaryHeap today should
be able to open an Algorithm Lab tab tomorrow and watch Dijkstra use
that heap in real time.  Shared visual language, shared controls, shared
compare-mode muscle memory.

This spec ensures the algorithm lab is designed with the same values:
**transparency** (every step visible), **comparison** (side-by-side
insight), and **correctness** (constraints enforced, never misleading).
