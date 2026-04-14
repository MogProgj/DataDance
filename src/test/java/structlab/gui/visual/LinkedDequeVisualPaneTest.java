package structlab.gui.visual;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(JavaFxToolkitExtension.class)
@Timeout(10)
class LinkedDequeVisualPaneTest {

    @Test
    void constructionDoesNotThrow() {
        assertDoesNotThrow(LinkedDequeVisualPane::new);
    }

    @Test
    void updateWithEmptyModel() {
        var pane = new LinkedDequeVisualPane();
        var model = new LinkedDequeStateModel(List.of(), 0, "null", "null");
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void updateWithPopulatedModel() {
        var pane = new LinkedDequeVisualPane();
        var model = new LinkedDequeStateModel(List.of("A", "B", "C"), 3, "A", "C");
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void updateCanBeCalledRepeatedly() {
        var pane = new LinkedDequeVisualPane();
        var empty = new LinkedDequeStateModel(List.of(), 0, "null", "null");
        var populated = new LinkedDequeStateModel(List.of("1"), 1, "1", "1");
        assertDoesNotThrow(() -> {
            pane.update(populated);
            pane.update(empty);
            pane.update(populated);
        });
    }
}
