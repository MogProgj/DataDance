# StructLab Interactive Simulator: How to Play

Welcome to the StructLab Interactive Terminal Simulator! This document is your guide to understanding how to run the simulator, explore data structures, and use the interactive commands. 

StructLab is designed as an interactive REPL (Read-Eval-Print Loop) environment. It allows you to "discover" data structures as if you were browsing a registry, right before you mount one as a "live session" and start performing physical data structure operations under the hood.

## How to Launch the Simulator

Ensure you have Java 17+ and Maven 3.9+ installed. You can start the interactive shell by running the following command from the root of the project:

```bash
mvn compile exec:java "-Dexec.mainClass=structlab.app.StructLabApp"
```

## The Two Core Modes

The simulator operates using two main "modes" or visual rulesets:
1. **Discovery Mode:** Where you search metadata, categories, and complexities across all supported abstract data structures mapped in the registry.
2. **Active Session Mode:** Where you "mount" a specific physical data structure implementation, simulate live memory traces on it, and mutate its ongoing state through operations.

---

## 1. Discovery Mode (Browsing)
When you first start the app, your terminal will read:
```
structlab>
```

You are now in **Discovery Mode**. Here are the rules and commands you can run:

* `list` (or `ls`, `catalog`) - Lists all abstract parent structures registered in StructLab (e.g., Stack, Queue, Heap).
* `search <keyword>` (or `s`) - Search the registry by keyword or name.
* `info <structure-id>` (or `i`) - Displays metadata, complexities, and all known **Implementations** for that structure framework. For instance: `info stack`.
* `stats` - Overview statistics about memory/registration loading.
* `help` (or `?`) - Print out the master command reference manual.
* `clear` (or `cls`) - Clear your visual console output buffer.
* `quit` (or `exit`, `q`) - Close the application.

---

## 2. Active Session Mode (Simulating)
Once you locate a structure and an implementation you want to play with, you can "mount" it into a live interactive session.

**Mounting a session:**
```
open <structure-id> <implementation-id>
```
*Aliases: `use`, `play`, `start`*

**Example:**
```
structlab> play stack impl-array-stack
```

Your prompt will dynamically context-shift and display the namespace of the structure you mounted: 
`structlab[stack/array-stack]>`

### Simulator Operation Rules
Once in an active session, you can pass operations directly to the data structure. You can view all legal operation commands available for that specific implementation by typing:
* `ops` - Outputs a list of operations (e.g., `push <arg>`, `pop`, `peek`) the active structure accepts.

To execute an operation, just type it directly:
```
structlab[stack/array-stack]> push 10
```
Or you can use the `run` / `do` prefix explicitly:
```
structlab[stack/array-stack]> run push 15
```

If the operation is successful, you will see a **Live Trace Output** detailing:
* The success state.
* Any values returned by the structure (e.g. `15` during an execution of `pop`).
* The current visual rendering of the structure's physical state.

### Managing the Running Session
As you perform operations, StructLab tracks the trace timeline memory footprints. You can inspect or clear this timeline using the following Session Controls:

* `state` (or `snapshot`) - Show the current visual state of the data structure.
* `history` (or `log`) - Show how many operations have occurred since mounting or the last wipe.
* `last` - Print raw details of the _very last_ operation triggered.
* `trace` - Output the step-by-step memory pointer trace logic over the preceding operation.
* `reset` (or `wipe`) - Completely resets your active structure back to an empty state and flushes the timeline history, freeing trace memory overhead.
* `session` - Show all live session details.
* `close` (or `back`) - Dismount your active session. Brings you safely back to **Discovery Mode** with the `structlab>` prompt.

---

## Example Flow Playthrough

Here is what a standard simulation session looks like:

```text
structlab> ls
(See Stack is in the list)

structlab> info stack
(See that 'impl-linked-stack' has an O(1) time complexity footprint)

structlab> play stack impl-linked-stack
(Session context changes)

structlab[stack/linked-stack]> ops
(See that 'push' is supported)

structlab[stack/linked-stack]> push 99
(Watch the Linked Stack append a Node containing 99!)

structlab[stack/linked-stack]> push 100

structlab[stack/linked-stack]> pop
(Returns 100 and un-links the Node natively)

structlab[stack/linked-stack]> history
(Shows 3 timeline events logged)

structlab[stack/linked-stack]> wipe
(Clears the nodes and resets the reference points to empty)

structlab[stack/linked-stack]> close
(Back to standard exploration)

structlab> quit
```

---

## 3. Comparison Mode

Comparison mode lets you run the same operations on all implementations of a
structure simultaneously and see the results side-by-side.

### Entering Comparison Mode

```
structlab> compare stack
```

This opens a comparison session for all Stack implementations (Array Stack and
Linked Stack). Your prompt changes to:

```
structlab[compare:stack]>
```

To compare only specific implementations:

```
structlab> compare queue impl-linked-queue impl-two-stack-queue
```

### Running Operations

Type operations directly — they execute on all implementations at once:

```
structlab[compare:stack]> push 42
structlab[compare:stack]> push 99
structlab[compare:stack]> pop
```

Each operation shows a side-by-side comparison of the result, returned value,
state, and trace step count for every implementation.

### Comparison Commands

| Command | Alias | Description |
|---|---|---|
| `compare <id>` | `cmp` | Open comparison mode (no args = list eligible) |
| `compare-ops` | `cmp-ops` | List common operations |
| `compare-state` | `cmp-state` | Show all implementation states |
| `compare-trace` | `cmp-trace` | Show traces from last operation |
| `compare-history` | `cmp-history` | Show comparison history |
| `compare-session` | `cmp-session` | Show session info |
| `compare-reset` | `cmp-reset` | Reset all to empty |
| `close` | `back` | Exit comparison mode |

### Example Comparison Flow

```text
structlab> compare stack
(Opens comparison session with Array Stack and Linked Stack)

structlab[compare:stack]> push 42
(Both implementations push 42 — results shown side-by-side)

structlab[compare:stack]> push 99

structlab[compare:stack]> cmp-state
(Shows the internal state of both Array Stack and Linked Stack)

structlab[compare:stack]> pop
(Both return 99 — shows consistency across implementations)

structlab[compare:stack]> cmp-trace
(Shows the step-by-step trace for each implementation's last pop)

structlab[compare:stack]> cmp-reset
(Both implementations reset to empty)

structlab[compare:stack]> close
(Back to Discovery Mode)
```
