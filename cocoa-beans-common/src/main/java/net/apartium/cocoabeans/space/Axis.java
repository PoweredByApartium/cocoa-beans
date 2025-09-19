package net.apartium.cocoabeans.space;

public enum Axis {
    X {
        @Override
        double getAlong(Position position) {
            return position.getX();
        }
    },
    Y {
        @Override
        double getAlong(Position position) {
            return position.getY();
        }
    },
    Z {
        @Override
        double getAlong(Position position) {
            return position.getZ();
        }
    };

    abstract double getAlong(Position position);
}
