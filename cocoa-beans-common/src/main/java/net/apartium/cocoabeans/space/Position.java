package net.apartium.cocoabeans.space;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

/**
 * Represents a mutable position in 3D space with double precision coordinates (x, y, z).
 * <p>
 * Provides a variety of vector operations, arithmetic, and utility methods for manipulating positions,
 * including addition, subtraction, multiplication, division, normalization, rounding, flooring, ceiling,
 * and conversion to immutable positions. Also supports distance calculations, dot and cross products,
 * and directional calculations based on yaw and pitch.
 * </p>
 * <p>
 * Common constants such as {@code ZERO}, {@code UP}, and {@code DOWN} are provided for convenience.
 * </p>
 * <p>
 * This class is available since version 0.0.22.
 * </p>
 */
@ApiStatus.AvailableSince("0.0.22")
public class Position {

    /**
     * Position at (0, 0, 0).
     */
    public static final Position ZERO = new ImmutablePosition(0, 0, 0);
    /**
     * Position at (0, 1, 0).
     */
    public static final Position UP = new ImmutablePosition(0, 1, 0);
    /**
     * Position at (0, -1, 0).
     */
    public static final Position DOWN = new ImmutablePosition(0, -1, 0);

    /**
     * The x coordinate.
     */
    private double x;
    /**
     * The y coordinate.
     */
    private double y;
    /**
     * The z coordinate.
     */
    private double z;

