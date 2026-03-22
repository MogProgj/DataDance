# StructLab Roadmap

StructLab is being developed as an implementation-first data structure laboratory
that will grow into an interactive simulator. The project begins with core data
structure implementations and gradually adds tracing, visualization, metadata,
interactivity, comparison features, and algorithm demonstrations.

---

## Phase 0 — Foundation and Repo Setup

### Goal
Create a clean base that can grow into a simulator later.

### What Happens Here
- Initialize repo
- Add README, roadmap, architecture notes, `.gitignore`
- Create package structure
- Define naming conventions
- Define project principles
- Define versioning direction

### Outputs
- Clean repository scaffold
- Docs that explain the vision
- Empty but organized folders for `core`, `trace`, `render`, `registry`, `simulator`

### Why This Phase Matters
This prevents the project from becoming disorganized. A well-structured scaffold
means every future change has a clear place to live.

---

## Phase 1 — Core Data Structure Engine

### Goal
Implement the first real structures from scratch.

### Structures to Build First
- Fixed array
- Dynamic array
- Stack on dynamic array
- Stack on linked list
- Queue on circular array
- Queue on linked list
- Queue on two stacks

### What Each Structure Must Support
- Basic operations
- Internal state exposure
- Invariant checking
- Simple string/state snapshot export

### Outputs
- Working implementations
- No GUI yet
- No simulator shell yet
- Solid core code

### Why This Phase Matters
Without this, the simulator has nothing real to simulate. Every later phase
depends on the correctness and observability of the core engine.

---

## Phase 2 — Trace and Explanation Layer

### Goal
Make operations observable and understandable.

### What Happens Here
Every operation produces a trace step. Each trace step captures:
- Operation name
- Input
- Before state
- After state
- Explanation
- Invariant result
- Optional complexity note

### Outputs
- Reusable trace model
- Human-readable operation logs
- Consistent state snapshots across structures

### Why This Phase Matters
This is where the project starts teaching instead of merely functioning. The trace
layer is what separates a data structure library from a data structure laboratory.

---

## Phase 3 — Console Rendering Layer

### Goal
Visually present state changes in a lightweight way.

### What Happens Here
- Build ASCII/text renderers for arrays, stacks, queues, and linked structures
- Render current state and transition after each operation
- Show markers like `top`, `front`, `rear`, `head`, `tail`, `size`, `capacity`

### Outputs
- Terminal-based visualizations
- Readable simulation output
- Clean before/after views

### Why This Phase Matters
This gives the project visual explanatory power without GUI complexity. Anyone
can run it in a terminal and immediately see what is happening inside each structure.

---

## Phase 4 — Data Structure Registry and Metadata System

### Goal
Build the searchable knowledge layer behind the future simulator.

### What Happens Here
For each data structure, define metadata including:
- Name
- Category
- Keywords
- Description
- Behavior
- Operations
- Invariants
- Implementations
- Combinations
- Related structures
- Learning notes

### Outputs
- Registry of structures
- Searchable tags and keywords
- Normalized metadata model

### Why This Phase Matters
This powers the future search and detail panels. Without a structured registry,
the simulator is just a collection of disconnected demos.

---

## Phase 5 — Terminal Interactive Simulator

### Goal
Turn the engine into an interactive exploration tool.

### What Happens Here
Users can:
- Search or choose a structure
- Inspect its description
- View supported operations
- Select an implementation
- Execute operations
- View trace and rendered state

### Example Flow
1. Search `"FIFO"`
2. Choose queue
3. Pick circular array, linked list, or two stacks
4. Run `enqueue` / `dequeue`
5. Watch the state update with trace and ASCII rendering

### Outputs
- First real simulator
- Terminal-first product version
- Practical interactive learning environment

### Why This Phase Matters
This is the first full expression of the project vision. Every previous phase
was building toward this point.

---

## Phase 6 — Broader Data Structure Family

### Goal
Expand beyond the initial set.

