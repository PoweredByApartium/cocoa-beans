package net.apartium.cocoabeans.structs;

/**
 * Immutable version of Position
 * When the position is modified, a new Position is created
 * and the original one is left unchanged
 */
public class ImmutablePosition extends Position{

    public ImmutablePosition(double x, double y, double z) {
        super(x, y, z);
    }

    public ImmutablePosition(Position other) {
        super(other);
    }

    @Override
    public Position setX(double x) {
        throw new UnsupportedOperationException("ImmutablePosition cannot be modified.");
    }

    @Override
    public Position setY(double y) {
        throw new UnsupportedOperationException("ImmutablePosition cannot be modified.");
    }

    @Override
    public Position setZ(double z) {
        throw new UnsupportedOperationException("ImmutablePosition cannot be modified.");
    }

    public Position add(Position other) {
        throw new UnsupportedOperationException("ImmutablePosition cannot be modified.");
    }

    public Position subtract(Position other) {
        throw new UnsupportedOperationException("ImmutablePosition cannot be modified.");
    }

    public Position multiply(double scalar) {
        throw new UnsupportedOperationException("ImmutablePosition cannot be modified.");
    }

    public Position divide(double scalar) {
        throw new UnsupportedOperationException("ImmutablePosition cannot be modified.");
    }

    @Override
    public Position crossProduct(Position other) {
        throw new UnsupportedOperationException("ImmutablePosition cannot be modified.");
    }

    @Override
    public Position floor() {
        throw new UnsupportedOperationException("ImmutablePosition cannot be modified.");
    }

    @Override
    public Position ceil() {
        throw new UnsupportedOperationException("ImmutablePosition cannot be modified.");
    }

    @Override
    public Position round() {
        throw new UnsupportedOperationException("ImmutablePosition cannot be modified.");
    }

    @Override
    public Position round(int places) {
        throw new UnsupportedOperationException("ImmutablePosition cannot be modified.");
    }

    @Override
    public Position abs() {
        throw new UnsupportedOperationException("ImmutablePosition cannot be modified.");
    }

    @Override
    public Position negate() {
        throw new UnsupportedOperationException("ImmutablePosition cannot be modified.");
    }

    @Override
    public Position normalize() {
        throw new UnsupportedOperationException("ImmutablePosition cannot be modified.");
    }
}
