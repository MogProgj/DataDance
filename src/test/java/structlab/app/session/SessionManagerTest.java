package structlab.app.session;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import structlab.app.comparison.ComparisonRuntimeEntry;
import structlab.app.comparison.ComparisonSession;
import structlab.app.runtime.RuntimeFactory;
import structlab.app.runtime.StructureRuntime;
import structlab.app.runtime.adapters.StackRuntimeAdapter;
import structlab.registry.ImplementationMetadata;
import structlab.registry.InMemoryStructureRegistry;
import structlab.registry.RegistrySeeder;
import structlab.registry.StructureMetadata;
import structlab.trace.TracedArrayStack;
import structlab.trace.TraceLog;
import structlab.core.stack.ArrayStack;
import java.util.List;
import java.util.Optional;

public class SessionManagerTest {

    @Test
    public void testSessionActivationAndClose() {
        SessionManager manager = new SessionManager();
        assertTrue(manager.getActiveSession().isEmpty());

        StructureRuntime runtime = new StackRuntimeAdapter("impl-array-stack", new TracedArrayStack<Integer>(new ArrayStack<Integer>(), new TraceLog()));
        ActiveStructureSession session = new ActiveStructureSession("stack", "impl-array-stack", runtime);

        manager.startSession(session);

        assertTrue(manager.hasActiveSession());
        Optional<ActiveStructureSession> activeOpt = manager.getActiveStructureSession();
        assertTrue(activeOpt.isPresent());

        ActiveStructureSession active = activeOpt.get();
        assertEquals("stack", active.getStructureId());
        assertEquals("impl-array-stack", active.getImplementationId());
        assertEquals(runtime, active.getRuntime());

        manager.clearSession();
        assertFalse(manager.hasActiveSession());
        assertTrue(manager.getActiveStructureSession().isEmpty());
    }

    @Test
    public void testComparisonSessionActivation() {
        SessionManager manager = new SessionManager();
        InMemoryStructureRegistry registry = new InMemoryStructureRegistry();
        RegistrySeeder.seed(registry);
        StructureMetadata sm = registry.getStructureById("struct-stack").orElseThrow();
        List<ImplementationMetadata> impls = registry.getImplementationsFor("struct-stack");
        List<ComparisonRuntimeEntry> entries = impls.stream()
                .map(im -> new ComparisonRuntimeEntry(im.id(), im.name(), RuntimeFactory.createRuntime(sm, im)))
                .toList();

        ComparisonSession cs = new ComparisonSession("struct-stack", "Stack", entries);
        manager.startSession(cs);

        assertTrue(manager.hasActiveSession());
        assertTrue(manager.isComparisonMode());
        assertTrue(manager.getComparisonSession().isPresent());
        assertFalse(manager.getActiveStructureSession().isPresent());
    }

    @Test
    public void testComparisonSessionClear() {
        SessionManager manager = new SessionManager();
        InMemoryStructureRegistry registry = new InMemoryStructureRegistry();
        RegistrySeeder.seed(registry);
        StructureMetadata sm = registry.getStructureById("struct-stack").orElseThrow();
        List<ImplementationMetadata> impls = registry.getImplementationsFor("struct-stack");
        List<ComparisonRuntimeEntry> entries = impls.stream()
                .map(im -> new ComparisonRuntimeEntry(im.id(), im.name(), RuntimeFactory.createRuntime(sm, im)))
                .toList();

        ComparisonSession cs = new ComparisonSession("struct-stack", "Stack", entries);
        manager.startSession(cs);
        manager.clearSession();

        assertFalse(manager.hasActiveSession());
        assertFalse(manager.isComparisonMode());
        assertTrue(manager.getComparisonSession().isEmpty());
    }

    @Test
    public void testSwitchFromSingleToComparisonSession() {
        SessionManager manager = new SessionManager();
        InMemoryStructureRegistry registry = new InMemoryStructureRegistry();
        RegistrySeeder.seed(registry);

        // Start a single session first
        StructureRuntime runtime = new StackRuntimeAdapter("impl-array-stack", new TracedArrayStack<Integer>(new ArrayStack<Integer>(), new TraceLog()));
        ActiveStructureSession single = new ActiveStructureSession("stack", "impl-array-stack", runtime);
        manager.startSession(single);
        assertFalse(manager.isComparisonMode());

        // Switch to comparison
        StructureMetadata sm = registry.getStructureById("struct-stack").orElseThrow();
        List<ImplementationMetadata> impls = registry.getImplementationsFor("struct-stack");
        List<ComparisonRuntimeEntry> entries = impls.stream()
                .map(im -> new ComparisonRuntimeEntry(im.id(), im.name(), RuntimeFactory.createRuntime(sm, im)))
                .toList();
        ComparisonSession cs = new ComparisonSession("struct-stack", "Stack", entries);
        manager.startSession(cs);

        assertTrue(manager.isComparisonMode());
        assertTrue(manager.getComparisonSession().isPresent());
        assertFalse(manager.getActiveStructureSession().isPresent());
    }
}
