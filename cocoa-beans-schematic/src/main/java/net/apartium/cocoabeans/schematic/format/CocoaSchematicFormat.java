package net.apartium.cocoabeans.schematic.format;

import net.apartium.cocoabeans.Mathf;
import net.apartium.cocoabeans.schematic.block.*;
import net.apartium.cocoabeans.schematic.iterator.BlockIterator;
import net.apartium.cocoabeans.space.AreaSize;
import net.apartium.cocoabeans.space.Position;
import net.apartium.cocoabeans.schematic.*;
import net.apartium.cocoabeans.space.axis.AxisOrder;
import net.apartium.cocoabeans.schematic.compression.CompressionEngine;
import net.apartium.cocoabeans.seekable.ByteArraySeekableChannel;
import net.apartium.cocoabeans.seekable.SeekableInputStream;
import net.apartium.cocoabeans.seekable.SeekableOutputStream;
import net.apartium.cocoabeans.structs.MinecraftPlatform;
import net.apartium.cocoabeans.structs.MinecraftVersion;
import org.jetbrains.annotations.ApiStatus;

import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.apartium.cocoabeans.utils.BufferUtils.*;

@ApiStatus.AvailableSince("0.0.46")
public class CocoaSchematicFormat<T extends Schematic<T>> implements SchematicFormat<T> {

    public static final String FINGERPRINT = "CBSC";
    public static final int FINGERPRINT_SIZE = 4;
    public static final int VERSION = 1;
    public static final long HEADER_START_INDEX = 9;

    public static class Headers {

        private Headers() {}

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
        public static final int BODY_EXTENSION = 0x0E;

    }

    private final Map<Integer, BlockDataEncoder> blockDataEncoderMap;
    private final Map<Integer, IndexEncoder> indexEncoderMap;
    private final Map<Byte, CompressionEngine> compressionEngines;
    private final Map<Long, BodyExtensionFormat<?>> bodyExtensionFormat;

    private CompressionEngine defaultCompressionEngineForBlocks;
    private CompressionEngine defaultCompressionEngineForIndexes;
    private CompressionEngine defaultCompressionEngineForBodyExtensions;
    private Map.Entry<Integer, BlockDataEncoder> preferBlockEncoder;
    private Map.Entry<Integer, IndexEncoder> preferIndexEncoder;
    private final SchematicFactory<T> schematicFactory;

    public CocoaSchematicFormat(Map<Integer, BlockDataEncoder> blockDataEncoderMap, Map<Integer, IndexEncoder> indexEncoderMap, Set<CompressionEngine> compressionEngines, byte defaultCompressionForBlock, byte defaultCompressionForIndexes, SchematicFactory<T> schematicFactory) {
        this.blockDataEncoderMap = new HashMap<>(blockDataEncoderMap);
        if (!blockDataEncoderMap.isEmpty())
            preferBlockEncoder = blockDataEncoderMap.entrySet().iterator().next();

        this.indexEncoderMap = new HashMap<>(indexEncoderMap);
        if (!indexEncoderMap.isEmpty())
            preferIndexEncoder = indexEncoderMap.entrySet().iterator().next();

        this.compressionEngines = compressionEngines.stream().collect(Collectors.toMap(CompressionEngine::type, Function.identity()));

        this.defaultCompressionEngineForBlocks = this.compressionEngines.get(defaultCompressionForBlock);
        this.defaultCompressionEngineForIndexes = this.compressionEngines.get(defaultCompressionForIndexes);
        this.defaultCompressionEngineForBodyExtensions = this.compressionEngines.get(defaultCompressionForBlock);

        this.schematicFactory = schematicFactory;
        this.bodyExtensionFormat = new HashMap<>();
    }

    public void registerBodyExtensionFormat(long id, BodyExtensionFormat<?> format) {
        bodyExtensionFormat.put(id, format);
    }

