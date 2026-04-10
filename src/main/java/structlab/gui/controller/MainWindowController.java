package structlab.gui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import structlab.app.comparison.ComparisonOperationResult;
import structlab.app.comparison.ComparisonSession;
import structlab.app.service.*;
import structlab.gui.GuiComparisonRenderer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainWindowController {

    // ── Discovery panel ─────────────────────────────────────────
    @FXML private ListView<StructureSummary> structureListView;
    @FXML private ListView<ImplementationSummary> implementationListView;
    @FXML private Button openSessionButton;
    @FXML private Button compareAllButton;

    // ── Center panel: structure details ──────────────────────────
    @FXML private Label detailNameLabel;
    @FXML private Label detailCategoryLabel;
    @FXML private Label detailDescriptionLabel;
    @FXML private Label detailKeywordsLabel;

    // ── Center panel: state and trace ───────────────────────────
    @FXML private TextArea stateArea;
    @FXML private TextArea traceArea;

    // ── Right panel: session info ───────────────────────────────
    @FXML private Label sessionStructureLabel;
    @FXML private Label sessionImplLabel;
    @FXML private Label sessionOpsCountLabel;
    @FXML private Button resetButton;
    @FXML private Button closeSessionButton;

    // ── Right panel: operations ─────────────────────────────────
    @FXML private ListView<OperationInfo> operationListView;
    @FXML private TextField argField;
    @FXML private Button executeButton;

    // ── Right panel: history ────────────────────────────────────
    @FXML private ListView<String> historyListView;

    // ── Status ──────────────────────────────────────────────────
    @FXML private Label statusLabel;
    @FXML private Label bottomStatusLabel;

    // ── Navigation rail ─────────────────────────────────────────
    @FXML private Button navExploreBtn;
    @FXML private Button navCompareBtn;

    // ── Workspace header ────────────────────────────────────────
    @FXML private Label workspaceTitleLabel;

    private StructLabService service;
    private boolean comparisonMode = false;

    // ── Lifecycle ───────────────────────────────────────────────

    public void initService(StructLabService service) {
        this.service = service;
        refreshDiscovery();
        initEmptyStates();
    }

    @FXML
    public void initialize() {
        setupCellFactories();
        setupSelectionListeners();
        if (navExploreBtn != null) {
            navExploreBtn.getStyleClass().add("nav-btn-active");
        }
    }

    // ── Cell factories ──────────────────────────────────────────

    private void setupCellFactories() {
        structureListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(StructureSummary item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.name() + "  [" + item.category() + "]");
            }
        });

        implementationListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ImplementationSummary item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.name());
            }
        });

        operationListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(OperationInfo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    String mutatesMark = item.mutates() ? " [mut]" : "";
                    setText(item.name() + "  " + item.complexityNote() + mutatesMark);
                    setTooltip(new Tooltip(
                            item.description()
                            + (item.aliases().isEmpty() ? "" : "\nAliases: " + String.join(", ", item.aliases()))
                            + "\nUsage: " + item.usage()
                            + "\nMutates: " + (item.mutates() ? "Yes" : "No")
                            + "\nComplexity: " + item.complexityNote()
                    ));
                }
            }
        });
    }

    // ── Selection listeners ─────────────────────────────────────

    private void setupSelectionListeners() {
        structureListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> onStructureSelected(selected));
        implementationListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> updateOpenButtonState());
        operationListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> onOperationSelected(selected));
    }

    private void onOperationSelected(OperationInfo selected) {
        if (selected == null) {
            argField.setPromptText("args (e.g. 42)");
            return;
        }
        // Strip the operation name prefix from the usage string to get just the arg hint
        String usage = selected.usage();
        String argsHint = usage.startsWith(selected.name() + " ")
                ? usage.substring(selected.name().length() + 1)
                : usage;
        argField.setPromptText(selected.argCount() == 0 ? "no args needed" : argsHint);
    }

    private void onStructureSelected(StructureSummary selected) {
        implementationListView.getItems().clear();
        if (selected == null) {
            clearStructureDetail();
            compareAllButton.setDisable(true);
            return;
        }
        refreshStructureDetail(selected);
        List<ImplementationSummary> impls = service.getImplementations(selected.id());
        implementationListView.setItems(FXCollections.observableArrayList(impls));
        compareAllButton.setDisable(impls.size() < 2 || service.hasActiveSession() || comparisonMode);
        updateOpenButtonState();
    }

    // ── Session actions ─────────────────────────────────────────

    @FXML
    private void onOpenSession() {
        ImplementationSummary impl = implementationListView.getSelectionModel().getSelectedItem();
        if (impl == null) {
            setStatus("Select an implementation first.");
            return;
        }

        try {
            SessionSnapshot snapshot = service.openSession(impl.parentStructureId(), impl.id());
            comparisonMode = false;
            refreshSessionInfo(snapshot);
            refreshState();
            refreshOperations();
            refreshHistory();
            refreshTrace();
            setSessionButtonStates(true);
            statusLabel.setText("Session");
            workspaceTitleLabel.setText(snapshot.structureName() + " \u2014 " + snapshot.implementationName());
            updateNavState();
            setStatus("Session opened for " + snapshot.implementationName() + ".");
        } catch (Exception e) {
            showError("Failed to open session", e.getMessage());
        }
    }

    @FXML
    private void onCompareAll() {
        StructureSummary selected = structureListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus("Select a structure first.");
            return;
        }

        try {
            ComparisonSession cs = service.openComparisonSession(selected.id(), List.of());
            comparisonMode = true;

            sessionStructureLabel.setText("Compare: " + cs.getStructureName());
            sessionImplLabel.setText(cs.entryCount() + " implementations");
            sessionOpsCountLabel.setText("Operations: 0");

            stateArea.setText(GuiComparisonRenderer.renderStates(cs));
            traceArea.clear();

            List<OperationInfo> ops = cs.getCommonOperations().stream()
                    .map(o -> new OperationInfo(
                            o.name(), o.aliases(), o.description(),
                            o.argCount(), o.usage(), o.mutates(), o.complexityNote()))
                    .toList();
            operationListView.setItems(FXCollections.observableArrayList(ops));
            historyListView.setItems(FXCollections.observableArrayList());

            setSessionButtonStates(true);
            statusLabel.setText("Compare");
            workspaceTitleLabel.setText("Compare \u2014 " + cs.getStructureName());
            updateNavState();
            setStatus("Comparison session opened for " + cs.getStructureName() + " with " + cs.entryCount() + " implementations.");
        } catch (Exception e) {
            showError("Failed to open comparison session", e.getMessage());
        }
    }

    @FXML
    private void onCloseSession() {
        comparisonMode = false;
        service.closeSession();
        clearSessionUI();
        setStatus("Session closed.");
    }

    @FXML
    private void onReset() {
        try {
            if (comparisonMode) {
                service.resetComparisonSession();
                ComparisonSession cs = service.requireComparisonSession();
                stateArea.setText(GuiComparisonRenderer.renderStates(cs));
                traceArea.clear();
                historyListView.setItems(FXCollections.observableArrayList());
                sessionOpsCountLabel.setText("Operations: 0");
                setStatus("All comparison implementations reset.");
            } else {
                service.resetSession();
                refreshState();
                refreshHistory();
                refreshTrace();
                refreshSessionOpsCount();
                setStatus("Session reset to empty state.");
            }
        } catch (Exception e) {
            showError("Reset failed", e.getMessage());
        }
    }

    // ── Operation execution ─────────────────────────────────────

    @FXML
    private void onExecuteOperation() {
        OperationInfo selectedOp = operationListView.getSelectionModel().getSelectedItem();
        if (selectedOp == null) {
            setStatus("Select an operation first.");
            return;
        }

        String rawArgs = argField.getText().trim();
        List<String> args = rawArgs.isEmpty()
                ? List.of()
                : Arrays.stream(rawArgs.split("\\s+")).collect(Collectors.toList());

        if (args.size() < selectedOp.argCount()) {
            setStatus("This operation requires " + selectedOp.argCount() + " argument(s). Usage: " + selectedOp.usage());
            return;
        }

        if (args.size() > selectedOp.argCount()) {
            setStatus("Too many arguments. Expected " + selectedOp.argCount() + ", got " + args.size() + ". Usage: " + selectedOp.usage());
            return;
        }

        if (comparisonMode) {
            executeComparisonOperation(selectedOp.name(), args);
        } else {
            executeSingleOperation(selectedOp, args);
        }
    }

    private void executeSingleOperation(OperationInfo selectedOp, List<String> args) {
        try {
            ExecutionResult result = service.executeOperation(selectedOp.name(), args);
            refreshState();
            refreshHistory();
            refreshTrace();
            refreshSessionOpsCount();
            argField.clear();

            if (result.success()) {
                String msg = "Executed " + result.operationName();
                if (result.returnedValue() != null && !"null".equals(result.returnedValue())) {
                    msg += " -> " + result.returnedValue();
                }
                setStatus(msg);
            } else {
                setStatus("Failed: " + result.message());
            }
        } catch (Exception e) {
            showError("Execution error", e.getMessage());
        }
    }

    private void executeComparisonOperation(String opName, List<String> args) {
        try {
            ComparisonOperationResult result = service.executeComparisonOperation(opName, args);
            ComparisonSession cs = service.requireComparisonSession();

            stateArea.setText(GuiComparisonRenderer.renderStates(cs));
            traceArea.setText(GuiComparisonRenderer.renderCompactTraces(cs));
            sessionOpsCountLabel.setText("Operations: " + cs.historySize());
            argField.clear();

            // Refresh history list
            List<ComparisonOperationResult> history = cs.getHistory();
            List<String> items = new java.util.ArrayList<>();
            for (int i = 0; i < history.size(); i++) {
                ComparisonOperationResult op = history.get(i);
                String status = op.allSucceeded() ? "[OK]" : "[PARTIAL]";
                String argsStr = op.args().isEmpty() ? "" : " " + String.join(" ", op.args());
                items.add(status + " " + (i + 1) + ". " + op.operationName() + argsStr);
            }
            historyListView.setItems(FXCollections.observableArrayList(items));

            if (result.allSucceeded()) {
                setStatus("Compared: " + opName + " — all succeeded");
            } else {
                long failCount = result.entryResults().stream().filter(e -> !e.success()).count();
                setStatus("Compared: " + opName + " — " + failCount + " failed");
            }
        } catch (Exception e) {
            showError("Comparison error", e.getMessage());
        }
    }

    // ── Refresh helpers ─────────────────────────────────────────

    private void refreshDiscovery() {
        if (service == null) return;
        List<StructureSummary> structures = service.getAllStructures();
        structureListView.setItems(FXCollections.observableArrayList(structures));
    }

    private void refreshStructureDetail(StructureSummary s) {
        detailNameLabel.setText(s.name());
        detailCategoryLabel.setText("Category: " + s.category());
        detailDescriptionLabel.setText(s.description() != null ? s.description() : "");
        String kw = s.keywords() != null && !s.keywords().isEmpty()
                ? "Keywords: " + String.join(", ", s.keywords()) : "";
        detailKeywordsLabel.setText(kw);
    }

    private void clearStructureDetail() {
        detailNameLabel.setText("Select a structure to begin exploring.");
        detailCategoryLabel.setText("");
        detailDescriptionLabel.setText("");
        detailKeywordsLabel.setText("");
    }

    private void refreshSessionInfo(SessionSnapshot snapshot) {
        sessionStructureLabel.setText("Structure: " + snapshot.structureName());
        sessionImplLabel.setText("Implementation: " + snapshot.implementationName());
        sessionOpsCountLabel.setText("Operations: " + snapshot.operationCount());
    }

    private void refreshSessionOpsCount() {
        service.getSessionSnapshot().ifPresent(s ->
                sessionOpsCountLabel.setText("Operations: " + s.operationCount()));
    }

    private void refreshState() {
        if (!service.hasActiveSession()) {
            stateArea.clear();
            return;
        }
        stateArea.setText(service.getRenderedState());
    }

    private void refreshTrace() {
        if (!service.hasActiveSession()) {
            traceArea.clear();
            return;
        }
        String trace = service.getLastTraceRendered();
        traceArea.setText(trace);
    }

    private void refreshHistory() {
        if (!service.hasActiveSession()) {
            historyListView.getItems().clear();
            return;
        }
        List<ExecutionResult> history = service.getHistory();
        if (history.isEmpty()) {
            historyListView.setItems(FXCollections.observableArrayList());
            return;
        }
        List<String> items = new java.util.ArrayList<>();
        for (int i = 0; i < history.size(); i++) {
            ExecutionResult r = history.get(i);
            String status = r.success() ? "[OK]" : "[FAIL]";
            String entry = status + " " + (i + 1) + ". " + r.operationName();
            if (r.success() && r.returnedValue() != null && !"null".equals(r.returnedValue())) {
                entry += " -> " + r.returnedValue();
            } else if (!r.success()) {
                entry += " — " + r.message();
            }
            items.add(entry);
        }
        historyListView.setItems(FXCollections.observableArrayList(items));
    }

    private void refreshOperations() {
        List<OperationInfo> ops = service.getAvailableOperations();
        operationListView.setItems(FXCollections.observableArrayList(ops));
    }

    // ── UI state helpers ────────────────────────────────────────

    private void initEmptyStates() {
        clearStructureDetail();
        clearSessionUI();
    }

    private void setSessionButtonStates(boolean sessionActive) {
        openSessionButton.setDisable(sessionActive);
        resetButton.setDisable(!sessionActive);
        closeSessionButton.setDisable(!sessionActive);
        executeButton.setDisable(!sessionActive);
    }

    private void updateOpenButtonState() {
        ImplementationSummary sel = implementationListView.getSelectionModel().getSelectedItem();
        openSessionButton.setDisable(sel == null || service.hasActiveSession() || comparisonMode);
    }

    private void clearSessionUI() {
        comparisonMode = false;
        sessionStructureLabel.setText("No active session");
        sessionImplLabel.setText("");
        sessionOpsCountLabel.setText("");
        stateArea.clear();
        traceArea.clear();
        operationListView.getItems().clear();
        historyListView.getItems().clear();
        argField.clear();

        statusLabel.setText("Discovery");
        workspaceTitleLabel.setText("StructLab");
        updateNavState();
        setSessionButtonStates(false);
    }

    private void setStatus(String msg) {
        bottomStatusLabel.setText(msg);
    }

    private void showError(String title, String message) {
        setStatus("Error: " + message);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ── Navigation rail ─────────────────────────────────────────

    @FXML
    private void onNavExplore() {
        if (comparisonMode) {
            onCloseSession();
        }
        updateNavState();
    }

    @FXML
    private void onNavCompare() {
        if (!comparisonMode && !service.hasActiveSession()) {
            onCompareAll();
        } else if (service.hasActiveSession() && !comparisonMode) {
            setStatus("Close the current session before comparing.");
        }
    }

    private void updateNavState() {
        navExploreBtn.getStyleClass().removeAll("nav-btn-active");
        navCompareBtn.getStyleClass().removeAll("nav-btn-active");
        if (comparisonMode) {
            navCompareBtn.getStyleClass().add("nav-btn-active");
        } else {
            navExploreBtn.getStyleClass().add("nav-btn-active");
        }
    }
}
