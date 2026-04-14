package structlab.core.graph;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TelemetryBuilderTest {

    @Test
    void createSetsPhase() {
        AlgorithmTelemetry t = TelemetryBuilder.create("Init").build();
        assertEquals("Init", t.phase());
        assertTrue(t.metrics().isEmpty());
        assertTrue(t.sections().isEmpty());
        assertTrue(t.events().isEmpty());
    }

    @Test
    void metricWithString() {
        AlgorithmTelemetry t = TelemetryBuilder.create("P")
                .metric("Label", "Value")
                .build();
        assertEquals(1, t.metrics().size());
        assertEquals("Label", t.metrics().get(0).label());
        assertEquals("Value", t.metrics().get(0).value());
    }

    @Test
    void metricWithInt() {
        AlgorithmTelemetry t = TelemetryBuilder.create("P")
                .metric("Count", 42)
                .build();
        assertEquals("42", t.metrics().get(0).value());
    }

    @Test
    void metricWithDouble() {
        AlgorithmTelemetry t = TelemetryBuilder.create("P")
                .metric("Dist", 3.5)
                .build();
        assertEquals("3.5", t.metrics().get(0).value());
    }

    @Test
    void metricWithInfinity() {
        AlgorithmTelemetry t = TelemetryBuilder.create("P")
                .metric("Dist", DijkstraRunner.INF)
                .build();
        assertEquals("\u221e", t.metrics().get(0).value());
    }

    @Test
    void sectionAddsNonEmpty() {
        AlgorithmTelemetry t = TelemetryBuilder.create("P")
                .section("Frontier", List.of("A", "B"))
                .build();
        assertEquals(1, t.sections().size());
        assertEquals("Frontier", t.sections().get(0).title());
        assertEquals(List.of("A", "B"), t.sections().get(0).items());
    }

    @Test
    void sectionSkipsEmpty() {
        AlgorithmTelemetry t = TelemetryBuilder.create("P")
                .section("Empty", List.of())
                .build();
        assertTrue(t.sections().isEmpty());
    }

    @Test
    void eventAdded() {
        AlgorithmTelemetry t = TelemetryBuilder.create("P")
                .event("Relaxed A→B")
                .build();
        assertEquals(List.of("Relaxed A→B"), t.events());
    }

    @Test
    void fluentChaining() {
        AlgorithmTelemetry t = TelemetryBuilder.create("Extract-Min")
                .metric("Node", "A")
                .metric("Distance", 5)
                .section("PQ", List.of("B(7)", "C(10)"))
                .event("Settled A")
                .build();

        assertEquals("Extract-Min", t.phase());
        assertEquals(2, t.metrics().size());
        assertEquals(1, t.sections().size());
        assertEquals(1, t.events().size());
    }
}
