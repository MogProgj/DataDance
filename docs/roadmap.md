# Roadmap

---

## Phase 0 — Foundation and repo scaffolding

### Goal
Set up the project properly before writing any implementations.

### Deliverables
- Repository initialised with clean Java project layout
- README with project vision and roadmap
- `.gitignore` for Java, IDE, and OS artefacts
- MIT licence
- `CONTRIBUTING.md`
- `docs/` folder with architecture, design principles, and future ideas
- Placeholder package structure under `src/`
- Naming and coding conventions documented

---

## Phase 1 — First working laboratory core

### Goal
Implement the first small but meaningful subset of structures from scratch.

### Structures
- Dynamic array
- Stack via dynamic array
- Stack via linked list
- Queue via circular array
- Queue via linked list
- Queue via two stacks

### Deliverables
- Implementations with no built-in Java collections
- Invariant-check methods
- Basic text renderers
- Scripted demo scenarios
- Initial unit tests

---

## Phase 2 — Rendering and state-explanation layer

### Goal
Make operations visually comprehensible through structured trace output.

### Deliverables
- ASCII-based renderers
- Trace output for each operation (before / after / cost note / invariant status)
- Resize, relink, and wraparound events shown explicitly
- Example output format:

```
Operation: push(7)
Before:    [3, 5]
After:     [3, 5, 7]
Backing:   [3, 5, 7, _, _]
Top index: 2
Invariant: OK
Cost:      O(1) amortised
```

---

## Phase 3 — Broader structure family

### Goal
Expand the structural map beyond arrays, stacks, and queues.

### Structures
- Singly linked list
- Doubly linked list
- Deque via linked list
- Deque via circular array
- Binary heap
- Priority queue via heap

### Deliverables
- Generalised interfaces
- Richer comparison demos
- Enhanced invariant and renderer systems

---

## Phase 4 — Hashing and hybrid structures

### Goal
Expose structures that are built from combinations of earlier ones.

### Structures
- Hash table with separate chaining (array + linked lists)
- Open addressing variant (optional)
- Disjoint set / union-find (optional)

### Deliverables
- Collision visualisation
- Rehash visualisation
- Bucket renderers

---

## Phase 5 — Comparison mode

### Goal
Run the same sequence of operations on multiple implementations and show aligned
results side by side.

### Deliverables
- Comparison runner
- Aligned state views
- Aligned complexity notes
- Highlighted differences

### Example
Run `enqueue(1), enqueue(2), dequeue(), enqueue(3)` on:
- Linked queue
- Circular array queue
- Two-stack queue

And compare how each internal state evolves.

---

## Phase 6 — Optional lightweight UI (JavaFX)

### Goal
Add a simple visual shell after the core logic is stable.

### Deliverables
- Structure selector
- Implementation selector
- Operation input panel
- Trace panel
- Visual state panel

### Note
The console version is sufficient for the intellectual goal.  UI is polish, not
foundation.

---

## Phase 7 — Algorithms built on top of structures

### Goal
Show why data structures matter in real algorithmic contexts.

### Possible demos
- DFS using stack
- BFS using queue
- Task scheduling using priority queue
- Browser history using deque
- Undo/redo using stacks
- Expression evaluation using stacks
- Producer–consumer queue simulation
