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

import structlab.app.comparison.ComparisonOperationResult;
import structlab.app.comparison.ComparisonSession;
import structlab.app.service.*;
import structlab.gui.*;

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
    private TextArea explState, explTrace;
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
    private Label cmpOverviewLabel;
    private TextArea cmpState, cmpTrace;
    private Label cmpSessLabel, cmpImplCount, cmpOpsCount;
    private Button cmpResetBtn, cmpCloseBtn;
    private ListView<OperationInfo> cmpOpList;
    private TextField cmpArgField;
    private Button cmpRunBtn;
    private ListView<String> cmpHistory;
    private boolean comparisonActive = false;

    // ══ Activity page elements ═══════════════════════════════
    private VBox activityFeed;

    // ══════════════════════════════════════════════════════════
    //  Lifecycle
    // ══════════════════════════════════════════════════════════

    public void initService(StructLabService service) {
        this.service = service;
        buildAllPages();
        navigateTo(NavigationPage.EXPLORE);
        activityLog.log("Application started", "StructLab initialized", "system");
    }

    @FXML
    public void initialize() {
        navButtons = new Button[]{
            navExploreBtn, navCompareBtn, navLearnBtn, navActivityBtn, navSettingsBtn
        };
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

        VBox detailCard = buildCard("STRUCTURE DETAILS");
        VBox detailBody = cardBody(detailCard);
        explDetailName = styledLabel("Select a structure to begin exploring.", "detail-name");
        explDetailCat = styledLabel("", "info-label");
        explDetailDesc = styledLabel("", "detail-description");
        explDetailDesc.setWrapText(true);
        explDetailKw = styledLabel("", "detail-keywords");
        detailBody.getChildren().addAll(explDetailName, explDetailCat, explDetailDesc, explDetailKw);

        VBox stateCard = buildCard("STRUCTURE STATE");
        VBox.setVgrow(stateCard, Priority.ALWAYS);
        VBox stateBody = cardBody(stateCard);
        VBox.setVgrow(stateBody, Priority.ALWAYS);
        explState = monoArea("Open a session to see the live structure state.");
        VBox.setVgrow(explState, Priority.ALWAYS);
        stateBody.getChildren().add(explState);

        VBox traceCard = buildCard("TRACE LOG");
        VBox traceBody = cardBody(traceCard);
        explTrace = monoArea("Execution traces appear here after operations.");
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
        explSessStruct = styledLabel("No active session", "info-label");
        explSessImpl = styledLabel("", "info-label");
        explSessOps = styledLabel("", "info-label");

        explResetBtn = secondaryBtn("Reset");
        explResetBtn.setOnAction(e -> handleExploreReset());
        explCloseBtn = secondaryBtn("Close");
        explCloseBtn.setOnAction(e -> handleExploreCloseSession());
        HBox sessBtns = growBtns(explResetBtn, explCloseBtn);
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
            explDetailName.setText("Select a structure to begin exploring.");
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
        if (!service.hasActiveSession()) { explState.clear(); return; }
        explState.setText(service.getRenderedState());
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
        explSessStruct.setText("No active session");
        explSessImpl.setText("");
        explSessOps.setText("");
        explState.clear();
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

        // Workspace
        VBox ws = new VBox();
        ws.getStyleClass().add("workspace");
        HBox.setHgrow(ws, Priority.ALWAYS);

        VBox overviewCard = buildCard("COMPARISON OVERVIEW");
        VBox overviewBody = cardBody(overviewCard);
        cmpOverviewLabel = styledLabel(
                "Select a structure and start a comparison to analyze implementations side by side.",
                "detail-description");
        cmpOverviewLabel.setWrapText(true);
        overviewBody.getChildren().add(cmpOverviewLabel);

        VBox stateCard = buildCard("COMPARISON STATE");
        VBox.setVgrow(stateCard, Priority.ALWAYS);
        VBox stateBody = cardBody(stateCard);
        VBox.setVgrow(stateBody, Priority.ALWAYS);
        cmpState = monoArea("Start a comparison to see implementation states side by side.");
        VBox.setVgrow(cmpState, Priority.ALWAYS);
        stateBody.getChildren().add(cmpState);

        VBox traceCard = buildCard("COMPARISON TRACES");
        VBox traceBody = cardBody(traceCard);
        cmpTrace = monoArea("Execution traces for all implementations appear here.");
        cmpTrace.setPrefHeight(150);
        traceBody.getChildren().add(cmpTrace);

        VBox wsContent = new VBox(12, overviewCard, stateCard, traceCard);
        wsContent.getStyleClass().add("workspace-content");
        wsContent.setPadding(new Insets(16, 20, 16, 20));
        VBox.setVgrow(wsContent, Priority.ALWAYS);
        ws.getChildren().add(wsContent);

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
        cmpSessLabel = styledLabel("No active comparison", "info-label");
        cmpImplCount = styledLabel("", "info-label");
        cmpOpsCount = styledLabel("", "info-label");

        cmpResetBtn = secondaryBtn("Reset All");
        cmpResetBtn.setOnAction(e -> handleCompareReset());
        cmpCloseBtn = secondaryBtn("Close");
        cmpCloseBtn.setOnAction(e -> handleCompareClose());
        HBox sessBtns = growBtns(cmpResetBtn, cmpCloseBtn);
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
            cmpOverviewLabel.setText("Comparing " + cs.entryCount()
                    + " implementations of " + cs.getStructureName()
                    + ". Execute operations to observe differences.");

            cmpState.setText(GuiComparisonRenderer.renderStates(cs));
            cmpTrace.clear();

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
            cmpState.setText(GuiComparisonRenderer.renderStates(cs));
            cmpTrace.clear();
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

            cmpState.setText(GuiComparisonRenderer.renderStates(cs));
            cmpTrace.setText(GuiComparisonRenderer.renderCompactTraces(cs));
            cmpOpsCount.setText("Operations: " + cs.historySize());
            cmpArgField.clear();

            List<ComparisonOperationResult> history = cs.getHistory();
            List<String> items = new ArrayList<>();
            for (int i = 0; i < history.size(); i++) {
                ComparisonOperationResult h = history.get(i);
                String status = h.allSucceeded() ? "\u2714" : "\u26a0";
                String argsStr = h.args().isEmpty() ? "" : " " + String.join(" ", h.args());
                items.add(status + "  " + (i + 1) + ". " + h.operationName() + argsStr);
            }
            cmpHistory.setItems(FXCollections.observableArrayList(items));

            String msg;
            if (result.allSucceeded()) {
                msg = "Compared: " + op.name() + " — all succeeded";
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

    private void setCompareSessionButtons(boolean active) {
        cmpStartBtn.setDisable(active);
        cmpResetBtn.setDisable(!active);
        cmpCloseBtn.setDisable(!active);
        cmpRunBtn.setDisable(!active);
    }

    private void clearCompareSession() {
        comparisonActive = false;
        cmpSessLabel.setText("No active comparison");
        cmpImplCount.setText("");
        cmpOpsCount.setText("");
        cmpOverviewLabel.setText(
                "Select a structure and start a comparison to analyze implementations side by side.");
        cmpState.clear();
        cmpTrace.clear();
        cmpOpList.getItems().clear();
        cmpHistory.getItems().clear();
        cmpArgField.clear();
        setCompareSessionButtons(false);
    }

    // ─────────────────────────────────────────────────────────
    //  LEARN PAGE
    // ─────────────────────────────────────────────────────────

    private Node buildLearnPage() {
        VBox content = new VBox(28);
        content.getStyleClass().add("page-content");
        content.setPadding(new Insets(36, 48, 36, 48));

        // Hero
        Label heroTitle = styledLabel("Data Structure Library", "hero-title");
        int totalImpls = service.getAllStructures().stream()
                .mapToInt(s -> service.getImplementations(s.id()).size()).sum();
        Label heroSub = styledLabel(
                service.getAllStructures().size() + " structure families, "
                + totalImpls + " implementations — explore the foundations of computer science.",
                "hero-subtitle");
        heroSub.setWrapText(true);
        VBox hero = new VBox(8, heroTitle, heroSub);
        hero.getStyleClass().add("hero-section");
        content.getChildren().add(hero);

        // Group by category
        List<StructureSummary> all = service.getAllStructures();
        Map<String, List<StructureSummary>> byCategory = all.stream()
                .collect(Collectors.groupingBy(
                        StructureSummary::category,
                        LinkedHashMap::new,
                        Collectors.toList()));

        for (Map.Entry<String, List<StructureSummary>> cat : byCategory.entrySet()) {
            content.getChildren().add(
                    styledLabel(cat.getKey().toUpperCase() + " STRUCTURES", "category-header"));

            FlowPane grid = new FlowPane(16, 16);
            grid.getStyleClass().add("card-grid");
            for (StructureSummary s : cat.getValue()) {
                grid.getChildren().add(buildLearnCard(s));
            }
            content.getChildren().add(grid);
        }

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("page-scroll");
        return scroll;
    }

    private VBox buildLearnCard(StructureSummary s) {
        VBox card = new VBox(8);
        card.getStyleClass().add("learn-card");
        card.setPrefWidth(360);
        card.setMinWidth(300);
        card.setPadding(new Insets(18));

        Label name = styledLabel(s.name(), "learn-card-title");
        Label desc = styledLabel(s.description() != null ? s.description() : "", "learn-card-desc");
        desc.setWrapText(true);

        VBox implBox = new VBox(6);
        List<ImplementationSummary> impls = service.getImplementations(s.id());
        for (ImplementationSummary impl : impls) {
            VBox implEntry = new VBox(2);
            Label implName = styledLabel(impl.name(), "learn-impl-name");
            implEntry.getChildren().add(implName);

            if (impl.timeComplexity() != null && !impl.timeComplexity().isEmpty()) {
                String tc = impl.timeComplexity().entrySet().stream()
                        .map(e -> e.getKey() + ": " + e.getValue())
                        .collect(Collectors.joining("  \u00b7  "));
                Label tcLabel = styledLabel(tc, "learn-complexity-detail");
                implEntry.getChildren().add(tcLabel);
            }
            if (impl.spaceComplexity() != null && !impl.spaceComplexity().isEmpty()) {
                Label space = styledLabel("Space: " + impl.spaceComplexity(), "learn-complexity-detail");
                implEntry.getChildren().add(space);
            }
            implBox.getChildren().add(implEntry);
        }

        String kwText = s.keywords() != null && !s.keywords().isEmpty()
                ? String.join("  \u00b7  ", s.keywords()) : "";

        card.getChildren().addAll(
                name, desc, new Separator(),
                styledLabel("IMPLEMENTATIONS", "learn-card-section"), implBox);
        if (!kwText.isEmpty()) {
            card.getChildren().addAll(new Separator(), styledLabel(kwText, "learn-card-keywords"));
        }
        return card;
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

        activityFeed = new VBox(0);
        activityFeed.getStyleClass().add("activity-feed");

        content.getChildren().addAll(hero, activityFeed);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("page-scroll");
        return scroll;
    }

    private void refreshActivityPage() {
        if (activityFeed == null) return;
        activityFeed.getChildren().clear();

        List<ActivityLog.Entry> recent = activityLog.getRecent(50);
        if (recent.isEmpty()) {
            Label empty = styledLabel(
                    "No activity yet. Open a session or start a comparison to see your history here.",
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
            textBox.getChildren().add(action);
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

        // Motion
        VBox motionCard = buildSettingsCard("MOTION & ANIMATION",
                "Control page transitions and UI animations.");
        CheckBox motionCb = styledCheck("Enable page transitions", settings.isMotionEnabled());
        motionCb.selectedProperty().bindBidirectional(settings.motionEnabledProperty());
        settingsBody(motionCard).getChildren().add(motionCb);

        // Display
        VBox displayCard = buildSettingsCard("DISPLAY",
                "Adjust layout density and information presentation.");
        CheckBox compactCb = styledCheck("Compact mode", settings.isCompactMode());
        compactCb.selectedProperty().bindBidirectional(settings.compactModeProperty());
        CheckBox densityCb = styledCheck("High density layout", settings.isHighDensity());
        densityCb.selectedProperty().bindBidirectional(settings.highDensityProperty());
        settingsBody(displayCard).getChildren().addAll(compactCb, densityCb);

        // Trace
        VBox traceCard = buildSettingsCard("TRACE OUTPUT",
                "Control the level of detail in execution traces.");
        CheckBox traceCb = styledCheck("Show raw trace output", settings.isShowRawTraces());
        traceCb.selectedProperty().bindBidirectional(settings.showRawTracesProperty());
        settingsBody(traceCard).getChildren().add(traceCb);

        // About
        VBox aboutCard = buildSettingsCard("ABOUT",
                "StructLab — Data Structure Simulator");
        int totalImpls = service.getAllStructures().stream()
                .mapToInt(s -> service.getImplementations(s.id()).size()).sum();
        Label version = styledLabel("Version 1.0  \u00b7  Java 17  \u00b7  JavaFX 21",
                "info-label");
        Label structures = styledLabel(
                service.getAllStructures().size() + " structure families  \u00b7  "
                + totalImpls + " implementations", "info-label");
        settingsBody(aboutCard).getChildren().addAll(version, structures);

        content.getChildren().addAll(hero, motionCard, displayCard, traceCard, aboutCard);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("page-scroll");
        return scroll;
    }

    // ══════════════════════════════════════════════════════════
    //  UI Builder Helpers
    // ══════════════════════════════════════════════════════════

    private Label styledLabel(String text, String... styleClasses) {
        Label l = new Label(text);
        l.getStyleClass().addAll(styleClasses);
        return l;
    }

    private TextArea monoArea(String prompt) {
        TextArea ta = new TextArea();
        ta.setEditable(false);
        ta.setWrapText(true);
        ta.getStyleClass().add("mono-area");
        ta.setPromptText(prompt);
        return ta;
    }

    private VBox buildCard(String title) {
        VBox card = new VBox();
        card.getStyleClass().add("card");

        HBox header = new HBox();
        header.getStyleClass().add("card-header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().add(styledLabel(title, "card-title"));

        VBox body = new VBox(4);
        body.getStyleClass().add("card-body");

        card.getChildren().addAll(header, body);
        return card;
    }

    private VBox cardBody(VBox card) {
        return (VBox) card.getChildren().get(1);
    }

    private VBox buildSettingsCard(String title, String description) {
        VBox card = new VBox();
        card.getStyleClass().add("settings-card");

        VBox header = new VBox(4);
        header.getStyleClass().add("settings-card-header");
        header.getChildren().addAll(
                styledLabel(title, "settings-card-title"),
                styledLabel(description, "settings-card-desc"));

        VBox body = new VBox(12);
        body.getStyleClass().add("settings-card-body");

        card.getChildren().addAll(header, body);
        return card;
    }

    private VBox settingsBody(VBox card) {
        return (VBox) card.getChildren().get(1);
    }

    private CheckBox styledCheck(String text, boolean initial) {
        CheckBox cb = new CheckBox(text);
        cb.setSelected(initial);
        cb.getStyleClass().add("settings-check");
        return cb;
    }

    private Button secondaryBtn(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("secondary-button");
        btn.setDisable(true);
        btn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btn, Priority.ALWAYS);
        return btn;
    }

    private HBox growBtns(Button... btns) {
        HBox box = new HBox(6, btns);
        box.setPadding(new Insets(6, 0, 0, 0));
        return box;
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
