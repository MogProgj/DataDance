package structlab.gui.visual;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(JavaFxToolkitExtension.class)
@Timeout(10)
class SinglyLinkedListVisualPaneTest {

    @Test
    void constructionDoesNotThrow() {
        assertDoesNotThrow(SinglyLinkedListVisualPane::new);
    }

    @Test
    void updateWithEmptyModel() {
        var pane = new SinglyLinkedListVisualPane();
        var model = new SinglyLinkedListStateModel(List.of(), 0, "null", "null");
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void updateWithPopulatedModel() {
        var pane = new SinglyLinkedListVisualPane();
        var model = new SinglyLinkedListStateModel(List.of("10", "20", "30"), 3, "10", "30");
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void updateCanBeCalledRepeatedly() {
        var pane = new SinglyLinkedListVisualPane();
        var empty = new SinglyLinkedListStateModel(List.of(), 0, "null", "null");
        var populated = new SinglyLinkedListStateModel(List.of("A"), 1, "A", "A");
        assertDoesNotThrow(() -> {
            pane.update(populated);
            pane.update(empty);
            pane.update(populated);
        });
    }
}
