package net.apartium.cocoabeans.commands.option;

import net.apartium.cocoabeans.commands.CommandManager;
import net.apartium.cocoabeans.commands.CommandTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NamedArgumentTest extends CommandTestBase {

    @Override
    @BeforeEach
    public void before() {
        super.before();
        testCommandManager.registerArgumentTypeHandler(CommandManager.COMMON_PARSERS);
        testCommandManager.registerArgumentTypeHandler(new Priority.Parser());
        testCommandManager.addCommand(new NamedArgumentCommandTest());
    }


    @Test
    void oneFlag() {
        evaluate("named", "-l");
        assertResult(null, true, null, false, null);

        evaluate("named", "-f");
        assertResult(null, false, null, true, null);
    }

    @Test
    void twoFlags() {
        evaluate("named", "-l -f");
        assertResult(null, true, null, true, null);

        evaluate("named", "-f -l");
        assertResult(null, true, null, true, null);

        evaluate("named", "-lf");
        assertResult(null, true, null, true, null);

        evaluate("named", "-fl");
        assertResult(null, true, null, true, null);
    }

    @Test
    void oneOption() {
        evaluate("named", "-v 10");
        assertResult(10, false, null, false, null);

        evaluate("named", "--volume 11");
        assertResult(11, false, null, false, null);

        evaluate("named", "-v 30 test");
        assertEquals("test", sender.getMessages().remove(0));
        assertResult(30, false, null, false, null);

        evaluate("named", "--volume 35 test");
        assertEquals("test", sender.getMessages().remove(0));
        assertResult(35, false, null, false, null);
    }

    @Test
    void twoOptions() {
        evaluate("named", "-v 20 --pitch 1.5");
        assertResult(20, false, 1.5f, false, null);

        evaluate("named", "--volume 25 --pitch 2.5");
        assertResult(25, false, 2.5f, false, null);

        evaluate("named", "-v 40 --pitch 4.3 test");
        assertEquals("test", sender.getMessages().remove(0));
        assertResult(40, false, 4.3f, false, null);

        evaluate("named", "--volume 45 --pitch 4.8 test");
        assertEquals("test", sender.getMessages().remove(0));
        assertResult(45, false, 4.8f, false, null);
    }

    @Test
    void flagAndOption() {
        evaluate("named", "-v 50 -l --pitch 5.3");
        assertResult(50, true, 5.3f, false, null);

        evaluate("named", "-v 55 -f --pitch 5.8");
        assertResult(55, false, 5.8f, true, null);

        evaluate("named", "-v 60 -l --pitch 6.3 test");
        assertEquals("test", sender.getMessages().remove(0));
        assertResult(60, true, 6.3f, false, null);

        evaluate("named", "-v 65 -f --pitch 6.8 test");
        assertEquals("test", sender.getMessages().remove(0));
        assertResult(65, false, 6.8f, true, null);

        evaluate("named", "-v 70 -fl test");
        assertEquals("test", sender.getMessages().remove(0));
        assertResult(70, true, null, true, null);

        evaluate("named", "-v 75 -lf --pitch 1.2 test");
        assertEquals("test", sender.getMessages().remove(0));
        assertResult(75, true, 1.2f, true, null);

        evaluate("named", "-lf --priority high");
        assertResult(null, true, null, true, Priority.HIGH);

        evaluate("named", "-fl --priority low");
        assertResult(null, true, null, true, Priority.LOW);

        evaluate("named", "-v 70 -l --priority highest test");
        assertEquals("test", sender.getMessages().remove(0));
        assertResult(70, true, null, false, Priority.HIGHEST);

        evaluate("named", "-v 70 -l --priority lowest test");
        assertEquals("test", sender.getMessages().remove(0));
        assertResult(70, true, null, false, Priority.LOWEST);
    }

    @Test
    void errorReport() {
        evaluate("named", "-p l");
        assertEquals("Which one did you mean? [low, lowest]", sender.getMessages().remove(0));

        evaluate("named", "-p h");
        assertEquals("Which one did you mean? [high, highest]", sender.getMessages().remove(0));

        evaluate("named", "-p 1");
        assertEquals("No such priority: \"1\"", sender.getMessages().remove(0));
    }

    @Test
    void noNamedArgs() {
        evaluate("named", "test");
        assertEquals("test", sender.getMessages().remove(0));
        assertResult(null, false, null, false, null);

        evaluate("named", new String[0]);
        assertResult(null, false, null, false, null);

        evaluate("named", "testB");
        assertEquals("test2", sender.getMessages().remove(0));
        assertResult(null, false, null, false, null);
    }

    @Test
    void optionFailedParse() {
        evaluate("named", "-v 80 -f --pitch test");
        assertEquals("Missing value for pitch", sender.getMessages().remove(0));

        evaluate("named", "-v 53a");
        assertEquals("Missing value for v", sender.getMessages().remove(0));
    }

    void assertResult(Integer volume, boolean loud, Float pitch, boolean fast, Priority priority) {
        assertEquals(
                List.of(
                        "volume: " + Optional.ofNullable(volume).map(Object::toString).orElse("none"),
                        "loud: " + loud,
                        "pitch: " + Optional.ofNullable(pitch).map(Object::toString).orElse("none"),
                        "fast: " + fast,
                        "priority: " + Optional.ofNullable(priority).map(Object::toString).orElse("none")
                ),
                sender.getMessages()
        );

        sender.getMessages().clear();
    }

    void evaluate(String label, String args) {
        evaluate(label, args.split("\\s+"));
    }

    void evaluate(String label, String[] args) {
        testCommandManager.handle(sender, label, args);
    }

}
