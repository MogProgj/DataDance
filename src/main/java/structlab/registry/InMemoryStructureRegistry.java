package structlab.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the StructureRegistry.
 */
public class InMemoryStructureRegistry implements StructureRegistry {

    private final Map<String, StructureMetadata> structures = new HashMap<>();
    private final Map<String, List<ImplementationMetadata>> implementations = new HashMap<>();

    @Override
    public List<StructureMetadata> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }

        String lowerKeyword = keyword.toLowerCase();
        return structures.values().stream()
                .filter(meta ->
                    meta.name().toLowerCase().contains(lowerKeyword) ||
                    meta.category().toLowerCase().contains(lowerKeyword) ||
                    (meta.keywords() != null && meta.keywords().stream().anyMatch(k -> k.toLowerCase().contains(lowerKeyword))) ||
                    (meta.description() != null && meta.description().toLowerCase().contains(lowerKeyword))
                )
                .collect(Collectors.toList());
    }

    @Override
    public Optional<StructureMetadata> getStructureById(String id) {
        return Optional.ofNullable(structures.get(id));
    }

    @Override
    public List<ImplementationMetadata> getImplementationsFor(String structureId) {
        return implementations.getOrDefault(structureId, List.of());
    }

    @Override
    public List<StructureMetadata> getAllStructures() {
        return new ArrayList<>(structures.values());
    }

    @Override
    public void registerStructure(StructureMetadata metadata) {
        structures.put(metadata.id(), metadata);
        // Initialize an empty list for its implementations if absent
        implementations.putIfAbsent(metadata.id(), new ArrayList<>());
    }

    @Override
    public void registerImplementation(ImplementationMetadata metadata) {
        implementations.computeIfAbsent(metadata.parentStructureId(), k -> new ArrayList<>())
                       .add(metadata);
    }
}
