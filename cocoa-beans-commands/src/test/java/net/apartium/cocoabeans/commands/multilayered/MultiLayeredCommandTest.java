package net.apartium.cocoabeans.commands.multilayered;

import net.apartium.cocoabeans.commands.CommandTestBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MultiLayeredCommandTest extends CommandTestBase {

    @BeforeEach
    @Override
    public void before() {
        super.before();
        testCommandManager.addCommand(new MultiLayeredCommand());
    }

    void evaluate(String label, String args) {
        sender.getMessages().clear();
        testCommandManager.handle(sender, label, args.isEmpty() ? new String[0] : args.split("\\s+"));
    }

    @Test
    public void receivedGreetings() {
        sender.addPermission("cocoa-beans.commands.multilayered");

        evaluate("multilayered", "greet");
        assertNextMessage("Hello!");
    }

    @Test
    public void mysteryTest() {
        sender.addPermission("cocoa-beans.commands.multilayered");

        evaluate("multilayered", "mystery meow");
        assertNextMessage("Mystery test0");
        evaluate("multilayered", "mystery woof");
        assertNextMessage("Mystery test1");
    }

    @Test
    public void receivedMeow() {
        sender.addPermission("cocoa-beans.commands.multilayered");

        evaluate("multilayered", "meow");
        assertNextMessage("Meow!");
    }

    @Test
    public void receivedAdminBlessings() {
        sender.addPermission("cocoa-beans.commands.multilayered");

        evaluate("multilayered", "admin");
        assertNextMessage("You got no permissions to run this command you fool!");

        sender.addPermission("cocoa-beans.commands.multilayered.admin");
        evaluate("multilayered", "admin");
        assertNextMessage("Whoa! go fuck yourself!");
    }

    @Test
    public void receivedUsage() {
        sender.addPermission("cocoa-beans.commands.multilayered");

        evaluate("multilayered", "");

        assertNextMessage("Multilayered Usage");
        assertNextMessage("/multilayered greet - greets user.");
        assertNextMessage("/multilayered admin - online users command.");
        assertNextMessage("/multilayered admin - admin command.");
        assertNextMessage("/multilayered admin - meowing at a user.");
    }

    @Test
    public void shhTest() {
        sender.addPermission("cocoa-beans.commands.multilayered");

        evaluate("multilayered", "shh");

        assertNextMessage("Shh!");
    }

    // TODO add test for lol both int and string
    @Test
    void lolTest() {
        sender.addPermission("cocoa-beans.commands.multilayered");

        evaluate("multilayered", "lol 21");
        assertNextMessage("Lol x21");

        evaluate("multilayered", "lol 1a");
        assertNextMessage("Lol 1a");
    }

    @Test
    public void receivedOnlinePlayersSize() {
        sender.addPermission("cocoa-beans.commands.multilayered");

        evaluate("multilayered", "users");

        assertNextMessage("There are 20 users online!");
    }

    @Test
    public void noPermission() {
        assertNoPermission("cocoa-beans.commands.multilayered");
    }

    private void assertNoPermission(String permission) {
        evaluate("multilayered", "");
        Assertions.assertFalse(sender.hasPermission(permission));

        String lastMessage = sender.getMessages().get(sender.getMessages().size() - 1);
        assertEquals(lastMessage, "You got no permissions to run this command you fool!");
    }

    private void assertNextMessage(String message) {
        List<String> messages = sender.getMessages();
        assertFalse(messages.isEmpty());

        String lastMessage = sender.getMessages().remove(0);
        assertEquals(lastMessage, message);


    }

}
