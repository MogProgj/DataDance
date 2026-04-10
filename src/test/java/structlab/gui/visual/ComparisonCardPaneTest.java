package structlab.gui.visual;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComparisonCardPaneTest {

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
        assertDoesNotThrow(ComparisonCardPane::new);
    }

    @Test
    void updateIdleSetsState() {
        ComparisonCardPane card = new ComparisonCardPane();
        assertDoesNotThrow(() -> card.updateIdle("ArrayStack"));
    }

    @Test
    void updateStateWithSupportedSnapshot() {
        ComparisonCardPane card = new ComparisonCardPane();
        String snap = "ArrayStack{size=2, top=20, elements=DynamicArray{size=2, capacity=4, elements=[10, 20], raw=[10, 20, null, null]}}";
        String rendered = "some rendered text";
        assertDoesNotThrow(() -> card.updateState("ArrayStack", snap, rendered));
    }

    @Test
    void updateStateWithUnsupportedFallsBack() {
        ComparisonCardPane card = new ComparisonCardPane();
        String snap = "SinglyLinkedList{size=2, head=5, chain=[5 -> 10]}";
        String rendered = "SinglyLinkedList rendered text";
        assertDoesNotThrow(() -> card.updateState("SinglyLinkedList", snap, rendered));
    }

    @Test
    void updateResultWithSuccess() {
        ComparisonCardPane card = new ComparisonCardPane();
        String snap = "LinkedStack{size=1, top=42, chain=[42]}";
        String rendered = "rendered";
        assertDoesNotThrow(() -> card.updateResult(
                "LinkedStack", true, "42", snap, rendered, 3, "step1\nstep2\nstep3\n", 1));
    }

    @Test
    void updateResultWithFailure() {
        ComparisonCardPane card = new ComparisonCardPane();
        String snap = "LinkedQueue{size=0, front=null, rear=null, chain=[]}";
        String rendered = "rendered";
        assertDoesNotThrow(() -> card.updateResult(
                "LinkedQueue", false, null, snap, rendered, 0, "Error: queue empty", 1));
    }

    @Test
    void updateResultWithNullSnapshot() {
        // Null snapshot should fall back gracefully
        ComparisonCardPane card = new ComparisonCardPane();
        assertDoesNotThrow(() -> card.updateResult(
                "Some Impl", true, "10", null, "raw text", 1, "trace", 1));
    }
}
