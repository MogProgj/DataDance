package structlab.gui.visual;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * View model for ordered tree (BST / AVL) visual state components.
 * <p>
 * Unlike {@link HeapStateModel} which stores a flat level-order array,
 * this model stores the tree in a pre-order parenthesised format that
 * preserves the actual tree shape (which may not be complete).
 *
 * @param nodes       pre-order list of tree nodes with left/right child indices
 * @param size        number of elements
 * @param height      tree height (-1 if empty)
 * @param rootValue   root node value, or "null" if empty
 * @param implName    "BinarySearchTree" or "AVLTree"
 */
public record OrderedTreeStateModel(
        List<TreeNodeInfo> nodes,
        int size,
        int height,
        String rootValue,
        String implName
) implements VisualState {

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Describes a single node in the tree with indices to its children.
     *
     * @param value      display value
     * @param leftIndex  index in the nodes list of the left child, or -1
     * @param rightIndex index in the nodes list of the right child, or -1
     */
    public record TreeNodeInfo(String value, int leftIndex, int rightIndex) {}

    /**
     * Parses the pre-order parenthesised tree format produced by BST/AVL snapshot:
     * {@code (value left right)} where missing children are {@code _}.
     */
    public static List<TreeNodeInfo> parseTreeString(String treeStr) {
        if (treeStr == null || treeStr.equals("_")) {
            return Collections.emptyList();
        }
        List<TreeNodeInfo> nodes = new ArrayList<>();
        parseNode(treeStr, new int[]{0}, nodes);
        return Collections.unmodifiableList(nodes);
    }

    private static int parseNode(String s, int[] pos, List<TreeNodeInfo> nodes) {
        skipSpaces(s, pos);
        if (pos[0] >= s.length()) return -1;

        if (s.charAt(pos[0]) == '_') {
            pos[0]++;
            return -1;
        }

        if (s.charAt(pos[0]) != '(') return -1;
        pos[0]++; // skip '('

        // Read value (until space)
        int start = pos[0];
        while (pos[0] < s.length() && s.charAt(pos[0]) != ' ') pos[0]++;
        String value = s.substring(start, pos[0]);

        int myIndex = nodes.size();
        nodes.add(null); // placeholder

        skipSpaces(s, pos);
        int leftIndex = parseNode(s, pos, nodes);

        skipSpaces(s, pos);
        int rightIndex = parseNode(s, pos, nodes);

        skipSpaces(s, pos);
        if (pos[0] < s.length() && s.charAt(pos[0]) == ')') pos[0]++;

        nodes.set(myIndex, new TreeNodeInfo(value, leftIndex, rightIndex));
        return myIndex;
    }

    private static void skipSpaces(String s, int[] pos) {
        while (pos[0] < s.length() && s.charAt(pos[0]) == ' ') pos[0]++;
    }
}
