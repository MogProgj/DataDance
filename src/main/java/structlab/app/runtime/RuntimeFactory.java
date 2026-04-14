package structlab.app.runtime;

import structlab.app.runtime.adapters.StackRuntimeAdapter;
import structlab.app.runtime.adapters.QueueRuntimeAdapter;
import structlab.app.runtime.adapters.ArrayRuntimeAdapter;
import structlab.app.runtime.adapters.ListRuntimeAdapter;
import structlab.app.runtime.adapters.DequeRuntimeAdapter;
import structlab.app.runtime.adapters.HeapRuntimeAdapter;
import structlab.app.runtime.adapters.HashRuntimeAdapter;
import structlab.app.runtime.adapters.TreeRuntimeAdapter;
import structlab.registry.ImplementationMetadata;
import structlab.registry.StructureMetadata;

import structlab.core.stack.ArrayStack;
import structlab.core.stack.LinkedStack;
import structlab.core.queue.CircularArrayQueue;
import structlab.core.queue.LinkedQueue;
import structlab.core.queue.TwoStackQueue;
import structlab.core.array.FixedArray;
import structlab.core.array.DynamicArray;
import structlab.core.list.SinglyLinkedList;
import structlab.core.list.DoublyLinkedList;
import structlab.core.deque.ArrayDequeCustom;
import structlab.core.deque.LinkedDeque;
import structlab.core.heap.BinaryHeap;
import structlab.core.heap.HeapPriorityQueue;
import structlab.core.hash.HashTableChaining;
import structlab.core.hash.HashSetCustom;
import structlab.core.hash.HashTableOpenAddressing;
import structlab.core.tree.BinarySearchTree;
import structlab.core.tree.AVLTree;

import structlab.trace.TraceLog;
import structlab.trace.TracedArrayStack;
import structlab.trace.TracedLinkedStack;
import structlab.trace.TracedCircularArrayQueue;
import structlab.trace.TracedLinkedQueue;
import structlab.trace.TracedTwoStackQueue;
import structlab.trace.TracedFixedArray;
import structlab.trace.TracedDynamicArray;
import structlab.trace.TracedSinglyLinkedList;
import structlab.trace.TracedDoublyLinkedList;
import structlab.trace.TracedArrayDequeCustom;
import structlab.trace.TracedLinkedDeque;
import structlab.trace.TracedBinaryHeap;
import structlab.trace.TracedHeapPriorityQueue;
import structlab.trace.TracedHashTableChaining;
import structlab.trace.TracedHashSetCustom;
import structlab.trace.TracedHashTableOpenAddressing;
import structlab.trace.TracedBinarySearchTree;
import structlab.trace.TracedAVLTree;

public class RuntimeFactory {

