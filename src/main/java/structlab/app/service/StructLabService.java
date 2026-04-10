package structlab.app.service;

import structlab.app.comparison.ComparisonOperationResult;
import structlab.app.comparison.ComparisonRuntimeEntry;
import structlab.app.comparison.ComparisonSession;
import structlab.app.runtime.OperationDescriptor;
import structlab.app.runtime.OperationExecutionResult;
import structlab.app.runtime.RuntimeFactory;
import structlab.app.runtime.StructureRuntime;
import structlab.app.session.ActiveStructureSession;
import structlab.app.session.SessionManager;
import structlab.registry.ImplementationMetadata;
import structlab.registry.InMemoryStructureRegistry;
import structlab.registry.RegistrySeeder;
import structlab.registry.StructureMetadata;
import structlab.registry.StructureRegistry;
import structlab.trace.TraceStep;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Facade layer for GUI and programmatic consumers.
 * Wraps the registry, session, and runtime model into clean method calls
 * without depending on shell command parsing.
 *
 * Session ownership is delegated to {@link SessionManager} so that both the
 * terminal shell and the GUI share a single session abstraction.
 */
public class StructLabService {

    private final StructureRegistry registry;
    private final SessionManager sessionManager;

    public StructLabService(StructureRegistry registry, SessionManager sessionManager) {
        this.registry = registry;
        this.sessionManager = sessionManager;
    }

    public static StructLabService createDefault() {
        InMemoryStructureRegistry registry = new InMemoryStructureRegistry();
        RegistrySeeder.seed(registry);
        return new StructLabService(registry, new SessionManager());
    }

    // ── Discovery ──────────────────────────────────────────────

    public List<StructureSummary> getAllStructures() {
        return registry.getAllStructures().stream()
                .map(m -> new StructureSummary(m.id(), m.name(), m.category(), m.keywords(), m.description()))
                .toList();
    }

    public Optional<StructureSummary> getStructure(String structureId) {
        String id = normalizeStructureId(structureId);
        return registry.getStructureById(id)
                .map(m -> new StructureSummary(m.id(), m.name(), m.category(), m.keywords(), m.description()));
    }

    public List<ImplementationSummary> getImplementations(String structureId) {
        String id = normalizeStructureId(structureId);
        return registry.getImplementationsFor(id).stream()
                .map(im -> new ImplementationSummary(
                        im.id(), im.name(), im.parentStructureId(),
                        im.description(), im.timeComplexity(), im.spaceComplexity()))
                .toList();
    }

    // ── Session lifecycle ──────────────────────────────────────

    public boolean hasActiveSession() {
        return sessionManager.getActiveStructureSession().isPresent();
    }

    public SessionSnapshot openSession(String structureId, String implementationId) {
        String sId = normalizeStructureId(structureId);
        String iId = normalizeImplementationId(implementationId);

        Optional<StructureMetadata> smOpt = registry.getStructureById(sId);
        if (smOpt.isEmpty()) {
            throw new IllegalArgumentException("Structure not found: " + sId);
        }

        List<ImplementationMetadata> impls = registry.getImplementationsFor(sId);
        Optional<ImplementationMetadata> imOpt = impls.stream()
                .filter(im -> im.id().equals(iId))
                .findFirst();
        if (imOpt.isEmpty()) {
            throw new IllegalArgumentException("Implementation not found: " + iId);
        }

        StructureRuntime runtime = RuntimeFactory.createRuntime(smOpt.get(), imOpt.get());
        ActiveStructureSession session = new ActiveStructureSession(sId, iId, runtime);
        sessionManager.startSession(session);
        return getSessionSnapshot().orElseThrow();
    }

    public void closeSession() {
        sessionManager.clearSession();
    }

    public Optional<SessionSnapshot> getSessionSnapshot() {
        return sessionManager.getActiveStructureSession().map(s ->
                new SessionSnapshot(
                        s.getStructureId(),
                        s.getImplementationId(),
                        s.getRuntime().getStructureName(),
                        s.getRuntime().getImplementationName(),
                        s.historySize()
                ));
    }

    // ── Operations ─────────────────────────────────────────────

    public List<OperationInfo> getAvailableOperations() {
        ActiveStructureSession session = requireSession();
        return session.getRuntime().getAvailableOperations().stream()
                .map(o -> new OperationInfo(
                        o.name(), o.aliases(), o.description(),
                        o.argCount(), o.usage(), o.mutates(), o.complexityNote()))
                .toList();
    }

    public ExecutionResult executeOperation(String operation, List<String> args) {
        ActiveStructureSession session = requireSession();
        OperationExecutionResult result = session.getRuntime().execute(operation, args);
        session.addHistory(result);
        return toExecutionResult(result);
    }

    // ── State and history ──────────────────────────────────────

    public String getRenderedState() {
        return requireSession().getRuntime().renderCurrentState();
    }

    public String getRawState() {
        return requireSession().getRuntime().getCurrentState();
    }

    public List<ExecutionResult> getHistory() {
        return requireSession().getHistory().stream()
                .map(this::toExecutionResult)
                .toList();
    }

    public Optional<ExecutionResult> getLastResult() {
        return requireSession().getLastResult().map(this::toExecutionResult);
    }

