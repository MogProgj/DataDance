package structlab.gui.visual;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(JavaFxToolkitExtension.class)
@Timeout(10)
class ArrayDequeVisualPaneTest {

    @Test
    void constructionDoesNotThrow() {
        assertDoesNotThrow(ArrayDequeVisualPane::new);
    }

    @Test
    void updateWithEmptyModel() {
        var pane = new ArrayDequeVisualPane();
        var model = new ArrayDequeStateModel(
                List.of(), List.of("null", "null", "null", "null"), 0, 4, 0);
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void updateWithPopulatedModel() {
        var pane = new ArrayDequeVisualPane();
        var model = new ArrayDequeStateModel(
                List.of("10", "20", "30"),
                List.of("null", "10", "20", "30", "null", "null", "null", "null"),
                3, 8, 1);
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void updateCanBeCalledRepeatedly() {
        var pane = new ArrayDequeVisualPane();
        var empty = new ArrayDequeStateModel(List.of(), List.of("null", "null"), 0, 2, 0);
        var populated = new ArrayDequeStateModel(List.of("A"), List.of("A", "null"), 1, 2, 0);
        assertDoesNotThrow(() -> {
            pane.update(populated);
            pane.update(empty);
            pane.update(populated);
        });
    }
}
