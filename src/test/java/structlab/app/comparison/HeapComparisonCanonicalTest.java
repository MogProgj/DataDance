package structlab.app.comparison;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import structlab.app.runtime.OperationDescriptor;
import structlab.app.service.StructLabService;

import java.util.List;

class HeapComparisonCanonicalTest {

    @Test
    void heapComparisonFindsAllThreeCanonicalOperations() {
        StructLabService service = StructLabService.createDefault();
        ComparisonSession session = service.openComparisonSession("struct-heap", List.of());

        List<OperationDescriptor> common = session.getCommonOperations();
        List<String> names = common.stream().map(OperationDescriptor::name).toList();

        assertEquals(3, names.size(), "Heap comparison must expose 3 common operations, not just peek");
        assertTrue(names.contains("insert"), "insert must be a common operation");
        assertTrue(names.contains("extractmin"), "extractmin must be a common operation");
        assertTrue(names.contains("peek"), "peek must be a common operation");
    }

    @Test
    void heapComparisonExecuteInsertAcrossAllImplementations() {
        StructLabService service = StructLabService.createDefault();
        ComparisonSession session = service.openComparisonSession("struct-heap", List.of());

        ComparisonOperationResult result = session.executeAll("insert", List.of("42"));
        assertTrue(result.allSucceeded(), "insert should succeed on all heap implementations");
        assertEquals(2, result.entryResults().size());
    }

    @Test
    void heapComparisonExtractMinWorks() {
        StructLabService service = StructLabService.createDefault();
        ComparisonSession session = service.openComparisonSession("struct-heap", List.of());

        session.executeAll("insert", List.of("10"));
        session.executeAll("insert", List.of("3"));
        session.executeAll("insert", List.of("7"));

        ComparisonOperationResult result = session.executeAll("extractmin", List.of());
        assertTrue(result.allSucceeded(), "extractmin should succeed on all heap implementations");

        for (ComparisonEntryResult entry : result.entryResults()) {
            assertEquals("3", entry.returnedValue(), "Both implementations should return the min (3)");
        }
    }
}
