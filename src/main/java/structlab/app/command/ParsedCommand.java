package structlab.app.command;

import java.util.List;

public record ParsedCommand(
    String raw,
    String name,
    List<String> arguments
) {
    public boolean hasArgs() {
        return arguments != null && !arguments.isEmpty();
    }
}
