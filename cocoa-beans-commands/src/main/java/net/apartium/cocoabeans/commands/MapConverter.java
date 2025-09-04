package net.apartium.cocoabeans.commands;


import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.44")
public interface MapConverter<T> {

    boolean isSourceTypeSupported(Class<?> clazz);

    Class<T> targetType();

    T convert(Object source);

}