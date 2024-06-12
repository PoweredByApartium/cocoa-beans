package net.apartium.cocoabeans.space;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.22")
public class BoxRegion implements Region {

    private double x0, x1;
    private double y0, y1;
    private double z0, z1;

    public BoxRegion(double x0, double x1, double y0, double y1, double z0, double z1) {
        this.x0 = Math.min(x0, x1);
        this.x1 = Math.max(x0, x1);
        this.y0 = Math.min(y0, y1);
        this.y1 = Math.max(y0, y1);
        this.z0 = Math.min(z0, z1);
        this.z1 = Math.max(z0, z1);
    }

    public BoxRegion(Position pos0, Position pos1) {
        this(pos0.getX(), pos1.getX(),
             pos0.getY(), pos1.getY(),
             pos0.getZ(), pos1.getZ()
        );
    }


    @Override
    public boolean contains(Position position) {
        double x = position.getX();
        double y = position.getY();
        double z = position.getZ();

        return x >= x0 && x <= x1
            && y >= y0 && y <= y1
            && z >= z0 && z <= z1;
    }

    @Override
    public double distance(Position position) {
        double x = position.getX();
        double y = position.getY();
        double z = position.getZ();

        double xDist = x < x0 ? x0 - x : x > x1 ? x - x1 : 0;
        double yDist = y < y0 ? y0 - y : y > y1 ? y - y1 : 0;
        double zDist = z < z0 ? z0 - z : z > z1 ? z - z1 : 0;

        return Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
    }

    public double getX0() {
        return x0;
    }

    public double getX1() {
        return x1;
    }

    public double getY0() {
        return y0;
    }

    public double getY1() {
        return y1;
    }

    public double getZ0() {
        return z0;
    }

    public double getZ1() {
        return z1;
    }

    public void setX(double x0, double x1) {
        this.x0 = Math.min(x0, x1);
        this.x1 = Math.max(x0, x1);
    }

    public void setY(double y0, double y1) {
        this.y0 = Math.min(y0, y1);
        this.y1 = Math.max(y0, y1);
    }

    public void setZ(double z0, double z1) {
        this.z0 = Math.min(z0, z1);
        this.z1 = Math.max(z0, z1);
    }

    public void set(Position pos0, Position pos1) {
        this.x0 = Math.min(pos0.getX(), pos1.getX());
        this.x1 = Math.max(pos0.getX(), pos1.getX());
        this.y0 = Math.min(pos0.getY(), pos1.getY());
        this.y1 = Math.max(pos0.getY(), pos1.getY());
        this.z0 = Math.min(pos0.getZ(), pos1.getZ());
        this.z1 = Math.max(pos0.getZ(), pos1.getZ());
    }


}
