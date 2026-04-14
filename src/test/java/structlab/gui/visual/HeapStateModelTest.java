package structlab.gui.visual;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HeapStateModelTest {

    @Test
    void emptyHeapModel() {
        HeapStateModel model = new HeapStateModel(List.of(), 0, "null", 4);
        assertTrue(model.isEmpty());
        assertEquals(0, model.levels());
    }

    @Test
    void singleElementHeap() {
        HeapStateModel model = new HeapStateModel(List.of("5"), 1, "5", 4);
        assertFalse(model.isEmpty());
        assertEquals(1, model.levels());
        assertEquals("5", model.minValue());
    }

    @Test
    void threeElementHeapHasTwoLevels() {
        HeapStateModel model = new HeapStateModel(List.of("5", "10", "15"), 3, "5", 4);
        assertEquals(2, model.levels());
    }

    @Test
    void sevenElementHeapHasThreeLevels() {
        HeapStateModel model = new HeapStateModel(
                List.of("1", "3", "2", "7", "5", "4", "6"), 7, "1", 8);
        assertEquals(3, model.levels());
    }

    @Test
    void levelStartAndCapacity() {
        assertEquals(0, HeapStateModel.levelStart(0));
        assertEquals(1, HeapStateModel.levelCapacity(0));

        assertEquals(1, HeapStateModel.levelStart(1));
        assertEquals(2, HeapStateModel.levelCapacity(1));

        assertEquals(3, HeapStateModel.levelStart(2));
        assertEquals(4, HeapStateModel.levelCapacity(2));

        assertEquals(7, HeapStateModel.levelStart(3));
        assertEquals(8, HeapStateModel.levelCapacity(3));
    }
}
