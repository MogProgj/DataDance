package structlab.gui.visual;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(JavaFxToolkitExtension.class)
@Timeout(10)
class HashChainingVisualPaneTest {

    @Test
    void constructionDoesNotThrow() {
        assertDoesNotThrow(HashChainingVisualPane::new);
    }

    @Test
    void updateWithEmptyModel() {
        var pane = new HashChainingVisualPane();
        var model = new HashChainingStateModel(0, 4, "DIVISION", 0, 0, List.of(
                new HashChainingStateModel.Bucket(0, List.of()),
                new HashChainingStateModel.Bucket(1, List.of()),
                new HashChainingStateModel.Bucket(2, List.of()),
                new HashChainingStateModel.Bucket(3, List.of())));
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void updateWithPopulatedModel() {
        var pane = new HashChainingVisualPane();
        var model = new HashChainingStateModel(2, 8, "DIVISION", 2, 0, List.of(
                new HashChainingStateModel.Bucket(0, List.of()),
                new HashChainingStateModel.Bucket(1, List.of(
                        new HashChainingStateModel.Entry("9", "900"),
                        new HashChainingStateModel.Entry("1", "100"))),
                new HashChainingStateModel.Bucket(2, List.of(
                        new HashChainingStateModel.Entry("2", "200")))));
        assertDoesNotThrow(() -> pane.update(model));
    }

    @Test
    void updateCanBeCalledRepeatedly() {
        var pane = new HashChainingVisualPane();
        var empty = new HashChainingStateModel(0, 4, "", 0, 0, List.of());
        var populated = new HashChainingStateModel(1, 4, "DIVISION", 1, 0, List.of(
                new HashChainingStateModel.Bucket(0, List.of(
                        new HashChainingStateModel.Entry("k", "v")))));
        assertDoesNotThrow(() -> {
            pane.update(populated);
            pane.update(empty);
            pane.update(populated);
        });
    }
}
