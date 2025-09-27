package net.apartium.cocoabeans.space.schematic;

public interface BlockPropFormat<T> {

    BlockProp<T> decode(byte[] value);
    byte[] encode(BlockProp<?> metaData);

}
