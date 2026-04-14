# Changelog

## Unreleased

### Added — Phase 6A: Ordered Tree Structures
- **Core structures**: `BinarySearchTree` and `AVLTree` in `structlab.core.tree`
  — insert, contains, remove, min, max, height, inorder/preorder/postorder
  traversals, BST-property invariant, Traceable interface
- **AVL rotations**: All four rotation types (Left, Right, Left-Right,
  Right-Left) with `lastRotation()` reporting
- **Trace wrappers**: `TracedBinarySearchTree`, `TracedAVLTree` — full
  before/after snapshots, invariant checks, rotation-aware explanations
- **Visual state**: `OrderedTreeStateModel` (pre-order parenthesised tree
  format), `OrderedTreeVisualPane`, `TreeCanvas.renderOrderedTree()` with
  inorder-offset layout algorithm
- **Runtime adapter**: `TreeRuntimeAdapter` — 8 operations with alias support
  (add/delete/search/find)
- **Registry**: struct-tree + impl-bst + impl-avl seeded; "tree" canonical
  operation family (8 ops) in `CanonicalOperationRegistry`
- **Compare support**: BST vs AVL side-by-side comparison
- Tests: `BinarySearchTreeTest`, `AVLTreeTest`, `TracedBinarySearchTreeTest`,
  `TracedAVLTreeTest`, `OrderedTreeStateModelTest`, `TreeRuntimeAdapterTest`

### Added — Phase 5A: Product Surface Upgrade
- **Compare Intelligence**: `ComparisonAnalysis` model with divergence detection
  (STATUS_MISMATCH, VALUE_MISMATCH, STATE_DIVERGENCE), per-entry nanosecond
  timing, fastest/slowest annotation, three-state verdict
  (MATCHING / DIVERGENT / PARTIAL_FAIL), amber/green card borders, timing labels
- **Learn Page Upgrade**: Search bar with real-time text filtering, category
  ComboBox filter, expanded cards showing behavior descriptions and learning notes
- **Settings Persistence**: `AppSettings` backed by `java.util.prefs.Preferences`,
  compact-mode and high-density root style classes applied live
- **Activity Enrichment**: Category-based filtering, clear log, category badges
- **Export Flows**: `ExportHelper` for Compare history and Activity feed in JSON
  and plain-text/markdown via FileChooser dialogs
- CSS: divergent/fastest card borders, timing labels, learn search bar,
  activity category badges, compact-mode and high-density root effects
- Tests: `ComparisonAnalysisTest`, `ExportHelperTest`, `ActivityLogEnhancedTest`,
  `AppSettingsPersistenceTest`, StructureSummary enrichment tests

### Changed
- `ComparisonEntryResult` now carries `durationNanos`
- `ComparisonSession.executeAll()` times each entry
- `ComparisonCardPane.updateResult()` expanded with timing, fastest, divergent params
- `ComparisonSummaryPane.updateAfterOperation()` accepts verdict text/style/timing
- `StructureSummary` expanded with `behavior` and `learningNotes` fields
- `StructLabService` maps `behavior()` and `learningNotes()` in all discovery methods

### Added
- Full project scaffold for StructLab
- `README.md` with project vision, design principles, and roadmap overview
- `docs/roadmap.md` — phased roadmap (Phase 0–7)
- `docs/architecture.md` — layer separation and dependency rules
- `docs/design-principles.md` — seven core principles governing the project
- `docs/future-ideas.md` — ideas for later phases
- `src/main/java/structlab/` package scaffold with `package-info.java` placeholders
- `src/test/java/structlab/` test scaffold
- `examples/` and `scripts/` placeholder directories
- Updated `.gitignore` to cover Java, Maven, Gradle, IntelliJ, Eclipse, VS Code, and OS artefacts
- Updated `CONTRIBUTING.md` with StructLab-specific guidelines

