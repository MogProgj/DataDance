package structlab.app.service;

import java.util.Map;

public record ImplementationSummary(
    String id,
    String name,
    String parentStructureId,
    String description,
    Map<String, String> timeComplexity,
    String spaceComplexity
) {}
