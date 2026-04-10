package structlab.gui.visual;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DoublyLinkedListStateModelTest {

    @Test
    void emptyModel() {
        var model = new DoublyLinkedListStateModel(List.of(), 0, "null", "null");
        assertTrue(model.isEmpty());
    }

    @Test
    void populatedModel() {
        var model = new DoublyLinkedListStateModel(List.of("X", "Y"), 2, "X", "Y");
        assertFalse(model.isEmpty());
        assertEquals("X", model.head());
        assertEquals("Y", model.tail());
    }
}
