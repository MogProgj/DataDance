package structlab.app.runtime;

import java.util.List;

public record OperationDescriptor(
    String name,
    List<String> aliases,
    String description,
    int argCount,
    String usage,
    boolean mutates,
    String example,
    String complexityNote
) {}
