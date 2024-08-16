package net.apartium.cocoabeans.space;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

/**
 * Rotation is 3D space
 */
@ApiStatus.AvailableSince("0.0.30")
public class Rotation {

    private float yaw;
    private float pitch;

    /**
     * Create a new rotation
     * @param yaw yaw
     * @param pitch pitch
     */
    @JsonCreator
    public Rotation(@JsonProperty("yaw") float yaw, @JsonProperty("pitch") float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /**
     * Create a new rotation
     * @param other create copy of other
     */
    public Rotation(Rotation other) {
        this.yaw = other.yaw;
        this.pitch = other.pitch;
    }

    /**
     * Returns the yaw
     * @return yaw
     */
    public float getYaw() {
        return yaw;
    }

    /**
     * Sets the yaw
     * @param yaw new yaw
     * @return this
     */
    public Rotation setYaw(float yaw) {
        this.yaw = yaw;
        return this;
    }

    /**
     * Returns the pitch
     * @return pitch
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * Sets the pitch
     * @param pitch new pitch
     * @return this
     */
    public Rotation setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    /**
     * Add another rotation to this rotation
     * @param other rotation to add
     * @return this
     */
    public Rotation add(Rotation other) {
        this.yaw += other.yaw;
        this.pitch += other.pitch;
        return this;
    }

    /**
     * Subtract another rotation from this rotation
     * @param other rotation to subtract
     * @return this
     */
    public Rotation subtract(Rotation other) {
        this.yaw -= other.yaw;
        this.pitch -= other.pitch;
        return this;
    }

    /**
     * Multiply this rotation by a scalar
     * @param scalar value to multiply by
     * @return this
     */
    public Rotation multiply(double scalar) {
        this.yaw *= scalar;
        this.pitch *= scalar;
        return this;
    }

    /**
     * Normalize the rotation angles to be within standard ranges
     * Yaw: [0, 360), Pitch: [-90, 90]
     * @return this
     */
    public Rotation normalize() {
        this.yaw = (this.yaw % 360 + 360) % 360;
        this.pitch = Math.max(-90, Math.min(90, this.pitch));
        return this;
    }

    /**
     * Calculate the dot product of this rotation with another
     * @param other rotation to calculate dot product with
     * @return dot product
     */
    public double dot(Rotation other) {
        return this.yaw * other.yaw + this.pitch * other.pitch;
    }

    /**
     * Calculate the magnitude (length) of this rotation vector
     * @return magnitude
     */
    public double magnitude() {
        return Math.sqrt(yaw * yaw + pitch * pitch);
    }

    @Override
    public String toString() {
        return "Rotation(yaw=" + yaw + ", pitch=" + pitch + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rotation rotation = (Rotation) o;
        return Float.compare(yaw, rotation.yaw) == 0 && Float.compare(pitch, rotation.pitch) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(yaw, pitch);
    }
}
