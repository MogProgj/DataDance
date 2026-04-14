package structlab.app.comparison;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import structlab.app.runtime.RuntimeFactory;
import structlab.app.runtime.StructureRuntime;
import structlab.registry.ImplementationMetadata;
import structlab.registry.InMemoryStructureRegistry;
import structlab.registry.RegistrySeeder;
import structlab.registry.StructureMetadata;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ComparisonRendererTest {

    private ComparisonSession session;

    @BeforeEach
    void setUp() {
        InMemoryStructureRegistry registry = new InMemoryStructureRegistry();
        RegistrySeeder.seed(registry);
        StructureMetadata sm = registry.getStructureById("struct-stack").orElseThrow();
        List<ImplementationMetadata> impls = registry.getImplementationsFor("struct-stack");
        List<ComparisonRuntimeEntry> entries = impls.stream()
                .map(im -> new ComparisonRuntimeEntry(im.id(), im.name(), RuntimeFactory.createRuntime(sm, im)))
                .toList();
        session = new ComparisonSession("struct-stack", "Stack", entries);
    }

    @Test
    void renderOperationResultContainsHeader() {
        ComparisonOperationResult result = session.executeAll("push", List.of("42"));
        String rendered = ComparisonRenderer.renderOperationResult(result);
        assertNotNull(rendered);
        assertTrue(rendered.contains("Compare:"));
        assertTrue(rendered.contains("push"));
    }

    @Test
    void renderOperationResultShowsAllImplementations() {
        ComparisonOperationResult result = session.executeAll("push", List.of("10"));
        String rendered = ComparisonRenderer.renderOperationResult(result);
        for (ComparisonRuntimeEntry entry : session.getEntries()) {
            assertTrue(rendered.contains(entry.getImplementationName()),
                    "Rendered output should contain " + entry.getImplementationName());
        }
    }

    @Test
    void renderStatesShowsAllImplementations() {
        session.executeAll("push", List.of("1"));
        String rendered = ComparisonRenderer.renderStates(session);
        assertNotNull(rendered);
        assertTrue(rendered.contains("Comparison States"));
        for (ComparisonRuntimeEntry entry : session.getEntries()) {
            assertTrue(rendered.contains(entry.getImplementationName()));
        }
    }

    @Test
    void renderStatesOnEmptySession() {
        String rendered = ComparisonRenderer.renderStates(session);
        assertNotNull(rendered);
        // Should still show the structure entries even when empty
        assertTrue(rendered.contains("[1]"));
        assertTrue(rendered.contains("[2]"));
    }

    @Test
    void renderTracesWhenNoHistory() {
        String rendered = ComparisonRenderer.renderTraces(session);
        assertEquals("No comparison operations executed yet.", rendered);
    }

    @Test
    void renderTracesAfterOperation() {
        session.executeAll("push", List.of("5"));
        String rendered = ComparisonRenderer.renderTraces(session);
        assertNotNull(rendered);
        assertTrue(rendered.contains("Comparison Traces"));
        assertTrue(rendered.contains("push"));
    }

    @Test
    void renderSessionInfo() {
        String rendered = ComparisonRenderer.renderSessionInfo(session);
        assertNotNull(rendered);
        assertTrue(rendered.contains("Stack"));
        assertTrue(rendered.contains("struct-stack"));
        assertTrue(rendered.contains("Implementations:"));
    }

    @Test
    void renderHistoryWhenEmpty() {
        String rendered = ComparisonRenderer.renderHistory(session);
        assertEquals("No comparison operations executed yet.", rendered);
    }

    @Test
    void renderHistoryAfterOperations() {
        session.executeAll("push", List.of("1"));
        session.executeAll("push", List.of("2"));
        String rendered = ComparisonRenderer.renderHistory(session);
        assertNotNull(rendered);
        assertTrue(rendered.contains("[OK]"));
        assertTrue(rendered.contains("push 1"));
        assertTrue(rendered.contains("push 2"));
    }

    @Test
    void renderOperationResultShowsReturnedValues() {
        session.executeAll("push", List.of("42"));
        ComparisonOperationResult result = session.executeAll("peek", List.of());
        String rendered = ComparisonRenderer.renderOperationResult(result);
        assertTrue(rendered.contains("42"), "Should show returned value 42");
    }
}
