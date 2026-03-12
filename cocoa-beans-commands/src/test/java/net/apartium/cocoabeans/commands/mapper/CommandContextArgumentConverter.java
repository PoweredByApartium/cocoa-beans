package net.apartium.cocoabeans.commands.mapper;

import net.apartium.cocoabeans.commands.ArgumentConverter;
import net.apartium.cocoabeans.commands.CommandContext;

public class CommandContextArgumentConverter implements ArgumentConverter<CommandContextContainer> {

    @Override
    public boolean isSourceTypeSupported(Class<?> clazz) {
        return CommandContext.class.isAssignableFrom(clazz);
    }

    @Override
    public Class<CommandContextContainer> targetType() {
        return CommandContextContainer.class;
    }

    @Override
    public CommandContextContainer convert(Object source) {
        return new CommandContextContainer((CommandContext) source);
    }

}
