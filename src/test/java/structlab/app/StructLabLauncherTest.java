package structlab.app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StructLabLauncherTest {

    @Test
    void noArgDefaultsToGui() {
        assertEquals(StructLabLauncher.Mode.GUI, StructLabLauncher.parseMode(new String[]{}));
    }

    @Test
    void nullArgsDefaultsToGui() {
        assertEquals(StructLabLauncher.Mode.GUI, StructLabLauncher.parseMode(null));
    }

    @Test
    void terminalLongFlag() {
        assertEquals(StructLabLauncher.Mode.TERMINAL,
                StructLabLauncher.parseMode(new String[]{"--terminal"}));
    }

    @Test
    void terminalShortFlag() {
        assertEquals(StructLabLauncher.Mode.TERMINAL,
                StructLabLauncher.parseMode(new String[]{"-t"}));
    }

    @Test
    void terminalFlagCaseInsensitive() {
        assertEquals(StructLabLauncher.Mode.TERMINAL,
                StructLabLauncher.parseMode(new String[]{"--TERMINAL"}));
    }

    @Test
    void helpLongFlag() {
        assertEquals(StructLabLauncher.Mode.HELP,
                StructLabLauncher.parseMode(new String[]{"--help"}));
    }

    @Test
    void helpShortFlag() {
        assertEquals(StructLabLauncher.Mode.HELP,
                StructLabLauncher.parseMode(new String[]{"-h"}));
    }

    @Test
    void helpFlagCaseInsensitive() {
        assertEquals(StructLabLauncher.Mode.HELP,
                StructLabLauncher.parseMode(new String[]{"--HELP"}));
    }

    @Test
    void helpTakesPrecedenceOverTerminal() {
        assertEquals(StructLabLauncher.Mode.HELP,
                StructLabLauncher.parseMode(new String[]{"--help", "--terminal"}));
    }

    @Test
    void unknownArgsDefaultToGui() {
        assertEquals(StructLabLauncher.Mode.GUI,
                StructLabLauncher.parseMode(new String[]{"--verbose", "foo"}));
    }
}
