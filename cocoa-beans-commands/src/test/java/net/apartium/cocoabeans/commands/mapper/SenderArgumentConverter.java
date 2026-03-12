package net.apartium.cocoabeans.commands.mapper;

import net.apartium.cocoabeans.commands.ArgumentConverter;
import net.apartium.cocoabeans.commands.Sender;

public class SenderArgumentConverter implements ArgumentConverter<SenderContainer> {

    @Override
    public boolean isSourceTypeSupported(Class<?> clazz) {
        return Sender.class.isAssignableFrom(clazz);
    }

    @Override
    public Class<SenderContainer> targetType() {
        return SenderContainer.class;
    }

    @Override
    public SenderContainer convert(Object source) {
        return new SenderContainer((Sender) source);
    }
}
