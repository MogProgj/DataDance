package structlab.gui.visual;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LinkedDequeVisualPaneTest {

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException | UnsupportedOperationException ignored) {}
    }

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
