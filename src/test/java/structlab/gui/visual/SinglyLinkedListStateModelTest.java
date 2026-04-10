package structlab.gui.visual;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SinglyLinkedListStateModelTest {

    @Test
    void emptyModel() {
        var model = new SinglyLinkedListStateModel(List.of(), 0, "null", "null");
        assertTrue(model.isEmpty());
        assertEquals(0, model.size());
    }

    @Test
    void populatedModel() {
        var model = new SinglyLinkedListStateModel(List.of("A", "B", "C"), 3, "A", "C");
        assertFalse(model.isEmpty());
        assertEquals(3, model.size());
        assertEquals("A", model.head());
        assertEquals("C", model.tail());
    }
}
