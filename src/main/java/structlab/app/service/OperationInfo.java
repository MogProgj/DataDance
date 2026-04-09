package structlab.app.service;

import java.util.List;

public record OperationInfo(
    String name,
    List<String> aliases,
    String description,
    int argCount,
    String usage,
    boolean mutates,
    String complexityNote
) {}
