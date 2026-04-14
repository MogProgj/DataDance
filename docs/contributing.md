# Contributing

---

## Before you start

1. Read [architecture.md](architecture.md) to understand the layer separation.
2. Read [visual-state-system.md](visual-state-system.md) if your change touches
   the GUI visual pipeline.
3. Read [ci-cd-and-testing.md](ci-cd-and-testing.md) for test expectations.

---

## Coding expectations

- Standard Java naming conventions (`camelCase` methods, `PascalCase` classes,
  `UPPER_SNAKE_CASE` constants).
- No built-in Java collection types for the structures under study.
- Keep method bodies short and single-purpose.
- Add Javadoc to all public classes and methods.
- Favour clarity over brevity.

---

## Layer discipline

- **Core** (`structlab.core`): No dependency on any other StructLab layer.
- **Trace** (`structlab.trace`): Depends only on core.
- **Render** (`structlab.render`): Depends on core and trace.
- **Registry** (`structlab.registry`): Standalone metadata.
- **App** (`structlab.app`): Depends on all above.
- **GUI** (`structlab.gui`): Depends on app.service, not on shell/command.

Do not introduce upward dependencies.

---

## Testing expectations

- All core structures must have comprehensive unit tests.
- Traced wrappers must verify trace step correctness.
- Visual panes must have JavaFX tests using `@ExtendWith(JavaFxToolkitExtension.class)`.
- New visual panes must include `@Timeout(10)` at the class level.
- All tests must pass before merging: `mvn test`
- CI must stay green.

---

## Adding a new structure family

1. Implement the structure in `structlab.core.<family>`
2. Add a traced wrapper in `structlab.trace`
3. Add render support in `structlab.render.StructureRenderer`
4. Register in `RegistrySeeder`
5. Add a runtime adapter in `structlab.app.runtime.adapters`
6. Create a `*StateModel` record implementing `VisualState`
7. Add a parse method in `StateModelParser` and update `parse()`
8. Create a `*VisualPane` with a corresponding test
9. Wire into `VisualPaneCache`
10. Add canonical operations in `CanonicalOperationRegistry` if needed
11. Update `structure-families.md`
12. Use `UiComponents` static methods for shared UI elements when
    building page content (e.g. `card()`, `styledLabel()`, `buttonRow()`)

---

## Adding a new visual pane to an existing family

1. Create the state model record implementing `VisualState`
2. Add the parse method in `StateModelParser`
3. Update `StateModelParser.parse()` switch
4. Create the visual pane class
5. Add to `VisualPaneCache`
6. Add a test with `@ExtendWith(JavaFxToolkitExtension.class)` and `@Timeout(10)`

---

## Documentation expectations

- Update relevant docs when adding structures, visual panes, or families.
- Keep the roadmap current.
- Do not leave contradictory information in multiple docs.
- Stale docs belong in `docs/archive/`, not in the main tree.

---

## Phase/naming discipline

Work is organized as numbered phases.  Each phase has a clear scope:
- Do not mix unrelated changes into one phase.
- Name branches descriptively (e.g. `feature/phase-2c4-array-visuals`).
- Keep changes incremental and reviewable.

---

## Commit style

Imperative mood, under 72 characters for the first line.

```
feat(visual): add FixedArrayVisualPane with rigid grid rendering
fix(ci): prevent JavaFX tests from hanging on headless CI
docs: update architecture and roadmap for Phase 2D
```
