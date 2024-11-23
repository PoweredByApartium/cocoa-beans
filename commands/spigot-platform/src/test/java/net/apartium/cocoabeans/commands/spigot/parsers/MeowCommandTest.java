package net.apartium.cocoabeans.commands.parser;

import net.apartium.cocoabeans.commands.CommandForTest;
import net.apartium.cocoabeans.commands.CommandTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MeowCommandTest extends CommandTestBase {


    @Override
    @BeforeEach
    public void before() {
        super.before();
        testCommandManager.registerArgumentTypeHandler(new MeowParser(0));
    }

    @Test
    void init() {
        System.out.println("meow");
    }
}
