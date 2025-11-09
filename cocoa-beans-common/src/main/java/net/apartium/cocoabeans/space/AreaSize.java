package net.apartium.cocoabeans.space;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public record AreaSize(double width, double height, double depth) {

    public AreaSize(AreaSize size) {
        this(size.width, size.height, size.depth);
    }

    public static AreaSize box(double length) {
        return new AreaSize(length, length, length);
    }

    public AreaSize floor() {
        return new AreaSize(Math.floor(width), Math.floor(height), Math.floor(depth));
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
