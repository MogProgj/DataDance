# Structure Families

StructLab currently supports 8 structure families with 19 concrete
implementations.  Every implementation has full visual pane support
in both Explore and Compare modes.

---

## Array

**Category:** Linear

Demonstrates contiguous memory, index-based access, and capacity
management.

| Implementation | ID | Visual pane | Educational focus |
|---|---|---|---|
| FixedArray | impl-fixed-array | FixedArrayVisualPane | Static capacity, index bounds |
| DynamicArray | impl-dynamic-array | DynamicArrayVisualPane | Amortized resize, capacity vs size |

**Compare support:** Full.  Shows how fixed and dynamic arrays handle
capacity limits differently — one rejects overflow, the other grows.

---

## Linked List

**Category:** Linear

Demonstrates node-based storage, pointer chasing, and traversal
direction differences.

| Implementation | ID | Visual pane | Educational focus |
|---|---|---|---|
| SinglyLinkedList | impl-singly-linked-list | SinglyLinkedListVisualPane | Forward-only traversal, head/tail tracking |
| DoublyLinkedList | impl-doubly-linked-list | DoublyLinkedListVisualPane | Bidirectional links, O(1) removal at both ends |

**Compare support:** Full.  Singly vs doubly linked lists reveal the
cost of backward traversal and the value of two-way pointers.

---

## Stack

**Category:** Linear

Demonstrates LIFO discipline across different backing stores.

| Implementation | ID | Visual pane | Educational focus |
|---|---|---|---|
| ArrayStack | impl-array-stack | StackVisualPane | Contiguous backing, amortized resize |
| LinkedStack | impl-linked-stack | StackVisualPane | Node-based, constant-time push/pop |

**Compare support:** Full.  ArrayStack vs LinkedStack shows memory
layout and resize behavior differences for identical LIFO operations.

---

## Queue

**Category:** Linear

Demonstrates FIFO discipline with three fundamentally different
backing strategies.

| Implementation | ID | Visual pane | Educational focus |
|---|---|---|---|
| CircularArrayQueue | impl-circular-array-queue | CircularQueueVisualPane | Circular buffer, wraparound |
| LinkedQueue | impl-linked-queue | QueueVisualPane | Node chain, front/rear pointers |
| TwoStackQueue | impl-two-stack-queue | QueueVisualPane | Amortized FIFO via two stacks |

**Compare support:** Full.  Three-way comparison reveals circular
buffer mechanics vs linked chains vs stack-based amortization.

---

## Deque

**Category:** Linear

Demonstrates double-ended access with array and linked backing.

| Implementation | ID | Visual pane | Educational focus |
|---|---|---|---|
| ArrayDequeCustom | impl-array-deque | ArrayDequeVisualPane | Circular buffer, dual-end operations |
| LinkedDeque | impl-linked-deque | LinkedDequeVisualPane | Doubly-linked chain, constant-time ends |

**Compare support:** Full.  Array-backed vs linked deque shows
circular buffer management vs pointer manipulation.

---

## Heap / Priority Queue

**Category:** Tree

Demonstrates the heap property, level-order array storage, and
priority-based extraction.

| Implementation | ID | Visual pane | Educational focus |
|---|---|---|---|
| BinaryHeap | impl-binary-heap | HeapVisualPane | Heap property, sift-up/down, tree-in-array |
| HeapPriorityQueue | impl-heap-priority-queue | PriorityQueueVisualPane | Priority abstraction over heap |

**Compare support:** Full.  BinaryHeap shows raw tree structure;
HeapPriorityQueue shows the abstraction layer and "next out" semantics.

---

## Hash Table

**Category:** Associative

Demonstrates hashing, collision resolution, load factor, and rehashing.

| Implementation | ID | Visual pane | Educational focus |
|---|---|---|---|
| HashTableChaining | impl-hash-table-chaining | HashChainingVisualPane | Separate chaining, bucket chains |
| HashTableOpenAddressing (Linear) | impl-hash-oa-linear | HashOpenAddressingVisualPane | Linear probing, clustering |
| HashTableOpenAddressing (Quadratic) | impl-hash-oa-quadratic | HashOpenAddressingVisualPane | Quadratic probing, spread |
| HashTableOpenAddressing (Double) | impl-hash-oa-double | HashOpenAddressingVisualPane | Double hashing, minimal clustering |
| HashSetCustom | impl-hash-set | HashSetVisualPane | Set semantics over chaining table |

**Compare support:** Full.  Five-way hash comparison shows chaining
vs three probing strategies vs set semantics — the richest comparison
family in the project.

---

## Ordered Tree

**Category:** Tree

Demonstrates ordered insertion, BST property, self-balancing rotations,
and tree traversals.

| Implementation | ID | Visual pane | Educational focus |
|---|---|---|---|
| BinarySearchTree | impl-bst | OrderedTreeVisualPane | BST property, unbalanced worst-case |
| AVLTree | impl-avl | OrderedTreeVisualPane | Rotations (LL/RR/LR/RL), guaranteed O(log N) |

**Compare support:** Full.  BST vs AVL reveals how the same insertions
can produce wildly different tree shapes — BST may degenerate to a
linked list while AVL stays balanced via rotations.
