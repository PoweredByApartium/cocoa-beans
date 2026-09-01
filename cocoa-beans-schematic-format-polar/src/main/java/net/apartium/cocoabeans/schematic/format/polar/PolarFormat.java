package net.apartium.cocoabeans.schematic.format.polar;

import net.apartium.cocoabeans.schematic.*;
import net.apartium.cocoabeans.schematic.block.*;
import net.apartium.cocoabeans.schematic.block.BlockChunkUtils;
import net.apartium.cocoabeans.schematic.format.BodyExtension;
import net.apartium.cocoabeans.schematic.format.SchematicFormat;
import net.apartium.cocoabeans.schematic.iterator.BlockChunkIterator;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.schematic.prop.BlockProp;
import net.apartium.cocoabeans.schematic.prop.StringBlockProp;
import net.apartium.cocoabeans.seekable.SeekableInputStream;
import net.apartium.cocoabeans.seekable.SeekableOutputStream;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import net.apartium.cocoabeans.structs.MinecraftVersion;
import net.apartium.cocoabeans.structs.NamespacedKey;
import org.jetbrains.annotations.ApiStatus;

import java.io.*;
import java.time.Instant;
import java.util.*;

/**
 * {@link SchematicFormat} implementation for the <a href="https://github.com/hollow-cube/polar">Polar</a>
 * world format created by Hollow Cube.
 *
 * <p>Polar is a compact binary world format designed for Minestom. It stores an entire world
 * as chunks with per-section block palettes. This implementation converts between the chunk-based
 * Polar representation and the cocoa-beans {@link Schematic} model by computing a bounding box
 * from the chunk extents and mapping blocks to relative coordinates.</p>
 *
 * <p>Supports Polar format versions 1 through {@link #LATEST_VERSION} (7). Compression may be
 * either raw (none) or Zstd.</p>
 *
 * @param <T> the concrete {@link Schematic} type produced by this format's {@link SchematicFactory}
 */
@ApiStatus.AvailableSince("0.0.46")
public class PolarFormat<T extends Schematic> implements SchematicFormat<T> {

    /** Magic bytes at the start of every Polar file: ASCII "Polr" */
    public static final int MAGIC = 0x506F6C72;

    public static final int LATEST_VERSION = 7;

    // Version constants
    static final int VERSION_UNIFIED_LIGHT = 1;
    static final int VERSION_USERDATA_OPT_BLOCK_ENT_NBT = 2;
    static final int VERSION_MINESTOM_NBT_READ_BREAK = 3;
    static final int VERSION_WORLD_USERDATA = 4;
    static final int VERSION_SHORT_GRASS = 5;
    static final int VERSION_DATA_CONVERTER = 6;
    static final int VERSION_IMPROVED_LIGHT = 7;

    // Compression types
    static final byte COMPRESSION_NONE = 0;
    static final byte COMPRESSION_ZSTD = 1;

    // Section constants
    static final int SECTION_BLOCK_COUNT = 16 * 16 * 16; // 4096
    static final int SECTION_BIOME_COUNT = 4 * 4 * 4;    // 64

    // Light content types
    static final byte LIGHT_MISSING = 0;
    static final byte LIGHT_EMPTY = 1;
    static final byte LIGHT_FULL = 2;
    static final byte LIGHT_PRESENT = 3;

    private final SchematicFactory<T> schematicFactory;
    private final byte compressionType;

    /**
     * Creates a new PolarFormat with Zstd compression for writing.
     *
     * @param schematicFactory factory for creating schematic instances on read
     */
    public PolarFormat(SchematicFactory<T> schematicFactory) {
        this(schematicFactory, COMPRESSION_ZSTD);
    }

    /**
     * Creates a new PolarFormat with the specified compression type for writing.
     *
     * @param schematicFactory factory for creating schematic instances on read
     * @param compressionType  compression type: {@link #COMPRESSION_NONE} or {@link #COMPRESSION_ZSTD}
     */
    public PolarFormat(SchematicFactory<T> schematicFactory, byte compressionType) {
        this.schematicFactory = schematicFactory;
        this.compressionType = compressionType;
    }

