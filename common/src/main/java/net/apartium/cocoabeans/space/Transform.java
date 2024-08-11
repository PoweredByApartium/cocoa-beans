package net.apartium.cocoabeans.space;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

/**
 * Represents a transform in 3D space, combining position and rotation
 * @see Position
 * @see Rotation
 */
@ApiStatus.AvailableSince("0.0.30")
public class Transform {

    private Position position;
    private Rotation rotation;

    /**
     * Create a new transform
     * @param x x
     * @param y y
     * @param z z
     * @param yaw yaw
     * @param pitch pitch
     */
    @JsonCreator
    public Transform(@JsonProperty("x") double x, @JsonProperty("y") double y, @JsonProperty("z") double z,
                     @JsonProperty("yaw") float yaw, @JsonProperty("pitch") float pitch) {
        this.position = new Position(x, y, z);
        this.rotation = new Rotation(yaw, pitch);
    }

    /**
     * Create a new transform
     * @param position position
     * @param rotation rotation
     */
    public Transform(Position position, Rotation rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    /**
     * Create a new transform from another one
     * @param other source
     */
    public Transform(Transform other) {
        this.position = new Position(other.position);
        this.rotation = new Rotation(other.rotation);
    }

    /**
     * Returns the x coordinate
     * @return x
     */
    public double getX() {
        return this.position.getX();
    }

    /**
     * Sets the x coordinate
     * @param x new x
     * @return this
     */
    public Transform setX(double x) {
        this.position.setX(x);
        return this;
    }

    /**
     * Returns the y coordinate
     * @return y
     */
    public double getY() {
        return this.position.getY();
    }

    /**
     * Sets the y coordinate
     * @param y new y
     * @return this
     */
    public Transform setY(double y) {
        this.position.setY(y);
        return this;
    }

    /**
     * Returns the z coordinate
     * @return z
     */
    public double getZ() {
        return this.position.getZ();
    }

    /**
     * Sets the z coordinate
     * @param z new z
     * @return this
     */
    public Transform setZ(double z) {
        this.position.setZ(z);
        return this;
    }

    /**
     * Returns the yaw
     * @return yaw
     */
    public float getYaw() {
        return this.rotation.getYaw();
    }

    /**
     * Sets the yaw
     * @param yaw new yaw
     * @return this
     */
    public Transform setYaw(float yaw) {
        this.rotation.setYaw(yaw);
        return this;
    }

    /**
     * Returns the pitch
     * @return pitch
     */
    public float getPitch() {
        return this.rotation.getPitch();
    }

    /**
     * Sets the pitch
     * @param pitch new pitch
     * @return this
     */
    public Transform setPitch(float pitch) {
        this.rotation.setPitch(pitch);
        return this;
    }

    /**
     * Returns the position
     * @return position
     */
    @JsonIgnore
    public Position getPosition() {
        return position;
    }

    /**
     * Sets the position
     * @param position new position
     * @return this
     */
    public Transform setPosition(Position position) {
        this.position = position;
        return this;
    }

    /**
     * Returns the rotation
     * @return rotation
     */
    @JsonIgnore
    public Rotation getRotation() {
        return rotation;
    }

    /**
     * Sets the rotation
     * @param rotation new rotation
     * @return this
     */
    public Transform setRotation(Rotation rotation) {
        this.rotation = rotation;
        return this;
    }

    /**
     * Translate the transform by a given position
     * @param translation position to translate by
     * @return this
     */
    public Transform translate(Position translation) {
        this.position.add(translation);
        return this;
    }

    /**
     * Rotate the transform by a given rotation
     * @param deltaRotation rotation to rotate by
     * @return this
     */
    public Transform rotate(Rotation deltaRotation) {
        this.rotation.add(deltaRotation);
        return this;
    }

    /**
     * Create a direction vector based on the current rotation
     * @return direction vector
     */
    @JsonIgnore
    public Position getDirectionVector() {
        return Position.fromDirection(rotation.getYaw(), rotation.getPitch());
    }

    @Override
    public String toString() {
        return "Transform(position=" + position + ", rotation=" + rotation + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transform transform = (Transform) o;
        return Objects.equals(position, transform.position) && Objects.equals(rotation, transform.rotation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, rotation);
    }
}