package structlab.app.ui;

import structlab.app.comparison.ComparisonSession;
import structlab.app.session.SessionManager;
import structlab.app.session.StructureSession;
import java.util.Optional;

public class PromptBuilder {

    public static String build(SessionManager sessionManager) {
        Optional<StructureSession> session = sessionManager.getActiveSession();

        if (session.isEmpty()) {
            return TerminalTheme.BOLD + TerminalTheme.MAGENTA + "structlab" + TerminalTheme.RESET + "> ";
        }

        if (session.get() instanceof ComparisonSession cs) {
            String struct = cs.getStructureId().replace("struct-", "");
            return TerminalTheme.BOLD + TerminalTheme.MAGENTA + "structlab"
                 + TerminalTheme.GRAY + "[" + TerminalTheme.YELLOW + "compare:" + struct
                 + TerminalTheme.GRAY + "]"
                 + TerminalTheme.RESET + "> ";
        }

        String struct = session.get().getStructureId();
        String impl = session.get().getImplementationId().replace("impl-", "");
        return TerminalTheme.BOLD + TerminalTheme.MAGENTA + "structlab"
             + TerminalTheme.GRAY + "[" + TerminalTheme.CYAN + struct + "/" + impl + TerminalTheme.GRAY + "]"
             + TerminalTheme.RESET + "> ";
    }
}
