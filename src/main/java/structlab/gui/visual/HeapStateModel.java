package structlab.gui.visual;

import java.util.List;

/**
 * View model for heap visual state components.
 *
 * @param elements heap array in level-order (index 0 = root)
 * @param size     number of elements in the heap
 * @param minValue the root/minimum value, or "null" if empty
 * @param capacity backing array capacity
 */
public record HeapStateModel(
        List<String> elements,
        int size,
        String minValue,
        int capacity
) {

    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the number of complete levels in the tree representation.
     * Level 0 has 1 node, level 1 has 2, level k has 2^k.
     */
    public int levels() {
        if (size == 0) return 0;
        return (int) (Math.floor(Math.log(size) / Math.log(2))) + 1;
    }

    /**
     * Returns the starting index for a given tree level (0-based).
     */
    public static int levelStart(int level) {
        return (1 << level) - 1; // 2^level - 1
    }

    /**
     * Returns the maximum number of nodes at a given tree level.
     */
    public static int levelCapacity(int level) {
        return 1 << level; // 2^level
    }
}
