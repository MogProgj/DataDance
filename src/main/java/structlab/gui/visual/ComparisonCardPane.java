package structlab.gui.visual;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;

/**
 * Visual comparison card for one implementation within Compare mode.
 * Shows implementation name, status badge, returned value, visual state
 * (if supported), and an expandable trace detail section.
 * <p>Uses {@link VisualStateHost} for the state display area, which
 * automatically renders visual panes for supported structure types
 * and falls back to text for unsupported ones.</p>
 */
public class ComparisonCardPane extends VBox {

    private final Label nameLabel;
    private final Label statusBadge;
    private final Label returnedLabel;
    private final Label opsLabel;
    private final Label timingLabel;
    private final VisualStateHost visualHost;
    private final VBox traceSection;
    private final TextArea traceArea;
    private final Label traceToggle;
    private boolean traceExpanded = false;

    public ComparisonCardPane() {
        getStyleClass().add("comparison-card");
        setSpacing(0);

        // ── Header row ──────────────────────────────
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("comparison-card-header");

        nameLabel = new Label("Implementation");
        nameLabel.getStyleClass().add("comparison-card-name");
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        statusBadge = new Label("IDLE");
        statusBadge.getStyleClass().addAll("comparison-status-badge", "comparison-status-idle");

        header.getChildren().addAll(nameLabel, statusBadge);

        // ── Metrics row ─────────────────────────────
        HBox metrics = new HBox(16);
        metrics.setAlignment(Pos.CENTER_LEFT);
        metrics.getStyleClass().add("comparison-card-metrics");

        returnedLabel = new Label("");
        returnedLabel.getStyleClass().add("comparison-card-returned");

        opsLabel = new Label("");
        opsLabel.getStyleClass().add("comparison-card-ops");

        timingLabel = new Label("");
        timingLabel.getStyleClass().add("comparison-card-timing");

        metrics.getChildren().addAll(returnedLabel, opsLabel, timingLabel);

        // ── State host (visual-first, text fallback) ─
        visualHost = new VisualStateHost("");
        visualHost.getStyleClass().add("comparison-card-state");
        visualHost.getFallbackArea().getStyleClass().add("comparison-card-state-text");
        visualHost.getFallbackArea().setPrefHeight(120);
        visualHost.getFallbackArea().setMaxHeight(160);

        // ── Trace section (collapsed by default) ────
        traceToggle = new Label("▸ Show trace details");
        traceToggle.getStyleClass().add("comparison-trace-toggle");
        traceToggle.setOnMouseClicked(e -> toggleTrace());

        traceArea = new TextArea();
        traceArea.setEditable(false);
        traceArea.setWrapText(true);
        traceArea.getStyleClass().add("comparison-card-trace-text");
        traceArea.setPrefHeight(100);
        traceArea.setMaxHeight(140);
        traceArea.setVisible(false);
        traceArea.setManaged(false);

        traceSection = new VBox(4, traceToggle, traceArea);
        traceSection.getStyleClass().add("comparison-card-trace");

        getChildren().addAll(header, metrics, visualHost, traceSection);
    }

    /**
     * Update this card to reflect the current comparison state for one implementation.
     * Called on session open and after each operation execution.
     */
    public void updateIdle(String implName) {
        nameLabel.setText(implName);
        setStatusBadge("IDLE", "comparison-status-idle");
        returnedLabel.setText("");
        opsLabel.setText("");
        timingLabel.setText("");
        getStyleClass().removeAll("comparison-card-divergent", "comparison-card-fastest");
        visualHost.clear();
        traceArea.setText("");
        collapseTrace();
    }

    /**
     * Update the card with a raw snapshot state (before any operation has been executed).
     */
    public void updateState(String implName, String rawSnapshot, String renderedState) {
        nameLabel.setText(implName);
        setStatusBadge("READY", "comparison-status-ready");
        returnedLabel.setText("");
        opsLabel.setText("");
        visualHost.render(rawSnapshot, renderedState, 200);
        traceArea.setText("");
    }

    /**
     * Update the card with a full operation result.
     *
     * @param timingText  human-readable duration (e.g. "12.3 μs"), or null to hide
     * @param fastest     true if this entry was the fastest in the comparison
     * @param divergent   true if divergence was detected across implementations
     */
    public void updateResult(String implName, boolean success, String returnedValue,
                             String rawSnapshot, String renderedState,
                             int traceStepCount, String traceText, int totalOps,
                             String timingText, boolean fastest, boolean divergent) {
        nameLabel.setText(implName);

        getStyleClass().removeAll("comparison-card-divergent", "comparison-card-fastest");
        if (divergent) getStyleClass().add("comparison-card-divergent");
        if (fastest) getStyleClass().add("comparison-card-fastest");

        if (success) {
            setStatusBadge("OK", "comparison-status-ok");
            if (returnedValue != null && !"null".equals(returnedValue)) {
                returnedLabel.setText("→ " + returnedValue);
            } else {
                returnedLabel.setText("");
            }
        } else {
            setStatusBadge("FAIL", "comparison-status-fail");
            returnedLabel.setText("");
        }
        opsLabel.setText(traceStepCount + " step" + (traceStepCount == 1 ? "" : "s"));

        if (timingText != null) {
            timingLabel.setText("⏱ " + timingText);
        } else {
            timingLabel.setText("");
        }

        visualHost.render(rawSnapshot, renderedState, 200);
        traceArea.setText(traceText);
    }

    private void toggleTrace() {
        traceExpanded = !traceExpanded;
        traceArea.setVisible(traceExpanded);
        traceArea.setManaged(traceExpanded);
        traceToggle.setText(traceExpanded ? "▾ Hide trace details" : "▸ Show trace details");
    }

    private void collapseTrace() {
        traceExpanded = false;
        traceArea.setVisible(false);
        traceArea.setManaged(false);
        traceToggle.setText("▸ Show trace details");
    }

    private void setStatusBadge(String text, String styleClass) {
        statusBadge.setText(text);
        statusBadge.getStyleClass().setAll("comparison-status-badge", styleClass);
    }
}
