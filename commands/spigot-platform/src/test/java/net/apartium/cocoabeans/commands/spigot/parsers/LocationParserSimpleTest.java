package net.apartium.cocoabeans.commands.spigot.parsers;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.apartium.cocoabeans.commands.exception.BadCommandResponse;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import net.apartium.cocoabeans.commands.parsers.DoubleParser;
import net.apartium.cocoabeans.commands.parsers.FloatParser;
import net.apartium.cocoabeans.commands.parsers.ParserAssertions;
import net.apartium.cocoabeans.commands.spigot.SpigotSender;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

class LocationParserSimpleTest {

    private ServerMock server;
    private PlayerMock player;

    @BeforeEach
    void setup() {
        server = MockBukkit.mock();
        player = server.addPlayer("ikfir");
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
        server = null;
    }

    @Test
    void simpleLocation() {
        ArgumentParser<Location> parser = new LocationParser(0);
        test(parser);
    }

    void test(ArgumentParser<Location> parser) {
        SpigotSender<CommandSender> sender = new SpigotSender<>(player);

        ParserAssertions.assertParserResult(parser, sender, null, args("world 0 0 0"), 0, new ArgumentParser.ParseResult<>(new Location(Bukkit.getWorld("world"), 0, 0, 0), 4));
        ParserAssertions.assertParserResult(parser, sender, null, args("world 10 20 30"), 0, new ArgumentParser.ParseResult<>(new Location(Bukkit.getWorld("world"), 10, 20, 30), 4));
        ParserAssertions.assertParserResult(parser, sender, null, args("world 10 20 30 21.4"), 0, new ArgumentParser.ParseResult<>(new Location(Bukkit.getWorld("world"), 10, 20, 30), 4));

        ParserAssertions.assertParserResult(parser, sender, null, args("world 10.5 20 30"), 0, new ArgumentParser.ParseResult<>(new Location(Bukkit.getWorld("world"), 10.5, 20, 30), 4));
        ParserAssertions.assertParserResult(parser, sender, "test", args("10.5 20 30"), 0, new ArgumentParser.ParseResult<>(new Location(Bukkit.getWorld("world"), 10.5, 20, 30), 3));

        ParserAssertions.assertParserThrowsReport(parser, sender, "test", args("10.a5 20 30"), 0, BadCommandResponse.class);
    }

    @Test
    void customParsers() {
        ArgumentParser<Location> parser = new LocationParser(0, "location", Map.of(
                "double", new DoubleParser(0),
                "float", new FloatParser(0),
                "world", new WorldParser(5)
        ));
        test(parser);
    }

    String[] args(String s) {
        return s.split("\\s+");
    }

}
