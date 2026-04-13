package structlab.app.service;

import java.util.*;

/**
 * A scannable complexity-comparison table for a structure family.
 * One row per operation, one column per implementation.
 */
public record ComplexityMatrix(
        List<String> implementationNames,
        List<Row> rows,
        Map<String, String> spaceByImplementation
) {

    public record Row(String operation, Map<String, String> byImplementation) {}

    /**
     * Builds a complexity matrix from a list of implementations.
     * The union of all operation names becomes the row set;
     * each cell holds the complexity string or "—" if absent.
     */
    public static ComplexityMatrix build(List<ImplementationSummary> implementations) {
        if (implementations.isEmpty()) {
            return new ComplexityMatrix(List.of(), List.of(), Map.of());
        }

        List<String> implNames = implementations.stream()
                .map(ImplementationSummary::name)
                .toList();

        // Collect union of all operation names (preserving insertion order)
        Set<String> allOps = new LinkedHashSet<>();
        for (ImplementationSummary impl : implementations) {
            if (impl.timeComplexity() != null) {
                allOps.addAll(impl.timeComplexity().keySet());
            }
        }

        List<Row> rows = new ArrayList<>();
        for (String op : allOps) {
            Map<String, String> byImpl = new LinkedHashMap<>();
            for (ImplementationSummary impl : implementations) {
                String complexity = impl.timeComplexity() != null
                        ? impl.timeComplexity().getOrDefault(op, "\u2014")
                        : "\u2014";
                byImpl.put(impl.name(), complexity);
            }
            rows.add(new Row(op, Map.copyOf(byImpl)));
        }

        Map<String, String> space = new LinkedHashMap<>();
        for (ImplementationSummary impl : implementations) {
            space.put(impl.name(),
                    impl.spaceComplexity() != null ? impl.spaceComplexity() : "\u2014");
        }

        return new ComplexityMatrix(
                List.copyOf(implNames),
                List.copyOf(rows),
                Map.copyOf(space));
    }
}
