package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.Region;
import net.apartium.cocoabeans.space.StrictBoxRegion;
import org.jetbrains.annotations.ApiStatus;

import java.awt.*;

// todo don't like the name + move to space api?
@ApiStatus.AvailableSince("0.0.45")
public record Dimensions(double width, double height, double depth) {

    public Dimensions(Dimensions size) {
        this(size.width, size.height, size.depth);
    }

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
