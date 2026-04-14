package structlab.gui.controller;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import structlab.app.comparison.ComparisonEntryResult;
import structlab.app.comparison.ComparisonAnalysis;
import structlab.app.comparison.ComparisonOperationResult;
import structlab.app.comparison.ComparisonRuntimeEntry;
import structlab.app.comparison.ComparisonSession;
import structlab.app.service.*;
import structlab.core.graph.GraphAlgorithmCatalog;
import structlab.core.graph.GraphAlgorithmSpec;
import structlab.gui.*;
import structlab.gui.export.ExportHelper;
import structlab.gui.visual.ComparisonCardPane;
import structlab.gui.visual.ComparisonSummaryPane;
import structlab.gui.visual.VisualStateHost;
import structlab.trace.TraceStep;

import static structlab.gui.visual.UiComponents.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MainWindowController {

    // ══ Shell FXML bindings ══════════════════════════════════
    @FXML private Label pageTitleLabel;
    @FXML private Label pageSubtitleLabel;
    @FXML private Label modeBadge;
    @FXML private StackPane pageHost;
    @FXML private Label statusLabel;
    @FXML private Label bottomStatusLabel;

    @FXML private Button navExploreBtn;
    @FXML private Button navCompareBtn;
    @FXML private Button navAlgoLabBtn;
    @FXML private Button navLearnBtn;
    @FXML private Button navActivityBtn;
    @FXML private Button navSettingsBtn;

    // ══ Service and models ═══════════════════════════════════
    private StructLabService service;
    private final AppSettings settings = new AppSettings();
    private final ActivityLog activityLog = new ActivityLog();
    private NavigationPage currentPage;
    private final Map<NavigationPage, Node> pageCache = new EnumMap<>(NavigationPage.class);
    private Button[] navButtons;

    // ══ Explore page elements ════════════════════════════════
    private ListView<StructureSummary> explStructList;
    private ListView<ImplementationSummary> explImplList;
    private Button explOpenBtn;
    private Label explDetailName, explDetailCat, explDetailDesc, explDetailKw;
    private TextArea explTrace;
    private VisualStateHost explVisualHost;
    private Label explSessStruct, explSessImpl, explSessOps;
    private Button explResetBtn, explCloseBtn;
    private ListView<OperationInfo> explOpList;
    private TextField explArgField;
    private Button explRunBtn;
    private ListView<String> explHistory;
    private boolean sessionActive = false;

    // ══ Compare page elements ════════════════════════════════
    private ListView<StructureSummary> cmpStructList;
    private Button cmpStartBtn;
    private ComparisonSummaryPane cmpSummary;
    private VBox cmpCardGrid;
    private final List<ComparisonCardPane> cmpCards = new ArrayList<>();
    private Label cmpSessLabel, cmpImplCount, cmpOpsCount;
    private Button cmpResetBtn, cmpCloseBtn, cmpExportBtn;
    private ListView<OperationInfo> cmpOpList;
    private TextField cmpArgField;
    private Button cmpRunBtn;
    private ListView<String> cmpHistory;
    private boolean comparisonActive = false;

    // ══ Learn page elements ══════════════════════════════════
    private VBox learnCardContainer;
    private TextField learnSearchField;
    private ComboBox<String> learnCategoryFilter;
    private StackPane learnTabHost;
    private Node learnStructuresTab, learnAlgorithmsTab, learnGuideTab;

    // ══ Activity page elements ═══════════════════════════════
    private VBox activityFeed;
    private ComboBox<String> activityCategoryFilter;

    // ══════════════════════════════════════════════════════════
    //  Lifecycle
    // ══════════════════════════════════════════════════════════

    public void initService(StructLabService service) {
        this.service = service;
        buildAllPages();
        wireSettingsEffects();
        navigateTo(NavigationPage.EXPLORE);
        activityLog.log("Application started", "StructLab initialized", "system");
        if (!settings.isOnboardingDismissed()) {
            showOnboardingOverlay();
        }
    }

    @FXML
    public void initialize() {
        navButtons = new Button[]{
            navExploreBtn, navCompareBtn, navAlgoLabBtn, navLearnBtn, navActivityBtn, navSettingsBtn
        };
    }

    // ══════════════════════════════════════════════════════════
    //  Onboarding
    // ══════════════════════════════════════════════════════════

    void showOnboardingOverlay() {
        int totalImpls = service.getAllStructures().stream()
                .mapToInt(s -> service.getImplementations(s.id()).size()).sum();

        VBox card = new VBox(16);
        card.getStyleClass().add("onboarding-card");
        card.setMaxWidth(520);
        card.setMaxHeight(Region.USE_PREF_SIZE);
        card.setPadding(new Insets(36, 40, 28, 40));

        Label title = styledLabel("Welcome to StructLab", "onboarding-title");
        Label tagline = styledLabel(
                "A hands-on data structure simulator with "
                + service.getAllStructures().size() + " structure families and "
                + totalImpls + " implementations.",
                "onboarding-tagline");
        tagline.setWrapText(true);

        VBox pages = new VBox(10);
        pages.getChildren().addAll(
                onboardingItem("Explore",
                        "Open a structure, run operations, and watch the state update live."),
                onboardingItem("Compare",
                        "Benchmark multiple implementations side by side."),
                onboardingItem("Algorithm Lab",
                        "Run graph algorithms step-by-step with visual playback."),
                onboardingItem("Learn",
                        "Browse structure families, algorithms, and a usage guide."));

        Label tip = styledLabel(
                "Try this first: pick a Stack in Explore, open a session, and push a few values.",
                "onboarding-tip");
        tip.setWrapText(true);

        CheckBox dontShow = styledCheck("Don\u2019t show this again", false);

        Button getStarted = new Button("Get Started");
        getStarted.getStyleClass().add("primary-button");

        HBox bottom = new HBox(12, dontShow, new Region(), getStarted);
        HBox.setHgrow(bottom.getChildren().get(1), Priority.ALWAYS);
        bottom.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(title, tagline, pages, tip, bottom);

        StackPane overlay = new StackPane(card);
        overlay.getStyleClass().add("onboarding-overlay");
        overlay.setAlignment(Pos.CENTER);

        getStarted.setOnAction(e -> {
            if (dontShow.isSelected()) {
                settings.setOnboardingDismissed(true);
            }
            pageHost.getChildren().remove(overlay);
        });

        pageHost.getChildren().add(overlay);
    }

    private HBox onboardingItem(String page, String desc) {
        Label bullet = styledLabel("\u25b8 " + page, "onboarding-page-name");
        bullet.setMinWidth(120);
        Label description = styledLabel(desc, "onboarding-page-desc");
        description.setWrapText(true);
        HBox row = new HBox(8, bullet, description);
        row.setAlignment(Pos.TOP_LEFT);
        return row;
    }

    // ══════════════════════════════════════════════════════════
    //  Navigation
    // ══════════════════════════════════════════════════════════

    @FXML
    private void onNavigate(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String pageId = (String) btn.getUserData();
        navigateTo(NavigationPage.valueOf(pageId));
    }

    private void navigateTo(NavigationPage page) {
        if (page == currentPage) return;
        Node newPage = pageCache.get(page);
        currentPage = page;

        pageTitleLabel.setText(page.title());
        pageSubtitleLabel.setText(buildSubtitle(page));
        updateNavActive(page);
        updateModeBadge();

        if (page == NavigationPage.ACTIVITY) refreshActivityPage();

        if (settings.isMotionEnabled() && !pageHost.getChildren().isEmpty()) {
            newPage.setOpacity(0);
            pageHost.getChildren().setAll(newPage);
            FadeTransition ft = new FadeTransition(Duration.millis(180), newPage);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.setInterpolator(Interpolator.EASE_OUT);
            ft.play();
        } else {
            newPage.setOpacity(1);
            pageHost.getChildren().setAll(newPage);
        }

        setStatus(page.title() + " workspace active.");
    }

    private String buildSubtitle(NavigationPage page) {
        return switch (page) {
            case EXPLORE -> sessionActive
                    ? "Session — " + explSessStruct.getText()
                    : page.subtitle();
            case COMPARE -> comparisonActive
                    ? "Comparing — " + cmpSessLabel.getText()
                    : page.subtitle();
            default -> page.subtitle();
        };
    }

    private void updateNavActive(NavigationPage page) {
        NavigationPage[] pages = NavigationPage.values();
        for (int i = 0; i < navButtons.length && i < pages.length; i++) {
            navButtons[i].getStyleClass().removeAll("nav-btn-active");
            if (pages[i] == page) {
                navButtons[i].getStyleClass().add("nav-btn-active");
            }
        }
    }

    private void updateModeBadge() {
        if (sessionActive) {
            modeBadge.setText("Session Active");
            modeBadge.getStyleClass().setAll("mode-badge", "mode-session");
        } else if (comparisonActive) {
            modeBadge.setText("Comparing");
            modeBadge.getStyleClass().setAll("mode-badge", "mode-compare");
        } else {
            modeBadge.setText("Ready");
            modeBadge.getStyleClass().setAll("mode-badge");
        }
    }

    // ══════════════════════════════════════════════════════════
    //  Page Construction
    // ══════════════════════════════════════════════════════════

    private void buildAllPages() {
        pageCache.put(NavigationPage.EXPLORE
            , buildExplorePage());
        pageCache.put(NavigationPage.COMPARE, buildComparePage());
        pageCache.put(NavigationPage.ALGORITHM_LAB, buildAlgorithmLabPage());
        pageCache.put(NavigationPage.LEARN, buildLearnPage());
        pageCache.put(NavigationPage.ACTIVITY, buildActivityPage());
        pageCache.put(NavigationPage.SETTINGS, buildSettingsPage());
    }

    // ─────────────────────────────────────────────────────────
    //  EXPLORE PAGE
    // ─────────────────────────────────────────────────────────

    private Node buildExplorePage() {
        // Context panel
        VBox ctx = new VBox(8);
        ctx.getStyleClass().add("context-panel");
        ctx.setPrefWidth(260); ctx.setMinWidth(260); ctx.setMaxWidth(260);
        ctx.setPadding(new Insets(16, 12, 16, 12));

        explStructList = new ListView<>();
        VBox.setVgrow(explStructList, Priority.ALWAYS);
        setupStructureCellFactory(explStructList);

        explImplList = new ListView<>();
        explImplList.setPrefHeight(140);
        setupImplCellFactory(explImplList);

        explOpenBtn = new Button("Open Session");
        explOpenBtn.getStyleClass().add("primary-button");
        explOpenBtn.setMaxWidth(Double.MAX_VALUE);
        explOpenBtn.setDisable(true);
        explOpenBtn.setOnAction(e -> handleExploreOpenSession());

        VBox btnBox = new VBox(6, explOpenBtn);
        btnBox.setPadding(new Insets(4, 0, 0, 0));

        ctx.getChildren().addAll(
                styledLabel("STRUCTURES", "section-header"),
                explStructList,
                styledLabel("IMPLEMENTATIONS", "section-header"),
                explImplList,
                btnBox
        );

        // Workspace
        VBox ws = new VBox();
        ws.getStyleClass().add("workspace");
        HBox.setHgrow(ws, Priority.ALWAYS);

        VBox detailCard = card("STRUCTURE DETAILS");
        VBox detailBody = cardBody(detailCard);
        explDetailName = styledLabel("Select a structure from the list to see its details.", "detail-name");
        explDetailCat = styledLabel("", "info-label");
        explDetailDesc = styledLabel("", "detail-description");
        explDetailDesc.setWrapText(true);
        explDetailKw = styledLabel("", "detail-keywords");
        detailBody.getChildren().addAll(explDetailName, explDetailCat, explDetailDesc, explDetailKw);

        VBox stateCard = card("STRUCTURE STATE");
        VBox.setVgrow(stateCard, Priority.ALWAYS);
        VBox stateBody = cardBody(stateCard);
        VBox.setVgrow(stateBody, Priority.ALWAYS);
        explVisualHost = new VisualStateHost("Pick a structure and implementation, then click \"Open Session\" to see it live.");
        VBox.setVgrow(explVisualHost, Priority.ALWAYS);
        stateBody.getChildren().add(explVisualHost);

        VBox traceCard = card("TRACE LOG");
        VBox traceBody = cardBody(traceCard);
        explTrace = monoArea("Run an operation to see its step-by-step execution trace here.");
        explTrace.setPrefHeight(150);
        traceBody.getChildren().add(explTrace);

        VBox wsContent = new VBox(12, detailCard, stateCard, traceCard);
        wsContent.getStyleClass().add("workspace-content");
        wsContent.setPadding(new Insets(16, 20, 16, 20));
        VBox.setVgrow(wsContent, Priority.ALWAYS);
        ws.getChildren().add(wsContent);

        // Inspector
        VBox insp = buildExploreInspector();

        // Selection listeners
        explStructList.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> onExploreStructureSelected(sel));
        explImplList.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> updateExploreOpenButton());
        explOpList.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> onExploreOperationSelected(sel));

        // Load data
        explStructList.setItems(FXCollections.observableArrayList(service.getAllStructures()));

        return new HBox(ctx, ws, insp);
    }

    private VBox buildExploreInspector() {
        VBox insp = new VBox(8);
        insp.getStyleClass().add("inspector-panel");
        insp.setPrefWidth(280); insp.setMinWidth(280); insp.setMaxWidth(280);
        insp.setPadding(new Insets(16, 12, 16, 12));

        VBox sessCard = new VBox(4);
        sessCard.getStyleClass().add("inspector-card");
        explSessStruct = styledLabel("No active session — open one from the left panel", "info-label");
        explSessImpl = styledLabel("", "info-label");
        explSessOps = styledLabel("", "info-label");

        explResetBtn = secondaryButton("Reset");
        explResetBtn.setOnAction(e -> handleExploreReset());
        explCloseBtn = secondaryButton("Close");
        explCloseBtn.setOnAction(e -> handleExploreCloseSession());
        HBox sessBtns = buttonRow(explResetBtn, explCloseBtn);
        sessCard.getChildren().addAll(
                styledLabel("SESSION", "section-header"),
                explSessStruct, explSessImpl, explSessOps, sessBtns);

        explOpList = new ListView<>();
        explOpList.setPrefHeight(130);
        setupOperationCellFactory(explOpList);

        explArgField = new TextField();
        explArgField.setPromptText("select an operation");
        explArgField.setOnAction(e -> handleExploreExecute());
        HBox.setHgrow(explArgField, Priority.ALWAYS);

        explRunBtn = new Button("Run");
        explRunBtn.getStyleClass().add("primary-button");
        explRunBtn.setDisable(true);
        explRunBtn.setOnAction(e -> handleExploreExecute());
        HBox execRow = new HBox(6, explArgField, explRunBtn);
        execRow.setAlignment(Pos.CENTER_LEFT);

        explHistory = new ListView<>();
        VBox.setVgrow(explHistory, Priority.ALWAYS);

        insp.getChildren().addAll(
                sessCard, new Separator(),
                styledLabel("OPERATIONS", "section-header"), explOpList, execRow,
                new Separator(),
                styledLabel("ACTIVITY", "section-header"), explHistory);

        return insp;
    }

    // ── Explore handlers ────────────────────────────────────

    private void onExploreStructureSelected(StructureSummary selected) {
        explImplList.getItems().clear();
        if (selected == null) {
            explDetailName.setText("Select a structure from the list to see its details.");
            explDetailCat.setText("");
            explDetailDesc.setText("");
            explDetailKw.setText("");
            return;
        }
        explDetailName.setText(selected.name());
        explDetailCat.setText(selected.category());
        explDetailDesc.setText(selected.description() != null ? selected.description() : "");
        String kw = selected.keywords() != null && !selected.keywords().isEmpty()
                ? String.join("  ·  ", selected.keywords()) : "";
        explDetailKw.setText(kw);

        List<ImplementationSummary> impls = service.getImplementations(selected.id());
        explImplList.setItems(FXCollections.observableArrayList(impls));
        updateExploreOpenButton();
    }

    private void updateExploreOpenButton() {
        ImplementationSummary sel = explImplList.getSelectionModel().getSelectedItem();
        explOpenBtn.setDisable(sel == null || sessionActive || comparisonActive);
    }

    private void handleExploreOpenSession() {
        if (comparisonActive) {
            setStatus("Close the active comparison before opening a session.");
            return;
        }
        ImplementationSummary impl = explImplList.getSelectionModel().getSelectedItem();
        if (impl == null) { setStatus("Select an implementation first."); return; }

        try {
            SessionSnapshot snap = service.openSession(impl.parentStructureId(), impl.id());
            sessionActive = true;
            comparisonActive = false;
            explSessStruct.setText(snap.structureName());
            explSessImpl.setText(snap.implementationName());
            explSessOps.setText("Operations: " + snap.operationCount());
            refreshExploreState();
            refreshExploreOps();
            refreshExploreHistory();
            refreshExploreTrace();
            setExploreSessionButtons(true);
            updateModeBadge();
            if (currentPage == NavigationPage.EXPLORE) {
                pageSubtitleLabel.setText("Session — " + snap.structureName());
            }
            activityLog.log("Opened session",
                    snap.structureName() + " / " + snap.implementationName(), "session");
            setStatus("Session opened for " + snap.implementationName() + ".");
        } catch (Exception e) {
            showError("Failed to open session", e.getMessage());
        }
    }

    private void handleExploreCloseSession() {
        sessionActive = false;
        service.closeSession();
        clearExploreSession();
        updateModeBadge();
        if (currentPage == NavigationPage.EXPLORE) {
            pageSubtitleLabel.setText(NavigationPage.EXPLORE.subtitle());
        }
        activityLog.log("Closed session", "", "session");
        setStatus("Session closed.");
    }

    private void handleExploreReset() {
        try {
            service.resetSession();
            refreshExploreState();
            refreshExploreHistory();
            refreshExploreTrace();
            refreshExploreOpsCount();
            activityLog.log("Session reset", "", "session");
            setStatus("Session reset to empty state.");
        } catch (Exception e) {
            showError("Reset failed", e.getMessage());
        }
    }

    private void handleExploreExecute() {
        OperationInfo op = explOpList.getSelectionModel().getSelectedItem();
        if (op == null) { setStatus("Select an operation first."); return; }

        String raw = explArgField.getText().trim();
        List<String> args = raw.isEmpty() ? List.of()
                : Arrays.stream(raw.split("\\s+")).collect(Collectors.toList());

        if (args.size() < op.argCount()) {
            setStatus("Requires " + op.argCount() + " argument(s). Usage: " + op.usage());
            return;
        }
        if (args.size() > op.argCount()) {
            setStatus("Too many arguments. Expected " + op.argCount() + ". Usage: " + op.usage());
            return;
        }

        try {
            ExecutionResult result = service.executeOperation(op.name(), args);
            refreshExploreState();
            refreshExploreHistory();
            refreshExploreTrace();
            refreshExploreOpsCount();
            explArgField.clear();

            String msg = "Executed " + result.operationName();
            if (result.success() && result.returnedValue() != null
                    && !"null".equals(result.returnedValue())) {
                msg += " \u2192 " + result.returnedValue();
            } else if (!result.success()) {
                msg = "Failed: " + result.message();
            }
            activityLog.log(result.operationName(), msg, "operation");
            setStatus(msg);
        } catch (Exception e) {
            showError("Execution error", e.getMessage());
        }
    }

    private void onExploreOperationSelected(OperationInfo sel) {
        if (sel == null) { explArgField.setPromptText("args (e.g. 42)"); return; }
        String usage = sel.usage();
        String hint = usage.startsWith(sel.name() + " ")
                ? usage.substring(sel.name().length() + 1) : usage;
        explArgField.setPromptText(sel.argCount() == 0 ? "no args needed" : hint);
    }

    // ── Explore refresh helpers ─────────────────────────────

    private void refreshExploreState() {
        if (!service.hasActiveSession()) {
            explVisualHost.showPlaceholder();
            return;
        }
        String raw = service.getRawState();
        String rendered = service.getRenderedState();
        explVisualHost.render(raw, rendered);
    }

    private void refreshExploreTrace() {
        if (!service.hasActiveSession()) { explTrace.clear(); return; }
        if (settings.isShowRawTraces()) {
            explTrace.setText(service.getLastTraceRendered());
        } else {
            service.getLastResult().ifPresentOrElse(
                r -> explTrace.setText(r.success()
                        ? "\u2714 " + r.operationName() + " completed"
                        : "\u2716 " + r.operationName() + " failed: " + r.message()),
                () -> explTrace.clear()
            );
        }
    }

    private void refreshExploreHistory() {
        if (!service.hasActiveSession()) { explHistory.getItems().clear(); return; }
        List<ExecutionResult> history = service.getHistory();
        List<String> items = new ArrayList<>();
        for (int i = 0; i < history.size(); i++) {
            ExecutionResult r = history.get(i);
            String status = r.success() ? "\u2714" : "\u2716";
            String entry = status + "  " + (i + 1) + ". " + r.operationName();
            if (r.success() && r.returnedValue() != null && !"null".equals(r.returnedValue())) {
                entry += " \u2192 " + r.returnedValue();
            } else if (!r.success()) {
                entry += " \u2014 " + r.message();
            }
            items.add(entry);
        }
        explHistory.setItems(FXCollections.observableArrayList(items));
    }

    private void refreshExploreOps() {
        explOpList.setItems(FXCollections.observableArrayList(service.getAvailableOperations()));
    }

    private void refreshExploreOpsCount() {
        service.getSessionSnapshot().ifPresent(s ->
                explSessOps.setText("Operations: " + s.operationCount()));
    }

    private void setExploreSessionButtons(boolean active) {
        explOpenBtn.setDisable(active);
        explResetBtn.setDisable(!active);
        explCloseBtn.setDisable(!active);
        explRunBtn.setDisable(!active);
    }

    private void clearExploreSession() {
        sessionActive = false;
        explVisualHost.resetCache();
        explVisualHost.showPlaceholder();
        explSessStruct.setText("No active session \u2014 open one from the left panel");
        explSessImpl.setText("");
        explSessOps.setText("");
        explTrace.clear();
        explOpList.getItems().clear();
        explHistory.getItems().clear();
        explArgField.clear();
        setExploreSessionButtons(false);
    }

    // ─────────────────────────────────────────────────────────
    //  COMPARE PAGE
    // ─────────────────────────────────────────────────────────

    private Node buildComparePage() {
        // Context panel
        VBox ctx = new VBox(8);
        ctx.getStyleClass().add("context-panel");
        ctx.setPrefWidth(260); ctx.setMinWidth(260); ctx.setMaxWidth(260);
        ctx.setPadding(new Insets(16, 12, 16, 12));

        Label hint = styledLabel("Structures with 2+ implementations", "info-label");
        hint.setWrapText(true);

        cmpStructList = new ListView<>();
        VBox.setVgrow(cmpStructList, Priority.ALWAYS);
        setupStructureCellFactory(cmpStructList);

        cmpStartBtn = new Button("Start Comparison");
        cmpStartBtn.getStyleClass().add("accent-button");
        cmpStartBtn.setMaxWidth(Double.MAX_VALUE);
        cmpStartBtn.setDisable(true);
        cmpStartBtn.setOnAction(e -> handleCompareStart());

        VBox btnBox = new VBox(6, cmpStartBtn);
        btnBox.setPadding(new Insets(4, 0, 0, 0));

        ctx.getChildren().addAll(
                styledLabel("STRUCTURES", "section-header"),
                hint, cmpStructList, btnBox);

        // Workspace — visual card grid
        VBox ws = new VBox();
        ws.getStyleClass().add("workspace");
        HBox.setHgrow(ws, Priority.ALWAYS);

        cmpSummary = new ComparisonSummaryPane();

        cmpCardGrid = new VBox(14);
        cmpCardGrid.getStyleClass().add("comparison-card-grid");

        Label emptyHint = styledLabel(
                "Choose a structure from the left, then click \"Start Comparison\" to benchmark its implementations.",
                "detail-description");
        emptyHint.setWrapText(true);
        emptyHint.setPadding(new Insets(24));
        cmpCardGrid.getChildren().add(emptyHint);

        VBox wsContent = new VBox(14, cmpSummary, cmpCardGrid);
        wsContent.getStyleClass().add("workspace-content");
        wsContent.setPadding(new Insets(16, 20, 16, 20));
        VBox.setVgrow(wsContent, Priority.ALWAYS);

        ScrollPane wsScroll = new ScrollPane(wsContent);
        wsScroll.setFitToWidth(true);
        wsScroll.getStyleClass().add("visual-scroll");
        VBox.setVgrow(wsScroll, Priority.ALWAYS);
        ws.getChildren().add(wsScroll);

        // Inspector
        VBox insp = buildCompareInspector();

        // Listeners
        cmpStructList.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> onCompareStructureSelected(sel));
        cmpOpList.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> onCompareOperationSelected(sel));

        cmpStructList.setItems(FXCollections.observableArrayList(service.getComparableStructures()));

        return new HBox(ctx, ws, insp);
    }

    private VBox buildCompareInspector() {
        VBox insp = new VBox(8);
        insp.getStyleClass().add("inspector-panel");
        insp.setPrefWidth(280); insp.setMinWidth(280); insp.setMaxWidth(280);
        insp.setPadding(new Insets(16, 12, 16, 12));

        VBox sessCard = new VBox(4);
        sessCard.getStyleClass().add("inspector-card");
        cmpSessLabel = styledLabel("No active comparison \u2014 pick a structure to begin", "info-label");
        cmpImplCount = styledLabel("", "info-label");
        cmpOpsCount = styledLabel("", "info-label");

        cmpResetBtn = secondaryButton("Reset All");
        cmpResetBtn.setOnAction(e -> handleCompareReset());
        cmpCloseBtn = secondaryButton("Close");
        cmpCloseBtn.setOnAction(e -> handleCompareClose());
        cmpExportBtn = secondaryButton("Export");
        cmpExportBtn.setOnAction(e -> exportCompareHistory());
        HBox sessBtns = buttonRow(cmpResetBtn, cmpCloseBtn, cmpExportBtn);
        sessCard.getChildren().addAll(
                styledLabel("COMPARISON", "section-header"),
                cmpSessLabel, cmpImplCount, cmpOpsCount, sessBtns);

        cmpOpList = new ListView<>();
        cmpOpList.setPrefHeight(130);
        setupOperationCellFactory(cmpOpList);

        cmpArgField = new TextField();
        cmpArgField.setPromptText("select an operation");
        cmpArgField.setOnAction(e -> handleCompareExecute());
        HBox.setHgrow(cmpArgField, Priority.ALWAYS);

        cmpRunBtn = new Button("Run All");
        cmpRunBtn.getStyleClass().add("primary-button");
        cmpRunBtn.setDisable(true);
        cmpRunBtn.setOnAction(e -> handleCompareExecute());
        HBox execRow = new HBox(6, cmpArgField, cmpRunBtn);
        execRow.setAlignment(Pos.CENTER_LEFT);

        cmpHistory = new ListView<>();
        VBox.setVgrow(cmpHistory, Priority.ALWAYS);

        insp.getChildren().addAll(
                sessCard, new Separator(),
                styledLabel("OPERATIONS", "section-header"), cmpOpList, execRow,
                new Separator(),
                styledLabel("HISTORY", "section-header"), cmpHistory);

        return insp;
    }

    // ── Compare handlers ────────────────────────────────────

    private void onCompareStructureSelected(StructureSummary selected) {
        cmpStartBtn.setDisable(selected == null || comparisonActive);
    }

    private void handleCompareStart() {
        if (sessionActive) {
            setStatus("Close the active session before starting a comparison.");
            return;
        }
        StructureSummary sel = cmpStructList.getSelectionModel().getSelectedItem();
        if (sel == null) { setStatus("Select a structure first."); return; }

        try {
            ComparisonSession cs = service.openComparisonSession(sel.id(), List.of());
            comparisonActive = true;
            sessionActive = false;

            cmpSessLabel.setText(cs.getStructureName());
            cmpImplCount.setText(cs.entryCount() + " implementations");
            cmpOpsCount.setText("Operations: 0");

            cmpSummary.updateSession(cs.getStructureName(), cs.entryCount());
            buildComparisonCards(cs);

            List<OperationInfo> ops = cs.getCommonOperations().stream()
                    .map(o -> new OperationInfo(
                            o.name(), o.aliases(), o.description(),
                            o.argCount(), o.usage(), o.mutates(), o.complexityNote()))
                    .toList();
            cmpOpList.setItems(FXCollections.observableArrayList(ops));
            cmpHistory.setItems(FXCollections.observableArrayList());

            setCompareSessionButtons(true);
            updateModeBadge();
            if (currentPage == NavigationPage.COMPARE) {
                pageSubtitleLabel.setText("Comparing — " + cs.getStructureName());
            }
            activityLog.log("Started comparison",
                    cs.getStructureName() + " — " + cs.entryCount() + " implementations", "comparison");
            setStatus("Comparison started for " + cs.getStructureName() + ".");
        } catch (Exception e) {
            showError("Failed to start comparison", e.getMessage());
        }
    }

    private void handleCompareClose() {
        comparisonActive = false;
        service.closeSession();
        clearCompareSession();
        updateModeBadge();
        if (currentPage == NavigationPage.COMPARE) {
            pageSubtitleLabel.setText(NavigationPage.COMPARE.subtitle());
        }
        activityLog.log("Closed comparison", "", "comparison");
        setStatus("Comparison closed.");
    }

    private void handleCompareReset() {
        try {
            service.resetComparisonSession();
            ComparisonSession cs = service.requireComparisonSession();
            buildComparisonCards(cs);
            cmpSummary.updateSession(cs.getStructureName(), cs.entryCount());
            cmpHistory.setItems(FXCollections.observableArrayList());
            cmpOpsCount.setText("Operations: 0");
            activityLog.log("Comparison reset", "", "comparison");
            setStatus("All implementations reset.");
        } catch (Exception e) {
            showError("Reset failed", e.getMessage());
        }
    }

    private void handleCompareExecute() {
        OperationInfo op = cmpOpList.getSelectionModel().getSelectedItem();
        if (op == null) { setStatus("Select an operation first."); return; }

        String raw = cmpArgField.getText().trim();
        List<String> args = raw.isEmpty() ? List.of()
                : Arrays.stream(raw.split("\\s+")).collect(Collectors.toList());

        if (args.size() < op.argCount()) {
            setStatus("Requires " + op.argCount() + " argument(s). Usage: " + op.usage());
            return;
        }
        if (args.size() > op.argCount()) {
            setStatus("Too many arguments. Expected " + op.argCount() + ". Usage: " + op.usage());
            return;
        }

        try {
            ComparisonOperationResult result =
                    service.executeComparisonOperation(op.name(), args);
            ComparisonSession cs = service.requireComparisonSession();
            ComparisonAnalysis analysis = ComparisonAnalysis.of(result);

            refreshComparisonCards(result, cs, analysis);

            String verdictText;
            String verdictStyle;
            switch (analysis.getVerdict()) {
                case MATCHING -> { verdictText = "MATCHING"; verdictStyle = "comparison-status-ok"; }
                case DIVERGENT -> { verdictText = "DIVERGENT"; verdictStyle = "comparison-status-divergent"; }
                case PARTIAL_FAIL -> { verdictText = "PARTIAL FAIL"; verdictStyle = "comparison-status-fail"; }
                default -> { verdictText = "UNKNOWN"; verdictStyle = "comparison-status-idle"; }
            }
            cmpSummary.updateAfterOperation(cs.historySize(), verdictText, verdictStyle,
                    analysis.timingSummary());
            cmpOpsCount.setText("Operations: " + cs.historySize());
            cmpArgField.clear();

            List<ComparisonOperationResult> history = cs.getHistory();
            List<String> items = new ArrayList<>();
            for (int i = 0; i < history.size(); i++) {
                ComparisonOperationResult h = history.get(i);
                ComparisonAnalysis ha = ComparisonAnalysis.of(h);
                String icon = switch (ha.getVerdict()) {
                    case MATCHING -> "\u2714";
                    case DIVERGENT -> "\u2194";
                    case PARTIAL_FAIL -> "\u26a0";
                };
                String argsStr = h.args().isEmpty() ? "" : " " + String.join(" ", h.args());
                items.add(icon + "  " + (i + 1) + ". " + h.operationName() + argsStr);
            }
            cmpHistory.setItems(FXCollections.observableArrayList(items));

            String msg;
            if (analysis.getVerdict() == ComparisonAnalysis.OverallVerdict.MATCHING) {
                msg = "Compared: " + op.name() + " — all matching";
            } else if (analysis.getVerdict() == ComparisonAnalysis.OverallVerdict.DIVERGENT) {
                msg = "Compared: " + op.name() + " — divergence detected";
            } else {
                long fails = result.entryResults().stream().filter(e -> !e.success()).count();
                msg = "Compared: " + op.name() + " — " + fails + " failed";
            }
            activityLog.log(op.name(), msg, "comparison");
            setStatus(msg);
        } catch (Exception e) {
            showError("Comparison error", e.getMessage());
        }
    }

    private void onCompareOperationSelected(OperationInfo sel) {
        if (sel == null) { cmpArgField.setPromptText("select an operation"); return; }
        String usage = sel.usage();
        String hint = usage.startsWith(sel.name() + " ")
                ? usage.substring(sel.name().length() + 1) : usage;
        cmpArgField.setPromptText(sel.argCount() == 0 ? "no args needed" : hint);
    }

    // ── Compare visual card helpers ─────────────────────────

    private void buildComparisonCards(ComparisonSession cs) {
        cmpCards.clear();
        cmpCardGrid.getChildren().clear();
        for (ComparisonRuntimeEntry entry : cs.getEntries()) {
            ComparisonCardPane card = new ComparisonCardPane();
            String rawSnapshot = entry.getRuntime().getCurrentState();
            String renderedState = entry.getRuntime().renderCurrentState();
            card.updateState(entry.getImplementationName(), rawSnapshot, renderedState);
            cmpCards.add(card);
            cmpCardGrid.getChildren().add(card);
        }
    }

    private void refreshComparisonCards(ComparisonOperationResult result, ComparisonSession cs,
                                         ComparisonAnalysis analysis) {
        List<ComparisonEntryResult> entryResults = result.entryResults();
        List<ComparisonRuntimeEntry> entries = cs.getEntries();
        for (int i = 0; i < entryResults.size() && i < cmpCards.size(); i++) {
            ComparisonEntryResult er = entryResults.get(i);
            ComparisonRuntimeEntry entry = entries.get(i);

            String rawSnapshot = entry.getRuntime().getCurrentState();
            String renderedState = entry.getRuntime().renderCurrentState();

            // Build trace text from trace steps
            StringBuilder traceText = new StringBuilder();
            for (TraceStep step : er.traceSteps()) {
                traceText.append(step.format()).append("\n");
            }
            if (!er.success() && er.message() != null) {
                traceText.append("Error: ").append(er.message()).append("\n");
            }

            cmpCards.get(i).updateResult(
                    er.implementationName(),
                    er.success(),
                    er.returnedValue(),
                    rawSnapshot,
                    renderedState,
                    er.traceSteps().size(),
                    traceText.toString(),
                    cs.historySize(),
                    er.formattedDuration(),
                    analysis.isFastest(er),
                    analysis.hasDivergences()
            );
        }
    }

    private void setCompareSessionButtons(boolean active) {
        cmpStartBtn.setDisable(active);
        cmpResetBtn.setDisable(!active);
        cmpCloseBtn.setDisable(!active);
        cmpExportBtn.setDisable(!active);
        cmpRunBtn.setDisable(!active);
    }

    private void clearCompareSession() {
        comparisonActive = false;
        cmpSessLabel.setText("No active comparison — pick a structure to begin");
        cmpImplCount.setText("");
        cmpOpsCount.setText("");
        cmpSummary.updateIdle();
        cmpCards.clear();
        cmpCardGrid.getChildren().clear();
        Label emptyHint = styledLabel(
                "Choose a structure from the left, then click \"Start Comparison\" to benchmark its implementations.",
                "detail-description");
        emptyHint.setWrapText(true);
        emptyHint.setPadding(new Insets(24));
        cmpCardGrid.getChildren().add(emptyHint);
        cmpOpList.getItems().clear();
        cmpHistory.getItems().clear();
        cmpArgField.clear();
        setCompareSessionButtons(false);
    }

    // ─────────────────────────────────────────────────────────
    //  ALGORITHM LAB PAGE
    // ─────────────────────────────────────────────────────────

    private Node buildAlgorithmLabPage() {
        AlgorithmLabController algoLab = new AlgorithmLabController(settings);
        return algoLab.buildWorkspace();
    }

    // ─────────────────────────────────────────────────────────
    //  LEARN PAGE
    // ─────────────────────────────────────────────────────────

    private Node buildLearnPage() {
        VBox content = new VBox(20);
        content.getStyleClass().add("page-content");
        content.setPadding(new Insets(36, 48, 36, 48));

        // Hero
        int totalImpls = service.getAllStructures().stream()
                .mapToInt(s -> service.getImplementations(s.id()).size()).sum();
        Label heroTitle = styledLabel("Learn", "hero-title");
        Label heroSub = styledLabel(
                service.getAllStructures().size() + " structure families, "
                + totalImpls + " implementations, "
                + GraphAlgorithmCatalog.all().size() + " graph algorithms.",
                "hero-subtitle");
        heroSub.setWrapText(true);
        VBox hero = new VBox(8, heroTitle, heroSub);
        hero.getStyleClass().add("hero-section");

        // Segmented tab bar
        ToggleGroup tabGroup = new ToggleGroup();
        ToggleButton structBtn = new ToggleButton("Structures");
        ToggleButton algoBtn = new ToggleButton("Algorithms");
        ToggleButton guideBtn = new ToggleButton("How to Use");
        structBtn.setToggleGroup(tabGroup);
        algoBtn.setToggleGroup(tabGroup);
        guideBtn.setToggleGroup(tabGroup);
        structBtn.getStyleClass().add("learn-tab-btn");
        algoBtn.getStyleClass().add("learn-tab-btn");
        guideBtn.getStyleClass().add("learn-tab-btn");
        structBtn.setSelected(true);
        HBox tabBar = new HBox(0, structBtn, algoBtn, guideBtn);
        tabBar.getStyleClass().add("learn-tab-bar");
        tabBar.setAlignment(Pos.CENTER_LEFT);

        // Build tab content
        learnStructuresTab = buildLearnStructuresTab();
        learnAlgorithmsTab = buildLearnAlgorithmsTab();
        learnGuideTab = buildLearnGuideTab();

        learnTabHost = new StackPane(learnStructuresTab);

        tabGroup.selectedToggleProperty().addListener((obs, old, sel) -> {
            if (sel == null) { if (old != null) old.setSelected(true); return; }
            if (sel == structBtn) learnTabHost.getChildren().setAll(learnStructuresTab);
            else if (sel == algoBtn) learnTabHost.getChildren().setAll(learnAlgorithmsTab);
            else learnTabHost.getChildren().setAll(learnGuideTab);
        });

        content.getChildren().addAll(hero, tabBar, learnTabHost);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("page-scroll");
        return scroll;
    }

    // ── Learn: Structures tab ────────────────────────────────

    private Node buildLearnStructuresTab() {
        VBox tab = new VBox(20);

        learnSearchField = new TextField();
        learnSearchField.setPromptText("Search structures by name, keyword, or description...");
        learnSearchField.getStyleClass().add("learn-search-field");
        learnSearchField.setPrefWidth(320);

        List<StructureSummary> all = service.getAllStructures();
        Set<String> categories = all.stream().map(StructureSummary::category)
                .collect(Collectors.toCollection(TreeSet::new));
        learnCategoryFilter = new ComboBox<>(FXCollections.observableArrayList(
                new ArrayList<>() {{ add("All Categories"); addAll(categories); }}));
        learnCategoryFilter.setValue("All Categories");
        learnCategoryFilter.getStyleClass().add("learn-category-filter");

        HBox filterBar = new HBox(12, learnSearchField, learnCategoryFilter);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        learnSearchField.textProperty().addListener((obs, o, n) -> refreshLearnCards());
        learnCategoryFilter.valueProperty().addListener((obs, o, n) -> refreshLearnCards());

        learnCardContainer = new VBox(28);
        refreshLearnCards();

        tab.getChildren().addAll(filterBar, learnCardContainer);
        return tab;
    }

    // ── Learn: Algorithms tab ────────────────────────────────

    private Node buildLearnAlgorithmsTab() {
        VBox tab = new VBox(24);

        Label intro = styledLabel(
                "Graph algorithms available in the Algorithm Lab. "
                + "Select a preset graph, pick an algorithm, and step through execution with visual playback.",
                "hero-subtitle");
        intro.setWrapText(true);

        List<GraphAlgorithmSpec> specs = GraphAlgorithmCatalog.all();
        Map<GraphAlgorithmSpec.Category, List<GraphAlgorithmSpec>> byCategory =
                specs.stream().collect(Collectors.groupingBy(
                        GraphAlgorithmSpec::category,
                        LinkedHashMap::new,
                        Collectors.toList()));

        for (var entry : byCategory.entrySet()) {
            tab.getChildren().add(
                    styledLabel(entry.getKey().label().toUpperCase(), "category-header"));
            FlowPane grid = new FlowPane(16, 16);
            grid.getStyleClass().add("card-grid");
            for (GraphAlgorithmSpec spec : entry.getValue()) {
                grid.getChildren().add(buildAlgorithmLearnCard(spec));
            }
            tab.getChildren().add(grid);
        }

        tab.getChildren().add(0, intro);

        Button openLab = new Button("Open Algorithm Lab");
        openLab.getStyleClass().add("primary-button");
        openLab.setOnAction(e -> navigateTo(NavigationPage.ALGORITHM_LAB));
        tab.getChildren().add(openLab);

        return tab;
    }

    private VBox buildAlgorithmLearnCard(GraphAlgorithmSpec spec) {
        VBox card = new VBox(8);
        card.getStyleClass().add("learn-card");
        card.setPrefWidth(360);
        card.setMinWidth(300);
        card.setPadding(new Insets(18));

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        titleRow.getChildren().add(styledLabel(spec.displayLabel(), "learn-card-title"));
        Label catBadge = styledLabel(spec.category().label(), "learn-compare-badge");
        titleRow.getChildren().add(catBadge);
        card.getChildren().add(titleRow);

        Label desc = styledLabel(spec.hint(), "learn-card-desc");
        desc.setWrapText(true);
        card.getChildren().add(desc);

        // Requirements
        VBox reqs = new VBox(4);
        reqs.getChildren().add(styledLabel("REQUIREMENTS", "learn-card-section"));
        reqs.getChildren().add(styledLabel(
                "Source node: " + (spec.sourceRequired() ? "Required" : "Not needed"),
                "info-label"));
        String targetText = switch (spec.targetMode()) {
            case NONE -> "Not used";
            case OPTIONAL -> "Optional";
            case REQUIRED -> "Required";
        };
        reqs.getChildren().add(styledLabel("Target node: " + targetText, "info-label"));

        String graphTypes = (spec.directedOk() && spec.undirectedOk()) ? "Directed & Undirected"
                : spec.directedOk() ? "Directed only"
                : "Undirected only";
        reqs.getChildren().add(styledLabel("Graph types: " + graphTypes, "info-label"));

        card.getChildren().addAll(new Separator(), reqs);

        // Quick action
        Button tryBtn = new Button("Try in Algorithm Lab");
        tryBtn.getStyleClass().add("learn-action-btn");
        tryBtn.setOnAction(e -> navigateTo(NavigationPage.ALGORITHM_LAB));
        card.getChildren().addAll(new Separator(), tryBtn);

        return card;
    }

    // ── Learn: How to Use tab ────────────────────────────────

    private Node buildLearnGuideTab() {
        VBox tab = new VBox(24);

        Label intro = styledLabel(
                "A quick tour of StructLab\u2019s pages and how to get the most out of each one.",
                "hero-subtitle");
        intro.setWrapText(true);
        tab.getChildren().add(intro);

        tab.getChildren().addAll(
                guideSection("Explore",
                        "Interact with a single data structure in real time.",
                        "1. Pick a structure family from the left panel (e.g. Stack).\n"
                        + "2. Choose an implementation and click \"Open Session\".\n"
                        + "3. Select an operation, enter arguments, and click Run.\n"
                        + "4. Watch the visual state update and read the trace log."),
                guideSection("Compare",
                        "Benchmark multiple implementations of the same structure.",
                        "1. Select a structure family that supports comparison.\n"
                        + "2. Click \"Start Comparison\" \u2014 all implementations open side by side.\n"
                        + "3. Run operations and compare execution time, state output, and traces."),
                guideSection("Algorithm Lab",
                        "Step through graph algorithms with animated playback.",
                        "1. Choose a preset graph or load your own.\n"
                        + "2. Pick an algorithm (BFS, Dijkstra, Prim, etc.).\n"
                        + "3. Set source/target nodes as needed, then click Run.\n"
                        + "4. Use the playback controls to step forward/back through frames."),
                guideSection("Learn",
                        "You\u2019re here! Browse structures, algorithms, and this guide.",
                        "Use the Structures tab to search and filter all structure families.\n"
                        + "The Algorithms tab lists every graph algorithm with its requirements.\n"
                        + "Each card has quick-action buttons to jump to Explore or Compare."),
                guideSection("Activity",
                        "Track your session history.",
                        "Every session, comparison, and operation is logged here.\n"
                        + "Filter by category or export the log to a file."),
                guideSection("Settings",
                        "Customize transitions, density, trace detail, and playback speed.",
                        "Toggle motion, compact mode, or raw traces.\n"
                        + "Adjust default playback speed for the Algorithm Lab.\n"
                        + "Restore all settings to defaults with one click."));

        return tab;
    }

    private VBox guideSection(String title, String subtitle, String steps) {
        VBox section = new VBox(8);
        section.getStyleClass().add("learn-card");
        section.setPadding(new Insets(18));
        section.setPrefWidth(600);

        section.getChildren().add(styledLabel(title, "learn-card-title"));
        Label sub = styledLabel(subtitle, "learn-card-desc");
        sub.setWrapText(true);
        section.getChildren().add(sub);

        Label body = styledLabel(steps, "info-label");
        body.setWrapText(true);
        section.getChildren().addAll(new Separator(), body);
        return section;
    }

    private void refreshLearnCards() {
        learnCardContainer.getChildren().clear();
        List<StructureSummary> all = service.getAllStructures();
        String query = learnSearchField.getText() != null ? learnSearchField.getText().trim().toLowerCase(Locale.ROOT) : "";
        String catFilter = learnCategoryFilter.getValue();

        // Filter
        List<StructureSummary> filtered = all.stream().filter(s -> {
            if (!"All Categories".equals(catFilter) && !s.category().equals(catFilter)) return false;
            if (query.isEmpty()) return true;
            if (s.name().toLowerCase(Locale.ROOT).contains(query)) return true;
            if (s.description() != null && s.description().toLowerCase(Locale.ROOT).contains(query)) return true;
            if (s.keywords() != null && s.keywords().stream().anyMatch(k -> k.toLowerCase(Locale.ROOT).contains(query))) return true;
            if (s.behavior() != null && s.behavior().toLowerCase(Locale.ROOT).contains(query)) return true;
            return false;
        }).toList();

        // Group by category
        Map<String, List<StructureSummary>> byCategory = filtered.stream()
                .collect(Collectors.groupingBy(
                        StructureSummary::category,
                        LinkedHashMap::new,
                        Collectors.toList()));

        if (filtered.isEmpty()) {
            Label empty = styledLabel("No structures match your search. Try a broader term or reset the category filter.", "detail-description");
            empty.setPadding(new Insets(24));
            learnCardContainer.getChildren().add(empty);
            return;
        }

        for (Map.Entry<String, List<StructureSummary>> cat : byCategory.entrySet()) {
            learnCardContainer.getChildren().add(
                    styledLabel(cat.getKey().toUpperCase() + " STRUCTURES", "category-header"));
            FlowPane grid = new FlowPane(16, 16);
            grid.getStyleClass().add("card-grid");
            for (StructureSummary s : cat.getValue()) {
                grid.getChildren().add(buildLearnCard(s));
            }
            learnCardContainer.getChildren().add(grid);
        }
    }

    private VBox buildLearnCard(StructureSummary s) {
        VBox card = new VBox(8);
        card.getStyleClass().add("learn-card");
        card.setPrefWidth(360);
        card.setMinWidth(300);
        card.setPadding(new Insets(18));

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        Label name = styledLabel(s.name(), "learn-card-title");
        titleRow.getChildren().add(name);
        if (service.isComparable(s.id())) {
            Label badge = styledLabel("Comparable", "learn-compare-badge");
            titleRow.getChildren().add(badge);
        }
        Label desc = styledLabel(s.description() != null ? s.description() : "", "learn-card-desc");
        desc.setWrapText(true);

        card.getChildren().addAll(titleRow, desc);

        // Behavior section
        if (s.behavior() != null && !s.behavior().isBlank()) {
            Label behaviorHeader = styledLabel("BEHAVIOR", "learn-card-section");
            Label behaviorText = styledLabel(s.behavior(), "learn-card-behavior");
            behaviorText.setWrapText(true);
            card.getChildren().addAll(new Separator(), behaviorHeader, behaviorText);
        }

        // Learning notes section
        if (s.learningNotes() != null && !s.learningNotes().isBlank()) {
            Label notesHeader = styledLabel("LEARNING NOTES", "learn-card-section");
            Label notesText = styledLabel(s.learningNotes(), "learn-card-notes");
            notesText.setWrapText(true);
            card.getChildren().addAll(new Separator(), notesHeader, notesText);
        }

        // Complexity matrix
        List<ImplementationSummary> impls = service.getImplementations(s.id());
        ComplexityMatrix matrix = ComplexityMatrix.build(impls);
        if (!matrix.rows().isEmpty()) {
            card.getChildren().add(new Separator());
            card.getChildren().add(styledLabel("COMPLEXITY MATRIX", "learn-card-section"));
            card.getChildren().add(buildComplexityTable(matrix));
        }

        // Implementations list (name + description + space)
        if (!impls.isEmpty()) {
            VBox implBox = new VBox(6);
            for (ImplementationSummary impl : impls) {
                Label implName = styledLabel(impl.name(), "learn-impl-name");
                implBox.getChildren().add(implName);
                if (impl.description() != null && !impl.description().isBlank()) {
                    Label implDesc = styledLabel(impl.description(), "learn-impl-desc");
                    implDesc.setWrapText(true);
                    implBox.getChildren().add(implDesc);
                }
                if (impl.spaceComplexity() != null && !impl.spaceComplexity().isEmpty()) {
                    Label space = styledLabel("Space: " + impl.spaceComplexity(),
                            "learn-complexity-detail");
                    implBox.getChildren().add(space);
                }
            }
            card.getChildren().addAll(new Separator(),
                    styledLabel("IMPLEMENTATIONS", "learn-card-section"), implBox);
        }

        // Keywords
        String kwText = s.keywords() != null && !s.keywords().isEmpty()
                ? String.join("  \u00b7  ", s.keywords()) : "";
        if (!kwText.isEmpty()) {
            card.getChildren().addAll(new Separator(), styledLabel(kwText, "learn-card-keywords"));
        }

        // Quick actions
        HBox actions = new HBox(8);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.setPadding(new Insets(4, 0, 0, 0));

        Button exploreBtn = new Button("Open in Explore");
        exploreBtn.getStyleClass().add("learn-action-btn");
        exploreBtn.setOnAction(e -> {
            explStructList.getSelectionModel().select(
                    explStructList.getItems().stream()
                            .filter(st -> st.id().equals(s.id()))
                            .findFirst().orElse(null));
            navigateTo(NavigationPage.EXPLORE);
        });
        actions.getChildren().add(exploreBtn);

        if (service.isComparable(s.id())) {
            Button compareBtn = new Button("Compare Implementations");
            compareBtn.getStyleClass().add("learn-action-btn");
            compareBtn.setOnAction(e -> {
                cmpStructList.getSelectionModel().select(
                        cmpStructList.getItems().stream()
                                .filter(st -> st.id().equals(s.id()))
                                .findFirst().orElse(null));
                navigateTo(NavigationPage.COMPARE);
            });
            actions.getChildren().add(compareBtn);
        }

        card.getChildren().addAll(new Separator(), actions);
        return card;
    }

    private GridPane buildComplexityTable(ComplexityMatrix matrix) {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("complexity-table");
        grid.setHgap(2);
        grid.setVgap(2);

        List<String> implNames = matrix.implementationNames();

        // Header row: "Time Complexity" | impl1 | impl2 | ...
        grid.add(styledLabel("Time Complexity", "complexity-header"), 0, 0);
        for (int col = 0; col < implNames.size(); col++) {
            grid.add(styledLabel(implNames.get(col), "complexity-header"), col + 1, 0);
        }

        // Data rows
        int row = 1;
        for (ComplexityMatrix.Row r : matrix.rows()) {
            grid.add(styledLabel(r.operation(), "complexity-op"), 0, row);
            for (int col = 0; col < implNames.size(); col++) {
                String val = r.byImplementation().getOrDefault(implNames.get(col), "\u2014");
                grid.add(styledLabel(val, "complexity-cell"), col + 1, row);
            }
            row++;
        }

        // Space row
        grid.add(styledLabel("Space", "complexity-op"), 0, row);
        for (int col = 0; col < implNames.size(); col++) {
            String val = matrix.spaceByImplementation()
                    .getOrDefault(implNames.get(col), "\u2014");
            grid.add(styledLabel(val, "complexity-cell"), col + 1, row);
        }

        return grid;
    }

    // ─────────────────────────────────────────────────────────
    //  ACTIVITY PAGE
    // ─────────────────────────────────────────────────────────

    private Node buildActivityPage() {
        VBox content = new VBox(20);
        content.getStyleClass().add("page-content");
        content.setPadding(new Insets(36, 48, 36, 48));

        Label heroTitle = styledLabel("Recent Activity", "hero-title");
        Label heroSub = styledLabel(
                "Your session history and recent actions in this workspace.", "hero-subtitle");
        heroSub.setWrapText(true);
        VBox hero = new VBox(8, heroTitle, heroSub);
        hero.getStyleClass().add("hero-section");

        // Filter + action bar
        activityCategoryFilter = new ComboBox<>(FXCollections.observableArrayList("All"));
        activityCategoryFilter.setValue("All");
        activityCategoryFilter.getStyleClass().add("learn-category-filter");
        activityCategoryFilter.valueProperty().addListener((obs, o, n) -> refreshActivityPage());

        Button clearBtn = new Button("Clear");
        clearBtn.getStyleClass().add("action-btn");
        clearBtn.setOnAction(e -> {
            activityLog.clear();
            refreshActivityPage();
            setStatus("Activity log cleared.");
        });

        Button exportBtn = new Button("Export");
        exportBtn.getStyleClass().add("action-btn");
        exportBtn.setOnAction(e -> exportActivityLog());

        HBox toolbar = new HBox(12, activityCategoryFilter, new Region(), clearBtn, exportBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(toolbar.getChildren().get(1), Priority.ALWAYS);

        activityFeed = new VBox(0);
        activityFeed.getStyleClass().add("activity-feed");

        content.getChildren().addAll(hero, toolbar, activityFeed);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("page-scroll");
        return scroll;
    }

    private void refreshActivityPage() {
        if (activityFeed == null) return;
        activityFeed.getChildren().clear();

        // Update category filter options
        Set<String> cats = activityLog.getCategories();
        List<String> catOptions = new ArrayList<>();
        catOptions.add("All");
        catOptions.addAll(cats);
        String selected = activityCategoryFilter.getValue();
        activityCategoryFilter.setItems(FXCollections.observableArrayList(catOptions));
        activityCategoryFilter.setValue(catOptions.contains(selected) ? selected : "All");

        String filter = activityCategoryFilter.getValue();
        List<ActivityLog.Entry> recent = "All".equals(filter)
                ? activityLog.getRecent(50) : activityLog.getByCategory(filter);

        if (recent.isEmpty()) {
            Label empty = styledLabel(
                    "No activity yet. Explore a structure or run a comparison \u2014 your session history will appear here.",
                    "empty-state-text");
            empty.setWrapText(true);
            empty.setPadding(new Insets(24));
            activityFeed.getChildren().add(empty);
            return;
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");
        for (ActivityLog.Entry entry : recent) {
            HBox row = new HBox(12);
            row.getStyleClass().add("activity-entry");
            row.setPadding(new Insets(10, 16, 10, 16));
            row.setAlignment(Pos.CENTER_LEFT);

            String icon = switch (entry.category()) {
                case "session" -> "\u25cf";
                case "comparison" -> "\u25c6";
                case "operation" -> "\u25b8";
                case "navigation" -> "\u2192";
                default -> "\u00b7";
            };

            Label iconLabel = styledLabel(icon, "activity-icon");
            iconLabel.setMinWidth(18);

            VBox textBox = new VBox(2);
            HBox.setHgrow(textBox, Priority.ALWAYS);
            Label action = styledLabel(entry.action(), "activity-action");
            Label catLabel = styledLabel("[" + entry.category() + "]", "activity-category-badge");
            HBox actionRow = new HBox(8, action, catLabel);
            actionRow.setAlignment(Pos.CENTER_LEFT);
            textBox.getChildren().add(actionRow);
            if (!entry.detail().isEmpty()) {
                Label detail = styledLabel(entry.detail(), "activity-detail");
                detail.setWrapText(true);
                textBox.getChildren().add(detail);
            }

            Label time = styledLabel(entry.timestamp().format(fmt), "activity-time");
            row.getChildren().addAll(iconLabel, textBox, time);
            activityFeed.getChildren().add(row);
        }
    }

    // ── Export helpers ────────────────────────────────────────

    private void exportCompareHistory() {
        ComparisonSession cs;
        try { cs = service.requireComparisonSession(); } catch (Exception e) {
            setStatus("No active comparison to export.");
            return;
        }
        javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
        fc.setTitle("Export Comparison History");
        fc.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("Markdown", "*.md"),
                new javafx.stage.FileChooser.ExtensionFilter("JSON", "*.json"),
                new javafx.stage.FileChooser.ExtensionFilter("Text", "*.txt"));
        fc.setInitialFileName("compare-history");
        File file = fc.showSaveDialog(pageHost.getScene().getWindow());
        if (file == null) return;
        try {
            String name = file.getName().toLowerCase(Locale.ROOT);
            String content = name.endsWith(".json")
                    ? ExportHelper.compareHistoryToJson(cs.getStructureName(), cs.getHistory())
                    : ExportHelper.compareHistoryToText(cs.getStructureName(), cs.getHistory());
            Files.writeString(file.toPath(), content);
            setStatus("Exported comparison history to " + file.getName());
            activityLog.log("Export", "Comparison history → " + file.getName(), "system");
        } catch (IOException e) {
            showError("Export failed", e.getMessage());
        }
    }

    private void exportActivityLog() {
        javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
        fc.setTitle("Export Activity Log");
        fc.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter("Markdown", "*.md"),
                new javafx.stage.FileChooser.ExtensionFilter("JSON", "*.json"),
                new javafx.stage.FileChooser.ExtensionFilter("Text", "*.txt"));
        fc.setInitialFileName("activity-log");
        File file = fc.showSaveDialog(pageHost.getScene().getWindow());
        if (file == null) return;
        try {
            String filter = activityCategoryFilter != null ? activityCategoryFilter.getValue() : "All";
            List<ActivityLog.Entry> entries = "All".equals(filter)
                    ? activityLog.getAll() : activityLog.getByCategory(filter);
            String name = file.getName().toLowerCase(Locale.ROOT);
            String content = name.endsWith(".json")
                    ? ExportHelper.activityToJson(entries)
                    : ExportHelper.activityToText(entries);
            Files.writeString(file.toPath(), content);
            setStatus("Exported activity log to " + file.getName());
            activityLog.log("Export", "Activity log → " + file.getName(), "system");
        } catch (IOException e) {
            showError("Export failed", e.getMessage());
        }
    }

    private void wireSettingsEffects() {
        // Apply compact/high-density style classes to the root
        applyRootStyleClass("compact-mode", settings.isCompactMode());
        applyRootStyleClass("high-density", settings.isHighDensity());
        settings.compactModeProperty().addListener((obs, o, n) -> applyRootStyleClass("compact-mode", n));
        settings.highDensityProperty().addListener((obs, o, n) -> applyRootStyleClass("high-density", n));
    }

    private void applyRootStyleClass(String styleClass, boolean add) {
        if (pageHost.getScene() != null && pageHost.getScene().getRoot() != null) {
            if (add) {
                pageHost.getScene().getRoot().getStyleClass().add(styleClass);
            } else {
                pageHost.getScene().getRoot().getStyleClass().remove(styleClass);
            }
        } else {
            // Scene may not be set yet; defer
            pageHost.sceneProperty().addListener((obs, o, n) -> {
                if (n != null && n.getRoot() != null) {
                    if (add) {
                        n.getRoot().getStyleClass().add(styleClass);
                    } else {
                        n.getRoot().getStyleClass().remove(styleClass);
                    }
                }
            });
        }
    }

    // ─────────────────────────────────────────────────────────
    //  SETTINGS PAGE
    // ─────────────────────────────────────────────────────────

    private Node buildSettingsPage() {
        VBox content = new VBox(24);
        content.getStyleClass().add("page-content");
        content.setPadding(new Insets(36, 48, 36, 48));
        content.setMaxWidth(680);

        Label heroTitle = styledLabel("Preferences", "hero-title");
        Label heroSub = styledLabel("Customize your StructLab experience.", "hero-subtitle");
        VBox hero = new VBox(8, heroTitle, heroSub);
        hero.getStyleClass().add("hero-section");

        // Motion & Layout
        VBox motionCard = settingsCard("MOTION & LAYOUT",
                "Control page transitions and layout density.");
        CheckBox motionCb = styledCheck("Enable page transitions", settings.isMotionEnabled());
        motionCb.selectedProperty().bindBidirectional(settings.motionEnabledProperty());
        CheckBox compactCb = styledCheck("Compact mode", settings.isCompactMode());
        compactCb.selectedProperty().bindBidirectional(settings.compactModeProperty());
        CheckBox densityCb = styledCheck("High density layout", settings.isHighDensity());
        densityCb.selectedProperty().bindBidirectional(settings.highDensityProperty());
        settingsCardBody(motionCard).getChildren().addAll(motionCb, compactCb, densityCb);

        // Trace & Learning
        VBox traceCard = settingsCard("TRACE & LEARNING",
                "Control the level of detail in execution traces.");
        CheckBox traceCb = styledCheck("Show raw trace output", settings.isShowRawTraces());
        traceCb.selectedProperty().bindBidirectional(settings.showRawTracesProperty());
        settingsCardBody(traceCard).getChildren().add(traceCb);

        // Algorithm Lab
        VBox algoCard = settingsCard("ALGORITHM LAB",
                "Default settings for the graph algorithm workspace.");
        CheckBox autoFitCb = styledCheck("Auto-fit graph after preset change",
                settings.isAutoFitGraph());
        autoFitCb.selectedProperty().bindBidirectional(settings.autoFitGraphProperty());
        CheckBox trackerCb = styledCheck("Show algorithm tracker panel",
                settings.isShowAlgorithmTracker());
        trackerCb.selectedProperty().bindBidirectional(settings.showAlgorithmTrackerProperty());
        CheckBox expandedCb = styledCheck("Tracker expanded by default",
                settings.isTrackerExpanded());
        expandedCb.selectedProperty().bindBidirectional(settings.trackerExpandedProperty());

        HBox speedRow = new HBox(12);
        speedRow.setAlignment(Pos.CENTER_LEFT);
        Label speedLbl = styledLabel("Default playback speed:", "info-label");
        Slider speedSlider = new Slider(0.25, 3.0, settings.getDefaultPlaybackSpeed());
        speedSlider.setPrefWidth(140);
        speedSlider.setMajorTickUnit(0.5);
        Label speedVal = new Label(String.format("%.1fx", settings.getDefaultPlaybackSpeed()));
        speedVal.getStyleClass().add("info-label");
        speedSlider.valueProperty().addListener((obs, o, n) -> {
            settings.setDefaultPlaybackSpeed(n.doubleValue());
            speedVal.setText(String.format("%.1fx", n.doubleValue()));
        });
        settings.defaultPlaybackSpeedProperty().addListener((obs, o, n) ->
                speedSlider.setValue(n.doubleValue()));
        speedRow.getChildren().addAll(speedLbl, speedSlider, speedVal);

        settingsCardBody(algoCard).getChildren().addAll(
                autoFitCb, trackerCb, expandedCb, speedRow);

        // Reset + About
        VBox aboutCard = settingsCard("RESET & ABOUT",
                "StructLab \u2014 Data Structure Simulator");
        int totalImpls = service.getAllStructures().stream()
                .mapToInt(s -> service.getImplementations(s.id()).size()).sum();
        Label version = styledLabel("Version 1.0  \u00b7  Java 17  \u00b7  JavaFX 21",
                "info-label");
        Label structures = styledLabel(
                service.getAllStructures().size() + " structure families  \u00b7  "
                + totalImpls + " implementations  \u00b7  "
                + GraphAlgorithmCatalog.all().size() + " graph algorithms", "info-label");

        Button restoreBtn = new Button("Restore Defaults");
        restoreBtn.getStyleClass().add("action-btn");
        restoreBtn.setOnAction(e -> {
            settings.restoreDefaults();
            setStatus("Settings restored to defaults.");
        });

        Button reopenOnboarding = new Button("Reopen Getting Started");
        reopenOnboarding.getStyleClass().add("action-btn");
        reopenOnboarding.setOnAction(e -> {
            settings.setOnboardingDismissed(false);
            showOnboardingOverlay();
        });

        HBox aboutBtns = new HBox(8, restoreBtn, reopenOnboarding);
        aboutBtns.setAlignment(Pos.CENTER_LEFT);

        settingsCardBody(aboutCard).getChildren().addAll(version, structures, aboutBtns);

        content.getChildren().addAll(hero, motionCard, traceCard, algoCard, aboutCard);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("page-scroll");
        return scroll;
    }

    // ══════════════════════════════════════════════════════════
    //  Cell Factories
    // ══════════════════════════════════════════════════════════

    private void setupStructureCellFactory(ListView<StructureSummary> list) {
        list.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(StructureSummary item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null
                        : item.name() + "  [" + item.category() + "]");
            }
        });
    }

    private void setupImplCellFactory(ListView<ImplementationSummary> list) {
        list.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ImplementationSummary item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.name());
            }
        });
    }

    private void setupOperationCellFactory(ListView<OperationInfo> list) {
        list.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(OperationInfo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    String mark = item.mutates() ? " [mut]" : "";
                    setText(item.name() + "  " + item.complexityNote() + mark);
                    setTooltip(new Tooltip(
                            item.description()
                            + (item.aliases().isEmpty() ? ""
                                    : "\nAliases: " + String.join(", ", item.aliases()))
                            + "\nUsage: " + item.usage()
                            + "\nMutates: " + (item.mutates() ? "Yes" : "No")
                            + "\nComplexity: " + item.complexityNote()));
                }
            }
        });
    }

    // ══════════════════════════════════════════════════════════
    //  Status + Errors
    // ══════════════════════════════════════════════════════════

    private void setStatus(String msg) {
        statusLabel.setText(msg);
    }

    private void showError(String title, String message) {
        setStatus("Error: " + message);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
