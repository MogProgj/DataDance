# Phase 0 Foundation Guide

This document turns the early StructLab vision into a simple Phase 0 working
contract.

It covers four things:
- Part 1 — project identity
- Part 2 — the rules of the game
- Part 3 — the room layout
- Part 4 — the Phase 0 boundary

If a future change makes these sections unclear, Phase 0 is not complete.

---

## Part 1 — Project identity

### One-sentence description

StructLab is a Java-based data structure laboratory for learning how core data
structures work by building them from scratch, inspecting their internal state,
and comparing multiple implementations of the same abstract behavior.

### What the project is

StructLab is:
- an educational project first
- implementation-first rather than API-first
- focused on intuition, invariants, and tradeoffs
- designed to grow into a simulator over time
- organized as a layered system so teaching concerns stay separate

### What the project is not

StructLab is not:
- a wrapper around Java's built-in collections
- a LeetCode solution dump
- a GUI application yet
- a general-purpose production collections library
- in the implementation phase yet

### Current status in plain language

Phase 0 means the project is still preparing the workshop before real building
begins.

At this stage we are:
- defining the project clearly
- locking principles and boundaries
- shaping the package layout
- documenting what future work will belong where

At this stage we are not yet:
- implementing arrays, stacks, queues, or other structures
- building the trace system
- building renderers
- building the interactive simulator

### Why this identity matters

This identity keeps the project from drifting.

Whenever a future contribution is proposed, it should be possible to ask:
- Does this help people understand data structures more deeply?
- Does this preserve visibility into internal state?
- Does this belong to the current phase?
- Does this fit the layered architecture?

If the answer is "no" to most of those questions, the work probably does not
belong in StructLab or does not belong yet.

### Part 1 done-criteria

Part 1 is done when a new contributor can quickly answer all of the following:
- What is StructLab?
- Why does it exist?
- What stage is the project currently in?
- What kind of project is it trying to become?
- What is explicitly out of scope right now?

---

## Part 2 — The rules of the game

These rules are the non-negotiable constraints for future work.

### Rule 1 — Build the structures yourself

Do not rely on Java's built-in collection implementations for the data
structure being studied.

Example:
- If the lesson is "build a stack," do not hide `java.util.Stack` or
  `ArrayDeque` behind a custom class.

Reason:
- the point is to understand the structure, not to wrap it

### Rule 2 — Make the inside visible

Each structure must expose enough internal state to support learning.

A successful operation is not enough. The project should make it possible to
see:
- what changed
- where it changed
- what the structure looks like after the operation
- whether the invariants still hold

Reason:
- if the inside is invisible, the educational value drops sharply

### Rule 3 — Prefer meaningful implementation comparisons

When an abstract data type has multiple useful concrete forms, StructLab should
show more than one.

Examples:
- stack via array and stack via linked list
- queue via circular array, linked list, and two stacks

Reason:
- the tradeoffs become easier to understand when the same behavior is realized
  in different ways

### Rule 4 — Every structure owns its invariants

Each structure should define what must always stay true and provide a way to
verify it.

Reason:
- invariants are part of understanding, not just part of testing

### Rule 5 — Separate the teaching layers

Do not mix responsibilities.

Keep these concerns separate:
- data structure logic
- trace recording
- rendering and presentation
- demo scenarios
- application entry points

Reason:
- clean boundaries make the project easier to grow and easier to teach from

### Rule 6 — Favor clarity over cleverness

Use abstractions only when they help people understand the design.

Avoid unnecessary complexity such as:
- decorative design patterns
- abstraction layers with no teaching value
- generic machinery that hides the real lesson

Reason:
- this project is meant to explain, not impress

### Rule 7 — Stay inside the current phase

Phase 0 is for foundation work.

That means documentation, structure, boundaries, and preparation are in scope.
Core implementations and simulation features are not yet in scope.

Reason:
- starting implementation too early weakens the foundation and blurs the
  roadmap

### Part 2 done-criteria

Part 2 is done when future contributors can clearly understand:
- what shortcuts are forbidden
- what qualities every future implementation must have
- how the architecture should stay clean
- how to judge whether a contribution matches the project's purpose
- what work must wait until a later phase

---

## Quick decision filter

Before adding anything to StructLab, ask:

1. Does this fit the project's educational purpose?
2. Does this respect the current phase?
3. Does this preserve clear layer boundaries?
4. Does this improve understanding instead of hiding complexity?
5. Does this keep internal state and invariants inspectable?

If the answer is "no" to any of these, stop and rethink the change.


---

## Part 3 — The room layout

Part 3 explains where future work belongs. In plain language, this is the map
of the workshop.

### The top-level rooms

StructLab is organized into a few major areas:
- `docs/` — vision, rules, architecture, roadmap, and phase guidance
- `src/main/java/structlab/` — future source code
- `src/test/java/structlab/` — future tests
- `examples/` — future standalone examples
- `scripts/` — future helper scripts

### The source-code rooms

Inside `src/main/java/structlab/`, each package has one job:

- `core` — the real data structure implementations
- `trace` — records what happened before and after an operation
- `render` — turns state and trace data into readable output
- `demo` — runs scripted educational scenarios
- `app` — contains entry points and wiring

### The layout rule

Every new file should have an obvious home.

Examples:
- a dynamic array implementation belongs in `core`
- an operation trace record belongs in `trace`
- ASCII output formatting belongs in `render`
- a scripted queue walkthrough belongs in `demo`
- a command-line launcher belongs in `app`

### What Part 3 protects us from

This layout prevents two common problems:
- mixing unrelated responsibilities into one place
- creating files in random locations just because they were convenient at the time

In plain language:
- `core` should not start printing fancy output
- `render` should not contain data structure logic
- `demo` should not become the place where core implementations secretly live

### Empty rooms are allowed

Some rooms are still placeholders during Phase 0. That is fine.

An empty room is acceptable if:
- its purpose is clearly documented
- it matches the planned architecture
- it is waiting for the right future phase

### Part 3 done-criteria

Part 3 is done when a contributor can answer all of the following without
guessing:
- Where does this new work belong?
- Which folder should hold it?
- Which layer is allowed to depend on which other layer?
- Which kinds of responsibilities must stay separated?

---

## Part 4 — The Phase 0 boundary

Part 4 defines the line between preparation work and implementation work.

### What is in scope for Phase 0

Phase 0 includes:
- documentation
- package and folder structure
- contribution guidance
- naming and design conventions
- scope-setting and boundary decisions
- placeholders for future areas when they are clearly labeled

### What is out of scope for Phase 0

Phase 0 does not include:
- implementing real data structures
- building the trace model
- building console renderers
- building demos that depend on missing implementations
- adding the interactive simulator
- adding the GUI
- prematurely adding complexity meant for later phases

### The handoff to Phase 1

Phase 0 ends when the workshop is organized well enough that real building can
start without confusion.

Phase 1 begins when we start implementing the first core structures from
scratch, such as arrays, stacks, and queues.

### The boundary rule

Whenever a proposed change appears, ask a simple question:

> Is this preparing the project, or is this building the product?

If it is building the product, it probably belongs in Phase 1 or later.

### Why Part 4 matters

Without this boundary, the project can drift into implementation too early.
That usually causes one of two problems:
- the architecture is not mature enough yet
- the repo mixes planning work and product work in a confusing way

### Part 4 done-criteria

Part 4 is done when contributors can clearly tell:
- whether a proposed change belongs to Phase 0
- when a task should be postponed to Phase 1
- what "foundation work" means in practice
- when the project is ready to stop preparing and start implementing
