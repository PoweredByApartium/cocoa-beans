package net.apartium.cocoabeans.commands.parameter;

import net.apartium.cocoabeans.commands.CommandTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestCommandWithParameterNames extends CommandTestBase {

    @BeforeEach
    @Override
    public void before() {
        super.before();

        testCommandManager.addCommand(new CommandWithParameterNames());
    }

    @Test
    void testAmount() {
        evaluate("test", "give ikfir 7 128");
        assertEquals(List.of("ikfir amount is 128 and num: 7"), sender.getMessages());

        evaluate("test", "give ikfirBot 18 64");
        assertEquals(List.of("ikfirBot amount is 64 and num: 18"), sender.getMessages());
    }

    @Test
    void testAmount2() {
        evaluate("test", "test ikfirBot 18 64");
        assertEquals(List.of("ikfirBot amount2 is 64 and num: 18"), sender.getMessages());
    }

    @Test
    void testMhm() {
        evaluate("test", "mhm ikfir 73 128 strAYo strBmhm 13 strC 19 20");
        assertEquals(List.of("mhmm{a=13, b=19, c=20, strA=strAYo, strB=strBmhm, strC=strC, amount=128, target=ikfir, num=73}"), sender.getMessages());
    }

    @Test
    void testTesting() {
        evaluate("test", "testing 123 2players ikfirBot");
        assertEquals(List.of("testing a ikfirBot with number of 123 by the amount of 2players"), sender.getMessages());
    }

    @Test
    void testWrongType() {
        assertThrows(IllegalArgumentException.class, () -> testCommandManager.addCommand(new WrongTypeParameterNames()));
    }

    @Test
    void testSend() {
        evaluate("test", "send ikfir Hey, how are you?");
        assertEquals(List.of("sending message to ikfir: Hey, how are you?"), sender.getMessages());

        evaluate("test", "send jeff Could you write more test for me jeff?");
        assertEquals(List.of("sending message to jeff: Could you write more test for me jeff?"), sender.getMessages());
    }

    @Test
    void testDuplicateParameterNameInSubCommand() {
        assertThrows(IllegalArgumentException.class, () -> testCommandManager.addCommand(new DuplicateParameterNameInSubCommand()));
    }

    @Test
    void testDuplicateParameterNameInMethod() {
        assertThrows(IllegalArgumentException.class, () -> testCommandManager.addCommand(new DuplicateParameterNameInMethod()));
    }

    void evaluate(String label, String args) {
        sender.getMessages().clear();
        testCommandManager.handle(sender, label, args.isEmpty() ? new String[0] : args.split("\\s+"));
    }
}
