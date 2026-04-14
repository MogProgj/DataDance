package structlab.gui.visual;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(JavaFxToolkitExtension.class)
@Timeout(10)
class HashSetVisualPaneTest {

    @Test
    void constructionDoesNotThrow() {
        assertDoesNotThrow(HashSetVisualPane::new);
    }

    @Test
    void updateWithEmptyModel() {
        var pane = new HashSetVisualPane();
        var model = new HashSetStateModel(0, 8, "DIVISION", 0, 0, List.of(
                new HashSetStateModel.SetBucket(0, List.of()),
                new HashSetStateModel.SetBucket(1, List.of())));
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void updateWithPopulatedModel() {
        var pane = new HashSetVisualPane();
        var model = new HashSetStateModel(2, 8, "DIVISION", 1, 0, List.of(
                new HashSetStateModel.SetBucket(0, List.of()),
                new HashSetStateModel.SetBucket(1, List.of()),
                new HashSetStateModel.SetBucket(2, List.of("2")),
                new HashSetStateModel.SetBucket(3, List.of("3"))));
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void updateCanBeCalledRepeatedly() {
        var pane = new HashSetVisualPane();
        var empty = new HashSetStateModel(0, 4, "", 0, 0, List.of());
        var populated = new HashSetStateModel(1, 4, "DIVISION", 1, 0, List.of(
                new HashSetStateModel.SetBucket(0, List.of("x"))));
        assertDoesNotThrow(() -> {
            pane.update(populated);
            pane.update(empty);
            pane.update(populated);
        });
    }
}
