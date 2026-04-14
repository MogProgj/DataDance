package structlab.gui.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import structlab.core.graph.*;
import structlab.gui.AppSettings;
import structlab.gui.visual.GraphVisualPane;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Explore-mode graph algorithm workspace.
 * Self-contained UI module that provides preset selection, algorithm selection,
 * source node selection, and playback controls for BFS/DFS step-by-step execution.
 */
public class AlgorithmLabController {

    private final GraphVisualPane graphPane;
    private final PlaybackController playback = new PlaybackController();
    private final AppSettings settings;

    // Compare mode
    private final GraphVisualPane comparePane = new GraphVisualPane();
    private final PlaybackController comparePlayback = new PlaybackController();
    private boolean compareMode = false;
    private ComboBox<String> compareAlgoCombo;
    private ToggleButton compareModeToggle;
    private HBox compareWorkspaceBox;
    private VBox primaryWorkspace;
    private VBox compareWorkspacePane;

    // Interactive studio
    private ToggleButton editModeToggle;
    private Button fitViewBtn;

    // Controls
    private ComboBox<String> presetCombo;
    private ComboBox<String> algorithmCombo;
    private ComboBox<String> sourceCombo;
    private ComboBox<String> targetCombo;
    private Label algoHintLabel;
    private VBox sourceSection;
    private VBox targetSection;
    private Label targetHintLabel;
    private Button runBtn, resetBtn;
    private Button playBtn, pauseBtn, nextBtn, prevBtn, toStartBtn, toEndBtn;
    private Slider speedSlider;
    private Label speedLabel;
    private Label frameLabel;
    private GraphBuilderPanel builderPanel;

    // Compare summary
    private Label comparePrimaryLabel;
    private Label compareSecondaryLabel;
    private Label compareSummaryLabel;

    // Info panel
    private Label discoveryLabel;
    private Label frontierLabel;
    private Label visitedLabel;
    private Label depthLabel;
    private Label statusMessageLabel;
    private Label distancesLabel;
    private Label pathLabel;

    // State
    private Graph currentGraph;
    private GraphPresets.Preset currentPreset;
    private Timeline autoPlayTimeline;
    private boolean isPlaying = false;
    private AlgorithmTrackerPane trackerPane;

    public AlgorithmLabController(AppSettings settings) {
        this.graphPane = new GraphVisualPane();
        this.settings = settings;
    }

