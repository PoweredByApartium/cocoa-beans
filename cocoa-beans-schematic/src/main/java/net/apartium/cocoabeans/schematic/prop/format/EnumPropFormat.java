package net.apartium.cocoabeans.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.jetbrains.annotations.ApiStatus;

import java.io.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.apartium.cocoabeans.utils.BufferUtils.*;

@ApiStatus.AvailableSince("0.0.46")
public abstract class EnumPropFormat<T extends Enum<T>> implements BlockPropFormat<T> {

    private final Class<T> enumClass;
    private final Supplier<T[]> valuesSupplier;
    private final Function<T, BlockProp<T>> constructor;

    protected EnumPropFormat(Class<T> enumClass, Supplier<T[]> valuesSupplier, Function<T, BlockProp<T>> constructor) {
        this.valuesSupplier = valuesSupplier;
        this.constructor = constructor;

        if (enumClass == null || !enumClass.isEnum())
            throw new IllegalArgumentException("Expected enum class!");

        this.enumClass = enumClass;
    }

    @Override
    public BlockProp<T> decode(byte[] value) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(value));
        try {
            return constructor.apply(readEnum(in, enumClass, valuesSupplier));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public byte[] encode(BlockProp<?> prop) {
        Object value = prop.value();
        if (value == null || !value.getClass().equals(enumClass))
            throw new IllegalArgumentException("Excepted instance of " + enumClass.getName() + " but got " + (value == null ? "null" : value.getClass().getName()));

        if (!(value instanceof Enum<?> valueEnum))
            throw new IllegalArgumentException("Excepted instance of " + enumClass.getName() + " but got " + value.getClass().getName());

        try {
            return writeEnum(valueEnum);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
