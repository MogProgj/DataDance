package structlab.registry;

import java.util.Map;
import java.util.Set;

// Import Phase 2 Trace wrappers to link implementations
import structlab.trace.*;

/**
 * Utility class to seed the registry with Phase 1 and Phase 2
 * structure metadata.
 */
public class RegistrySeeder {

    public static void seed(StructureRegistry registry) {

        // 1. Array
        StructureMetadata arrayMeta = new StructureMetadata(
            "struct-array",
            "Array",
            "Linear",
            Set.of("contiguous", "indexed", "linear", "array"),
            "A collection of elements identified by index or key, stored in contiguous memory.",
            "O(1) random access, O(N) insertion/deletion at arbitrary points.",
            "Arrays are the foundational building block for many other structures."
        );
        registry.registerStructure(arrayMeta);

        registry.registerImplementation(new ImplementationMetadata(
            "impl-fixed-array", "Fixed Array", "struct-array",
            "A basic array with a static size allocated at creation.",
            Map.of("access", "O(1)", "insert", "O(N)"), "O(N)",
            TracedFixedArray.class
        ));
        registry.registerImplementation(new ImplementationMetadata(
            "impl-dynamic-array", "Dynamic Array", "struct-array",
            "An array that automatically resizes (typically doubles) when full.",
            Map.of("access", "O(1)", "insert(end)", "O(1) amortized"), "O(N)",
            TracedDynamicArray.class
        ));

        // 2. Linked List
        StructureMetadata listMeta = new StructureMetadata(
            "struct-list",
            "Linked List",
            "Linear",
            Set.of("node", "pointer", "linear", "chain"),
            "A linear collection of elements, called nodes, each pointing to the next node by means of a pointer.",
            "Elements are not stored at contiguous memory locations.",
            "Efficient O(1) insertions/deletions at known nodes, but O(N) access/search."
        );
        registry.registerStructure(listMeta);

        registry.registerImplementation(new ImplementationMetadata(
            "impl-singly-linked-list", "Singly Linked List", "struct-list",
            "A collection of nodes where each node points to the next.",
            Map.of("access", "O(N)", "insert(head)", "O(1)"), "O(N)",
            TracedSinglyLinkedList.class
        ));
        registry.registerImplementation(new ImplementationMetadata(
            "impl-doubly-linked-list", "Doubly Linked List", "struct-list",
            "A collection of nodes where each node points to both the next and previous node.",
            Map.of("access", "O(N)", "insert(head/tail)", "O(1)"), "O(N)",
            TracedDoublyLinkedList.class
        ));

        // 3. Stack
        StructureMetadata stackMeta = new StructureMetadata(
            "struct-stack",
            "Stack",
            "Linear",
            Set.of("LIFO", "push", "pop", "linear"),
            "A Last-In-First-Out (LIFO) data structure.",
            "Items are added and removed from the same end (top).",
            "Useful for tracking state, undo mechanisms, and parsing expressions."
        );
        registry.registerStructure(stackMeta);

        registry.registerImplementation(new ImplementationMetadata(
            "impl-array-stack", "Array Stack", "struct-stack",
            "Stack backed by a dynamic array.",
            Map.of("push", "O(1) amort", "pop", "O(1)", "peek", "O(1)"), "O(N)",
            TracedArrayStack.class
        ));
        registry.registerImplementation(new ImplementationMetadata(
            "impl-linked-stack", "Linked Stack", "struct-stack",
            "Stack backed by a singly linked list.",
            Map.of("push", "O(1)", "pop", "O(1)", "peek", "O(1)"), "O(N)",
            TracedLinkedStack.class
        ));

        // 3. Queue
        StructureMetadata queueMeta = new StructureMetadata(
            "struct-queue",
            "Queue",
            "Linear",
            Set.of("FIFO", "enqueue", "dequeue", "linear"),
            "A First-In-First-Out (FIFO) data structure.",
            "Items are added to the back and removed from the front.",
            "Useful for scheduling, buffering, and breadth-first search."
        );
        registry.registerStructure(queueMeta);

        registry.registerImplementation(new ImplementationMetadata(
            "impl-circular-array-queue", "Circular Array Queue", "struct-queue",
            "Queue backed by an array that wraps around to prevent shifting elements.",
            Map.of("enqueue", "O(1) amort", "dequeue", "O(1)"), "O(N)",
            TracedCircularArrayQueue.class
        ));
        registry.registerImplementation(new ImplementationMetadata(
            "impl-linked-queue", "Linked Queue", "struct-queue",
            "Queue backed by a singly linked list with head and tail pointers.",
            Map.of("enqueue", "O(1)", "dequeue", "O(1)"), "O(N)",
            TracedLinkedQueue.class
        ));
        registry.registerImplementation(new ImplementationMetadata(
            "impl-two-stack-queue", "Two Stack Queue", "struct-queue",
            "Queue implemented using two stacks (inbox and outbox).",
            Map.of("enqueue", "O(1)", "dequeue", "O(1) amort"), "O(N)",
            TracedTwoStackQueue.class
        ));

        // 4. Deque
        StructureMetadata dequeMeta = new StructureMetadata(
            "struct-deque",
            "Deque",
            "Linear",
            Set.of("double-ended", "queue", "stack", "linear"),
            "A double-ended queue where elements can be added/removed from both ends.",
            "Combines behaviors of stacks and queues.",
            "Useful for sliding window algorithms and full-featured buffers."
        );
        registry.registerStructure(dequeMeta);

        registry.registerImplementation(new ImplementationMetadata(
            "impl-array-deque", "Array Deque", "struct-deque",
            "Deque backed by a circular array.",
            Map.of("addFirst", "O(1) amort", "addLast", "O(1) amort"), "O(N)",
            TracedArrayDequeCustom.class
        ));
        registry.registerImplementation(new ImplementationMetadata(
            "impl-linked-deque", "Linked Deque", "struct-deque",
            "Deque backed by a doubly linked list.",
            Map.of("addFirst", "O(1)", "addLast", "O(1)"), "O(N)",
            TracedLinkedDeque.class
        ));

        // 5. Heap
        StructureMetadata heapMeta = new StructureMetadata(
            "struct-heap",
            "Heap (Priority Queue)",
            "Tree",
            Set.of("priority", "min-heap", "max-heap", "tree"),
            "A specialized tree-based structure satisfying the heap property.",
            "The parent node is always ordered with respect to its children.",
            "Perfect for priority queues and heap sort."
        );
        registry.registerStructure(heapMeta);

        registry.registerImplementation(new ImplementationMetadata(
            "impl-binary-heap", "Binary Heap", "struct-heap",
            "A complete binary tree usually implemented using an array.",
            Map.of("insert", "O(log N)", "extract", "O(log N)", "peek", "O(1)"), "O(N)",
            TracedBinaryHeap.class
        ));
        // There is also TracedHeapPriorityQueue, we can optionally link it.
        registry.registerImplementation(new ImplementationMetadata(
            "impl-heap-priority-queue", "Heap Priority Queue", "struct-heap",
            "A priority queue backed by a binary heap.",
            Map.of("enqueue", "O(log N)", "dequeue", "O(log N)", "peek", "O(1)"), "O(N)",
            TracedHeapPriorityQueue.class
        ));

        // 6. Hash Maps/Sets (Phase 1 has some Hash implementations)
        StructureMetadata hashMeta = new StructureMetadata(
            "struct-hash",
            "Hash Table",
            "Associative",
            Set.of("hash", "map", "set", "dictionary"),
            "A structure that maps keys to values for fast lookups.",
            "Uses a hash function to compute an index into an array of buckets.",
            "Watch out for collisions and load factor resizing."
        );
        registry.registerStructure(hashMeta);

        // Note: Hash Set/Map tracing is not fully done yet, we can use null or placeholder for missing Traced classes.
        registry.registerImplementation(new ImplementationMetadata(
            "impl-hash-table-chaining", "Hash Table Chaining", "struct-hash",
            "Hash table resolving collisions using linked lists.",
            Map.of("put", "O(1) avg", "get", "O(1) avg", "remove", "O(1) avg"), "O(N)",
            structlab.core.hash.HashTableChaining.class
        ));
        registry.registerImplementation(new ImplementationMetadata(
            "impl-hash-set", "Hash Set", "struct-hash",
            "Set backed by a hash table.",
            Map.of("add", "O(1) avg", "contains", "O(1) avg", "remove", "O(1) avg"), "O(N)",
            structlab.core.hash.HashSetCustom.class
        ));
    }
}
