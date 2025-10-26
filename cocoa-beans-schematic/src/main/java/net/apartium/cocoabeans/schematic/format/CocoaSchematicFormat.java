package net.apartium.cocoabeans.schematic.format;

import net.apartium.cocoabeans.Mathf;
import net.apartium.cocoabeans.schematic.block.BlockPlacement;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.Dimensions;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.schematic.*;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.compression.CompressionEngine;
import net.apartium.cocoabeans.schematic.utils.ByteArraySeekableChannel;
import net.apartium.cocoabeans.schematic.utils.SeekableInputStream;
import net.apartium.cocoabeans.schematic.utils.SeekableOutputStream;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import net.apartium.cocoabeans.structs.MinecraftVersion;
import org.jetbrains.annotations.ApiStatus;

import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.apartium.cocoabeans.schematic.utils.FileUtils.*;

@ApiStatus.AvailableSince("0.0.45")
public class CocoaSchematicFormat implements SchematicFormat {

    public static final String FINGERPRINT = "CBSC";
    public static final int FINGERPRINT_SIZE = 4;
    public final int VERSION = 1;
    public static final long HEADER_START_INDEX = 9;

    public static class Headers {

        public static final int ID = 0x01;
        public static final int AXIS_ORDER = 0x02;
        public static final int OFFSET = 0x03;
        public static final int SIZE = 0x04;
        public static final int BLOCK_DATA_ENCODING = 0x05;
        public static final int BLOCK_INFO = 0x06;
        public static final int INDEX_ENCODING = 0x07;
        public static final int INDEX_INFO = 0x08;
        public static final int TIMESTAMP = 0x09;
        public static final int AUTHOR = 0x0A;
        public static final int TITLE = 0x0B;
        public static final int PLATFORM = 0x0C;

    }

    private final Map<Integer, BlockDataEncoder> blockDataEncoderMap;
    private final Map<Integer, IndexEncoder> indexEncoderMap;
    private final Map<Byte, CompressionEngine> compressionEngines;

    private CompressionEngine defaultCompressionEngineForBlocks;
    private CompressionEngine defaultCompressionEngineForIndexes;
    private Map.Entry<Integer, BlockDataEncoder> preferBlockEncoder;
    private Map.Entry<Integer, IndexEncoder> preferIndexEncoder;
    private final SchematicFactory<?> schematicFactory;

    public CocoaSchematicFormat(Map<Integer, BlockDataEncoder> blockDataEncoderMap, Map<Integer, IndexEncoder> indexEncoderMap, Set<CompressionEngine> compressionEngines, byte defaultCompressionForBlock, byte defaultCompressionForIndexes, SchematicFactory<?> schematicFactory) {
        this.blockDataEncoderMap = new HashMap<>(blockDataEncoderMap);
        if (!blockDataEncoderMap.isEmpty())
            preferBlockEncoder = blockDataEncoderMap.entrySet().iterator().next();

        this.indexEncoderMap = new HashMap<>(indexEncoderMap);
        if (!indexEncoderMap.isEmpty())
            preferIndexEncoder = indexEncoderMap.entrySet().iterator().next();

        this.compressionEngines = compressionEngines.stream().collect(Collectors.toMap(CompressionEngine::type, Function.identity()));

        this.defaultCompressionEngineForBlocks = this.compressionEngines.get(defaultCompressionForBlock);
        this.defaultCompressionEngineForIndexes = this.compressionEngines.get(defaultCompressionForIndexes);

        this.schematicFactory = schematicFactory;
    }

    public void registerBlockEncoder(int id, BlockDataEncoder encoder) {
        blockDataEncoderMap.put(id, encoder);
    }

    public void registerIndexEncoder(int id, IndexEncoder encoder) {
        indexEncoderMap.put(id, encoder);
    }

    public void setPreferBlockEncoder(int id, BlockDataEncoder encoder) {
        preferBlockEncoder = Map.entry(id, encoder);
    }

    public void setPreferIndexEncoder(int id, IndexEncoder encoder) {
        preferIndexEncoder = Map.entry(id, encoder);
    }

    public void registerCompressionEngine(byte id, CompressionEngine engine) {
        compressionEngines.put(id, engine);
    }

    public void setDefaultCompressionEngineForBlocks(CompressionEngine engine) {
        defaultCompressionEngineForBlocks = engine;
    }

    public void setDefaultCompressionEngineForIndexes(CompressionEngine engine) {
        defaultCompressionEngineForIndexes = engine;
    }

