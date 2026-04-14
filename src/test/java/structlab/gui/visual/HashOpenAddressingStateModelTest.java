package structlab.gui.visual;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HashOpenAddressingStateModelTest {

    @Test
    void emptyModel() {
        var model = new HashOpenAddressingStateModel(0, 8, "LINEAR", "DIVISION", 0, List.of(
                new HashOpenAddressingStateModel.Slot(0, HashOpenAddressingStateModel.SlotState.EMPTY, null, null),
                new HashOpenAddressingStateModel.Slot(1, HashOpenAddressingStateModel.SlotState.EMPTY, null, null)
        ));
        assertTrue(model.isEmpty());
        assertEquals(0.0, model.loadFactor());
        assertEquals(0, model.occupiedCount());
        assertEquals(0, model.deletedCount());
        assertEquals(2, model.emptyCount());
        assertEquals(0, model.maxClusterSize());
    }

    @Test
    void populatedModelWithTombstones() {
        var model = new HashOpenAddressingStateModel(2, 8, "LINEAR", "DIVISION", 0, List.of(
                new HashOpenAddressingStateModel.Slot(0, HashOpenAddressingStateModel.SlotState.EMPTY, null, null),
                new HashOpenAddressingStateModel.Slot(1, HashOpenAddressingStateModel.SlotState.OCCUPIED, "1", "10"),
                new HashOpenAddressingStateModel.Slot(2, HashOpenAddressingStateModel.SlotState.OCCUPIED, "2", "20"),
                new HashOpenAddressingStateModel.Slot(3, HashOpenAddressingStateModel.SlotState.DELETED, null, null),
                new HashOpenAddressingStateModel.Slot(4, HashOpenAddressingStateModel.SlotState.EMPTY, null, null),
                new HashOpenAddressingStateModel.Slot(5, HashOpenAddressingStateModel.SlotState.EMPTY, null, null),
                new HashOpenAddressingStateModel.Slot(6, HashOpenAddressingStateModel.SlotState.EMPTY, null, null),
                new HashOpenAddressingStateModel.Slot(7, HashOpenAddressingStateModel.SlotState.EMPTY, null, null)
        ));
        assertFalse(model.isEmpty());
        assertEquals(2.0 / 8, model.loadFactor(), 0.001);
        assertEquals(2, model.occupiedCount());
        assertEquals(1, model.deletedCount());
        assertEquals(5, model.emptyCount());
    }

    @Test
    void maxClusterSizeDetectsContiguousRun() {
        var model = new HashOpenAddressingStateModel(4, 8, "LINEAR", "DIVISION", 0, List.of(
                new HashOpenAddressingStateModel.Slot(0, HashOpenAddressingStateModel.SlotState.EMPTY, null, null),
                new HashOpenAddressingStateModel.Slot(1, HashOpenAddressingStateModel.SlotState.OCCUPIED, "1", "10"),
                new HashOpenAddressingStateModel.Slot(2, HashOpenAddressingStateModel.SlotState.OCCUPIED, "2", "20"),
                new HashOpenAddressingStateModel.Slot(3, HashOpenAddressingStateModel.SlotState.OCCUPIED, "3", "30"),
                new HashOpenAddressingStateModel.Slot(4, HashOpenAddressingStateModel.SlotState.EMPTY, null, null),
                new HashOpenAddressingStateModel.Slot(5, HashOpenAddressingStateModel.SlotState.OCCUPIED, "5", "50"),
                new HashOpenAddressingStateModel.Slot(6, HashOpenAddressingStateModel.SlotState.EMPTY, null, null),
                new HashOpenAddressingStateModel.Slot(7, HashOpenAddressingStateModel.SlotState.EMPTY, null, null)
        ));
        assertEquals(3, model.maxClusterSize());
    }

    @Test
    void slotStateHelpers() {
        var empty = new HashOpenAddressingStateModel.Slot(0, HashOpenAddressingStateModel.SlotState.EMPTY, null, null);
        assertTrue(empty.isEmpty());
        assertFalse(empty.isOccupied());
        assertFalse(empty.isDeleted());

        var occupied = new HashOpenAddressingStateModel.Slot(1, HashOpenAddressingStateModel.SlotState.OCCUPIED, "k", "v");
        assertFalse(occupied.isEmpty());
        assertTrue(occupied.isOccupied());

        var deleted = new HashOpenAddressingStateModel.Slot(2, HashOpenAddressingStateModel.SlotState.DELETED, null, null);
        assertTrue(deleted.isDeleted());
    }
}
