package structlab.gui;

import java.util.regex.Pattern;

/**
 * Utility methods for preparing text for JavaFX GUI display.
 * Strips ANSI escape codes and provides plain-text formatting helpers.
 */
public final class GuiTextUtils {

    private static final Pattern ANSI_PATTERN = Pattern.compile("\u001B\\[[;\\d]*m");

    private GuiTextUtils() {}

    /**
     * Strips all ANSI escape sequences from the given text.
     */
    public static String stripAnsi(String text) {
        if (text == null) return "";
        return ANSI_PATTERN.matcher(text).replaceAll("");
    }
}
