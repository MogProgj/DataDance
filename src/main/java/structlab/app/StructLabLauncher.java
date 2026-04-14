package structlab.app;

import javafx.application.Application;
import structlab.gui.StructLabFxApp;

/**
 * Unified entry point for StructLab.
 *
 * <ul>
 *   <li>{@code java -jar structlab.jar} — launches the GUI (default)</li>
 *   <li>{@code java -jar structlab.jar --terminal} — launches the terminal REPL</li>
 *   <li>{@code java -jar structlab.jar --help} — prints usage</li>
 * </ul>
 *
 * <p>This class is intentionally <strong>not</strong> a subclass of
 * {@link Application} so that the shaded JAR can use it as the manifest
 * main class without triggering the JavaFX module-path check that
 * rejects direct {@code Application} subclasses in an uber-JAR.</p>
 */
public final class StructLabLauncher {

    private StructLabLauncher() {}

    public static void main(String[] args) {
        Mode mode = parseMode(args);
        switch (mode) {
            case HELP     -> printUsage();
            case TERMINAL -> StructLabApp.main(args);
            case GUI      -> Application.launch(StructLabFxApp.class, args);
        }
    }

    static Mode parseMode(String[] args) {
        if (args == null || args.length == 0) return Mode.GUI;
        for (String arg : args) {
            if ("--help".equalsIgnoreCase(arg) || "-h".equalsIgnoreCase(arg)) {
                return Mode.HELP;
            }
            if ("--terminal".equalsIgnoreCase(arg) || "-t".equalsIgnoreCase(arg)) {
                return Mode.TERMINAL;
            }
        }
        return Mode.GUI;
    }

    private static void printUsage() {
        System.out.println("""
                StructLab — Data Structure Simulator

                Usage:
                  java -jar structlab.jar            Launch the GUI (default)
                  java -jar structlab.jar --terminal  Launch the terminal REPL
                  java -jar structlab.jar --help      Show this message

                Options:
                  --terminal, -t   Start the interactive terminal simulator
                  --help, -h       Print usage information and exit

                Learn more: https://github.com/your-org/DataDance""");
    }

    enum Mode { GUI, TERMINAL, HELP }
}
