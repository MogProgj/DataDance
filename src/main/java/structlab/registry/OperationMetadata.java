package structlab.registry;

/**
 * Metadata describing a specific operation available on a data structure implementation.
 */
public record OperationMetadata(
        String name,
        String description
) {}
