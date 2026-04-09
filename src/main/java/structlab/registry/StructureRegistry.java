package structlab.registry;

import java.util.List;
import java.util.Optional;

/**
 * Interface defining the searchable knowledge layer for Data Structures
 * and their respective Implementations.
 */
public interface StructureRegistry {

    /**
     * Search structures by a keyword (e.g., LIFO, array, tree).
     */
    List<StructureMetadata> search(String keyword);

    /**
     * Retrieve the abstract structure metadata by its unique ID.
     */
    Optional<StructureMetadata> getStructureById(String id);

    /**
     * Retrieve all concrete implementations linked to a parent abstract structure.
     */
    List<ImplementationMetadata> getImplementationsFor(String structureId);

    /**
     * Get a list of all recognized data structures.
     */
    List<StructureMetadata> getAllStructures();

    /**
     * Optional: Register a structure metadata.
     */
    void registerStructure(StructureMetadata metadata);

    /**
     * Optional: Register an implementation metadata.
     */
    void registerImplementation(ImplementationMetadata metadata);
}
