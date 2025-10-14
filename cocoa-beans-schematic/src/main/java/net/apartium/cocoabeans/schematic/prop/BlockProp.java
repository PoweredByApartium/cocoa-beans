package net.apartium.cocoabeans.schematic.prop;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

@ApiStatus.AvailableSince("0.0.45")
public interface BlockProp<T> {

    public static class Legacy {
        public static final String DATA = "LEGACY_DATA";
        public static final String SIGN_LINES = "LEGACY_SIGN_LINES";
    }

    Function<Byte, ByteBlockProp> BYTE = ByteBlockProp::new;
    Function<String, StringBlockProp> STRING = StringBlockProp::new;
    Function<String[], ArrayStringBlockProp> ARRAY_STRING = ArrayStringBlockProp::new;

    T value();

}
