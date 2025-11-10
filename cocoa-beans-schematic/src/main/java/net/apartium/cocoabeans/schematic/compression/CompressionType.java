package net.apartium.cocoabeans.schematic.compression;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public enum CompressionType {
    RAW((byte) 0),
    ZSTD((byte) 1),
    GZIP((byte) 2),
    LZMA2((byte) 3),
    OPEN_ZL((byte) 4);

    private final byte id;

    public static CompressionType fromId(int id){
        for (CompressionType type : values())
            if ((type.id & 0xFF) == id)
                return type;

        throw new IllegalArgumentException("bad compression id " + id);
    }

    CompressionType(byte id) {
        this.id = id;
    }

    public byte getId() {
        return id;
    }
}
