package structlab.gui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import structlab.app.service.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainWindowController {

    // ── Discovery panel ─────────────────────────────────────────
    @FXML private ListView<StructureSummary> structureListView;
    @FXML private ListView<ImplementationSummary> implementationListView;
    @FXML private Button openSessionButton;

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

    private StructLabService service;

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
                    String aliases = item.aliases().isEmpty() ? "" : " (" + String.join(", ", item.aliases()) + ")";
                    String mutatesMark = item.mutates() ? " [mut]" : "";
                    setText(item.name() + aliases + "  " + item.complexityNote() + mutatesMark);
                    setTooltip(new Tooltip(
                            item.description() + "\nUsage: " + item.usage() +
                            "\nMutates: " + (item.mutates() ? "Yes" : "No") +
                            "\nComplexity: " + item.complexityNote()
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
    }

    private void onStructureSelected(StructureSummary selected) {
        implementationListView.getItems().clear();
        if (selected == null) {
            clearStructureDetail();
            return;
        }
        refreshStructureDetail(selected);
        List<ImplementationSummary> impls = service.getImplementations(selected.id());
        implementationListView.setItems(FXCollections.observableArrayList(impls));
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
            refreshSessionInfo(snapshot);
            refreshState();
            refreshOperations();
            refreshHistory();
            refreshTrace();
            setSessionButtonStates(true);
            statusLabel.setText("Session: " + snapshot.structureName() + " / " + snapshot.implementationName());
            setStatus("Session opened for " + snapshot.implementationName() + ".");
        } catch (Exception e) {
            showError("Failed to open session", e.getMessage());
        }
    }

    @FXML
    private void onCloseSession() {
        service.closeSession();
        clearSessionUI();
        setStatus("Session closed.");
    }

    @FXML
    private void onReset() {
        try {
            service.resetSession();
            refreshState();
            refreshHistory();
            refreshTrace();
            refreshSessionOpsCount();
            setStatus("Session reset to empty state.");
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

        if (selectedOp.argCount() > 0 && args.isEmpty()) {
            setStatus("This operation requires " + selectedOp.argCount() + " argument(s). Usage: " + selectedOp.usage());
            return;
        }

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
        detailNameLabel.setText("Select a structure to view details.");
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
        openSessionButton.setDisable(sel == null || service.hasActiveSession());
    }

    private void clearSessionUI() {
        sessionStructureLabel.setText("No active session");
        sessionImplLabel.setText("");
        sessionOpsCountLabel.setText("");
        stateArea.clear();
        traceArea.clear();
        operationListView.getItems().clear();
        historyListView.getItems().clear();
        argField.clear();

        statusLabel.setText("Discovery Mode");
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
}
