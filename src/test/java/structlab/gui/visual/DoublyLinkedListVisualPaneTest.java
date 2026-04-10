package structlab.gui.visual;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DoublyLinkedListVisualPaneTest {

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException | UnsupportedOperationException ignored) {}
    }

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
