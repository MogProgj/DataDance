package structlab.app.service;

public record SessionSnapshot(
    String structureId,
    String implementationId,
    String structureName,
    String implementationName,
    int operationCount
) {}
