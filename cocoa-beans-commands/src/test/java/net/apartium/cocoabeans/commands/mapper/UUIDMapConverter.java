package net.apartium.cocoabeans.commands.mapper;

import net.apartium.cocoabeans.commands.MapConverter;

import java.util.Set;
import java.util.UUID;

public class UUIDMapConverter implements MapConverter<String> {

    private Set<Class<?>> supportedSourceType() {
        return Set.of(UUID.class);
    }

    @Override
    public boolean isSourceTypeSupported(Class<?> clazz) {
        for (Class<?> type : supportedSourceType())
            if (type.isAssignableFrom(clazz))
                return true;

        return false;
    }

    @Override
    public Class<String> targetType() {
        return String.class;
    }


    @Override
    public String convert(Object source) {
        return source.toString();
    }
}
