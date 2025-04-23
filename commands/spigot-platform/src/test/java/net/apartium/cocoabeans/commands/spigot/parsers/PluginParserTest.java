package net.apartium.cocoabeans.commands.spigot.parsers;

import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import net.apartium.cocoabeans.commands.parsers.ParserAssertions;
import net.apartium.cocoabeans.commands.spigot.CommandsSpigotTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static net.apartium.cocoabeans.commands.parsers.ParserAssertions.args;

public class PluginParserTest extends CommandsSpigotTestBase {

    PluginParser parser;

    @BeforeEach
    @Override
    public void setup() {
        super.setup();
        parser = new PluginParser(0);
    }

    @Test
    void assertResults() {
        ParserAssertions.assertParserResult(parser, null, null, args(plugin.getName()), new ArgumentParser.ParseResult<>(plugin, 1));

    }
}
