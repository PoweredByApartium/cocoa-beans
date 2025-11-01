package net.apartium.cocoabeans.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;

import java.io.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.apartium.cocoabeans.utils.FileUtils.readString;
import static net.apartium.cocoabeans.utils.FileUtils.writeString;

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
            String name = readString(in);
            int fallbackOrdinal = in.readInt();

            try {
                return constructor.apply(Enum.valueOf(enumClass, name));
            } catch (IllegalArgumentException e) {
                return constructor.apply(valuesSupplier.get()[fallbackOrdinal]);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] encode(BlockProp<?> prop) {
        Object value = prop.value();
        if (value == null || !value.getClass().equals(enumClass))
            throw new IllegalArgumentException("BlockProp excepted " + enumClass.getName() + " and got " + (value == null ? "null" : value.getClass().getName()));

        if (!(value instanceof Enum<?> valueEnum))
            throw new IllegalArgumentException("BlockProp excepted " + enumClass.getName() + " but got " + value.getClass().getName());

        int ordinal = valueEnum.ordinal();
        String name = valueEnum.name();

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteArray);

        try {
            out.write(writeString(name));
            out.writeInt(ordinal);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return byteArray.toByteArray();
    }
}
