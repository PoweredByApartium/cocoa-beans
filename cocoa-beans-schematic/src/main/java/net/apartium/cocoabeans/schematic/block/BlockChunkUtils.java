package net.apartium.cocoabeans.schematic.block;

import net.apartium.cocoabeans.Mathf;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class BlockChunkUtils {

    public static MutableBlockChunk rescaleChunkIfNeeded(MutableBlockChunk chunk, AxisOrder axes, Position pos) {
        int maxAxis = (int) Math.max(pos.getX(), Math.max(pos.getY(), pos.getZ()));

        if (maxAxis >= chunk.getScaler())
            return new MutableBlockChunkImpl(
                    axes,
                    Mathf.nextPowerOfFour(maxAxis) * 4,
                    Position.ZERO,
                    Position.ZERO,
                    chunk
            );

        return chunk;
    }

}
