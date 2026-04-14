package structlab.gui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GuiTextUtilsTest {

    @Test
    void stripAnsiRemovesBoldAndColors() {
        String input = "\u001B[1m\u001B[36m══ Compare: push 42 ══\u001B[0m";
        String result = GuiTextUtils.stripAnsi(input);
        assertEquals("══ Compare: push 42 ══", result);
    }

    @Test
    void stripAnsiPreservesPlainText() {
        String plain = "Hello, World!";
        assertEquals(plain, GuiTextUtils.stripAnsi(plain));
    }

    @Test
    void stripAnsiHandlesNull() {
        assertEquals("", GuiTextUtils.stripAnsi(null));
    }

    @Test
    void stripAnsiHandlesEmptyString() {
        assertEquals("", GuiTextUtils.stripAnsi(""));
    }

    @Test
    void stripAnsiRemovesMultipleSequences() {
        String input = "\u001B[32m✔\u001B[0m [1] Array Stack\u001B[1m\u001B[0m";
        String result = GuiTextUtils.stripAnsi(input);
        assertEquals("✔ [1] Array Stack", result);
    }

    @Test
    void stripAnsiRemovesAllKnownCodes() {
        String input = "\u001B[31mRED\u001B[0m \u001B[32mGREEN\u001B[0m \u001B[33mYELLOW\u001B[0m "
                + "\u001B[34mBLUE\u001B[0m \u001B[35mMAGENTA\u001B[0m \u001B[36mCYAN\u001B[0m "
                + "\u001B[90mGRAY\u001B[0m";
        String result = GuiTextUtils.stripAnsi(input);
        assertEquals("RED GREEN YELLOW BLUE MAGENTA CYAN GRAY", result);
    }

    @Test
    void stripAnsiPreservesUnicodeSymbols() {
        String input = "\u001B[32m✔\u001B[0m ✖ ⚠ ▶ ℹ";
        String result = GuiTextUtils.stripAnsi(input);
        assertEquals("✔ ✖ ⚠ ▶ ℹ", result);
    }
}
