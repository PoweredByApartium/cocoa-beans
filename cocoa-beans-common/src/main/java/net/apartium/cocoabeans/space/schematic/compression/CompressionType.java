package net.apartium.cocoabeans.space.schematic.compression;

public enum CompressionType {
    RAW((byte) 0),
    ZSTD((byte) 1),
    GZIP((byte) 2);

    private final byte id;

    public static CompressionType fromId(int id){
        for (var t: values())
            if ((t.id & 0xFF) == id)
                return t;

        throw new IllegalArgumentException("bad compression id " + id);
    }

    CompressionType(byte id) {
        this.id = id;
    }

    public byte getId() {
        return id;
    }
}
