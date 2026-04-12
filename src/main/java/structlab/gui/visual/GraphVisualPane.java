package structlab.gui.visual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import structlab.core.graph.AlgorithmFrame;
import structlab.core.graph.DijkstraRunner;
import structlab.core.graph.Graph;

import java.util.*;
import java.util.function.Consumer;

/**
 * Premium graph visualization pane for the Algorithm Lab.
 * Renders a node-link graph with algorithm state highlighting, edge weight
 * labels, distance badges, and shortest-path highlighting.
 *
 * <p>Uses a layered/hierarchical auto-layout computed from BFS layering
 * of the graph, similar in spirit to the existing {@link structlab.gui.visual.tree.TreeCanvas}
 * but generalized for arbitrary graphs.</p>
 */
public class GraphVisualPane extends VBox {

    private static final double NODE_RADIUS = 22;
    private static final double NODE_SIZE = NODE_RADIUS * 2;
    private static final double LEVEL_HEIGHT = 90;
    private static final double H_SPACING = 80;
    private static final double V_PADDING = 30;
    private static final double H_PADDING = 30;

    private final Pane canvas;
    private final Label statusLabel;
    private final HBox legendBar;
    private final Label stepLabel;

    private Graph currentGraph;
    private boolean weightedMode;
    private Map<String, double[]> nodePositions = new LinkedHashMap<>();

    // Interactive studio state
    private boolean editMode = false;
    private String selectedNode = null;
    private String dragNode = null;
    private double dragOffsetX, dragOffsetY;
    private Consumer<Graph> onGraphChanged;
    private int nextNodeId = 1;

    // Pan & zoom state
    private final Scale zoomTransform = new Scale(1, 1, 0, 0);
    private final Translate panTransform = new Translate(0, 0);
    private double panStartX, panStartY;
    private double panAnchorX, panAnchorY;
    private boolean isPanning = false;

    public GraphVisualPane() {
        getStyleClass().add("graph-visual-pane");
        setSpacing(0);

        // Status line at top
        HBox topBar = new HBox(12);
        topBar.getStyleClass().add("graph-top-bar");
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(8, 14, 8, 14));
        statusLabel = new Label("Select a graph preset and algorithm to begin.");
        statusLabel.getStyleClass().add("graph-status-label");
        statusLabel.setWrapText(true);
        stepLabel = new Label("");
        stepLabel.getStyleClass().add("graph-step-label");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(statusLabel, spacer, stepLabel);

        // Canvas for nodes and edges
        canvas = new Pane();
        canvas.getStyleClass().add("graph-canvas");
        canvas.setMinHeight(200);
        canvas.getTransforms().addAll(panTransform, zoomTransform);
        VBox.setVgrow(canvas, Priority.ALWAYS);

        // Scroll-to-zoom
        canvas.setOnScroll(this::onCanvasScroll);
        // Canvas click / pan
        canvas.setOnMousePressed(this::onCanvasMousePressed);
        canvas.setOnMouseDragged(this::onCanvasMouseDragged);
        canvas.setOnMouseReleased(this::onCanvasMouseReleased);

        // Legend bar
        legendBar = new HBox(16);
        legendBar.getStyleClass().add("graph-legend-bar");
        legendBar.setAlignment(Pos.CENTER_LEFT);
        legendBar.setPadding(new Insets(6, 14, 6, 14));
        buildLegend(false);

