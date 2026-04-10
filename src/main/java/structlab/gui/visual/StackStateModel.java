package structlab.gui.visual;

import java.util.List;

/**
 * View model for stack state — works for both ArrayStack and LinkedStack.
 *
 * @param elements stack elements from bottom to top
 * @param size     number of elements
 * @param topValue display string for the top element, or "null" if empty
 */
public record StackStateModel(List<String> elements, int size, String topValue) {

    public boolean isEmpty() {
        return size == 0;
    }
}
