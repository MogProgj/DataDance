package structlab.app.command;

public record CommandResult(
    boolean success,
    String title,
    String body,
    String hint,
    boolean exitRequested
) {
    public static CommandResult ok() {
        return new CommandResult(true, null, null, null, false);
    }

    public static CommandResult ok(String body) {
        return new CommandResult(true, null, body, null, false);
    }

    public static CommandResult success(String title, String body) {
        return new CommandResult(true, title, body, null, false);
    }

    public static CommandResult success(String title, String body, String hint) {
        return new CommandResult(true, title, body, hint, false);
    }

    public static CommandResult error(String error) {
        return new CommandResult(false, "Error", error, null, false);
    }

    public static CommandResult error(String title, String error) {
        return new CommandResult(false, title, error, null, false);
    }

    public static CommandResult error(String title, String error, String hint) {
        return new CommandResult(false, title, error, hint, false);
    }

    public static CommandResult exit() {
        return new CommandResult(true, null, null, null, true);
    }

    public String message() {
        return body != null ? body : "";
    }
}