    /**
     * Create a new position with the given coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    @JsonCreator
    @ApiStatus.AvailableSince("0.0.30")
    public Position(@JsonProperty("x") double x, @JsonProperty("y") double y, @JsonProperty("z") double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    /**
     * Create a new position by copying another position.
     * @param other the position to copy
     */
    public Position(Position other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public static Position min(Position a, Position b) {
        return new Position(
                Math.min(a.getX(), b.getX()),
                Math.min(a.getY(), b.getY()),
                Math.min(a.getZ(), b.getZ())
        );
    }
    /**
     * Returns the maximum of two positions for each coordinate.
     * @param a first position
     * @param b second position
     * @return position with maximum x, y, z
     */
    public static Position max(Position a, Position b) {
        return new Position(
                Math.max(a.getX(), b.getX()),
                Math.max(a.getY(), b.getY()),
                Math.max(a.getZ(), b.getZ())
        );
    }

    /**
     * Returns the x coordinate.
     * @return x
     */
    public double getX() {
        return x;
    }
    /**
     * Returns the y coordinate.
     * @return y
     */
    public double getY() {
        return y;
    }
    /**
     * Returns the z coordinate.
     * @return z
     */
    public double getZ() {
        return z;
    }

    /**
     * Sets the x coordinate.
     * @param x new x
     * @return this
     */
    public Position setX(double x) {
        this.x = x;
        return this;
    }
    /**
     * Sets the y coordinate.
     * @param y new y
     * @return this
     */
    public Position setY(double y) {
        this.y = y;
        return this;
    }
    /**
     * Sets the z coordinate.
     * @param z new z
     * @return this
     */
    public Position setZ(double z) {
        this.z = z;
        return this;
    }

    /**
     * Adds another position to this position.
     * @param other the position to add
     * @return this after addition
     */
    public Position add(Position other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
    }
    /**
     * Subtracts another position from this position.
     * @param other the position to subtract
     * @return this after subtraction
     */
    public Position subtract(Position other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        return this;
    }
    /**
     * Multiplies this position by a scalar.
     * @param scalar the scalar value
     * @return this after multiplication
     */
    public Position multiply(double scalar) {
        x *= scalar;
        y *= scalar;
        z *= scalar;
        return this;
    }
    /**
     * Divides this position by a scalar.
     * @param scalar the scalar value
     * @return this after division
     */
    public Position divide(double scalar) {
        x /= scalar;
        y /= scalar;
        z /= scalar;
        return this;
    }
    /**
     * Negates this position.
     * @return this after negation
     */
    public Position negate() {
        x = -x;
        y = -y;
        z = -z;
        return this;
    }
    /**
     * Computes the dot product with another position.
     * @param other the other position
     * @return dot product
     */
    public double dot(Position other) {
        return x * other.x + y * other.y + z * other.z;
    }
    /**
     * Computes the cross product with another position.
     * @param other the other position
     * @return cross product position
     */
    public Position crossProduct(Position other) {
        return new Position(
                y * other.z - z * other.y,
                z * other.x - x * other.z,
                x * other.y - y * other.x
        );
    }
    /**
     * Returns the length (magnitude) of this position vector.
     * @return length
     */
    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }
    /**
     * Normalizes this position vector.
     * @return this after normalization
     */
    public Position normalize() {
        double length = length();
        if (length == 0) return this;
        return multiply(1 / length);
    }
    /**
     * Floors the x, y, z coordinates.
     * @return this after flooring
     */
    public Position floor() {
        x = Math.floor(x);
        y = Math.floor(y);
        z = Math.floor(z);
        return this;
    }
    /**
     * Ceils the x, y, z coordinates.
     * @return this after ceiling
     */
    public Position ceil() {
        x = Math.ceil(x);
        y = Math.ceil(y);
        z = Math.ceil(z);
        return this;
    }
    /**
     * Rounds the x, y, z coordinates.
     * @return this after rounding
     */
    public Position round() {
        x = Math.round(x);
        y = Math.round(y);
        z = Math.round(z);
        return this;
    }
    /**
     * Rounds the x, y, z coordinates to the specified number of decimal places.
     * @param places number of decimal places
     * @return this after rounding
     */
    public Position round(int places) {
        x = Math.round(x * Math.pow(10, places)) / Math.pow(10, places);
        y = Math.round(y * Math.pow(10, places)) / Math.pow(10, places);
        z = Math.round(z * Math.pow(10, places)) / Math.pow(10, places);
        return this;
    }
    /**
     * Sets the x, y, z coordinates to their absolute values.
     * @return this after applying abs
     */
    public Position abs() {
        x = Math.abs(x);
        y = Math.abs(y);
        z = Math.abs(z);
        return this;
    }
    /**
     * Returns the squared distance between this and another position.
     * @param other the other position
     * @return squared distance
     */
    public double distanceSquared(Position other) {
        return (x - other.x) * (x - other.x) + (y - other.y) * (y - other.y) + (z - other.z) * (z - other.z);
    }
    /**
     * Returns the distance between this and another position.
     * @param other the other position
     * @return distance
     */
    public double distance(Position other) {
        return Math.sqrt(distanceSquared(other));
    }
    /**
     * Creates a copy of this position.
     * @return new instance of position
     */
    public Position copy() {
        return new Position(this);
    }
    /**
     * Checks if this position is zero (all coordinates are 0).
     * @return true if position is zero
     */
    @JsonIgnore
    public boolean isZero() {
        return x == 0 && y == 0 && z == 0;
    }
    /**
     * Returns an immutable version of this position.
     * @return immutable position
     */
    public Position immutable() {
        return new ImmutablePosition(this);
    }
    /**
     * Returns a rotation based on the direction to a target position.
     * @param target target position
     * @return rotation to look at target
     */
    @ApiStatus.AvailableSince("0.0.30")
    public Rotation lookAt(Position target) {
        double dx = target.x - this.x;
        double dy = target.y - this.y;
        double dz = target.z - this.z;
        double yaw = Math.atan2(dx, dz);
        double pitch = Math.atan2(dy, Math.sqrt(dx * dx + dz * dz));
        yaw = Math.toDegrees(yaw);
        pitch = Math.toDegrees(pitch);
        return new Rotation((float) yaw, (float) pitch);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (!(obj instanceof Position other))
            return false;

        return x == other.x && y == other.y && z == other.z;
    }
    @Override
    public String toString() {
        return x + " " + y + " " + z;
    }
    /**
     * Returns a position based on yaw and pitch angles.
     * @param rotX yaw angle in degrees
     * @param rotY pitch angle in degrees
     * @return position based on direction
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
    /**
     * Adds two positions and returns a new position.
     * @param a first position
     * @param b second position
     * @return sum of positions
     */
    public static Position add(Position a, Position b) {
        return new Position(
                a.getX() + b.getX(),
                a.getY() + b.getY(),
                a.getZ() + b.getZ()
        );
    }
    /**
     * Multiplies a position by a long value and returns a new position.
     * @param position the position
     * @param value the scalar value
     * @return multiplied position
     */
    public static Position multiply(Position position, long value) {
        return new Position(
                position.getX() * value,
                position.getY() * value,
                position.getZ() * value
        );
    }
}
