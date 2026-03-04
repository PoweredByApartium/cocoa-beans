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

    /**
     * Creates a new {@code EnumPropFormat} for the given enum type.
     *
     * @param enumClass      the enum class this format handles
     * @param valuesSupplier a supplier that returns all enum constants (typically {@code MyEnum::values})
     * @param constructor    a function that wraps an enum constant in a {@link BlockProp}
     * @throws IllegalArgumentException if {@code enumClass} is null or not an enum
     */
    protected EnumPropFormat(Class<T> enumClass, Supplier<T[]> valuesSupplier, Function<T, BlockProp<T>> constructor) {
        this.valuesSupplier = valuesSupplier;
        this.constructor = constructor;

        if (enumClass == null || !enumClass.isEnum())
            throw new IllegalArgumentException("Expected enum class!");

        this.enumClass = enumClass;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if the bytes do not represent a valid enum constant or contain unexpected trailing data
     */
    @Override
    public BlockProp<T> decode(byte[] value) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(value));
        try {
            BlockProp<T> prop = constructor.apply(readEnum(in, enumClass, valuesSupplier));
            if (in.available() > 0)
                throw new IllegalArgumentException("Unexpected data after enum");

            return prop;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if the prop value is not an instance of the expected enum class
     * @throws UncheckedIOException if an I/O error occurs during encoding
     */
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