    /** Builds and returns the complete Algorithm Lab workspace node. */
    public Node buildWorkspace() {
        // ── Left control panel ──────────────────────────────
        VBox controlPanel = new VBox(8);
        controlPanel.getStyleClass().add("algo-control-panel");
        controlPanel.setPrefWidth(260);
        controlPanel.setMinWidth(260);
        controlPanel.setMaxWidth(260);
        controlPanel.setPadding(new Insets(16, 12, 16, 12));

        // Preset selection
        VBox presetSection = buildSection("GRAPH PRESET");
        presetCombo = new ComboBox<>();
        presetCombo.setMaxWidth(Double.MAX_VALUE);
        presetCombo.getStyleClass().add("algo-combo");
        presetCombo.setPromptText("Select a graph...");

        List<GraphPresets.Preset> presets = GraphPresets.all();
        java.util.List<String> presetNames = new java.util.ArrayList<>();
        presetNames.add("\u2726 Custom Graph");
        presets.forEach(p -> presetNames.add(p.name()));
        presetCombo.setItems(FXCollections.observableArrayList(presetNames));
        presetCombo.setOnAction(e -> onPresetSelected());

        Label presetDesc = new Label("");
        presetDesc.getStyleClass().add("algo-preset-desc");
        presetDesc.setWrapText(true);

        // Custom graph builder (initially hidden)
        builderPanel = new GraphBuilderPanel();
        builderPanel.setVisible(false);
        builderPanel.setManaged(false);
        builderPanel.setOnGraphChanged(g -> onBuilderGraphChanged(g));

        sectionBody(presetSection).getChildren().addAll(presetCombo, presetDesc, builderPanel);
        this.presetDescLabel = presetDesc;

        // Algorithm selection
        VBox algoSection = buildSection("ALGORITHM");
        algorithmCombo = new ComboBox<>();
        algorithmCombo.setMaxWidth(Double.MAX_VALUE);
        algorithmCombo.getStyleClass().add("algo-combo");
        algorithmCombo.setItems(FXCollections.observableArrayList(
                GraphAlgorithmCatalog.displayLabels()));
        algorithmCombo.getSelectionModel().selectFirst();
        algorithmCombo.setOnAction(e -> onAlgorithmSelected());
        algoHintLabel = new Label("");
        algoHintLabel.getStyleClass().add("algo-preset-desc");
        algoHintLabel.setWrapText(true);
        sectionBody(algoSection).getChildren().addAll(algorithmCombo, algoHintLabel);

        // Source selection
        sourceSection = buildSection("SOURCE NODE");
        sourceCombo = new ComboBox<>();
        sourceCombo.setMaxWidth(Double.MAX_VALUE);
        sourceCombo.getStyleClass().add("algo-combo");
        sourceCombo.setPromptText("Select source...");
        sectionBody(sourceSection).getChildren().add(sourceCombo);

        // Target selection
        targetSection = buildSection("TARGET NODE");
        targetCombo = new ComboBox<>();
        targetCombo.setMaxWidth(Double.MAX_VALUE);
        targetCombo.getStyleClass().add("algo-combo");
        targetCombo.setPromptText("No target (full tree)");
        targetHintLabel = new Label("Optional — set for shortest-path mode");
        targetHintLabel.getStyleClass().add("algo-preset-desc");
        targetHintLabel.setWrapText(true);
        sectionBody(targetSection).getChildren().addAll(targetCombo, targetHintLabel);

        // Run / Reset
        VBox actionSection = new VBox(6);
        actionSection.setPadding(new Insets(8, 0, 0, 0));
        runBtn = new Button("Run Algorithm");
        runBtn.getStyleClass().add("primary-button");
        runBtn.setMaxWidth(Double.MAX_VALUE);
        runBtn.setDisable(true);
        runBtn.setOnAction(e -> onRun());

        resetBtn = new Button("Reset");
        resetBtn.getStyleClass().add("secondary-button");
        resetBtn.setMaxWidth(Double.MAX_VALUE);
        resetBtn.setDisable(true);
        resetBtn.setOnAction(e -> onReset());

        actionSection.getChildren().addAll(runBtn, resetBtn);

        // ── Interactive + Compare toggles ───────────────────
        VBox modeSection = buildSection("MODE");
        editModeToggle = new ToggleButton("Edit Mode");
        editModeToggle.getStyleClass().add("secondary-button");
        editModeToggle.setMaxWidth(Double.MAX_VALUE);
        editModeToggle.setOnAction(e -> onToggleEditMode());

        fitViewBtn = new Button("Fit to View");
        fitViewBtn.getStyleClass().add("secondary-button");
        fitViewBtn.setMaxWidth(Double.MAX_VALUE);
        fitViewBtn.setOnAction(e -> graphPane.fitToView());

        compareModeToggle = new ToggleButton("Compare Mode");
        compareModeToggle.getStyleClass().add("secondary-button");
        compareModeToggle.setMaxWidth(Double.MAX_VALUE);
        compareModeToggle.setOnAction(e -> onToggleCompareMode());

        compareAlgoCombo = new ComboBox<>();
        compareAlgoCombo.setMaxWidth(Double.MAX_VALUE);
        compareAlgoCombo.getStyleClass().add("algo-combo");
        compareAlgoCombo.setItems(FXCollections.observableArrayList(
                GraphAlgorithmCatalog.displayLabels()));
        compareAlgoCombo.setPromptText("Compare algorithm...");
        compareAlgoCombo.setVisible(false);
        compareAlgoCombo.setManaged(false);

        sectionBody(modeSection).getChildren().addAll(
                editModeToggle, fitViewBtn, compareModeToggle, compareAlgoCombo);

        // ── Save / Load scenario ────────────────────────────
        VBox scenarioSection = buildSection("SCENARIO");
        Button saveBtn = new Button("Save Scenario…");
        saveBtn.getStyleClass().add("secondary-button");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setOnAction(e -> onSaveScenario());

        Button loadBtn = new Button("Load Scenario…");
        loadBtn.getStyleClass().add("secondary-button");
        loadBtn.setMaxWidth(Double.MAX_VALUE);
        loadBtn.setOnAction(e -> onLoadScenario());
        sectionBody(scenarioSection).getChildren().addAll(saveBtn, loadBtn);

        // Tracker pane (visibility bound to settings)
        trackerPane = new AlgorithmTrackerPane(settings.isTrackerExpanded());
        trackerPane.visibleProperty().bind(settings.showAlgorithmTrackerProperty());
        trackerPane.managedProperty().bind(settings.showAlgorithmTrackerProperty());

        controlPanel.getChildren().addAll(presetSection, new Separator(), algoSection,
                new Separator(), sourceSection, new Separator(), targetSection,
                new Separator(), actionSection, new Separator(), modeSection,
                new Separator(), scenarioSection, new Separator(), trackerPane);

        ScrollPane controlScroll = new ScrollPane(controlPanel);
        controlScroll.setFitToWidth(true);
        controlScroll.getStyleClass().add("visual-scroll");
        controlScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        controlScroll.setPrefWidth(276);
        controlScroll.setMinWidth(276);
        controlScroll.setMaxWidth(276);

        // ── Center workspace ────────────────────────────────
        // Primary graph view
        ScrollPane graphScroll = new ScrollPane(graphPane);
        graphScroll.setFitToWidth(true);
        graphScroll.getStyleClass().add("visual-scroll");
        VBox.setVgrow(graphScroll, Priority.ALWAYS);

        // Primary workspace label
        comparePrimaryLabel = new Label("");
        comparePrimaryLabel.getStyleClass().addAll("section-header", "compare-header");
        comparePrimaryLabel.setPadding(new Insets(4, 8, 4, 8));
        comparePrimaryLabel.setVisible(false);
        comparePrimaryLabel.setManaged(false);

        HBox playbackBar = buildPlaybackBar();
        primaryWorkspace = new VBox(0, comparePrimaryLabel, graphScroll, playbackBar);
        primaryWorkspace.getStyleClass().add("algo-workspace-content");
        VBox.setVgrow(primaryWorkspace, Priority.ALWAYS);
        HBox.setHgrow(primaryWorkspace, Priority.ALWAYS);

        // Compare graph view (initially hidden)
        ScrollPane compareScroll = new ScrollPane(comparePane);
        compareScroll.setFitToWidth(true);
        compareScroll.getStyleClass().add("visual-scroll");
        VBox.setVgrow(compareScroll, Priority.ALWAYS);

        compareSecondaryLabel = new Label("COMPARE");
        compareSecondaryLabel.getStyleClass().addAll("section-header", "compare-header");
        compareSecondaryLabel.setPadding(new Insets(4, 8, 4, 8));

        compareSummaryLabel = new Label("");
        compareSummaryLabel.getStyleClass().add("algo-preset-desc");
        compareSummaryLabel.setWrapText(true);
        compareSummaryLabel.setPadding(new Insets(2, 8, 2, 8));

        compareWorkspacePane = new VBox(0, compareSecondaryLabel, compareSummaryLabel, compareScroll);
        compareWorkspacePane.getStyleClass().add("algo-workspace-content");
        compareWorkspacePane.getStyleClass().add("compare-workspace");
        VBox.setVgrow(compareWorkspacePane, Priority.ALWAYS);
        HBox.setHgrow(compareWorkspacePane, Priority.ALWAYS);
        compareWorkspacePane.setVisible(false);
        compareWorkspacePane.setManaged(false);

        compareWorkspaceBox = new HBox(0, primaryWorkspace, compareWorkspacePane);
        HBox.setHgrow(compareWorkspaceBox, Priority.ALWAYS);

        VBox workspace = new VBox();
        workspace.getStyleClass().add("workspace");
        HBox.setHgrow(workspace, Priority.ALWAYS);
        VBox.setVgrow(compareWorkspaceBox, Priority.ALWAYS);
        workspace.getChildren().add(compareWorkspaceBox);

        // Wire up interactive canvas callback
        graphPane.setOnGraphChanged(g -> onCanvasGraphChanged(g));

        // ── Right info panel ────────────────────────────────
        VBox infoPanel = buildInfoPanel();

        return new HBox(controlScroll, workspace, infoPanel);
    }

