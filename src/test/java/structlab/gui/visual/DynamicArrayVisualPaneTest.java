package structlab.gui.visual;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(JavaFxToolkitExtension.class)
@Timeout(10)
class DynamicArrayVisualPaneTest {

    @Test
    void constructionDoesNotThrow() {
        assertDoesNotThrow(DynamicArrayVisualPane::new);
    }

    @Test
    void updateWithEmptyModel() {
        DynamicArrayVisualPane pane = new DynamicArrayVisualPane();
        DynamicArrayStateModel model = new DynamicArrayStateModel(
                List.of(), List.of("null", "null", "null", "null"), 0, 4);
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void updateWithPartiallyFilledModel() {
        DynamicArrayVisualPane pane = new DynamicArrayVisualPane();
        DynamicArrayStateModel model = new DynamicArrayStateModel(
                List.of("10", "20"), List.of("10", "20", "null", "null"), 2, 4);
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void updateWithFullCapacityModel() {
        DynamicArrayVisualPane pane = new DynamicArrayVisualPane();
        DynamicArrayStateModel model = new DynamicArrayStateModel(
                List.of("A", "B", "C", "D"), List.of("A", "B", "C", "D"), 4, 4);
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void parseAndUpdateRoundTrip() {
        String snapshot = "DynamicArray{size=3, capacity=8, elements=[5, 10, 15], raw=[5, 10, 15, null, null, null, null, null]}";
        DynamicArrayStateModel model = StateModelParser.parseDynamicArray(snapshot);
        assertEquals(3, model.size());
        assertEquals(8, model.capacity());
        assertEquals(List.of("5", "10", "15"), model.elements());
        assertFalse(model.isEmpty());
        assertTrue(model.hasReservedSpace());
        assertEquals(5, model.unusedSlots());

        DynamicArrayVisualPane pane = new DynamicArrayVisualPane();
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void hasReservedSpaceWhenCapacityExceedsSize() {
        DynamicArrayStateModel model = new DynamicArrayStateModel(
                List.of("1", "2"), List.of("1", "2", "null", "null"), 2, 4);
        assertTrue(model.hasReservedSpace());
        assertEquals(2, model.unusedSlots());
    }

    @Test
    void noReservedSpaceWhenFull() {
        DynamicArrayStateModel model = new DynamicArrayStateModel(
                List.of("1", "2"), List.of("1", "2"), 2, 2);
        assertFalse(model.hasReservedSpace());
        assertEquals(0, model.unusedSlots());
    }

    @Test
    void emptyModelReportsIsEmpty() {
        DynamicArrayStateModel model = new DynamicArrayStateModel(
                List.of(), List.of("null", "null", "null", "null"), 0, 4);
        assertTrue(model.isEmpty());
    }
}
