# GUI Shell and Pages

The StructLab desktop application is a JavaFX 21 single-window shell with
five navigation pages.

---

## Shell structure

```
┌────────────────────────────────────────────────────────┐
│  Toolbar         "StructLab"            [status text]  │
├────────────────────────────────────────────────────────┤
│ ┌─────────┐ ┌──────────────────────────────────────┐   │
│ │ Sidebar │ │          Page content                │   │
│ │         │ │                                      │   │
│ │ Explore │ │                                      │   │
│ │ Compare │ │                                      │   │
│ │ Learn   │ │                                      │   │
│ │Activity │ │                                      │   │
│ │Settings │ │                                      │   │
│ └─────────┘ └──────────────────────────────────────┘   │
├────────────────────────────────────────────────────────┤
│  Status bar                                            │
└────────────────────────────────────────────────────────┘
```

- **Toolbar**: App title on the left, contextual status on the right
  (e.g. "Discovery Mode" or "Session: Stack / Array Stack")
- **Sidebar**: Navigation buttons for the five pages
- **Page content**: Swapped based on the active `NavigationPage`
- **Status bar**: Feedback messages ("Ready", "Session opened", etc.)

---

## Navigation model

`NavigationPage` is an enum with five values.  The controller swaps
the center content when a sidebar button is clicked.  Only one page
is visible at a time.  Session state is preserved when switching pages.

---

## Page responsibilities

### Explore

The primary single-session workspace.

- **Discovery panel** (left): Structure list, implementation list,
  "Open Session" button.
- **Center panel**: Structure details, visual state pane (or text
  fallback), last trace step.
- **Session panel** (right): Session info, operation list, argument
  field + Execute button, operation history, Reset/Close controls.

The visual state pane is produced by `VisualStateFactory`, which
delegates to `VisualPaneCache`.

### Compare

Side-by-side comparison workspace.

- Structure/family selector at the top
- Comparison summary header (`ComparisonSummaryPane`)
- Grid of `ComparisonCardPane` instances — one per implementation
- Shared operation bar for executing the same operation on all cards
- Each card shows: implementation name, status badge, visual state,
  returned value, expandable trace details

See [compare-workspace.md](compare-workspace.md) for full detail.

### Learn

Data structure reference library.  Displays registry metadata:
categories, descriptions, keywords, complexity information,
implementation variants.

### Activity

Session history and recent actions log.  Shows a timeline of
operations performed during the current application run.

### Settings

Application preferences (placeholder for future configuration
such as theme, animation speed, default family).

---

## FXML and CSS

- Layout: `resources/structlab/gui/main-window.fxml`
- Styles: `resources/structlab/gui/styles.css`
- Entry point: `StructLabFxApp` → `MainWindowController`

The CSS uses a dark neutral theme with monospace text areas, rounded
cards, and color-coded status badges.
