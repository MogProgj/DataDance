package structlab.core.graph;

/**
 * Stable identity for every graph algorithm supported by the workbench.
 * Used as the single source of truth instead of display-string matching.
 */
public enum GraphAlgorithmId {
    BFS,
    DFS,
    DIJKSTRA,
    BELLMAN_FORD,
    TOPOLOGICAL_SORT,
    A_STAR,
    PRIM,
    KRUSKAL,
    SCC,
    BRIDGES,
    ARTICULATION_POINTS
}
