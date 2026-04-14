package structlab.gui.visual;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(JavaFxToolkitExtension.class)
@Timeout(10)
class FixedArrayVisualPaneTest {

    @Test
    void constructionDoesNotThrow() {
        assertDoesNotThrow(FixedArrayVisualPane::new);
    }

    @Test
    void updateWithEmptyModel() {
        FixedArrayVisualPane pane = new FixedArrayVisualPane();
        FixedArrayStateModel model = new FixedArrayStateModel(
                List.of(), List.of("null", "null", "null"), 0, 3);
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void updateWithPartiallyFilledModel() {
        FixedArrayVisualPane pane = new FixedArrayVisualPane();
        FixedArrayStateModel model = new FixedArrayStateModel(
                List.of("10", "20"), List.of("10", "20", "null", "null"), 2, 4);
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void updateWithFullModel() {
        FixedArrayVisualPane pane = new FixedArrayVisualPane();
        FixedArrayStateModel model = new FixedArrayStateModel(
                List.of("A", "B", "C"), List.of("A", "B", "C"), 3, 3);
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void parseAndUpdateRoundTrip() {
        String snapshot = "FixedArray{size=2, capacity=4, elements=[10, 20], raw=[10, 20, null, null]}";
        FixedArrayStateModel model = StateModelParser.parseFixedArray(snapshot);
        assertEquals(2, model.size());
        assertEquals(4, model.capacity());
        assertEquals(List.of("10", "20"), model.elements());
        assertFalse(model.isEmpty());
        assertFalse(model.isFull());

        FixedArrayVisualPane pane = new FixedArrayVisualPane();
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void fullModelReportsIsFull() {
        FixedArrayStateModel model = new FixedArrayStateModel(
                List.of("1", "2", "3"), List.of("1", "2", "3"), 3, 3);
        assertTrue(model.isFull());
        assertFalse(model.isEmpty());
    }

    @Test
    void emptyModelReportsIsEmpty() {
        FixedArrayStateModel model = new FixedArrayStateModel(
                List.of(), List.of("null", "null"), 0, 2);
        assertTrue(model.isEmpty());
        assertFalse(model.isFull());
    }
}
