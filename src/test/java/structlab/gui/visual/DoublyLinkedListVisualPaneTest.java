package structlab.gui.visual;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(JavaFxToolkitExtension.class)
@Timeout(10)
class DoublyLinkedListVisualPaneTest {

    @Test
    void constructionDoesNotThrow() {
        assertDoesNotThrow(DoublyLinkedListVisualPane::new);
    }

    @Test
    void updateWithEmptyModel() {
        var pane = new DoublyLinkedListVisualPane();
        var model = new DoublyLinkedListStateModel(List.of(), 0, "null", "null");
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void updateWithPopulatedModel() {
        var pane = new DoublyLinkedListVisualPane();
        var model = new DoublyLinkedListStateModel(List.of("X", "Y", "Z"), 3, "X", "Z");
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void updateCanBeCalledRepeatedly() {
        var pane = new DoublyLinkedListVisualPane();
        var empty = new DoublyLinkedListStateModel(List.of(), 0, "null", "null");
        var populated = new DoublyLinkedListStateModel(List.of("1", "2"), 2, "1", "2");
        assertDoesNotThrow(() -> {
            pane.update(populated);
            pane.update(empty);
            pane.update(populated);
        });
    }
}
