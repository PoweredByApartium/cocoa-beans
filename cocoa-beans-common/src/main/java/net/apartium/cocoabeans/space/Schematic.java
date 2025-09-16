package net.apartium.cocoabeans.space;

public interface Schematic {

    Position offset();

    /**
     * x
     * @return width of schematic
     */
    int width();

    /**
     * y
     * @return height of schematic
     */
    int height();

    /**
     * z
     * @return depth of schematic
     */
    int depth();

    BlockData getBlockData(int x, int y, int z);

    Schematic rotate(int rotate);

    Schematic flipX();
    Schematic flipY();
    Schematic flipZ();

    Schematic moveOffset(Position offset);

}