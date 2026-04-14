# StructLab GUI Playthrough Manual

A step-by-step manual for testing the StructLab JavaFX GUI.  Follow this
document from top to bottom to validate that the application works correctly.

---

## 1. Purpose

This manual is the primary human acceptance test for the StructLab GUI.  It
defines exactly what to do, what to see, and what counts as pass or fail.

---

## 2. Prerequisites

- **Java 17+** installed and on PATH
- **Maven 3.9+** installed and on PATH
- Project compiles cleanly: `mvn compile`
- No other StructLab instance running

---

## 3. How to start the app

From the project root directory, run:

```bash
mvn clean javafx:run
```

The application window should appear within a few seconds.

---

## 4. What you should see on launch

The window is divided into four areas:

| Area | Location | Contents |
|---|---|---|
| **Toolbar** | Top | "StructLab" title on the left, "Discovery Mode" status on the right |
| **Discovery panel** | Left | "Data Structures" list (populated), "Implementations" list (empty), "Open Session" button (disabled) |
| **Center panel** | Center | "Structure Details" section showing "Select a structure to view details.", empty "Structure State" area, empty "Last Trace" area |
| **Right panel** | Right | "No active session" label, disabled Reset/Close buttons, empty operations list, disabled Execute button, empty history list |
| **Status bar** | Bottom | "Ready" text |

**Pass:** All areas visible, structure list populated, buttons disabled, no errors.

---

## 5. Discovery walkthrough

### 5.1 Select a structure

1. Click on any structure in the "Data Structures" list (e.g., "Stack [stack]").
2. The "Implementations" list below should populate with available implementations.
3. The "Structure Details" box in the center should update to show:
   - Structure name (bold)
   - Category
   - Description
   - Keywords (italic)

**Pass:** Implementation list shows at least one entry.  Details box shows real
data, not placeholder text.

### 5.2 Select a different structure

1. Click a different structure (e.g., "Queue [queue]").
2. The implementations list should change to show queue implementations.
3. The details box should update to show the queue description.

**Pass:** Lists update correctly on selection change.

---

## 6. Session walkthrough

### 6.1 Open a session

1. Select a structure (e.g., "Stack").
2. Select an implementation (e.g., "Array Stack").
3. Click "Open Session".

**Expected results:**
- Toolbar status changes to "Session: Stack / Array Stack" (or similar).
- Right panel shows session info: structure name, implementation name, "Operations: 0".
- Operations list populates with available operations (push, pop, peek, etc.).
- Reset and Close Session buttons become enabled.
- Execute button becomes enabled.
- Bottom status bar shows "Session opened for Array Stack."
- The Structure State area shows the initial (empty) state rendering.

**Pass:** All of the above.

### 6.2 Verify "Open Session" is disabled during active session

1. While a session is open, try clicking a different structure.
2. The "Open Session" button should remain disabled.

**Pass:** Cannot open a second session while one is active.

---

## 7. Operation walkthrough

### 7.1 Execute a simple operation

1. With a stack session open, select "push" from the operations list.
2. Type `42` in the argument field.
3. Click "Execute".

**Expected results:**
- Structure State area updates to show the stack containing 42.
- History list shows: `[OK] 1. push -> null` or similar.
- Last Trace area shows trace step output with before/after snapshots.
- Operations count updates to 1.
- Bottom status shows "Executed push".

**Pass:** State, history, trace, and ops count all update.

### 7.2 Execute multiple operations

1. Push more values: `10`, `20`, `30`.
2. Execute a `pop` operation (no argument needed).

**Expected results:**
- Pop returns 30 (displayed in status bar and history).
- State shows the stack without 30.
- History shows all operations in order.
- Trace shows the pop operation's before/after.

**Pass:** Return values correct, history accumulates, trace updates.

### 7.3 Execute a read-only operation

1. Execute `peek` (no argument).

**Expected results:**
- Returns the top value without modifying the stack.
- State remains unchanged.
- History adds the peek entry.

**Pass:** State unchanged, value returned correctly.

---

## 8. Failure testing walkthrough

### 8.1 Pop from an empty stack

1. Open a fresh session or reset the current one.
2. Execute `pop` with no elements in the stack.

**Expected results:**
- History shows `[FAIL]` entry with an error message.
- Bottom status shows the failure message.
- Trace area shows the failed operation trace.
- Structure state remains empty.

**Pass:** Failure is shown clearly, app does not crash.

### 8.2 Missing required argument

1. Select `push` from the operations list.
2. Leave the argument field empty.
3. Click "Execute".

**Expected results:**
- Bottom status bar shows a message about required arguments.
- No operation is executed.
- History does not change.

