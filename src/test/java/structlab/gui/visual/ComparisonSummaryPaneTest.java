package structlab.gui.visual;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(JavaFxToolkitExtension.class)
@Timeout(10)
class ComparisonSummaryPaneTest {

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
        assertDoesNotThrow(() -> pane.updateAfterOperation(5, "MATCHING", "comparison-status-ok", "12.0 μs"));
    }

    @Test
    void updateAfterOperationWithFailure() {
        ComparisonSummaryPane pane = new ComparisonSummaryPane();
        pane.updateSession("Queue", 2);
        assertDoesNotThrow(() -> pane.updateAfterOperation(3, "PARTIAL FAIL", "comparison-status-fail", "5.0 μs – 20.0 μs"));
    }
}
