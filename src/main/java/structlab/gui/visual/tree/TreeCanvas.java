package structlab.gui.visual.tree;

import javafx.scene.layout.Pane;
import structlab.gui.visual.OrderedTreeStateModel.TreeNodeInfo;

import java.util.List;

/**
 * Reusable canvas for rendering tree structures using {@link TreeNode}
 * and {@link TreeEdge} primitives with hierarchical (layered) layout.
 *
 * <p>Computes node positions for a complete binary tree layout and draws
 * curved edges between parent and child nodes.  This foundation supports
 * heap tree rendering now and can be extended for general tree/graph
 * layouts in the future.</p>
 *
 * <h3>Layout algorithm</h3>
 * Uses a top-down level-order placement:
 * <ul>
 *   <li>Level 0 (root): centered</li>
 *   <li>Level k: 2^k evenly spaced nodes</li>
 *   <li>Edges connect parent at index {@code i} to children at {@code 2i+1} and {@code 2i+2}</li>
 * </ul>
 */
public class TreeCanvas extends Pane {

    private static final double NODE_SIZE = 40;
    private static final double LEVEL_HEIGHT = 56;
    private static final double H_PADDING = 12;
    private static final double V_PADDING = 10;

    public TreeCanvas() {
        getStyleClass().add("tree-canvas");
        setMinHeight(50);
    }

    /**
     * Renders a binary heap as a tree with edges connecting parents to children.
     *
     * @param elements    heap array in level-order (index 0 = root)
     * @param rootEmphasized whether to emphasize the root node
     */
    public void renderHeapTree(List<String> elements, boolean rootEmphasized) {
        getChildren().clear();
        if (elements == null || elements.isEmpty()) return;

        int n = elements.size();
        int levels = (int) (Math.floor(Math.log(n) / Math.log(2))) + 1;

        // Calculate width: bottom level has up to 2^(levels-1) nodes
        int maxLeafCount = 1 << (levels - 1);
        double totalWidth = maxLeafCount * (NODE_SIZE + H_PADDING) + H_PADDING;
        double totalHeight = levels * LEVEL_HEIGHT + V_PADDING;

        setPrefSize(totalWidth, totalHeight);
        setMinWidth(totalWidth);

        // Calculate positions for every node
        double[] nodeX = new double[n];
        double[] nodeY = new double[n];

        for (int level = 0; level < levels; level++) {
            int levelStart = (1 << level) - 1;
            int levelCap = 1 << level;
            double levelWidth = totalWidth;
            double spacing = levelWidth / levelCap;

            for (int j = 0; j < levelCap && (levelStart + j) < n; j++) {
                int idx = levelStart + j;
                nodeX[idx] = spacing * j + spacing / 2;
                nodeY[idx] = V_PADDING + level * LEVEL_HEIGHT + NODE_SIZE / 2;
            }
        }

        // Draw edges first (behind nodes)
        for (int i = 0; i < n; i++) {
            int leftChild = 2 * i + 1;
            int rightChild = 2 * i + 2;

            if (leftChild < n) {
                boolean isRootEdge = (i == 0);
                TreeEdge edge = new TreeEdge(
                        nodeX[i], nodeY[i] + NODE_SIZE / 2 - 4,
                        nodeX[leftChild], nodeY[leftChild] - NODE_SIZE / 2 + 4,
                        isRootEdge);
                getChildren().add(edge);
            }
            if (rightChild < n) {
                boolean isRootEdge = (i == 0);
                TreeEdge edge = new TreeEdge(
                        nodeX[i], nodeY[i] + NODE_SIZE / 2 - 4,
                        nodeX[rightChild], nodeY[rightChild] - NODE_SIZE / 2 + 4,
                        isRootEdge);
                getChildren().add(edge);
            }
        }

        // Draw nodes on top
        for (int i = 0; i < n; i++) {
            boolean isRoot = (i == 0);
            TreeNode node = new TreeNode(elements.get(i), isRoot && rootEmphasized);
            node.setLayoutX(nodeX[i] - NODE_SIZE / 2);
            node.setLayoutY(nodeY[i] - NODE_SIZE / 2);
            getChildren().add(node);
        }
    }

    /**
     * Renders an ordered binary tree (BST / AVL) using the parsed node list.
     * Unlike heap rendering, this handles arbitrary (non-complete) tree shapes
     * by computing positions with a recursive inorder-offset algorithm.
     *
     * @param nodes the tree nodes in pre-order with left/right child indices
     */
    public void renderOrderedTree(List<TreeNodeInfo> nodes) {
        getChildren().clear();
        if (nodes == null || nodes.isEmpty()) return;

        int n = nodes.size();
        double[] posX = new double[n];
        double[] posY = new double[n];

        // Compute tree depth for sizing
        int depth = computeDepth(nodes, 0);
        int levels = depth + 1;

        // Inorder traversal assigns horizontal positions
        int[] counter = {0};
        assignPositions(nodes, 0, posX, posY, counter, 0);

        double totalWidth = (counter[0]) * (NODE_SIZE + H_PADDING) + H_PADDING;
        double totalHeight = levels * LEVEL_HEIGHT + V_PADDING;
        setPrefSize(Math.max(totalWidth, 100), totalHeight);
        setMinWidth(Math.max(totalWidth, 100));

        // Draw edges first
        for (int i = 0; i < n; i++) {
            TreeNodeInfo ni = nodes.get(i);
            if (ni.leftIndex() >= 0) {
                boolean isRoot = (i == 0);
                getChildren().add(new TreeEdge(
                        posX[i], posY[i] + NODE_SIZE / 2 - 4,
                        posX[ni.leftIndex()], posY[ni.leftIndex()] - NODE_SIZE / 2 + 4,
                        isRoot));
            }
            if (ni.rightIndex() >= 0) {
                boolean isRoot = (i == 0);
                getChildren().add(new TreeEdge(
                        posX[i], posY[i] + NODE_SIZE / 2 - 4,
                        posX[ni.rightIndex()], posY[ni.rightIndex()] - NODE_SIZE / 2 + 4,
                        isRoot));
            }
        }

        // Draw nodes on top
        for (int i = 0; i < n; i++) {
            boolean isRoot = (i == 0);
            TreeNode node = new TreeNode(nodes.get(i).value(), isRoot);
            node.setLayoutX(posX[i] - NODE_SIZE / 2);
            node.setLayoutY(posY[i] - NODE_SIZE / 2);
            getChildren().add(node);
        }
    }

    private int computeDepth(List<TreeNodeInfo> nodes, int index) {
        if (index < 0) return -1;
        TreeNodeInfo ni = nodes.get(index);
        return 1 + Math.max(
                computeDepth(nodes, ni.leftIndex()),
                computeDepth(nodes, ni.rightIndex()));
    }

    private void assignPositions(List<TreeNodeInfo> nodes, int index,
                                 double[] posX, double[] posY, int[] counter, int level) {
        if (index < 0) return;
        TreeNodeInfo ni = nodes.get(index);

        // Inorder: left, self, right — gives sorted horizontal ordering
        assignPositions(nodes, ni.leftIndex(), posX, posY, counter, level + 1);

        double spacing = NODE_SIZE + H_PADDING;
        posX[index] = H_PADDING + counter[0] * spacing + spacing / 2;
        posY[index] = V_PADDING + level * LEVEL_HEIGHT + NODE_SIZE / 2;
        counter[0]++;

        assignPositions(nodes, ni.rightIndex(), posX, posY, counter, level + 1);
    }
}
