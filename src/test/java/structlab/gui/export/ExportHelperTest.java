package structlab.gui.export;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import structlab.app.comparison.ComparisonEntryResult;
import structlab.app.comparison.ComparisonOperationResult;
import structlab.gui.ActivityLog;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExportHelperTest {

    private ComparisonEntryResult entry(String name, boolean success, String returned, long nanos) {
        return new ComparisonEntryResult("id-" + name, name, success, "push", "", returned, "[]",
                List.of(), nanos);
    }

    @Nested
    class CompareExport {
        @Test
        void textContainsStructureName() {
            ComparisonOperationResult op = new ComparisonOperationResult("push", List.of("42"),
                    List.of(entry("ArrayStack", true, "null", 1000),
                            entry("LinkedStack", true, "null", 2000)));
            String text = ExportHelper.compareHistoryToText("Stack", List.of(op));
            assertTrue(text.contains("Stack"));
            assertTrue(text.contains("push"));
            assertTrue(text.contains("ArrayStack"));
        }

        @Test
        void jsonIsWellFormed() {
            ComparisonOperationResult op = new ComparisonOperationResult("push", List.of("42"),
                    List.of(entry("ArrayStack", true, "null", 1000)));
            String json = ExportHelper.compareHistoryToJson("Stack", List.of(op));
            assertTrue(json.startsWith("{"));
            assertTrue(json.contains("\"structure\""));
            assertTrue(json.contains("\"operations\""));
            assertTrue(json.endsWith("}"));
        }

        @Test
        void emptyHistoryProducesMinimalOutput() {
            String text = ExportHelper.compareHistoryToText("Queue", List.of());
            assertTrue(text.contains("Queue"));
            String json = ExportHelper.compareHistoryToJson("Queue", List.of());
            assertTrue(json.contains("\"operations\": ["));
        }
    }

    @Nested
    class ActivityExport {
        @Test
        void textContainsEntries() {
            ActivityLog log = new ActivityLog();
            log.log("push", "pushed 42", "operation");
            log.log("open", "opened Stack session", "session");
            String text = ExportHelper.activityToText(log.getAll());
            assertTrue(text.contains("push"));
            assertTrue(text.contains("operation"));
            assertTrue(text.contains("session"));
        }

        @Test
        void jsonContainsEntries() {
            ActivityLog log = new ActivityLog();
            log.log("pop", "popped value", "operation");
            String json = ExportHelper.activityToJson(log.getAll());
            assertTrue(json.startsWith("["));
            assertTrue(json.contains("\"action\": \"pop\""));
            assertTrue(json.endsWith("]"));
        }

        @Test
        void emptyActivityProducesMinimalOutput() {
            String text = ExportHelper.activityToText(List.of());
            assertTrue(text.contains("Activity Log"));
            String json = ExportHelper.activityToJson(List.of());
            assertEquals("[\n]", json);
        }
    }
}
