package structlab.gui.visual;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComparisonSummaryPaneTest {

    @BeforeAll
    static void initToolkit() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException | UnsupportedOperationException ignored) {
            // toolkit already initialised
        }
    }

    @Test
    void constructionDoesNotThrow() {
        assertDoesNotThrow(ComparisonSummaryPane::new);
    }

    @Test
    void updateIdleDoesNotThrow() {
        ComparisonSummaryPane pane = new ComparisonSummaryPane();
        assertDoesNotThrow(pane::updateIdle);
    }

    @Test
    void updateSessionSetsLabels() {
        ComparisonSummaryPane pane = new ComparisonSummaryPane();
        assertDoesNotThrow(() -> pane.updateSession("Stack", 3));
    }

    @Test
    void updateAfterOperationWithSuccess() {
        ComparisonSummaryPane pane = new ComparisonSummaryPane();
        pane.updateSession("Stack", 2);
        assertDoesNotThrow(() -> pane.updateAfterOperation(5, true));
    }

    @Test
    void updateAfterOperationWithFailure() {
        ComparisonSummaryPane pane = new ComparisonSummaryPane();
        pane.updateSession("Queue", 2);
        assertDoesNotThrow(() -> pane.updateAfterOperation(3, false));
    }
}
