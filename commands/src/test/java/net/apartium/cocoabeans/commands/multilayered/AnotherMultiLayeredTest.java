package net.apartium.cocoabeans.commands.multilayered;

import net.apartium.cocoabeans.commands.CommandTestBase;
import net.apartium.cocoabeans.commands.parsers.SourceParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnotherMultiLayeredTest extends CommandTestBase {

    @BeforeEach
    @Override
    public void before() {
        super.before();
        testCommandManager.addCommand(new AnotherMultiLayeredCommand());
    }

    @Test
    void lol() {
        sender.addPermission("cocoa-beans.commands.multilayered");
        evaluate("another-multilayered", "lol 21");

        assertEquals(List.of("Lol 21"), sender.getMessages());
    }

    @Test
    void greetTest() {
        sender.addPermission("cocoa-beans.commands.multilayered");
        evaluate("another-multilayered", "greet");

        assertEquals(List.of("Get off my server!"), sender.getMessages());
    }

    @Test
    void showUsageTest() {
        sender.addPermission("cocoa-beans.commands.multilayered");
        evaluate("another-multilayered", "");

        assertEquals(List.of("Another Multilayered Usage"), sender.getMessages());
    }

    @Test
    void onlineUsersTest() {
        sender.addPermission("cocoa-beans.commands.multilayered");
        evaluate("another-multilayered", "users");

        assertEquals(
                List.of("Only you are online!", "Kinda sad that you're online"),
                sender.getMessages());
    }

    @Test
    void meowTest() {
        sender.addPermission("cocoa-beans.commands.multilayered");
        evaluate("another-multilayered", "meow");

        assertEquals(List.of("Very cool interface"), sender.getMessages());
    }

    @Test
    void adminTest() {
        sender.addPermission("cocoa-beans.commands.multilayered");
        evaluate("another-multilayered", "admin");

        assertEquals(List.of("You got no permissions to run this command you fool!"), sender.getMessages());

        sender.addPermission("cocoa-beans.commands.multilayered.admin");
        evaluate("another-multilayered", "admin");

        assertEquals(
                List.of(
                      "You're not worthy to be an admin!!!",
                      "You feel so bad for being an admin",
                      "You are so bad for being an admin",
                      "Get a job, You discord mod!",
                      "\"Laugh at my bad jokes!\" - A Discord moderator"
                ),
                sender.getMessages()
        );
    }

    @Test
    void mysteryTest() {
        sender.addPermission("cocoa-beans.commands.multilayered");
        evaluate("another-multilayered", "mystery meow");

        assertEquals(List.of("Mystery I'm a cat!"), sender.getMessages());
        evaluate("another-multilayered", "mystery woof");

        assertEquals(List.of("Mystery I'm a dog!"), sender.getMessages());
    }

    void evaluate(String label, String args) {
        sender.getMessages().clear();
        testCommandManager.handle(sender, label, args.isEmpty() ? new String[0] : args.split("\\s+"));
    }
}