    @Override
    public T read(SeekableInputStream in) {
        try {
            DataInputStream din = new DataInputStream(in);

            // Header
            int magic = din.readInt();
            if (magic != MAGIC)
                throw new IOException("Invalid Polar magic: expected 0x" + Integer.toHexString(MAGIC) + ", got 0x" + Integer.toHexString(magic));

            int version = din.readUnsignedShort();
            if (version < VERSION_UNIFIED_LIGHT || version > LATEST_VERSION)
                throw new IOException("Unsupported Polar version: " + version);

            if (version >= VERSION_DATA_CONVERTER)
                readVarInt(din); // data version, unused for schematic conversion

            byte compression = din.readByte();
            int dataLength = readVarInt(din);

            byte[] compressedData = din.readNBytes(readVarInt(din));
            byte[] data;
            if (compression == COMPRESSION_ZSTD) {
                data = ZstdCompressionEngine.INSTANCE.decompress(compressedData);
            } else {
                data = compressedData;
            }

            if (data.length != dataLength)
                throw new IOException("Data length mismatch: expected " + dataLength + ", got " + data.length);

            return readWorldData(data, version);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private T readWorldData(byte[] data, int version) throws IOException {
        DataInputStream din = new DataInputStream(new ByteArrayInputStream(data));

        byte minSection = din.readByte();
        byte maxSection = din.readByte();

        // World-level user data
        if (version >= VERSION_WORLD_USERDATA) {
            int userDataLength = readVarInt(din);
            din.skipNBytes(userDataLength);
        }

        int chunkCount = readVarInt(din);

        // Collect all blocks with absolute world positions
        List<BlockPlacement> worldBlocks = new ArrayList<>();
        int minBlockX = Integer.MAX_VALUE, minBlockY = Integer.MAX_VALUE, minBlockZ = Integer.MAX_VALUE;
        int maxBlockX = Integer.MIN_VALUE, maxBlockY = Integer.MIN_VALUE, maxBlockZ = Integer.MIN_VALUE;

        for (int c = 0; c < chunkCount; c++) {
            int chunkX = readVarInt(din);
            int chunkZ = readVarInt(din);

            int sectionCount = maxSection - minSection + 1;

            for (int s = 0; s < sectionCount; s++) {
                int sectionY = minSection + s;
                boolean isEmpty = din.readBoolean();

                if (isEmpty)
                    continue;

                // Block palette
                int blockPaletteSize = readVarInt(din);
                String[] blockPalette = new String[blockPaletteSize];
                for (int i = 0; i < blockPaletteSize; i++)
                    blockPalette[i] = readString(din);

                int[] blockIndices;
                if (blockPaletteSize > 1) {
                    int blockDataLength = readVarInt(din);
                    long[] blockData = new long[blockDataLength];
                    for (int i = 0; i < blockDataLength; i++)
                        blockData[i] = din.readLong();

                    int bpe = PolarPaletteUtil.bitsPerEntry(blockPaletteSize);
                    blockIndices = PolarPaletteUtil.unpack(blockData, bpe, SECTION_BLOCK_COUNT);
                } else {
                    blockIndices = new int[SECTION_BLOCK_COUNT];
                    // all zeros → index 0
                }

                // Biome palette (read and skip)
                int biomePaletteSize = readVarInt(din);
                for (int i = 0; i < biomePaletteSize; i++)
                    readString(din);

                if (biomePaletteSize > 1) {
                    int biomeDataLength = readVarInt(din);
                    din.skipNBytes((long) biomeDataLength * Long.BYTES);
                }

                // Light data
                readLightData(din, version);
                readLightData(din, version);

                // Parse block palette entries and place blocks
                BlockData[] paletteBlocks = new BlockData[blockPaletteSize];
                for (int i = 0; i < blockPaletteSize; i++)
                    paletteBlocks[i] = parseBlockState(blockPalette[i]);

                int baseX = chunkX * 16;
                int baseY = sectionY * 16;
                int baseZ = chunkZ * 16;

                for (int index = 0; index < SECTION_BLOCK_COUNT; index++) {
                    int paletteIndex = blockIndices[index];
                    BlockData block = paletteBlocks[paletteIndex];
                    if (block == null)
                        continue; // air

                    int localY = index >> 8;
                    int localZ = (index >> 4) & 0xF;
                    int localX = index & 0xF;

                    int worldX = baseX + localX;
                    int worldY = baseY + localY;
                    int worldZ = baseZ + localZ;

                    minBlockX = Math.min(minBlockX, worldX);
                    minBlockY = Math.min(minBlockY, worldY);
                    minBlockZ = Math.min(minBlockZ, worldZ);
                    maxBlockX = Math.max(maxBlockX, worldX);
                    maxBlockY = Math.max(maxBlockY, worldY);
                    maxBlockZ = Math.max(maxBlockZ, worldZ);

                    worldBlocks.add(new BlockPlacement(
                            new Position(worldX, worldY, worldZ),
                            block
                    ));
                }
            }

            // Block entities (read and skip for now)
            int blockEntityCount = readVarInt(din);
            for (int i = 0; i < blockEntityCount; i++)
                readBlockEntity(din, version);

            // Heightmaps
            readHeightmaps(din, maxSection - minSection + 1);

            // Chunk user data
            if (version >= VERSION_USERDATA_OPT_BLOCK_ENT_NBT) {
                int userDataLength = readVarInt(din);
                din.skipNBytes(userDataLength);
            }
        }

        if (worldBlocks.isEmpty()) {
            return schematicFactory.createSchematic(
                    Instant.now(),
                    new MinecraftPlatform(MinecraftVersion.UNKNOWN, "polar", ""),
                    SchematicMetadata.of(),
                    new BlockChunkIterator(BlockChunk.empty()),
                    new AreaSize(0, 0, 0),
                    AxisOrder.XZY,
                    Position.ZERO,
                    Map.of()
            );
        }

        // Build block chunk with relative coordinates (origin at min corner)
        Position offset = new Position(minBlockX, minBlockY, minBlockZ);
        int width = maxBlockX - minBlockX + 1;
        int height = maxBlockY - minBlockY + 1;
        int depth = maxBlockZ - minBlockZ + 1;

        MutableBlockChunk relativeChunk = BlockChunk.empty();
        for (BlockPlacement placement : worldBlocks) {
            Position pos = placement.position();
            Position relPos = new Position(
                    pos.getX() - minBlockX,
                    pos.getY() - minBlockY,
                    pos.getZ() - minBlockZ
            );
            relativeChunk = BlockChunkUtils.rescaleChunkIfNeeded(relativeChunk, AxisOrder.XZY, relPos);
            relativeChunk.setBlock(new BlockPlacement(relPos, placement.block()));
        }

        return schematicFactory.createSchematic(
                Instant.now(),
                new MinecraftPlatform(MinecraftVersion.UNKNOWN, "polar", ""),
                SchematicMetadata.of(),
                new BlockChunkIterator(relativeChunk),
                new AreaSize(width, height, depth),
                AxisOrder.XZY,
                offset,
                Map.of()
        );
    }

    @Override
    public void write(T schematic, SeekableOutputStream out) {
        try {
            byte[] worldData = writeWorldData(schematic);

            byte[] compressed;
            if (compressionType == COMPRESSION_ZSTD) {
                compressed = ZstdCompressionEngine.INSTANCE.compress(worldData);
            } else {
                compressed = worldData;
            }

            DataOutputStream dout = new DataOutputStream(out);
            dout.writeInt(MAGIC);
            dout.writeShort(LATEST_VERSION);
            writeVarInt(dout, 0); // data version
            dout.writeByte(compressionType);
            writeVarInt(dout, worldData.length);
            writeVarInt(dout, compressed.length);
            dout.write(compressed);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private byte[] writeWorldData(T schematic) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);

        Position offset = schematic.offset();
        AreaSize size = schematic.size();

        // Compute chunk extents from the schematic's world-space bounding box
        int worldMinX = (int) offset.getX();
        int worldMinY = (int) offset.getY();
        int worldMinZ = (int) offset.getZ();
        int worldMaxX = worldMinX + (int) size.width() - 1;
        int worldMaxY = worldMinY + (int) size.height() - 1;
        int worldMaxZ = worldMinZ + (int) size.depth() - 1;

        int minChunkX = worldMinX >> 4;
        int maxChunkX = worldMaxX >> 4;
        int minChunkZ = worldMinZ >> 4;
        int maxChunkZ = worldMaxZ >> 4;

        int minSectionY = worldMinY >> 4;
        int maxSectionY = worldMaxY >> 4;

        byte minSection = (byte) minSectionY;
        byte maxSection = (byte) maxSectionY;

        out.writeByte(minSection);
        out.writeByte(maxSection);

        // World user data
        writeVarInt(out, 0);

        int chunkCount = (maxChunkX - minChunkX + 1) * (maxChunkZ - minChunkZ + 1);
        writeVarInt(out, chunkCount);

        int sectionCount = maxSection - minSection + 1;

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                writeVarInt(out, cx);
                writeVarInt(out, cz);

                for (int s = 0; s < sectionCount; s++) {
                    int sectionY = minSection + s;
                    writeSection(out, schematic, cx, sectionY, cz);
                }

                // Block entities
                writeVarInt(out, 0);

                // Heightmaps (empty mask)
                out.writeInt(0);

                // Chunk user data
                writeVarInt(out, 0);
            }
        }

        return baos.toByteArray();
    }

