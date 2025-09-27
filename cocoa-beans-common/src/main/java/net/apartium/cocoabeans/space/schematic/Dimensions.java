package net.apartium.cocoabeans.space.schematic;

import net.apartium.cocoabeans.space.BoxRegion;
import net.apartium.cocoabeans.space.Position;

public record Dimensions(double width, double height, double depth) {

    public static Dimensions box(double length) {
        return new Dimensions(length, length, length);
    }

    public Dimensions floor() {
        return new Dimensions(Math.floor(width), Math.floor(height), Math.floor(depth));
    }

    public BoxRegion toBoxRegion(Position start) {
        return new BoxRegion(
                start,
                new Position(start.getX() + width, start.getY() + height, start.getZ() + depth)
        );
    }

    public int toAreaSize() {
        return (int) Math.floor(width * height * depth);
    }
}
