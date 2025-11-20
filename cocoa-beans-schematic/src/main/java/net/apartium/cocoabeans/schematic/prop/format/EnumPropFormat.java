package net.apartium.cocoabeans.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.jetbrains.annotations.ApiStatus;

import java.io.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.apartium.cocoabeans.utils.FileUtils.*;

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
        return constructor.apply(readEnum(in, enumClass, valuesSupplier));
    }

    @Override
    public byte[] encode(BlockProp<?> prop) {
        Object value = prop.value();
        if (value == null || !value.getClass().equals(enumClass))
            throw new IllegalArgumentException("BlockProp excepted " + enumClass.getName() + " and got " + (value == null ? "null" : value.getClass().getName()));

        if (!(value instanceof Enum<?> valueEnum))
            throw new IllegalArgumentException("BlockProp excepted " + enumClass.getName() + " but got " + value.getClass().getName());

        return writeEnum(valueEnum);
    }
}
