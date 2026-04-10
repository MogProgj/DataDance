package structlab.gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import structlab.app.comparison.ComparisonOperationResult;
import structlab.app.comparison.ComparisonRuntimeEntry;
import structlab.app.comparison.ComparisonSession;
import structlab.app.runtime.RuntimeFactory;
import structlab.registry.ImplementationMetadata;
import structlab.registry.InMemoryStructureRegistry;
import structlab.registry.RegistrySeeder;
import structlab.registry.StructureMetadata;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GuiComparisonRendererTest {

    private InMemoryStructureRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new InMemoryStructureRegistry();
        RegistrySeeder.seed(registry);
    }

    private ComparisonSession openStackComparison() {
        StructureMetadata sm = registry.getStructureById("struct-stack").orElseThrow();
        List<ImplementationMetadata> impls = registry.getImplementationsFor("struct-stack");
        List<ComparisonRuntimeEntry> entries = impls.stream()
                .map(im -> new ComparisonRuntimeEntry(im.id(), im.name(),
                        RuntimeFactory.createRuntime(sm, im)))
                .toList();
        return new ComparisonSession("struct-stack", "Stack", entries);
    }

    // ── renderStates ────────────────────────────────────────

    @Nested
    class RenderStatesTests {

        @Test
        void containsNoAnsiCodes() {
            ComparisonSession cs = openStackComparison();
            String rendered = GuiComparisonRenderer.renderStates(cs);
            assertFalse(rendered.contains("\u001B"), "Should not contain ANSI escape codes");
        }

        @Test
        void showsAllImplementations() {
            ComparisonSession cs = openStackComparison();
            String rendered = GuiComparisonRenderer.renderStates(cs);
            for (ComparisonRuntimeEntry entry : cs.getEntries()) {
                assertTrue(rendered.contains(entry.getImplementationName()),
                        "Should contain " + entry.getImplementationName());
            }
        }

        @Test
        void containsSectionHeader() {
            ComparisonSession cs = openStackComparison();
            String rendered = GuiComparisonRenderer.renderStates(cs);
            assertTrue(rendered.contains("Comparison States"));
        }

        @Test
        void showsIndexNumbers() {
            ComparisonSession cs = openStackComparison();
            String rendered = GuiComparisonRenderer.renderStates(cs);
            assertTrue(rendered.contains("[1]"));
            assertTrue(rendered.contains("[2]"));
        }
    }

    // ── renderCompactTraces ─────────────────────────────────

    @Nested
    class RenderCompactTracesTests {

        @Test
        void noHistoryShowsMessage() {
            ComparisonSession cs = openStackComparison();
            String rendered = GuiComparisonRenderer.renderCompactTraces(cs);
            assertEquals("No comparison operations executed yet.", rendered);
        }

        @Test
        void afterOperationShowsCompactSummary() {
            ComparisonSession cs = openStackComparison();
            cs.executeAll("push", List.of("42"));
            String rendered = GuiComparisonRenderer.renderCompactTraces(cs);

            assertFalse(rendered.contains("\u001B"), "Should not contain ANSI codes");
            assertTrue(rendered.contains("push"));
            assertTrue(rendered.contains("✔"));
            // Should show step count
            assertTrue(rendered.contains("step"), "Should mention trace steps");
        }

        @Test
        void afterOperationShowsReturnedValue() {
            ComparisonSession cs = openStackComparison();
            cs.executeAll("push", List.of("42"));
            cs.executeAll("peek", List.of());
            String rendered = GuiComparisonRenderer.renderCompactTraces(cs);

            assertTrue(rendered.contains("42"), "Should show returned value");
        }

        @Test
        void failedOperationShowsCross() {
            ComparisonSession cs = openStackComparison();
            cs.executeAll("pop", List.of()); // pop on empty stack
            String rendered = GuiComparisonRenderer.renderCompactTraces(cs);

            assertTrue(rendered.contains("✖"), "Should show failure icon");
        }

        @Test
        void compactTraceIsShort() {
            ComparisonSession cs = openStackComparison();
            cs.executeAll("push", List.of("10"));
            String compact = GuiComparisonRenderer.renderCompactTraces(cs);

            // Compact trace should be much shorter than a full trace dump
            // It should NOT contain the full before/after states for every step
            assertFalse(compact.contains("BEFORE:"), "Compact trace should not have full step details");
            assertFalse(compact.contains("AFTER:"), "Compact trace should not have full step details");
        }

        @Test
        void showsAllImplementationNames() {
            ComparisonSession cs = openStackComparison();
            cs.executeAll("push", List.of("5"));
            String rendered = GuiComparisonRenderer.renderCompactTraces(cs);

            for (ComparisonRuntimeEntry entry : cs.getEntries()) {
                assertTrue(rendered.contains(entry.getImplementationName()),
                        "Should contain " + entry.getImplementationName());
            }
        }
    }

    // ── renderOperationResult ───────────────────────────────

    @Nested
    class RenderOperationResultTests {

        @Test
        void containsNoAnsiCodes() {
            ComparisonSession cs = openStackComparison();
            ComparisonOperationResult result = cs.executeAll("push", List.of("1"));
            String rendered = GuiComparisonRenderer.renderOperationResult(result);
            assertFalse(rendered.contains("\u001B"), "Should not contain ANSI codes");
        }

        @Test
        void containsOperationName() {
            ComparisonSession cs = openStackComparison();
            ComparisonOperationResult result = cs.executeAll("push", List.of("42"));
            String rendered = GuiComparisonRenderer.renderOperationResult(result);
            assertTrue(rendered.contains("push"));
        }

        @Test
        void showsAllImplementations() {
            ComparisonSession cs = openStackComparison();
            ComparisonOperationResult result = cs.executeAll("push", List.of("1"));
            String rendered = GuiComparisonRenderer.renderOperationResult(result);
            for (ComparisonRuntimeEntry entry : cs.getEntries()) {
                assertTrue(rendered.contains(entry.getImplementationName()));
            }
        }

        @Test
        void showsSuccessCheck() {
            ComparisonSession cs = openStackComparison();
            ComparisonOperationResult result = cs.executeAll("push", List.of("1"));
            String rendered = GuiComparisonRenderer.renderOperationResult(result);
            assertTrue(rendered.contains("✔"));
        }
    }
}
