package net.apartium.cocoabeans.commands;


import org.jetbrains.annotations.ApiStatus;

import java.util.List;

/**
 * Allows defining custom types in sub commands that will be converted before execution.
 * For example, if a sub command uses string parser but would like to receive the string argument as a buffer instead, we can create an argument converter to handle that.
 * The sub command will be able to use the standard string parser but receive an argument buffer instead.
 * @param <T>
 * @see SimpleArgumentMapper#SimpleArgumentMapper(List)
 */
@ApiStatus.AvailableSince("0.0.44")
public interface ArgumentConverter<T> {

    /**
     * Is argument source type supported by this converter.
     * If an argument is supported, the {@link ArgumentConverter#convert(Object)} method should be able to convert it into {@link ArgumentConverter#targetType()}
     * @param clazz argument source type, cannot be null
     * @return true if supported, else false
     */
    boolean isSourceTypeSupported(Class<?> clazz);

    /**
     * Target type of this converter.
     * @return target type
     */
    Class<T> targetType();

    /**
     * Convert source object into target type.
     * It is guaranteed that objects provided were already checked for source type support.
     * @param source source object
     * @return object of target type
     */
    T convert(Object source);

}