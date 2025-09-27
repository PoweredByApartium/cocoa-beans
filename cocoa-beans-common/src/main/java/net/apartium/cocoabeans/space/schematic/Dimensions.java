package net.apartium.cocoabeans.space.schematic;

public record Dimensions(double width, double height, double depth) {

    public static Dimensions box(double length) {
        return new Dimensions(length, length, length);
    }

    public Dimensions floor() {
        return new Dimensions(Math.floor(width), Math.floor(height), Math.floor(depth));
    }

    public int toArraySize() {
        return (int) Math.floor(width * height * depth);
    }
}
