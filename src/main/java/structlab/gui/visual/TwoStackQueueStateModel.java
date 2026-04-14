package structlab.gui.visual;

import java.util.List;

/**
 * View model for TwoStackQueue — preserves both stacks plus the effective queue order.
 *
 * <p>Unlike the generic {@link QueueStateModel} which flattens the queue into a single
 * element list, this model retains the inbox and outbox stack contents separately,
 * enabling the visual pane to show the two-stack implementation truthfully.</p>
 *
 * @param inboxElements  inbox (enqueue) stack, bottom to top
 * @param outboxElements outbox (dequeue) stack, bottom to top
 * @param queueOrder     effective queue order, front to rear
 * @param size           total number of elements
 * @param front          display string for the front element, or "null" if empty
 * @param rear           display string for the rear element, or "null" if empty
 */
public record TwoStackQueueStateModel(
        List<String> inboxElements,
        List<String> outboxElements,
        List<String> queueOrder,
        int size,
        String front,
        String rear
) implements VisualState {

    @Override
    public boolean isEmpty() {
        return size == 0;
    }
}
