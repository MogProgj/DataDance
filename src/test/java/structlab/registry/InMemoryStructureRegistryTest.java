package structlab.registry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryStructureRegistryTest {

    private InMemoryStructureRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new InMemoryStructureRegistry();
        RegistrySeeder.seed(registry);
    }

    @Test
    void testSearchByExactKeyword() {
        List<StructureMetadata> results = registry.search("LIFO");

        assertFalse(results.isEmpty(), "Should find elements registered under LIFO keyword");
        assertTrue(results.stream().anyMatch(m -> m.id().equals("struct-stack")), "LIFO must return struct-stack");
    }

    @Test
    void testSearchByPartialMatch() {
        // Should find fixed array, dynamic array (since they fall under 'Array' category/name),
        // and may also find others that describe themselves using 'array' (e.g. array-stack, circular array queue -> handled if word appears in parent metadata)

        List<StructureMetadata> results = registry.search("array");

        assertFalse(results.isEmpty(), "Should find elements related to arrays");
        assertTrue(results.stream().anyMatch(m -> m.name().toLowerCase().contains("array")), "Results should contain an 'Array' structure");
    }

    @Test
    void testGetImplementationsForAbstractStructure() {
        List<ImplementationMetadata> stackImpls = registry.getImplementationsFor("struct-stack");

        assertEquals(2, stackImpls.size(), "Should have Array Stack and Linked Stack implementations");
        assertTrue(stackImpls.stream().anyMatch(i -> i.id().equals("impl-array-stack")));
        assertTrue(stackImpls.stream().anyMatch(i -> i.id().equals("impl-linked-stack")));
    }

    @Test
    void testGetStructureById() {
        Optional<StructureMetadata> queue = registry.getStructureById("struct-queue");

        assertTrue(queue.isPresent());
        assertEquals("Queue", queue.get().name());
    }
}
