package net.apartium.cocoabeans.schematic.axis;

import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.schematic.Dimensions;
import org.jetbrains.annotations.ApiStatus;

// todo move to space?
@ApiStatus.AvailableSince("0.0.45")
public enum Axis {
    X {
        @Override
        public double getAlong(Position position) {
            return position.getX();
        }

        @Override
        public double getAlong(Dimensions dimensions) {
            return dimensions.width();
        }
    },
    Y {
        @Override
        public double getAlong(Position position) {
            return position.getY();
        }

        @Override
        public double getAlong(Dimensions dimensions) {
            return dimensions.height();
        }
    },
    Z {
        @Override
        public double getAlong(Position position) {
            return position.getZ();
        }

        @Override
        public double getAlong(Dimensions dimensions) {
            return dimensions.depth();
        }
    };

    public abstract double getAlong(Position position);
    public abstract double getAlong(Dimensions dimensions);
}
