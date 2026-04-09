package structlab.app.command;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {

    // Matches unquoted words or double-quoted strings
    private static final Pattern ARG_PATTERN = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");

    public static ParsedCommand parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new ParsedCommand(input, "", List.of());
        }

        String trimmed = input.trim();
        Matcher m = ARG_PATTERN.matcher(trimmed);

        List<String> tokens = new ArrayList<>();
        while (m.find()) {
            String token = m.group(1);
            if (token.startsWith("\"") && token.endsWith("\"")) {
                token = token.substring(1, token.length() - 1);
            }
            tokens.add(token);
        }

        String name = tokens.isEmpty() ? "" : tokens.get(0).toLowerCase();
        List<String> args = tokens.size() > 1 ? tokens.subList(1, tokens.size()) : List.of();

        return new ParsedCommand(trimmed, name, args);
    }
}
