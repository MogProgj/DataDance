package structlab.app.comparison;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CanonicalOperationRegistryTest {

    @Test
    void heapFamilyHasThreeCanonicalOps() {
        List<CanonicalOperationRegistry.CanonicalOperation> ops =
                CanonicalOperationRegistry.forFamily("heap");
        assertEquals(3, ops.size());
    }

    @Test
    void heapFamilyHasMappingReturnsTrue() {
        assertTrue(CanonicalOperationRegistry.hasMapping("heap"));
        assertTrue(CanonicalOperationRegistry.hasMapping("Heap"));
    }

    @Test
    void unknownFamilyHasMappingReturnsFalse() {
        assertFalse(CanonicalOperationRegistry.hasMapping("unknown"));
        assertFalse(CanonicalOperationRegistry.hasMapping(null));
    }

    @Test
    void unknownFamilyReturnsEmptyList() {
        assertTrue(CanonicalOperationRegistry.forFamily("unknown").isEmpty());
        assertTrue(CanonicalOperationRegistry.forFamily(null).isEmpty());
    }

    // ── resolveCanonical ────────────────────────────────────

    @Test
    void resolveCanonicalInsertFromEnqueue() {
        assertEquals("insert", CanonicalOperationRegistry.resolveCanonical("heap", "enqueue"));
    }

    @Test
    void resolveCanonicalExtractminFromDequeue() {
        assertEquals("extractmin", CanonicalOperationRegistry.resolveCanonical("heap", "dequeue"));
    }

    @Test
    void resolveCanonicalExtractminFromRemovemin() {
        assertEquals("extractmin", CanonicalOperationRegistry.resolveCanonical("heap", "removemin"));
    }

    @Test
    void resolveCanonicalPeekUnchanged() {
        assertEquals("peek", CanonicalOperationRegistry.resolveCanonical("heap", "peek"));
    }

    @Test
    void resolveCanonicalUnknownOpReturnsOriginal() {
        assertEquals("frobnicate", CanonicalOperationRegistry.resolveCanonical("heap", "frobnicate"));
    }

    @Test
    void resolveCanonicalIsCaseInsensitive() {
        assertEquals("insert", CanonicalOperationRegistry.resolveCanonical("heap", "ENQUEUE"));
        assertEquals("extractmin", CanonicalOperationRegistry.resolveCanonical("heap", "Dequeue"));
    }

    // ── areEquivalent ───────────────────────────────────────

    @Test
    void insertAndEnqueueAreEquivalent() {
        assertTrue(CanonicalOperationRegistry.areEquivalent("heap", "insert", "enqueue"));
    }

    @Test
    void extractminAndDequeueAreEquivalent() {
        assertTrue(CanonicalOperationRegistry.areEquivalent("heap", "extractmin", "dequeue"));
    }

    @Test
    void peekAndPeekAreEquivalent() {
        assertTrue(CanonicalOperationRegistry.areEquivalent("heap", "peek", "peek"));
    }

    @Test
    void insertAndPeekAreNotEquivalent() {
        assertFalse(CanonicalOperationRegistry.areEquivalent("heap", "insert", "peek"));
    }

    @Test
    void nullOperationsAreNotEquivalent() {
        assertFalse(CanonicalOperationRegistry.areEquivalent("heap", null, "peek"));
        assertFalse(CanonicalOperationRegistry.areEquivalent("heap", "peek", null));
    }

    // ── registeredFamilies ──────────────────────────────────

    @Test
    void registeredFamiliesContainsHeap() {
        assertTrue(CanonicalOperationRegistry.registeredFamilies().contains("heap"));
    }

    // ── CanonicalOperation.matches ──────────────────────────

    @Test
    void canonicalOperationMatchesOwnName() {
        var op = new CanonicalOperationRegistry.CanonicalOperation(
                "insert", java.util.Set.of("enqueue"), "Insert", 1);
        assertTrue(op.matches("insert"));
        assertTrue(op.matches("INSERT"));
    }

    @Test
    void canonicalOperationMatchesAlias() {
        var op = new CanonicalOperationRegistry.CanonicalOperation(
                "insert", java.util.Set.of("enqueue"), "Insert", 1);
        assertTrue(op.matches("enqueue"));
        assertTrue(op.matches("ENQUEUE"));
    }

    @Test
    void canonicalOperationDoesNotMatchUnrelated() {
        var op = new CanonicalOperationRegistry.CanonicalOperation(
                "insert", java.util.Set.of("enqueue"), "Insert", 1);
        assertFalse(op.matches("peek"));
        assertFalse(op.matches(null));
    }

    // ── Hash family ─────────────────────────────────────────

    @Test
    void hashFamilyHasFourCanonicalOps() {
        var ops = CanonicalOperationRegistry.forFamily("hash");
        assertEquals(4, ops.size());
    }

    @Test
    void hashFamilyHasMappingReturnsTrue() {
        assertTrue(CanonicalOperationRegistry.hasMapping("hash"));
    }

    @Test
    void hashPutResolvesFromInsert() {
        assertEquals("put", CanonicalOperationRegistry.resolveCanonical("hash", "insert"));
    }

    @Test
    void hashPutAndInsertAreEquivalent() {
        assertTrue(CanonicalOperationRegistry.areEquivalent("hash", "put", "insert"));
    }

    @Test
    void hashContainsKeyResolvesFromContains() {
        assertEquals("containskey", CanonicalOperationRegistry.resolveCanonical("hash", "contains"));
    }

    @Test
    void hashRemoveResolvesFromDelete() {
        assertEquals("remove", CanonicalOperationRegistry.resolveCanonical("hash", "delete"));
    }

    @Test
    void registeredFamiliesContainsHash() {
        assertTrue(CanonicalOperationRegistry.registeredFamilies().contains("hash"));
    }

    // ── Linked list family ───────────────────────────────────

    @Test
    void listFamilyHasSixCanonicalOps() {
        var ops = CanonicalOperationRegistry.forFamily("list");
        assertEquals(6, ops.size());
    }

    @Test
    void listFamilyHasMapping() {
        assertTrue(CanonicalOperationRegistry.hasMapping("list"));
    }

    @Test
    void listAddFirstResolvesFromPrepend() {
        assertEquals("addfirst", CanonicalOperationRegistry.resolveCanonical("list", "prepend"));
    }

    @Test
    void listAddLastResolvesFromAppend() {
        assertEquals("addlast", CanonicalOperationRegistry.resolveCanonical("list", "append"));
    }

    @Test
    void registeredFamiliesContainsList() {
        assertTrue(CanonicalOperationRegistry.registeredFamilies().contains("list"));
    }

    // ── Deque family ────────────────────────────────────────

    @Test
    void dequeFamilyHasSixCanonicalOps() {
        var ops = CanonicalOperationRegistry.forFamily("deque");
        assertEquals(6, ops.size());
    }

    @Test
    void dequeFamilyHasMapping() {
        assertTrue(CanonicalOperationRegistry.hasMapping("deque"));
    }

    @Test
    void dequeAddFirstResolvesFromPushFront() {
        assertEquals("addfirst", CanonicalOperationRegistry.resolveCanonical("deque", "pushfront"));
    }

    @Test
    void dequeRemoveLastResolvesFromPopBack() {
        assertEquals("removelast", CanonicalOperationRegistry.resolveCanonical("deque", "popback"));
    }

    @Test
    void registeredFamiliesContainsDeque() {
        assertTrue(CanonicalOperationRegistry.registeredFamilies().contains("deque"));
    }
}