    @Override
    public void write(Schematic schematic, SeekableOutputStream out) {
        try {
            for (int i = 0; i < FINGERPRINT_SIZE; i++)
                out.write(FINGERPRINT.charAt(i));

            out.write(writeU16(VERSION));

            Map.Entry<Integer, BlockDataEncoder> blockEncoder = preferBlockEncoder;
            Map.Entry<Integer, IndexEncoder> indexEncoder = preferIndexEncoder;

            HeaderResult headerResult = headers(schematic, blockEncoder, indexEncoder);
            byte[] headers = headerResult.data;
            out.write(writeU24(headers.length));
            out.write(headers);

            BlockIterator iterator = schematic.blocksIterator();
            Map<BlockData, Long> blockIndexes = new IdentityHashMap<>();
            ByteArrayOutputStream blockOut = new ByteArrayOutputStream();
            long offset = 0;

            final AxisOrder axes = schematic.axisOrder();

            Dimensions dimensions = schematic.size();

            long maxDimensions = (long) Math.max(dimensions.width(), Math.max(dimensions.height(), dimensions.depth()));
            long scaler = Mathf.nextPowerOfFour(maxDimensions);
            BlockChunk blockChunk = new BlockChunk(axes, scaler, Position.ZERO, Position.ZERO);

            while (iterator.hasNext()) {
                BlockPlacement placement = iterator.next();
                BlockData blockData = placement.block();
                blockChunk.setBlock(placement);

                if (blockIndexes.containsKey(blockData))
                    continue;

                blockIndexes.put(blockData, offset);

                byte[] data = blockEncoder.getValue().write(blockData);
                blockOut.write(data);

                offset += data.length;
            }

            byte[] originalBlocksData = blockOut.toByteArray();
            byte[] compressed = defaultCompressionEngineForBlocks.compress(originalBlocksData);

            CompressionBlockInfo blockInfo = new CompressionBlockInfo(
                    defaultCompressionEngineForBlocks.type(),
                    originalBlocksData.length,
                    compressed.length,
                    out.position(),
                    crc32(originalBlocksData)
            );

            long blockStartIndex = HEADER_START_INDEX + headerResult.offsetBlockInfo;
            out.position(blockStartIndex);
            out.write(blockInfo.toByteArray());

            out.position(blockInfo.offset());
            out.write(compressed);


            byte[] indexesAsBytes = indexEncoder.getValue().write(schematic.blocksIterator(), schematic.axisOrder(), blockIndexes);
            compressed = defaultCompressionEngineForIndexes.compress(indexesAsBytes);

            CompressionBlockInfo indexInfo = new CompressionBlockInfo(
                    defaultCompressionEngineForIndexes.type(),
                    indexesAsBytes.length,
                    compressed.length,
                    out.position(),
                    crc32(indexesAsBytes)
            );

            long indexStartIndex = HEADER_START_INDEX + headerResult.offsetIndexInfo;
            out.position(indexStartIndex);
            out.write(indexInfo.toByteArray());

            out.position(indexInfo.offset());
            out.write(compressed);

            // TODO add additional body here
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private record HeaderResult(byte[] data, long offsetBlockInfo, long offsetIndexInfo) { }

    private HeaderResult headers(Schematic schematic, Map.Entry<Integer, BlockDataEncoder> blockEncoder, Map.Entry<Integer, IndexEncoder> indexEncoder) throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bOut);

        out.write(writeU16(Headers.ID));
        out.write(writeU64(schematic.id().getMostSignificantBits()));
        out.write(writeU64(schematic.id().getLeastSignificantBits()));

        out.write(writeU16(Headers.AXIS_ORDER));
        out.write(schematic.axisOrder().getId());

        out.write(writeU16(Headers.OFFSET));
        out.writeInt((int) schematic.offset().getX());
        out.writeInt((int) schematic.offset().getY());
        out.writeInt((int) schematic.offset().getZ());

        out.write(writeU16(Headers.SIZE));
        out.write(writeU32((int) schematic.size().width()));
        out.write(writeU32((int) schematic.size().height()));
        out.write(writeU32((int) schematic.size().depth()));

        out.write(writeU16(Headers.BLOCK_DATA_ENCODING));
        out.write(writeU16(blockEncoder.getKey()));

        out.write(writeU16(Headers.BLOCK_INFO));
        int blockInfoIndex = out.size();
        out.write(new byte[CompressionBlockInfo.SIZE]);

        out.write(writeU16(Headers.INDEX_ENCODING));
        out.write(writeU16(indexEncoder.getKey()));

        out.write(writeU16(Headers.INDEX_INFO));
        int indexInfoIndex = out.size();
        out.write(new byte[CompressionBlockInfo.SIZE]);

        out.write(writeU16(Headers.TIMESTAMP));
        out.write(writeU64(schematic.created().toEpochMilli()));

        out.write(writeU16(Headers.AUTHOR));
        out.write(writeString(schematic.author()));

        out.write(writeU16(Headers.TITLE));
        out.write(writeString(schematic.title()));

        MinecraftPlatform platform = schematic.platform();

        out.write(writeU16(Headers.PLATFORM));
        out.write(writeU32(platform.version().major()));
        out.write(writeU32(platform.version().update()));
        out.write(writeU32(platform.version().minor()));
        out.write(writeU32(platform.version().protocol()));
        out.write(writeString(platform.platformName()));
        out.write(writeString(platform.platformVersion()));

        // TODO add tlv headers here

        return new HeaderResult(
                bOut.toByteArray(),
                blockInfoIndex,
                indexInfoIndex
        );
    }