    public void registerAllBodyExtensionsFormat(Map<Long, BodyExtensionFormat<?>> bodyExtensionFormat) {
        this.bodyExtensionFormat.putAll(bodyExtensionFormat);
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

    public void setDefaultCompressionEngineForBodyExtensions(CompressionEngine engine) {
        defaultCompressionEngineForBodyExtensions = engine;
    }

    private <E> void writeBodyExtension(SeekableOutputStream out, BodyExtension<E> bodyExtension, HeaderResult headerResult) throws IOException {
        Long offset = headerResult.bodyExtensionsOffset().get(bodyExtension.id());
        if (offset == null)
            throw new IllegalStateException("Body extension not found in header!\nSomething went wrong with schematic!");

        if (bodyExtensionFormat.containsKey(bodyExtension.id()))
            throw new IllegalStateException("Unknown body extension format: " + bodyExtension.id());

        BodyExtensionFormat<E> format = (BodyExtensionFormat<E>) bodyExtensionFormat.get(bodyExtension.id());

        byte[] data = format.write(bodyExtension);
        byte[] compressed = compressionEngines.get(defaultCompressionEngineForBodyExtensions.type()).compress(data);

        CompressionBlockInfo bodyExtensionInfo = new CompressionBlockInfo(
                defaultCompressionEngineForBodyExtensions.type(),
                data.length,
                compressed.length,
                out.position(),
                crc32(data)
        );

        out.position(HEADER_START_INDEX + offset);
        out.write(bodyExtensionInfo.toByteArray());

        out.position(bodyExtensionInfo.offset());
        out.write(compressed);
    }

    @Override
    public void write(Schematic<T> schematic, SeekableOutputStream out) {
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

            AreaSize dimensions = schematic.size();

            long maxDimensions = (long) Math.max(dimensions.width(), Math.max(dimensions.height(), dimensions.depth()));
            long scaler = Mathf.nextPowerOfFour(maxDimensions);
            MutableBlockChunk blockChunk = new MutableBlockChunkImpl(axes, scaler, Position.ZERO, Position.ZERO);

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

            for (BodyExtension<?> bodyExtension : schematic.bodyExtensions())
                writeBodyExtension(out, bodyExtension, headerResult);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private record HeaderResult(byte[] data, long offsetBlockInfo, long offsetIndexInfo, Map<Long, Long> bodyExtensionsOffset) { }

    private void writeAuthorHeader(SchematicMetadata metadata, OutputStream out) throws IOException {
        String author = metadata.author();
        if (author == null)
            return;

        out.write(writeU16(Headers.AUTHOR));
        out.write(writeString(author));
    }

    private void writeTitleHeader(SchematicMetadata metadata, OutputStream out) throws IOException {
        String title = metadata.title();
        if (title == null)
            return;

        out.write(writeU16(Headers.TITLE));
        out.write(writeString(title));
    }

    private HeaderResult headers(Schematic<T> schematic, Map.Entry<Integer, BlockDataEncoder> blockEncoder, Map.Entry<Integer, IndexEncoder> indexEncoder) throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bOut);

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

        SchematicMetadata metadata = schematic.metadata();

        writeAuthorHeader(metadata, out);
        writeTitleHeader(metadata, out);

        MinecraftPlatform platform = schematic.originPlatform();

        out.write(writeU16(Headers.PLATFORM));
        out.write(writeU32(platform.version().major()));
        out.write(writeU32(platform.version().update()));
        out.write(writeU32(platform.version().minor()));
        out.write(writeU32(platform.version().protocol()));
        out.write(writeString(platform.platformName()));
        out.write(writeString(platform.platformVersion()));

        Map<Long, Long> bodyExtensionsOffset = new HashMap<>();
        for (BodyExtension<?> bodyExtension : schematic.bodyExtensions()) {
            out.write(writeU16(Headers.BODY_EXTENSION));
            out.write(writeU64(bodyExtension.id()));

            long offset = out.size();
            out.write(new byte[CompressionBlockInfo.SIZE]);

            bodyExtensionsOffset.put(bodyExtension.id(), offset);
        }

        return new HeaderResult(
                bOut.toByteArray(),
                blockInfoIndex,
                indexInfoIndex,
                bodyExtensionsOffset
        );
    }

    private void ensuresFingerprint(SeekableInputStream in) throws IOException {
        for (int i = 0; i < FINGERPRINT_SIZE; i++) {
            if (in.read() != FINGERPRINT.charAt(i))
                throw new EOFException("Invalid fingerprint!");
        }
    }

    private void ensuresVersion(DataInputStream in) throws IOException {
        int version = readU16(in);
        if (version != VERSION)
            throw new UnsupportedEncodingException("Version aren't match\nexpected: " + VERSION + "\ngot: " + version);
    }

    private void readBodyExtensions(SeekableInputStream in, Map<Long, BodyExtension<?>> bodyExtensions, Map<Long, CompressionBlockInfo> bodyExtensionCompressionInfo) throws IOException {
        for (Map.Entry<Long, CompressionBlockInfo> infoEntry : bodyExtensionCompressionInfo.entrySet()) {
            BodyExtension<?> bodyExtension = readBodyExtension(in, infoEntry.getKey(), infoEntry.getValue());
            if (bodyExtension == null)
                continue;

            bodyExtensions.put(infoEntry.getKey(), bodyExtension);
        }
    }

