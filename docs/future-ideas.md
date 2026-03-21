# Future Ideas

This file collects directions the project could grow after the initial phases are
complete.  Nothing here is committed — it is a living list.

---

## Additional structures

- Sorted-array priority queue
- Unsorted-array priority queue
- Skip list
- Trie
- Binary search tree (unbalanced)
- AVL tree
- Red-black tree (overview only — implementation is complex)
- Graph as adjacency list
- Graph as adjacency matrix

---

## Algorithm demos

Using only the structures built in this project:

- Depth-first search (stack)
- Breadth-first search (queue)
- Dijkstra's algorithm (priority queue)
- Topological sort (stack or queue)
- Huffman encoding (priority queue)
- Expression evaluation and conversion (stacks)
- Sliding window maximum (deque)

---

## Comparison mode ideas

- Run the same operation sequence on two or more implementations simultaneously
- Print aligned before/after state snapshots side by side
- Highlight which fields differ between implementations
- Show per-operation cost notes in a comparison table
- Export comparison output to a text file for offline review

---

## Tracing and export

- Exportable trace log (plain text or JSON) for offline study
- Step-through mode: pause after each operation and wait for user input
- Replay mode: replay a saved trace from a file

---

## Performance experiments

- Time the same operation sequence on multiple implementations
- Visualise amortised vs worst-case costs empirically
- Measure memory usage across implementations
- Compare growth curves for resize-heavy workloads

---

## JavaFX visualiser (Phase 6)

- Structure selector drop-down
- Implementation selector
- Operation input panel (type an operation, press Run)
- Trace panel showing step-by-step output
- Visual state panel showing the backing storage graphically
- Side-by-side comparison view

---

## Educational extensions

- Annotated source: inline comments explaining every non-obvious line
- Complexity cheat sheet auto-generated from metadata on each operation
- Invariant violation intentionally triggered to show what breaks
- "What if" mode: run an operation that violates an invariant and observe the result
