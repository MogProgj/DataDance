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

> **Phase 7 — Comparison Mode (active)**
>
> Phases 1–6 are complete.  A terminal simulator backend and a JavaFX GUI
> are both functional.  Phase 7 adds **comparison mode**: run the same
> operation sequence against multiple implementations of the same ADT and
> compare results, state, and traces side-by-side.
>
> **What works:**
> - Terminal simulator: full discovery, session, operation, and comparison flow
> - JavaFX GUI: structure browsing, detail display, session lifecycle,
>   operation execution, state/trace/history rendering, comparison mode
>   via "Compare All" button
> - Comparison mode: open a comparison session for any structure with 2+
>   implementations, execute operations across all implementations,
>   view side-by-side state/trace/history, reset all, close
> - Supported comparison families: Stack (2), Queue (3), Deque (2),
>   Heap (2), Array (2), Linked List (2), Hash Table (5)
> - Service facade: clean API consumed by the GUI and terminal, tested
>   independently
> - Backend tests: 400+ automated tests covering core, trace, render,
>   service, and comparison layers
>
> **Current limitations:**
> - GUI comparison view uses text-based rendering (not rich graphics)
> - Operations use integer-based interactive values

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
| Hash | Hash table (chaining) | `HashTableChaining` | yes | yes | yes |
| Hash | Hash set | `HashSetCustom` | yes | yes | yes |
| Hash | Hash table OA (linear) | `HashTableOpenAddressing` | yes | yes | yes |
| Hash | Hash table OA (quadratic) | `HashTableOpenAddressing` | yes | yes | yes |
| Hash | Hash table OA (double) | `HashTableOpenAddressing` | yes | yes | yes |

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
| `TracedHashTableChaining` | Traced wrapper for `HashTableChaining` |
| `TracedHashSetCustom` | Traced wrapper for `HashSetCustom` |

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
| HashTableChaining | Bucket array with per-bucket chains, size, capacity, load factor |
| HashSetCustom | Set size with backing hash table bucket view |

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
| 6 | JavaFX GUI shell and service layer |
| 7 | Broader data structure family |
| 8 | Comparison mode (same ops on multiple implementations) |
| 9 | Algorithm demonstrations on top of structures |
| 10 | Polish, testing, and educational refinement |
| 6 | JavaFX GUI shell and service layer |
| 7 | Broader data structure family |
| 8 | Comparison mode (same ops on multiple implementations) |
| 9 | Algorithm demonstrations on top of structures |
| 10 | Polish, testing, and educational refinement |

Full details are in [`docs/roadmap.md`](docs/roadmap.md).

For the Phase 0 working contract, see [`docs/phase-0-foundation.md`](docs/phase-0-foundation.md).

---

## Running the project

