package structlab.app.ui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TerminalFormatterTest {

    @Test
    public void testBoxTextShouldContainTitleAndContent() {
        String result = TerminalFormatter.boxText("Test Title", "Line 1\nLine 2", TerminalTheme.BLUE);

        assertNotNull(result);
        assertTrue(result.contains("Test Title"), "Should contain the title");
        assertTrue(result.contains("Line 1"), "Should contain Line 1");
        assertTrue(result.contains("Line 2"), "Should contain Line 2");
    }

    @Test
    public void testBoxTextWithANSIEscapeCodes() {
        String contentWithAnsi = "\u001B[31mRed Code\u001B[0m";
        String result = TerminalFormatter.boxText("Color Mode", contentWithAnsi, TerminalTheme.GREEN);

        assertTrue(result.contains("\u001B[31mRed Code\u001B[0m"), "Should preserve internal ANSI codes");
    }
}
