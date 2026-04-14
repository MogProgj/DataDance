# Phase 6: JavaFX GUI Shell and Service Layer

## Goal

Introduce a clean, GUI-facing service layer and a minimal but functional JavaFX
desktop application.  The GUI must consume the same registry, session, and
runtime model as the terminal simulator — but through a semantic facade, not
by parsing terminal text.

## What was delivered

### Service layer (`structlab.app.service`)

| Class | Purpose |
|---|---|
| `StructLabService` | Facade wrapping registry, session, and runtime into clean method calls |
| `StructureSummary` | Read-only view-model for structure metadata |
| `ImplementationSummary` | Read-only view-model for implementation metadata |
| `SessionSnapshot` | Snapshot of the active session (IDs, names, operation count) |
| `OperationInfo` | View-model for an available operation (name, aliases, usage, mutates, complexity) |
| `ExecutionResult` | Result of executing an operation (success, message, returned value, trace steps) |

The service layer is tested independently in `StructLabServiceTest` covering
discovery, session lifecycle, operations, state/history, and reset.

### JavaFX application (`structlab.gui`)

| File | Purpose |
|---|---|
| `StructLabFxApp` | JavaFX `Application` entry point; loads FXML and wires service |
| `controller/MainWindowController` | FXML controller binding UI events to `StructLabService` |
| `resources/structlab/gui/main-window.fxml` | BorderPane layout with discovery, state, trace, operations, history panels |
| `resources/structlab/gui/styles.css` | Clean neutral theme with monospace code areas and status coloring |

### Build integration

- `javafx-controls` and `javafx-fxml` dependencies (JavaFX 21.0.5)
- `javafx-maven-plugin` configured with `structlab.gui.StructLabFxApp`
- Launch: `mvn clean javafx:run`
- Terminal mode unchanged: `mvn compile exec:java`

## Architecture decisions

1. **No coupling to shell parsing.** The GUI never calls `CommandRouter` or
   `CommandParser`.  It uses `StructLabService` exclusively.
2. **FXML + Controller pattern.** Layout is declarative; the controller handles
   events and state management.
3. **Record-based DTOs.** All view-models are Java records — immutable,
   compact, and easy to test.
4. **Unified session ownership.** `StructLabService` delegates session
   management to `SessionManager`, the same abstraction used by the terminal
   shell.  This eliminates duplicate session state and prevents GUI/terminal
   session drift.
5. **Service is stateful but single-user.** Matches the terminal model: one
   active session at a time.

## GUI layout

The main window uses a `BorderPane` with:

- **Left:** Discovery panel — structure list, implementation list, open session button
- **Center:** Structure detail box (name, category, description, keywords),
  structure state area, last trace area
- **Right:** Session info, reset/close buttons, operations list, argument field
  with execute button, history list
- **Top:** Toolbar with app title and status
- **Bottom:** Status bar with feedback messages

Empty states are handled explicitly: placeholder text appears when no structure
is selected, no session is open, no trace is available, or no history exists.

## What is next

- Richer visualisation panels (ASCII art to graphical node/array rendering)
- Operation argument validation in the GUI
- Comparison mode (side-by-side execution on multiple implementations)
- Animation of trace steps
