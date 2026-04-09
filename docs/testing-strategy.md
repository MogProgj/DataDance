# StructLab Testing Strategy

This document defines how StructLab is tested, which layer is responsible for
what, and what must pass before merging any change.

---

## Testing layers

### 1. Automated backend/service tests (primary regression gate)

**Run with:** `mvn test`

These tests cover:

- Core data structure correctness (arrays, stacks, queues, lists, deques, heaps)
- Trace layer accuracy (traced wrappers produce correct TraceStep records)
- Render layer parsing and ASCII output
- Service facade correctness (discovery, session lifecycle, operations, history, reset)
- Command parsing, routing, and result formatting for the terminal simulator

**When to run:** Before every commit and merge.  CI should enforce this gate.

**Expectation:** All tests must pass.  No exceptions.

### 2. GUI manual testing (primary human acceptance surface)

**Run with:** `mvn clean javafx:run`

The GUI is the main surface for human feature validation.
A dedicated playthrough manual exists at [`gui-playthrough-manual.md`](gui-playthrough-manual.md).

**What it covers:**

- Discovery flow: structure selection, detail display, implementation listing
- Session lifecycle: open, close, reset
- Operation execution: argument entry, execution, state updates
- Trace and history display after operations
- Failure handling: invalid operations, bad arguments, empty states
- Multi-structure regression across stacks, queues, lists, deques, heaps

**When to use:** After any change that touches the GUI, service layer, or
runtime model.

### 3. Console smoke testing (secondary validation/debug path)

**Run with:** `mvn compile exec:java "-Dexec.mainClass=structlab.app.StructLabApp"`

The terminal simulator remains functional and useful for quick debugging.

**Minimal smoke test:**

1. Launch the simulator
2. Run `ls` to verify structure listing
3. Run `info stack` to verify metadata display
4. Run `play stack impl-array-stack` to open a session
5. Run `push 10`, `push 20`, `pop` to verify operations
6. Run `state`, `history`, `trace` to verify output
7. Run `close` then `quit`

**When to use:** After changes to command handlers, session manager, or runtime
adapters.  Not required for every merge, but recommended when terminal-adjacent
code changes.

---

## When to use each layer

| Change type | Backend tests | GUI manual | Console smoke |
|---|---|---|---|
| Core data structure | Required | — | — |
| Trace / render layer | Required | — | — |
| Service facade | Required | Recommended | — |
| GUI controller / FXML | Required | Required | — |
| Command handlers / shell | Required | — | Recommended |
| Runtime adapters | Required | Recommended | Recommended |
| Docs only | — | — | — |

---

## Before-merge checklist

Every merge into the main branch must satisfy:

- [ ] `mvn clean test` passes with zero failures
- [ ] GUI launches with `mvn clean javafx:run` without errors
- [ ] At least one full GUI playthrough per the manual (if GUI/service code changed)
- [ ] Console smoke test passes (if terminal/runtime code changed)
- [ ] No new compilation warnings related to changed files
- [ ] Docs updated if behavior changed
