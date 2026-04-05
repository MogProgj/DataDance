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

> **Phase 1 — Core Data Structure Engine (complete)**
>
> All Phase 1 structures are implemented with tests, demos, invariant checking,
> and snapshot support.  The project is ready to move into Phase 2 (Trace layer).

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

Requires Java 17+ and Gradle.

```bash
gradle build       # compile and run all tests
gradle test         # run tests only
```

To run a demo:

```bash
gradle run -PmainClass=structlab.demo.ArrayStackDemo
```

---

## Contributing

See [`CONTRIBUTING.md`](CONTRIBUTING.md).

---

## License

MIT — see [`LICENSE`](LICENSE).