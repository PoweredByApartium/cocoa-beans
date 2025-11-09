package net.apartium.cocoabeans.space.axis;

import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.46")
public enum Axis {
    X {
        @Override
        public double getAlong(Position position) {
            return position.getX();
        }

        @Override
        public double getAlong(AreaSize dimensions) {
            return dimensions.width();
        }
    },
    Y {
        @Override
        public double getAlong(Position position) {
            return position.getY();
        }

        @Override
        public double getAlong(AreaSize dimensions) {
            return dimensions.height();
        }
    },
    Z {
        @Override
        public double getAlong(Position position) {
            return position.getZ();
        }

        @Override
        public double getAlong(AreaSize dimensions) {
            return dimensions.depth();
        }
    };

    public abstract double getAlong(Position position);
    public abstract double getAlong(AreaSize dimensions);
}
