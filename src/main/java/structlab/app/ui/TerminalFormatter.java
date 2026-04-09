package structlab.app.ui;

public class TerminalFormatter {

    public static String boxText(String title, String content, String color) {
        String safeContent = content == null ? "" : content;
        String[] lines = safeContent.split("\n");
        int maxLen = title.length() + 2;
        for (String line : lines) {
            int len = line.replaceAll("\u001B\\[[;\\d]*m", "").length();
            if (len > maxLen) maxLen = len;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(color).append(TerminalTheme.BOX_TOP_LEFT)
          .append(TerminalTheme.BOX_HORIZONTAL).append(" ").append(title).append(" ");
        int dashCount = maxLen - title.length() - 1;
        if (dashCount > 0) {
            sb.append(TerminalTheme.BOX_HORIZONTAL.repeat(dashCount));
        }
        sb.append(TerminalTheme.BOX_TOP_RIGHT).append(TerminalTheme.RESET).append("\n");

        for (String line : lines) {
            int realLen = line.replaceAll("\u001B\\[[;\\d]*m", "").length();
            int pad = maxLen - realLen;
            sb.append(color).append(TerminalTheme.BOX_VERTICAL).append(TerminalTheme.RESET)
              .append(" ").append(line);
            if (pad > 0) {
                sb.append(" ".repeat(pad));
            }
            sb.append(" ").append(color).append(TerminalTheme.BOX_VERTICAL).append(TerminalTheme.RESET).append("\n");
        }

        sb.append(color).append(TerminalTheme.BOX_BOTTOM_LEFT)
          .append(TerminalTheme.BOX_HORIZONTAL.repeat(maxLen + 2))
          .append(TerminalTheme.BOX_BOTTOM_RIGHT).append(TerminalTheme.RESET).append("\n");

        return sb.toString();
    }

    public static String successBox(String title, String content) {
        return boxText(TerminalTheme.CHECK + " " + title, content, TerminalTheme.GREEN);
    }

    public static String errorBox(String title, String content) {
        return boxText(TerminalTheme.CROSS + " " + title, TerminalTheme.RED + content + TerminalTheme.RESET, TerminalTheme.RED);
    }

    public static String infoBox(String title, String content) {
        return boxText(TerminalTheme.INFO + " " + title, content, TerminalTheme.CYAN);
    }

    public static String keyValueBlock(String title, java.util.Map<String, String> values) {
        StringBuilder sb = new StringBuilder();
        int maxKeyLen = values.keySet().stream().mapToInt(String::length).max().orElse(0);
        for (java.util.Map.Entry<String, String> entry : values.entrySet()) {
            sb.append(String.format("%-" + maxKeyLen + "s : %s\n", entry.getKey(), entry.getValue()));
        }
        return boxText(title, sb.toString().trim(), TerminalTheme.BLUE);
    }

    public static String emptyStateBox(String title, String content, String hint) {
        String body = TerminalTheme.GRAY + content + "\n" + TerminalTheme.YELLOW + hint + TerminalTheme.RESET;
        return boxText(title, body, TerminalTheme.GRAY);
    }

    public static void printError(String message) {
        System.out.println(errorBox("ERROR", message));
    }

    public static void printInfo(String message) {
        System.out.println(TerminalTheme.CYAN + TerminalTheme.INFO + " " + message + TerminalTheme.RESET);
    }

    public static void printSuccess(String message) {
        System.out.println(TerminalTheme.GREEN + TerminalTheme.CHECK + " " + message + TerminalTheme.RESET);
    }
}