    private void writeSection(DataOutputStream out, T schematic, int chunkX, int sectionY, int chunkZ) throws IOException {
        Position offset = schematic.offset();
        AreaSize size = schematic.size();

        int baseX = chunkX * 16;
        int baseY = sectionY * 16;
        int baseZ = chunkZ * 16;

        // Collect blocks in this section
        List<String> palette = new ArrayList<>();
        Map<String, Integer> paletteMap = new HashMap<>();
        int[] blockIndices = new int[SECTION_BLOCK_COUNT];
        boolean hasNonAir = false;

        // Air is always palette index 0
        palette.add("minecraft:air");
        paletteMap.put("minecraft:air", 0);

        for (int localY = 0; localY < 16; localY++) {
            for (int localZ = 0; localZ < 16; localZ++) {
                for (int localX = 0; localX < 16; localX++) {
                    int worldX = baseX + localX;
                    int worldY = baseY + localY;
                    int worldZ = baseZ + localZ;

                    int sectionIndex = (localY << 8) | (localZ << 4) | localX;

                    // Convert to schematic-relative coords
                    int relX = worldX - (int) offset.getX();
                    int relY = worldY - (int) offset.getY();
                    int relZ = worldZ - (int) offset.getZ();

                    if (relX < 0 || relY < 0 || relZ < 0
                            || relX >= (int) size.width()
                            || relY >= (int) size.height()
                            || relZ >= (int) size.depth()) {
                        blockIndices[sectionIndex] = 0; // air
                        continue;
                    }

                    BlockData block = schematic.getBlockData(relX, relY, relZ);

                    if (block == null) {
                        blockIndices[sectionIndex] = 0; // air
                        continue;
                    }

                    hasNonAir = true;
                    String blockState = formatBlockState(block);

                    Integer paletteIndex = paletteMap.get(blockState);
                    if (paletteIndex == null) {
                        paletteIndex = palette.size();
                        palette.add(blockState);
                        paletteMap.put(blockState, paletteIndex);
                    }

                    blockIndices[sectionIndex] = paletteIndex;
                }
            }
        }

        if (!hasNonAir) {
            out.writeBoolean(true); // isEmpty = true
            return;
        }

        out.writeBoolean(false); // isEmpty = false

        // Block palette
        writeVarInt(out, palette.size());
        for (String entry : palette)
            writeString(out, entry);

        // Block data
        if (palette.size() > 1) {
            int bpe = PolarPaletteUtil.bitsPerEntry(palette.size());
            long[] packed = PolarPaletteUtil.pack(blockIndices, bpe);
            writeVarInt(out, packed.length);
            for (long l : packed)
                out.writeLong(l);
        }

        // Biome palette (single entry = plains)
        writeVarInt(out, 1);
        writeString(out, "minecraft:plains");

        // Block light - missing
        out.writeByte(LIGHT_MISSING);
        // Sky light - missing
        out.writeByte(LIGHT_MISSING);
    }

