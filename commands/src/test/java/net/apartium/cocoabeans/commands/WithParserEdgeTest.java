package net.apartium.cocoabeans.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WithParserEdgeTest {

    public TestCommandManager testCommandManager;

    public TestSender sender;

    @BeforeEach
    public void before() {
        testCommandManager = new TestCommandManager();

        sender = new TestSender();

        assertThrows(RuntimeException.class, () -> testCommandManager.addCommand(new WithParserEdgeCommand()));
    }

    @Test
    void meow() {
        evaluate("with-parser-edge", "test 123");
        assertEquals(List.of("test(Sender sender, String string) 123"), sender.getMessages());
    }

    void evaluate(String label, String args) {
        sender.getMessages().clear();
        testCommandManager.handle(sender, label, args.isEmpty() ? new String[0] : args.split("\\s+"));
    }
}
