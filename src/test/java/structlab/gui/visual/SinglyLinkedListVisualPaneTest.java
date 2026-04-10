package structlab.gui.visual;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SinglyLinkedListVisualPaneTest {

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException | UnsupportedOperationException ignored) {}
    }

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