    // --- Block state parsing ---

    /**
     * Parses a block state string like "minecraft:oak_stairs[facing=north,half=bottom]"
     * into a {@link BlockData}, or returns null for air blocks.
     */
    static BlockData parseBlockState(String blockState) {
        if (blockState.equals("minecraft:air"))
            return null;

        int bracketStart = blockState.indexOf('[');
        String blockId;
        Map<String, BlockProp<?>> props;

        if (bracketStart == -1) {
            blockId = blockState;
            props = Map.of();
        } else {
            blockId = blockState.substring(0, bracketStart);
            String propsStr = blockState.substring(bracketStart + 1, blockState.length() - 1);
            props = parseProps(propsStr);
        }

        int colonIndex = blockId.indexOf(':');
        NamespacedKey key;
        if (colonIndex == -1) {
            key = new NamespacedKey("minecraft", blockId);
        } else {
            key = new NamespacedKey(blockId.substring(0, colonIndex), blockId.substring(colonIndex + 1));
        }

        return new GenericBlockData(key, props);
    }

    private static Map<String, BlockProp<?>> parseProps(String propsStr) {
        Map<String, BlockProp<?>> props = new HashMap<>();
        String[] pairs = propsStr.split(",");
        for (String pair : pairs) {
            int eq = pair.indexOf('=');
            if (eq == -1)
                continue;

            String name = pair.substring(0, eq);
            String value = pair.substring(eq + 1);
            props.put(name, new StringBlockProp(value));
            // TODO actual custom prop handle here
        }
        return props;
    }

