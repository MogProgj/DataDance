# Phase 5: Terminal Simulator Foundation

This document outlines the architecture and implementation plan for transforming the StructLab console app into a rich, interactive, terminal-first data structure simulator.

## High-Level Goal
Transform the current app from a simple registry browser into a polished terminal simulator that allows a user to browse structures, inspect trace states, and manipulate structures interactively, all while feeling like a mini interactive data structure lab.

## Target Architecture

1. **`structlab.app.shell.AppShell`**: Coordinates the REPL loop, prompt rendering, and input flow.
2. **`structlab.app.command.*`**: 
   - `CommandParser`: Parses strings into arguments (handles quotes).
   - `CommandRouter`: Maps commands/aliases to handlers.
   - `CommandContext`: execution context holding registry and session.
   - `CommandResult`: structured layout for responses.
3. **`structlab.app.session.*`**:
   - `SessionManager`: tracks the active simulator session.
   - `StructureSession`: represents a live selected structure, its runtime adapter, and history.
4. **`structlab.app.runtime.*`**:
   - Adapters to convert core `Traced*` models into interactive simulator endpoints.
   - `OperationDescriptor`, `RuntimeFactory`, `OperationExecutionResult`.
5. **`structlab.app.ui.*`**:
   - `TerminalTheme` & `TerminalFormatter`: reusable ANSI banners, boxed panels, colored cards.
   - `PromptBuilder`: builds context-aware prompts (e.g., `structlab[heap]>`).

## Implementation Phases

### Phase A — Foundation refactor
- Split app shell into modular packages.
- Create command parser, router, result, and context.
- Create UI formatting/theme helpers.
- Create session manager outline.

### Phase B — Runtime integration
- Create runtime abstraction and factory.
- Add adapters for fully supported structures.
- Allow session open + operation execution.

### Phase C — Command expansion
- Add rich discovery, session, operation, and help commands.
- Imbue smart aliases and error warnings.

### Phase D — UX polish
- Redesign outputs, error states, traces, and histories to be creative and visually distinct.

### Phase E — Tests + docs + Stabilization Patch
- Expand tests around parsing, routing, and output generation.
- Stabilization patch applied to simulator layer, moving away from `System.out.println` towards tightly-coupled `CommandResult` object structures (success, title, body, hint, exitRequested).
- Tightened `SessionManager` state to handle clean dismounts and exceptions.
- Restructured `StructureRuntime` and `AbstractRuntimeAdapter` to implement localized `clearTraceHistory()` behavior preventing trace pollution.
- Shifted all rendering logic (TerminalFormatter boxes) completely out of handlers and into AppShell for clean separation.
- Added dynamic contextual prompting to `PromptBuilder`.
- `OperationExecutionResult` tracks failed execution traces truthfully.
- Session `history` / `last` records both success and failure with trace counts.
- `reset` empties the data structure AND trace history reliably.
- `ops` lists aliases, mutability and complexity.
- `clear` acts purely as a shell-level instruction.

### What is next

The phase 5 terminal foundation is now COMPLETE and stable. The next planned step is the integration of the GUI layer (Phase 6).
- 280+ total test cases ensuring architectural consistency across the Repl shell and session memory handling.

