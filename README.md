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

> **Phase 3 — Console Rendering Layer (complete for arrays, stacks, and queues)**
>
> Phases 1 and 2 are complete for arrays, stacks, and queues.  Phase 3 adds
> a console rendering layer that turns trace output into structure-aware
> ASCII visualizations with markers (`top`, `front`, `rear`, `F`, `R`),
> boxed array cells, vertical stack views, linked-node chains, and
> side-by-side before/after transition views.  Lists, deques, heaps, and
> hash structures are not yet traced or rendered.

---

## Implemented structures

| Category | Structure | Implementation | Tests | Demo |
|---|---|---|---|---|
| Array | Fixed array | `FixedArray` | yes | yes |
| Array | Dynamic array | `DynamicArray` | yes | yes |
| Stack | Array stack | `ArrayStack` (on DynamicArray) | yes | yes |
| Stack | Linked stack | `LinkedStack` | yes | yes |
| Queue | Circular array queue | `CircularArrayQueue` | yes | yes |
| Queue | Linked queue | `LinkedQueue` | yes | yes |
| Queue | Two-stack queue | `TwoStackQueue` | yes | yes |
| List | Singly linked list | `SinglyLinkedList` | yes | yes |
| List | Doubly linked list | `DoublyLinkedList` | yes | yes |
| Deque | Linked deque | `LinkedDeque` | yes | yes |
| Deque | Array deque | `ArrayDequeCustom` | yes | yes |
| Heap | Binary heap | `BinaryHeap` (on DynamicArray) | yes | yes |
| Heap | Priority queue | `HeapPriorityQueue` | yes | yes |

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

All traced demos now use `ConsoleTraceRenderer` for polished terminal output.

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

To run a demo, use your IDE's main-class runner (e.g. right-click the demo
class in IntelliJ and choose Run), or from the command line:

```bash
mvn compile exec:java -Dexec.mainClass=structlab.demo.ArrayStackDemo
```

> **Note:** the `exec:java` command requires the `exec-maven-plugin`.  If you
> prefer not to add it, running demos directly from IntelliJ or any IDE with
> Maven support works out of the box.

---

## Contributing

See [`CONTRIBUTING.md`](CONTRIBUTING.md).

---

## License

MIT — see [`LICENSE`](LICENSE).