    @Override
    public Schematic read(SeekableInputStream in) {
        try {
            DataInputStream din = new DataInputStream(in);

            for (int i = 0; i < FINGERPRINT_SIZE; i++) {
                if (in.read() != FINGERPRINT.charAt(i))
                    throw new EOFException("Invalid fingerprint!");
            }

            {
                int version = readU16(din);
                if (version != VERSION)
                    throw new UnsupportedEncodingException("Version aren't match\nexpected: " + VERSION + "\ngot: " + version);
            }

            int headerSize = readU24(din);
            byte[] header = in.readNBytes(headerSize);
            Map<Integer, Object> headers = parseHeaders(header);
            assertBaseHeaders(headers);

            CompressionBlockInfo blockInfo = (CompressionBlockInfo) headers.get(Headers.BLOCK_INFO);
            in.position(blockInfo.offset());
            byte[] compressedBlocks = in.readNBytes((int) blockInfo.compressedSize());
            byte[] originalBlockData = compressionEngines.get(blockInfo.compressionType()).decompress(compressedBlocks);
            if (originalBlockData.length != blockInfo.uncompressedSize())
                throw new EOFException("Block data length mismatch");

            if (crc32(originalBlockData) != blockInfo.checksum())
                throw new EOFException("Checksum mismatch");

            CompressionBlockInfo indexInfo = (CompressionBlockInfo) headers.get(Headers.INDEX_INFO);
            in.position(indexInfo.offset());
            byte[] compressedIndexInfo = in.readNBytes((int) indexInfo.uncompressedSize());
            byte[] originalIndexInfo = compressionEngines.get(indexInfo.compressionType()).decompress(compressedIndexInfo);
            if (originalIndexInfo.length != indexInfo.uncompressedSize())
                throw new EOFException("Index info length mismatch");

            if (crc32(originalIndexInfo) != indexInfo.checksum())
                throw new EOFException("Checksum mismatch");

            AxisOrder axes = (AxisOrder) headers.get(Headers.AXIS_ORDER);

            SeekableInputStream blockIn = new SeekableInputStream(ByteArraySeekableChannel.of(originalBlockData));
            BlockDataEncoder blockEncoder = (BlockDataEncoder) headers.get(Headers.BLOCK_DATA_ENCODING);

            SeekableInputStream indexIn = new SeekableInputStream(ByteArraySeekableChannel.of(originalIndexInfo));
            IndexEncoder indexEncoder = (IndexEncoder) headers.get(Headers.INDEX_ENCODING);
            BlockIterator blocks = indexEncoder.read(indexIn, axes, blockEncoder, blockIn);

            return schematicFactory.createSchematic(
                    (UUID) headers.get(Headers.ID),
                    (Instant) headers.get(Headers.TIMESTAMP),
                    (MinecraftPlatform) headers.get(Headers.PLATFORM),
                    (String) headers.get(Headers.AUTHOR),
                    (String) headers.get(Headers.TITLE),
                    blocks,
                    (Dimensions) headers.get(Headers.SIZE),
                    (AxisOrder) headers.get(Headers.AXIS_ORDER),
                    (Position) headers.get(Headers.OFFSET)

            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertBaseHeaders(Map<Integer, Object> headers) {
        if (headers.isEmpty())
            throw new IllegalStateException("No headers found!");

        if (!headers.containsKey(Headers.ID))
            throw new IllegalStateException("ID Header not found!");

        if (!headers.containsKey(Headers.AXIS_ORDER))
            throw new IllegalStateException("Axis Order Header not found!");

        if (!headers.containsKey(Headers.OFFSET))
            throw new IllegalStateException("Offset Header not found!");

        if (!headers.containsKey(Headers.SIZE))
            throw new IllegalStateException("Size Header not found!");

        if (!headers.containsKey(Headers.BLOCK_DATA_ENCODING))
            throw new IllegalStateException("BlockDataEncoding Header not found!");

        if (!headers.containsKey(Headers.BLOCK_INFO))
            throw new IllegalStateException("BlockInfo Header not found!");

        if (!headers.containsKey(Headers.INDEX_ENCODING))
            throw new IllegalStateException("IndexEncoding Header not found!");

        if (!headers.containsKey(Headers.INDEX_INFO))
            throw new IllegalStateException("IndexInfo Header not found!");

        if (!headers.containsKey(Headers.TIMESTAMP))
            throw new IllegalStateException("Timestamp Header not found!");

        if (!headers.containsKey(Headers.AUTHOR))
            throw new IllegalStateException("Author Header not found!");

        if (!headers.containsKey(Headers.TITLE))
            throw new IllegalStateException("Title Header not found!");

        if (!headers.containsKey(Headers.PLATFORM))
            throw new IllegalStateException("Platform Header not found!");
    }

    private Map<Integer, Object> parseHeaders(byte[] headers) throws IOException {
        int index = 0;

        Map<Integer, Object> map = new HashMap<>();

        while (index < headers.length) {
            int id = (headers[index] << 8) | (headers[index + 1]);
            index += 2;

            index = switch (id) {
                case Headers.ID -> {
                    map.put(Headers.ID, toUUID(splitFromAToB(headers, index, index + 16)));
                    yield index + 16;
                }
                case Headers.AXIS_ORDER -> {
                    map.put(Headers.AXIS_ORDER, AxisOrder.byId(headers[index]));
                    yield index + 1;
                }

                case Headers.OFFSET -> {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 12)));
                    map.put(Headers.OFFSET, new Position(in.readInt(), in.readInt(), in.readInt())); // TODO may change to 10 bytes instead of 12 because we only need 25 bits for 30m blocks
                    yield index + 12;
                }

                case Headers.SIZE -> {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 12)));
                    map.put(Headers.SIZE, new Dimensions(readU32(in), readU32(in), readU32(in)));
                    yield index + 12;
                }