**Pass:** Validation message shown, no crash, no empty push.

### 8.3 Invalid operation name

If the operations list is being used (which it should be), you cannot type an
invalid operation name.  This test confirms that only valid operations from the
list can be executed.

**Pass:** No way to type arbitrary operation names.

---

## 9. Reset walkthrough

1. With several operations already executed, click "Reset".

**Expected results:**
- Structure state returns to the initial empty rendering.
- History list clears.
- Trace area clears or shows "No trace steps available."
- Operations count resets to 0.
- Session info (structure name, implementation name) remains unchanged.
- Bottom status shows "Session reset to empty state."

**Pass:** State and history cleared, session still active.

---

## 10. Close session walkthrough

1. Click "Close Session".

**Expected results:**
- Session info shows "No active session".
- Operations list clears.
- History clears.
- State and trace areas clear.
- Reset, Close Session, and Execute buttons become disabled.
- "Open Session" button becomes enabled again.
- Toolbar status returns to "Discovery Mode".
- Bottom status shows "Session closed."

**Pass:** GUI returns to clean discovery state.

---

## 11. Multi-structure regression routine

Repeat sections 6–10 for each of the following structures:

| Structure | Implementation | Key operations |
|---|---|---|
| Stack | Array Stack | push, pop, peek, size, isEmpty |
| Stack | Linked Stack | push, pop, peek, size, isEmpty |
| Queue | Circular Array Queue | enqueue, dequeue, peek, size, isEmpty |
| Queue | Linked Queue | enqueue, dequeue, peek, size, isEmpty |
| Queue | Two-Stack Queue | enqueue, dequeue, peek, size, isEmpty |
| List | Singly Linked List | addFirst, addLast, removeFirst, get, size |
| List | Doubly Linked List | addFirst, addLast, removeFirst, removeLast, get, size |
| Deque | Linked Deque | addFirst, addLast, removeFirst, removeLast, peekFirst, peekLast |
| Deque | Array Deque | addFirst, addLast, removeFirst, removeLast, peekFirst, peekLast |
| Heap | Binary Heap | insert, extractMin, peekMin, size |
| Heap | Heap Priority Queue | insert, extractMin, peekMin, size |

For each:
1. Open session
2. Execute 3–4 operations
3. Verify state, history, and trace update correctly
4. Reset and verify clean state
5. Close session and verify clean return to discovery

**Pass:** All structures behave correctly without crashes.

---

## 12. Expected results summary

| Check | Pass | Fail |
|---|---|---|
| App launches without errors | Window appears | Exception in console |
| Structure list populated on start | Non-empty list | Empty list or crash |
| Structure details display | Name, category, description visible | Blank or placeholder |
| Session opens correctly | State rendered, ops listed | Error dialog or blank |
| Operations execute and update state | State + history + trace change | No update or crash |
| Failed operations display clearly | [FAIL] in history, message shown | Silent failure or crash |
| Reset clears state and history | Empty state, 0 ops count | Stale data remains |
| Close returns to discovery | All panels cleared, buttons reset | Orphaned session state |
| Multi-structure regression | All structures pass above checks | Any structure fails |

---

## 13. Common problems

### GUI does not launch

- Verify Java 17+: `java -version`
- Verify Maven: `mvn -version`
- Run `mvn compile` first to check for compilation errors
- Check that JavaFX dependencies downloaded: look in `~/.m2/repository/org/openjfx/`

### No structures visible

- The registry seeder may have failed.  Check console output for exceptions.
- Verify `RegistrySeeder.seed()` is called in `StructLabFxApp.start()`.

### Session does not open

- Ensure both a structure *and* an implementation are selected.
- Check the bottom status bar for error messages.

### Operation fails unexpectedly

- Check the argument format (integers separated by spaces).
- Check the operation's expected argument count in the tooltip.
- An operation failing on an empty structure (e.g., pop on empty stack) is expected behavior.

### Window layout looks broken

- Ensure minimum window size is respected (900x550).
- Try resizing the window.

---

## 14. Smoke test for terminal mode

The terminal simulator is a secondary interface.  Quick smoke test:

**Command:**

```bash
mvn compile exec:java "-Dexec.mainClass=structlab.app.StructLabApp"
```

**Minimal flow:**

```text
structlab> ls
structlab> info stack
structlab> play stack impl-array-stack
structlab[stack/array-stack]> push 10
structlab[stack/array-stack]> push 20
structlab[stack/array-stack]> pop
structlab[stack/array-stack]> state
structlab[stack/array-stack]> history
structlab[stack/array-stack]> trace
structlab[stack/array-stack]> close
structlab> quit
```

**Pass:** All commands produce sensible output, no exceptions, clean exit.