    // ── UI builders ─────────────────────────────────────────

    private Label presetDescLabel;

    private HBox buildPlaybackBar() {
        HBox bar = new HBox(8);
        bar.getStyleClass().add("algo-playback-bar");
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(10, 16, 10, 16));

        toStartBtn = iconButton("⏮", "Reset to start");
        toStartBtn.setOnAction(e -> onToStart());
        prevBtn = iconButton("◀", "Previous step");
        prevBtn.setOnAction(e -> onPrev());
        playBtn = iconButton("▶", "Play");
        playBtn.setOnAction(e -> onPlay());
        pauseBtn = iconButton("⏸", "Pause");
        pauseBtn.setOnAction(e -> onPause());
        pauseBtn.setVisible(false);
        pauseBtn.setManaged(false);
        nextBtn = iconButton("▶▶", "Next step");
        nextBtn.setText("▶|");
        nextBtn.setOnAction(e -> onNext());
        toEndBtn = iconButton("⏭", "Jump to end");
        toEndBtn.setOnAction(e -> onToEnd());

        frameLabel = new Label("—");
        frameLabel.getStyleClass().add("algo-frame-label");

        Region spacerL = new Region();
        HBox.setHgrow(spacerL, Priority.ALWAYS);
        Region spacerR = new Region();
        HBox.setHgrow(spacerR, Priority.ALWAYS);

        speedSlider = new Slider(0.25, 3.0, settings.getDefaultPlaybackSpeed());
        speedSlider.getStyleClass().add("algo-speed-slider");
        speedSlider.setPrefWidth(100);
        speedSlider.setMajorTickUnit(0.5);
        speedSlider.valueProperty().addListener((obs, o, n) -> {
            speedLabel.setText(String.format("%.1fx", n.doubleValue()));
            if (autoPlayTimeline != null && isPlaying) {
                restartAutoPlay();
            }
        });

        speedLabel = new Label(String.format("%.1fx", settings.getDefaultPlaybackSpeed()));
        speedLabel.getStyleClass().add("algo-speed-label");

        bar.getChildren().addAll(
                toStartBtn, prevBtn, playBtn, pauseBtn, nextBtn, toEndBtn,
                spacerL, frameLabel, spacerR,
                new Label("Speed:"), speedSlider, speedLabel);

