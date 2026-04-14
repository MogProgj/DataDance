package structlab.core.graph;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlgorithmTelemetryTest {

    @Test
    void ofCopiesCollections() {
        var metrics = List.of(new AlgorithmTelemetry.Metric("Dist", "5"));
        var sections = List.of(new AlgorithmTelemetry.Section("PQ", List.of("A(3)", "B(7)")));
        var events = List.of("Relaxed A→B");

        AlgorithmTelemetry t = AlgorithmTelemetry.of("Relaxation", metrics, sections, events);

        assertEquals("Relaxation", t.phase());
        assertEquals(1, t.metrics().size());
        assertEquals("Dist", t.metrics().get(0).label());
        assertEquals("5", t.metrics().get(0).value());
        assertEquals(1, t.sections().size());
        assertEquals("PQ", t.sections().get(0).title());
        assertEquals(List.of("A(3)", "B(7)"), t.sections().get(0).items());
        assertEquals(List.of("Relaxed A→B"), t.events());
    }

    @Test
    void ofReturnsDefensiveCopies() {
        var metrics = new java.util.ArrayList<>(List.of(
                new AlgorithmTelemetry.Metric("X", "1")));
        var sections = new java.util.ArrayList<AlgorithmTelemetry.Section>();
        var events = new java.util.ArrayList<>(List.of("ev"));

        AlgorithmTelemetry t = AlgorithmTelemetry.of("Phase", metrics, sections, events);

        // Mutating the original lists should not affect the telemetry
        metrics.clear();
        events.clear();
        assertEquals(1, t.metrics().size());
        assertEquals(1, t.events().size());
    }

    @Test
    void emptyHasDashPhaseAndEmptyCollections() {
        AlgorithmTelemetry t = AlgorithmTelemetry.empty();

        assertEquals("\u2014", t.phase());
        assertTrue(t.metrics().isEmpty());
        assertTrue(t.sections().isEmpty());
        assertTrue(t.events().isEmpty());
    }

    @Test
    void metricRecordEquality() {
        var m1 = new AlgorithmTelemetry.Metric("Weight", "12");
        var m2 = new AlgorithmTelemetry.Metric("Weight", "12");
        assertEquals(m1, m2);
    }

    @Test
    void sectionRecordEquality() {
        var s1 = new AlgorithmTelemetry.Section("Queue", List.of("A", "B"));
        var s2 = new AlgorithmTelemetry.Section("Queue", List.of("A", "B"));
        assertEquals(s1, s2);
    }
}
