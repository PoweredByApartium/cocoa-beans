package net.apartium.cocoabeans.commands.parsers.uuid;

import net.apartium.cocoabeans.commands.CommandTestBase;
import net.apartium.cocoabeans.commands.parsers.UUIDParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UUIDCommandTest extends CommandTestBase {

    @Override
    @BeforeEach
    public void before () {
        super.before();
        testCommandManager.registerArgumentTypeHandler(new UUIDParser());

        testCommandManager.addCommand(new UUIDCommand());
    }

    @Test
    public void validUUID() {
        evaluate("uuid", "12345678-1234-1234-1234-123456789012");
        assertEquals(List.of("Got UUID: 12345678-1234-1234-1234-123456789012"), sender.getMessages());

        evaluate("uuid", "566748a4-efad-4dba-8612-e69baf0ace5d");
        assertEquals(List.of("Got UUID: 566748a4-efad-4dba-8612-e69baf0ace5d"), sender.getMessages());

        evaluate("uuid", "01928ea9-c576-77e0-90ae-1193b9675e21");
        assertEquals(List.of("Got UUID: 01928ea9-c576-77e0-90ae-1193b9675e21"), sender.getMessages());

        evaluate("uuid", "942c53f8-014e-408f-8113-50e514286045");
        assertEquals(List.of("Got UUID: 942c53f8-014e-408f-8113-50e514286045"), sender.getMessages());

        evaluate("uuid", "942c53f8-014e-408f-8113-50e514286045");
        assertEquals(List.of("Got UUID: 942c53f8-014e-408f-8113-50e514286045"), sender.getMessages());
    }

    @Test
    public void tabCompletion() {
        assertEquals(List.of(), evaluateTabCompletion("uuid", ""));
        assertEquals(List.of(), evaluateTabCompletion("uuid", "test"));
    }

    @Test
    public void invalidUUID() {
        evaluate("uuid", "12345678-12a34-1234-1234-123456789012");
        assertEquals(List.of("Invalid UUID: 12345678-12a34-1234-1234-123456789012"), sender.getMessages());

        evaluate("uuid", "randomText");
        assertEquals(List.of("Invalid UUID: randomText"), sender.getMessages());
    }

    List<String> evaluateTabCompletion(String label, String args) {
        return testCommandManager.handleTabComplete(sender, label, args.split("\\s+"));
    }

    void evaluate(String label, String args) {
        sender.getMessages().clear();
        testCommandManager.handle(sender, label, args.isEmpty() ? new String[0] : args.split("\\s+"));
    }
}
