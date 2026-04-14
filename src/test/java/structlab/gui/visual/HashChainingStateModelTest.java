package structlab.gui.visual;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HashChainingStateModelTest {

    @Test
    void emptyModel() {
        var model = new HashChainingStateModel(0, 4, "DIVISION", 0, 0, List.of(
                new HashChainingStateModel.Bucket(0, List.of()),
                new HashChainingStateModel.Bucket(1, List.of()),
                new HashChainingStateModel.Bucket(2, List.of()),
                new HashChainingStateModel.Bucket(3, List.of())
        ));
        assertTrue(model.isEmpty());
        assertEquals(0.0, model.loadFactor());
        assertEquals(0, model.occupiedCount());
        assertEquals(0, model.collisionBuckets());
    }

    @Test
    void populatedModel() {
        var model = new HashChainingStateModel(3, 8, "DIVISION", 2, 0, List.of(
                new HashChainingStateModel.Bucket(0, List.of()),
                new HashChainingStateModel.Bucket(1, List.of(
                        new HashChainingStateModel.Entry("9", "900"),
                        new HashChainingStateModel.Entry("1", "100"))),
                new HashChainingStateModel.Bucket(2, List.of(
                        new HashChainingStateModel.Entry("2", "200"))),
                new HashChainingStateModel.Bucket(3, List.of()),
                new HashChainingStateModel.Bucket(4, List.of()),
                new HashChainingStateModel.Bucket(5, List.of()),
                new HashChainingStateModel.Bucket(6, List.of()),
                new HashChainingStateModel.Bucket(7, List.of())
        ));
        assertFalse(model.isEmpty());
        assertEquals(3.0 / 8, model.loadFactor(), 0.001);
        assertEquals(2, model.occupiedCount());
        assertEquals(1, model.collisionBuckets());
    }

    @Test
    void bucketChainLength() {
        var bucket = new HashChainingStateModel.Bucket(0, List.of(
                new HashChainingStateModel.Entry("a", "1"),
                new HashChainingStateModel.Entry("b", "2"),
                new HashChainingStateModel.Entry("c", "3")));
        assertEquals(3, bucket.chainLength());
        assertFalse(bucket.isEmpty());
    }

    @Test
    void emptyBucket() {
        var bucket = new HashChainingStateModel.Bucket(5, List.of());
        assertTrue(bucket.isEmpty());
        assertEquals(0, bucket.chainLength());
    }
}
