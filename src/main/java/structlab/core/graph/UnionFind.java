package structlab.core.graph;

import java.util.*;

/**
 * Union-Find (Disjoint Set Union) with path compression and union by rank.
 * Used by Kruskal's MST algorithm for efficient cycle detection.
 */
public class UnionFind {

    private final Map<String, String> parent = new LinkedHashMap<>();
    private final Map<String, Integer> rank = new LinkedHashMap<>();
    private int componentCount;

    /** Creates a Union-Find with the given elements, each in its own set. */
    public UnionFind(Collection<String> elements) {
        for (String e : elements) {
            parent.put(e, e);
            rank.put(e, 0);
        }
        this.componentCount = elements.size();
    }

    /** Finds the representative of the set containing {@code x} (with path compression). */
    public String find(String x) {
        String root = x;
        while (!root.equals(parent.get(root))) {
            root = parent.get(root);
        }
        // Path compression
        String current = x;
        while (!current.equals(root)) {
            String next = parent.get(current);
            parent.put(current, root);
            current = next;
        }
        return root;
    }

    /**
     * Merges the sets containing {@code a} and {@code b}.
     *
     * @return {@code true} if the sets were different (merge performed),
     *         {@code false} if they were already in the same set (cycle detected)
     */
    public boolean union(String a, String b) {
        String rootA = find(a);
        String rootB = find(b);
        if (rootA.equals(rootB)) return false;

        int rankA = rank.get(rootA);
        int rankB = rank.get(rootB);
        if (rankA < rankB) {
            parent.put(rootA, rootB);
        } else if (rankA > rankB) {
            parent.put(rootB, rootA);
        } else {
            parent.put(rootB, rootA);
            rank.put(rootA, rankA + 1);
        }
        componentCount--;
        return true;
    }

    /** Returns true if {@code a} and {@code b} are in the same set. */
    public boolean connected(String a, String b) {
        return find(a).equals(find(b));
    }

    /** Returns the number of disjoint components. */
    public int componentCount() {
        return componentCount;
    }
}
