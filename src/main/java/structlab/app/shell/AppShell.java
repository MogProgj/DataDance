package structlab.app.shell;

import structlab.app.command.CommandContext;
import structlab.app.command.CommandParser;
import structlab.app.command.CommandResult;
import structlab.app.command.CommandRouter;
import structlab.app.command.ParsedCommand;
import structlab.app.command.handlers.DiscoveryCommands;
import structlab.app.session.SessionManager;
import structlab.app.ui.PromptBuilder;
import structlab.registry.StructureRegistry;

import java.util.Scanner;

public class AppShell {

    private final CommandRouter router;
    private final CommandContext context;
    private final SessionManager sessionManager;
    private boolean running = true;

    public AppShell(StructureRegistry registry) {
        this.sessionManager = new SessionManager();
        this.context = new CommandContext(registry, sessionManager);
        this.router = new CommandRouter();

        // Register phase 5 command packages
        DiscoveryCommands.registerAll(this.router);
        structlab.app.command.handlers.SessionCommands.registerAll(this.router);
    }

    public void run() {
        System.out.println("\nType 'help' to see available commands or 'quit' to exit.\n");

        try (Scanner scanner = new Scanner(System.in)) {
            while (running) {
                System.out.print(PromptBuilder.build(sessionManager));
                String input = scanner.nextLine();

                ParsedCommand cmd = CommandParser.parse(input);
                CommandResult result = router.handle(context, cmd);

                // Print the result properly
                if (result.title() != null && !result.success()) {
                    if (result.hint() != null) {
                        System.out.println(structlab.app.ui.TerminalFormatter.errorBox(result.title(), result.body() + "\n" + structlab.app.ui.TerminalTheme.YELLOW + result.hint()));
                    } else {
                        System.out.println(structlab.app.ui.TerminalFormatter.errorBox(result.title(), result.body()));
                    }
                } else if (result.title() != null && result.success()) {
                    if (result.body() != null && !result.body().isEmpty()) {
                        System.out.println(structlab.app.ui.TerminalFormatter.successBox(result.title(), result.body()));
                    }
                } else if (result.body() != null && !result.body().isEmpty()) {
                     System.out.println(result.body());
                }

                if (result.exitRequested()) {
                    running = false;
                }
            }
        }
        System.out.println("Goodbye!");
    }
}