Requires Java 17+.  Maven is provided by the included
[Maven Wrapper](https://maven.apache.org/wrapper/) — no global Maven
installation needed.

### GUI mode (primary)

```bash
./mvnw clean javafx:run
```

The JavaFX application window will open.  See
[`docs/gui-playthrough-manual.md`](docs/gui-playthrough-manual.md) for a
step-by-step walkthrough.

### Terminal mode (secondary)

```bash
./mvnw compile exec:java "-Dexec.mainClass=structlab.app.StructLabApp"
```

See [`docs/how-to-play.md`](docs/how-to-play.md) for terminal commands and
usage.

### Build and test

```bash
./mvnw compile         # compile all sources
./mvnw test            # compile and run all tests
```

### Run a traced demo

```bash
./mvnw compile exec:java -Dexec.mainClass=structlab.demo.TracedArrayStackDemo
```

Replace the class name with any demo under `structlab.demo`.

---

## Testing the app

| Layer | Command | Purpose |
|---|---|---|
| Backend tests | `./mvnw test` | Primary regression gate — must always pass |
| GUI manual testing | `./mvnw clean javafx:run` | Primary human acceptance surface |
| Console smoke test | `./mvnw compile exec:java "-Dexec.mainClass=structlab.app.StructLabApp"` | Secondary debug/validation path |

- **GUI** is the main surface for manual feature validation.
- **Backend tests** are mandatory before every merge.
- **Terminal** remains available for quick debugging and smoke testing.

For full details, see:
- [`docs/gui-playthrough-manual.md`](docs/gui-playthrough-manual.md) — GUI testing manual
- [`docs/testing-strategy.md`](docs/testing-strategy.md) — testing policy and checklist
- [`docs/how-to-play.md`](docs/how-to-play.md) — terminal simulator guide

---

## Comparison mode

Comparison mode lets you run the same operation sequence against every
implementation of an ADT and immediately see how each one behaves.

### Terminal

```
structlab> compare stack
  → opens comparison session for all Stack implementations

structlab[compare:stack]> push 42
  → executes push(42) on Array Stack and Linked Stack, shows side-by-side results

structlab[compare:stack]> cmp-state
  → shows current state of all implementations

structlab[compare:stack]> cmp-trace
  → shows trace output from the last operation

structlab[compare:stack]> cmp-reset
  → resets all implementations to empty

structlab[compare:stack]> close
  → exits comparison mode
```

**Commands:**

| Command | Alias | Description |
|---|---|---|
| `compare <structure>` | `cmp` | Open comparison mode (or list eligible structures) |
| `compare-ops` | `cmp-ops` | List operations common to all implementations |
| `compare-state` | `cmp-state` | Show state of all implementations |
| `compare-trace` | `cmp-trace` | Show trace from last operation |
| `compare-history` | `cmp-history` | Show comparison operation history |
| `compare-session` | `cmp-session` | Show comparison session info |
| `compare-reset` | `cmp-reset` | Reset all implementations |

### GUI

Select a structure in the left panel and click **Compare All** to open a
comparison session with all available implementations.  Operations,
state, and traces update across all implementations in the existing
center panel.  Click **Close Session** to exit comparison mode.

---

## CI/CD Pipeline

### Continuous Integration (`ci.yml`)

Every push to `main` or `Gemini` (and every pull request) triggers two jobs:

| Job | What it does |
|---|---|
| **verify** | `./mvnw clean verify` — compile, run all tests, generate JaCoCo coverage |
| **package** | `./mvnw clean package -DskipTests` — produce the shaded uber-JAR artifact |

CI artifacts are downloadable from the GitHub Actions run page:
- `jacoco-report` — HTML coverage report (14-day retention)
- `structlab-snapshot` — packaged JAR (30-day retention)

### Release automation (`release.yml`)

Pushing a version tag triggers a full release build:

```bash
# Cut a release
git tag v0.2.0
git push origin v0.2.0
```

The release workflow will:
1. Set the Maven version from the tag (e.g. `v0.2.0` → `0.2.0`)
2. Run the full test suite
3. Build the shaded uber-JAR
4. Create a GitHub Release with auto-generated release notes
5. Attach the JAR to the release as a downloadable asset

**Tag format:** `vX.Y.Z` (e.g. `v0.2.0`, `v1.0.0`).
Pre-release tags like `v0.3.0-rc1` are supported and marked as pre-release.

During development the `pom.xml` version stays at `SNAPSHOT`.  The release
workflow overrides it at build time — no manual pom.xml edits needed.

### Local CI

```bash
bash scripts/ci-local.sh
```

This runs the same verify + package steps that CI performs.

---

## Docker

Docker support provides **reproducible builds** and **CI parity**, not GUI
execution.  JavaFX requires a display server, so the containerised app runs
in **terminal mode only**.

### What Docker is for

- Running the full test suite in an isolated, reproducible environment
- Building the packaged JAR in a clean room (no local toolchain variation)
- Headless terminal-mode execution
- CI parity — same JDK, same Maven, same results

### What Docker is NOT for

- Running the JavaFX GUI (requires a display server / X11 forwarding)
- Production deployment (this is a desktop app, not a server)

### Usage

```bash
# Build the image (runs tests during build)
docker build -t structlab .

# Run in terminal mode
docker run --rm structlab

# Extract the built JAR
docker create --name sl structlab
docker cp sl:/app/structlab.jar .
docker rm sl
```

The Dockerfile uses a multi-stage build:
1. **Builder stage** (`maven:3.9-eclipse-temurin-17`) — dependency resolution,
   compile, test, package
2. **Runtime stage** (`eclipse-temurin:17-jre`) — minimal image with only the
   built JAR

---

## Contributing

See [`CONTRIBUTING.md`](CONTRIBUTING.md).

---

## License

MIT — see [`LICENSE`](LICENSE).