    /**
     * Formats a {@link BlockData} into a Polar block state string
     * like "minecraft:stone" or "minecraft:oak_stairs[facing=north,half=bottom]".
     */
    static String formatBlockState(BlockData block) {
        String id = block.type().namespace() + ":" + block.type().key();

        Map<String, BlockProp<?>> props = block.props();
        if (props.isEmpty())
            return id;

        StringBuilder sb = new StringBuilder(id).append('[');
        boolean first = true;
        for (Map.Entry<String, BlockProp<?>> entry : props.entrySet()) {
            if (!first)
                sb.append(',');
            first = false;

            sb.append(entry.getKey()).append('=').append(entry.getValue().value());
        }
        sb.append(']');
        return sb.toString();
    }

    // --- Light data ---

    private void readLightData(DataInputStream din, int version) throws IOException {
        if (version >= VERSION_IMPROVED_LIGHT) {
            byte content = din.readByte();
            if (content == LIGHT_PRESENT)
                din.skipNBytes(2048);
        } else {
            boolean present = din.readBoolean();
            if (present)
                din.skipNBytes(2048);
        }
    }

    // --- Block entities ---

    private void readBlockEntity(DataInputStream din, int version) throws IOException {
        din.readInt(); // packed position

        boolean hasId = din.readBoolean();
        if (hasId)
            readString(din);

        boolean hasNbt = din.readBoolean();
        if (hasNbt)
            skipNbt(din);
    }

    // --- Heightmaps ---

    private void readHeightmaps(DataInputStream din, int sectionCount) throws IOException {
        int mask = din.readInt();

        for (int i = 0; i < 32; i++) {
            if ((mask & (1 << i)) == 0)
                continue;

            int longCount = readVarInt(din);
            din.skipNBytes((long) longCount * Long.BYTES);
        }
    }

    // --- NBT skipping (minimal) ---

    private void skipNbt(DataInputStream din) throws IOException {
        byte type = din.readByte();
        if (type == 0) // TAG_End
            return;

        skipNbtPayload(din, type);
    }

    private void skipNbtPayload(DataInputStream din, byte type) throws IOException {
        switch (type) {
            case 0 -> {} // TAG_End
            case 1 -> din.skipNBytes(1);  // TAG_Byte
            case 2 -> din.skipNBytes(2);  // TAG_Short
            case 3 -> din.skipNBytes(4);  // TAG_Int
            case 4 -> din.skipNBytes(8);  // TAG_Long
            case 5 -> din.skipNBytes(4);  // TAG_Float
            case 6 -> din.skipNBytes(8);  // TAG_Double
            case 7 -> { // TAG_Byte_Array
                int len = din.readInt();
                din.skipNBytes(len);
            }
            case 8 -> { // TAG_String
                int len = din.readUnsignedShort();
                din.skipNBytes(len);
            }
            case 9 -> { // TAG_List
                byte listType = din.readByte();
                int len = din.readInt();
                for (int i = 0; i < len; i++)
                    skipNbtPayload(din, listType);
            }
            case 10 -> { // TAG_Compound
                while (true) {
                    byte childType = din.readByte();
                    if (childType == 0) break; // TAG_End
                    int nameLen = din.readUnsignedShort();
                    din.skipNBytes(nameLen);
                    skipNbtPayload(din, childType);
                }
            }
            case 11 -> { // TAG_Int_Array
                int len = din.readInt();
                din.skipNBytes((long) len * 4);
            }
            case 12 -> { // TAG_Long_Array
                int len = din.readInt();
                din.skipNBytes((long) len * 8);
            }
            default -> throw new IOException("Unknown NBT type: " + type);
        }
    }

    // --- VarInt I/O ---

    static int readVarInt(DataInput in) throws IOException {
        int value = 0;
        int position = 0;
        byte currentByte;

        while (true) {
            currentByte = in.readByte();
            value |= (currentByte & 0x7F) << position;

            if ((currentByte & 0x80) == 0)
                break;

            position += 7;
            if (position >= 32)
                throw new IOException("VarInt is too big");
        }

        return value;
    }

    static void writeVarInt(DataOutputStream out, int value) throws IOException {
        while (true) {
            if ((value & ~0x7F) == 0) {
                out.writeByte(value);
                return;
            }

            out.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
    }

    private static String readString(DataInputStream din) throws IOException {
        int length = readVarInt(din);
        byte[] bytes = new byte[length];
        din.readFully(bytes);
        return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
    }

    private static void writeString(DataOutputStream out, String value) throws IOException {
        byte[] bytes = value.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        writeVarInt(out, bytes.length);
        out.write(bytes);
    }

}