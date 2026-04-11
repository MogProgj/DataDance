package structlab.gui.visual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;

/**
 * Visual comparison card for one implementation within Compare mode.
 * Shows implementation name, status badge, returned value, visual state
 * (if supported), and an expandable trace detail section.
 */
public class ComparisonCardPane extends VBox {

    private final Label nameLabel;
    private final Label statusBadge;
    private final Label returnedLabel;
    private final Label opsLabel;
    private final StackPane stateHost;
    private final TextArea fallbackState;
    private final VBox traceSection;
    private final TextArea traceArea;
    private final Label traceToggle;
    private boolean traceExpanded = false;

    // own visual pane cache — not shared with explore or other cards
    private final VisualPaneCache paneCache = new VisualPaneCache();

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

        metrics.getChildren().addAll(returnedLabel, opsLabel);

        // ── State host ──────────────────────────────
        fallbackState = new TextArea();
        fallbackState.setEditable(false);
        fallbackState.setWrapText(true);
        fallbackState.getStyleClass().add("comparison-card-state-text");
        fallbackState.setPrefHeight(120);
        fallbackState.setMaxHeight(160);

        stateHost = new StackPane(fallbackState);
        stateHost.getStyleClass().add("comparison-card-state");

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

        getChildren().addAll(header, metrics, stateHost, traceSection);
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
        fallbackState.setText("");
        stateHost.getChildren().setAll(fallbackState);
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
        showState(rawSnapshot, renderedState);
        traceArea.setText("");
    }

    /**
     * Update the card with a full operation result.
     */
    public void updateResult(String implName, boolean success, String returnedValue,
                             String rawSnapshot, String renderedState,
                             int traceStepCount, String traceText, int totalOps) {
        nameLabel.setText(implName);

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
        showState(rawSnapshot, renderedState);
        traceArea.setText(traceText);
    }

    private void showState(String rawSnapshot, String renderedState) {
        if (rawSnapshot != null && VisualStateFactory.isSupported(rawSnapshot)) {
            Node visual = createOrUpdateVisual(rawSnapshot);
            if (visual != null) {
                ScrollPane scroll = new ScrollPane(visual);
                scroll.setFitToWidth(true);
                scroll.getStyleClass().add("visual-scroll");
                scroll.setMaxHeight(200);
                stateHost.getChildren().setAll(scroll);
                return;
            }
        }
        fallbackState.setText(renderedState != null ? renderedState : "");
        stateHost.getChildren().setAll(fallbackState);
    }

    /**
     * Per-card visual pane creation via shared {@link VisualPaneCache}.
     */
    private Node createOrUpdateVisual(String snapshot) {
        String type = StateModelParser.structureType(snapshot);
        if ("HeapPriorityQueue".equals(type)) {
            HeapStateModel m = StateModelParser.parseHeapPriorityQueue(snapshot);
            return paneCache.updateAsPriorityQueue(m);
        }

        VisualState state = StateModelParser.parse(snapshot);
        if (state == null) return null;
        return paneCache.update(state);
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
