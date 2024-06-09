package net.apartium.cocoabeans.structs;

public class SphereRegion implements Region {

    private Position center;
    private double radius;
    private double radiusSquared;

    public SphereRegion(Position center, double radius) {
        this.center = center;
        this.radius = radius;
        this.radiusSquared = radius * radius;
    }

    @Override
    public boolean contains(Position position) {
        return position.distanceSquared(center) <= radiusSquared;
    }

    @Override
    public double distance(Position position) {
        return position.distance(center) - radius;
    }

    public Position getCenter() {
        return center.copy();
    }

    public double getRadius() {
        return radius;
    }

    public void setCenter(Position center) {
        this.center = center.copy();
    }

    public void setRadius(double radius) {
        this.radius = radius;
        this.radiusSquared = radius * radius;
    }

}
