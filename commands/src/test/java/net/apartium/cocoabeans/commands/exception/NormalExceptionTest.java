package net.apartium.cocoabeans.commands.exception;

import net.apartium.cocoabeans.commands.CommandTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NormalExceptionTest extends CommandTestBase {

    @BeforeEach
    @Override
    public void before() {
        super.before();

        testCommandManager.addCommand(new NormalExceptionCommand());
    }

    @Test
    void test() {
        testCommandManager.handle(sender, "test", new String[]{"throw"});
        assertEquals(List.of("GO way", "Caught my exception"), sender.getMessages());
    }


}
