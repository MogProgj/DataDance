package structlab.gui.visual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.Polygon;
import structlab.core.graph.AlgorithmFrame;
import structlab.core.graph.DijkstraRunner;
import structlab.core.graph.Graph;

import java.util.*;

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
        VBox.setVgrow(canvas, Priority.ALWAYS);

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
        Set<String> spEdgeKeys = buildShortestPathEdgeSet(shortestPath);

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

            String edgeClass;
            if (isShortestPathEdge) {
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

            // Draw arrowhead for directed graphs
            if (currentGraph.isDirected()) {
                drawArrowhead(from, to, isShortestPathEdge || isTreeEdge);
            }

            // Draw weight label for weighted graphs
            if (weightedMode && edge.weight() != 1.0) {
                drawEdgeWeight(midX + nx * 2.5, midY + ny * 2.5, edge.weight(),
                        isShortestPathEdge);
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

    private void drawArrowhead(double[] from, double[] to, boolean highlighted) {
        double dx = to[0] - from[0];
        double dy = to[1] - from[1];
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
        boolean isDijkstra = frame != null
                && frame.algorithm() == AlgorithmFrame.AlgorithmType.DIJKSTRA;

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
            if (nodeLabel.equals(currentNode)) {
                nodeCircle.getStyleClass().add("graph-node-current");
            } else if (isOnShortestPath) {
                nodeCircle.getStyleClass().add("graph-node-shortest-path");
            } else if (nodeLabel.equals(targetNode)) {
                nodeCircle.getStyleClass().add(visited.contains(nodeLabel)
                        ? "graph-node-target-settled" : "graph-node-target");
            } else if (frontier.contains(nodeLabel)) {
                nodeCircle.getStyleClass().add("graph-node-frontier");
            } else if (visited.contains(nodeLabel)) {
                nodeCircle.getStyleClass().add("graph-node-visited");
            } else {
                nodeCircle.getStyleClass().add("graph-node-idle");
            }

            Label label = new Label(nodeLabel);
            label.getStyleClass().add("graph-node-label");
            if (nodeLabel.equals(currentNode)) {
                label.getStyleClass().add("graph-node-label-current");
            }

            nodeCircle.getChildren().add(label);
            nodeGroup.getChildren().add(nodeCircle);

            // Distance badge for Dijkstra
            if (isDijkstra && distances.containsKey(nodeLabel)) {
                double dist = distances.get(nodeLabel);
                Label distLabel = new Label(DijkstraRunner.formatDist(dist));
                distLabel.getStyleClass().add("graph-distance-badge");
                if (visited.contains(nodeLabel)) {
                    distLabel.getStyleClass().add("graph-distance-settled");
                }
                nodeGroup.getChildren().add(distLabel);
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
}
