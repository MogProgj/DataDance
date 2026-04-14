package structlab.gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import structlab.app.service.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reproduces the exact flow the GUI uses when the user types args and clicks Execute.
 */
class PutOperationIntegrationTest {

    private StructLabService service;

    @BeforeEach
    void setUp() {
        service = StructLabService.createDefault();
    }

    // Simulates what MainWindowController.onExecuteOperation does
    private List<String> simulateArgSplit(String rawInput) {
        String rawArgs = rawInput.trim();
        return rawArgs.isEmpty()
                ? List.of()
                : Arrays.stream(rawArgs.split("\\s+")).collect(Collectors.toList());
    }

    @Test
    void chainingPutWithTwoArgs() {
        service.openSession("struct-hash", "impl-hash-table-chaining");
        List<String> args = simulateArgSplit("1 100");
        assertEquals(2, args.size());
        ExecutionResult result = service.executeOperation("put", args);
        assertTrue(result.success(), "put should succeed with 2 args: " + result.message());
    }

    @Test
    void chainingPutWithSingleArgFails() {
        service.openSession("struct-hash", "impl-hash-table-chaining");
        List<String> args = simulateArgSplit("1");
        assertEquals(1, args.size());
        // The GUI should block this, but if it reaches the adapter it should gracefully fail
        ExecutionResult result = service.executeOperation("put", args);
        assertFalse(result.success());
    }

    @Test
    void chainingPutThenGetVerifiesData() {
        service.openSession("struct-hash", "impl-hash-table-chaining");
        ExecutionResult putResult = service.executeOperation("put", simulateArgSplit("5 500"));
        assertTrue(putResult.success(), "put failed: " + putResult.message());

        ExecutionResult getResult = service.executeOperation("get", simulateArgSplit("5"));
        assertTrue(getResult.success());
        assertEquals("500", getResult.returnedValue());
    }

    @Test
    void oaLinearPutWithTwoArgs() {
        service.openSession("struct-hash", "impl-hash-oa-linear");
        List<String> args = simulateArgSplit("1 100");
        ExecutionResult result = service.executeOperation("put", args);
        assertTrue(result.success(), "OA linear put should succeed: " + result.message());
    }

    @Test
    void oaQuadraticPutWithTwoArgs() {
        service.openSession("struct-hash", "impl-hash-oa-quadratic");
        List<String> args = simulateArgSplit("1 100");
        ExecutionResult result = service.executeOperation("put", args);
        assertTrue(result.success(), "OA quadratic put should succeed: " + result.message());
    }

    @Test
    void oaDoublePutWithTwoArgs() {
        service.openSession("struct-hash", "impl-hash-oa-double");
        List<String> args = simulateArgSplit("1 100");
        ExecutionResult result = service.executeOperation("put", args);
        assertTrue(result.success(), "OA double put should succeed: " + result.message());
    }

    @Test
    void fullGuiFlowSimulation() {
        service.openSession("struct-hash", "impl-hash-table-chaining");

        // Get the put operation descriptor
        OperationInfo putOp = service.getAvailableOperations().stream()
                .filter(o -> o.name().equals("put"))
                .findFirst()
                .orElseThrow();
        assertEquals(2, putOp.argCount());

        // User types "1 100"
        String userInput = "1 100";
        List<String> args = simulateArgSplit(userInput);

        // GUI validation
        assertFalse(args.size() < putOp.argCount(), "GUI should allow 2 args for put");

        // Execute
        ExecutionResult result = service.executeOperation(putOp.name(), args);
        assertTrue(result.success(), "put(1,100) should succeed: " + result.message());

        // Verify in history
        List<ExecutionResult> history = service.getHistory();
        assertEquals(1, history.size());
        assertTrue(history.get(0).success());

        // Verify state
        String state = service.getRenderedState();
        assertTrue(state.contains("1"), "State should contain key 1");
    }
}
