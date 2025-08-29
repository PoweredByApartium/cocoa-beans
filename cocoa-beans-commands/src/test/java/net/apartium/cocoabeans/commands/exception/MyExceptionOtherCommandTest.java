package net.apartium.cocoabeans.commands.exception;

import net.apartium.cocoabeans.commands.CommandTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MyExceptionOtherCommandTest extends CommandTestBase {

    @BeforeEach
    @Override
    public void before() {
        super.before();

        testCommandManager.addCommand(new MyExceptionOtherCommand());
    }

    @Test
    void test() {
        testCommandManager.handle(sender, "why", new String[]{"no"});
        assertEquals(List.of("Caught my exception"), sender.getMessages());
    }

}
