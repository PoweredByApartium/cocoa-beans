package net.apartium.cocoabeans.commands.optional;

import net.apartium.cocoabeans.commands.CommandTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OptionalCommandTest extends CommandTestBase {

    OptionalCommandForTest command;

    @Override
    @BeforeEach
    public void before() {
        super.before();
        command = new OptionalCommandForTest();
        testCommandManager.addCommand(command);
    }

    @Test
    void noData() {
        evaluate("test", "optional");
        assertEquals(List.of("a: A, b: B, c: C"), sender.getMessages());
    }

    @Test
    void oneData() {
        evaluate("test", "optional 1");
        assertEquals(List.of("a: 1, b: B, c: C"), sender.getMessages());
    }

    @Test
    void twoData() {
        evaluate("test", "optional 1 2");
        assertEquals(List.of("a: 1, b: 2, c: C"), sender.getMessages());
    }

    @Test
    void threeData() {
        evaluate("test", "optional 1 2 3");
        assertEquals(List.of("a: 1, b: 2, c: 3"), sender.getMessages());
    }

    @Test
    void noColor() {
        evaluate("test", "color");
        assertEquals(List.of("a: black, b: gray, c: white"), sender.getMessages());
    }

    @Test
    void oneColor() {
        evaluate("test", "color RED");
        assertEquals(List.of("a: red, b: gray, c: white"), sender.getMessages());
    }

    @Test
    void twoColors() {
        evaluate("test", "color RED GREEN");
        assertEquals(List.of("a: red, b: green, c: white"), sender.getMessages());
    }

    @Test
    void threeColors() {
        evaluate("test", "color RED GREEN BLUE");
        assertEquals(List.of("a: red, b: green, c: blue"), sender.getMessages());
    }



    void evaluate(String label, String args) {
        sender.getMessages().clear();
        testCommandManager.handle(sender, label, args.isEmpty() ? new String[0] : args.split("\\s+"));
    }

}
