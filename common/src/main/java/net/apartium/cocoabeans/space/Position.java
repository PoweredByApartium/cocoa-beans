package net.apartium.cocoabeans.space;

import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

/**
 * Position in 3D space
 */
@ApiStatus.AvailableSince("0.0.22")
public class Position {

    public static final Position ZERO = new ImmutablePosition(0, 0, 0);
    public static final Position UP = new ImmutablePosition(0, 1, 0);
    public static final Position DOWN = new ImmutablePosition(0, -1, 0);

    private double x;
    private double y;
    private double z;

    /**
     * Create a new position
     * @param x
     * @param y
     * @param z
     */
    public Position(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Create a new position
     * @param other create copy of other
     */
    public Position(Position other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    /**
     * Returns the x coordinate
     * @return x
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the y coordinate
     * @return y
     */
    public double getY() {
        return y;
    }

    /**
     * Returns the z coordinate
     * @return z
     */
    public double getZ() {
        return z;
    }

    /**
     * Sets the x coordinate
     * @param x new x
     * @return this
     */
    public Position setX(double x) {
        this.x = x;

        return this;
    }

    /**
     * Sets the y coordinate
     * @param y new y
     * @return this
     */
    public Position setY(double y) {
        this.y = y;

        return this;
    }

    /**
     * Sets the z coordinate
     * @param z new z
     * @return this
     */
    public Position setZ(double z) {
        this.z = z;

        return this;
    }

    /**
     * Add two positions to current position
     * ```
     * this.x += other.x
     * this.y += other.y
     * this.z += other.z
     * ```
     *
     * @param other other to add
     * @return this + other
     */
    public Position add(Position other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;

        return this;
    }

    /**
     * Subtract other position from this
     * ```
     * this.x -= other.x
     * this.y -= other.y
     * this.z -= other.z
     * ```
     *
     * @param other other to subtract
     * @return this - other
     */
    public Position subtract(Position other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;

        return this;
    }

    /**
     * Multiply position by scalar
     * ```
     * x * scalar
     * y * scalar
     * z * scalar
     * ```
     * @param scalar scalar
     * @return this
     */
    public Position multiply(double scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;

        return this;
    }

    /**
     * Divide position by scalar
     * ```
     * x / scalar
     * y / scalar
     * z / scalar
     * ```
     * @param scalar scalar
     * @return this
     */
    public Position divide(double scalar) {
        x /= scalar;
        y /= scalar;
        z /= scalar;

        return this;
    }

    /**
     * Negate position
     * ```
     * -x
     * -y
     * -z
     * ```
     * @return this
     */
    public Position negate() {
        x = -x;
        y = -y;
        z = -z;

        return this;
    }

    /**
     * Get the dot product of this and other
     * @param other other
     * @return dot product
     */
    public double dot(Position other) {
        return x * other.x + y * other.y + z * other.z;
    }

    /**
     * Get the cross product of this and other
     * @param other other
     * @return cross product
     */
    public Position crossProduct(Position other) {
        return new Position(
                y * other.z - z * other.y,
                z * other.x - x * other.z,
                x * other.y - y * other.x
        );
    }

    /**
     * Get the length of the vector
     * ```
     * sqrt(x * x + y * y + z * z)
     * ```
     * @return length
     */
    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    /**
     * Normalize the vector
     * ```
     * this / length
     * ```
     * @return this
     */
    public Position normalize() {
        double length = length();
        if (length == 0) return this;
        return multiply(1 / length);
    }

    /**
     * Floor the x, y, z coordinates
     * @return this
     */
    public Position floor() {
        x = Math.floor(x);
        y = Math.floor(y);
        z = Math.floor(z);
        return this;
    }

    /**
     * Ceil the x, y, z coordinates
     * @return this
     */
    public Position ceil() {
        x = Math.ceil(x);
        y = Math.ceil(y);
        z = Math.ceil(z);
        return this;
    }

    /**
     * Round the x, y, z coordinates
     * @return this
     */
    public Position round() {
        x = Math.round(x);
        y = Math.round(y);
        z = Math.round(z);
        return this;
    }

    /**
     * Round the x, y, z coordinates
     * @param places number of decimal places
     * @return this
     */
    public Position round(int places) {
        x = Math.round(x * Math.pow(10, places)) / Math.pow(10, places);
        y = Math.round(y * Math.pow(10, places)) / Math.pow(10, places);
        z = Math.round(z * Math.pow(10, places)) / Math.pow(10, places);
        return this;
    }

    /**
     * Get the absolute values of the x, y, z coordinates
     * @return this
     */
    public Position abs() {
        x = Math.abs(x);
        y = Math.abs(y);
        z = Math.abs(z);
        return this;
    }

    /**
     * Get the squared distance between this and other
     * ```
     * (x - other.x) * (x - other.x) + (y - other.y) * (y - other.y) + (z - other.z) * (z - other.z)
     * ```
     * @param other other
     * @return distance squared
     */
    public double distanceSquared(Position other) {
        return (x - other.x) * (x - other.x) + (y - other.y) * (y - other.y) + (z - other.z) * (z - other.z);
    }

    /**
     * Get the distance between this and other
     * @param other other
     * @return distance
     */
    public double distance(Position other) {
        return Math.sqrt(distanceSquared(other));
    }

    /**
     * Create copy of position
     * @return new Instance of position
     */
    public Position copy() {
        return new Position(this);
    }

    /**
     * check if position is zero
     * @return true if position is zero
     */
    public boolean isZero() {
        return x == 0 && y == 0 && z == 0;
    }

    /**
     * Create a new immutable position
     * @return immutable position
     */
    public Position immutable() {
        return new ImmutablePosition(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position other = (Position) obj;
        return x == other.x && y == other.y && z == other.z;
    }

    @Override
    public String toString() {
        return x + " " + y + " " + z;
    }

    /**
     * Get position based on yaw and pitch
     * @param rotX (yaw)
     * @param rotY (pitch)
     * @return position based on yaw and pitch
     */
    public static Position fromDirection(double rotX, double rotY) {
        rotX = Math.toRadians(rotX);
        rotY = Math.toRadians(rotY);

        double xz = Math.cos(rotY);

        return new Position(
                xz * Math.sin(-rotX),
                Math.sin(rotY),
                xz * Math.cos(rotX)
        );

    }
}
