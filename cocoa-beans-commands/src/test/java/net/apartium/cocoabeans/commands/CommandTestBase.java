package net.apartium.cocoabeans.commands;

import org.junit.jupiter.api.BeforeEach;

public class CommandTestBase {
    public TestCommandManager testCommandManager;

    public TestSender sender;

    @BeforeEach
    public void before() {
        testCommandManager = new TestCommandManager();

        testCommandManager.registerArgumentTypeHandler(CommandManager.COMMON_PARSERS);

        sender = new TestSender();
    }

}
