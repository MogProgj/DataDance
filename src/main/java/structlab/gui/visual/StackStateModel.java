package structlab.gui.visual;

import java.util.List;

/**
 * View model for stack state — works for both ArrayStack and LinkedStack.
 *
 * @param elements stack elements from bottom to top
 * @param size     number of elements
 * @param topValue display string for the top element, or "null" if empty
 * @param implType implementation type: "ArrayStack" or "LinkedStack"
 */
public record StackStateModel(List<String> elements, int size, String topValue, String implType) implements VisualState {

    /**
     * Backward-compatible constructor that defaults to "ArrayStack".
     */
    public StackStateModel(List<String> elements, int size, String topValue) {
        this(elements, size, topValue, "ArrayStack");
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isArrayBacked() {
        return "ArrayStack".equals(implType);
    }

    public boolean isLinkedBacked() {
        return "LinkedStack".equals(implType);
    }
}
