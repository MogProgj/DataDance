package structlab.app.command;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class CommandParserTest {

    @Test
    public void testEmptyInput() {
        ParsedCommand cmd = CommandParser.parse("");
        assertEquals("", cmd.name());
        assertTrue(cmd.arguments().isEmpty());
    }

    @Test
    public void testNullInput() {
        ParsedCommand cmd = CommandParser.parse(null);
        assertEquals("", cmd.name());
        assertTrue(cmd.arguments().isEmpty());
    }

    @Test
    public void testSingleCommand() {
        ParsedCommand cmd = CommandParser.parse("help");
        assertEquals("help", cmd.name());
        assertTrue(cmd.arguments().isEmpty());
        assertEquals("help", cmd.raw());
    }

    @Test
    public void testCommandWithArguments() {
        ParsedCommand cmd = CommandParser.parse("push 10 arg2");
        assertEquals("push", cmd.name());
        assertEquals(List.of("10", "arg2"), cmd.arguments());
        assertEquals("push 10 arg2", cmd.raw());
    }

    @Test
    public void testQuotedArguments() {
        ParsedCommand cmd = CommandParser.parse("push \"hello world\" arg2");
        assertEquals("push", cmd.name());
        assertEquals(List.of("hello world", "arg2"), cmd.arguments());
    }

    @Test
    public void testExtraWhitespace() {
        ParsedCommand cmd = CommandParser.parse("  list    all  ");
        assertEquals("list", cmd.name());
        assertEquals(List.of("all"), cmd.arguments());
        assertEquals("list    all", cmd.raw());
    }

    @Test
    public void testCaseInsensitiveName() {
        ParsedCommand cmd = CommandParser.parse("PUSH 10");
        assertEquals("push", cmd.name()); // Name should be lowercased
        assertEquals(List.of("10"), cmd.arguments()); // Args keep their case
    }
}
