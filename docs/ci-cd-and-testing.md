# CI/CD and Testing

---

## GitHub Actions pipeline

The CI pipeline is defined in `.github/workflows/ci.yml` and runs on
every push to `main` and `Gemini` and on pull requests.

### verify job

- **Runs on:** ubuntu-latest
- **JDK:** Temurin 17
- **Command:** `xvfb-run --auto-servernum ./mvnw -B -ntp verify`
- **Xvfb:** Wraps the Maven command to provide a virtual X11 display
  so JavaFX `Platform.startup()` works on headless CI
- **Artifacts:** JaCoCo coverage report (14-day retention)

### package job

- **Depends on:** verify
- **Condition:** push events only (not PRs)
- **Command:** `./mvnw -B -ntp package -DskipTests -Djacoco.skip=true`
- **Artifacts:** `structlab-*.jar` uber-JAR (30-day retention)

### Concurrency

In-flight CI for the same branch is cancelled when a new push arrives.

---

## Test suite

### Current state

- **980+ tests**, 0 failures (as of Phase 5A)
- **88 test files** under `src/test/java/`

### Test layers

| Layer | What it covers | Example test class |
|-------|----------------|-------------------|
| Core | Data structure correctness | `DynamicArrayTest`, `BinaryHeapTest` |
| Trace | Traced wrapper accuracy | `TracedArrayStackTest`, `TracedLinkedQueueTest` |
| Render | Snapshot parsing, ASCII output | `SnapshotParserTest`, `StructureRendererTest` |
| Service | Facade correctness | `StructLabServiceTest` |
| Command | Terminal command parsing, routing | `CommandParserTest`, `CommandRouterTest` |
| Session | Session lifecycle | `SessionManagerTest`, `ActiveStructureSessionTest` |
| Runtime | Runtime adapter factory | `RuntimeFactoryTest` |
| Registry | Metadata store | `InMemoryStructureRegistryTest` |
| Visual state | State model parsing | `StateModelParserTest`, `StateModelParserHashTest` |
| Visual panes | JavaFX pane rendering | `StackVisualPaneTest`, `HeapVisualPaneTest`, etc. |
| Factory | Visual factory dispatch | `VisualStateFactoryTest` |
| Comparison | Comparison card/summary panes, analysis | `ComparisonCardPaneTest`, `ComparisonAnalysisTest` |
| Operations | Canonical operation registry | `CanonicalOperationRegistryTest` |
| Export | Compare + Activity export helpers | `ExportHelperTest` |
| Settings | Preferences persistence | `AppSettingsPersistenceTest`, `AppSettingsTest` |
| Activity | Activity log filtering/clear | `ActivityLogEnhancedTest` |

### JavaFX test handling

All 13 JavaFX visual pane test classes use a shared
`@ExtendWith(JavaFxToolkitExtension.class)` that initializes the
JavaFX toolkit once per JVM via the root extension store.

Safety measures:
- `@Timeout(10)` on all JavaFX test classes (10-second per-test safety net)
- `JavaFxToolkitExtension` uses a `CountDownLatch` with 5-second timeout
  on `Platform.startup()` to prevent hangs
- CI uses `xvfb-run --auto-servernum` to provide a virtual display

---

## Coverage

JaCoCo coverage reports are generated on every `mvn verify` run and
uploaded as CI artifacts.  Reports are available for 14 days.

---

## Local test guidance

### Run all tests
```bash
mvn test
```

### Run a specific test class
```bash
mvn test -Dtest=StackVisualPaneTest
```

### Run GUI manually
```bash
mvn javafx:run
```

### Run terminal simulator
```bash
mvn compile exec:java "-Dexec.mainClass=structlab.app.StructLabApp"
```

### JavaFX tests on local machines
Local machines with a display server (Windows, macOS, Linux with X11)
run JavaFX tests natively — no `xvfb-run` needed.
