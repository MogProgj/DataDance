package structlab.app.service;

import java.util.Set;

public record StructureSummary(
    String id,
    String name,
    String category,
    Set<String> keywords,
    String description
) {}
