package net.apartium.cocoabeans.commands.mapper;

import net.apartium.cocoabeans.commands.CommandManager;
import net.apartium.cocoabeans.commands.SimpleArgumentMapper;
import net.apartium.cocoabeans.commands.TestCommandManager;
import net.apartium.cocoabeans.commands.TestSender;
import net.apartium.cocoabeans.commands.lexer.SimpleCommandLexer;
import net.apartium.cocoabeans.commands.parsers.UUIDParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ArgumentConverterTest {

    TestCommandManager testCommandManager;

    TestSender sender;

    @BeforeEach
    void before() {
        testCommandManager = new TestCommandManager(
                new SimpleArgumentMapper(List.of(new UUIDArgumentConverter(), new SenderArgumentConverter(), new CommandContextArgumentConverter())),
                new SimpleCommandLexer());

        testCommandManager.registerArgumentTypeHandler(CommandManager.COMMON_PARSERS);
        testCommandManager.registerArgumentTypeHandler(new UUIDParser());

        sender = new TestSender();

        testCommandManager.addCommand(new MyCoolCommand());
    }

    @Test
    void meowA() {
        UUID uuid = UUID.randomUUID();
        evaluate("cool", "a " + uuid);
        assertEquals(
                List.of("a: " + uuid),
                sender.getMessages()
        );
    }

    @Test
    void meowB() {
        UUID uuid = UUID.randomUUID();
        evaluate("cool", "b " + uuid);
        assertEquals(
                List.of("b: " + uuid),
                sender.getMessages()
        );
    }

    @Test
    void meowC() {
        UUID uuid = UUID.randomUUID();
        evaluate("cool", "c nah_id_win " + uuid);
        assertEquals(
                List.of("nah_id_win -c uuid: " + uuid),
                sender.getMessages()
        );
    }

    @Test
    void meowD() {
        UUID uuid = UUID.randomUUID();
        evaluate("cool", "d " + uuid + " nah_id_win");
        assertEquals(
                List.of("nah_id_win -d uuid: " + uuid),
                sender.getMessages()
        );
    }

    @Test
    void meowE() {
        UUID uuid = UUID.randomUUID();
        evaluate("cool", "e nah_id_win " + uuid);
        assertEquals(
                List.of("nah_id_win -e uuid: " + uuid),
                sender.getMessages()
        );
    }

    @Test
    void meowF() {
        UUID uuid = UUID.randomUUID();
        evaluate("cool", "f " + uuid + " nah_id_win");
        assertEquals(
                List.of("nah_id_win -f uuid: " + uuid),
                sender.getMessages()
        );
    }

    @Test
    void meowG() {
        UUID uuid = UUID.randomUUID();
        evaluate("cool", "g " + uuid + " nah_id_win");
        assertEquals(
                List.of("nah_id_win -g uuid: " + uuid),
                sender.getMessages()
        );
    }

    @Test
    void senderConverter() {
        evaluate("cool", "sender-converter");
        assertEquals(
                List.of("sender converted"),
                sender.getMessages()
        );

    }

    @Test
    void contextConverter() {
        evaluate("cool", "context-converter");
        assertEquals(
                List.of("context converted"),
                sender.getMessages()
        );

    }


    void evaluate(String label, String args) {
        sender.getMessages().clear();
        testCommandManager.handle(sender, label, args.isEmpty() ? new String[0] : args.split("\\s+"));
    }

}
