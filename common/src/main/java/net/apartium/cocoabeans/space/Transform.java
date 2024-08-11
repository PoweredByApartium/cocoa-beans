package net.apartium.cocoabeans.space;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a transform in 3D space, combining position and rotation
 */
@ApiStatus.AvailableSince("0.0.23")
public class Transform {

    private Position position;
    private Rotation rotation;

    /**
     * Create a new transform
     * @param position
     * @param rotation
     */
    public Transform(Position position, Rotation rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    /**
     * Create a new transform
     * @param x
     * @param y
     * @param z
     * @param yaw
     * @param pitch
     */
    @JsonCreator
    public Transform(@JsonProperty("x") double x, @JsonProperty("y")double y, @JsonProperty("z")double z,
                     @JsonProperty("yaw") double yaw, @JsonProperty("pitch") double pitch) {
        this.position = new Position(x, y, z);
        this.rotation = new Rotation(yaw, pitch);
    }

    /**
     * Create a new transform
     * @param other create copy of other
     */
    public Transform(Transform other) {
        this.position = new Position(other.position);
        this.rotation = new Rotation(other.rotation);
    }

    /**
     * Returns the position
     * @return position
     */
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
    public Position getDirectionVector() {
        return Position.fromDirection(rotation.getYaw(), rotation.getPitch());
    }

    @Override
    public String toString() {
        return "Transform(position=" + position + ", rotation=" + rotation + ")";
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}