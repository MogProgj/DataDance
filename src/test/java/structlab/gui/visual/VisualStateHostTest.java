package structlab.gui.visual;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(JavaFxToolkitExtension.class)
@Timeout(10)
class VisualStateHostTest {

    @Test
    void constructionShowsFallback() {
        VisualStateHost host = new VisualStateHost("No session.");
        assertFalse(host.isShowingVisual());
        assertEquals("No session.", host.getFallbackArea().getPromptText());
    }

    @Test
    void renderWithSupportedSnapshotShowsVisual() {
        VisualStateHost host = new VisualStateHost("placeholder");
        String snap = "ArrayStack{size=2, top=20, elements=DynamicArray{size=2, capacity=4, elements=[10, 20], raw=[10, 20, null, null]}}";
        host.render(snap, "text fallback");
        assertTrue(host.isShowingVisual());
    }

    @Test
    void renderWithUnsupportedSnapshotShowsText() {
        VisualStateHost host = new VisualStateHost("placeholder");
        host.render("UnknownType{data=abc}", "rendered text");
        assertFalse(host.isShowingVisual());
        assertEquals("rendered text", host.getFallbackArea().getText());
    }

    @Test
    void renderWithNullSnapshotShowsText() {
        VisualStateHost host = new VisualStateHost("placeholder");
        host.render(null, "fallback text");
        assertFalse(host.isShowingVisual());
        assertEquals("fallback text", host.getFallbackArea().getText());
    }

    @Test
    void showPlaceholderResetsFallback() {
        VisualStateHost host = new VisualStateHost("Original placeholder");
        host.render(null, "some text");
        host.showPlaceholder();
        assertFalse(host.isShowingVisual());
        assertEquals("", host.getFallbackArea().getText());
        assertEquals("Original placeholder", host.getFallbackArea().getPromptText());
    }

    @Test
    void clearEmptiesFallback() {
        VisualStateHost host = new VisualStateHost("placeholder");
        host.render(null, "data");
        host.clear();
        assertEquals("", host.getFallbackArea().getText());
    }

    @Test
    void resetCacheDoesNotThrow() {
        VisualStateHost host = new VisualStateHost("placeholder");
        assertDoesNotThrow(host::resetCache);
    }

    @Test
    void renderWithMaxScrollHeight() {
        VisualStateHost host = new VisualStateHost("placeholder");
        String snap = "ArrayStack{size=1, top=10, elements=DynamicArray{size=1, capacity=2, elements=[10], raw=[10, null]}}";
        host.render(snap, "text", 200);
        assertTrue(host.isShowingVisual());
    }

    @Test
    void renderLinkedQueueShowsVisual() {
        VisualStateHost host = new VisualStateHost("placeholder");
        String snap = "LinkedQueue{size=2, front=5, rear=10, chain=[5 -> 10]}";
        host.render(snap, "fallback");
        assertTrue(host.isShowingVisual());
    }

    @Test
    void getFallbackAreaAllowsCustomization() {
        VisualStateHost host = new VisualStateHost("placeholder");
        host.getFallbackArea().getStyleClass().add("custom-style");
        assertTrue(host.getFallbackArea().getStyleClass().contains("custom-style"));
    }
}
