package structlab.gui.controller;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import structlab.core.graph.AlgorithmFrame;
import structlab.core.graph.AlgorithmTelemetry;

import static structlab.gui.visual.UiComponents.styledLabel;

/**
 * Left-panel tracker widget that displays structured telemetry for the
 * current algorithm frame.  Reads the {@link AlgorithmTelemetry} attached
 * to each {@link AlgorithmFrame} and renders phase, metrics, sections,
 * and events without algorithm-specific branching.
 *
 * <p>When no telemetry is available, falls back to a compact summary
 * derived from the frame's generic fields.</p>
 */
public class AlgorithmTrackerPane extends VBox {

    private final Label phaseLabel;
    private final VBox metricsBox;
    private final VBox sectionsBox;
    private final VBox eventsBox;
    private boolean defaultExpanded;

    public AlgorithmTrackerPane() {
        this(false);
    }

    public AlgorithmTrackerPane(boolean defaultExpanded) {
        this.defaultExpanded = defaultExpanded;
        setSpacing(6);
        getStyleClass().add("algo-tracker-pane");
        setPadding(new Insets(8, 0, 8, 0));

        Label header = styledLabel("ALGORITHM TRACKER", "section-header");

        phaseLabel = new Label("\u2014");
        phaseLabel.getStyleClass().add("algo-tracker-phase");
        phaseLabel.setWrapText(true);

        metricsBox = new VBox(2);
        metricsBox.getStyleClass().add("algo-tracker-metrics");

        sectionsBox = new VBox(4);
        sectionsBox.getStyleClass().add("algo-tracker-sections");

        eventsBox = new VBox(2);
        eventsBox.getStyleClass().add("algo-tracker-events");

        getChildren().addAll(header, phaseLabel, new Separator(),
                metricsBox, sectionsBox, eventsBox);
    }

    /** Updates the tracker with data from the given frame. */
    public void update(AlgorithmFrame frame) {
        if (frame == null) {
            clear();
            return;
        }

        AlgorithmTelemetry telemetry = frame.telemetry();
        if (telemetry != null) {
            renderTelemetry(telemetry);
        } else {
            renderFallback(frame);
        }
    }

    /** Resets the tracker to its empty state. */
    public void clear() {
        phaseLabel.setText("\u2014");
        metricsBox.getChildren().clear();
        sectionsBox.getChildren().clear();
        eventsBox.getChildren().clear();
    }

    // ── Rendering ───────────────────────────────────────────

    private void renderTelemetry(AlgorithmTelemetry t) {
        phaseLabel.setText(t.phase());

        metricsBox.getChildren().clear();
        for (AlgorithmTelemetry.Metric m : t.metrics()) {
            Label lbl = styledLabel(m.label() + ": " + m.value(), "algo-tracker-metric");
            metricsBox.getChildren().add(lbl);
        }

        sectionsBox.getChildren().clear();
        for (AlgorithmTelemetry.Section s : t.sections()) {
            VBox sectionContent = new VBox(1);
            for (String item : s.items()) {
                sectionContent.getChildren().add(
                        styledLabel(item, "algo-tracker-item"));
            }
            TitledPane tp = new TitledPane(s.title(), sectionContent);
            tp.setExpanded(defaultExpanded);
            tp.getStyleClass().add("algo-tracker-section-pane");
            sectionsBox.getChildren().add(tp);
        }

        eventsBox.getChildren().clear();
        for (String event : t.events()) {
            eventsBox.getChildren().add(
                    styledLabel("\u25b8 " + event, "algo-tracker-event"));
        }
    }

    private void renderFallback(AlgorithmFrame frame) {
        phaseLabel.setText(frame.statusMessage() != null
                ? frame.statusMessage() : "\u2014");

        metricsBox.getChildren().clear();
        metricsBox.getChildren().add(styledLabel(
                "Step: " + frame.stepIndex(), "algo-tracker-metric"));
        metricsBox.getChildren().add(styledLabel(
                "Visited: " + frame.visited().size(), "algo-tracker-metric"));
        if (!frame.frontier().isEmpty()) {
            metricsBox.getChildren().add(styledLabel(
                    "Frontier: " + frame.frontier().size(), "algo-tracker-metric"));
        }

        sectionsBox.getChildren().clear();
        if (!frame.discoveryOrder().isEmpty()) {
            VBox discContent = new VBox(1);
            discContent.getChildren().add(styledLabel(
                    String.join(" \u2192 ", frame.discoveryOrder()),
                    "algo-tracker-item"));
            TitledPane tp = new TitledPane("Discovery Order", discContent);
            tp.setExpanded(defaultExpanded);
            tp.getStyleClass().add("algo-tracker-section-pane");
            sectionsBox.getChildren().add(tp);
        }

        eventsBox.getChildren().clear();
        if (frame.currentNode() != null) {
            eventsBox.getChildren().add(styledLabel(
                    "\u25b8 Processing: " + frame.currentNode(),
                    "algo-tracker-event"));
        }
    }
}