        getChildren().addAll(topBar, canvas, legendBar);
    }

    /** Sets up the graph topology and computes layout positions. */
    public void setGraph(Graph graph) {
        setGraph(graph, false);
    }

    /** Sets up the graph topology with explicit weighted mode flag. */
    public void setGraph(Graph graph, boolean weighted) {
        this.currentGraph = graph;
        this.weightedMode = weighted;
        this.nodePositions = computeLayout(graph);
        buildLegend(weighted);
        renderIdle();
    }

    /** Renders the graph in idle state (no algorithm running). */
    public void renderIdle() {
        canvas.getChildren().clear();
        if (currentGraph == null) return;
        drawEdges(null);
        drawNodes(null);
        String info = currentGraph.nodeCount() + " nodes, " + currentGraph.edgeCount() + " edges"
                + (currentGraph.isDirected() ? " (directed)" : " (undirected)");
        if (weightedMode) info += " — weighted";
        statusLabel.setText(info);
        stepLabel.setText("");
    }

    /** Renders the graph with algorithm frame state highlighting. */
    public void renderFrame(AlgorithmFrame frame) {
        canvas.getChildren().clear();
        if (currentGraph == null || frame == null) return;
        drawEdges(frame);
        drawNodes(frame);
        statusLabel.setText(frame.statusMessage());
        stepLabel.setText("Step " + (frame.stepIndex() + 1));
    }

    /** Updates status text. */
    public void setStatusText(String text) {
        statusLabel.setText(text);
    }

    // ── Layout computation ──────────────────────────────────

    private Map<String, double[]> computeLayout(Graph graph) {
        Map<String, double[]> positions = new LinkedHashMap<>();
        if (graph.nodeCount() == 0) return positions;

        List<String> nodes = graph.nodes();

        // BFS layering from first node
        Map<String, Integer> layerMap = new LinkedHashMap<>();
        Deque<String> queue = new ArrayDeque<>();
        String start = nodes.get(0);
        queue.add(start);
        layerMap.put(start, 0);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            int layer = layerMap.get(current);
            for (String neighbor : graph.neighbors(current)) {
                if (!layerMap.containsKey(neighbor)) {
                    layerMap.put(neighbor, layer + 1);
                    queue.add(neighbor);
                }
            }
        }

        // Handle disconnected nodes
        int maxLayer = layerMap.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        for (String node : nodes) {
            if (!layerMap.containsKey(node)) {
                maxLayer++;
                layerMap.put(node, maxLayer);
            }
        }

        // Group nodes by layer
        Map<Integer, List<String>> layers = new TreeMap<>();
        for (Map.Entry<String, Integer> entry : layerMap.entrySet()) {
            layers.computeIfAbsent(entry.getValue(), k -> new ArrayList<>()).add(entry.getKey());
        }

        // Position nodes
        int numLayers = layers.size();
        double maxNodesInLayer = layers.values().stream().mapToInt(List::size).max().orElse(1);
        double totalWidth = Math.max(maxNodesInLayer * H_SPACING + H_PADDING * 2, 300);
        double totalHeight = numLayers * LEVEL_HEIGHT + V_PADDING * 2;

        canvas.setPrefSize(totalWidth, totalHeight);
        canvas.setMinWidth(totalWidth);
        canvas.setMinHeight(totalHeight);

        int layerIndex = 0;
        for (Map.Entry<Integer, List<String>> entry : layers.entrySet()) {
            List<String> layerNodes = entry.getValue();
            int count = layerNodes.size();
            double layerWidth = count * H_SPACING;
            double startX = (totalWidth - layerWidth) / 2 + H_SPACING / 2;
            double y = V_PADDING + layerIndex * LEVEL_HEIGHT + NODE_RADIUS;

            for (int i = 0; i < count; i++) {
                double x = startX + i * H_SPACING;
                positions.put(layerNodes.get(i), new double[]{x, y});
            }
            layerIndex++;
        }

        return positions;
    }

    // ── Drawing ─────────────────────────────────────────────

    private void drawEdges(AlgorithmFrame frame) {
        if (currentGraph == null) return;
        Set<AlgorithmFrame.TraversalEdge> treeEdges = frame != null ? frame.treeEdges() : Set.of();
        List<String> shortestPath = frame != null ? frame.shortestPath() : List.of();
        boolean isBridgeAlgo = frame != null
                && frame.algorithm() == AlgorithmFrame.AlgorithmType.BRIDGES;
        boolean isAPAlgo = frame != null
                && frame.algorithm() == AlgorithmFrame.AlgorithmType.ARTICULATION_POINTS;

        // Build bridge edge keys for BRIDGES algorithm (pairs in shortestPath)
        Set<String> bridgeEdgeKeys = new java.util.HashSet<>();
        if (isBridgeAlgo) {
            for (int i = 0; i + 1 < shortestPath.size(); i += 2) {
                bridgeEdgeKeys.add(shortestPath.get(i) + "\0" + shortestPath.get(i + 1));
                bridgeEdgeKeys.add(shortestPath.get(i + 1) + "\0" + shortestPath.get(i));
            }
        }

        Set<String> spEdgeKeys = isBridgeAlgo || isAPAlgo
                ? Set.of() : buildShortestPathEdgeSet(shortestPath);

        for (Graph.Edge edge : currentGraph.edges()) {
            double[] from = nodePositions.get(edge.from());
            double[] to = nodePositions.get(edge.to());
            if (from == null || to == null) continue;

            boolean isTreeEdge = treeEdges.contains(
                    new AlgorithmFrame.TraversalEdge(edge.from(), edge.to()))
                    || treeEdges.contains(
                    new AlgorithmFrame.TraversalEdge(edge.to(), edge.from()));

            boolean isShortestPathEdge = spEdgeKeys.contains(edge.from() + "\0" + edge.to())
                    || spEdgeKeys.contains(edge.to() + "\0" + edge.from());

            boolean isBridgeEdge = bridgeEdgeKeys.contains(
                    edge.from() + "\0" + edge.to());

            String edgeClass;
            if (isBridgeEdge) {
                edgeClass = "graph-edge-bridge";
            } else if (isShortestPathEdge) {
                edgeClass = "graph-edge-shortest-path";
            } else if (isTreeEdge) {
                edgeClass = "graph-edge-tree";
            } else {
                edgeClass = "graph-edge-default";
            }

            // Compute slight curve via control points
            double dx = to[0] - from[0];
            double dy = to[1] - from[1];
            double midX = (from[0] + to[0]) / 2;
            double midY = (from[1] + to[1]) / 2;

            double len = Math.sqrt(dx * dx + dy * dy);
            double offset = len > 0 ? Math.min(12, len * 0.08) : 0;
            double nx = len > 0 ? -dy / len * offset : 0;
            double ny = len > 0 ? dx / len * offset : 0;

            CubicCurve curve = new CubicCurve();
            curve.setStartX(from[0]);
            curve.setStartY(from[1]);
            curve.setEndX(to[0]);
            curve.setEndY(to[1]);
            curve.setControlX1(midX + nx);
            curve.setControlY1(midY + ny);
            curve.setControlX2(midX + nx);
            curve.setControlY2(midY + ny);
            curve.setFill(null);
            curve.setStrokeLineCap(StrokeLineCap.ROUND);
            curve.setMouseTransparent(true);
            curve.getStyleClass().addAll("graph-edge", edgeClass);

            canvas.getChildren().add(curve);

            // Draw arrowhead for directed graphs (use curve tangent for correct alignment)
            if (currentGraph.isDirected()) {
                drawArrowhead(midX + nx, midY + ny, to,
                        isShortestPathEdge || isTreeEdge || isBridgeEdge);
            }

            // Draw weight label for weighted graphs (fixed perpendicular offset)
            if (weightedMode) {
                double perpX = len > 0 ? -dy / len : 0;
                double perpY = len > 0 ? dx / len : 0;
                double labelOff = 14;
                drawEdgeWeight(midX + perpX * labelOff, midY + perpY * labelOff,
                        edge.weight(), isShortestPathEdge || isBridgeEdge);
            }
        }
    }

    private void drawEdgeWeight(double x, double y, double weight, boolean highlighted) {
        Label wLabel = new Label(DijkstraRunner.formatDist(weight));
        wLabel.getStyleClass().add("graph-edge-weight");
        if (highlighted) {
            wLabel.getStyleClass().add("graph-edge-weight-highlighted");
        }
        wLabel.setMouseTransparent(true);
        // Offset slightly from the edge midpoint
        wLabel.setLayoutX(x - 8);
        wLabel.setLayoutY(y - 10);
        canvas.getChildren().add(wLabel);
    }

    private void drawArrowhead(double ctrlX, double ctrlY, double[] to, boolean highlighted) {
        // Tangent at t=1 of cubic Bezier: direction from control point to endpoint
        double dx = to[0] - ctrlX;
        double dy = to[1] - ctrlY;
        double len = Math.sqrt(dx * dx + dy * dy);
        if (len < 1) return;

        double ux = dx / len;
        double uy = dy / len;
        double tipX = to[0] - ux * NODE_RADIUS;
        double tipY = to[1] - uy * NODE_RADIUS;

        double arrowLen = 10;
        double arrowWidth = 5;

        double ax = tipX - ux * arrowLen + uy * arrowWidth;
        double ay = tipY - uy * arrowLen - ux * arrowWidth;
        double bx = tipX - ux * arrowLen - uy * arrowWidth;
        double by = tipY - uy * arrowLen + ux * arrowWidth;

        Polygon arrow = new Polygon(tipX, tipY, ax, ay, bx, by);
        arrow.getStyleClass().add(highlighted ? "graph-arrow-tree" : "graph-arrow-default");
        arrow.setMouseTransparent(true);
        canvas.getChildren().add(arrow);
    }

    private void drawNodes(AlgorithmFrame frame) {
        if (currentGraph == null) return;
        Set<String> visited = frame != null ? frame.visited() : Set.of();
        List<String> frontier = frame != null ? frame.frontier() : List.of();
        String currentNode = frame != null ? frame.currentNode() : null;
        String targetNode = frame != null ? frame.targetNode() : null;
        Map<String, Double> distances = frame != null ? frame.distances() : Map.of();
        List<String> shortestPath = frame != null ? frame.shortestPath() : List.of();
        boolean showDistances = frame != null
                && (frame.algorithm() == AlgorithmFrame.AlgorithmType.DIJKSTRA
                    || frame.algorithm() == AlgorithmFrame.AlgorithmType.BELLMAN_FORD
                    || frame.algorithm() == AlgorithmFrame.AlgorithmType.A_STAR);
        boolean showIndegrees = frame != null
                && frame.algorithm() == AlgorithmFrame.AlgorithmType.TOPOLOGICAL_SORT;
        boolean isMST = frame != null
                && (frame.algorithm() == AlgorithmFrame.AlgorithmType.PRIM
                    || frame.algorithm() == AlgorithmFrame.AlgorithmType.KRUSKAL);
        boolean isSCC = frame != null
                && frame.algorithm() == AlgorithmFrame.AlgorithmType.SCC;
        boolean isBridges = frame != null
                && frame.algorithm() == AlgorithmFrame.AlgorithmType.BRIDGES;
        boolean isAP = frame != null
                && frame.algorithm() == AlgorithmFrame.AlgorithmType.ARTICULATION_POINTS;

        // Extract articulation points from shortestPath if applicable
        Set<String> articulationPoints = isAP ? new java.util.HashSet<>(shortestPath) : Set.of();

        for (String nodeLabel : currentGraph.nodes()) {
            double[] pos = nodePositions.get(nodeLabel);
            if (pos == null) continue;

            // Container for node circle + optional distance badge
            VBox nodeGroup = new VBox(2);
            nodeGroup.setAlignment(Pos.CENTER);

            StackPane nodeCircle = new StackPane();
            nodeCircle.getStyleClass().add("graph-node");
            nodeCircle.setPrefSize(NODE_SIZE, NODE_SIZE);
            nodeCircle.setMinSize(NODE_SIZE, NODE_SIZE);
            nodeCircle.setMaxSize(NODE_SIZE, NODE_SIZE);

            // State-based styling
            boolean isOnShortestPath = shortestPath.contains(nodeLabel);
            boolean isArticulationPoint = articulationPoints.contains(nodeLabel);
            if (nodeLabel.equals(currentNode)) {
                nodeCircle.getStyleClass().add("graph-node-current");
            } else if (isArticulationPoint && isAP) {
                nodeCircle.getStyleClass().add("graph-node-articulation");
            } else if (isOnShortestPath && !isAP && !isBridges) {
                nodeCircle.getStyleClass().add("graph-node-shortest-path");
            } else if (nodeLabel.equals(targetNode)) {
                nodeCircle.getStyleClass().add(visited.contains(nodeLabel)
                        ? "graph-node-target-settled" : "graph-node-target");
            } else if (frontier.contains(nodeLabel)) {
                nodeCircle.getStyleClass().add("graph-node-frontier");
            } else if (visited.contains(nodeLabel)) {
                if (isSCC) {
                    // Color by SCC component
                    int compId = distances.containsKey(nodeLabel)
                            ? distances.get(nodeLabel).intValue() : 0;
                    nodeCircle.getStyleClass().add("graph-node-scc-" + ((compId - 1) % 6 + 1));
                } else {
                    nodeCircle.getStyleClass().add("graph-node-visited");
                }
            } else {
                nodeCircle.getStyleClass().add("graph-node-idle");
            }

            // Interactive selection highlight
            if (editMode && nodeLabel.equals(selectedNode) && frame == null) {
                nodeCircle.getStyleClass().add("graph-node-selected");
            }

            Label label = new Label(nodeLabel);
            label.getStyleClass().add("graph-node-label");
            if (nodeLabel.equals(currentNode)) {
                label.getStyleClass().add("graph-node-label-current");
            }

            nodeCircle.getChildren().add(label);
            nodeGroup.getChildren().add(nodeCircle);

            // Distance badge for Dijkstra / Bellman-Ford
            if (showDistances && distances.containsKey(nodeLabel)) {
                double dist = distances.get(nodeLabel);
                Label distLabel = new Label(DijkstraRunner.formatDist(dist));
                distLabel.getStyleClass().add("graph-distance-badge");
                if (visited.contains(nodeLabel)) {
                    distLabel.getStyleClass().add("graph-distance-settled");
                }
                nodeGroup.getChildren().add(distLabel);
            }

            // Indegree badge for Topological Sort
            if (showIndegrees && distances.containsKey(nodeLabel)) {
                int indegree = distances.get(nodeLabel).intValue();
                Label indLabel = new Label("in:" + indegree);
                indLabel.getStyleClass().add("graph-distance-badge");
                if (indegree == 0) {
                    indLabel.getStyleClass().add("graph-indegree-zero");
                }
                nodeGroup.getChildren().add(indLabel);
            }

            // Disc/Low badge for Bridges / Articulation Points
            if ((isBridges || isAP) && distances.containsKey(nodeLabel)) {
                double encoded = distances.get(nodeLabel);
                int d = (int) (encoded / 1000.0);
                int l = (int) (encoded % 1000.0);
                Label dlLabel = new Label("d:" + d + " l:" + l);
                dlLabel.getStyleClass().add("graph-distance-badge");
                if (isArticulationPoint) {
                    dlLabel.getStyleClass().add("graph-badge-articulation");
                }
                nodeGroup.getChildren().add(dlLabel);
            }

            // SCC component badge
            if (isSCC && distances.containsKey(nodeLabel)) {
                int compId = distances.get(nodeLabel).intValue();
                Label sccLabel = new Label("SCC#" + compId);
                sccLabel.getStyleClass().add("graph-distance-badge");
                sccLabel.getStyleClass().add("graph-badge-scc-" + ((compId - 1) % 6 + 1));
                nodeGroup.getChildren().add(sccLabel);
            }

            nodeGroup.setLayoutX(pos[0] - NODE_RADIUS);
            nodeGroup.setLayoutY(pos[1] - NODE_RADIUS);

            canvas.getChildren().add(nodeGroup);
        }
    }

    // ── Shortest-path edge tracking ─────────────────────────

    private static Set<String> buildShortestPathEdgeSet(List<String> path) {
        Set<String> edgeKeys = new HashSet<>();
        for (int i = 0; i + 1 < path.size(); i++) {
            edgeKeys.add(path.get(i) + "\0" + path.get(i + 1));
        }
        return edgeKeys;
    }

    // ── Legend ───────────────────────────────────────────────

    private void buildLegend(boolean weighted) {
        legendBar.getChildren().clear();
        legendBar.getChildren().addAll(
                legendItem("graph-legend-current", "Current"),
                legendItem("graph-legend-frontier", "Frontier"),
                legendItem("graph-legend-visited", weighted ? "Settled" : "Visited"),
                legendItem("graph-legend-idle", "Unvisited"),
                legendItem("graph-legend-tree-edge", weighted ? "SP Tree" : "Tree Edge")
        );
        if (weighted) {
            legendBar.getChildren().addAll(
                    legendItem("graph-legend-shortest-path", "Shortest Path"),
                    legendItem("graph-legend-target", "Target")
            );
        }
    }

    private HBox legendItem(String styleClass, String text) {
        Region dot = new Region();
        dot.getStyleClass().addAll("graph-legend-dot", styleClass);
        dot.setPrefSize(10, 10);
        dot.setMinSize(10, 10);
        dot.setMaxSize(10, 10);
        Label label = new Label(text);
        label.getStyleClass().add("graph-legend-text");
        HBox item = new HBox(5, dot, label);
        item.setAlignment(Pos.CENTER_LEFT);
        return item;
    }

    // ── Interactive studio ───────────────────────────────────

    /** Enables or disables interactive edit mode. */
    public void setEditMode(boolean edit) {
        this.editMode = edit;
        this.selectedNode = null;
        if (edit && currentGraph != null) {
            renderIdle();
        }
    }

    public boolean isEditMode() {
        return editMode;
    }

    /** Sets the callback invoked when the graph is modified interactively. */
    public void setOnGraphChanged(Consumer<Graph> callback) {
        this.onGraphChanged = callback;
    }

    /** Returns the currently selected node, or null. */
    public String getSelectedNode() {
        return selectedNode;
    }

    /** Deselects any selected node. */
    public void clearSelection() {
        this.selectedNode = null;
        if (editMode && currentGraph != null) renderIdle();
    }

    /** Returns a copy of the current node positions map. */
    public Map<String, double[]> getNodePositions() {
        return new LinkedHashMap<>(nodePositions);
    }

    /** Sets positions from an external source (e.g. restored layout). */
    public void setNodePositions(Map<String, double[]> positions) {
        this.nodePositions = new LinkedHashMap<>(positions);
    }

    /** Resets pan and zoom to defaults. */
    public void resetView() {
        zoomTransform.setX(1);
        zoomTransform.setY(1);
        panTransform.setX(0);
        panTransform.setY(0);
    }

    /** Fits the current graph into the visible viewport. */
    public void fitToView() {
        if (nodePositions.isEmpty()) return;
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
        for (double[] pos : nodePositions.values()) {
            minX = Math.min(minX, pos[0]);
            minY = Math.min(minY, pos[1]);
            maxX = Math.max(maxX, pos[0]);
            maxY = Math.max(maxY, pos[1]);
        }
        double graphW = maxX - minX + NODE_SIZE * 2;
        double graphH = maxY - minY + NODE_SIZE * 2;
        double viewW = getWidth() > 0 ? getWidth() : 600;
        double viewH = getHeight() > 0 ? getHeight() - 60 : 400;
        double scale = Math.min(viewW / graphW, viewH / graphH);
        scale = Math.max(0.3, Math.min(scale, 2.0));
        zoomTransform.setX(scale);
        zoomTransform.setY(scale);
        panTransform.setX(-minX * scale + NODE_SIZE * scale);
        panTransform.setY(-minY * scale + NODE_SIZE * scale);
    }

    // ── Canvas mouse handlers ────────────────────────────────

    private void onCanvasScroll(ScrollEvent e) {
        double factor = e.getDeltaY() > 0 ? 1.1 : 0.9;
        double newScale = zoomTransform.getX() * factor;
        newScale = Math.max(0.3, Math.min(newScale, 3.0));
        zoomTransform.setX(newScale);
        zoomTransform.setY(newScale);
        e.consume();
    }

    private void onCanvasMousePressed(MouseEvent e) {
        if (e.getButton() == MouseButton.MIDDLE
                || (e.getButton() == MouseButton.PRIMARY && e.isShiftDown())) {
            // Pan start
            isPanning = true;
            panStartX = e.getScreenX();
            panStartY = e.getScreenY();
            panAnchorX = panTransform.getX();
            panAnchorY = panTransform.getY();
            e.consume();
            return;
        }

        if (!editMode || e.getButton() != MouseButton.PRIMARY) return;

        // Convert to canvas coords accounting for transforms
        double cx = (e.getX() - panTransform.getX()) / zoomTransform.getX();
        double cy = (e.getY() - panTransform.getY()) / zoomTransform.getY();

        // Check if clicking on existing node
        String hitNode = hitTestNode(cx, cy);
        if (hitNode != null) {
            if (selectedNode != null && !selectedNode.equals(hitNode)) {
                // Connect two nodes
                if (!currentGraph.hasEdge(selectedNode, hitNode)) {
                    currentGraph.addEdge(selectedNode, hitNode);
                    selectedNode = null;
                    fireGraphChanged();
                    renderIdle();
                }
            } else if (selectedNode != null && selectedNode.equals(hitNode)) {
                // Deselect
                selectedNode = null;
                renderIdle();
            } else {
                // Select node (or start drag)
                selectedNode = hitNode;
                dragNode = hitNode;
                dragOffsetX = cx - nodePositions.get(hitNode)[0];
                dragOffsetY = cy - nodePositions.get(hitNode)[1];
                renderIdle();
            }
            e.consume();
            return;
        }

        // Click on empty space — add node
        selectedNode = null;
        String label = generateNodeLabel();
        currentGraph.addNode(label);
        nodePositions.put(label, new double[]{cx, cy});
        fireGraphChanged();
        renderIdle();
        e.consume();
    }

    private void onCanvasMouseDragged(MouseEvent e) {
        if (isPanning) {
            panTransform.setX(panAnchorX + (e.getScreenX() - panStartX));
            panTransform.setY(panAnchorY + (e.getScreenY() - panStartY));
            e.consume();
            return;
        }
        if (!editMode || dragNode == null) return;
        double cx = (e.getX() - panTransform.getX()) / zoomTransform.getX();
        double cy = (e.getY() - panTransform.getY()) / zoomTransform.getY();
        nodePositions.put(dragNode, new double[]{cx - dragOffsetX, cy - dragOffsetY});
        renderIdle();
        e.consume();
    }

    private void onCanvasMouseReleased(MouseEvent e) {
        if (isPanning) {
            isPanning = false;
            e.consume();
            return;
        }
        if (dragNode != null) {
            dragNode = null;
            e.consume();
        }
    }

    /** Removes the given node interactively. */
    public void removeNodeInteractive(String node) {
        if (currentGraph == null || !currentGraph.hasNode(node)) return;
        currentGraph.removeNode(node);
        nodePositions.remove(node);
        if (node.equals(selectedNode)) selectedNode = null;
        fireGraphChanged();
        renderIdle();
    }

    /** Removes an edge interactively. */
    public void removeEdgeInteractive(String from, String to) {
        if (currentGraph == null) return;
        currentGraph.removeEdge(from, to);
        fireGraphChanged();
        renderIdle();
    }

    private String hitTestNode(double x, double y) {
        for (Map.Entry<String, double[]> entry : nodePositions.entrySet()) {
            double[] pos = entry.getValue();
            double dx = x - pos[0];
            double dy = y - pos[1];
            if (dx * dx + dy * dy <= NODE_RADIUS * NODE_RADIUS) {
                return entry.getKey();
            }
        }
        return null;
    }

    private String generateNodeLabel() {
        while (currentGraph.hasNode(String.valueOf(nextNodeId))) {
            nextNodeId++;
        }
        return String.valueOf(nextNodeId++);
    }

    private void fireGraphChanged() {
        if (onGraphChanged != null) {
            onGraphChanged.accept(currentGraph);
        }
    }
}
