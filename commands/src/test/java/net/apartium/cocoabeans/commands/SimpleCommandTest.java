package net.apartium.cocoabeans.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SimpleCommandTest extends CommandTestBase {

    @BeforeEach
    @Override
    public void before() {
        super.before();
        testCommandManager.addCommand(new SimpleCommand());
    }

    @Test
    void test() {
        execute("123");
        assertEquals(List.of("Amount: 123"), sender.getMessages());

        execute("125 mhm");
        assertEquals(List.of("Mhm: 125"), sender.getMessages());

        execute("another 32.5");
        assertEquals(List.of("Another: 32.5"), sender.getMessages());
    }

    void execute(String args) {
        sender.getMessages().clear();
        testCommandManager.handle(sender, "simple", args.split("\\s+"));
    }

}
