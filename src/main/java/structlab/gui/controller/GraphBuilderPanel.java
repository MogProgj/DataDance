package structlab.gui.controller;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import structlab.core.graph.Graph;

import java.util.Locale;
import java.util.function.Consumer;

/**
 * Form-based custom graph builder panel.
 * Allows users to create and edit graphs by adding/removing nodes and edges,
 * toggling directed/undirected, and setting edge weights.
 */
public class GraphBuilderPanel extends VBox {

    /** Typed edge item — eliminates regex parsing on remove. */
    public record EdgeItem(String from, String to, double weight, boolean directed) {
        @Override
        public String toString() {
            String sep = directed ? " → " : " — ";
            return from + sep + to + " (w=" + formatWeight(weight) + ")";
        }

        private static String formatWeight(double w) {
            if (w == Math.floor(w) && !Double.isInfinite(w)) {
                return String.valueOf((long) w);
            }
            return String.format(Locale.US, "%.2f", w);
        }
    }

    private final CheckBox directedCheck;
    private final TextField nodeField;
    private final ComboBox<String> fromCombo;
    private final ComboBox<String> toCombo;
    private final TextField weightField;
    private final ListView<String> nodeList;
    private final ListView<EdgeItem> edgeList;
    private final Label summaryLabel;

    private Graph graph;
    private Consumer<Graph> onGraphChanged;

    public GraphBuilderPanel() {
        getStyleClass().add("graph-builder-panel");
        setSpacing(6);
        setPadding(new Insets(10, 10, 10, 10));

        this.graph = new Graph(false);

        // ── Header ──────────────────────────────────────────
        Label header = new Label("CUSTOM GRAPH");
        header.getStyleClass().add("section-header");

        // ── Directed toggle ─────────────────────────────────
        directedCheck = new CheckBox("Directed");
        directedCheck.getStyleClass().add("graph-builder-check");
        directedCheck.setOnAction(e -> rebuildGraph());

        // ── Summary ─────────────────────────────────────────
        summaryLabel = new Label("0 nodes, 0 edges");
        summaryLabel.getStyleClass().add("algo-preset-desc");

        // ── Add Node ────────────────────────────────────────
        Label nodeHeader = new Label("ADD NODE");
        nodeHeader.getStyleClass().add("algo-info-label");

        HBox nodeRow = new HBox(4);
        nodeRow.setAlignment(Pos.CENTER_LEFT);
        nodeField = new TextField();
        nodeField.setPromptText("Label (e.g. A)");
        nodeField.getStyleClass().add("graph-builder-field");
        nodeField.setPrefWidth(100);
        HBox.setHgrow(nodeField, Priority.ALWAYS);
        Button addNodeBtn = new Button("+");
        addNodeBtn.getStyleClass().add("graph-builder-btn");
        addNodeBtn.setOnAction(e -> onAddNode());
        nodeField.setOnAction(e -> onAddNode());
        nodeRow.getChildren().addAll(nodeField, addNodeBtn);

        // ── Node list ───────────────────────────────────────
        nodeList = new ListView<>();
        nodeList.getStyleClass().add("graph-builder-list");
        nodeList.setCellFactory(lv -> new StyledListCell<>());
        nodeList.setPrefHeight(70);
        nodeList.setMaxHeight(90);

        Button removeNodeBtn = new Button("Remove Selected Node");
        removeNodeBtn.getStyleClass().add("secondary-button");
        removeNodeBtn.setMaxWidth(Double.MAX_VALUE);
        removeNodeBtn.setOnAction(e -> onRemoveNode());

        // ── Add Edge ────────────────────────────────────────
        Label edgeHeader = new Label("ADD EDGE");
        edgeHeader.getStyleClass().add("algo-info-label");

        HBox edgeRow1 = new HBox(4);
        edgeRow1.setAlignment(Pos.CENTER_LEFT);
        fromCombo = new ComboBox<>();
        fromCombo.setPromptText("From");
        fromCombo.getStyleClass().add("algo-combo");
        fromCombo.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(fromCombo, Priority.ALWAYS);
        Label arrow = new Label("→");
        arrow.getStyleClass().add("graph-builder-arrow");
        toCombo = new ComboBox<>();
        toCombo.setPromptText("To");
        toCombo.getStyleClass().add("algo-combo");
        toCombo.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(toCombo, Priority.ALWAYS);
        edgeRow1.getChildren().addAll(fromCombo, arrow, toCombo);

        HBox edgeRow2 = new HBox(4);
        edgeRow2.setAlignment(Pos.CENTER_LEFT);
        weightField = new TextField("1");
        weightField.setPromptText("Weight");
        weightField.getStyleClass().add("graph-builder-field");
        weightField.setPrefWidth(60);
        Label wLabel = new Label("Weight:");
        wLabel.getStyleClass().add("algo-info-label");
        Button addEdgeBtn = new Button("Add Edge");
        addEdgeBtn.getStyleClass().add("primary-button");
        addEdgeBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(addEdgeBtn, Priority.ALWAYS);
        addEdgeBtn.setOnAction(e -> onAddEdge());
        edgeRow2.getChildren().addAll(wLabel, weightField, addEdgeBtn);

        // ── Edge list ───────────────────────────────────────
        edgeList = new ListView<>();
        edgeList.getStyleClass().add("graph-builder-list");
        edgeList.setCellFactory(lv -> new StyledListCell<>());
        edgeList.setPrefHeight(80);
        edgeList.setMaxHeight(100);

        Button removeEdgeBtn = new Button("Remove Selected Edge");
        removeEdgeBtn.getStyleClass().add("secondary-button");
        removeEdgeBtn.setMaxWidth(Double.MAX_VALUE);
        removeEdgeBtn.setOnAction(e -> onRemoveEdge());

        // ── Clear ───────────────────────────────────────────
        Button clearBtn = new Button("Clear Graph");
        clearBtn.getStyleClass().add("secondary-button");
        clearBtn.setMaxWidth(Double.MAX_VALUE);
        clearBtn.setOnAction(e -> onClear());

        getChildren().addAll(
                header, directedCheck, summaryLabel,
                new Separator(),
                nodeHeader, nodeRow, nodeList, removeNodeBtn,
                new Separator(),
                edgeHeader, edgeRow1, edgeRow2, edgeList, removeEdgeBtn,
                new Separator(),
                clearBtn
        );
    }