    public List<TraceStep> getLastTraceSteps() {
        return requireSession().getLastResult()
                .filter(r -> r.traceSteps() != null)
                .map(r -> List.copyOf(r.traceSteps()))
                .orElse(List.of());
    }

    public String getLastTraceRendered() {
        List<TraceStep> steps = getLastTraceSteps();
        if (steps.isEmpty()) return "No trace steps available.";
        StringBuilder sb = new StringBuilder();
        for (TraceStep step : steps) {
            sb.append(structlab.render.ConsoleTraceRenderer.render(step)).append("\n");
        }
        return sb.toString().trim();
    }

    // ── Reset ──────────────────────────────────────────────────

    public void resetSession() {
        ActiveStructureSession session = requireSession();
        session.getRuntime().reset();
        session.clearHistory();
    }

    // ── Comparison mode ────────────────────────────────────────

    /**
     * List structure families that have 2+ implementations eligible for comparison.
     */
    public List<StructureSummary> getComparableStructures() {
        return registry.getAllStructures().stream()
                .filter(m -> registry.getImplementationsFor(m.id()).size() >= 2)
                .map(m -> new StructureSummary(m.id(), m.name(), m.category(), m.keywords(), m.description()))
                .toList();
    }

    /**
     * Open a comparison session for a structure family with the given implementation IDs.
     * If implIds is empty, auto-groups implementations by their operation signatures
     * and selects the largest compatible group (>= 2 implementations).
     */
    public ComparisonSession openComparisonSession(String structureId, List<String> implIds) {
        String sId = normalizeStructureId(structureId);

        StructureMetadata sm = registry.getStructureById(sId)
                .orElseThrow(() -> new IllegalArgumentException("Structure not found: " + sId));

        List<ImplementationMetadata> allImpls = registry.getImplementationsFor(sId);
        if (allImpls.size() < 2) {
            throw new IllegalArgumentException("Structure '" + sId + "' has fewer than 2 implementations.");
        }

        List<ImplementationMetadata> selected;
        if (implIds == null || implIds.isEmpty()) {
            selected = selectLargestCompatibleGroup(sm, allImpls);
        } else {
            selected = new ArrayList<>();
            for (String rawId : implIds) {
                String iId = normalizeImplementationId(rawId);
                ImplementationMetadata found = allImpls.stream()
                        .filter(im -> im.id().equals(iId))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Implementation not found: " + iId));
                selected.add(found);
            }
            if (selected.size() < 2) {
                throw new IllegalArgumentException("Comparison requires at least 2 implementations.");
            }
        }

        List<ComparisonRuntimeEntry> entries = new ArrayList<>();
        for (ImplementationMetadata im : selected) {
            StructureRuntime runtime = RuntimeFactory.createRuntime(sm, im);
            entries.add(new ComparisonRuntimeEntry(im.id(), im.name(), runtime));
        }

        ComparisonSession session = new ComparisonSession(sId, sm.name(), entries);
        sessionManager.startSession(session);
        return session;
    }

    public boolean isComparisonMode() {
        return sessionManager.isComparisonMode();
    }

    /**
     * Groups implementations by their operation-name signature and returns
     * the largest group containing at least 2 implementations.
     * Falls back to all implementations if every group has only 1 member.
     */
    private List<ImplementationMetadata> selectLargestCompatibleGroup(
            StructureMetadata sm, List<ImplementationMetadata> impls) {

        // Build a map: operation-name-set -> list of impls sharing that set
        Map<Set<String>, List<ImplementationMetadata>> groups = new LinkedHashMap<>();
        for (ImplementationMetadata im : impls) {
            StructureRuntime tmp = RuntimeFactory.createRuntime(sm, im);
            Set<String> opNames = tmp.getAvailableOperations().stream()
                    .map(OperationDescriptor::name)
                    .collect(Collectors.toCollection(TreeSet::new));
            groups.computeIfAbsent(opNames, k -> new ArrayList<>()).add(im);
        }

        // Pick the largest group with >= 2 members
        List<ImplementationMetadata> best = groups.values().stream()
                .filter(g -> g.size() >= 2)
                .max(Comparator.comparingInt(List::size))
                .orElse(impls);  // fallback to all if no group qualifies

        return best;
    }


    public ComparisonSession requireComparisonSession() {
        return sessionManager.getComparisonSession()
                .orElseThrow(() -> new IllegalStateException("No active comparison session."));
    }

    public ComparisonOperationResult executeComparisonOperation(String operation, List<String> args) {
        return requireComparisonSession().executeAll(operation, args);
    }

    public void resetComparisonSession() {
        requireComparisonSession().resetAll();
    }

    // ── Internal ───────────────────────────────────────────────

    private ActiveStructureSession requireSession() {
        return sessionManager.getActiveStructureSession()
                .orElseThrow(() -> new IllegalStateException("No active session. Open a session first."));
    }

    private ExecutionResult toExecutionResult(OperationExecutionResult r) {
        return new ExecutionResult(
                r.success(), r.operationName(), r.message(),
                r.returnedValue(), r.traceSteps() != null ? List.copyOf(r.traceSteps()) : List.of());
    }

    private String normalizeStructureId(String id) {
        return id.startsWith("struct-") ? id : "struct-" + id;
    }

    private String normalizeImplementationId(String id) {
        return id.startsWith("impl-") ? id : "impl-" + id;
    }
}
