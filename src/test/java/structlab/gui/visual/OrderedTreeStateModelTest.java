package structlab.gui.visual;

import org.junit.jupiter.api.Test;
import structlab.gui.visual.OrderedTreeStateModel.TreeNodeInfo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderedTreeStateModelTest {

    @Test
    void parseTreeStringNull() {
        List<TreeNodeInfo> nodes = OrderedTreeStateModel.parseTreeString(null);
        assertTrue(nodes.isEmpty());
    }

    @Test
    void parseTreeStringUnderscore() {
        List<TreeNodeInfo> nodes = OrderedTreeStateModel.parseTreeString("_");
        assertTrue(nodes.isEmpty());
    }

    @Test
    void parseTreeStringSingleNode() {
        // (42 _ _)
        List<TreeNodeInfo> nodes = OrderedTreeStateModel.parseTreeString("(42 _ _)");
        assertEquals(1, nodes.size());
        assertEquals("42", nodes.get(0).value());
        assertEquals(-1, nodes.get(0).leftIndex());
        assertEquals(-1, nodes.get(0).rightIndex());
    }

    @Test
    void parseTreeStringLeftOnly() {
        // (20 (10 _ _) _)
        List<TreeNodeInfo> nodes = OrderedTreeStateModel.parseTreeString("(20 (10 _ _) _)");
        assertEquals(2, nodes.size());
        // root at index 0
        assertEquals("20", nodes.get(0).value());
        assertEquals(1, nodes.get(0).leftIndex());
        assertEquals(-1, nodes.get(0).rightIndex());
        // left child at index 1
        assertEquals("10", nodes.get(1).value());
    }

    @Test
    void parseTreeStringRightOnly() {
        // (20 _ (30 _ _))
        List<TreeNodeInfo> nodes = OrderedTreeStateModel.parseTreeString("(20 _ (30 _ _))");
        assertEquals(2, nodes.size());
        assertEquals("20", nodes.get(0).value());
        assertEquals(-1, nodes.get(0).leftIndex());
        assertEquals(1, nodes.get(0).rightIndex());
        assertEquals("30", nodes.get(1).value());
    }

    @Test
    void parseTreeStringThreeNodes() {
        // (20 (10 _ _) (30 _ _))
        List<TreeNodeInfo> nodes = OrderedTreeStateModel.parseTreeString("(20 (10 _ _) (30 _ _))");
        assertEquals(3, nodes.size());
        // root
        assertEquals("20", nodes.get(0).value());
        assertEquals(1, nodes.get(0).leftIndex());
        assertEquals(2, nodes.get(0).rightIndex());
        // left child
        assertEquals("10", nodes.get(1).value());
        assertEquals(-1, nodes.get(1).leftIndex());
        assertEquals(-1, nodes.get(1).rightIndex());
        // right child
        assertEquals("30", nodes.get(2).value());
    }

    @Test
    void parseTreeStringDeepTree() {
        // (30 (20 (10 _ _) _) (40 _ _))
        List<TreeNodeInfo> nodes = OrderedTreeStateModel.parseTreeString("(30 (20 (10 _ _) _) (40 _ _))");
        assertEquals(4, nodes.size());
        assertEquals("30", nodes.get(0).value());
        assertEquals(1, nodes.get(0).leftIndex());   // node 20
        assertEquals(3, nodes.get(0).rightIndex());   // node 40
        assertEquals("20", nodes.get(1).value());
        assertEquals(2, nodes.get(1).leftIndex());    // node 10
        assertEquals(-1, nodes.get(1).rightIndex());
        assertEquals("10", nodes.get(2).value());
        assertEquals("40", nodes.get(3).value());
    }

    @Test
    void resultListIsUnmodifiable() {
        List<TreeNodeInfo> nodes = OrderedTreeStateModel.parseTreeString("(1 _ _)");
        assertThrows(UnsupportedOperationException.class, () -> nodes.add(new TreeNodeInfo("x", -1, -1)));
    }

    @Test
    void isEmptyWhenSizeZero() {
        OrderedTreeStateModel model =
                new OrderedTreeStateModel(List.of(), 0, -1, "null", "BinarySearchTree");
        assertTrue(model.isEmpty());
    }

    @Test
    void isNotEmptyWhenSizePositive() {
        OrderedTreeStateModel model =
                new OrderedTreeStateModel(
                        List.of(new TreeNodeInfo("5", -1, -1)),
                        1, 0, "5", "AVLTree");
        assertFalse(model.isEmpty());
    }
}