    /** Sets the callback invoked whenever the graph changes. */
    public void setOnGraphChanged(Consumer<Graph> callback) {
        this.onGraphChanged = callback;
    }

    /** Returns the current custom graph. */
    public Graph getGraph() {
        return graph;
    }

    /** Returns true if the current graph has any non-1.0 weight edges. */
    public boolean isWeighted() {
        return graph.edges().stream().anyMatch(e -> e.weight() != 1.0);
    }

    /** Returns true if the current graph is directed. */
    public boolean isDirected() {
        return graph.isDirected();
    }

    /** Loads a preset graph into the builder for editing. */
    public void loadGraph(Graph source, boolean directed) {
        this.graph = source.copy();
        this.directedCheck.setSelected(directed);
        refreshLists();
        fireChanged();
    }

    // ── Event handlers ──────────────────────────────────────

    private void onAddNode() {
        String label = nodeField.getText().trim();
        if (label.isEmpty()) return;
        if (graph.hasNode(label)) {
            showWarning("Node '" + label + "' already exists.");
            return;
        }
        graph.addNode(label);
        nodeField.clear();
        refreshLists();
        fireChanged();
    }

    private void onRemoveNode() {
        String selected = nodeList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        graph.removeNode(selected);
        refreshLists();
        fireChanged();
    }

    private void onAddEdge() {
        String from = fromCombo.getValue();
        String to = toCombo.getValue();
        if (from == null || to == null) {
            showWarning("Select both From and To nodes.");
            return;
        }
        if (from.equals(to)) {
            showWarning("Self-loops are not supported.");
            return;
        }
        if (graph.hasEdge(from, to)) {
            showWarning("Edge " + from + " → " + to + " already exists.");
            return;
        }

        double weight;
        try {
            weight = Double.parseDouble(weightField.getText().trim());
        } catch (NumberFormatException ex) {
            showWarning("Invalid weight — enter a number.");
            return;
        }

        graph.addEdge(from, to, weight);
        refreshLists();
        fireChanged();
    }

    private void onRemoveEdge() {
        EdgeItem selected = edgeList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        graph.removeEdge(selected.from(), selected.to());
        refreshLists();
        fireChanged();
    }

    private void onClear() {
        graph = new Graph(directedCheck.isSelected());
        refreshLists();
        fireChanged();
    }

    /** Rebuilds the graph when directed/undirected toggle changes. */
    private void rebuildGraph() {
        boolean newDirected = directedCheck.isSelected();
        if (newDirected == graph.isDirected()) return;

        // Rebuild with same nodes and edges
        Graph newGraph = new Graph(newDirected);
        for (String node : graph.nodes()) {
            newGraph.addNode(node);
        }
        for (Graph.Edge edge : graph.edges()) {
            newGraph.addEdge(edge.from(), edge.to(), edge.weight());
        }
        this.graph = newGraph;
        refreshLists();
        fireChanged();
    }

    // ── UI helpers ──────────────────────────────────────────

    private void refreshLists() {
        // Nodes
        nodeList.setItems(FXCollections.observableArrayList(graph.nodes()));

        // Edge combos
        fromCombo.setItems(FXCollections.observableArrayList(graph.nodes()));
        toCombo.setItems(FXCollections.observableArrayList(graph.nodes()));

        // Edge list — typed items, display via toString()
        boolean directed = graph.isDirected();
        edgeList.setItems(FXCollections.observableArrayList(
                graph.edges().stream()
                        .map(e -> new EdgeItem(e.from(), e.to(), e.weight(), directed))
                        .toList()));

        summaryLabel.setText(graph.nodeCount() + " nodes, " + graph.edgeCount() + " edges"
                + (graph.isDirected() ? " (directed)" : " (undirected)"));
    }

    private void fireChanged() {
        if (onGraphChanged != null) {
            onGraphChanged.accept(graph);
        }
    }

    private static void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Graph Builder");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Custom ListCell that toggles a style class for selection instead of
     * relying on the CSS <code>:selected</code> pseudo-class (which Sonar
     * flags as non-standard).
     */
    private static class StyledListCell<T> extends ListCell<T> {
        private static final String SELECTED_CLASS = "graph-builder-list-selected";

        StyledListCell() {
            selectedProperty().addListener((obs, was, now) -> {
                if (Boolean.TRUE.equals(now)) {
                    if (!getStyleClass().contains(SELECTED_CLASS)) {
                        getStyleClass().add(SELECTED_CLASS);
                    }
                } else {
                    getStyleClass().remove(SELECTED_CLASS);
                }
            });
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty || item == null ? null : item.toString());
        }
    }
}