        setPlaybackDisabled(true);
        return bar;
    }

    private VBox buildInfoPanel() {
        VBox panel = new VBox(8);
        panel.getStyleClass().add("algo-info-panel");
        panel.setPrefWidth(240);
        panel.setMinWidth(240);
        panel.setMaxWidth(240);
        panel.setPadding(new Insets(16, 12, 16, 12));

        VBox stateSection = buildSection("ALGORITHM STATE");
        statusMessageLabel = new Label("No algorithm running");
        statusMessageLabel.getStyleClass().add("algo-status-msg");
        statusMessageLabel.setWrapText(true);
        depthLabel = new Label("");
        depthLabel.getStyleClass().add("algo-info-value");
        discoveryLabel = new Label("");
        discoveryLabel.getStyleClass().add("algo-info-value");
        discoveryLabel.setWrapText(true);
        frontierLabel = new Label("");
        frontierLabel.getStyleClass().add("algo-info-value");
        frontierLabel.setWrapText(true);
        visitedLabel = new Label("");
        visitedLabel.getStyleClass().add("algo-info-value");

        distancesLabel = new Label("");
        distancesLabel.getStyleClass().add("algo-info-value");
        distancesLabel.setWrapText(true);
        pathLabel = new Label("");
        pathLabel.getStyleClass().add("algo-info-value");
        pathLabel.setWrapText(true);

        VBox stateBody = sectionBody(stateSection);
        stateBody.getChildren().addAll(
                statusMessageLabel,
                new Separator(),
                styledLabel("DEPTH / LAYER", "algo-info-label"), depthLabel,
                styledLabel("DISCOVERY ORDER", "algo-info-label"), discoveryLabel,
                styledLabel("FRONTIER", "algo-info-label"), frontierLabel,
                styledLabel("VISITED / SETTLED", "algo-info-label"), visitedLabel,
                new Separator(),
                styledLabel("DISTANCES", "algo-info-label"), distancesLabel,
                styledLabel("SHORTEST PATH", "algo-info-label"), pathLabel);

        panel.getChildren().add(stateSection);
        return panel;
    }

    // ── Event handlers ──────────────────────────────────────

    private void onPresetSelected() {
        stopAutoPlay();
        int index = presetCombo.getSelectionModel().getSelectedIndex();
        if (index < 0) return;

        boolean isCustom = (index == 0);
        builderPanel.setVisible(isCustom);
        builderPanel.setManaged(isCustom);

        if (isCustom) {
            currentPreset = null;
            currentGraph = builderPanel.getGraph();
            presetDescLabel.setText("Build your own graph — add nodes and edges below.");
            populateNodeCombos();
            graphPane.setGraph(currentGraph, builderPanel.isWeighted());
        } else {
            currentPreset = GraphPresets.all().get(index - 1);
            currentGraph = currentPreset.graph();
            presetDescLabel.setText(currentPreset.description());
            populateNodeCombos();

            if (currentGraph.hasNode(currentPreset.suggestedSource())) {
                sourceCombo.getSelectionModel().select(currentPreset.suggestedSource());
            }
            if (currentPreset.suggestedTarget() != null
                    && currentGraph.hasNode(currentPreset.suggestedTarget())) {
                targetCombo.getSelectionModel().select(currentPreset.suggestedTarget());
            }

            // Smart algorithm auto-select
            String name = currentPreset.name();
            if (name.startsWith("DAG")) {
                algorithmCombo.getSelectionModel().select("Topo Sort");
            } else if (name.contains("Negative")) {
                algorithmCombo.getSelectionModel().select("Bellman-Ford");
            } else if (currentPreset.weighted()) {
                algorithmCombo.getSelectionModel().select("Dijkstra");
            }

            graphPane.setGraph(currentGraph, currentPreset.weighted());
            if (settings.isAutoFitGraph()) graphPane.fitToView();
        }

        resetPlaybackControls(currentGraph == null || currentGraph.nodeCount() == 0);
        onAlgorithmSelected();
    }

    private void onBuilderGraphChanged(Graph graph) {
        this.currentGraph = graph;
        populateNodeCombos();
        graphPane.setGraph(graph, builderPanel.isWeighted());
        if (settings.isAutoFitGraph()) graphPane.fitToView();
        resetPlaybackControls(graph.nodeCount() == 0);
    }

    /** Updates control visibility and hint text based on the selected algorithm spec. */
    private void onAlgorithmSelected() {
        GraphAlgorithmSpec spec = selectedSpec();
        if (spec == null) return;

        // Source visibility
        boolean needsSource = spec.sourceRequired();
        sourceSection.setVisible(needsSource);
        sourceSection.setManaged(needsSource);

        // Target visibility
        boolean showTarget = spec.targetMode() != GraphAlgorithmSpec.TargetMode.NONE;
        targetSection.setVisible(showTarget);
        targetSection.setManaged(showTarget);
        if (showTarget) {
            targetHintLabel.setText(
                    spec.targetMode() == GraphAlgorithmSpec.TargetMode.REQUIRED
                            ? "Required — select a target node"
                            : "Optional — set for shortest-path mode");
        }

        // Hint label
        algoHintLabel.setText(spec.hint());
    }

    private void populateNodeCombos() {
        if (currentGraph == null) return;
        sourceCombo.setItems(FXCollections.observableArrayList(currentGraph.nodes()));
        if (!currentGraph.nodes().isEmpty()) {
            sourceCombo.getSelectionModel().selectFirst();
        }
        List<String> targetOptions = new java.util.ArrayList<>();
        targetOptions.add("— No target —");
        targetOptions.addAll(currentGraph.nodes());
        targetCombo.setItems(FXCollections.observableArrayList(targetOptions));
        targetCombo.getSelectionModel().selectFirst();
    }

    private void onRun() {
        if (currentGraph == null) return;
        GraphAlgorithmSpec spec = selectedSpec();
        if (spec == null) return;

        // Validate graph compatibility
        if (!spec.supportsGraph(currentGraph)) {
            String need = spec.directedOk() ? "directed" : "undirected";
            showAlgorithmError("Cannot run " + spec.displayLabel(),
                    spec.displayLabel() + " requires a " + need + " graph.");
            return;
        }

        String source = sourceCombo.getValue();
        if (spec.sourceRequired() && (source == null || source.isEmpty())) {
            showAlgorithmError("Source required",
                    spec.displayLabel() + " requires a source node.");
            return;
        }

        String targetVal = targetCombo.getValue();
        String target = (targetVal == null || targetVal.startsWith("—")) ? null : targetVal;
        if (spec.targetMode() == GraphAlgorithmSpec.TargetMode.REQUIRED && target == null) {
            showAlgorithmError("Target required",
                    spec.displayLabel() + " requires a target node.");
            return;
        }

        // Exit edit mode when running
        if (graphPane.isEditMode()) {
            editModeToggle.setSelected(false);
            graphPane.setEditMode(false);
        }

        stopAutoPlay();

        List<AlgorithmFrame> frames = dispatchRun(spec, source, target);
        if (frames == null) return;

        playback.load(frames);
        resetBtn.setDisable(false);
        setPlaybackDisabled(false);
        renderCurrentFrame();

        // Compare mode: run the second algorithm
        if (compareMode) {
            runCompareAlgorithm(source);
        }
    }

    /** Resolves the currently selected spec from the algorithm combo. */
    GraphAlgorithmSpec selectedSpec() {
        String label = algorithmCombo.getValue();
        return label != null ? GraphAlgorithmCatalog.byLabel(label) : null;
    }

    /** Central dispatch using the catalog. */
    private List<AlgorithmFrame> dispatchRun(GraphAlgorithmSpec spec,
                                              String source, String target) {
        try {
            return GraphAlgorithmCatalog.run(spec, currentGraph,
                    source, target, graphPane.getNodePositions());
        } catch (IllegalArgumentException ex) {
            showAlgorithmError("Cannot run " + spec.displayLabel(), ex.getMessage());
            return null;
        }
    }

    /** Runs the compare algorithm and syncs the compare pane. */
    private void runCompareAlgorithm(String source) {
        String compareLabel = compareAlgoCombo.getValue();
        if (compareLabel == null) return;
        GraphAlgorithmSpec compareSpec = GraphAlgorithmCatalog.byLabel(compareLabel);
        if (compareSpec == null) return;

        // Validate compare algorithm against graph
        if (!compareSpec.supportsGraph(currentGraph)) {
            compareSummaryLabel.setText(compareSpec.displayLabel()
                    + " is not compatible with this graph type.");
            comparePlayback.clear();
            comparePane.renderIdle();
            return;
        }

        String targetVal = targetCombo.getValue();
        String target = (targetVal == null || targetVal.startsWith("—")) ? null : targetVal;

        if (compareSpec.sourceRequired() && (source == null || source.isEmpty())) {
            compareSummaryLabel.setText(compareSpec.displayLabel() + " requires a source node.");
            return;
        }
        if (compareSpec.targetMode() == GraphAlgorithmSpec.TargetMode.REQUIRED && target == null) {
            compareSummaryLabel.setText(compareSpec.displayLabel() + " requires a target node.");
            return;
        }

        List<AlgorithmFrame> compareFrames = dispatchRun(compareSpec, source, target);
        if (compareFrames != null) {
            comparePlayback.load(compareFrames);
            AlgorithmFrame first = comparePlayback.current();
            if (first != null) comparePane.renderFrame(first);
            updateCompareLabels();
        }
    }

    /** Keeps the compare pane's graph in sync with the primary graph. */
    private void syncCompareGraph() {
        if (!compareMode || currentGraph == null) return;
        boolean weighted = currentPreset != null
                ? currentPreset.weighted() : builderPanel.isWeighted();
        comparePane.setGraph(currentGraph, weighted);
        Map<String, double[]> positions = graphPane.getNodePositions();
        if (!positions.isEmpty()) {
            comparePane.setNodePositions(positions);
            comparePane.renderIdle();
        }
    }

    /** Updates compare workspace labels after running both algorithms. */
    private void updateCompareLabels() {
        String primaryLabel = algorithmCombo.getValue();
        String secondaryLabel = compareAlgoCombo.getValue();
        comparePrimaryLabel.setText(primaryLabel != null ? primaryLabel : "PRIMARY");
        compareSecondaryLabel.setText(secondaryLabel != null ? secondaryLabel : "COMPARE");

        // Summary: frame counts
        int pFrames = playback.frameCount();
        int cFrames = comparePlayback.frameCount();
        compareSummaryLabel.setText(
                (primaryLabel != null ? primaryLabel : "?") + ": " + pFrames + " steps  vs  "
                + (secondaryLabel != null ? secondaryLabel : "?") + ": " + cFrames + " steps");
    }

    private static void showAlgorithmError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Graph");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void onReset() {
        stopAutoPlay();
        playback.clear();
        graphPane.renderIdle();
        setPlaybackDisabled(true);
        clearInfoPanel();
        updateFrameLabel();
    }

    private void onNext() {
        if (playback.next()) {
            renderCurrentFrame();
        }
    }

    private void onPrev() {
        if (playback.previous()) {
            renderCurrentFrame();
        }
    }

    private void onToStart() {
        stopAutoPlay();
        playback.reset();
        renderCurrentFrame();
    }

    private void onToEnd() {
        stopAutoPlay();
        playback.jumpToEnd();
        renderCurrentFrame();
    }

    private void onPlay() {
        if (!playback.isLoaded()) return;
        isPlaying = true;
        playBtn.setVisible(false);
        playBtn.setManaged(false);
        pauseBtn.setVisible(true);
        pauseBtn.setManaged(true);
        startAutoPlay();
    }

    private void onPause() {
        stopAutoPlay();
    }

    private void onToggleEditMode() {
        boolean edit = editModeToggle.isSelected();
        graphPane.setEditMode(edit);
        if (edit) {
            graphPane.setStatusText("Edit mode — click canvas to add nodes, click two nodes to connect, drag to move");
        } else {
            graphPane.clearSelection();
            if (currentGraph != null) graphPane.renderIdle();
        }
    }

    private void onToggleCompareMode() {
        compareMode = compareModeToggle.isSelected();
        compareAlgoCombo.setVisible(compareMode);
        compareAlgoCombo.setManaged(compareMode);
        compareWorkspacePane.setVisible(compareMode);
        compareWorkspacePane.setManaged(compareMode);

        // Compare header labels
        comparePrimaryLabel.setVisible(compareMode);
        comparePrimaryLabel.setManaged(compareMode);

        if (compareMode) {
            syncCompareGraph();
        } else {
            comparePlayback.clear();
            comparePane.renderIdle();
            compareSummaryLabel.setText("");
        }
    }

    private void onCanvasGraphChanged(Graph graph) {
        // Called when the interactive canvas modifies the graph
        this.currentGraph = graph;
        populateNodeCombos();
        // Sync builder panel if visible
        int presetIndex = presetCombo.getSelectionModel().getSelectedIndex();
        if (presetIndex == 0 && builderPanel.isVisible()) {
            builderPanel.loadGraph(graph, graph.isDirected());
        }
        resetPlaybackControls(graph.nodeCount() == 0);
    }

    // ── Auto-play ───────────────────────────────────────────

    private void startAutoPlay() {
        double speed = speedSlider.getValue();
        double intervalMs = 800.0 / speed;

        autoPlayTimeline = new Timeline(new KeyFrame(
                Duration.millis(intervalMs), e -> {
            if (!playback.hasNext()) {
                stopAutoPlay();
                return;
            }
            playback.next();
            renderCurrentFrame();
        }));
        autoPlayTimeline.setCycleCount(Timeline.INDEFINITE);
        autoPlayTimeline.play();
    }

    private void stopAutoPlay() {
        isPlaying = false;
        if (autoPlayTimeline != null) {
            autoPlayTimeline.stop();
            autoPlayTimeline = null;
        }
        playBtn.setVisible(true);
        playBtn.setManaged(true);
        pauseBtn.setVisible(false);
        pauseBtn.setManaged(false);
    }

    private void restartAutoPlay() {
        if (autoPlayTimeline != null) {
            autoPlayTimeline.stop();
        }
        startAutoPlay();
    }

    // ── Rendering ───────────────────────────────────────────

    private void renderCurrentFrame() {
        AlgorithmFrame frame = playback.current();
        if (frame == null) return;
        graphPane.renderFrame(frame);
        updateInfoPanel(frame);
        trackerPane.update(frame);
        updateFrameLabel();
        updatePlaybackButtons();

        // Sync compare view to same step index
        if (compareMode && comparePlayback.isLoaded()) {
            int targetIdx = playback.currentIndex();
            comparePlayback.jumpTo(targetIdx);
            AlgorithmFrame cf = comparePlayback.current();
            if (cf != null) comparePane.renderFrame(cf);
        }
    }

    private void updateInfoPanel(AlgorithmFrame frame) {
        statusMessageLabel.setText(frame.statusMessage());

        boolean isDijkstra = frame.algorithm() == AlgorithmFrame.AlgorithmType.DIJKSTRA;
        boolean isBellmanFord = frame.algorithm() == AlgorithmFrame.AlgorithmType.BELLMAN_FORD;
        boolean isAStar = frame.algorithm() == AlgorithmFrame.AlgorithmType.A_STAR;
        boolean isTopoSort = frame.algorithm() == AlgorithmFrame.AlgorithmType.TOPOLOGICAL_SORT;
        boolean isPrim = frame.algorithm() == AlgorithmFrame.AlgorithmType.PRIM;
        boolean isKruskal = frame.algorithm() == AlgorithmFrame.AlgorithmType.KRUSKAL;
        boolean isSCC = frame.algorithm() == AlgorithmFrame.AlgorithmType.SCC;
        boolean isBridges = frame.algorithm() == AlgorithmFrame.AlgorithmType.BRIDGES;
        boolean isAP = frame.algorithm() == AlgorithmFrame.AlgorithmType.ARTICULATION_POINTS;
        boolean isMST = isPrim || isKruskal;
        boolean isDiag = isSCC || isBridges || isAP;
        boolean showsDistances = isDijkstra || isBellmanFord || isAStar;

        if (showsDistances || isTopoSort || isMST || isDiag) {
            depthLabel.setText("—");
        } else {
            depthLabel.setText(String.valueOf(frame.depth()));
        }

        List<String> disc = frame.discoveryOrder();
        if (isTopoSort) {
            discoveryLabel.setText(disc.isEmpty() ? "—" : "Order: " + String.join(" → ", disc));
        } else if (isMST) {
            discoveryLabel.setText(disc.isEmpty() ? "—" : "Added: " + String.join(" → ", disc));
        } else if (isSCC) {
            discoveryLabel.setText(disc.isEmpty() ? "—" : String.join(" → ", disc));
        } else {
            discoveryLabel.setText(disc.isEmpty() ? "—" : String.join(" → ", disc));
        }

        List<String> front = frame.frontier();
        String frontierType;
        if (isDijkstra) {
            frontierType = "PQ";
        } else if (isAStar) {
            frontierType = "Open (f)";
        } else if (isBellmanFord) {
            frontierType = "Relaxed";
        } else if (isTopoSort) {
            frontierType = "Ready";
        } else if (isPrim) {
            frontierType = "PQ (key)";
        } else if (isKruskal) {
            frontierType = "Edge";
        } else if (isSCC) {
            frontierType = "Stack";
        } else if (isBridges || isAP) {
            frontierType = "DFS";
        } else if (frame.algorithm() == AlgorithmFrame.AlgorithmType.BFS) {
            frontierType = "Queue";
        } else {
            frontierType = "Stack";
        }
        frontierLabel.setText(front.isEmpty() ? "— (empty " + frontierType + ")"
                : frontierType + ": [" + String.join(", ", front) + "]");

        String visitedWord;
        if (showsDistances) visitedWord = "Settled";
        else if (isTopoSort) visitedWord = "Processed";
        else if (isMST) visitedWord = "In MST";
        else if (isDiag) visitedWord = "Visited";
        else visitedWord = "Visited";
        visitedLabel.setText(visitedWord + ": " + frame.visited().size() + " of "
                + (currentGraph != null ? currentGraph.nodeCount() : "?") + " nodes");

        // Distances and path for Dijkstra / Bellman-Ford / A*
        if (showsDistances) {
            java.util.Map<String, Double> dist = frame.distances();
            if (!dist.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                dist.forEach((node, d) -> {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(node).append("=").append(DijkstraRunner.formatDist(d));
                });
                distancesLabel.setText(sb.toString());
            } else {
                distancesLabel.setText("—");
            }

            List<String> path = frame.shortestPath();
            if (!path.isEmpty()) {
                pathLabel.setText(String.join(" → ", path));
            } else if (frame.targetNode() != null) {
                pathLabel.setText("(not yet found)");
            } else {
                pathLabel.setText("—");
            }
        } else if (isTopoSort) {
            // Show indegrees from distances map
            java.util.Map<String, Double> inDegrees = frame.distances();
            if (!inDegrees.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                inDegrees.forEach((node, d) -> {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(node).append("=").append(d.intValue());
                });
                distancesLabel.setText("Indegrees: " + sb);
            } else {
                distancesLabel.setText("—");
            }
            pathLabel.setText("—");
        } else if (isMST) {
            // Show MST total weight
            java.util.Map<String, Double> dist = frame.distances();
            Double total = dist.get("__MST_TOTAL__");
            if (total != null) {
                distancesLabel.setText("MST weight: " + DijkstraRunner.formatDist(total));
            } else {
                distancesLabel.setText("—");
            }
            pathLabel.setText("MST edges: " + frame.treeEdges().size());
        } else if (isSCC) {
            // Show component assignments
            java.util.Map<String, Double> compDist = frame.distances();
            if (!compDist.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                compDist.forEach((node, c) -> {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(node).append("→SCC#").append(c.intValue());
                });
                distancesLabel.setText(sb.toString());
            } else {
                distancesLabel.setText("—");
            }
            pathLabel.setText("—");
        } else if (isBridges) {
            // Show bridges found so far
            List<String> sp = frame.shortestPath();
            int bridgeCount = sp.size() / 2;
            if (bridgeCount > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i + 1 < sp.size(); i += 2) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(sp.get(i)).append("-").append(sp.get(i + 1));
                }
                distancesLabel.setText("Bridges: " + sb);
            } else {
                distancesLabel.setText("No bridges yet");
            }
            pathLabel.setText("—");
        } else if (isAP) {
            // Show articulation points found so far
            List<String> aps = frame.shortestPath();
            if (!aps.isEmpty()) {
                distancesLabel.setText("Cut vertices: {" + String.join(", ", aps) + "}");
            } else {
                distancesLabel.setText("No cut vertices yet");
            }
            pathLabel.setText("—");
        } else {
            distancesLabel.setText("—");
            pathLabel.setText("—");
        }
    }

    private void clearInfoPanel() {
        statusMessageLabel.setText("No algorithm running");
        depthLabel.setText("");
        discoveryLabel.setText("");
        frontierLabel.setText("");
        visitedLabel.setText("");
        distancesLabel.setText("");
        pathLabel.setText("");
        trackerPane.clear();
    }

    private void updateFrameLabel() {
        if (!playback.isLoaded()) {
            frameLabel.setText("—");
        } else {
            frameLabel.setText((playback.currentIndex() + 1) + " / " + playback.frameCount());
        }
    }

    private void updatePlaybackButtons() {
        prevBtn.setDisable(!playback.hasPrevious());
        nextBtn.setDisable(!playback.hasNext());
        toStartBtn.setDisable(!playback.hasPrevious());
        toEndBtn.setDisable(!playback.hasNext());
    }

    private void setPlaybackDisabled(boolean disabled) {
        playBtn.setDisable(disabled);
        pauseBtn.setDisable(disabled);
        nextBtn.setDisable(disabled);
        prevBtn.setDisable(disabled);
        toStartBtn.setDisable(disabled);
        toEndBtn.setDisable(disabled);
    }

    // ── Scenario persistence ───────────────────────────────

    private void onSaveScenario() {
        if (currentGraph == null) return;
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Graph Scenario");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("DataDance Scenario", "*.dds"));
        fc.setInitialFileName("scenario.dds");
        File file = fc.showSaveDialog(graphPane.getScene().getWindow());
        if (file == null) return;

        String algo = algorithmCombo.getValue();
        String source = sourceCombo.getValue();
        String targetVal = targetCombo.getValue();
        String target = (targetVal != null && !targetVal.startsWith("—")) ? targetVal : null;
        boolean weighted = currentPreset != null ? currentPreset.weighted() : builderPanel.isWeighted();

        GraphScenario scenario = new GraphScenario(
                file.getName().replaceFirst("\\.dds$", ""),
                currentGraph, weighted,
                graphPane.getNodePositions(), algo, source, target);
        try {
            scenario.saveTo(file.toPath());
        } catch (Exception ex) {
            showAlgorithmError("Save failed", ex.getMessage());
        }
    }

    private void onLoadScenario() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Load Graph Scenario");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("DataDance Scenario", "*.dds"));
        File file = fc.showOpenDialog(graphPane.getScene().getWindow());
        if (file == null) return;

        try {
            GraphScenario scenario = GraphScenario.loadFrom(file.toPath());
            applyScenario(scenario);
        } catch (Exception ex) {
            showAlgorithmError("Load failed", ex.getMessage());
        }
    }

    /** Applies a loaded scenario to the workspace — separated for testability. */
    void applyScenario(GraphScenario scenario) {
        currentGraph = scenario.graph();
        currentPreset = null;

        // Select custom graph preset
        presetCombo.getSelectionModel().select(0);
        builderPanel.setVisible(true);
        builderPanel.setManaged(true);
        builderPanel.loadGraph(currentGraph, currentGraph.isDirected());
        presetDescLabel.setText("Loaded: " + scenario.name());

        populateNodeCombos();
        graphPane.setGraph(currentGraph, scenario.weighted());
        if (!scenario.nodePositions().isEmpty()) {
            graphPane.setNodePositions(scenario.nodePositions());
            graphPane.renderIdle();
        }
        if (settings.isAutoFitGraph()) graphPane.fitToView();

        if (scenario.algorithm() != null) {
            algorithmCombo.getSelectionModel().select(scenario.algorithm());
        }
        if (scenario.source() != null && currentGraph.hasNode(scenario.source())) {
            sourceCombo.getSelectionModel().select(scenario.source());
        }
        if (scenario.target() != null && currentGraph.hasNode(scenario.target())) {
            targetCombo.getSelectionModel().select(scenario.target());
        }

        resetPlaybackControls(currentGraph.nodeCount() == 0);
        onAlgorithmSelected();
    }

    // ── Helpers ──────────────────────────────────────────────

    /** Resets playback state after a graph change. */
    private void resetPlaybackControls(boolean disableRun) {
        syncCompareGraph();
        playback.clear();
        runBtn.setDisable(disableRun);
        resetBtn.setDisable(true);
        setPlaybackDisabled(true);
        clearInfoPanel();
        updateFrameLabel();
    }

    private VBox buildSection(String title) {
        VBox section = new VBox(4);
        section.getStyleClass().add("algo-section");
        Label header = new Label(title);
        header.getStyleClass().add("section-header");
        VBox body = new VBox(6);
        body.getStyleClass().add("algo-section-body");
        section.getChildren().addAll(header, body);
        return section;
    }

    private VBox sectionBody(VBox section) {
        return (VBox) section.getChildren().get(1);
    }

    private Button iconButton(String icon, String tooltip) {
        Button btn = new Button(icon);
        btn.getStyleClass().add("algo-playback-btn");
        btn.setTooltip(new Tooltip(tooltip));
        return btn;
    }

    private Label styledLabel(String text, String... classes) {
        Label l = new Label(text);
        l.getStyleClass().addAll(classes);
        return l;
    }
}
