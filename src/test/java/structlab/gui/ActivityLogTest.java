package structlab.gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActivityLogTest {

    private ActivityLog log;

    @BeforeEach
    void setUp() {
        log = new ActivityLog();
    }

    @Test
    void startsEmpty() {
        assertEquals(0, log.size());
        assertTrue(log.getAll().isEmpty());
        assertTrue(log.getRecent(10).isEmpty());
    }

    @Test
    void logIncreasesSize() {
        log.log("Opened session", "Stack / ArrayStack", "session");
        assertEquals(1, log.size());
    }

    @Test
    void getRecentReturnsNewestFirst() {
        log.log("First", "d1", "session");
        log.log("Second", "d2", "operation");
        log.log("Third", "d3", "comparison");

        List<ActivityLog.Entry> recent = log.getRecent(10);
        assertEquals(3, recent.size());
        assertEquals("Third", recent.get(0).action());
        assertEquals("First", recent.get(2).action());
    }

    @Test
    void getRecentRespectsLimit() {
        log.log("A", "", "s");
        log.log("B", "", "s");
        log.log("C", "", "s");

        List<ActivityLog.Entry> recent = log.getRecent(2);
        assertEquals(2, recent.size());
        assertEquals("C", recent.get(0).action());
        assertEquals("B", recent.get(1).action());
    }

    @Test
    void getAllReturnsNewestFirst() {
        log.log("First", "", "a");
        log.log("Second", "", "b");

        List<ActivityLog.Entry> all = log.getAll();
        assertEquals(2, all.size());
        assertEquals("Second", all.get(0).action());
    }

    @Test
    void entriesAreUnmodifiable() {
        log.log("A", "", "x");
        assertThrows(UnsupportedOperationException.class,
                () -> log.getAll().add(new ActivityLog.Entry("B", "", "y", java.time.LocalDateTime.now())));
        assertThrows(UnsupportedOperationException.class,
                () -> log.getRecent(10).clear());
    }

    @Test
    void entryRecordFieldsAreCorrect() {
        log.log("Pushed", "value 42", "operation");
        ActivityLog.Entry entry = log.getRecent(1).get(0);
        assertEquals("Pushed", entry.action());
        assertEquals("value 42", entry.detail());
        assertEquals("operation", entry.category());
        assertNotNull(entry.timestamp());
    }
}
