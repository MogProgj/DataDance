package structlab.core.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Fluent builder for constructing {@link AlgorithmTelemetry} instances.
 * Shared across graph algorithm runners to reduce boilerplate while
 * keeping each algorithm's telemetry semantically distinct.
 */
public final class TelemetryBuilder {

    private String phase = "—";
    private final List<AlgorithmTelemetry.Metric> metrics = new ArrayList<>();
    private final List<AlgorithmTelemetry.Section> sections = new ArrayList<>();
    private final List<String> events = new ArrayList<>();

    private TelemetryBuilder() {}

    public static TelemetryBuilder create(String phase) {
        TelemetryBuilder b = new TelemetryBuilder();
        b.phase = phase;
        return b;
    }

    public TelemetryBuilder metric(String label, String value) {
        metrics.add(new AlgorithmTelemetry.Metric(label, value));
        return this;
    }

    public TelemetryBuilder metric(String label, int value) {
        return metric(label, String.valueOf(value));
    }

    public TelemetryBuilder metric(String label, double value) {
        return metric(label, DijkstraRunner.formatDist(value));
    }

    public TelemetryBuilder section(String title, List<String> items) {
        if (!items.isEmpty()) {
            sections.add(new AlgorithmTelemetry.Section(title, List.copyOf(items)));
        }
        return this;
    }

    public TelemetryBuilder event(String description) {
        events.add(description);
        return this;
    }

    public AlgorithmTelemetry build() {
        return AlgorithmTelemetry.of(phase, metrics, sections, events);
    }
}