### Structures to Add
- Singly linked list
- Doubly linked list
- Circular linked list
- Deque on circular array
- Deque on doubly linked list
- Binary heap
- Priority queue on heap
- Priority queue on sorted array
- Priority queue on unsorted array
- Hash table with separate chaining
- Hash table with open addressing
- Set on hash table
- Disjoint set / union-find
- Binary search tree
- AVL tree
- Red-black tree
- Trie
- Graph via adjacency list
- Graph via adjacency matrix

### Outputs
- Richer registry
- Larger simulator catalog
- Deeper implementation comparisons

### Why This Phase Matters
The project grows into a much fuller conceptual map. The initial structures cover
the fundamentals; this phase covers the broader landscape.

---

## Phase 7 — Comparison Mode

### Goal
Compare multiple implementations of the same ADT side by side.

### What Happens Here
Users can run the same operation sequence on different implementations and compare:
- Internal state
- Trace steps
- Invariants
- Cost intuition
- Structural differences

### Examples
- Queue via linked list vs. circular array vs. two stacks
- Stack via array vs. linked list
- Priority queue via heap vs. sorted array vs. unsorted array

### Outputs
- Side-by-side comparison engine
- Stronger learning value
- Deeper intuition for tradeoffs

### Why This Phase Matters
This is where design understanding becomes much stronger. Seeing two or three
implementations of the same ADT evolve in parallel under identical operations
makes the tradeoffs concrete and memorable.

---

## Phase 8 — Graphical Simulator Layer

### Goal
Add an optional GUI after the architecture is stable.

### Suggested Technology
- JavaFX

### What Happens Here
- Search bar
- Structure browser
- Detail panel
- Implementation selector
- Operations panel
- Trace panel
- Visual state panel
- Console mode remains available

### Outputs
- Visual simulator app
- Cleaner UX
- More engaging demonstrations

### Why This Phase Matters
This adds polish without building the project backward. Because the console
simulator already exists, the GUI is a presentation layer over a working engine,
not a load-bearing wall.

---

## Phase 9 — Algorithm Demonstrations on Top of Structures

### Goal
Show data structures in actual algorithmic workflows.

### Examples
- DFS using stack
- BFS using queue
- Expression evaluation with stack
- Undo/redo with stacks
- Task scheduling with priority queue
- Autocomplete with trie
- Connectivity with union-find

### Outputs
- Algorithm modules
- Structure-to-algorithm bridge
- Practical demonstrations

### Why This Phase Matters
This connects abstract structures to real uses. It answers the question every
student eventually asks: when would I actually need this?

---

## Phase 10 — Polish, Testing, and Educational Refinement

### Goal
Make the project stable, durable, and presentation-worthy.

### What Happens Here
- Improve tests
- Add invariant stress tests
- Improve docs
- Add example scenarios
- Add curated learning paths
- Refine naming and structure
- Possibly add exportable trace snapshots

### Outputs
- Portfolio-quality repository
- Stable simulator
- Useful study tool
- Expandable educational system

### Why This Phase Matters
This turns the project into a serious long-term body of work. A well-tested,
well-documented simulator with curated learning paths has lasting value beyond
any individual course or semester.

---

## Recommended Build Order

1. Repo and docs
2. First core structures
3. Trace system
4. Console rendering
5. Metadata registry
6. Terminal simulator
7. More structures
8. Comparison mode
9. GUI
10. Algorithms and polish

---

## Version Checkpoints

| Version | Milestone |
|---------|-----------|
| 0.1 | Repo scaffold and docs |
| 0.2 | Core arrays, stacks, queues |
| 0.3 | Trace + ASCII rendering |
| 0.4 | Registry + searchable structure info |
| 0.5 | Terminal interactive simulator |
| 0.6 | More structures and combinations |
| 0.7 | Comparison mode |
| 0.8 | JavaFX graphical simulator |
| 1.0 | Full StructLab simulator with educational demos |
