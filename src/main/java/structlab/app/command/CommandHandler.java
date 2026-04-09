package structlab.app.command;

public interface CommandHandler {
    CommandResult execute(CommandContext context, ParsedCommand command);
    String getDescription();
}