                case Headers.BLOCK_DATA_ENCODING -> {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 2)));
                    map.put(Headers.BLOCK_DATA_ENCODING, blockDataEncoderMap.get(readU16(in)));
                    yield index + 2;
                }

                case Headers.BLOCK_INFO -> {
                    map.put(Headers.BLOCK_INFO, CompressionBlockInfo.fromBytes(splitFromAToB(headers, index, index + CompressionBlockInfo.SIZE)));
                    yield index + CompressionBlockInfo.SIZE;
                }

                case Headers.INDEX_ENCODING -> {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 2)));
                    map.put(Headers.INDEX_ENCODING, indexEncoderMap.get(readU16(in)));
                    yield index + 2;
                }

                case Headers.INDEX_INFO -> {
                    map.put(Headers.INDEX_INFO, CompressionBlockInfo.fromBytes(splitFromAToB(headers, index, index + CompressionBlockInfo.SIZE)));
                    yield index + CompressionBlockInfo.SIZE;
                }

                case Headers.TIMESTAMP -> {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 8)));
                    map.put(Headers.TIMESTAMP, Instant.ofEpochMilli(readU64(in)));
                    yield index + 8;
                }

                case Headers.AUTHOR -> {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 4)));
                    int length = (int) readU32(in);

                    in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 4 + length)));
                    map.put(Headers.AUTHOR, readString(in));
                    yield index + 4 + length;
                }

                case  Headers.TITLE -> {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 4)));
                    int length = (int) readU32(in);

                    in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 4 + length)));
                    map.put(Headers.TITLE, readString(in));
                    yield index + 4 + length;
                }

                case Headers.PLATFORM -> {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(headers, index, index + 20));

                    MinecraftVersion version = new MinecraftVersion(
                            (int) readU32(in),
                            (int) readU32(in),
                            (int) readU32(in),
                            (int) readU32(in)
                    );

                    int lengthName = (int) readU32(in);
                    in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index + 16, index + 20 + lengthName)));
                    String platformName = readString(in);

                    in = new DataInputStream(new ByteArrayInputStream(headers, index + 20 + lengthName, index + 24 + lengthName));
                    int lengthPlatform = (int) readU32(in);
                    in = new DataInputStream(new ByteArrayInputStream(headers, index + 20 + lengthName, index + 24 + lengthName + lengthPlatform));
                    String platformVersion = readString(in);

                    map.put(Headers.PLATFORM, new MinecraftPlatform(version, platformName, platformVersion));
                    yield index + 24 + lengthName + lengthPlatform;

                }

                default -> {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 3)));
                    int length = readU24(in);
                    map.put(id, splitFromAToB(headers, index + 3, index + 3 + length)); // TODO add way to parse those bytes
                    yield index + 3 + length;

                }
            };
        }

        return map;
    }

    private byte[] splitFromAToB(byte[] data, int a, int b) {
        if (data.length <= a)
            throw new IndexOutOfBoundsException("Couldn't start from the end!");

        if (data.length < b)
            throw new IndexOutOfBoundsException("Couldn't reach after the end!");

        byte[] result = new byte[b - a];
        System.arraycopy(data, a, result, 0, b - a);
        return result;
    }

}
