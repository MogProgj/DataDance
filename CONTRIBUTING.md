# Contributing

Thanks for checking out StructLab.

---

## Ground rules

1. Read `docs/design-principles.md` before writing any code.
2. Do not use built-in Java collection types for the data structures being
   studied.
3. Keep the layer separation in `docs/architecture.md` intact.

---

## How to contribute

1. Create an issue describing the change you want to make.
2. Create a branch from `main` with a descriptive name
   (e.g. `feat/array-stack`, `fix/queue-wraparound`).
3. Keep changes small and focused on a single structure or layer.
4. Add or update tests alongside the implementation.
5. Open a pull request with a clear description of what changed and why.

---

## Commit style

Write commit messages as if explaining the change to a teammate.

```
Add dynamic array with amortised resize
Fix circular queue wraparound at capacity boundary
Document stack invariant check
```

Use the imperative mood and keep the first line under 72 characters.

---

## Code style

- Standard Java naming conventions (`camelCase` for methods and variables,
  `PascalCase` for classes, `UPPER_SNAKE_CASE` for constants).
- Favour clarity over brevity.
- Add Javadoc to all public classes and methods.
- Keep methods short and single-purpose.

