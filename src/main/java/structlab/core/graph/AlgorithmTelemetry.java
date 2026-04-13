package structlab.core.graph;

import java.util.List;

/**
 * Typed telemetry snapshot for a single algorithm frame.
 * Provides structured data that the Algorithm Tracker UI can display
 * without needing algorithm-specific if/else logic.
 */
public record AlgorithmTelemetry(
        /** Current algorithm phase, e.g. "Initialization", "Relaxation", "Complete". */
        String phase,
        /** Key-value metrics, e.g. "MST Weight" -> "12", "Settled" -> "3/7". */
        List<Metric> metrics,
        /** Titled sections with item lists. */
        List<Section> sections,
        /** What changed in this step. */
        List<String> events
) {

    public record Metric(String label, String value) {}

    public record Section(String title, List<String> items) {}

    public static AlgorithmTelemetry of(String phase, List<Metric> metrics,
                                         List<Section> sections, List<String> events) {
        return new AlgorithmTelemetry(phase, List.copyOf(metrics),
                List.copyOf(sections), List.copyOf(events));
    }

    public static AlgorithmTelemetry empty() {
        return new AlgorithmTelemetry("\u2014", List.of(), List.of(), List.of());
    }
}
