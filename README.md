# StructLab

A data structure laboratory for implementing, comparing, tracing, and visually
understanding core data structures through multiple underlying representations.

---

## What is this?

StructLab is an **implementation-first educational project** for understanding
data structures at a deep level.  The goal is not to memorise APIs.  The goal is
to build lasting intuition by doing four things for every structure:

1. **Abstract behaviour** — what does the ADT promise?
2. **Concrete implementation** — how is it actually realised in memory?
3. **Invariants** — what must always be true, and how do we verify it?
4. **Tradeoffs** — why would you choose one implementation over another?

This is not a LeetCode dump.  It is a conceptual atlas of data structures.

---

## Design principles

### 1. Implementation before convenience

No built-in Java collections for the structures being studied.  If you are
implementing a stack, you do not secretly wrap `java.util.Stack`.  That would be
academic tax fraud.

### 2. Visibility of internal state

Every structure must be inspectable.  Not just "push worked", but what changed,
where it changed, and whether all invariants still hold.

### 3. Multiple implementations of the same ADT

The same abstract data type should be realised in at least two ways wherever
meaningful.  That is where real understanding forms.

### 4. Traceability

Operations should be explainable in terms of before-state, the operation itself,
after-state, runtime intuition, and invariant results.

### 5. Separation of concerns

Core data-structure logic, tracing/logging, rendering/visualisation, and demo
scenarios must remain in separate layers so the project can grow without becoming
a swamp.

---

## Project status

> **Phase 3 — Console Rendering Layer (complete for arrays, stacks, queues, lists, deques, heaps)**
>
> Phases 1–3 are complete for arrays, stacks, queues, lists, deques, and
> heaps/priority queues.  Each traced structure has structure-aware ASCII
> rendering with markers, boxed cells, node chains, tree-level views,
> and before/after transition display.  Hash structures are not yet
> traced or rendered.

---

## Implemented structures

| Category | Structure | Implementation | Tests | Traced | Rendered |
|---|---|---|---|---|---|
| Array | Fixed array | `FixedArray` | yes | yes | yes |
| Array | Dynamic array | `DynamicArray` | yes | yes | yes |
| Stack | Array stack | `ArrayStack` (on DynamicArray) | yes | yes | yes |
| Stack | Linked stack | `LinkedStack` | yes | yes | yes |
| Queue | Circular array queue | `CircularArrayQueue` | yes | yes | yes |
| Queue | Linked queue | `LinkedQueue` | yes | yes | yes |
| Queue | Two-stack queue | `TwoStackQueue` | yes | yes | yes |
| List | Singly linked list | `SinglyLinkedList` | yes | yes | yes |
| List | Doubly linked list | `DoublyLinkedList` | yes | yes | yes |
| Deque | Linked deque | `LinkedDeque` | yes | yes | yes |
| Deque | Array deque | `ArrayDequeCustom` | yes | yes | yes |
| Heap | Binary heap | `BinaryHeap` (on DynamicArray) | yes | yes | yes |
| Heap | Priority queue | `HeapPriorityQueue` | yes | yes | yes |

---

## Phase 2 — Trace layer

The trace layer lives under `src/main/java/structlab/trace/` and provides:

| Class | Purpose |
|---|---|
| `TraceStep` | Immutable record capturing one traced operation |
| `TraceLog` | Ordered collection of `TraceStep` entries |
| `InvariantResult` | Enum: `PASSED`, `FAILED`, `SKIPPED` |
| `Traceable` | Interface implemented by traceable structures |
| `TracedDynamicArray` | Traced wrapper for `DynamicArray` |
| `TracedFixedArray` | Traced wrapper for `FixedArray` |
| `TracedArrayStack` | Traced wrapper for `ArrayStack` |
| `TracedLinkedStack` | Traced wrapper for `LinkedStack` |
| `TracedCircularArrayQueue` | Traced wrapper for `CircularArrayQueue` |
| `TracedLinkedQueue` | Traced wrapper for `LinkedQueue` |
| `TracedTwoStackQueue` | Traced wrapper for `TwoStackQueue` |
| `TracedSinglyLinkedList` | Traced wrapper for `SinglyLinkedList` |
| `TracedDoublyLinkedList` | Traced wrapper for `DoublyLinkedList` |
| `TracedLinkedDeque` | Traced wrapper for `LinkedDeque` |
| `TracedArrayDequeCustom` | Traced wrapper for `ArrayDequeCustom` |
| `TracedBinaryHeap` | Traced wrapper for `BinaryHeap` |
| `TracedHeapPriorityQueue` | Traced wrapper for `HeapPriorityQueue` |

Each `TraceStep` captures: structure name, implementation name, operation name,
input arguments, before-state snapshot, after-state snapshot, invariant result,
optional complexity note, and a human-readable explanation.

