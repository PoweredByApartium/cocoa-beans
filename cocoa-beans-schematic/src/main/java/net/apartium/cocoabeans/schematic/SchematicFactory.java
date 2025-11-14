package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

import java.time.Instant;
import java.util.Map;

/**
 * Platform specific schematic factory
 * @param <S> schematic tpye
 */
@NullMarked
@ApiStatus.AvailableSince("0.0.46")
public interface SchematicFactory<S extends Schematic> {

    /**
     * Creates an empty schematic builder
     * @return a new builder instance
     */
    SchematicBuilder<S> createSchematic();

    /**
     * Creates a schematic with specified values
     * @param created created at
     * @param platform platform
     * @param metadata metadata
     * @param blocks content
     * @param size size
     * @param axisOrder axis order
     * @param offset offset
     * @return schematic instance
     */
    S createSchematic(Instant created, MinecraftPlatform platform, SchematicMetadata metadata, BlockIterator blocks, AreaSize size, AxisOrder axisOrder, Position offset);

    /**
     * Create a metadata by its content
     * @param metadata metadata
     * @return a new schematic metadata instance
     */
    SchematicMetadata createMetadata(Map<String, Object> metadata);

}
