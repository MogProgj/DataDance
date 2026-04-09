package structlab.app.command;

import structlab.app.session.SessionManager;
import structlab.registry.StructureRegistry;

public record CommandContext(
    StructureRegistry registry,
    SessionManager sessionManager
) {}
