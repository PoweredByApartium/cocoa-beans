package net.apartium.cocoabeans.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.ArrayStringBlockProp;
import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.jetbrains.annotations.ApiStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static net.apartium.cocoabeans.utils.FileUtils.readString;

@ApiStatus.AvailableSince("0.0.46")
public class StringArrayBlockPropFormat implements BlockPropFormat<String[]> {

    public static final int MAX_LENGTH = (int) (Math.pow(2, 8) - 1);

    @Override
    public ArrayStringBlockProp decode(byte[] value) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(value));

        try {
            String[] result = new String[in.readUnsignedByte()];
            for (int i = 0; i < result.length; i++) {
                int length = in.read();
                result[i] = readString(in, length);
            }

            return new ArrayStringBlockProp(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] encode(BlockProp<?> prop) {
        if (!(prop.value() instanceof String[] arr))
            throw new IllegalArgumentException("BlockProp expected String[] and got " + prop.value().getClass());

        if (arr.length > MAX_LENGTH)
            throw new IllegalArgumentException("BlockProp array length exceeds " + MAX_LENGTH);

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteArray);
        try {
            out.writeByte(arr.length);

            for (String str : arr) {
                byte[] data = str.getBytes(StandardCharsets.UTF_8);
                if (data.length > MAX_LENGTH)
                    throw new IllegalArgumentException("BlockProp text too long!\nExpected " + MAX_LENGTH + " bytes, got " + str.length());

                out.writeByte(data.length);
                out.write(data);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return byteArray.toByteArray();
    }

}
