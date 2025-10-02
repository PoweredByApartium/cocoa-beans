package net.apartium.cocoabeans.space.schematic;

import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.Region;
import net.apartium.cocoabeans.space.StrictBoxRegion;

public record Dimensions(double width, double height, double depth) {

    public static Dimensions box(double length) {
        return new Dimensions(length, length, length);
    }

    public Dimensions floor() {
        return new Dimensions(Math.floor(width), Math.floor(height), Math.floor(depth));
    }

    public Region toBoxRegion(Position start) {
        return new StrictBoxRegion(
                start,
                new Position(start.getX() + width, start.getY() + height, start.getZ() + depth)
        );
    }

    public int toAreaSize() {
        return (int) Math.floor(width * height * depth);
    }
}
