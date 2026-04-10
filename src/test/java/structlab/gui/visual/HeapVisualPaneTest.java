package structlab.gui.visual;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HeapVisualPaneTest {

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException ignored) {
            // toolkit already initialised
        }
    }

    @Test
    void constructionDoesNotThrow() {
        assertDoesNotThrow(HeapVisualPane::new);
    }

    @Test
    void updateWithEmptyModel() {
        HeapVisualPane pane = new HeapVisualPane();
        HeapStateModel model = new HeapStateModel(List.of(), 0, "null", 4);
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void updateWithSingleElement() {
        HeapVisualPane pane = new HeapVisualPane();
        HeapStateModel model = new HeapStateModel(List.of("42"), 1, "42", 4);
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void updateWithMultipleElements() {
        HeapVisualPane pane = new HeapVisualPane();
        HeapStateModel model = new HeapStateModel(
                List.of("5", "10", "15", "20", "25"), 5, "5", 8);
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void updateWithFullThreeLevelHeap() {
        HeapVisualPane pane = new HeapVisualPane();
        HeapStateModel model = new HeapStateModel(
                List.of("1", "3", "2", "7", "5", "4", "6"), 7, "1", 8);
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void updateCanBeCalledRepeatedly() {
        HeapVisualPane pane = new HeapVisualPane();
        HeapStateModel model1 = new HeapStateModel(List.of("5"), 1, "5", 4);
        HeapStateModel model2 = new HeapStateModel(List.of("3", "5"), 2, "3", 4);
        HeapStateModel empty = new HeapStateModel(List.of(), 0, "null", 4);
        assertDoesNotThrow(() -> {
            pane.update(model1);
            pane.update(model2);
            pane.update(empty);
        });
    }
}
