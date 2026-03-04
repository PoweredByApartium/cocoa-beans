package net.apartium.cocoabeans.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.StringBlockProp;
import org.jetbrains.annotations.ApiStatus;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import static net.apartium.cocoabeans.utils.BufferUtils.readString;

@ApiStatus.AvailableSince("0.0.46")
public class StringBlockPropFormat implements BlockPropFormat<String> {

    public static final int MAX_LENGTH = (int) (Math.pow(2, 8) - 1);

    /**
     * {@inheritDoc}
     *
     * <p>The binary layout is: a 1-byte unsigned length, followed by that many UTF-8 bytes.</p>
     *
     * @throws UncheckedIOException if an I/O error occurs while reading
     */
    @Override
    public StringBlockProp decode(byte[] value) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(value));

        try {
            int length = in.readUnsignedByte();
            return new StringBlockProp(readString(in, length));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>The binary layout is: a 1-byte unsigned length, followed by that many UTF-8 bytes.</p>
     *
     * @throws IllegalArgumentException if the prop value is not a {@link String}, or if the
     *                                  UTF-8 encoding exceeds {@value #MAX_LENGTH} bytes
     */
    @Override
    public byte[] encode(BlockProp<?> prop) {
        if (!(prop.value() instanceof String text))
            throw new IllegalArgumentException("BlockProp expected String and got " + prop.value().getClass().getName());

        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        if (data.length > MAX_LENGTH)
            throw new IllegalArgumentException("BlockProp text too long!\nExpected " + MAX_LENGTH + " bytes, got " + text.length());

        byte[] result = new byte[data.length + 1];
        result[0] = (byte) data.length;
        System.arraycopy(data, 0, result, 1, data.length);

        return result;
    }

}
