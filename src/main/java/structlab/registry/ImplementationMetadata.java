package structlab.registry;

import java.util.Map;

/**
 * Metadata defining a specific concrete implementation of an abstract data structure.
 */
public record ImplementationMetadata(
        String id,
        String name,
        String parentStructureId,
        String description,
        Map<String, String> timeComplexity,
        String spaceComplexity,
        Class<?> implementationClass
) {}
