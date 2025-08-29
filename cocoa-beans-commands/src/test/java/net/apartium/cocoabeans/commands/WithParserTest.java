package net.apartium.cocoabeans.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WithParserTest  extends CommandTestBase {

    @Override
    @BeforeEach
    public void before() {
        super.before();
        testCommandManager.addCommand(new WithParserCommand());
    }

    @Test
    void test() {
        evaluate("withparser", "test 1");
        assertEquals(List.of("test(Sender sender, double num) 1.0"), sender.getMessages());
        evaluate("withparser", "test 78");
        assertEquals(List.of("test(Sender sender, double num) 78.0"), sender.getMessages());
    }

    void evaluate(String label, String args) {
        sender.getMessages().clear();
        testCommandManager.handle(sender, label, args.isEmpty() ? new String[0] : args.split("\\s+"));
    }

}
