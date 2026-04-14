package structlab.gui.visual;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HashSetStateModelTest {

    @Test
    void emptyModel() {
        var model = new HashSetStateModel(0, 8, "DIVISION", 0, 0, List.of(
                new HashSetStateModel.SetBucket(0, List.of()),
                new HashSetStateModel.SetBucket(1, List.of())
        ));
        assertTrue(model.isEmpty());
        assertEquals(0.0, model.loadFactor());
        assertEquals(0, model.occupiedCount());
    }

    @Test
    void populatedModel() {
        var model = new HashSetStateModel(2, 8, "DIVISION", 1, 0, List.of(
                new HashSetStateModel.SetBucket(0, List.of()),
                new HashSetStateModel.SetBucket(1, List.of()),
                new HashSetStateModel.SetBucket(2, List.of("2")),
                new HashSetStateModel.SetBucket(3, List.of("3")),
                new HashSetStateModel.SetBucket(4, List.of()),
                new HashSetStateModel.SetBucket(5, List.of()),
                new HashSetStateModel.SetBucket(6, List.of()),
                new HashSetStateModel.SetBucket(7, List.of())
        ));
        assertFalse(model.isEmpty());
        assertEquals(2.0 / 8, model.loadFactor(), 0.001);
        assertEquals(2, model.occupiedCount());
    }

    @Test
    void setBucketHelpers() {
        var empty = new HashSetStateModel.SetBucket(0, List.of());
        assertTrue(empty.isEmpty());
        assertEquals(0, empty.memberCount());

        var populated = new HashSetStateModel.SetBucket(1, List.of("a", "b"));
        assertFalse(populated.isEmpty());
        assertEquals(2, populated.memberCount());
    }
}
