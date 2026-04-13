package structlab.app.service;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ComplexityMatrixTest {

    @Test
    void buildFromEmptyListReturnsEmptyMatrix() {
        ComplexityMatrix matrix = ComplexityMatrix.build(List.of());
        assertTrue(matrix.implementationNames().isEmpty());
        assertTrue(matrix.rows().isEmpty());
        assertTrue(matrix.spaceByImplementation().isEmpty());
    }

    @Test
    void buildFromSingleImplementation() {
        ImplementationSummary impl = new ImplementationSummary(
                "impl-array", "Fixed Array", "struct-array",
                "A fixed-size array",
                Map.of("access", "O(1)", "insert", "O(n)"),
                "O(n)"
        );

        ComplexityMatrix matrix = ComplexityMatrix.build(List.of(impl));

        assertEquals(List.of("Fixed Array"), matrix.implementationNames());
        assertEquals(2, matrix.rows().size());
        assertEquals("O(n)", matrix.spaceByImplementation().get("Fixed Array"));
    }

    @Test
    void buildFromMultipleImplementations() {
        ImplementationSummary array = new ImplementationSummary(
                "impl-fixed", "Fixed Array", "struct-array", "desc",
                Map.of("access", "O(1)", "insert", "O(n)"),
                "O(n)"
        );
        ImplementationSummary dynamic = new ImplementationSummary(
                "impl-dyn", "Dynamic Array", "struct-array", "desc",
                Map.of("access", "O(1)", "insert", "O(1) amortized", "resize", "O(n)"),
                "O(n)"
        );

        ComplexityMatrix matrix = ComplexityMatrix.build(List.of(array, dynamic));

        assertEquals(List.of("Fixed Array", "Dynamic Array"), matrix.implementationNames());
        // Union of operations: access, insert, resize
        assertEquals(3, matrix.rows().size());

        // Check a row that exists in both
        ComplexityMatrix.Row accessRow = matrix.rows().stream()
                .filter(r -> r.operation().equals("access"))
                .findFirst().orElseThrow();
        assertEquals("O(1)", accessRow.byImplementation().get("Fixed Array"));
        assertEquals("O(1)", accessRow.byImplementation().get("Dynamic Array"));

        // Check a row only in the second
        ComplexityMatrix.Row resizeRow = matrix.rows().stream()
                .filter(r -> r.operation().equals("resize"))
                .findFirst().orElseThrow();
        assertEquals("\u2014", resizeRow.byImplementation().get("Fixed Array"));
        assertEquals("O(n)", resizeRow.byImplementation().get("Dynamic Array"));
    }

    @Test
    void buildHandlesNullTimeComplexity() {
        ImplementationSummary impl = new ImplementationSummary(
                "impl-a", "Alpha", "struct-x", "desc", null, "O(1)"
        );

        ComplexityMatrix matrix = ComplexityMatrix.build(List.of(impl));
        assertTrue(matrix.rows().isEmpty());
        assertEquals("O(1)", matrix.spaceByImplementation().get("Alpha"));
    }

    @Test
    void buildHandlesNullSpaceComplexity() {
        ImplementationSummary impl = new ImplementationSummary(
                "impl-a", "Alpha", "struct-x", "desc",
                Map.of("push", "O(1)"), null
        );

        ComplexityMatrix matrix = ComplexityMatrix.build(List.of(impl));
        assertEquals(1, matrix.rows().size());
        assertEquals("\u2014", matrix.spaceByImplementation().get("Alpha"));
    }
}
