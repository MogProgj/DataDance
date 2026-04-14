package structlab.gui.visual;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(JavaFxToolkitExtension.class)
@Timeout(10)
class HashOpenAddressingVisualPaneTest {

    @Test
    void constructionDoesNotThrow() {
        assertDoesNotThrow(HashOpenAddressingVisualPane::new);
    }

    @Test
    void updateWithEmptyModel() {
        var pane = new HashOpenAddressingVisualPane();
        var model = new HashOpenAddressingStateModel(0, 8, "LINEAR", "DIVISION", 0, List.of(
                new HashOpenAddressingStateModel.Slot(0, HashOpenAddressingStateModel.SlotState.EMPTY, null, null),
                new HashOpenAddressingStateModel.Slot(1, HashOpenAddressingStateModel.SlotState.EMPTY, null, null)));
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void updateWithOccupiedAndDeletedSlots() {
        var pane = new HashOpenAddressingVisualPane();
        var model = new HashOpenAddressingStateModel(1, 4, "QUADRATIC", "DIVISION", 0, List.of(
                new HashOpenAddressingStateModel.Slot(0, HashOpenAddressingStateModel.SlotState.EMPTY, null, null),
                new HashOpenAddressingStateModel.Slot(1, HashOpenAddressingStateModel.SlotState.OCCUPIED, "1", "10"),
                new HashOpenAddressingStateModel.Slot(2, HashOpenAddressingStateModel.SlotState.DELETED, null, null),
                new HashOpenAddressingStateModel.Slot(3, HashOpenAddressingStateModel.SlotState.EMPTY, null, null)));
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void updateCanBeCalledRepeatedly() {
        var pane = new HashOpenAddressingVisualPane();
        var empty = new HashOpenAddressingStateModel(0, 4, "LINEAR", "DIVISION", 0, List.of());
        var populated = new HashOpenAddressingStateModel(1, 4, "LINEAR", "DIVISION", 0, List.of(
                new HashOpenAddressingStateModel.Slot(0, HashOpenAddressingStateModel.SlotState.OCCUPIED, "k", "v")));
        assertDoesNotThrow(() -> {
            pane.update(populated);
            pane.update(empty);
            pane.update(populated);
        });
    }
}