**Failed-operation policy:** When an operation fails due to a detectable
precondition (empty stack/queue, full fixed array), the failure is traced as a
`TraceStep` with a `FAILED:` explanation before the exception propagates.  This
makes failures visible in the trace log for educational purposes.

Run a traced demo:

```bash
mvn compile exec:java -Dexec.mainClass=structlab.demo.TracedDynamicArrayDemo
```

---

## Phase 3 — Console rendering layer

The render layer lives under `src/main/java/structlab/render/` and provides:

| Class | Purpose |
|---|---|
| `SnapshotParser` | Parses snapshot strings into structured fields |
| `StructureRenderer` | Produces ASCII art for each structure type |
| `ConsoleTraceRenderer` | Renders full trace steps with before/after views |

Rendered structures include visual markers and layout cues:

| Structure | Visual features |
|---|---|
| DynamicArray, FixedArray | Boxed cells, logical/backing arrays, index row, capacity |
| ArrayStack | Vertical stack with `<-- top` marker |
| CircularArrayQueue | Buffer with `F`/`R` markers, logical order chain |
| LinkedStack | Node chain: `top -> [30] -> [20] -> null` |
| LinkedQueue | Node chain with `front`/`rear` pointer markers |
| TwoStackQueue | Side-by-side inbox/outbox stacks, effective queue order |
| SinglyLinkedList | Node chain with `head`/`tail` markers |
| DoublyLinkedList | Bidirectional chain (`<-->`) with `head`/`tail` markers |
| LinkedDeque | Bidirectional chain with `front`/`rear` markers |
| ArrayDequeCustom | Circular buffer with `F`/`R` markers, logical order |
| BinaryHeap | Array view plus tree-level view showing parent/child layout |
| HeapPriorityQueue | Priority info with underlying heap array and tree view |

All traced demos use `ConsoleTraceRenderer` for polished terminal output.

---

## Planned architecture

```
StructLab/
├── docs/               Documentation and design records
├── src/
│   ├── main/java/structlab/
│   │   ├── core/       Pure data structure implementations
│   │   │   ├── array/
│   │   │   ├── list/
│   │   │   ├── stack/
│   │   │   ├── queue/
│   │   │   ├── deque/
│   │   │   ├── heap/
│   │   │   └── hash/
│   │   ├── trace/      Operation tracing and logging
│   │   ├── render/     ASCII / console state renderers
│   │   ├── demo/       Scripted demo scenarios
│   │   └── app/        Entry points and wiring
│   └── test/java/structlab/
├── examples/           Standalone usage examples
└── scripts/            Helper build / run scripts
```

See [`docs/architecture.md`](docs/architecture.md) for a full explanation of the
layer boundaries.

---

## Roadmap overview

| Phase | Focus |
|---|---|
| 0 | Foundation and repo setup |
| 1 | Core data structure engine (arrays, stacks, queues) |
| 2 | Trace and explanation layer |
| 3 | Console rendering layer |
| 4 | Data structure registry and metadata system |
| 5 | Terminal interactive simulator |
| 6 | Broader data structure family |
| 7 | Comparison mode (same ops on multiple implementations) |
| 8 | Graphical simulator layer (JavaFX) |
| 9 | Algorithm demonstrations on top of structures |
| 10 | Polish, testing, and educational refinement |

Full details are in [`docs/roadmap.md`](docs/roadmap.md).

For the Phase 0 working contract, see [`docs/phase-0-foundation.md`](docs/phase-0-foundation.md).

---

## Running the project

Requires Java 17+ and Maven 3.9+.

```bash
mvn compile         # compile all sources
mvn test            # compile and run all tests
```

To run a traced demo from the command line:

```bash
mvn compile exec:java -Dexec.mainClass=structlab.demo.TracedArrayStackDemo
```

To run the interactive terminal simulator:

```bash
mvn compile exec:java -Dexec.mainClass=structlab.app.StructLabApp
```

For full instructions, rules, and commands on how to play/run the simulator, please see [**`docs/how-to-play.md`**](docs/how-to-play.md).

### Simulator Commands
Once inside the simulator, try the following flow:
* `ls` - View all available data structures
* `info stack` - See available implementations and time complexities for a stack
* `play stack impl-array-stack` - Mount an array-based stack and begin simulation
* `ops` - Check supported operations on your open structure
* `push 10`, `pop`, `peek` - Apply live changes and render state
* `history`, `last`, `reset` - Inspect or reset simulation timeline
* `close` or `quit` - Exit session or simulator

Replace the class name with any demo under `structlab.demo`.  Running demos
from your IDE (right-click and Run) also works.

---

## Contributing

See [`CONTRIBUTING.md`](CONTRIBUTING.md).

---

## License

MIT — see [`LICENSE`](LICENSE).
