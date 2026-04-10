package structlab.gui.visual;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LinkedDequeStateModelTest {

    @Test
    void emptyModel() {
        var model = new LinkedDequeStateModel(List.of(), 0, "null", "null");
        assertTrue(model.isEmpty());
    }

    @Test
    void populatedModel() {
        var model = new LinkedDequeStateModel(List.of("1", "2", "3"), 3, "1", "3");
        assertFalse(model.isEmpty());
        assertEquals("1", model.front());
        assertEquals("3", model.rear());
    }
}
