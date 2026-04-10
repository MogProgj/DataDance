package structlab.gui.visual;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArrayDequeStateModelTest {

    @Test
    void emptyModel() {
        var model = new ArrayDequeStateModel(List.of(), List.of("null", "null", "null", "null"), 0, 4, 0);
        assertTrue(model.isEmpty());
        assertEquals(0, model.rearIndex());
    }

    @Test
    void populatedModel() {
        var model = new ArrayDequeStateModel(List.of("1", "2", "3"), List.of("null", "1", "2", "3"), 3, 4, 1);
        assertFalse(model.isEmpty());
        assertEquals(0, model.rearIndex()); // (1+3)%4 = 0
    }

    @Test
    void wrappedModel() {
        var model = new ArrayDequeStateModel(List.of("A", "B"), List.of("B", "null", "null", "A"), 2, 4, 3);
        assertEquals(1, model.rearIndex()); // (3+2)%4 = 1
    }
}
