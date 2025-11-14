package net.apartium.cocoabeans.schematic;

import net.apartium.cocoabeans.schematic.block.BlockData;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.space.axis.Axis;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import java.time.Instant;
import java.util.function.Function;

/**
 * Schematic builder
 * @see Schematic
 * @see SchematicFactory#createSchematic()
 * @param <T>
 */
@NullMarked
@ApiStatus.AvailableSince("0.0.46")
public interface SchematicBuilder<T extends Schematic> {

    /**
     * Specifies schematic platform
     * @param platform platform
     * @return this builder instance
     */
    SchematicBuilder<T> platform(MinecraftPlatform platform);

    /**
     * Specifies schematic creation date
     * @param created creation date
     * @return this builder instance
     */
    SchematicBuilder<T> created(Instant created);

    /**
     * Specifies metadata
     * @param metadata metadata
     * @return this builder instance
     */
    SchematicBuilder<T> metadata(SchematicMetadata metadata);

    /**
     * Specifies metadata
     * @param block metadata construction block
     * @return this builder instance
     */
    SchematicBuilder<T> metadata(Function<SchematicMetadataBuilder<?>, SchematicMetadata> block);

    /**
     * Specifies schematic size
     * @param size schematic size
     * @return this builder instance
     */
    SchematicBuilder<T> size(AreaSize size);

    /**
     * Rotate the existing schematic content
     * @param degrees degrees to rotate by
     * @return this builder instance
     */
    SchematicBuilder<T> rotate(int degrees);

    /**
     * Flip the existing schematic content
     * @param axis axis to rotate on
     * @return this builder instance
     */
    SchematicBuilder<T> flip(@NotNull Axis axis);

    /**
     * Translate the schematic offset
     * @param offset offset
     * @return this builder instance
     */
    SchematicBuilder<T> translate(Position offset);

    /**
     * Translate the schematic offset
     * @param axisOrder axisOrder
     * @return this builder instance
     */
    SchematicBuilder<T> translate(AxisOrder axisOrder);

    /**
     * todo kfir tf is that
     * @param axis
     * @param amount
     * @return this builder instance
     */
    SchematicBuilder<T> shift(Axis axis, int amount);

    /**
     * Set a block on the schematic
     * @param placement block data
     * @return this builder instance
     */
    SchematicBuilder<T> setBlock(BlockPlacement placement);

    /**
     * Set a block on the schematic
     * @param x block x
     * @param y block y
     * @param z block z
     * @param data block data
     * @return this builder instance
     */
    SchematicBuilder<T> setBlock(int x, int y, int z, BlockData data);

    /**
     * Remove a block from the schematic
     * @param x block x
     * @param y block y
     * @param z block z
     * @return this builder instance
     */
    SchematicBuilder<T> removeBlock(int x, int y, int z);

    /**
     * Build a new instance of schematic with this builder's data
     * @return a new schematic instance
     */
    T build();

}
