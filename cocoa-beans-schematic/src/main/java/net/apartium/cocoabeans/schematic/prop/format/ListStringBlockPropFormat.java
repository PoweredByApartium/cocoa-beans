package net.apartium.cocoabeans.schematic.prop.format;

import net.apartium.cocoabeans.schematic.prop.ListStringBlockProp;
import net.apartium.cocoabeans.schematic.prop.BlockProp;
import org.jetbrains.annotations.ApiStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static net.apartium.cocoabeans.utils.FileUtils.readString;

@ApiStatus.AvailableSince("0.0.46")
public class ListStringBlockPropFormat implements BlockPropFormat<List<String>> {

    public static final ListStringBlockPropFormat INSTANCE = new ListStringBlockPropFormat();
    public static final int MAX_LENGTH = (int) (Math.pow(2, 8) - 1);

    private final Function<List<String>, BlockProp<List<String>>> constructor;

    private ListStringBlockPropFormat() {
        this(ListStringBlockProp::new);
    }

    public ListStringBlockPropFormat(Function<List<String>, BlockProp<List<String>>> constructor) {
        this.constructor = constructor;
    }


    @Override
    public BlockProp<List<String>> decode(byte[] value) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(value));

        try {
            List<String> result = new ArrayList<>();
            while (in.available() > 0) {
                int length = in.read();
                result.add(readString(in, length));
            }

            return constructor.apply(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getListStringOrElseThrow(BlockProp<?> prop) {
        Object value = prop.value();
        if (value == null)
            throw new NullPointerException("Prop value is null");

        if (!(value instanceof List<?> rawData))
            throw new IllegalArgumentException("Prop value is wrong type");

        List<String> result = new ArrayList<>();
        for (Object item : rawData) {
            if (!(item instanceof String text))
                throw new IllegalArgumentException("Prop value is wrong type");

            result.add(text);
        }

        return result;
    }

    @Override
    public byte[] encode(BlockProp<?> prop) {
        List<String> list = getListStringOrElseThrow(prop);

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(byteArray);
        try {
            for (String str : list) {
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
