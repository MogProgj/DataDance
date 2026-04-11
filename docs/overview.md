# StructLab — Overview

StructLab is a Java-based data structure laboratory for building, tracing,
comparing, and visually understanding core data structures through multiple
concrete implementations.

---

## Who is this for?

- Students learning data structures at a deep, implementation-first level
- Self-taught developers who want to see what happens inside the box
- Anyone who learns better by building and observing than by reading API docs

---

## What can you do with StructLab?

### Explore

Open a live session with any supported structure and implementation.
Execute operations (push, enqueue, insert, etc.) and watch the internal
state change visually in real time — including backing arrays, chains,
buckets, heap trees, and circular buffers.

### Compare

Run the same operation sequence across multiple implementations of the
same abstract data type, side by side.  See how an ArrayStack and a
LinkedStack handle the same pushes and pops differently, or how chaining
and open addressing diverge on the same hash insertions.

### Learn

Browse the structure registry — categories, descriptions, complexity
metadata, and implementation variants.

### Activity

Review session history and recent actions.

### Settings

Application preferences.

---

## Current capabilities

| Area | Status |
|------|--------|
| Core structures | 7 families, 17 implementations |
| Visual state panes | 14 family-specific panes |
| Compare workspace | Full side-by-side visual comparison |
| Terminal simulator | Fully functional REPL mode |
| GUI (JavaFX) | Five-page desktop shell |
| Trace system | Before/after state, invariant checks, complexity |
| CI | GitHub Actions with Xvfb, JaCoCo coverage |

### Structure families

- **Array** — FixedArray, DynamicArray
- **Linked List** — SinglyLinkedList, DoublyLinkedList
- **Stack** — ArrayStack, LinkedStack
- **Queue** — CircularArrayQueue, LinkedQueue, TwoStackQueue
- **Deque** — ArrayDequeCustom, LinkedDeque
- **Heap / Priority Queue** — BinaryHeap, HeapPriorityQueue
- **Hash Table** — HashTableChaining, HashTableOpenAddressing (linear, quadratic, double), HashSetCustom

---

## High-level product shape

```
┌────────────────────────────────────────────────────────┐
│  GUI Shell (JavaFX)                                    │
│  ┌─────┬─────┬──────┬──────────┬──────────┐           │
│  │Expl.│Comp.│Learn │ Activity │ Settings │           │
│  └─────┴─────┴──────┴──────────┴──────────┘           │
│                     ▼                                  │
│  StructLabService (facade)                             │
│       │                                                │
│       ├─ StructureRegistry (metadata / discovery)      │
│       ├─ SessionManager  (session lifecycle)           │
│       ├─ RuntimeFactory  (adapters per implementation) │
│       └─ ComparisonSession (multi-impl execution)      │
│                     ▼                                  │
│  Core structures ← Trace wrappers ← SnapshotParser    │
│                     ▼                                  │
│  Visual state pipeline:                                │
│    snapshot → StateModelParser → VisualState sealed    │
│    → VisualPaneCache → family-specific JavaFX pane     │
└────────────────────────────────────────────────────────┘
```

---

## How to run

**GUI mode:**
```bash
mvn javafx:run
```

**Terminal simulator:**
```bash
mvn compile exec:java "-Dexec.mainClass=structlab.app.StructLabApp"
```

**Tests:**
```bash
mvn test
```