    private BodyExtension<?> readBodyExtension(SeekableInputStream in, long bodyExtensionId, CompressionBlockInfo bodyExtensionInfo) throws IOException {
        BodyExtensionFormat<?> format = bodyExtensionFormat.get(bodyExtensionId);
        if (format == null)
            throw new IllegalArgumentException("Unknown body extension format: " + bodyExtensionId);

        in.position(bodyExtensionInfo.offset());

        byte[] compressedBodyExtensionInfo = in.readNBytes((int) bodyExtensionInfo.compressedSize());
        byte[] originalBodyExtensionInfo = compressionEngines.get(bodyExtensionInfo.compressionType()).decompress(compressedBodyExtensionInfo);
        if (originalBodyExtensionInfo.length != bodyExtensionInfo.uncompressedSize())
            throw new EOFException("Body Extensions info length mismatch\nExpected: " + bodyExtensionInfo.uncompressedSize() + "\nActual:" + originalBodyExtensionInfo.length);

        long hash = crc32(originalBodyExtensionInfo);
        if (hash != bodyExtensionInfo.checksum())
            throw new EOFException("Checksum mismatch\nExpected: " + bodyExtensionInfo.checksum() + "\nActual:" + hash);

        return format.read(new ByteArrayInputStream(originalBodyExtensionInfo), originalBodyExtensionInfo.length);
    }

    @Override
    public Schematic<T> read(SeekableInputStream in) {
        try {
            DataInputStream din = new DataInputStream(in);

            ensuresFingerprint(in);
            ensuresVersion(din);

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
            byte[] compressedIndexInfo = in.readNBytes((int) indexInfo.compressedSize());
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

            Map<Long, BodyExtension<?>> bodyExtensions = new HashMap<>();
            if (headers.getOrDefault(Headers.BODY_EXTENSION, null) instanceof Map<?, ?> map) {
                Map<Long, CompressionBlockInfo> bodyExtensionCompressionInfo = (Map<Long, CompressionBlockInfo>) map;
                readBodyExtensions(in, bodyExtensions, bodyExtensionCompressionInfo);
            }

            Map<String, Object> metadata = new HashMap<>();
            if (headers.containsKey(Headers.AUTHOR))
                metadata.put("author", headers.get(Headers.AUTHOR));

            if (headers.containsKey(Headers.TITLE))
                metadata.put("title", headers.get(Headers.TITLE));

            return schematicFactory.createSchematic(
                    (Instant) headers.get(Headers.TIMESTAMP),
                    (MinecraftPlatform) headers.get(Headers.PLATFORM),
                    SchematicMetadata.of(metadata),
                    blocks,
                    (AreaSize) headers.get(Headers.SIZE),
                    (AxisOrder) headers.get(Headers.AXIS_ORDER),
                    (Position) headers.get(Headers.OFFSET),
                    bodyExtensions
            );
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void assertBaseHeaders(Map<Integer, Object> headers) {
        if (headers.isEmpty())
            throw new IllegalStateException("No headers found!");

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

        if (!headers.containsKey(Headers.PLATFORM))
            throw new IllegalStateException("Platform Header not found!");
    }

    private Map<Integer, Object> parseHeaders(byte[] headers) throws IOException {
        int index = 0;

        Map<Integer, Object> map = new HashMap<>();

        while (index < headers.length) {
            int id = (headers[index] << 8) | (Byte.toUnsignedInt(headers[index + 1]));
            index += 2;

            index = switch (id) {
                case Headers.AXIS_ORDER -> {
                    map.put(Headers.AXIS_ORDER, AxisOrder.byId(headers[index]));
                    yield index + 1;
                }

                case Headers.OFFSET -> {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 12)));
                    map.put(Headers.OFFSET, new Position(in.readInt(), in.readInt(), in.readInt()));
                    yield index + 12;
                }

                case Headers.SIZE -> {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 12)));
                    map.put(Headers.SIZE, new AreaSize(readU32(in), readU32(in), readU32(in)));
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

                case Headers.BODY_EXTENSION -> {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 8)));
                    long bodyExtensionId = readU64(in);

                    CompressionBlockInfo info = CompressionBlockInfo.fromBytes(splitFromAToB(headers, index + 8, index + 8 + CompressionBlockInfo.SIZE));

                    if (map.computeIfAbsent(Headers.BODY_EXTENSION, key -> new HashMap<>()) instanceof Map<?, ?> bodyExtensionMap)
                        ((Map<Long, CompressionBlockInfo>) bodyExtensionMap).put(bodyExtensionId, info);

                    yield index + 8 + CompressionBlockInfo.SIZE;
                }

                default -> {
                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(splitFromAToB(headers, index, index + 3)));
                    int length = readU24(in);
                    map.put(id, splitFromAToB(headers, index + 3, index + 3 + length));
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
