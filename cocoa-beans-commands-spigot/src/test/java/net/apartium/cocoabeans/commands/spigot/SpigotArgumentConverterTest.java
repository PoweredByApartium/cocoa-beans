package net.apartium.cocoabeans.commands.spigot;

import be.seeseemelk.mockbukkit.entity.PlayerMock;
import net.apartium.cocoabeans.commands.ArgumentConverter;
import net.apartium.cocoabeans.commands.spigot.parsers.DogCommand;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpigotArgumentConverterTest extends CommandsSpigotTestBase {

    private PlayerMock voigon;

    @BeforeEach
    @Override
    public void setup() {
        super.setup();

        commandManager.addCommand(new DogCommand());

        voigon = server.addPlayer("Voigon");

    }

    @Override
    protected SpigotArgumentMapper createArgumentMapper() {
        return new SpigotArgumentMapper(List.of(new SenderContainerArgumentConverter(), new StringContainerArgumentConverter()));
    }

    @Test
    void testArgumentMapping() {
        execute(voigon, "dog set test");
        assertEquals("value is test", voigon.nextMessage());

    }

    @Test
    void testSenderMapping() {
        execute(voigon, "dog remove sender");
        assertEquals("value is sender", voigon.nextMessage());

    }

    private static class StringContainerArgumentConverter implements ArgumentConverter<DogCommand.StringContainer> {
        @Override
        public boolean isSourceTypeSupported(Class<?> clazz) {
            return clazz == String.class;
        }

        @Override
        public Class<DogCommand.StringContainer> targetType() {
            return DogCommand.StringContainer.class;
        }

        @Override
        public DogCommand.StringContainer convert(Object source) {
            String str = (String) source;
            if (str.equals("null"))
                return null;
            return new DogCommand.StringContainer(str);
        }
    }

    private static class SenderContainerArgumentConverter implements ArgumentConverter<DogCommand.SenderContainer> {
        @Override
        public boolean isSourceTypeSupported(Class<?> clazz) {
            return CommandSender.class.isAssignableFrom(clazz);
        }

        @Override
        public Class<DogCommand.SenderContainer> targetType() {
            return DogCommand.SenderContainer.class;
        }

        @Override
        public DogCommand.SenderContainer convert(Object source) {
            return new DogCommand.SenderContainer((CommandSender) source);
        }
    }
}