    public static StructureRuntime createRuntime(StructureMetadata sm, ImplementationMetadata im) {
        if (im.id().equals("impl-array-stack")) {
            return new StackRuntimeAdapter(im.name(), new TracedArrayStack<Integer>(new ArrayStack<Integer>(), new TraceLog()));
        } else if (im.id().equals("impl-linked-stack")) {
            return new StackRuntimeAdapter(im.name(), new TracedLinkedStack<Integer>(new LinkedStack<Integer>(), new TraceLog()));
        } else if (im.id().equals("impl-circular-array-queue")) {
            return new QueueRuntimeAdapter(im.name(), new TracedCircularArrayQueue<Integer>(new CircularArrayQueue<Integer>(10), new TraceLog()));
        } else if (im.id().equals("impl-linked-queue")) {
            return new QueueRuntimeAdapter(im.name(), new TracedLinkedQueue<Integer>(new LinkedQueue<Integer>(), new TraceLog()));
        } else if (im.id().equals("impl-two-stack-queue")) {
            return new QueueRuntimeAdapter(im.name(), new TracedTwoStackQueue<Integer>(new TwoStackQueue<Integer>(), new TraceLog()));
        } else if (im.id().equals("impl-fixed-array")) {
            return new ArrayRuntimeAdapter(im.name(), new TracedFixedArray<Integer>(new FixedArray<Integer>(10), new TraceLog()));
        } else if (im.id().equals("impl-dynamic-array")) {
            return new ArrayRuntimeAdapter(im.name(), new TracedDynamicArray<Integer>(new DynamicArray<Integer>(), new TraceLog()));
        } else if (im.id().equals("impl-singly-linked-list")) {
            return new ListRuntimeAdapter(im.name(), new TracedSinglyLinkedList<Integer>(new SinglyLinkedList<Integer>(), new TraceLog()));
        } else if (im.id().equals("impl-doubly-linked-list")) {
            return new ListRuntimeAdapter(im.name(), new TracedDoublyLinkedList<Integer>(new DoublyLinkedList<Integer>(), new TraceLog()));
        } else if (im.id().equals("impl-array-deque")) {
            return new DequeRuntimeAdapter(im.name(), new TracedArrayDequeCustom<Integer>(new ArrayDequeCustom<Integer>(), new TraceLog()));
        } else if (im.id().equals("impl-linked-deque")) {
            return new DequeRuntimeAdapter(im.name(), new TracedLinkedDeque<Integer>(new LinkedDeque<Integer>(), new TraceLog()));
        } else if (im.id().equals("impl-binary-heap")) {
            return new HeapRuntimeAdapter(im.name(), new TracedBinaryHeap<Integer>(new BinaryHeap<Integer>(), new TraceLog()));
        } else if (im.id().equals("impl-heap-priority-queue")) {
            return new HeapRuntimeAdapter(im.name(), new TracedHeapPriorityQueue<Integer>(new HeapPriorityQueue<Integer>(), new TraceLog()));
        } else if (im.id().equals("impl-hash-table-chaining")) {
            return new HashRuntimeAdapter(im.name(), new TracedHashTableChaining<Integer, Integer>(new HashTableChaining<Integer, Integer>(), new TraceLog()));
        } else if (im.id().equals("impl-hash-set")) {
            return new HashRuntimeAdapter(im.name(), new TracedHashSetCustom<Integer>(new HashSetCustom<Integer>(), new TraceLog()));
        } else if (im.id().equals("impl-hash-oa-linear")) {
            return new HashRuntimeAdapter(im.name(), new TracedHashTableOpenAddressing<Integer, Integer>(new HashTableOpenAddressing<Integer, Integer>(HashTableOpenAddressing.DEFAULT_INITIAL_CAPACITY, HashTableOpenAddressing.DEFAULT_LOAD_FACTOR, HashTableOpenAddressing.DEFAULT_HASH_TYPE, HashTableOpenAddressing.OpenAddressingType.LINEAR), new TraceLog()));
        } else if (im.id().equals("impl-hash-oa-quadratic")) {
            return new HashRuntimeAdapter(im.name(), new TracedHashTableOpenAddressing<Integer, Integer>(new HashTableOpenAddressing<Integer, Integer>(HashTableOpenAddressing.DEFAULT_INITIAL_CAPACITY, HashTableOpenAddressing.DEFAULT_LOAD_FACTOR, HashTableOpenAddressing.DEFAULT_HASH_TYPE, HashTableOpenAddressing.OpenAddressingType.QUADRATIC), new TraceLog()));
        } else if (im.id().equals("impl-hash-oa-double")) {
            return new HashRuntimeAdapter(im.name(), new TracedHashTableOpenAddressing<Integer, Integer>(new HashTableOpenAddressing<Integer, Integer>(HashTableOpenAddressing.DEFAULT_INITIAL_CAPACITY, HashTableOpenAddressing.DEFAULT_LOAD_FACTOR, HashTableOpenAddressing.DEFAULT_HASH_TYPE, HashTableOpenAddressing.OpenAddressingType.DOUBLE_HASHING), new TraceLog()));
        } else if (im.id().equals("impl-bst")) {
            return new TreeRuntimeAdapter(im.name(), new TracedBinarySearchTree<Integer>(new BinarySearchTree<Integer>(), new TraceLog()));
        } else if (im.id().equals("impl-avl")) {
            return new TreeRuntimeAdapter(im.name(), new TracedAVLTree<Integer>(new AVLTree<Integer>(), new TraceLog()));
        }

        throw new UnsupportedOperationException("Runtime instantiation for " + im.id() + " is not completely wired yet.");
    }
}
