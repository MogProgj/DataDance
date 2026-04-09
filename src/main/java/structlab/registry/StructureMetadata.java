package structlab.registry;

import java.util.Set;

/**
 * Metadata defining the abstract concept of a data structure.
 */
public record StructureMetadata(
        String id,
        String name,
        String category,
        Set<String> keywords,
        String description,
        String behavior,
        String learningNotes
) {}
