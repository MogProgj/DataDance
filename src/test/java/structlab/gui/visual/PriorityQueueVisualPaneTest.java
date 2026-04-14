package structlab.gui.visual;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(JavaFxToolkitExtension.class)
@Timeout(10)
class PriorityQueueVisualPaneTest {

    @Test
    void constructionDoesNotThrow() {
        assertDoesNotThrow(PriorityQueueVisualPane::new);
    }

    @Test
    void updateWithEmptyModel() {
        PriorityQueueVisualPane pane = new PriorityQueueVisualPane();
        HeapStateModel model = new HeapStateModel(List.of(), 0, "null", 4);
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void updateWithSingleElement() {
        PriorityQueueVisualPane pane = new PriorityQueueVisualPane();
        HeapStateModel model = new HeapStateModel(List.of("42"), 1, "42", 4);
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void updateWithMultipleElements() {
        PriorityQueueVisualPane pane = new PriorityQueueVisualPane();
        HeapStateModel model = new HeapStateModel(
                List.of("5", "10", "15", "20", "25"), 5, "5", 8);
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void updateCanBeCalledRepeatedly() {
        PriorityQueueVisualPane pane = new PriorityQueueVisualPane();
        HeapStateModel model1 = new HeapStateModel(List.of("5"), 1, "5", 4);
        HeapStateModel model2 = new HeapStateModel(List.of("3", "5"), 2, "3", 4);
        HeapStateModel empty = new HeapStateModel(List.of(), 0, "null", 4);
        assertDoesNotThrow(() -> {
            pane.update(model1);
            pane.update(model2);
            pane.update(empty);
        });
    }

    @Test
    void sortedByPriorityReturnsAscendingOrder() {
        List<String> sorted = PriorityQueueVisualPane.sortedByPriority(
                List.of("20", "5", "15", "10"));
        assertEquals(List.of("5", "10", "15", "20"), sorted);
    }

    @Test
    void sortedByPriorityHandlesEmptyList() {
        List<String> sorted = PriorityQueueVisualPane.sortedByPriority(List.of());
        assertTrue(sorted.isEmpty());
    }

    @Test
    void sortedByPriorityHandlesSingleElement() {
        List<String> sorted = PriorityQueueVisualPane.sortedByPriority(List.of("7"));
        assertEquals(List.of("7"), sorted);
    }

    @Test
    void sortedByPriorityFallsBackToStringComparison() {
        List<String> sorted = PriorityQueueVisualPane.sortedByPriority(
                List.of("cherry", "apple", "banana"));
        assertEquals(List.of("apple", "banana", "cherry"), sorted);
    }

    @Test
    void sortedByPriorityDoesNotMutateInput() {
        List<String> input = List.of("20", "5", "15");
        PriorityQueueVisualPane.sortedByPriority(input);
        assertEquals(List.of("20", "5", "15"), input);
    }
}
