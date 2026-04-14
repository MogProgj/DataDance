package structlab.gui;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ActivityLogEnhancedTest {

    @Test
    void filterByCategoryReturnsMatches() {
        ActivityLog log = new ActivityLog();
        log.log("push", "detail", "operation");
        log.log("open", "detail", "session");
        log.log("pop", "detail", "operation");

        List<ActivityLog.Entry> ops = log.getByCategory("operation");
        assertEquals(2, ops.size());
        assertTrue(ops.stream().allMatch(e -> "operation".equals(e.category())));
    }

    @Test
    void filterByCategoryIsCaseInsensitive() {
        ActivityLog log = new ActivityLog();
        log.log("push", "detail", "operation");
        assertEquals(1, log.getByCategory("OPERATION").size());
    }

    @Test
    void getCategoriesReturnsDistinct() {
        ActivityLog log = new ActivityLog();
        log.log("a", "", "session");
        log.log("b", "", "operation");
        log.log("c", "", "session");
        Set<String> cats = log.getCategories();
        assertEquals(2, cats.size());
        assertTrue(cats.contains("session"));
        assertTrue(cats.contains("operation"));
    }

    @Test
    void clearRemovesAllEntries() {
        ActivityLog log = new ActivityLog();
        log.log("a", "b", "c");
        log.log("d", "e", "f");
        assertEquals(2, log.size());
        log.clear();
        assertEquals(0, log.size());
        assertTrue(log.getAll().isEmpty());
    }

    @Test
    void filterOnEmptyLogReturnsEmpty() {
        ActivityLog log = new ActivityLog();
        assertTrue(log.getByCategory("anything").isEmpty());
    }
}
